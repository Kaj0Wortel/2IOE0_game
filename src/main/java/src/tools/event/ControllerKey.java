
package src.tools.event;


// Own imports
import src.tools.MultiTool;
import src.tools.log.Logger;


// JInput imports
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;


// Java imports
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.java.games.input.Component.Identifier;
import net.java.games.input.ContrlEnv;
import src.GS;


/**
 * 
 */
public class ControllerKey
        extends Key {
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    /** Comparison mode constants. */
    /**
     * Add this value to {@link #compMode} to include controller comparisons
     * when calling the {@link #equals(Object)} function.
     */
    final public static int COMP_MODE_CONTROLLER      = 0b0001;
    
    /**
     * Add this value to {@link #compMode} to include indentifier comparisons
     * when calling the {@link #equals(Object)} function.
     */
    final public static int COMP_MODE_IDENTIFIER      = 0b0010;
    
    /**
     * Add this value to {@link #compMode} to include value comparisons
     * when calling the {@link #equals(Object)} function.
     * Is ignored when used together with {@link #COMP_MODE_VALUE_IF_NEEDED}.
     */
    final public static int COMP_MODE_VALUE           = 0b0100;
    
    /**
     * Add this value to {@link #compMode} to include value comparisons
     * when calling the {@link #equals(Object)} function if needed.
     * This means that the value will only be compared in a sensible way
     * when the identifier of {@code this} this is an axis or D-pad.
     * 
     * Moreover, checks if the values are equal for a D-pad, and checks
     * whether the axis is pushed in the right direction, ignoring the amount
     */
    final public static int COMP_MODE_VALUE_IF_NEEDED = 0b1100;
    
    /**
     * The default comparison mode. Use this to look up keys in a table
     * to detect whether to use them as input for a component.
     */
    final public static int DEFAULT_COMP_MODE = 
            COMP_MODE_CONTROLLER |
            COMP_MODE_IDENTIFIER |
            COMP_MODE_VALUE_IF_NEEDED;
    
    
    /** Identifier constants. */
    // Main buttons.
    final public static Identifier PS2_TRIANGLE = Button._0;
    final public static Identifier PS2_CIRCLE = Button._1;
    final public static Identifier PS2_CROSS = Button._2;
    final public static Identifier PS2_SQUARE = Button._3;
    
    // D-pad.
    final public static Identifier PS2_DPAD = Axis.POV;
    
    // Shoulder + joystick buttons.
    final public static Identifier PS2_L1 = Button._6;
    final public static Identifier PS2_L2 = Button._4;
    final public static Identifier PS2_L3 = Button._10;
    final public static Identifier PS2_R1 = Button._7;
    final public static Identifier PS2_R2 = Button._5;
    final public static Identifier PS2_R3 = Button._11;
    
    // Joystick axis.
    final public static Identifier PS2_LEFT_JOYS_LEFT_RIGHT = Axis.X;
    final public static Identifier PS2_LEFT_JOYS_UP_DOWN = Axis.Y;
    final public static Identifier PS2_RIGHT_JOYS_LEFT_RIGHT = Axis.RZ;
    final public static Identifier PS2_RIGHT_JOYS_UP_DOWN = Axis.Z;
    
    // Menu buttons
    final public static Identifier PS2_SELECT = Button._8;
    final public static Identifier PS2_START = Button._9;
    
    
    /** Sensitivity constants. */
    final private static float JOYSTICK_SENS = 0.1f;
    final private static float BUTTON_SENS = 0.5f;
    
    
    final public static Map<String, Identifier> IDENTIFIERS = new HashMap<>();
    
    
    /**-------------------------------------------------------------------------
     * Static initialisation.
     * -------------------------------------------------------------------------
     */
    static {
        for (Class<?> clazz : Identifier.class
                .getDeclaredClasses()) {
            if (!Identifier.class.isAssignableFrom(clazz)) continue;
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) &&
                        Identifier.class
                                .isAssignableFrom(field.getDeclaringClass())) {
                    try {
                        Identifier ident = (Identifier) field.get(null);
                        IDENTIFIERS.put(ident.toString(), ident);
                        
                    } catch (IllegalArgumentException |
                            IllegalAccessException e) {
                        Logger.write(e);
                    }
                }
            }
        }
        System.out.println(IDENTIFIERS);
    }
    
    // tmp
    public static void tmp() {};
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    public static int compMode = DEFAULT_COMP_MODE;
    
    final private Controller controller;
    final private Component comp;
    final private Identifier ident;
    final private float value;
    
    
    /**
     * Clone constructor.
     * 
     * @param key the controller key to copy.
     */
    public ControllerKey(ControllerKey key) {
        super((Key) key);
        this.controller = key.controller;
        this.comp = key.comp;
        this.ident = key.ident;
        this.value = key.value;
    }
    
    /**
     * Constructor.
     * 
     * @param deviceID the id of the device.
     * @param comp the component of the button this key denotes.
     * @param value the value of the component.
     */
    public ControllerKey(Controller controller, Component comp,
             float value) {
        super(true, -1, DEFAULT_MASK, value > BUTTON_SENS);
        this.controller = controller;
        this.comp = comp;
        this.ident = comp.getIdentifier();
        this.value = value;
    }
    
    private ControllerKey(Controller controller, Identifier ident,
            float value) {
        super(true, -1, DEFAULT_MASK, value > BUTTON_SENS);
        this.controller = controller;
        this.comp = null;
        this.ident = ident;
        this.value = value;
    }
    
    
    /**
     * @return the controller this event was fired from.
     */
    public Controller getController() {
        return controller;
    }
    
    /**
     * @return the component this key represents.
     */
    public Component getComponent() {
        return comp;
    }
    
    /**
     * @return the identifier of the button.
     */
    public Identifier getIdentifier() {
        return ident;
    }
    
    /**
     * @return the value of the button.
     */
    public float getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ControllerKey) {
            ControllerKey key = (ControllerKey) obj;
            
            if ((compMode & COMP_MODE_CONTROLLER) == COMP_MODE_CONTROLLER) {
                if (this.controller != key.controller)
                    return false;
            }
            
            if ((compMode & COMP_MODE_IDENTIFIER) == COMP_MODE_IDENTIFIER) {
                if (this.comp.getIdentifier() != key.comp.getIdentifier())
                    return false;
            }
            
            if ((compMode & COMP_MODE_VALUE_IF_NEEDED) ==
                    COMP_MODE_VALUE_IF_NEEDED) {
                float v1 = this.value;
                float v2 = key.value;
                if (ident == Axis.POV) {
                    // If both values are equal, then certainly true.
                    if (v1 != v2) {
                        // If either of them is in the center,
                        // then certainly false.
                        if (v1 == POV.CENTER || v2 == POV.CENTER) return false;
                        
                        // If both are multi-directional (and unequal),
                        // false by default.
                        if (v1 % 0.25f != 0 && v2 % 0.25f != 0) return false;
                        
                        // If the values are more then 0.25f apart (assume
                        // looping around), then certainly false.
                        if (Math.abs(v1 - v2) > 0.25f) {
                            // Because {@code LEFT == 1.0f} and
                            // {@code UP_LEFT == 0.125f, and at least one
                            // is not multi-directional.
                            if (v1 != POV.LEFT && v2 != POV.LEFT) {
                                return false;
                            }
                        }
                    }
                    
                } else if (ident instanceof Axis) {
                    if ((v1 < -JOYSTICK_SENS && !(v2 < -JOYSTICK_SENS)) ||
                            (v1 > JOYSTICK_SENS && !(v2 > JOYSTICK_SENS)) ||
                            (-JOYSTICK_SENS <= v1 && v1 <= JOYSTICK_SENS &&
                            !(-JOYSTICK_SENS <= v2 && JOYSTICK_SENS <= v2))) {
                        return false;
                    }
                    
                } else if (ident instanceof Button ||
                        ident instanceof Identifier.Key) {
                    if (v1 != v2) return false;
                }
                
            } else if ((compMode & COMP_MODE_VALUE) == COMP_MODE_VALUE) {
                if (this.value != key.value)
                    return false;
            }
            
            return true;
            
        } else if (obj instanceof Identifier) {
            return obj.equals(ident);
            
        } else if (obj instanceof Controller) {
            return ContrlEnv.compareController((Controller) obj, controller);
            
        } else if (obj instanceof Component) {
            return obj.equals(comp);
        }
        
        return false;
    }
    
    /**
     * Checks if the joystick is pushed to the left or upwards.
     * Function for {@link Axis} only, excluding {@link Axis#POV}.
     * 
     * @return {@code true} if the joystick is pushed to the left or upwards.
     */
    public boolean isLeftOrUp() {
        return value < -JOYSTICK_SENS;
    }
    
    
    /**
     * Checks if the joystick is pushed to the right or downwards.
     * Function for {@link Axis} only, excluding {@link Axis#POV}.
     * 
     * @return {@code true} if the joystick is pushed to the right or downwards.
     */
    public boolean isDownOrRight() {
        return value > JOYSTICK_SENS;
    }
    
    /**
     * Checks if the joystick is in the center.
     * Function for {@link Axis} only, excluding {@link Axis#POV}.
     * 
     * @return {@code true} if the joystick is in the center.
     */
    public boolean isCenter() {
        return -JOYSTICK_SENS <= value && value <= JOYSTICK_SENS;
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
    
    @Override
    public String toString() {
        return getClass().toString() + "="
                + controller.getName() + ","
                + GS.keyDet.controllerToString(controller) + ","
                + (ident == null ? "null" : ident.toString()) + ","
                + value;
    }
    
    /**
     * Creates a key from the given data.
     * 
     * @param name the name of the key.
     * @param data the data needed to create the key.
     * @return a fresh key.
     * @throws IllegalArgumentException if the given data was invallid.
     */
    public static Key createFromString(String name, String[] data)
            throws IllegalArgumentException {
        if (ControllerKey.class.toString().equals(name)) {
            try {
                Controller cont = new GeneratedController(data[0], data[1]);
                Identifier ident = IDENTIFIERS.get(data[3]);
                float value = Float.parseFloat(data[4]);
                
                return new ControllerKey(cont, ident, value);
                
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
            
        } else {
            Key.createFromString(name, data);
        }
        
        return null;
    }
    
}
