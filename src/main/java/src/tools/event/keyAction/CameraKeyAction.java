package src.tools.event.keyAction;

import src.tools.MultiTool;

public class CameraKeyAction
        extends KeyAction {
    
    public static enum MovementAction {
        FORWARD, BACKWARD, LEFT, RIGHT, OTHER;
    }
    
    final private CameraKeyAction.MovementAction action;
    
    
    public CameraKeyAction(CameraKeyAction.MovementAction action) {
        super();
        this.action = action;
    }
    
    public CameraKeyAction(int id, MovementAction action) {
        super(id);
        this.action = action;
    }
    
    public CameraKeyAction.MovementAction getAction() {
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
     * @throws IllegalArgumentException if the given data was invallid.
     */
    public static KeyAction createFromString(String[] data)
        throws IllegalArgumentException {
        int id = Integer.parseInt(data[0]);
        CameraKeyAction.MovementAction action = CameraKeyAction
                .MovementAction.valueOf(data[1]);
        System.out.println("ACTION:" + action);
        return new CameraKeyAction(id, action);
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(getID(), action);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CameraKeyAction)) return false;
        CameraKeyAction ka = (CameraKeyAction) obj;
        return this.action == ka.action &&
                this.getID() == ka.getID();
    }
    
    
}
