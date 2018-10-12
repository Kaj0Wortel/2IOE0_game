package src.AI;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.InputStreamInputSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
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
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
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

import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
    private int id = idCounter++;                                       // ID for keeping track of AINN instance

    final private GridItemInstance instance;                            // Grid from game state

    private MultiLayerNetwork[] networks = new MultiLayerNetwork[2];    // Array holding networks

    private RecordReader aStarReader;                                   // Reader for A star data
    private DataSetIterator it;                                         // Iterator to iterate through data

    private static DataNormalization normalizer = new NormalizerStandardize();

    private int turnSeed;                                               // Seed of turning net
    private int turnl1;                                                 // l1 of turning net
    private int turnl2;                                                 // l2 of turning net
    private int turnHidden;                                             // Amount of hidden nodes of turning net

    private int driveSeed;                                              // Seed of driving net
    private int drivel1;                                                // l1 of driving net
    private int drivel2;                                                // l2 of driving net
    private int driveHidden;                                            // Amount of hidden nodes of driving net

    private int in;                                                     // Number of input nodes
    private int out;                                                    // Number of output nodes
    final private static int GRIDSIZE = 5;

    private PStructAction curAction = new PStructAction(0, 0, 0, 1);    // The current action.
    // List < Quartet < currentState, Action, Reward, nextState > >
