package src.Controllers;

import java.util.List;
import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.PlayerKeyAction;

public class PlayerController
        extends Controller {

    Instance player;
    
    final private PlayerKeyAction[] playerActions;

    public PlayerController(Instance player, int id) {
        this.player = player;
        
        playerActions = new PlayerKeyAction[] {
                new PlayerKeyAction(id, PlayerKeyAction.MovementAction.LEFT),
                new PlayerKeyAction(id, PlayerKeyAction.MovementAction.RIGHT),
                new PlayerKeyAction(id, PlayerKeyAction.MovementAction.FORWARD),
                new PlayerKeyAction(id, PlayerKeyAction.MovementAction.BACKWARD),
                new PlayerKeyAction(id, PlayerKeyAction.MovementAction.JUMP)
        };
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
                if (action.getAction() == PlayerKeyAction.MovementAction.LEFT) {
                    turn += Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerKeyAction.MovementAction.RIGHT) {
                    turn -= Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerKeyAction.MovementAction.FORWARD) {
                    acc += Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerKeyAction.MovementAction.BACKWARD) {
                    acc -= Math.abs(pressed.get(0).getValue());
                }
                if (action.getAction() == PlayerKeyAction.MovementAction.JUMP) {
                    vertV = 10;
                }
            }
        }
        
        // Movement physics determine new position, rotation and velocity
        player.movement(new PStructAction(turn, acc, vertV, dt));
    }
    
    
}
