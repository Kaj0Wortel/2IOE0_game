
package src.tools.event;


// Own imports
import src.tools.MultiTool;


// XInput imports
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Controller;


/**
 * 
 */
public class ControllerKey
        extends Key {
    
    // Main buttons.
    final public Identifier PS2_TRIANGLE = Button._0;
    final public Identifier PS2_CIRCLE = Button._1;
    final public Identifier PS2_CROSS = Button._2;
    final public Identifier PS2_SQUARE = Button._3;
    
    // D-pad.
    final public Identifier PS2_DPAD = Axis.POV;
    
    // Shoulder + joystick buttons.
    final public Identifier PS2_L1 = Button._6;
    final public Identifier PS2_L2 = Button._4;
    final public Identifier PS2_L3 = Button._10;
    final public Identifier PS2_R1 = Button._7;
    final public Identifier PS2_R2 = Button._5;
    final public Identifier PS2_R3 = Button._11;
    
    // Joystick axis.
    final public Identifier PS2_LEFT_JOYS_LEFT_RIGHT = Axis.X;
    final public Identifier PS2_LEFT_JOYS_UP_DOWN = Axis.Y;
    final public Identifier PS2_RIGHT_JOYS_LEFT_RIGHT = Axis.RZ;
    final public Identifier PS2_RIGHT_JOYS_UP_DOWN = Axis.Z;
    
    
    // Menu buttons
    final public Identifier PS2_SELECT = Button._8;
    final public Identifier PS2_START = Button._9;
    
    
    
    final private static float SENSITIVITY = 0.1f;
    
    final private Controller controller;
    final private Component comp;
    final private float value;
    
    
    public ControllerKey(ControllerKey key) {
        super((Key) key);
        this.controller = key.controller;
        this.comp = key.comp;
        this.value = key.value;
    }
    
    
    /**
     * Constructor.
     * 
     * @param deviceID the id of the device.
     * @param comp the component of the button this key denotes.
     * @param value the value of the button.
     */public ControllerKey(Controller controller, Component comp, float value) {
        super(true, -1, DEFAULT_MASK,
                -SENSITIVITY < value && value > SENSITIVITY);
        this.controller = controller;
        this.comp = comp;
        this.value = value;
    }
    
    
    /**
     * @return the controller this event was fired from.
     */
    public Controller getController() {
        return controller;
    }
    
    /**
     * @return the button this key represents.
     */
    public Component getComponent() {
        return comp;
    }
    
    /**
     * @return the tilt of the button.
     */
    public float getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ControllerKey) {
            ControllerKey key = (ControllerKey) obj;
            return controller.equals(key.controller) &&
                    comp.getIdentifier().equals(key.comp.getIdentifier());
            
        } else if (obj instanceof Identifier) {
            return obj.equals(comp.getIdentifier());
            
        } else if (obj instanceof Controller) {
            return obj.equals(controller);
            
        } else if (obj instanceof Component) {
            return obj.equals(comp);
        }
        
        return false;
    }
    
    /**
     * @return {@code true} if the joystick or D-pad is pulled up.
     */
    public boolean isUp() {
        return value < -SENSITIVITY;
    }
    
    /**
     * @return {@code true} if the joystick or D-pad is pulled to the left.
     */
    public boolean isLeft() {
        if (comp.getIdentifier() == PS2_DPAD) {
            return value == 1; // todo
            
        } else {
            return value < -SENSITIVITY;
        }
    }
    
    public boolean isRightOrDown() {
        return value > SENSITIVITY;
    }
    
    public boolean isCenter() {
        return false; // todo
        //return !isLeftOrUp() && !isRightOrDown();
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(super.hashCode(),
                comp.getIdentifier().hashCode());
    }
    
    @Override
    public ControllerKey clone() {
        return new ControllerKey(this);
    }
    
    
}
