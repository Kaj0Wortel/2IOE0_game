package src.Controllers;

import java.util.List;
import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.event.keyAction.PlayerKeyAction;
import src.tools.event.keyAction.action.CameraMovementAction;
import src.tools.event.keyAction.action.PlayerMovementAction;

public class PlayerController
        extends Controller {

    Instance player;
    
    final private PlayerKeyAction[] playerActions;

    public PlayerController(Instance player, int id) {
        this.player = player;
        
        PlayerMovementAction[] values = PlayerMovementAction.values();
        playerActions = new PlayerKeyAction[values.length];
        for (int i = 0; i < values.length; i++) {
            playerActions[i] = new PlayerKeyAction(1, values[i]);
        }
    }
    
    @Override
    public void controlUpdate(long dt) {
        float turn = 0;
        float acc = 0;
        float vertV = 0;
        
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
                    vertV = 10;
                }
            }
        }
        
        // Movement physics determine new position, rotation and velocity
        player.movement(new PStructAction(turn, acc, vertV, dt));
    }
    
    
}
