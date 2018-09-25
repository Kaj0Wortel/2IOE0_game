package src.Controllers;

import src.Assets.Instance;
import src.tools.event.keyAction.PlayerKeyAction;

public class PlayerController {

    Instance player;

    public PlayerController(Instance player){
        this.player = player;
    }

    public void processKey(PlayerKeyAction.MovementAction e) {
        int turn = 0;
        int acc = 0;
        
        if (e == PlayerKeyAction.MovementAction.LEFT) {
            //player.turnLeft();
            turn++;
        }
        if (e == PlayerKeyAction.MovementAction.RIGHT) {
            //player.turnRight();
            turn--;
        }
        if (e == PlayerKeyAction.MovementAction.FORWARD) {
            //player.moveForward();
            acc++;
        }
        if (e == PlayerKeyAction.MovementAction.BACKWARD) {
            //player.moveBackwards();
            acc--;
        }
        // Movement physics determine new position, rotation and velocity
        player.movement(turn, acc);
    }
}
