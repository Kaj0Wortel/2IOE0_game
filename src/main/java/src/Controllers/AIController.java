
package src.Controllers;

import src.AI.AINN;
import src.Assets.instance.Car;
import src.Physics.PStructAction;


// Own imports


// Java imports


/**
 * 
 */
public class AIController
        extends Controller<Car> {
    
    final private AINN ainn;
    
    /**
     * The instance to be updated.
     * @param instance 
     */
    public AIController(Car instance) {
        super(instance);
        ainn = new AINN(instance);
        ainn.start();
    }
    
    
    @Override
    public PStructAction controlUpdate(long dt) {
        if (ainn == null || ainn.isStopped()) return null;
        return ainn.createAction(dt);
    }
    
    @Override
    public void ignoreUpdate(long time) {
        super.ignoreUpdate(time);
        if (!ainn.isStopped()) ainn.stop();
    }
    
    
}
