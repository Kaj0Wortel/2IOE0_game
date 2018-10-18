
package src.Assets.instance;


// Own imports
import src.Assets.OBJTexture;
import src.Physics.PStructAction;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.tools.Cloneable;
import src.tools.PosHitBox3f;


// Java imports


/**
 * 
 */
public class ThrowingItem
        extends Item
        implements Cloneable {
    
    public ThrowingItem(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicContext) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicContext);
    }
    
    public ThrowingItem(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicContext, boolean addToUpdater) {
        super(box, sizex, sizey, sizez, rotx, roty, rotz, model,
                 integratedRotation, physicContext, addToUpdater);
    }
    
    public ThrowingItem(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz, OBJTexture model,
            float internRotx, float internRoty, float internRotz,
            PhysicsContext physicContext, boolean addToUpdater) {
        super(box, sizex, sizey, sizez, rotx, roty, rotz, model,
                internRotx, internRoty, internRotz,
                physicContext, addToUpdater);
    }
    
    
    @Override
    public void physicsAtCollision(Instance instance, PStructAction pStruct,
            ModPhysicsContext pc, ModState s) {
        // TODO
    }
    
    @Override
    public void updateItem(long dt) {
        // TODO
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public ThrowingItem clone() {
        State s = getState();
        return new ThrowingItem(s.box.clone(), s.sizex, s.sizey, s.sizez,
                s.rotx, s.roty, s.rotz, model.clone(),
                s.internRotx, s.internRoty, s.internRotz,
                physicsContext.clone(), false);
    }
    
    @Override
    public void movement(PStructAction action) {
        State s = getState(); // For sync.
        System.out.println(s.box.pos());
        super.movement(action);
        if (s.isResetting) {
            destroy();
        }
    }

    @Override
    public char getSimpleRepr() {
        // Ignore throwing items
        return '0';
    }
}
