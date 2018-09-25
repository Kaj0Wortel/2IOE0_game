package src.Controllers;

import src.Assets.Instance;
import src.tools.event.keyAction.PlayerKeyAction;

public class PlayerController {

    Instance player;

    public PlayerController(Instance player){
        this.player = player;
    }

    public void processKey(PlayerKeyAction.MovementAction e, long dt) {
        int turn = 0;
        int acc = 0;
        
        if (e == PlayerKeyAction.MovementAction.LEFT) {
            turn++;
        }
        if (e == PlayerKeyAction.MovementAction.RIGHT) {
            turn--;
        }
        if (e == PlayerKeyAction.MovementAction.FORWARD) {
            acc++;
        }
        if (e == PlayerKeyAction.MovementAction.BACKWARD) {
            acc--;
        }
        // Movement physics determine new position, rotation and velocity
        player.movement(turn, acc, dt);
    }
}
