package src.AI;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.InputStreamInputSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.nativeblas.NativeOps;
import org.nd4j.nativeblas.NativeOpsHolder;
import org.nd4j.nativeblas.Nd4jBlas;
import src.Assets.OBJCollection;
import src.Assets.OBJTexture;
import src.Assets.TextureImg;
import src.Assets.instance.GridItemInstance;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.PhysicsContext;
import src.grid.GridItem;
import src.tools.PosHitBox3f;
import src.tools.log.Logger;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Creates a neural network array for driving.
 * Start generating actions by invoking {@link #start()},
 * and stop by invoking {@link #stop()}.
 */
public class AINN {
    // <editor-fold defaultstate="collapsed" desc="Global Variables">
    private static int idCounter = 0;
    private int id = idCounter++;
    private PStructAction curAction;
    private Thread updateThread = null;
    private Lock lock = new ReentrantLock();
    private boolean stopUpdateThread = false;
    final private long MILLIS_PER_UPDATE = 100L;

    private MultiLayerNetwork[] networks;
    private MyLossFunction myLossFunction;
    private MyIterator myIter;

    final protected GridItemInstance instance;
    final protected static int GRIDSIZE = 5;
    final private static String CSV;
    final private static List<Point2D.Float> COORDINATES;
    final protected static List<String> labelNames;
    final protected static int POSSIBLECLASSES = 3;
    final protected static int BATCHSIZE = 5;
    final protected static int LABELINDEX = 7;

    // private List<Quartet<DataSet, Integer, Double, DataSet>> qMemory = new LinkedList<>();   // Memory for Q-learning

    // Static block for reading CSV once.
    static {
        COORDINATES = new ArrayList<>();
        String fileName = "nodes.csv";
        String filePath = GS.RESOURCE_DIR + "A_star_data";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader r = new BufferedReader(new FileReader(filePath + GS.FS + fileName))) {
            String line;

            // while there are more lines, read them and populate CSV string and COORDINATES list
            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append(GS.LS);
                String[] tokens = line.split(";");
                COORDINATES.add(new Point2D.Float(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1])));
            }

        } catch (IOException e) {
            Logger.write(new Object[]{"Static file reading failure", e}, Logger.Type.ERROR);
        }

        CSV = sb.toString();

        // Logger.write(new Object[]{"Reading CSV done.", CSV, COORDINATES}, Logger.Type.DEBUG);

        // Setting labelNames
        labelNames = new LinkedList<>();
        labelNames.add("Left / Backward");
        labelNames.add("Nothing");
        labelNames.add("Right / Forward");
    }


    /**
     * Listener for updating the values after an iteration of the AINN.
     *
     * Note that {@link BaseTrainingListener} has methods {@code onEpochStart()} and {@code onEpochEnd()}.
     * The first one can be used to get the 'previous output', which might be useful for Q-learning.
     */
    final private BaseTrainingListener LISTENER = new BaseTrainingListener() {

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
            // Cast model to MultiLayerNetwork in order to get output.
            MultiLayerNetwork network = (MultiLayerNetwork) model;

            INDArray out = null;                        // Network output as INDArray
            float[] output = new float[]{0f, 1f, 0f};   // Actual action that is done.
            double score = 0.0d;                        // Reward for current state

            // Try to get output from network
            try {
                out = network.output(myIter.next().getFeatures());
            } catch (Exception e) {
                Logger.write(new Object[]{"Cannot get OUTPUT for model ", network.toString().replaceAll("\n", GS.LS), e,}, Logger.Type.ERROR);
                e.printStackTrace();
            }

            // Logger.write(new Object[]{"out from network = " + out.toString().replaceAll("\n", GS.LS)}, Logger.Type.DEBUG);

            // Try to convert INDArray output to integer output
            try {
                output = out.toFloatVector();
            } catch (Exception e) {
                Logger.write(new Object[]{"Cannot convert OUTPUT to INT for model" + model.toString(), e}, Logger.Type.ERROR);
                e.printStackTrace();
            }


            // Logger.write(new Object[]{"output from out = " + out.toFloatVector()}, Logger.Type.DEBUG);

            // Try to get score from network
            try {
                // TODO use score in Q-learning
                score = network.score();
            } catch (Exception e) {
                Logger.write(new Object[]{"Cannot get network SCORE for model " + model.toString(), e}, Logger.Type.ERROR);
                e.printStackTrace();
            }

            float outValue = 0f;
            float max = out.maxNumber().floatValue();

            if (output[0] == max) {
                outValue = -max;
            } else if (output[2] == max) {
                outValue = max;
            }

            if (model == networks[0]) {
                curAction.turn = outValue;
            }

            if (model == networks[1]) {
                curAction.accel = outValue;
            }
        }
    };
    // </editor-fold>

    /**
     * Constructor for AINN.
     *
     * Does the following:
     * 1. Set the correct {@code GridItemInstance}.
     * 2. Limit threads to be used to one ({@link #limitThreadsToOne()}).
     * 3. Configure both neural networks to be used.
     * 4. Add listeners to both networks.
     * 5. Start the AI thread.
     *
     * @param instance GridItemInstance to be set.
     */
    public AINN(@NotNull GridItemInstance instance) {
        this.instance = instance;
        limitThreadsToOne();

        // normalizer = new NormalizerStandardize();
        networks = new MultiLayerNetwork[2];
        curAction = new PStructAction(0, 0, 0, 1);

        // <editor-fold defaultstate="collapsed" desc="Configuration for network responsible for turning">
        int in = 60;
        int out = 3;

        int turnSeed = 1;
        double turnl1 = 0.001;
        double turnl2 = 0.001;
        int turnHidden = 25;

        MultiLayerConfiguration turnConfig = new NeuralNetConfiguration.Builder()
                .seed(turnSeed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .l1(turnl1)
                .l2(turnl2)
                .inferenceWorkspaceMode(WorkspaceMode.NONE)
                .trainingWorkspaceMode(WorkspaceMode.NONE)
                .list()
                .layer(
                        0, new DenseLayer.Builder().nIn(in).nOut(turnHidden).build()
                )
                .layer(
                        1, new DenseLayer.Builder().nIn(turnHidden).nOut(turnHidden).build()
                )
                .layer(
                        2, new OutputLayer.Builder().nIn(turnHidden).nOut(out).activation(Activation.SOFTMAX)
                                .lossFunction(myLossFunction)
                                .build()
                )
                .pretrain(false)
                .backprop(true)
                .build();
        /// </editor-fold>


        // <editor-fold defaultstate="collapsed" desc="Configuration for network responsible for acceleration">
        int driveSeed = 1;
        double drivel1 = 0.001;
        double drivel2 = 0.001;
        int driveHidden = 25;
        MultiLayerConfiguration driveConfig = new NeuralNetConfiguration.Builder()
                .seed(driveSeed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .l1(drivel1)
                .l2(drivel2)
                .inferenceWorkspaceMode(WorkspaceMode.NONE)
                .trainingWorkspaceMode(WorkspaceMode.NONE)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(in).nOut(driveHidden).build())
                .layer(1, new DenseLayer.Builder().nIn(driveHidden).nOut(driveHidden).build())
                .layer(2, new OutputLayer.Builder().nIn(driveHidden).nOut(out).activation(Activation.SOFTMAX).build())
                .pretrain(false)
                .backprop(true)
                .build();
        /// </editor-fold>

        // Initialise networks array
        networks[0] = new MultiLayerNetwork(turnConfig);
        networks[1] = new MultiLayerNetwork(driveConfig);

        // Add listeners
        networks[0].addListeners(LISTENER);
        networks[1].addListeners(LISTENER);

        // Start the thread
        SwingUtilities.invokeLater(this::start);
    }

    /**
     * Creates iterator to iterate over CSV data, read from inputstream.
     * {@code skipLines} lines will be skipped when initialising iterator.
     *
     * @param skipLines amount of lines to skip.
     * @throws RuntimeException if {@code skipLines < 0}
     */
    private void readAndCreateIter(int skipLines) {
        if (skipLines < 0) throw new RuntimeException("Cannot create CSV reader skipping negative lines!");

        RecordReader aStarReader = new CSVRecordReader(skipLines, GS.DELIM);

        try (InputStream is = new ByteArrayInputStream(CSV.getBytes(Charset.defaultCharset()))) {
            aStarReader.initialize(new InputStreamInputSplit(is));
        } catch (Exception e) {
            Logger.write(new Object[]{"InputStream reading failure at AINN with id " + id, e}, Logger.Type.ERROR);
        }

        GridItem[][][][] G = GS.grid.getItemsAround(instance, 2, 2, 2);

        try {
            myIter = new MyIterator(aStarReader, BATCHSIZE, LABELINDEX, POSSIBLECLASSES, G);
        } catch (Exception e) {
            Logger.write(new Object[]{"Iterator creation failure at AINN with id " + id, e}, Logger.Type.ERROR);
        }
    }

    /**
     * Finds the index of the closest point in coordinates.
     * This is needed in order to select the correct line in the iterator.
     *
     * @return index of closes point in coordinates.
     */
    private int findIndexOfClosestPoint(Point2D.Float me) {
        float minDist = Float.MAX_VALUE;
        Point2D.Float c = new Point2D.Float(me.x, me.y);

        for (Point2D.Float p : COORDINATES) {
            float d = dist(me, p);

            if (d < minDist) {
                minDist = d;
                c = p;
            }
        }

        // Logger.write(new Object[]{minDist, c, COORDINATES.indexOf(c)}, Logger.Type.DEBUG);

        return COORDINATES.indexOf(c);
    }

    /**
     * Computes and returns the distance of the current position to another point.
     * Used in {@link #findIndexOfClosestPoint(Point2D.Float)}.
     *
     * @param other some point.
     * @return euclidean distance of current position to {@code other}.
     */
    private float dist(@NotNull Point2D.Float me, @NotNull Point2D.Float other) {
        return (float) Math.sqrt((other.x - me.x) * (other.x - me.x) + (other.y - me.y) * (other.y - me.y));
    }

    /**
     * Returns the point at which you are right now.
     */
    private Point2D.Float findPoint() {
        Vector3f pos = instance.getCurPosition();
        return new Point2D.Float(pos.x, -pos.z);
    }

    /**
     * Runs one iteration of the AINN;
     * 1. Get next DataSet using {@link MyIterator}.
     * 2. Normalise said DataSet using {@link org.nd4j.linalg.dataset.api.preprocessor.DataNormalization}.
     * 3. Train using {@code fit()} function.
     */
    protected void execute() {
        // Create iterator to read CSV, starting at correct index.
        readAndCreateIter(findIndexOfClosestPoint(findPoint()));

        // get next()
        DataSet nextSet = myIter.next();

        // This data is correct.
        /* Logger.write(new Object[]{
                "Initial data non-normalised as in execute()" + id + ":",
                nextSet.getFeatures().length() + "\n\t" + nextSet.getFeatures().toString().replaceAll("\n", GS.LS),
                nextSet.getLabels().length() + "\n\t" + nextSet.getLabels().toString().replaceAll("\n", GS.LS),
        }, Logger.Type.DEBUG); */


        // TODO Decide if we want this normalisation
        // normalizer.fit(nextSet);
        // normalizer.transform(nextSet);

/*
        Logger.write(new Object[]{
                "AFTER NORMALISATION" + id + ":",
                nextSet.getFeatures().length() + "\n\t" + nextSet.getFeatures().toString().replaceAll("\n", GS.LS + "\t"),
                nextSet.getLabels().length() + "\n\t" + nextSet.getLabels().toString().replaceAll("\n", GS.LS + "\t"),
                nextSet.getLabelNamesList()
        }, Logger.Type.DEBUG);
*/

        // TODO Q-Learning comment: Here we have a current state: nextSet.

        networks[0].init();
        networks[1].init();

        // Train turning net
        try {

            /*
            Logger.write(new Object[]{
                    "Data with which we fit: ",
                    "getFeatures()" + nextSet.getFeatures().toString().replaceAll("\n", GS.LS),
                    "getLabels()" + nextSet.getLabels().toString().replaceAll("\n", GS.LS),
            }, Logger.Type.DEBUG);
            */

            networks[0].fit(nextSet);
        } catch (Exception e) {
            Logger.write(new Object[]{"Fitting network[0] for turning failed " + id, e}, Logger.Type.ERROR);
            e.printStackTrace();
        }

        // Train driving net
        try {
            networks[1].fit(nextSet);
        } catch (Exception e) {
            Logger.write(new Object[]{"Fitting network[1] for driving failed " + id, e}, Logger.Type.ERROR);
            e.printStackTrace();
        }

        /*
        // Visualisation. Disabled for now.
        for (MultiLayerNetwork net : networks) {
            if (net == networks[0]) {
                visualize(net);
            }
        }
        */
    }

    /**
     * Creates an UIServer, initialises a StatsStorage and attaches it to the UIServer.
     * Then, adds a StatsListener to {@code network} in order for visualisation.
     *
     * @param network A not-null network that needs to be visualised.
     */
    private static void visualize(@NotNull MultiLayerNetwork network) {
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        network.setListeners(new StatsListener(statsStorage));
    }

    /**
     * Starts the update thread.
     * First resets the {@code stopUpdateThread} flag.
     * Then start a new update thread, unless there was already one running.
     * Note that it is possible to call {@link #stop()} and then
     * {@link #start()} without stopping the old and starting a new
     * update thread.
     */
    public void start() {
        lock.lock();

        try {
            stopUpdateThread = false;

        } finally {
            lock.unlock();
        }

        if (updateThread != null) return;

        updateThread = new Thread("AINN-thread-" + id) {
            private long prevTime = System.currentTimeMillis();

            @Override
            public void run() {
                // The update thread should have slightly below priority,
                // but not minimal.
                Thread.currentThread().setPriority(3);

                while (true) {
                    // Execute the net.
                    try {
                        execute();

                    } catch (Exception e) {
                        Logger.write(new Object[]{
                                "Exception occured in AINN " + id + ":",
                                e
                        }, Logger.Type.ERROR);

                        // Make error clickable.
                        e.printStackTrace();
                    }

                    // Wait to fill the time.
                    long curTime = System.currentTimeMillis();
                    long wait = MILLIS_PER_UPDATE - (curTime - prevTime);
                    prevTime = curTime;

                    Logger.write(new Object[]{
                            "AINN " + id + " did not finish within the "
                                    + "time limit.",
                            "(it took " + (MILLIS_PER_UPDATE - wait) + "ms)"
                    }, Logger.Type.WARNING);

                    if (wait > 0) {
                        try {
                            Thread.sleep(wait);
                        } catch (InterruptedException e) {

                        }
                    } else {
                        Logger.write(new Object[]{
                                "AINN " + id + " did not finish within the "
                                        + "time limit.",
                                "(it took " + (MILLIS_PER_UPDATE - wait) + "ms)"
                        }, Logger.Type.WARNING);
                    }

                    // Check exit condition.
                    lock.lock();
                    try {
                        // If needed, stop the update thread.
                        if (stopUpdateThread) {
                            updateThread = null;
                            return;
                        }

                    } finally {
                        lock.unlock();
                    }

                    if (1 == 0) break; // To stop the compiler from complaining.
                }

                Logger.write("Unexpected exit of AINN " + id
                        + " update thread", Logger.Type.ERROR);
                updateThread = null;
            }
        };
        updateThread.start();
    }

    /**
     * Stops the update thread. Note that the thread is not immediately
     * stopped, but might take some time to terminate.
     */
    public void stop() {
        if (updateThread == null) return;
        lock.lock();
        try {
            stopUpdateThread = true;

        } finally {
            lock.unlock();
        }
    }

    /**
     * @return {@code true} if the update is, or is scheduled to be stopped.
     * {@code false} otherwise.
     *
     * To check whether the update thread is actually running,
     * use {@link #isUpdateRunning()}
     */
    public boolean isStopped() {
        return stopUpdateThread;
    }

    /**
     * TODO fix documentation @Kaj. Also, can this not be removed?
     *
     * @return {@code true} if the update
     */
    public boolean isUpdateRunning() {
        return updateThread == null;
    }

    /**
     * Creates an action representing the choice of the neural net.
     *
     * @param dt the time difference needed for the event.
     * @return a fresh action that was generated by the neural net.
     */
    public PStructAction createAction(long dt) {
        return new PStructAction(curAction.turn, curAction.accel, curAction.verticalVelocity, dt);
    }

    /**
     * Gets computing object-threads, and limit them to 1.
     */
    private static void limitThreadsToOne() {
        Nd4jBlas nd4jBlas = (Nd4jBlas) Nd4j.factory().blas();
        NativeOpsHolder instance = NativeOpsHolder.getInstance();
        NativeOps deviceNativeOps = instance.getDeviceNativeOps();
        deviceNativeOps.setOmpNumThreads(1);
        nd4jBlas.setMaxThreads(1);
    }

    // TODO: Delete this class, most likely obsolete.
    @Deprecated
    public static class InstanceImpl extends GridItemInstance {

        public InstanceImpl() {
            super(new PosHitBox3f(), 1, 0, 0, 0, new OBJTexture(new OBJCollection(), new TextureImg(0f, 0f)), 0, new PhysicsContext());
        }

        @Override
        public boolean isStatic() {
            return true;
        }
    }
}