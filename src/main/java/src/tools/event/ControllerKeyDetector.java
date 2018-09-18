
package src.tools.event;


// Java imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

// JInput imports
import java.util.Set;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;


// Own imports
import src.tools.log.Logger;
import net.java.games.input.ContrlEnv;
import src.Locker;
import static src.tools.event.ControllerKey.COMP_MODE_COPY_EQUALS;
import static src.tools.event.ControllerKey.DEFAULT_GET_COMP_MODE;
import static src.tools.event.ControllerKey.DEFAULT_REPLACE_COMP_MODE;
import src.tools.update.TimerTool;


/**
 * A controller key detector class that keeps track of the current attached
 * controllers, including the keyboard and mouse presses.
 */
public class ControllerKeyDetector {
    
    // Update interval (in ms);
    final private static long INTERVAL = 50L;
    
    protected Set<ControllerKey> prevState = new HashSet<>();
    
    
    private boolean initialized = false;
    private TimerTool updater;
    
    final public Map<String, Controller[]> connected
            = new HashMap<>();
    
    /**
     * Constructor.
     * Also starts the device refresh thread in {@link ContrlEnv} and the
     * device polling thread.
     */
    public ControllerKeyDetector() {
        super();
        init();
    }
    
    /**
     * Initializes the controller environment.
     */
    private void init() {
        Locker.add(this);
        ContrlEnv ce = new ContrlEnv();
        
        // Call this function first to ensure that all events 
        // for new controllers are gone. Then add the listeners and
        // add the returned controllers manually. Otherwise some
        // controllers might be ignored.
        Controller[] controllers = ce.getControllers();
        ce.addControllerListener(new ControllerListener() {
            @Override
            public void controllerAdded(ControllerEvent e) {
                addController(e.getController());
            }
            
            @Override
            public void controllerRemoved(ControllerEvent e) {
                removeController(e.getController());
            }
        });
        
        // Add all controllers that were here initially.
        for (Controller c : controllers) {
            addController(c);
        }
        
        // tmp initialization (static generation).
        ControllerKey.tmp();
        
        // Create timer.
        updater = new TimerTool(INTERVAL, INTERVAL, () -> {
            // The update thread should have minimal priority.
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            update();
        });
        updater.start();
        
        // Set to true if fully initialized.
        initialized = true;
    }
    
    /**
     * Startes the update timer.
     */
    public void start() {
        updater.start();
    }
    
    /**
     * Pauses the update timer.
     */
    public void pause() {
        updater.pause();
    }
    
    /**
     * Cancels the update timer.
     */
    public void cancel() {
        updater.cancel();
    }
    
    /**
     * Adds the given controller to the connected array.
     * 
     * @param controller the controller to add.
     */
    private void addController(Controller controller) {
        Locker.lock(this);
        try {
            String name = controller.toString();
            Controller[] sameNameArr = connected.get(name);
            if (sameNameArr == null) {
                connected.put(name, new Controller[] {controller});
                return;
            }
            
            // Check if there was an empty position that can be filled in.
            for (int i = 0; i < sameNameArr.length; i++) {
                if (sameNameArr[i] == null) {
                    sameNameArr[i] = controller;
                    return;
                }
            }
            
            // If there was no empty position, incease the length of the
            // array by 1 and put the controller in the last element.
            Controller[] newArr = Arrays.copyOf(sameNameArr,
                    sameNameArr.length + 1);
            newArr[sameNameArr.length] = controller;
            connected.put(name, newArr);
            
        } finally {
            Locker.unlock(this);
        }
    }
    
