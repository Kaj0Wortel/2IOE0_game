
package src.Assets.instance;


// Own imports

import src.Assets.OBJTexture;
import src.Assets.instance.ThrowingItemFactory.ItemType;
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;

// Java imports


/**
 * 
 */
public class MaterialInstance
        extends GridItemInstance {

    private ItemType inventoryItem = ItemType.RED_SHELL; // = null

    public MaterialInstance(PosHitBox3f box, float size,
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
