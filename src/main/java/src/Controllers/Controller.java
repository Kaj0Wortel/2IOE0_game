

package src.Controllers;


// Own imports
import src.tools.event.keyAction.KeyAction;


// Java imports


/**
 * 
 */
public abstract class Controller {
    
    public abstract void processKey(KeyAction e, long dt);
    
}
