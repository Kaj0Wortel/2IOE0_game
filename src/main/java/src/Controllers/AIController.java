
package src.Controllers;

import src.Assets.instance.Car;
import src.Physics.PStructAction;


// Own imports


// Java imports


/**
 * 
 */
public class AIController
        extends Controller<Car> {
    
    
    /**
     * The instance to be updated.
     * @param instance 
     */
    public AIController(Car instance) {
        super(instance);
        
        // Your initialization.
    }
    
    
    @Override
    public PStructAction controlUpdate(long dt) {
        return null;
    }
    
    
}
