
package src.tools.event;


// Own imports
import src.tools.MultiTool;


// XInput imports
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;


/**
 * 
 */
public class ControllerKey
        extends Key {
    
    final private static float DEFAULT_TILT = 1.0f;
    
    final private int deviceID;
    final private XInputButton button;
    final private XInputAxis axis;
    private float tilt;
    
    
    public ControllerKey(ControllerKey key) {
        super((Key) key);
        this.deviceID = key.deviceID;
        this.button = key.button;
        this.axis = key.axis;
        this.tilt = key.tilt;
    }
    
    /**
     * Constructor.
     * 
     * @param deviceID the id of the device.
     * @param button the button this key denotes.
     * @param axis the axis of the controller.
     * @param tilt the amount the axis is tilted. Default is
     *     {@code #DEFAULT_TILT} for buttons that don't support tilt.
     */
    public ControllerKey(int deviceID, XInputButton button) {
        this(deviceID, button, null, DEFAULT_TILT);
    }
    
    public ControllerKey(int deviceID, XInputAxis axis, float tilt) {
        this(deviceID, null, axis, tilt);
    }
    
    private ControllerKey(int deviceID, XInputButton button, XInputAxis axis,
            float tilt) {
        super(true, -1, DEFAULT_MASK, DEFAULT_KEY_RELEASE);
        this.deviceID = deviceID;
        this.button = button;
        this.axis = axis;
        this.tilt = tilt;
    }
    
    
    /**
     * @return the id of the device this key was pressed on. Is always between
     *     0 and {@link com.ivan.xinput.natives.XInputConstants#MAX_PLAYERS}.
     */
    public int getDeviceID() {
        return deviceID;
    }
    
    /**
     * @return the tilt of the button.
     */
    public float getTilt() {
        return tilt;
    }
    
    public boolean supportsTilt() {
        return axis != null;
    }
    
    /**
     * @return the button this key represents.
     */
    public XInputButton getButton() {
        return button;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ControllerKey) {
            ControllerKey key = (ControllerKey) obj;
            // Do not compare the tilt here, as that value can change
            // anytime, resulting in possible un-removed keys.
            return deviceID == key.deviceID &&
                    button == key.button && 
                    axis == key.axis;
            
        } else if (obj instanceof XInputButton) {
            return obj == button;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(super.hashCode(), button.hashCode());
    }
    
    @Override
    public ControllerKey clone() {
        return new ControllerKey(this);
    }
    
    
}
