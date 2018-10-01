
package src.Assets.instance;


// Own imports
import org.joml.Vector3f;


// Java imports
import src.Assets.OBJTexture;
import src.Physics.PStructAction;
import src.Physics.Physics;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.tools.Box3f;


/**
 * 
 */
public class EnvironmentItem
        extends Item {
    
    public static enum Type {
        STATIC_OBSTACLE, SPEED_BOOST, SLOW_DOWN;
    }
    
    final public Type type;
    
    
    public EnvironmentItem(Box3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst, Type type) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
        this.type = type;
    }
    
    
    @Override
    public void physicsAtCollision(Instance source, PStructAction pStruct,
            PhysicsContext pc, ModState s) {
        if (type == Type.SPEED_BOOST) {
            if (Math.abs(s.velocity) < pc.maxLinearVelocity) {
                s.velocity = speedBoost(s.box.pos(), s.velocity); // not refined
            }
            Physics.calcPhysics(source, pStruct, pc, s);
                
        } else if (type == Type.SLOW_DOWN) {
            float vMax = slowDownSpot(s.box.pos(), pc.maxLinearVelocity); // not refined
            PhysicsContext context = new PhysicsContext(pc.linAccel,
                    pc.rotationalVelocity, vMax, pc.frictionConstant,
                    pc.gravity, pc.turnCorrection, pc.knockback,
                    pc.knockbackDur, pc.accBlockDur, pc.largeSlowDown,
                    pc.bounceFactor, pc.airControl);
            Physics.calcPhysics(source, pStruct, context, s);
        }
    }
    
    
    private float speedBoost(Vector3f startPos, float startV) {
        if (startPos.x + 3 > 40 - 0.5 &&
                40 + 0.5 > startPos.x - 3 &&
                startPos.y + 3 > 0 - 0.5 &&
                0 + 0.5 > startPos.y - 3) {
            return startV*2;
        }
        return startV;
    }
    
    private float slowDownSpot(Vector3f startPos, float vMax) {
        if (startPos.x + 3 > 0 - 25 &&
                0 + 25 > startPos.x - 3 &&
                startPos.y + 3 > -100 - 25 &&
                -100 + 25 > startPos.y - 3) {
            return vMax/4;
        }
        return vMax;
    }
    
    
}
