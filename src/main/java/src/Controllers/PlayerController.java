package src.Controllers;

import src.Assets.Instance;
import src.tools.event.keyAction.PlayerKeyAction;

public class PlayerController {

    Instance player;

    public PlayerController(Instance camera){
        this.player = player;
    }

    public void processKey(PlayerKeyAction.MovementAction e) {
        if (e == PlayerKeyAction.MovementAction.LEFT) {
            player.turnLeft();
        }
        if (e == PlayerKeyAction.MovementAction.RIGHT) {
            player.turnRight();
        }
        if (e == PlayerKeyAction.MovementAction.FORWARD) {
            player.moveForward();
        }
        if (e == PlayerKeyAction.MovementAction.BACKWARD) {
            player.moveBackwards();
        }
    }
}
