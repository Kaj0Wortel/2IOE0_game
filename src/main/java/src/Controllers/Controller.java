

package src.Controllers;

import javax.swing.SwingUtilities;
import src.tools.update.Updateable;
import src.tools.update.Updater;


// Own imports


/**
 * 
 */
public abstract class Controller
        implements Updateable {
    
    protected long prevTimeStamp = -1;
    
    
    public Controller() {
        this(true);
    }
    
    public Controller(boolean add) {
        if (add) {
            SwingUtilities.invokeLater(() -> {
                Updater.addTask(this);
            });
        }
    }
    
    
    @Override
    public void performUpdate(long time) {
        if (prevTimeStamp == -1) {
            prevTimeStamp = time;
            return;
        }
        
        controlUpdate(time - prevTimeStamp);
        prevTimeStamp = time;
    }
    
    @Override
    public void ignoreUpdate(long time) {
        prevTimeStamp = time;
    }
    
    /**
     * Called when the controller should update the object it is controlling.
     * 
     * @param dt the amount of elapsed time.
     */
    public abstract void controlUpdate(long dt);
    
    

    @Override
    public Priority getPriority() {
        return Priority.UPDATE_ALWAYS;
    }
    
    
}
