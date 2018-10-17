

package src.Controllers;

import javax.swing.SwingUtilities;
import src.Assets.instance.Instance;
import src.Physics.PStructAction;
import src.tools.update.Updateable;
import src.tools.update.Updater;


// Own imports


/**
 * 
 */
public abstract class Controller<I extends Instance>
        implements Updateable {
    
    protected long prevTimeStamp = -1;
    protected I instance;
    
    public Controller(I instance) {
        this(instance, true);
    }
    
    public Controller(I instance, boolean add) {
        this.instance = instance;
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
        
        if (instance != null) {
            if (instance.isDestroyed()) {
                Updater.removeTask(this);
                
            } else {
                PStructAction action = controlUpdate(time - prevTimeStamp);
                if (action != null) instance.movement(action);
            }
            
        } else {
            controlUpdate(time - prevTimeStamp);
        }
        
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
    public abstract PStructAction controlUpdate(long dt);
    
    

    @Override
    public Priority getPriority() {
        return Priority.ONLY_WHEN_RUNNING;
    }
    
    
}
