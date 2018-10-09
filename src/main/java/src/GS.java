
package src;


// Jogamp imports

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import net.java.games.input.ContrlEnv;
import net.java.games.input.ControllerEnvironment;
import org.joml.Vector3f;
import src.Assets.GUI;
import src.Assets.Light;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.Assets.skybox.Skybox;
import src.Controllers.CameraController;
import src.Controllers.PlayerController;
import src.Physics.Physics;
import src.Renderer.Camera;
import src.Renderer.Renderer;
import src.grid.Grid;
import src.gui.MainPanel;
import src.racetrack.Track;
import src.tools.event.ControllerKey;
import src.tools.event.ControllerKeyDetector;
import src.tools.event.Key;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.event.keyAction.CarKeyAction;
import src.tools.event.keyAction.KeyAction;
import src.tools.event.keyAction.PlayerKeyAction;
import src.tools.font.FontLoader;
import src.tools.io.BufferedReaderPlus;
import src.tools.io.ImageManager;
import src.tools.log.*;
import src.tools.update.Updater;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import static src.tools.event.ControllerKey.DEFAULT_GET_COMP_MODE;
import static src.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CONFIG;

// Own imports
// Java imports
// JInput imports


/**
 * Global state.
 */
public class GS {
    /**-------------------------------------------------------------------------
     * Enums.
     * -------------------------------------------------------------------------
     */
    /**
     * Enum class for the game state.
     */
    public static enum GameState {
        PLAYING, PAUSED, STOPPED;
    }
    
    /**
     * TODO.
     */
    public static enum CameraMode {
        SOME_MODE;
    }
    
    
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    /** The application name. */
    final public static String APP_NAME = "2IOE0 game";
    
    /** The system independant file separator. */
    final public static String FS = System.getProperty("file.separator");
    
    /** The system independant line separator. */
    final public static String LS = System.getProperty("line.separator");
    
    /** Whether to disable the logging by {@link java.util.logging.Logger}. */
    final private static boolean DISABLE_JAVA_LOGGING = true;
    
    /** Handy file paths. */
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src"  + FS;
    final public static String FONT_DIR = WORKING_DIR + "tools"
            + FS + "font" + FS;
    
    final public static String LOG_FILE = WORKING_DIR + "log.log";
    final public static String SHADER_DIR = WORKING_DIR
            + "shaderPrograms" + FS;
    
    final public static String DATA_DIR = WORKING_DIR + "data" + FS;
    final public static String KEYS_CONFIG = DATA_DIR + "keys.conf";
    
    final public static String RESOURCE_DIR = WORKING_DIR + "res" + FS;
    final public static String MUSIC_DIR = RESOURCE_DIR + "music" + FS;
    final public static String IMG_DIR = RESOURCE_DIR + "img" + FS;
    final public static String OBJ_DIR = RESOURCE_DIR + "obj" + FS;
    final public static String TEX_DIR = RESOURCE_DIR + "textures" + FS;
    
    
    /** Image constants. */
    final public static String FRAME_ICON = "FRAME_ICON";
    
    /** Assets and camera. */
    final private static List<Instance> assets = new ArrayList<>();
    final private static List<Instance> materialAssets = new ArrayList();
    final private static List<Instance> terrain = new ArrayList<>();
    final private static List<Light> lights = new ArrayList<>();
    final private static List<GUI> guis = new ArrayList<>();


    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    private static GameState gameState = GameState.PLAYING;
    public static ControllerKeyDetector keyDet;
    public static MainPanel mainPanel;
    public static Camera camera;
    public static CameraController cameraController;
    public static PlayerController playerController;
    public static Grid grid;
    private static CameraMode cameraMode;
    private static boolean fullScreen = false;
    private static Map<KeyAction, List<ControllerKey>> keyMap = new HashMap<>();
    private static Simulator simulator;
    private static Renderer renderer;
    private static GLCanvas canvas;
    private static FPSAnimator animator;
    private static Track raceTrack;
    private static Skybox skybox;

    public static List<Car> cars = new ArrayList<>();
    public static Instance player;

    public static int width = 1080;
    public static int height = 720;
    
    
    