    /**
     * Removes the given controller from the connected array.
     * Leaves a {@code null} behind at the previous location to prevent
     * controller swapping after a disconnected controller.
     * 
     * @param controller 
     */
    private void removeController(Controller controller) {
        Locker.lock(this);
        try {
            String name = controller.toString();
            Controller[] sameNameArr = connected.get(name);
            if (sameNameArr == null) {
                Logger.write(new Object[] {
                    "Tried to remove controller:",
                    controller,
                    "But the controller wasn't registered (1)!"
                }, Logger.Type.WARNING);
                return;
            }
            
            for (int i = 0; i < sameNameArr.length; i++) {
                if (ContrlEnv.compareController(sameNameArr[i], controller)) {
                    sameNameArr[i] = null;
                    return;
                }
            }
            
            Logger.write(new Object[] {
                "Tried to remove controller:",
                controller,
                "But the controller wasn't registered (2)!"
            }, Logger.Type.WARNING);
            
        } finally {
            Locker.unlock(this);
        }
    }
    
    
    
    
    
    /**
     * Gets the id of the given controller.
     * The id's are needed to make a distiction between different
     * controllers with the same name.
     * 
     * @param controller the controller to get the id for.
     * @return the id of the given controller.
     */
    public int getControllerID(Controller controller) {
        Locker.lock(this);
        try {
            String name = controller.toString();
            Controller[] sameNameArr = connected.get(name);
            if (sameNameArr == null) {
                Logger.write(new Object[] {
                    "Tried to convert controller to string:",
                    controller,
                    "but the controller wasn't registered (1)!"
                }, Logger.Type.WARNING);
                return -1;
            }
            
            for (int i = 0; i < sameNameArr.length; i++) {
                if (ContrlEnv.compareController(
                        sameNameArr[i], controller)) {
                    return i;
                }
            }
            
            Logger.write(new Object[] {
                "Tried to convert controller to string:",
                controller,
                "But the controller wasn't registered (2)!"
            }, Logger.Type.WARNING);
            
        } finally {
            Locker.unlock(this);
        }
        
        return -1;
    }
    
