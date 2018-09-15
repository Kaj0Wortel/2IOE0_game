
package src.tools.event;


// Java imports
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// JInput imports
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;


// Own imports
import src.tools.log.Logger;
import net.java.games.input.ContrlEnv;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;


/**
 * A controller key detector class that keeps track of the current attached
 * controllers, including the keyboard and mouse presses.
 */
public class ControllerKeyDetector
        extends KeyPressedDetector {
    
    private boolean initialized = false;
    
    /**
     * The lock and connected map.
     * Note that {@link src.Locker) cannot be used since the has of 
     * {@link #connected} changes when items are added.
     */
    final public Lock lock = new ReentrantLock();
    final public Map<String, Controller[]> connected
            = new HashMap<>();
    
    /**
     * @see KeyPressedDetector#KeyPressedDetector()
     */
    public ControllerKeyDetector() {
        super();
        init();
    }
    
    /**
     * @see KeyPressedDetector#KeyPressedDetector(java.awt.Component)
     */
    public ControllerKeyDetector(java.awt.Component comp) {
        super(comp);
        init();
    }
    
    /**
     * Initializes the controller environment.
     */
    private void init() {
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
        
        // tmp initialization (static method generation.
        ControllerKey.tmp();
        
        // Set to true if fully initialized.
        initialized = true;
    }
    
    /**
     * Adds the given controller to the connected array.
     * 
     * @param controller the controller to add.
     */
    private void addController(Controller controller) {
        lock.lock();
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
            lock.unlock();
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
        lock.lock();
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
            lock.unlock();
        }
    }
    
    /**
     * @param controller the controller to process.
     * @return the save string of the given controller.
     *     {@code null} if the controller was not available.
     */
    public String controllerToString(Controller controller) {
        lock.lock();
        try {
            String name = controller.toString();
            Controller[] sameNameArr = connected.get(name);
            if (sameNameArr == null) {
                Logger.write(new Object[] {
                    "Tried to remove controller:",
                    controller,
                    "But the controller wasn't registered!"
                }, Logger.Type.ERROR);
                return null;
            }
            
            for (int i = 0; i < sameNameArr.length; i++) {
                if (sameNameArr[i] == controller) {
                    return name + "-" + i;
                }
            }
            
            Logger.write(new Object[] {
                "Tried to remove controller:",
                controller,
                "But the controller wasn't registered!"
            }, Logger.Type.ERROR);
            
        } finally {
            lock.unlock();
        }
        
        return null;
    }
    
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
                    Logger.write(e);
                    return null;
                }
                
                break;
            }
        }
        
        if (name == null || id < 0) return null;
        
        Controller[] controllers = connected.get(name);
        if (controllers.length < id) return null;
        else return controllers[id];
    }
    
    @Override
    public void update() {
        if (!initialized) {
            super.update();
            return;
        }
        
        // Create an event object for the underlying plugin to populate.
        Event event = new Event();
        
        for (Controller controller : ControllerEnvironment
                .getDefaultEnvironment()
                .getControllers()) {
            // Poll and ignore disable and/or invallid controllers.
            if (!controller.poll()) continue;
            
            // TMP ignore all other events.
            if (!"Twin USB Joystick".equals(controller.toString())) continue;
            
            // Process the events.
            EventQueue queue = controller.getEventQueue();
            while (queue.getNextEvent(event)) {
                Component comp = event.getComponent();
                //System.out.println(event);
                //System.out.println(comp.getIdentifier().getClass());
                //System.out.println(comp.getIdentifier().getName());
                //System.out.println(comp.getIdentifier() == Axis.RZ);
                
            }
        }
        
        // Update the sets.
        super.update();
    }
    
    
}
