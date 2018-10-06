package src.AI;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.nativeblas.NativeOps;
import org.nd4j.nativeblas.NativeOpsHolder;
import org.nd4j.nativeblas.Nd4jBlas;
import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.tools.log.Logger;

/**
 * Creates a neural network array for driving.
 * Start generating actions by invoking {@link #start()},
 * and stop by invoking {@link #stop()}.
 */
public class AINN {
    private static int idCounter = 0;
    private int id = idCounter++;
    
    final private Instance instance;

    private MultiLayerNetwork[] networks = new MultiLayerNetwork[2];
    private RecordReader aStarReader;
    private DataSetIterator it;

    private int turnSeed;
    private int turnl1;
    private int turnl2;
    private int turnHidden;

    private int driveSeed;
    private int drivel1;
    private int drivel2;
    private int driveHidden;

    private int in;
    private int out;
    
    // The current action.
    private PStructAction curAction = new PStructAction(0, 0, 0, 1);
    
    // Update thread releated variables.
    private Thread updateThread = null;
    private Lock lock = new ReentrantLock();
    private boolean stopUpdateThread = false;
    
    // Update delay.
    private long MILLIS_PER_UPDATE = 100L;
    
    
    /**
     * Constructor.
     */
    public AINN(Instance instance) {
        this.instance = instance;
        
        // Set threads to 1.
        setMaxThreads(1);

        // Reading Data
        aStarReader = new CSVRecordReader(0, GS.DELIM);

        try {
            aStarReader.initialize(new FileSplit(new File(
                    GS.RESOURCE_DIR + "A_star_data")));
            
        } catch (IOException | InterruptedException e) {
            Logger.write(new Object[] {
                    "At AIIN " + id + ": CSV reading failure.",
                    e
            }, Logger.Type.ERROR);
        }

        // TODO Create iterator

        // TODO Add other fields from GS

        // TODO Normalize data


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
    }
    
    /**
     * Listener for updating the values after an iteration of the AINN.
     */
    final private BaseTrainingListener LISTENER = new BaseTrainingListener() {
        @Override
        public void onEpochEnd(Model model) {
            if (model == networks[0]) { // turn model.
                int output = 1; // TODO
                
                curAction.turn = output - 1;
                
            } else if (model == networks[1]) { // drive model.
                int output = 1; // TODO
                
                curAction.accel = output - 1;
            }
        }
    };
    
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
                        Logger.write(new Object[] {
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
                        Logger.write(new Object[] {
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
     *     {@code false} otherwise.
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
     * Runs one iteration of the AINN.
     */
    protected void execute() {
        // TODO
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
     * Makes sure that this class only uses a total of {@code x} threads.
     *
     * @param x amount of threads allowed for native ops and blas
     */
    private static void setMaxThreads(int x) {
        Nd4jBlas nd4jBlas = (Nd4jBlas) Nd4j.factory().blas();
        NativeOpsHolder instance = NativeOpsHolder.getInstance();
        NativeOps deviceNativeOps = instance.getDeviceNativeOps();
        deviceNativeOps.setOmpNumThreads(x);
        nd4jBlas.setMaxThreads(x);
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
    
    
}