    /**
     * @param controller the controller to process.
     * @return the save string of the given controller.
     *     {@code null} if the controller was not available.
     *//*
    public String controllerToString(Controller controller) {
        if (controller instanceof GeneratedController) {
            return ((GeneratedController) controller).getKey();
        }
        
        Locker.lock(this);
        try {
            String name = controller.toString();
            Controller[] sameNameArr = connected.get(name);
            if (sameNameArr == null) {
                Logger.write(new Object[] {
                    "Tried to convert controller to string:",
                    controller,
                    "but the controller wasn't registered (1)!"
                }, Logger.Type.WARNING);
                return null;
            }
            
            for (int i = 0; i < sameNameArr.length; i++) {
                if (ContrlEnv.compareController(
                        sameNameArr[i], controller)) {
                    return name + "-" + i;
                }
            }
            
            Logger.write(new Object[] {
                "Tried to convert controller to string:",
                controller,
                "But the controller wasn't registered (2)!"
            }, Logger.Type.WARNING);
            
        } finally {
            Locker.unlock(this);
        }
        
        return null;
    }
    /**/
    /**
     * @param str the save string to parse.
     * @return a controller denoted by the given save string.
     *     {@code null} if the denoted controller is disconnected.
     */
    public Controller getControllerFromString(String str) {
        String name = null;
        int id = -1;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '-') {
                name = str.substring(0, i);
                try {
                    id = Integer.parseInt(str.substring(i + 1));
                    
                } catch (NumberFormatException e) {
                    return null;
                }
                
                break;
            }
        }
        
        if (name == null || id < 0) return null;
        
        Controller[] controllers = connected.get(name);
        if (controllers == null || controllers.length < id) return null;
        else return controllers[id];
    }
    
    /**
     * The update function.
     */
    public void update() {
        // If not yet initialized, simply return.
        if (!initialized) return;
        if (!(ControllerEnvironment.getDefaultEnvironment()
                instanceof ContrlEnv)) return;
        
        ContrlEnv contrlEnv = (ContrlEnv) ControllerEnvironment
                .getDefaultEnvironment();
        
        Set<ControllerKey> newState = new HashSet<>();
        
        Locker.lock(this);
        try {
            for (Controller controller : contrlEnv.getControllers()) {
                if (!controller.poll()) continue;
                int id = getControllerID(controller);

                ControllerKey.setCompMode(DEFAULT_REPLACE_COMP_MODE);
                for (Component comp : controller.getComponents()) {
                    ControllerKey key = new ControllerKey(controller, id, comp,
                            comp.getPollData());
                    newState.add(key);
                    //if (key.getValue() == 1.0f) System.out.println(key);
                }
            }
        } finally {
            Locker.unlock(this);
        }
        
        Locker.lock(ControllerKey.class);
        try {
            prevState = newState;
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
        
        /*
        // Create an event object for the underlying plugin to populate.
        Event event = new Event();
        
        Locker.lock(ControllerKey.class);
        try {
            ControllerKey.setCompMode(DEFAULT_REPLACE_COMP_MODE);
            
            for (Controller controller : ControllerEnvironment
                    .getDefaultEnvironment()
                    .getControllers()) {
                // Poll and ignore disable and/or invallid controllers.
                if (!controller.poll()) continue;

                // TMP ignore all other events.
                //if (!"Twin USB Joystick".equals(controller.toString())) continue;
                
                // Process the events.
                EventQueue queue = controller.getEventQueue();
                while (queue.getNextEvent(event)) {
                    ControllerKey key = new ControllerKey(controller,
                            event.getComponent(), event.getValue());
                    // Remove earlier added keys that are now invallid.
                    keysCurPressed.remove(key);
                    keysPressedSinceLastUpdate.remove(key);
                    keysCurPressed.add(key);
                    keysPressedSinceLastUpdate.add(key);
                }
            }
            
            // Update the sets.
            super.update();
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }/**/
    }
    
    /**
     * @return all keys that were pressed at least once between the two
     *     last updates. Returned set should not be modified externally.
     * 
     * Note:
     * Unsafe return on purpose for speedup.
     * Do not modify the returned set!
     */
    public Set<ControllerKey> getKeysPressed() {
        return prevState;
    }
    
    /**
     * Checks if the key was pressed.
     * 
     * @param key key-value to check for.
     * @return true iff the given key was pressed between the two last updates.
     */
    public boolean wasPressed(ControllerKey key) {
        return wasPressed(key, DEFAULT_GET_COMP_MODE);
    }
    
    public boolean wasPressed(ControllerKey key, int compMode) {
        Locker.lock(ControllerKey.class);
        try {
            ControllerKey.setCompMode(compMode);
            return wasPressed(key);
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
    }
    
    /**
     * Checks if any of the keys in the list were pressed.
     * 
     * @param keys the keys to check.
     * @param compMode the comparison mode.
     * @return {@code true} if at least one key in the list were pressed.
     */
    public boolean werePressed(List<ControllerKey> keys) {
        return werePressed(keys, DEFAULT_GET_COMP_MODE);
    }
    
    private boolean werePressed(List<ControllerKey> keys, int compMode) {
        Locker.lock(ControllerKey.class);
        try {
            ControllerKey.setCompMode(compMode);
            for (ControllerKey key : keys) {
                if (prevState.contains(key)) {
                    return true;
                }
            }
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
        
        return false;
    }
    
    /**
     * Gets the pressed keys from the given list.
     * 
     * @param keys
     * @return a list containing all controller keys that were pressed.
     *     Note that the value from {@link ControllerKey#getLastEqualCompareKey()}
     *     will be used instead of the given key.
     */
    public List<ControllerKey> getPressedFrom(List<ControllerKey> keys) {
        return getPressedFrom(keys, DEFAULT_GET_COMP_MODE |
                COMP_MODE_COPY_EQUALS);
    }
    
    private List<ControllerKey> getPressedFrom(List<ControllerKey> keys,
            int compMode) {
        List<ControllerKey> list = new ArrayList<>();
        Locker.lock(ControllerKey.class);
        try {
            ControllerKey.setCompMode(compMode);
            for (ControllerKey key : keys) {
                if (prevState.contains(key)) {
                    list.add(key.getLastEqualCompareKey());
                }
            }
            
        } finally {
            Locker.unlock(ControllerKey.class);
        }
        
        return list;
    }
    
    
}
