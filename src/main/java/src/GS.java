
package src;


// Own imports


// Java imports

import src.Assets.Instance;
import src.Controllers.CameraController;
import src.Renderer.Camera;
import src.gui.MainPanel;
import src.tools.event.Key;
import src.tools.io.ImageManager;
import src.tools.log.FileLogger;
import src.tools.log.Logger;
import src.tools.log.MultiLogger;
import src.tools.log.ScreenLogger;
import src.tools.update.Updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import src.tools.event.ControllerKeyDetector;
import src.tools.font.FontLoader;
import src.tools.log.ThreadLogger;


/**
 * Global state.
 */
public class GS {
    /**-------------------------------------------------------------------------
     * Enums.
     * -------------------------------------------------------------------------
     */
    // Enum class for the game state.
    public static enum GameState {
        PLAYING, PAUSED, STOPPED;
    }
    
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
    
    final public static String DATA_DIR = WORKING_DIR + "data" + FS;
    final public static String LOG_FILE = WORKING_DIR + "log.log";
    final public static String SHADER_DIR = WORKING_DIR
            + "shaderPrograms" + FS;
    
    final public static String RESOURCE_DIR = WORKING_DIR + "res" + FS;
    final public static String MUSIC_DIR = RESOURCE_DIR + "music" + FS;
    final public static String IMG_DIR = RESOURCE_DIR + "img" + FS;
    final public static String OBJ_DIR = RESOURCE_DIR + "obj" + FS;
    
    
    /** Image constants. */
    final public static String FRAME_ICON = "FRAME_ICON";
    
    /** Assets and camera. */
    final private static List<Instance> assets = new ArrayList<>();
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    private static GameState gameState = GameState.PLAYING;
    public static ControllerKeyDetector keyDet;
    public static MainPanel mainPanel;
    public static Camera camera;
    public static CameraMode cameraMode;
    public static CameraController cameraController;
    private static boolean fullScreen = false;
    
    
    /**-------------------------------------------------------------------------
     * Logger initialization.
     * -------------------------------------------------------------------------
     */
    public static void init() {
        // Initialize the logger(s).
        // Setup file logger to prevent missing events.
        Logger fileLogger = new FileLogger(LOG_FILE);
        Logger.setDefaultLogger(fileLogger);
        
        // Initialize the fonts.
        FontLoader.init();
        
        // Setup key map for the screen logger.
        Map<Key, Runnable> keyMap = new HashMap<>();
        keyMap.put(Key.ESC, () -> System.exit(0));
        keyMap.put(Key.N1, () -> printDebug());
        keyMap.put(Key.N2, () -> {
            setFullScreen(!isFullScreen());
        });
        Logger.setDefaultLogger(new ThreadLogger(new MultiLogger(
                new ScreenLogger("Test logger", keyMap),
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

        cameraController = new CameraController(camera);

        registerImageSheets();
        
        createGUI();
    }
    
    /**
     * Registers all images that are neede in the application.
     */
    private static void registerImageSheets() {
        ImageManager.registerSheet("game_icon.png", GS.FRAME_ICON, -1, -1);
    }
    
    /**
     * Creates the GUI of the application.
     * Also creates the global key listener.
     */
    private static void createGUI() {
        GS.mainPanel = new MainPanel();
        GS.keyDet = new ControllerKeyDetector(GS.mainPanel);
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
    
    public static void addAsset(Instance asset){
        assets.add(asset);
    }
    
    public static CameraMode isCameraMode() {
        return cameraMode;
    }
    
    public static void setCameraMode(CameraMode cameraMode) {
        GS.cameraMode = cameraMode;
    }
    
    public static Camera getCamera(){
        return camera;
    }

    public static CameraController getCameraController() {
        return cameraController;
    }
}