    /**-------------------------------------------------------------------------
     * Logger initialization.
     * -------------------------------------------------------------------------
     */
    public static void init() {
        // Initialize the logger(s).
        // Setup file logger to prevent missing events.
        Logger fileLogger = null;
        try {
            fileLogger = new FileLogger(LOG_FILE);
            
        } catch (IOException e) {
            System.err.println(e);
        }
        Logger.setDefaultLogger(fileLogger);
        
        // Initialize the fonts.
        FontLoader.init();
        
        // Setup key map for the screen logger.
        Map<Key, Runnable> debugMap = new HashMap<>();
        debugMap.put(Key.ESC, () -> System.exit(0));
        debugMap.put(Key.N1, () -> printDebug());
        debugMap.put(Key.N2, () -> {
            setFullScreen(!isFullScreen());
        });
        debugMap.put(Key.N3, () -> {
            ((ContrlEnv) ControllerEnvironment
                    .getDefaultEnvironment()).forceUpdate();
        });
        Logger.setDefaultLogger(new ThreadLogger(new MultiLogger(
                new ScreenLogger("Test logger", debugMap),
                fileLogger // Use the same file logger to keep the logfile.
        )));
        Logger.write("Starting application...", Logger.Type.INFO);
        Logger.setShutDownMessage("Shutting down application...",
                Logger.Type.INFO);
        
        if (DISABLE_JAVA_LOGGING) {
            LogManager.getLogManager().reset();
            Logger.write("Logging via \"java.util.logging.Logger\" has "
                    + "now been disabled!");
        }
        
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities cap = new GLCapabilities(profile);
        canvas = new GLCanvas(cap);
        
        registerImageSheets();
        createGUI();
        GS.keyDet = new ControllerKeyDetector();
        
        reloadKeyMap();
        
        grid = new Grid(0f, 0f, -1000f, 4f, 4f, 2000f);

        animator = new FPSAnimator(canvas, 60, true);

        camera = new Camera(new Vector3f(0, 5, 20), 0, 0, 0);
        cameraController = new CameraController(camera);
        Locker.add(cameraController);

        simulator = new Simulator();
        renderer = new Renderer(simulator, width, height);

        canvas.addGLEventListener(renderer);
        canvas.setSize(1080, 720);

        animator.start();
        renderer.cleanup();

        Updater.start();
    }
    
    /**
     * Registers all images that are neede in the application.
     */
    private static void registerImageSheets() {
        ImageManager.registerSheet("game_icon.png", GS.FRAME_ICON, -1, -1);
    }
    
