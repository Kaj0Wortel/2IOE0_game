

package src.Controllers;

import src.tools.event.keyAction.KeyAction;


// Own imports


// Java imports


/**
 * 
 */
public abstract class Controller {
    
    public abstract void processKey(KeyAction e, long dt);
    
}
