package src.AI;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.InputStreamInputSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.javatuples.Pair;
import org.javatuples.Quintet;
import org.javatuples.Triplet;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.nativeblas.NativeOps;
import org.nd4j.nativeblas.NativeOpsHolder;
import org.nd4j.nativeblas.Nd4jBlas;
import src.Assets.OBJCollection;
import src.Assets.OBJTexture;
import src.Assets.TextureImg;
import src.Assets.instance.GridItemInstance;
import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.PhysicsContext;
import src.Progress.ProgressManager;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Creates a neural network array for driving.
 * Start generating actions by invoking {@link #start()},
 * and stop by invoking {@link #stop()}.
 */
public class AINN {
    // <editor-fold defaultstate="collapsed" desc="Global variables">
    private static int idCounter = 0;
    private int id = idCounter++;

    InstanceImpl simulInst;
    Instance.State simulState;
    Instance.State drivingState;
    private PStructAction curAction;
    final static List<PStructAction> actions;
    // private long dt = 0;

    private Thread updateThread = null;
    private Lock lock = new ReentrantLock();
    private boolean stopUpdateThread = false;
    final private long MILLIS_PER_UPDATE = 40L; // TODO set to 1000/60L for testing A*

    private MultiLayerNetwork network;
    private MyIterator dataIterator;
    private INDArray currIterData;
    private List<Quintet<INDArray, PStructAction, Double, INDArray, Boolean>> replayMemory;

    private static List<Point2D.Float> COORDINATES;
    public static List<Triplet<Point2D.Float, Integer, Integer>> OUTCOMES;
    // int outcomeIndex = 0;

    final protected GridItemInstance instance;
    final private static String CSV;

    final static List<String> labelNames;

    final static int GRIDSIZE = 5;

    final private static int POSSIBLECLASSES = 9;
    final private static int BATCHSIZE = 1;
    final private static int LABELINDEX = 7;
    // </editor-fold>

    /**
     * Function to reverse a list
     *
     * @param list the list to reverse
     * @param <T>  of type T
     * @return reversed list of type T (ArrayList)
     */
    private static <T> List<T> reverse(final List<T> list) {
        final int last = list.size() - 1;
        return IntStream.rangeClosed(0, last)
                .map(i -> (last - i))
                .mapToObj(list::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Static block for reading CSV once and setting labelNames.
    static {
        List<Point2D.Float> C = new ArrayList<>();
        List<Triplet<Point2D.Float, Integer, Integer>> O = new ArrayList<>();

        COORDINATES = new ArrayList<>();
        OUTCOMES = new ArrayList<>();

        String fileName = "nodes.csv";
        String filePath = GS.RESOURCE_DIR + "A_star_data";
        StringBuilder sb = new StringBuilder();

        try (BufferedReader r = new BufferedReader(new FileReader(filePath + GS.FS + fileName))) {
            String line;

            while ((line = r.readLine()) != null) {
                sb.append(line);
                sb.append(GS.LS);

                String[] tokens = line.split(";");
                Point2D.Float coordinate = new Point2D.Float(Float.parseFloat(tokens[0]) + 15f, Float.parseFloat(tokens[1]));
                Integer turn = Integer.parseInt(tokens[7]);
                Integer accel = Integer.parseInt(tokens[8]);

                C.add(coordinate);
                O.add(new Triplet<>(coordinate, turn, accel));
            }

        } catch (IOException e) {
            Logger.write(new Object[]{"Static file reading failure", e}, Logger.Type.ERROR);
        }

        CSV = sb.toString();

        // Reverse Coordinates and outcomes list since A* puts in different order
        COORDINATES = reverse(C);
        OUTCOMES = reverse(O);

        // Setting labelNames and actions
        labelNames = new LinkedList<>();
        actions = new LinkedList<>();
        labelNames.add("label: 0; (-1, -1); (Left , Backwards)");
        actions.add(new PStructAction(-1, -1, 0, 1));
        labelNames.add("label: 1; (-1, 0); (Left, Nothing)");
        actions.add(new PStructAction(-1, 0, 0, 1));
        labelNames.add("label: 2; (-1, 1); (Left, Forwards)");
        actions.add(new PStructAction(-1, 1, 0, 1));
        labelNames.add("label: 3; (0, -1); (Nothing, Backwards)");
        actions.add(new PStructAction(0, -1, 0, 1));
        labelNames.add("label: 4; (0, 0); (Nothing, Nothing)");
        actions.add(new PStructAction(0, 0, 0, 1));
        labelNames.add("label: 5; (0, 1); (Nothing, Forwards)");
        actions.add(new PStructAction(0, 1, 0, 1));
        labelNames.add("label: 6; (1, -1); (Right, Backwards)");
        actions.add(new PStructAction(1, -1, 0, 1));
        labelNames.add("label: 7; (1, 0); (Right, Nothing)");
        actions.add(new PStructAction(1, 0, 0, 1));
        labelNames.add("label: 8; (1, 1); (Right, Forwards)");
        actions.add(new PStructAction(1, 1, 0, 1));
    }

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
        // Set instance
        this.instance = instance;

        // Limit threads
        limitThreadsToOne();

        // Set states
        curAction = new PStructAction(0, 0, 0, 1);
        drivingState = instance.getState();
        simulInst = new InstanceImpl();

        // do initial code before loop
        initAINN();
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

    /**
     * Initialises memory and networks and then starts the loop.
     */
    private void initAINN() {
        // initialise replay memory
        replayMemory = new LinkedList<>();

        // init action-value function Q with random weights
        configNetworks();

        // observe initial state S:
        try {
            // Set iterator correctly
            readAndCreateIter(findIndexOfClosestPoint(findPoint(drivingState)));

            // Get next initial state
            currIterData = dataIterator.next().getFeatures();
        } catch (NullPointerException e) {
            Logger.write("Cannot create first state due to nullpointer");
        }

        // Start the loop
        SwingUtilities.invokeLater(this::start);
    }

    /**
     * Create neural network configuration and add listener to it.
     */
    private void configNetworks() {
        // <editor-fold defaultstate="collapsed" desc="Neural network configuration.">
        int in = 12;
        int out = 9;

        int turnSeed = 69;
        double turnl1 = 0.001;
        double turnl2 = 0.001;
        int turnHidden = 25;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
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
                        2, new OutputLayer.Builder().nIn(turnHidden).nOut(out).activation(Activation.SOFTMAX).lossFunction(LossFunctions.LossFunction.MSE).build()
                )
                .pretrain(false)
                .backprop(true)
                .build();
        // </editor-fold>

        network = new MultiLayerNetwork(conf);
        network.addListeners(new ScoreIterationListener());
        network.init();

        visualize(network);
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

        @SuppressWarnings("deprecation")
        RecordReader aStarReader = new CSVRecordReader(skipLines, GS.DELIM);

        try (InputStream is = new ByteArrayInputStream(CSV.getBytes(Charset.defaultCharset()))) {
            aStarReader.initialize(new InputStreamInputSplit(is));
        } catch (Exception e) {
            Logger.write(new Object[]{"InputStream reading failure at AINN with id " + id, e}, Logger.Type.ERROR);
        }

        GridItem[][][][] G = GS.grid.getItemsAround(instance, 2, 2, 2);

        try {
            dataIterator = new MyIterator(aStarReader, BATCHSIZE, LABELINDEX, POSSIBLECLASSES, G);
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
    public int findIndexOfClosestPoint(Point2D.Float me) {
        float minDist = Float.MAX_VALUE;
        Point2D.Float c = new Point2D.Float(me.x, me.y);

        for (Point2D.Float p : COORDINATES) {
            float d = dist(me, p);

            if (d < minDist) {
                minDist = d;
                c = p;
            }
        }

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
    public Point2D.Float findPoint(Instance.State state) {
        Vector3f pos = state.box.pos();

        return new Point2D.Float((-pos.z + 15f), pos.x);
    }

    /**
     * I suggest including something with:
     * - How centered am I on the track?
     * - How far am I in the race (checkpoint wise)?
     * - Distance to player in front (if exists, otherwise 0)?
     */
    private float calculateReward() {
        // Get current point.
        Point2D.Float me = findPoint(simulState);

        // Get closest point from CSV
        Point2D.Float you = COORDINATES.get(findIndexOfClosestPoint(me));

        // Reward for now is the distance between both.
        return dist(me, you);
    }

    private Quintet<INDArray, PStructAction, Double, INDArray, Boolean> getRandomQuartet() {
        int numOfReplays = replayMemory.size();

        if (numOfReplays == 0) {
            Logger.write("Cannot get replay, does not exist.");
            return null;
        } else {
            return replayMemory.get(GS.R.nextInt(numOfReplays));
        }
    }

    /**
     * Returns a miniBatch of size {@code batchSize}.
     *
     * @param batchSize size of the minibatch
     * @return miniBatch
     */
    private List<Quintet<INDArray, PStructAction, Double, INDArray, Boolean>> getMiniBatch(int batchSize) {
        List<Quintet<INDArray, PStructAction, Double, INDArray, Boolean>> miniBatch = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            miniBatch.add(getRandomQuartet());
        }

        return miniBatch;
    }

    /**
     * Chooses an action using probability {@code epsilon} randomly, otherwise the best one.
     *
     * @param epsilon probability for a random action
     * @return an action
     */
    private PStructAction chooseAction(float epsilon) {
        if ((float) Math.random() < epsilon) {
            return actions.get(GS.R.nextInt(actions.size()));
        } else {
            INDArray out = null;
            float[] output = new float[9];

            // Try to get output from network
            try {
                out = network.output(currIterData);
            } catch (Exception e) {
                Logger.write(new Object[]{"Cannot get OUTPUT for model ", network.toString().replaceAll("\n", GS.LS), e,}, Logger.Type.ERROR);
                e.printStackTrace();
            }

            // Try to convert INDArray output to integer output
            try {
                output = out.toFloatVector();
            } catch (Exception e) {
                Logger.write(new Object[]{"Cannot convert OUTPUT to float for model" + network.toString(), e}, Logger.Type.ERROR);
                e.printStackTrace();
            }

            int index = 0;

            float maxVal = Float.MIN_VALUE;
            for (int i = 0; i < output.length; i++) {
                if (maxVal < output[i]) {
                    maxVal = output[i];
                    index = i;
                }
            }

            return actions.get(index);
        }
    }

    /**
     * Does an action.
     *
     * @param action action to do
     * @param state state on which to do action
     * @return new state by doing {@code action} on {@code state}.
     */
    private Instance.State doAction(@NotNull PStructAction action, Instance.State state) {
        Logger.write(new Object[]{"Will do action: " + action.toString() + "on state" + state.toString()});

        InstanceImpl x = new InstanceImpl();
        x.setState(state);

        SwingUtilities.invokeLater(() -> x.movement(action));

        return x.getState();
    }

    private Pair<float[], PStructAction[]> computeTargetValues(int batchSize) {
        float[] targets = new float[batchSize];
        PStructAction[] carActions = new PStructAction[batchSize];

        List<Quintet<INDArray, PStructAction, Double, INDArray, Boolean>> miniBatch = getMiniBatch(batchSize);

        for (int i = 0; i < batchSize; i++) {
            Quintet<INDArray, PStructAction, Double, INDArray, Boolean> me = miniBatch.get(i);

            // If state is terminal
            if (me.getValue4()) {
                // Set target value to current reward
                targets[i] = me.getValue2().floatValue();
                carActions[i] = me.getValue1();
            } else {
                float gamma = 0.4f;
                float netArgMax = network.output(me.getValue3()).maxNumber().floatValue();

                targets[i] = me.getValue2().floatValue() + gamma * netArgMax;
                carActions[i] = me.getValue1();
                Logger.write(new Object[]{"netArgMax: " + netArgMax, "setting target value " + i + "to " + (me.getValue2().floatValue() + gamma * netArgMax)});
            }
        }

        return new Pair<>(targets, carActions);
    }

    /**
     * Computes target values
     * @param P Pair of float values and actions
     * @return target values.
     */
    private INDArray getTarget(Pair<float[], PStructAction[]> P) {
        float[] individualTargets = P.getValue0();
        PStructAction[] individualActions = P.getValue1();

        float[] retFloat = new float[9];

        for (int i = 0; i < individualActions.length; i++) {
            if (individualActions[i].equals(actions.get(0))) {
                retFloat[0] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(1))) {
                retFloat[1] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(2))) {
                retFloat[2] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(3))) {
                retFloat[3] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(4))) {
                retFloat[4] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(5))) {
                retFloat[5] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(6))) {
                retFloat[6] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(7))) {
                retFloat[7] += individualTargets[i];
            } else if (individualActions[i].equals(actions.get(8))) {
                retFloat[8] += individualTargets[i];
            }
        }

        return Nd4j.create(retFloat);
    }

    /**
     * Runs one iteration of the AINN;
     * 1. Get next DataSet using {@link MyIterator}.
     * 2. Normalise said DataSet using {@link org.nd4j.linalg.dataset.api.preprocessor.DataNormalization}.
     * 3. Train using {@code fit()} function.
     */
    private void execute() {
        // Choose an action with probability epsilon doing random stuff
        float epsilon = 0.2f;
        PStructAction action = chooseAction(epsilon);

        // Get the current state of things
        drivingState = instance.getState();

        // Compute simulated state by doing action on some state
        simulState = doAction(action, drivingState);

        // Compute reward from doing the action
        float reward = calculateReward();

        // Observe the new state as per simulation by setting iterator correctly
        readAndCreateIter(findIndexOfClosestPoint(findPoint(simulState)));
        INDArray newState = dataIterator.next().getFeatures();

        // Compute if newState is final
        ProgressManager PM = simulInst.getProgressManager();
        boolean isFinal = PM.lapTotal < PM.lap;

        // Store replay in memory
        replayMemory.add(new Quintet<>(currIterData, action, ((double) reward), newState, isFinal));

        // For each minibatch, compute target value
        int batchSize = 32;
        Pair<float[], PStructAction[]> individualTargets = computeTargetValues(batchSize);

        // Construct label INDarray
        INDArray target = getTarget(individualTargets);

        // Train network on target with loss MSE
        try {
            network.fit(currIterData, target);
        } catch (Exception e) {
            Logger.write(new Object[]{"Cannot fit network " + id, e}, Logger.Type.ERROR);
            e.printStackTrace();
        }

        // Set state to newState
        currIterData = newState;

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
     * Instance implementation, used for simulating actions that should not be rendered.
     */
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

