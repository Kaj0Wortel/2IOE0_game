package src.Physics;


public class PStructAction { // Physics struct action
    public float turn;
    public float accel;
    public float verticalVelocity;
    public boolean throwItem;
    public long dt;
    
    
    public PStructAction(float turn, float accelleration,
            float verticalVelocity, long dt) {
        this(turn, accelleration, verticalVelocity, false, dt);
    }
        
    public PStructAction(float turn, float accelleration,
            float verticalVelocity, boolean throwItem, long dt) {
        this.turn = turn;
        this.accel = accelleration;
        this.verticalVelocity = verticalVelocity;
        this.throwItem = throwItem;
        this.dt = dt;
    }
    
    
}
