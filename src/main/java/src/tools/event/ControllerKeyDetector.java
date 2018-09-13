
package src.tools.event;


// Own imports
import src.tools.log.Logger;
import java.awt.Component;


// Java imports


// XInput imports
import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.XInputDevice14;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.ivan.xinput.listener.XInputDeviceListener;
import static com.ivan.xinput.natives.XInputConstants.MAX_PLAYERS;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
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
    
    final protected boolean[] connected = new boolean[MAX_PLAYERS];
    
    
    public ControllerKeyDetector() {
        super();
        init();
    }
    
    public ControllerKeyDetector(Component comp) {
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
     * @param deviceID the device to check.
     * @return {@code true} if the device denoted by the given id is connected.
     */
    public boolean isConnected(int deviceID) {
        return connected[deviceID];
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
