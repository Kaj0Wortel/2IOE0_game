
package src.tools.event;


// Own imports
import net.java.games.input.ContrlEnv;
import src.tools.MultiTool;
import src.tools.log.Logger;
import src.Locker;


// JInput imports
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;


// Java imports
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 */
public class ControllerKey {
    static {
        Locker.add(ControllerKey.class);
    }
    
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    /** Comparison mode constants. */
    /**
     * Add this value to {@link #compMode} to include controller comparisons
     * when calling the {@link #equals(Object)} function.
     */
    final public static int COMP_MODE_CONTROLLER      = 0b0000_0001;
    
    /**
     * Add this value to {@link #compMode} to include indentifier comparisons
     * when calling the {@link #equals(Object)} function.
     */
    final public static int COMP_MODE_IDENTIFIER      = 0b0000_0010;
    
    /**
     * Add this value to {@link #compMode} to include value comparisons
     * when calling the {@link #equals(Object)} function.
     * Is ignored when used together with {@link #COMP_MODE_VALUE_IF_NEEDED}.
     */
    final public static int COMP_MODE_VALUE           = 0b0000_0100;
    
    /**
     * Add this value to {@link #compMode} to include value comparisons
     * when calling the {@link #equals(Object)} function if needed.
     * This means that the value will only be compared in a sensible way
     * when the identifier of {@code this} this is an axis or D-pad.
     * 
     * Moreover, checks if the values are equal for a D-pad, and checks
     * whether the axis is pushed in the right direction, ignoring the amount
     */
    final public static int COMP_MODE_VALUE_IF_NEEDED = 0b0000_1100;
    
    /**
     * Add this value to {@link #compMode} to store the last
     * controller key of which the {@code #equals(Object)} function
     * returned true.
     */
    final public static int COMP_MODE_COPY_EQUALS     = 0b0001_0000;
    
    /**
     * The default comparison mode for adding/replacing/updating keys.
     */
    final public static int DEFAULT_REPLACE_COMP_MODE = 
            COMP_MODE_CONTROLLER |
            COMP_MODE_IDENTIFIER;
    
    /**
     * The default comparison mode for matching keys.
     */
    final public static int DEFAULT_GET_COMP_MODE = 
            COMP_MODE_CONTROLLER |
            COMP_MODE_IDENTIFIER |
            COMP_MODE_VALUE_IF_NEEDED;
    
    /**
     * The default hash mode.
     */
    final private static int HASH_MODE =
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
    final public static Map<String, Type> TYPES = new HashMap<>();
    
    
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
        
