
package src.Physics;


// Own imports


// Java imports


/**
 * 
 */
public class PhysicsContext {
    final public float linAccel;
    final public float rotationalVelocity;
    final public float maxLinearVelocity;
    final public float frictionConstant;
    final public float gravity;
    
    //  How fast you have to go to reach rotVmax.
    final public float turnCorrection; // 0 - vMax
    // How strong the knockback is.
    final public float knockback; // 0 - 1~ish
    // The closer to 1, the longer the knockback.
    final public float knockbackDur; // 0.5 - 1
    // The higher, the longer acceleration is blocked after colliding.
    final public float accBlockDur; // 5 - 40
    // Extra de-acceleration when velocity is too big.
    final public float largeSlowDown; // 1 - 10~ish
    // How much vertical velocity is maintained after surface collision.
    final public float bounceFactor; // 0 - <1
    // How good your controlls are in air.
    final public float airControl; // 0 - 1 (if > 0.6: change a correction factor)
    
    
    public PhysicsContext(float linearAcceleration,
            float rotationalVelocity, float maxLinearVelocity,
            float frictionConstant, float gravity,
            float turnCorrection, float knockback,
            float knockbackDur, float accBlockDur, float largeSlowDown,
            float bounceFactor, float airControl) {
        this.linAccel = linearAcceleration;
        this.rotationalVelocity = rotationalVelocity;
        this.maxLinearVelocity = maxLinearVelocity;
        this.frictionConstant = frictionConstant;
        this.gravity = gravity;
        
        this.turnCorrection = turnCorrection;
        this.knockback = knockback;
        this.knockbackDur = knockbackDur;
        this.accBlockDur = accBlockDur;
        this.largeSlowDown = largeSlowDown;
        this.bounceFactor = bounceFactor;
        this.airControl = airControl;
    }
    
    /**
     * Initializes the default constants.
     */
    public PhysicsContext() {
        this.linAccel = 0.8f;
        this.rotationalVelocity = (float) Math.PI / 10f;
        this.maxLinearVelocity = 10f;
        this.frictionConstant = 0.2f;
        this.gravity = -1;
        
        this.turnCorrection = 2f;
        this.knockback = 0.3f;
        this.knockbackDur = 0.95f;
        this.accBlockDur = 20f;
        this.largeSlowDown = 4f;
        this.bounceFactor = 0.5f;
        this.airControl = 0.6f;
    }
    
    
}
