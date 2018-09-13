
package src.tools.event;


// Own imports
import src.tools.log.Logger;


// Java imports


// XInput imports
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.XInputDevice14;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;


/**
 * A controller key detector class that keeps track of the current attached
 * controllers, including the current keypresses of the keyboard.
 * 
 * To download the JXInput lib:
 * <url>https://github.com/StrikerX3/JXInput/releases/tag/v0.8</url>
 */
public class ControllerKeyDetector
        extends KeyPressedDetector {
    
    public ControllerKeyDetector() {
        super();
        init();
    }
    
    public ControllerKeyDetector(java.awt.Component comp) {
        super(comp);
        init();
    }
    
    
    private void init() {
        // Check if XInput 1.3 is available.
        // Currently only using this one.
        if (XInputDevice.isAvailable()) {
            System.out.println("XInput 1.3 is available on this platform.");
        }

        // Check if XInput 1.4 is available.
        if (XInputDevice14.isAvailable()) {
            System.out.println("XInput 1.4 is available on this platform.");
        }
        
        // Get the DLL version.
        System.out.println("Native library version: "
                + XInputDevice.getLibraryVersion());
        System.out.println("-----------------");
        for (Controller controller : ControllerEnvironment
                .getDefaultEnvironment()
                .getControllers()) {
            System.out.println(controller);
            if (controller.getType() == Type.STICK) {
                if (!controller.poll()) continue;
                for (Component comp
                        : controller.getComponents()) {
                    System.out.println("    " + comp.toString());
                }
            }
            if (controller.toString().equals("Twin USB Joystick")) {
                System.out.println("    " + controller.getType());
            }
        }
        System.out.println("-----------------");
        /*
        try {
            Controller[] controllers = ControllerEnvironment
                    .getDefaultEnvironment()
                    .getControllers();
            //XInputDevice[] devices = XInputDevice.getAllDevices();
            
            for (int i = 0; i < controllers.length; i++) {
                // Poll devices.
                controllers[i].poll();
                
                // Get the controllers event queue.
                EventQueue queue = controllers[i].getEventQueue();
            }
            
            /*
            for (int i = 0; i < MAX_PLAYERS; i++) {
                final int num = i;
                devices[i].addListener(new XInputDeviceListener() {
                    @Override
                    public void connected() {
                        connected[num] = true;
                    }
                    
                    @Override
                    public void disconnected() {
                        connected[num] = false;
                    }
                    
                    @Override
                    public void buttonChanged(XInputButton button,
                            boolean pressed) {
                        Key key = new ControllerKey(num, button);
                        if (pressed) {
                            keysCurPressed.add(key);
                            keysPressedSinceLastUpdate.add(key);
                            
                        } else {
                            keysCurPressed.remove(key);
                        }
                    }
                });
            }
            
        } catch (XInputNotLoadedException e) {
            Logger.write(e);
        }*/
    }
    
    /**
     * Sets the vibration of the controller with the given device id.
     * 
     * @param deviceID the id of the device.
     * @param leftMotor the left motor speed. Must be between 0 and 65535.
     * @param rightMotor the right motor speed. Must be between 0 and 65535.
     * @return {@code false} if the motor was not connected.
     * @throws IllegalArgumentException if either motor speed values are
     *     not between 0 and 65535.
     * 
     * @see XInputDevice#setVibration(int, int)
     */
    public boolean setVibration(int deviceID, int leftMotor, int rightMotor)
            throws IllegalArgumentException {
        try {
            return XInputDevice.getDeviceFor(deviceID)
                    .setVibration(leftMotor, rightMotor);
            
        } catch (XInputNotLoadedException e) {
            Logger.write(e);
            return false;
        }
    }
    
    @Override
    public void update() {
        // Create an event object for the underlying plugin to populate.
        Event event = new Event();
        for (Controller controller : ControllerEnvironment
                .getDefaultEnvironment()
                .getControllers()) {
            // Poll and ignore disable and/or invallid controllers.
            if (!controller.poll()) continue;
            
            // tmp ignore all other events.
            if (!"Twin USB Joystick".equals(controller.toString())) continue;
            
            // Process the events.
            EventQueue queue = controller.getEventQueue();
            while (queue.getNextEvent(event)) {
                Component comp = event.getComponent();
                System.out.println(event);
                System.out.println(comp.getIdentifier().getClass());
                System.out.println(comp.getIdentifier().getName());
                System.out.println(comp.getIdentifier() == Axis.RZ);
                
            }
        }
        /*
        try {
            XInputDevice[] devices = XInputDevice.getAllDevices();
            for (int i = 0; i < MAX_PLAYERS; i++) {
                if (connected[i] = devices[i].poll()) {
                    System.out.println("Device " + i + " is connected!");
                    XInputAxes axes = devices[i].getComponents().getAxes();
                    for (XInputAxis axis : XInputAxis.values()) {
                        Key key = new ControllerKey(i, axis, axes.get(axis));
                        // Add to this set since this one will be moved
                        // to the history set after the update.
                        // Therefore no need to remove old key presses.
                        keysPressedSinceLastUpdate.add(key);
                    }
                }
            }
            
        } catch (XInputNotLoadedException e) {
            Logger.write(e);
        }
        */
        // Update the sets.
        super.update();
    }
    
    
}