        for (Field field : Type.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) &&
                    Type.class.isAssignableFrom(field.getDeclaringClass())) {
                try {
                    Type type = (Type) field.get(null);
                    TYPES.put(type.toString(), type);
                    
                } catch (IllegalArgumentException |
                        IllegalAccessException e) {
                    Logger.write(e);
                }
            }
        }
        System.out.println(TYPES);
    }
    
    // tmp
    public static void tmp() {};
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    private static int compMode = DEFAULT_REPLACE_COMP_MODE;
    
    final private Controller controller;
    final private Component comp;
    final private float value;
    final private int id;
    
    final private boolean useTypeOnly;
    final private int hashValue;
    
    private ControllerKey lastTrueCompare;
    
    
    /**
     * Clone constructor.
     * 
     * @param key the controller key to copy.
     */
    public ControllerKey(ControllerKey key) {
        this.controller = key.controller;
        this.id = key.id;
        this.comp = key.comp;
        this.value = key.value;
        this.hashValue = key.hashValue;
        this.useTypeOnly = false;
    }
    
    /**
     * Constructor.
     * 
     * @param deviceID the id of the device.
     * @param comp the component of the button this key denotes.
     * @param value the value of the component.
     * @param typeOnly whether to compare the controller only by type
     *     instead of by key-name.
     */
    public ControllerKey(Controller controller, int id, Component comp,
            float value) {
        this.controller = controller;
        this.id = id;
        this.comp = comp;
        this.value = value;
        this.useTypeOnly = false;
        this.hashValue = calcHashCode();
    }
    
    public ControllerKey(Controller controller, Component comp,
            float value, boolean typeOnly) {
        this.controller = controller;
        this.id = -1;
        this.comp = comp;
        this.value = value;
        this.useTypeOnly = typeOnly;
        this.hashValue = calcHashCode();
    }
    
    
    /**
     * @return the key that was equal to this key when the flag
     *     {@link #COMP_MODE_COPY_EQUALS} is set. More specifically,
     *     the last key {@code key} which was used in {@code this.equals(key)}
     *     and where the expression was {@code true}. If the expression was
     *     {@code false}, then return {@code null}.
     */
    public ControllerKey getLastEqualCompareKey() {
        return lastTrueCompare;
    }
    
    /**
     * Use this function to update the comparison mode of all
     * controller key objects.
     * 
     * @param newMode the new comparison mode.
     */
    public static void setCompMode(int newMode) {
        compMode = newMode;
    }
    
    /**
     * @return the current comparison mode.
     */
    public static int getCompMode() {
        return compMode;
    }
    
    /**
     * @return the controller this event was fired from.
     */
    public Controller getController() {
        return controller;
    }
    
    /**
     * @return the id of the controller.
     * 
     * The id's are needed to make a distiction between different
     * controllers with the same name.
     */
    public int getID() {
        return id;
    }
    
    /**
     * @return the component this key represents.
     */
    public Component getComponent() {
        return comp;
    }
    
    /**
     * @return the identifier this key represents.
     */
    public Identifier getIdentifier() {
        return comp.getIdentifier();
    }
    
    /**
     * @return the value of the button.
     */
    public float getValue() {
        return value;
    }
    
    /**
     * Compares {@code this} with {@code obj}.
     * 
     * It is advised to use the class lock before using this method:
     * {@code Locker.lock(ControllerKey.class)}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        Identifier ident = comp.getIdentifier();
        
        if (obj instanceof ControllerKey) {
            boolean copyIfTrue = (compMode & COMP_MODE_COPY_EQUALS) ==
                    COMP_MODE_COPY_EQUALS;
            if (copyIfTrue) lastTrueCompare = null;
            
            ControllerKey key = (ControllerKey) obj;
            
            if ((compMode & COMP_MODE_CONTROLLER) == COMP_MODE_CONTROLLER) {
                if (useTypeOnly || key.typeOnly()) {
                    if (!controller.getType().toString()
                            .equals(key.controller.getType().toString())) {
                        //System.out.println("not here 01");
                        return false;
                    }
                    
                } else {
                    if (id == -1 || key.getID() == -1 || id != key.getID()) {
                        //System.out.println("not here 02");
                        return false;
                    }
                    
                    if (!controller.getName().equals(key.controller.getName())) {
                        //System.out.println("not here 02.1");
                        return false;
                    }
                }
            }
            
            if ((compMode & COMP_MODE_IDENTIFIER) == COMP_MODE_IDENTIFIER) {
                if (!this.comp.getIdentifier().getName()
                        .equals(key.comp.getIdentifier().getName())) {
                    //System.out.println("not here 03");
                    return false;
                }
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
                        if (v1 == POV.CENTER || v2 == POV.CENTER) {
                            System.out.println("not here 04");
                            return false;
                        }
                        
                        // If both are multi-directional (and unequal),
                        // false by default.
                        if (v1 % 0.25f != 0 && v2 % 0.25f != 0) {
                            //System.out.println("not here 05");
                            return false;
                        }
                        
                        // If the values are more then 0.25f apart (assume
                        // looping around), then certainly false.
                        if (Math.abs(v1 - v2) > 0.25f) {
                            // Because {@code LEFT == 1.0f} and
                            // {@code UP_LEFT == 0.125f, and at least one
                            // is not multi-directional.
                            if (v1 != POV.LEFT && v2 != POV.LEFT) {
                                //System.out.println("not here 06");
                                return false;
                            }
                        }
                    }
                    
                } else if (ident instanceof Axis) {
                    if ((v1 < -JOYSTICK_SENS && !(v2 < -JOYSTICK_SENS)) ||
                            (v1 > JOYSTICK_SENS && !(v2 > JOYSTICK_SENS)) ||
                            (-JOYSTICK_SENS <= v1 && v1 <= JOYSTICK_SENS &&
                            !(-JOYSTICK_SENS <= v2 && JOYSTICK_SENS <= v2))) {
                        //System.out.println("not here 07");
                        return false;
                    }
                    
                } else if (ident instanceof Button ||
                        ident instanceof Identifier.Key) {
                    if (this.isPressed() != key.isPressed()) {
                        //System.out.println("not here 08");
                        return false;
                    }
                }
                
            } else if ((compMode & COMP_MODE_VALUE) == COMP_MODE_VALUE) {
                if (this.value != key.value) {
                    //System.out.println("not here 09");
                    return false;
                }
            }
            
            if (copyIfTrue) lastTrueCompare = key;
            return true;
            
        } else if (obj instanceof Identifier) {
            return obj.equals(ident);
            
        } else if (obj instanceof Controller) {
            if (useTypeOnly) {
                return ((Controller) obj).getType().toString()
                        .equals(controller.getType().toString());
                
            } else {
                return ContrlEnv
                        .compareController((Controller) obj, controller);
            }
            
        } else if (obj instanceof Component) {
            return obj.equals(comp);
        }
        
        return false;
    }
    
    /**
     * @return whether this controller should be compared by type only,
     *     instead of by key name.
     */
    public boolean typeOnly() {
        return useTypeOnly;
    }
    
    /**
     * Checks if the joystick is pushed to the left or upwards.
     * Function for {@link Axis} only, excluding {@link Axis#POV}.
     * 
     * @return {@code true} if the joystick is pushed to the left or upwards.
     */
    public boolean isJoyUpOrLeft() {
        return value < -JOYSTICK_SENS;
    }
    
    /**
     * Checks if the joystick is pushed to the right or downwards.
     * Function for {@link Axis} only, excluding {@link Axis#POV}.
     * 
     * @return {@code true} if the joystick is pushed to the right or downwards.
     */
    public boolean isJoyDownOrRight() {
        return value > JOYSTICK_SENS;
    }
    
    /**
     * Checks if the D-pad is pushed somewhere upwards.
     * Function for {@link Axis#POV only, excluding the other {@link Axis}'.
     * 
     * @return {@code true} if the joystick is pushed somewhere upwards.
     */
    public boolean isDPADUp() {
        return POV.UP_LEFT <= value && value <= POV.UP_RIGHT;
    }
    
    /**
     * Checks if the D-pad is pushed somewhere to the right.
     * Function for {@link Axis#POV only, excluding the other {@link Axis}'.
     * 
     * @return {@code true} if the joystick is pushed somewhere to the right.
     */
    public boolean isDPADRight() {
        return POV.UP_RIGHT <= value && value <= POV.DOWN_RIGHT;
    }
    
    /**
     * Checks if the D-pad is pushed somewhere downwards
     * Function for {@link Axis#POV only, excluding the other {@link Axis}'.
     * 
     * @return {@code true} if the joystick is pushed somewhere downwards.
     */
    public boolean isDPADDown() {
        return POV.DOWN_RIGHT <= value && value <= POV.DOWN_LEFT;
    }
    
    /**
     * Checks if the D-pad is pushed somewhere to the left
     * Function for {@link Axis#POV only, excluding the other {@link Axis}'.
     * 
     * @return {@code true} if the joystick is pushed somewhere to the left.
     */
    public boolean isDPADLeft() {
         // Note that this one is different compared to the others since
         // {@code DOWN_LEFT == 0.875f} and {@code UP_LEFT == 0.125f}.
        return value == POV.DOWN_LEFT ||
                value == POV.LEFT ||
                value == POV.UP_LEFT;
    }
    
    /**
     * Checks if the joystick is in the center.
     * Function for all {@link Axis}.
     * 
     * @return {@code true} if the joystick is in the center.
     */
    public boolean isCenter() {
        return -JOYSTICK_SENS <= value && value <= JOYSTICK_SENS;
    }
    
    /**
     * Checks if the button is pressed.
     * Function for {@link Identifier.Key} and {@link Identifier.Button} only.
     * 
     * @return {@code true} if the button was pressed. {@code false} otherwise.
     */
    public boolean isPressed() {
        return value > BUTTON_SENS;
    }
    
    @Override
    public int hashCode() {
        return hashValue;
    }
    
    /**
     * Calculates the hash code when creating the object.
     * 
     * @return the hash code.
     */
    @SuppressWarnings("IncompatibleBitwiseMaskOperation")
    public int calcHashCode() {
        Identifier ident = comp.getIdentifier();
        Object[] data = new Object[7];
        if ((HASH_MODE & COMP_MODE_CONTROLLER) == COMP_MODE_CONTROLLER) {
            data[0] = controller.toString();
        }
        
        if ((HASH_MODE & COMP_MODE_IDENTIFIER) == COMP_MODE_IDENTIFIER) {
            data[1] = ident.getName();
        }
        
        if ((HASH_MODE & COMP_MODE_VALUE_IF_NEEDED) ==
                COMP_MODE_VALUE_IF_NEEDED) {
            if (ident == Axis.POV) {
                data[2] = isDPADUp();
                data[3] = isDPADRight();
                data[4] = isDPADDown();
                data[5] = isDPADLeft();
                data[6] = isCenter();
                
            } else if (ident instanceof Axis) {
                data[2] = isJoyUpOrLeft();
                data[3] = isJoyDownOrRight();
                data[4] = isCenter();
                
            } else if (ident instanceof Button ||
                    ident instanceof Identifier.Key) {
                data[2] = isPressed();
            }
            
        } else if ((HASH_MODE & COMP_MODE_VALUE) == COMP_MODE_VALUE) {
            data[2] = value;
        }
        
        return MultiTool.calcHashCode(data);
    }
    
    @Override
    public ControllerKey clone() {
        return new ControllerKey(this);
    }
    
    @Override
    public String toString() {
        Identifier ident = comp.getIdentifier();
        System.out.println(controller.getName());
        if (useTypeOnly) {
            return getClass().getName() + "="
                    + "true,"
                    + controller.getType().toString() + ","
                    + (ident == null ? "Unknown" : ident.getName()) + ","
                    + value;
            
        } else {
            return getClass().getName() + "="
                    + "false,"
                    + id + ","
                    + controller.getName() + ","
                    + (ident == null ? "Unknown" : ident.getName()) + ","
                    + value;
        }
    }
    
    /**
     * Creates a key from the given data.
     * 
     * @param data the data needed to create the key.
     * @return a fresh key described by the data.
     * @throws IllegalArgumentException if the given data was invallid.
     */
    public static ControllerKey createFromString(String[] data)
            throws IllegalArgumentException {
        boolean typeOnly = Boolean.parseBoolean(data[0]);
        
        if (typeOnly) {
            Type type = TYPES.get(data[1]);
            Identifier ident = IDENTIFIERS.get(data[2]);
            if (ident == null) ident = Axis.UNKNOWN;
            float value = Float.parseFloat(data[3]);
            Controller cont = new GeneratedController("", type);
            Component comp = new GeneratedComponent(ident, value);
            
            return new ControllerKey(cont, comp, value, true);
            
        } else {
            int id = Integer.parseInt(data[1]);
            Controller cont = new GeneratedController(data[2]);
            Identifier ident = IDENTIFIERS.get(data[3]);
            if (ident == null) ident = Axis.UNKNOWN;
            float value = Float.parseFloat(data[4]);
            Component comp = new GeneratedComponent(ident, value);
            
            return new ControllerKey(cont, id, comp, value);
        }
    }
    
    
}
