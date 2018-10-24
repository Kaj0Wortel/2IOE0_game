
package src;


// Own imports
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CarKeyAction;
import src.tools.event.keyAction.action.CarMovementAction;
import src.tools.update.Updateable;
import src.tools.update.Updater;


// Java imports
import java.util.List;


/**
 * Class that is invoked when started.
 */
public class Main {
    
    final private static boolean SKIP_INTRO = false;
    
    public static void main(String[] args) {
        GS.init();
        if (SKIP_INTRO) {
            GS.startRendering(1, true);
        }
    }
    
    // BEGIN TMP
    private static class TmpUpdateable
            implements Updateable {
        
        final private CarKeyAction[] actions;
        
        public TmpUpdateable(int id) {
            actions = new CarKeyAction[] {
                new CarKeyAction(id, CarMovementAction.LEFT),
                new CarKeyAction(id, CarMovementAction.RIGHT)
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
