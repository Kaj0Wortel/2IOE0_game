
package src.tools.event.keyAction;


import src.tools.MultiTool;
import src.tools.event.keyAction.action.PlayerMovementAction;


/**
 * 
 */
public class PlayerKeyAction
        extends KeyAction<PlayerMovementAction> {
    
    final private PlayerMovementAction action;


    public PlayerKeyAction(PlayerMovementAction action) {
        super();
        this.action = action;
    }

    public PlayerKeyAction(int id, PlayerMovementAction action) {
        super(id);
        this.action = action;
    }

    
    @Override
    public PlayerMovementAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return getClass().getName() + "="
                + getID() + ","
                + (action == null ? "null" : action.toString());
    }

    /**
     * Creates a new key action from the given data.
     *
     * @param data the data needed to create the key action.
     * @return a fresh key action described by the data.
     * @throws IllegalArgumentException if the given data was invalid.
     */
    public static KeyAction createFromString(String[] data)
        throws IllegalArgumentException {
        int id = Integer.parseInt(data[0]);
        PlayerMovementAction action = PlayerMovementAction.valueOf(data[1]);
        System.out.println("ACTION:" + action);
        return new PlayerKeyAction(id, action);
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(getID(), action);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlayerKeyAction)) return false;
        PlayerKeyAction ka = (PlayerKeyAction) obj;
        return this.action == ka.action &&
                this.getID() == ka.getID();
    }
    
    
}
