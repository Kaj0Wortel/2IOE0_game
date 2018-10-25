package src.Controllers;

import java.util.List;
import src.Assets.instance.Car;
import src.GS;
import src.Physics.PStructAction;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.PlayerKeyAction;
import src.tools.event.keyAction.action.PlayerMovementAction;

public class PlayerController
        extends Controller<Car> {
    
    final private PlayerKeyAction[] playerActions;
    private static boolean playBoing;
    
    private boolean prevCamChanged = false;

    public PlayerController(Car player, int id) {
        super(player);
        
        PlayerMovementAction[] values = PlayerMovementAction.values();
        playerActions = new PlayerKeyAction[values.length];
        for (int i = 0; i < values.length; i++) {
            playerActions[i] = new PlayerKeyAction(id, values[i]);
        }
    }
    
    @Override
    public PStructAction controlUpdate(long dt) {
        float turn = 0;
        float acc = 0;
        float vertV = 0;
        boolean throwItem = false;
        boolean camChanged = false;
        
        for (PlayerKeyAction action : playerActions) {
            List<ControllerKey> keys = GS.getKeys(action);
            if (keys == null) continue;
            
            List<ControllerKey> pressed = GS.keyDet.getPressedFrom(keys);
            if (!pressed.isEmpty()) {
                if (action.getAction() == PlayerMovementAction.LEFT) {
                    turn += Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerMovementAction.RIGHT) {
                    turn -= Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerMovementAction.FORWARD) {
                    acc += Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerMovementAction.BACKWARD) {
                    acc -= Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerMovementAction.JUMP) {
                    vertV = 8;
                }
                if (action.getAction() == PlayerMovementAction.THROW_ITEM) {
                    throwItem = true;
                }
                if (action.getAction() == PlayerMovementAction.CHANGE_CAM) {
                    if (!prevCamChanged) GS.getCam(instance).cycleNextCameraMode();
                    camChanged = true;
                }
                if (action.getAction() == PlayerMovementAction.DRIFT) {
                    // Your stuff here Daan
                }
            }
        }
        prevCamChanged = camChanged;
        
        // Movement physics determine new position, rotation and velocity
        return new PStructAction(turn, acc, vertV, throwItem, dt);
    }
    
    
}
