
package src.Assets.instance;


// Own imports
import src.Assets.OBJTexture;


// Java imports
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;


/**
 * 
 */
public class Car
        extends GridItemInstance {
    
    public Car(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    
}
