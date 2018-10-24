
package src.Assets.instance;


// Own imports
import com.jogamp.opengl.GL3;
import src.Assets.OBJTexture;


// Java imports
import src.Physics.PStructAction;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.Shaders.ShaderProgram;
import src.tools.PosHitBox3f;


/**
 * 
 */
public class PickupItem
        extends Item {
    final protected static long INACTIVE_TIME = 5_000L;
    final protected static float SPIN_SPEED = 3f;
    
    private long hitTimeRemaining = 0L;
    private boolean isHit = false;
    
    
    public PickupItem(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
    }
    
    
    @Override
    public void updateItem(long dt) {
        if (isHit) {
            hitTimeRemaining -= dt;
            isHit = (hitTimeRemaining > 0);
            
        } else {
            roty(SPIN_SPEED);
        }
    }
    
    @Override
    public void physicsAtCollision(Instance instance, PStructAction pStruct,
            ModPhysicsContext pc, ModState s) {
        if (isHit || !(instance instanceof Car)) return;
        Car car = (Car) instance;
        
        System.out.println("Pickup-item!");
        isHit = true;
        hitTimeRemaining = INACTIVE_TIME;
        
        car.giveItem(s);
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void draw(GL3 gl, ShaderProgram shader) {
        if (!isHit) super.draw(gl, shader);
    }
    
    
}
