
package src;


// Jogamp imports
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Vector3f;


// Own imports
import src.Assets.GUI;
import src.Assets.Light;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.Assets.instance.Item;
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
import static src.tools.event.ControllerKey.DEFAULT_GET_COMP_MODE;
import src.tools.event.ControllerKeyDetector;
import src.tools.event.Key;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.event.keyAction.CarKeyAction;
import src.tools.event.keyAction.KeyAction;
import src.tools.event.keyAction.PlayerKeyAction;
import src.tools.font.FontLoader;
import src.tools.io.BufferedReaderPlus;
import static src.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CONFIG;
import src.tools.io.ImageManager;
import src.tools.log.FileLogger;
import src.tools.log.Logger;
import src.tools.log.MultiLogger;
import src.tools.log.ScreenLogger;
import src.tools.log.ThreadLogger;
import src.tools.update.Updater;


// Java imports
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.LogManager;


// JInput imports
import net.java.games.input.ContrlEnv;
import net.java.games.input.ControllerEnvironment;
import src.gui.ScaleCanvas;


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
    
    /** The amount of user controlled players. */
    final public static int MAX_PLAYERS = 1;
    
    /** Random number generator. */
    final public static Random R = new Random();
    
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
    final private static List<Item> items = new ArrayList<>();
    
    
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
    public static GLCanvas canvas;
    private static FPSAnimator animator;
    private static Track raceTrack;
    private static Skybox skybox;
    
    public static List<Car> cars = new ArrayList<>();
    public static Car player;
    
    public static int WIDTH = 1080;
    public static int HEIGHT = 720;
    
    
    
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
        // Set the default font.
        FontLoader.setDefaultFont(FontLoader.getLocalFont("source-sans-pro"
                + GS.FS + "SourceSansPro-Black.ttf").deriveFont(16F));
        
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
                    + "now been disabled!", Logger.Type.INFO);
        }
        
        registerImageSheets();
        reloadKeyMap();
        
        GS.keyDet = new ControllerKeyDetector();
        GLProfile.initSingleton();
        createGUI();
    }
    
    /**
     * Function to start running the actual game.
     */
    public static void startRendering() {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities cap = new GLCapabilities(profile);
        canvas = new GLCanvas(cap);
        GS.mainPanel.showSwitchPanel(false);
        GS.mainPanel.add(canvas);
        
        grid = new Grid(0f, 0f, -10_000f, 20f, 20f, 20_000f);

        animator = new FPSAnimator(canvas, 60, true);

        camera = new Camera(new Vector3f(0, 5, 20), 0, 0, 0);
        cameraController = new CameraController(camera);
        Locker.add(cameraController);

        simulator = new Simulator();
        renderer = new Renderer(simulator, WIDTH, HEIGHT);

        canvas.addGLEventListener(renderer);
        canvas.setSize(WIDTH, HEIGHT);

        animator.start();
        renderer.cleanup();
        
        Updater.start();
    }
    
    /**
     * Registers all images that are neede in the application.
     */
    private static void registerImageSheets() {
        Logger.write(new String[] {
            "",
            "======= BEGIN REGISTERING IMAGE SHEETS ======="
        }, Logger.Type.INFO);
        
        ImageManager.registerSheet("game_icon.png", GS.FRAME_ICON, -1, -1);
        
        final String GUI = "gui" + GS.FS;
        
        ImageManager.registerSheet(GUI + "window.png", "CORNERS",
                new Rectangle[][] {
                    new Rectangle[] {new Rectangle( 0,  0, 7, 36)},
                    new Rectangle[] {new Rectangle(47,  0, 7, 36)},
                    new Rectangle[] {new Rectangle(47, 56, 7,  7)},
                    new Rectangle[] {new Rectangle( 0, 56, 7,  7)}
                }
        );
        
        ImageManager.registerSheet(GUI + "window.png", "BARS",
                new Rectangle[][] {
                    new Rectangle[] {new Rectangle( 7,  0, 40, 36)},
                    new Rectangle[] {new Rectangle(47, 36,  7, 20)},
                    new Rectangle[] {new Rectangle( 7, 56, 40,  7)},
                    new Rectangle[] {new Rectangle( 0, 36,  7, 20)}
                }
        );
        
        ImageManager.registerSheet(GUI + "buttons.png", "BUTTONS_EXIT",
                0, 0, 60, 20, 20, 20);
        ImageManager.registerSheet(GUI + "buttons.png", "BUTTONS_MINIMIZE",
                0, 20, 60, 40, 20, 20);
        ImageManager.registerSheet(GUI + "buttons.png", "BUTTONS_FULL_SCREEN",
                0, 40, 60, 60, 20, 20);
        ImageManager.registerSheet(GUI + "buttons.png", "BUTTONS_WINDOWED",
                0, 60, 60, 80, 20, 20);
        
        ImageManager.registerSheet(GUI + "window.png", "TABS",
                new Rectangle[][] {
                    new Rectangle[] {
                        new Rectangle( 0, 63,  7, 23),
                        new Rectangle( 7, 63,  2, 23),
                        new Rectangle( 9, 63,  7, 23)
                    },
                    new Rectangle[] {
                        new Rectangle(16, 63,  7, 23),
                        new Rectangle(23, 63,  2, 23),
                        new Rectangle(25, 63,  7, 23)
                    },
                    new Rectangle[] {
                        new Rectangle(32, 63,  7, 23),
                        new Rectangle(39, 63,  2, 23),
                        new Rectangle(41, 63,  7, 23)
                    },
                    new Rectangle[] {new Rectangle(48, 63,  6, 23)}
                }
        );
        
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_001.png",
                "BUTTON_001_CORNERS", 0, 0, 64, 32, 16, 16);
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_001.png",
                "BUTTON_001_BARS", 0, 32, 64, 64, 16, 16);
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_001.png",
                "BUTTON_001_BACK", 0, 48, 64, 64, 16, 16);
        
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_005.png",
                "ERROR_CORNERS", 0, 0, 64, 32, 16, 16);
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_005.png",
                "ERROR_BARS", 0, 32, 64, 64, 16, 16);
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_006.png",
                "DEFAULT_CORNERS", 0, 0, 64, 32, 16, 16);
        ImageManager.registerSheet(GUI + "IOBorder_img_TYPE_006.png",
                "DEFAULT_BARS", 0, 32, 64, 64, 16, 16);
        Logger.write(new String[] {
            "======= END REGISTERING IMAGE SHEETS =======",
            ""
        }, Logger.Type.INFO);
    }
    
    /**
     * Creates the GUI of the application.
     * Also adds all necessary listeners.
     */
    private static void createGUI() {
        GS.mainPanel = new MainPanel();

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
     * Sets the key map used for the global state.
     * @param newKeyMap 
     */
    public static void setKeyMap(Map<KeyAction, List<ControllerKey>> newKeyMap) {
        // Replace the key map.
        Locker.lock(ControllerKey.class);
        try {
            keyMap.clear();
            keyMap = newKeyMap;
            System.out.println("key map:" + keyMap);
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
    }
    
    /**
     * Reloads the key bindings map.
     */
    public static void reloadKeyMap() {
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
    
    /**
     * @return an iterator over all current keybindings.
     */
    public static Iterator<Map.Entry<KeyAction, List<ControllerKey>>>
            getKeyIterator() {
        return keyMap.entrySet().iterator();
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Generates a random integer.
     * 
     * @param down the lowest value that might be returned.
     * @param up the highest value that might be returned.
     * @return a random integer between {@code down} (inclusive) and
     *     {@code up} (inclusive).
     */
    public static int rani(int down, int up) {
        return GS.R.nextInt(up - down+1) + down;
    }
    
    /**
     * Generates a random number.
     * 
     * @param down the lowest value that might be returned.
     * @param up the highest value that might be returned.
     * @return a random float between {@code down} (inclusive) and
     *     {@code up} (inclusive).
     */
    public static float ranf(float down, float up) {
        return GS.R.nextFloat() * (up - down) - down;
    }
    
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

    public static List<Instance> getMaterialAssets(){
        return materialAssets;
    }

    public static List<Item> getItems() {
        return items;
    }

    public static void addItem(Item item){
        items.add(item);
    }
    
    public static void removeItem(Item item) {
        items.remove(item);
    }

    public static void addMaterialAsset(Instance asset) {
        materialAssets.add(asset);
    }

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
