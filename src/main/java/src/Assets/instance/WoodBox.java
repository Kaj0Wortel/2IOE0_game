
package src.Assets.instance;

import src.Assets.OBJTexture;
import src.Assets.TextureImg;
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;


// Own imports


// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class WoodBox
        extends GridItemInstance {
    //new TextureImg(gl, "wood_box.png", 5, 0.5f);

    public WoodBox(PosHitBox3f box, float size, float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation, PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation, physicConst);
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    
}
