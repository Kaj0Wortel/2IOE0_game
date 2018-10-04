
package src.AI;


// Own imports


// Java imports


// dl4j
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import src.Physics.PStructAction;


/**
 * 
 */
public class AINN {
    
    private PStructAction curState = new PStructAction(0, 0, 0, 1);
    private PStructAction newState = null;
    private boolean updatedLeftRight = false;
    private boolean updatedForwardBack = false;
    
    
    public AINN() {
        // TODO init here
    }
    
    
    public void execute() {
        updatedLeftRight = false;
        updatedForwardBack = true;
        // TODO
    }
    
    private BaseTrainingListener listener = new BaseTrainingListener() {
        
        @Override
        public void onEpochEnd(Model model) {
            if (1 == 1) { // left-right model.
                updatedLeftRight = true;
                
                
                
            } else { // forward-backward model.
                updatedForwardBack = true;
                
            }
        }
    };
    
    
}
