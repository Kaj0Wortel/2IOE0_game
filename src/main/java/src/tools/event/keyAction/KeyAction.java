
package src.tools.event.keyAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import src.GS;
import src.tools.event.keyAction.action.CameraMovementAction;
import src.tools.event.keyAction.action.CarMovementAction;
import src.tools.event.keyAction.action.PlayerMovementAction;
import src.tools.log.Logger;


/**
 * 
 */
public abstract class KeyAction<V extends Enum<V>> {
    final private int id;
    
    
    final private static List<Enum> ACTION_LIST;
    static {
        ACTION_LIST = new ArrayList<>();
        
        File[] files = new File(GS.WORKING_DIR + "tools" + GS.FS +
                "event" + GS.FS + "keyAction" + GS.FS + "action").listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.endsWith(".java")) continue;
            
            try {
                // Remove the ".java" part of the file.
                String className = fileName.substring(0, fileName.length() - 5);
                // Get the class.
                Class<?> c = Class.forName(KeyAction.class.getPackage()
                        .getName() + ".action." + className);
                // If it is an enum, add it to the list.
                if (Enum.class.isAssignableFrom(c)) {
                    Enum[] enums = ((Class<Enum>) c).getEnumConstants();
                    if (enums != null) ACTION_LIST.addAll(Arrays.asList(enums));
                }
                
            } catch (ClassNotFoundException e) {
                Logger.write(e);
            }
        }
    }
    
    /**
     * @return all available enum action.
     * 
     * Warning:
     * Do NOT modify this list!
     */
    public static List<Enum> getAllActions() {
        return ACTION_LIST;
    }
    
    /**
     * @param id id of the action.
     * @param e enum representing the action.
     * @return a new {@code KeyAction} instance, initialized with the given
     *     values.
     */
    public static KeyAction createKeyAction(int id, Enum e) {
        if (e instanceof CameraMovementAction) {
            return new CameraKeyAction(id, (CameraMovementAction) e);
            
        } else if (e instanceof CameraMovementAction) {
            return new CarKeyAction(id, (CarMovementAction) e);
            
        } else if (e instanceof CameraMovementAction) {
            return new PlayerKeyAction(id, (PlayerMovementAction) e);
            
        } else {
            return null;
        }
    }
    
    
    public KeyAction() {
        this(-1);
    }
    
    public KeyAction(int id) {
        this.id = id;
    }
    
    /**
     * @return the id of the key action.
     */
    public int getID() {
        return id;
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should produce the save string of {@code this}.
     */
    @Override
    public abstract String toString();
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should be such that a new instance of
     * the same class as {@code this} with the same internal values will
     * produce the same hash.
     * 
     * This means for for any subclass the following must hold:
     * {@code (new ExtendedKeyAction([some data])).hashCode()
     *     == (new ExtendedKeyAction([some data])).hashCode()},
     * where in both case [some data] is equal.
     */
    @Override
    public abstract int hashCode();
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should be such that a new instance of
     * the same class as {@code this} with the same internal values will
     * be equal to {@code this}.
     * 
     * This means for for any subclass the following must hold:
     * {@code (new ExtendedKeyAction([some data]))
     *     .equals(new ExtendedKeyAction([some data])) == true},
     * where in both case [some data] is equal.
     */
    @Override
    public abstract boolean equals(Object obj);
    
    /**
     * @return the action of this action class.
     */
    public abstract V getAction();
    
    
}