    /**
     * Creates the GUI of the application.
     * Also adds all necessary listeners.
     */
    private static void createGUI() {
        GS.mainPanel = new MainPanel();

        GS.mainPanel.add(canvas);
        GS.mainPanel.setSize(1080, 720);
        
        // Add listeners.
        mainPanel.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Updater.cancel();
                keyDet.cancel();
                Locker.remove(keyDet);
            }
        });
    }
    
    /**
     * Reloads the key map.
     */
    private static void reloadKeyMap() {
        Logger.write(new Object[] {
            "",
            "===== BEGIN RELOADING KEY MAP =====",
             "File = " + KEYS_CONFIG
        }, Logger.Type.INFO);
        
        // Create new key map.
        Map<KeyAction, List<ControllerKey>> newKeyMap = new HashMap<>();
        
        try (BufferedReaderPlus brp = new BufferedReaderPlus(KEYS_CONFIG,
                HASHTAG_COMMENT, TYPE_CONFIG)) {
            
            List<ControllerKey> keys = new ArrayList<>();
            while (brp.readNextConfLine()) {
                try {/*
                    if (brp.fieldEquals(Key.class.getName())) {
                        keys.add(Key
                                .createFromString(brp.getData()));
                        System.out.println("Key created: "
                                + keys.get(keys.size() - 1));
                        
                    } else */
                    if (brp.fieldEquals(ControllerKey.class.getName())) {
                        keys.add(ControllerKey
                                .createFromString(brp.getData()));
                        System.out.println("ControllerKey created: "
                                + keys.get(keys.size() - 1));
                        
                    } else if (brp.fieldEquals(CarKeyAction.class.getName())) {
                        KeyAction action = CarKeyAction
                                .createFromString(brp.getData());
                        newKeyMap.put(action, keys);
                        keys = new ArrayList<>();
                        System.out.println("CarKeyAction created: " + action);

                    } else if (brp.fieldEquals(CameraKeyAction.class.getName())) {
                        KeyAction action = CameraKeyAction
                                .createFromString(brp.getData());
                        newKeyMap.put(action, keys);
                        keys = new ArrayList<>();
                        System.out.println("CameraKeyAction created: " + action);

                    } else if (brp.fieldEquals(PlayerKeyAction.class.getName())) {
                        KeyAction action = PlayerKeyAction
                                .createFromString(brp.getData());
                        newKeyMap.put(action, keys);
                        keys = new ArrayList<>();
                        System.out.println("PlayerKeyAction created: " + action);

                    } else {
                        Logger.write("Ignored field on line "
                                + brp.getLineCounter() + ": " + brp.getField(),
                                Logger.Type.WARNING);
                    }
                    
                } catch (IllegalArgumentException e) {
                    Logger.write(new Object[] {
                        "Exception while reading key map on line "
                                + brp.getLineCounter() + ":",
                        e
                    }, Logger.Type.ERROR);
                }
            }
            
        } catch (IOException e) {
            Logger.write(e);
        }
        
        // Replace the key map.
        Locker.lock(ControllerKey.class);
        try {
            keyMap.clear();
            keyMap = newKeyMap;
            System.out.println("key map:" + keyMap);
            
        } finally {
            Locker.unlock(ControllerKey.class);
            Logger.write(new Object[] {
                "===== FINISHED RELOADING KEY MAP =====",
                ""
            }, Logger.Type.INFO);
        }
    }
    
    /**
     * Gets the keys that are defined for the given action.
     * 
     * @param action the action to get the keys from.
     * @return either:
     *     - A list containing all keys corresponding to this action.
     *     - {@code null}, meaning that the action is undefined.
     */
    public static List<ControllerKey> getKeys(KeyAction action) {
        Locker.lock(ControllerKey.class);
        try {
            ControllerKey.setCompMode(DEFAULT_GET_COMP_MODE);
            return keyMap.get(action);
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * @return the current fps rate.
     */
    public static double getFPS() {
        return Updater.getFPS();
    }
    
    /**
     * @param fps the new fps rate.
     */
    public static void setFPS(double fps) {
        Updater.setFPS(fps);
    }
    
    /**
     * @return the current game state.
     */
    public static GameState getGameState() {
        return gameState;
    }
    
    /**
     * @param state the new game state.
     */
    public static void setGameState(GameState state) {
        gameState = state;
    }
    
    public static void setFullScreen(boolean full) {
        if (fullScreen == full) return;
        fullScreen = full;
        if (mainPanel != null) mainPanel.update();
    }
    
    /**
     * @return {@code true} if the main panel is in full screen.
     */
    public static boolean isFullScreen() {
        return fullScreen;
    }
    
    /**
     * Prints a debug string to the logger.
     */
    public static void printDebug() {
        Logger.write("");
        Logger.write("==== Begin debug ====");
        Logger.write("Working dir:" + WORKING_DIR);
        Logger.write("Img dir:" + IMG_DIR);
        Logger.write("Music dir:" + MUSIC_DIR);
        Logger.write("Data dir:" + DATA_DIR);
        Logger.write("Log file:" + LOG_FILE);
        Logger.write("Font dir:" + FONT_DIR);
        Logger.write("==== End debug ====");
        Logger.write("");
    }
    
    public static List<Instance> getAssets(){
        return assets;
    }

    public static List<Instance> getTerrain(){
        return terrain;
    }

    public static List<Light> getLights(){
        return lights;
    }

    public static List<GUI> getGUIs(){
        return guis;
    }

    public static List<Instance> getMaterialAssets(){ return materialAssets; }

    public static void addMaterialAsset(Instance asset) { materialAssets.add(asset);}

    public static void addGUI(GUI gui){
        guis.add(gui);
    }
    
    public static void addAsset(Instance asset){
        assets.add(asset);
    }

    public static void addTerrain(Instance asset){
        terrain.add(asset);
    }

    public static void addLight(Light light){
        lights.add(light);
    }

    public static CameraMode isCameraMode() {
        return cameraMode;
    }
    
    public static void setCameraMode(CameraMode cameraMode) {
        GS.cameraMode = cameraMode;
    }

    public static PlayerController getPlayerController() {
        return  playerController;
    }
    
    public static void setTrack(Track track) {
        Physics.setTrack(track);
        raceTrack = track;
    }

    public static void setSkybox(Skybox box){
        skybox = box;
    }

    public static Skybox getSkybox(){
        return skybox;
    }
    
    public static Track getTrack() {
        return raceTrack;
    }
    
    
}
