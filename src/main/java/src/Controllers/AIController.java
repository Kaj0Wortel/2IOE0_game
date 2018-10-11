
package src.Controllers;

import src.AI.AINN;
import src.Assets.instance.GridItemInstance;
import src.Assets.instance.Instance;


// Own imports


// Java imports


/**
 * 
 */
public class AIController
        extends Controller {
    
    final private GridItemInstance instance;
    final private AINN ainn;
    
    
    /**
     * The instance to be updated.
     * @param instance 
     */
    public AIController(GridItemInstance instance) {
        this.instance = instance;
        ainn = new AINN(instance);
    }
    
    
    @Override
    public void controlUpdate(long dt) {
        if (ainn == null) return;
        if (ainn.isStopped()) ainn.start();
        instance.movement(ainn.createAction(dt));
    }
    
    @Override
    public void ignoreUpdate(long time) {
        super.ignoreUpdate(time);
        if (!ainn.isStopped()) ainn.stop();
    }
    
    
}
