
package src.tools.event;


// Own imports
import com.ivan.xinput.XInputButtons;
import com.ivan.xinput.XInputComponents;
import src.tools.log.Logger;


// Java imports


// JXInput imports
import static com.ivan.xinput.natives.XInputConstants.MAX_PLAYERS;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;


/**
 * A controller detector class that keeps track of the current attached
 * controllers and their actions.
 * 
 * To download the jxinput lib:
 * <url>https://github.com/StrikerX3/JXInput/releases/tag/v0.8</url>
 */
@Deprecated
public class ControllerDetector {
    private static XInputComponents[] prev
            = new XInputComponents[MAX_PLAYERS];
    
    private static XInputComponents[] cur
            = new XInputComponents[MAX_PLAYERS];
    
    // Private constructor for static singleton desing pattern.
    private ControllerDetector() { }
    
    static {
        update();
    }
    
    public static void reset() {
        try {
            XInputDevice[] devices = XInputDevice.getAllDevices();
            for (int i = 0; i < devices.length; i++) {
                XInputDevice device = devices[i];
                
                // Poll twice to clear the state.
                if (device.poll() && device.poll()) {
                    prev[i] = cur[i];
                    cur[i] = device.getComponents();
                    
                } else {
                    prev[i] = null;
                    cur[i] = null;
                }
            }
            
        } catch (XInputNotLoadedException e) {
            Logger.write(e);
        }
    }
    
    /**
     * Function to update the controller state.
     */
    public static void update() {
        try {
            XInputDevice[] devices = XInputDevice.getAllDevices();
            for (int i = 0; i < devices.length; i++) {
                XInputDevice device = devices[i];
                System.out.println(device.poll());
                
                if (device.poll()) {
                    prev[i] = cur[i];
                    cur[i] = device.getComponents();
                    
                } else {
                    prev[i] = null;
                    cur[i] = null;
                }
            }
            
        } catch (XInputNotLoadedException e) {
            Logger.write(e);
        }
    }
    
    public static boolean isConnected(int id) {
        return cur[id] == null;
    }
    
    /**
     * @param id the id of the controller.
     * @param button the button to check for.
     * @return {@code true} if the button is currently pressed.
     */
    public static boolean isPressed(int id, XInputButton button) {
        if (!isConnected(id))
            throw new IllegalStateException(
                    "Controller " + id + " was not connected!");
        return isPressed(cur[id].getButtons(), button);
    }
    
    /**
     * @param id the id of the controller.
     * @param button the button to check for.
     * @return {@code true} if the button was previously pressed.
     */
    public static boolean wasPressed(int id, XInputButton button) {
        if (!isConnected(id))
            throw new IllegalStateException(
                    "Controller " + id + " was not connected!");
        return isPressed(prev[id].getButtons(), button);
    }
    
    /**
     * @param buttons the input button to get the data from.
     * @param button the button to check for.
     * @return {@code true} if the given button was pressed on
     *     the given input button.
     */
    private static boolean isPressed(XInputButtons buttons,
            XInputButton button) {
        switch (button) {
            case A:
                return buttons.x;
            case B:
                return buttons.b;
            case X:
                return buttons.x;
            case Y:
                return buttons.y;
            case BACK:
                return buttons.back;
            case START:
                return buttons.start;
            case LEFT_SHOULDER:
                return !buttons.lShoulder;
            case RIGHT_SHOULDER:
                return buttons.rShoulder;
            case LEFT_THUMBSTICK:
                return buttons.lThumb;
            case RIGHT_THUMBSTICK:
                return buttons.rThumb;
            case DPAD_UP:
                return buttons.up;
            case DPAD_DOWN:
                return buttons.down;
            case DPAD_LEFT:
                return buttons.left;
            case DPAD_RIGHT:
                return buttons.right;
            case GUIDE_BUTTON:
                return buttons.guide;
            case UNKNOWN:
                return buttons.unknown;
        }
        return false;
    }
    
    
    public static void main(String[] args) {
        
    }
    
    
}
