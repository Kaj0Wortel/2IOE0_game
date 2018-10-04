package src.AI;

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
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.nativeblas.NativeOps;
import org.nd4j.nativeblas.NativeOpsHolder;
import org.nd4j.nativeblas.Nd4jBlas;
import src.GS;
import src.Physics.PStructAction;
import src.tools.log.Logger;

/**
 * Creates a neural network array for driving.
 */
public class AINN {

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
    
    private PStructAction curState = new PStructAction(0, 0, 0, 1);
    private PStructAction newState = null;
    private boolean updatedLeftRight = false;
    private boolean updatedForwardBack = false;
    

    public AINN() {
        setMaxThreads(1);           // Set threads to 1.

        // Reading Data
        aStarReader = new CSVRecordReader(0, GS.DELIM);

        try {
            aStarReader.initialize(new FileSplit(new ClassPathResource("astar.txt").getFile()));
        } catch (Exception e) {
            Logger.write(new Object[] {
                    "At AINN: CSV reading failure.",
                    e
            }, Logger.Type.INFO);
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
    }

    public void execute() {
        updatedLeftRight = false;
        updatedForwardBack = false;
        // TODO
    }
    
    private BaseTrainingListener listener = new BaseTrainingListener() {
        @Override
        public void onEpochEnd(Model model) {
            if (model == networks[0]) { // turn model.
                updatedLeftRight = true;
                
                
                
            } else if (model == networks[1]) { // drive model.
                updatedForwardBack = true;
                
            }
        }
    };
    

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