//    private List<Quartet<DataSet, Integer, Double, DataSet>> qMemory = new LinkedList<>();   // Memory for Q-learning

    // Thread variables
    private Thread updateThread = null;
    private Lock lock = new ReentrantLock();
    private boolean stopUpdateThread = false;
    final private long MILLIS_PER_UPDATE = 100L;

    /* Static block for reading CSV once. */
    final private static String CSV;
    final private static List<Point2D.Float> COORDINATES;

    static {
        COORDINATES = new ArrayList<>();
        String fileName = "nodes.csv";
        String filePath = GS.RESOURCE_DIR + "A_star_data";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader r = new BufferedReader(new FileReader(filePath + GS.FS + fileName))) {
            String line;

            while ((line = r.readLine()) != null) {
                // CSV file
                sb.append(line);
                sb.append(GS.LS);

                // coordinates file
                String[] tokens = line.split(";");
                COORDINATES.add(new Point2D.Float(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1])));
            }

        } catch (IOException e) {
            Logger.write(new Object[]{"Static reading of file", e}, Logger.Type.ERROR);
        }

        CSV = sb.toString();

        Logger.write(
                new Object[]{"Reading CSV done.", CSV},
                Logger.Type.DEBUG
        );
    }


    /**
     * Listener for updating the values after an iteration of the AINN.
     *
     * Note that {@link BaseTrainingListener} has methods {@code onEpochStart()} and {@code onEpochEnd()}.
     * The first one can be used to get the 'previous output', which might be useful for Q-learning.
     */
    final private BaseTrainingListener LISTENER = new BaseTrainingListener() {
        // TODO : Fix listener logic.
        @Override
        public void onEpochStart(Model model) {
        }

        @Override
        public void onEpochEnd(Model model) {
            // Cast model to MultiLayerNetwork in order to get output.
            MultiLayerNetwork network = (MultiLayerNetwork) model;

            INDArray out = null;            // Network output as INDArray
            int output = 0;                 // Actual action that is done.
            double score = 0.0d;            // Reward for current state

            // Try to get output from network
            try {
                out = network.output(it);
            } catch (Exception e) {
                Logger.write(
                        new Object[]{"Cannot get network OUTPUT for model " + model.toString(), e},
                        Logger.Type.ERROR
                );
            }

            // Try to convert INDArray output to integer output
            try {
                output = out.maxNumber().intValue();
            } catch (Exception e) {
                Logger.write(
                        new Object[]{"Cannot convert OUTPUT to INT for model" + model.toString(), e},
                        Logger.Type.ERROR
                );
            }

            // Try to get score from network
            try {
                score = network.score();
            } catch (Exception e) {
                Logger.write(
                        new Object[]{"Cannot get network SCORE for model " + model.toString(), e},
                        Logger.Type.ERROR
                );
            }

            if (model == networks[0]) {
                curAction.turn = output - 1;
            }

            if (model == networks[1]) {
                curAction.accel = output - 1;
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
     *
     * @param instance GridItemInstance to be set.
     */
    public AINN(@NotNull GridItemInstance instance) {
        this.instance = instance;

        limitThreadsToOne();

        // <editor-fold defaultstate="collapsed" desc="Configuration for network responsible for turning">
        MultiLayerConfiguration turnConfig = new NeuralNetConfiguration.Builder()
                .seed(turnSeed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .l1(turnl1)
                .l2(turnl2)
                .list()
                .layer(
                        0, new DenseLayer.Builder()
                                .nIn(in)
                                .nOut(turnHidden)
                                .build()
                )
                .layer(
                        1, new DenseLayer.Builder()
                                .nIn(turnHidden)
                                .nOut(turnHidden)
                                .build()
                )
                .layer(
                        2, new OutputLayer.Builder()
                                .nIn(turnHidden)
                                .nOut(out)
                                // .lossFunction(new LossMCXENT(Nd4j.create(new double[]{0.5, 0.5, 1.0})))
                                // TODO: Q-learning comment: Create correct loss function.
                                .activation(Activation.SOFTMAX)
                                .build()
                )
                .pretrain(false)
                .backprop(true)
                .build();
        /// </editor-fold>


        // <editor-fold defaultstate="collapsed" desc="Configuration for network responsible for acceleration">
        MultiLayerConfiguration driveConfig = new NeuralNetConfiguration.Builder()
                .seed(driveSeed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .l1(drivel1)
                .l2(drivel2)
                .list()
                .layer(
                        0, new DenseLayer.Builder()
                                .nIn(in)
                                .nOut(driveHidden)
                                .build()
                )
                .layer(
                        1, new DenseLayer.Builder()
                                .nIn(driveHidden)
                                .nOut(driveHidden)
                                .build()
                )
                .layer(
                        2, new OutputLayer.Builder()
                                .nIn(driveHidden)
                                .nOut(out)
                                .activation(Activation.SOFTMAX)
                                .build()
                )
                .pretrain(false)
                .backprop(true)
                .build();
        /// </editor-fold>

        // Initialise networks array
        networks[0] = new MultiLayerNetwork(turnConfig);
        networks[1] = new MultiLayerNetwork(driveConfig);

        // Add listeners.
        networks[0].addListeners(LISTENER);
        networks[1].addListeners(LISTENER);

        start();
    }

    /**
     * Creates iterator to iterate over CSV data, read from inputstream.
     * {@code skipLines} lines will be skipped when initialising iterator.
     *
     * @param skipLines amount of lines to skip.
     */
    private void readAndCreateIter(int skipLines) {
        aStarReader = new CSVRecordReader(skipLines, GS.DELIM);

        try (InputStream is = new ByteArrayInputStream(CSV.getBytes(Charset.defaultCharset()))) {
            aStarReader.initialize(new InputStreamInputSplit(is));
        } catch (Exception e) {
            Logger.write(
                    new Object[]{"InputStream reading failure at AINN with id " + id, e},
                    Logger.Type.ERROR
            );
        }

        int batchSize = 5;
        int labelIndex = 7;
        int possibleClasses = 3;

        try {
            it = new RecordReaderDataSetIterator(aStarReader, batchSize, labelIndex, possibleClasses);
        } catch (Exception e) {
            Logger.write(
                    new Object[]{"Iterator creation failure at AINN with id " + id, e},
                    Logger.Type.ERROR
            );
        }
    }

    /**
     * Finds the index of the closest point in coordinates.
     * This is needed in order to select the correct line in the iterator.
     *
     * @return index of closes point in coordinates.
     */
    private int findIndexOfClosestPoint() {
        float minDist = Float.MAX_VALUE;
        Point2D.Float c = new Point2D.Float();

        for (Point2D.Float p : COORDINATES) {
            float d = dist(p);

            if (d < minDist) {
                minDist = d;
                c = p;
            }
        }

        return COORDINATES.indexOf(c);
    }


    /**
     * Converts {@code items} to floats. Floats can be directly added to a DataSet.
     *
     * @param items GridItem[] to convert
     * @return converted GridItem[] to float[]
     */
    private float[] gridItemsToFloats(@NotNull GridItem[] items) {
        // Logger.write("\n\t\t" + items.length);

        float[] ret = new float[GRIDSIZE];

        for (int i = 0; (i < items.length && i < GRIDSIZE); i++) {
            // Logger.write(new Object[]{i, Float.parseFloat(Character.toString(items[i].getSimpleRepr()))});

            ret[i] = (float) items[i].getSimpleRepr();
        }

        return ret;
    }

    /**
     * Computes and returns the distance of the current position to another point.
     * Used in {@link #findIndexOfClosestPoint()}.
     *
     * @param other some point.
     * @return euclidean distance of current position to {@code other}.
     */
    private float dist(@NotNull Point2D.Float other) {
        Vector3f pos = instance.getCurPosition();
        Point2D.Float me = new Point2D.Float(pos.x, -pos.z);

        return (float) Math.sqrt((other.x - me.x) * (other.x - me.x) + (other.y - me.y) * (other.y - me.y));
    }

    /**
     * Given a GridItem[][][][] instance {@code G},
     * computes the correct DataSet to use for the current iteration.
     *
     * @param G GridItem[][][][] instance
     * @return DataSet to use.
     */
    private DataSet getNextDataSet(@NotNull GridItem[][][][] G) {
        // Create Iterator at correct place
        readAndCreateIter(findIndexOfClosestPoint());

        // Retrieve data from grid (5 : hardcoded GRIDSIZE)
        float[][][] gridData = new float[GRIDSIZE][GRIDSIZE][GRIDSIZE];

        for (int i = 0; (i < G.length && i < GRIDSIZE); i++) {
            for (int j = 0; (j < G[i].length && j < GRIDSIZE); j++) {
                for (int k = 0; (k < G[i][j].length && k < GRIDSIZE); k++) {
                    gridData[i][k] = gridItemsToFloats(G[i][j][k]);
                }
            }
        }

        // Add Data To DataSet
        DataSet DS = it.next();


        for (float[][] aGridData : gridData) {
            for (float[] anAGridData : aGridData) {
                Logger.write(
                        Arrays.toString(anAGridData).replaceAll("\n", GS.LS),
                        Logger.Type.ERROR
                );
            }
        }

        INDArray Nd4jArr = Nd4j.create(gridData);

        Logger.write(
                new Object[]{"Nd4jArr looks like:", Nd4jArr.toString().replaceAll("\n", GS.LS)},
                Logger.Type.ERROR
        );

        DS.addFeatureVector(Nd4jArr);

        return DS;
    }

    /**
     * Runs one iteration of the AINN;
     * 1. Get DataSet to train on ({@link #getNextDataSet(GridItem[][][][])}).
     * 2. Normalise said DataSet using {@code DataNormalization}.
     * 3. Train using {@code fit()} function.
     */
    protected void execute() {
        GridItem[][][][] G = GS.grid.getItemsAround(instance, 2, 2, 2);
        DataSet nextSet = getNextDataSet(G);

        // Log initial data sent to network
        Logger.write(
                new Object[]{"Initial data non-normalised " + id + ":", nextSet},
                Logger.Type.INFO
        );

        normalizer.fit(nextSet);
        normalizer.transform(nextSet);

        // Log data after normalising
        Logger.write(
                new Object[]{"Initial normalised data " + id + ":", nextSet},
                Logger.Type.INFO
        );

        // TODO Q-Learning comment: Here we have a current state: nextSet.


        // Train turning net
        try {
            networks[0].fit(nextSet);
        } catch (Exception e) {
            Logger.write(
                    new Object[]{"Fitting network[0] for turning failed " + id, e},
                    Logger.Type.ERROR
            );
        }

        // Train driving net
        try {
            networks[1].fit(nextSet);
        } catch (Exception e) {
            Logger.write(
                    new Object[]{"Fitting network[1] for driving failed " + id, e},
                    Logger.Type.ERROR
            );
        }

        // Visualise
        for (MultiLayerNetwork net : networks) {
            if (net == networks[0]) {
                visualize(net);
            }
        }
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

        new Thread("AINN-thread-" + id) {
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
                    }

                    // Wait to fill the time.
                    long curTime = System.currentTimeMillis();
                    long wait = MILLIS_PER_UPDATE - (curTime - prevTime);
                    prevTime = curTime;
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
        return new PStructAction(curAction.turn, curAction.accel,
                curAction.verticalVelocity, dt);
    }

    /**
     * Makes sure that this class only uses 1 thread.
     */
    private static void limitThreadsToOne() {
        Nd4jBlas nd4jBlas = (Nd4jBlas) Nd4j.factory().blas();
        NativeOpsHolder instance = NativeOpsHolder.getInstance();
        NativeOps deviceNativeOps = instance.getDeviceNativeOps();
        deviceNativeOps.setOmpNumThreads(1);
        nd4jBlas.setMaxThreads(1);
    }

    // TMP CLASS
    public static class InstanceImpl
            extends GridItemInstance {

        public InstanceImpl() {
            super(new PosHitBox3f(), 1, 0, 0, 0, new OBJTexture(new OBJCollection(), new TextureImg(0f, 0f)), 0, new PhysicsContext());
        }

        @Override
        public boolean isStatic() {
            return true;
        }
    }

}