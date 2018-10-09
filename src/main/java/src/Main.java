
package src;


// Own imports

import net.java.games.input.Controller;
import net.java.games.input.Rumbler;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CarKeyAction;
import src.tools.event.keyAction.CarKeyAction.MovementAction;
import src.tools.update.Updateable;

import java.util.List;
import src.AI.AINN;

// Java imports


/**
 * Class that is invoked when started.
 */
public class Main {
    
    public static void main(String[] args) {
        GS.init();
        
        // TMP
        //Updateable up = new TmpUpdateable(1);
        //Updater.addTask(up);
        new AINN(null);
    }
    
    // BEGIN TMP
    private static class TmpUpdateable
            implements Updateable {
        
        final private CarKeyAction[] actions;
        
        public TmpUpdateable(int id) {
            actions = new CarKeyAction[] {
                new CarKeyAction(id, MovementAction.LEFT),
                new CarKeyAction(id, MovementAction.RIGHT)
            };
        }
        
        @Override
        public void performUpdate(long timeStamp)
                throws InterruptedException {
            // Checking keys (1)
            /*
            for (CarKeyAction action : actions) {
                List<Key> keys = GS.getKeys(action);
                if (keys == null) return;
                if (GS.keyDet.werePressed(keys)) {
                    System.out.println(action.getAction());
                }
            }
            */
            // Checking keys with key data.
            for (CarKeyAction action : actions) {
                List<ControllerKey> keys = GS.getKeys(action);
                if (keys == null) continue;
                for (ControllerKey key : GS.keyDet.getPressedFrom(keys)) {
                    // Data that was retrieved:
                    System.out.println(key);
                    System.out.println(key.getIdentifier());
                    System.out.println(key.getValue());
                    // etc.
                    
                    // Using rumblers:
                    Controller c = key.getController();
                    if (c != null) {
                        for (Rumbler rumbler : c.getRumblers()) {
                            if (rumbler != null) {
                                rumbler.rumble(1.0f); // max
                                //rumbler.rumble(0.0f); // min
                            }
                        }
                        
                    }
                }
            }
            
        }
        
        @Override
        public Priority getPriority() {
            return Priority.ONLY_WHEN_RUNNING;
        }
        
        
    }
    
    // END TMP
    
}
