
package src.Assets.instance;


// Own imports
import src.Assets.OBJTexture;
import src.Physics.PStructAction;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.tools.Box3f;


// Java imports


/**
 * 
 */
public abstract class Item
        extends GridItemInstance {
    
    public Item(Box3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
    }
    
    
    public abstract void physicsAtCollision(Instance source,
            PStructAction pStruct, ModPhysicsContext physConst, ModState s);
    
    
}
