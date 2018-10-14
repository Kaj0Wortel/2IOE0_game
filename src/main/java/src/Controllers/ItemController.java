
package src.Controllers;


// Own imports
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.instance.Car;
import src.Assets.instance.Instance.State;
import src.Assets.instance.ThrowingItem;
import src.Physics.PStructAction;


// Java imports


/**
 * 
 */
public class ItemController
        extends Controller<ThrowingItem> {
    
    final private Car source;
    final private Car target;
    
    public ItemController(ThrowingItem item, Car source, Car target) {
        super(item);
        this.source = source;
        this.target = target;
    }
    
    @Override
    public PStructAction controlUpdate(long dt) {
        if (target == null) return new PStructAction(0, 1.5f, 0, dt);
        
        State is = instance.getState();
        State ts = target.getState();
        
        float itemRot = (float) Math.toRadians(is.roty);
        
        Vector3f p1 = is.box.pos();
        Vector3f p2 = ts.box.pos();
        Vector2f targetDir = new Vector2f(p2.x - p1.x, p2.z - p1.z);
        Vector2f defaultDir = new Vector2f(0, 1);
        float targetRot = (float) Math.acos(targetDir.dot(defaultDir) /
                (targetDir.length() * defaultDir.length()));
        if (targetDir.x < 0) targetRot = -targetRot;
        
        float finalRot = (float) ((targetRot - itemRot) % (2*Math.PI));
        if (finalRot < -Math.PI) {
            finalRot += 2*Math.PI;
            
        } else if (finalRot > Math.PI) {
            finalRot -= 2*Math.PI;
        }
        
        return new PStructAction((float) (-finalRot / Math.PI), 1.5f, 0, dt);
    }
    
    
}
