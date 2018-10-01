
package src.Assets.instance;


// Own imports
import src.Assets.OBJTexture;


// Java imports
import src.Physics.PStructAction;
import src.Physics.Physics;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.tools.Box3f;


/**
 * 
 */
public class PickupItem
        extends Item {
    
    public PickupItem(Box3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
    }
    
    
    @Override
    public void physicsAtCollision(Instance instance, PStructAction pStruct,
            PhysicsContext ps, ModState s) {
        Physics.calcPhysics(instance, pStruct, ps, s);
    }
    
    
}