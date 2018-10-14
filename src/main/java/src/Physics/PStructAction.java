package src.Physics;


public class PStructAction { // Physics struct action
    public float turn;
    public float accel;
    public float verticalVelocity;
    public long dt;
    
    //public Vector3f pos;
    //public double v;
    //final public double rot;
    //final public double colV;
    //public double vertA;
    //public double vertV;
    
    public PStructAction(float turn, float accelleration,
            float verticalVelocity, long dt) {
        this.turn = turn;
        this.accel = accelleration;
        this.verticalVelocity = verticalVelocity;
        this.dt = dt;
    }
    
    /*
    public PStructAction (Vector3f newPos, double newV, 
            double newRot, double newColV,
            double newVertA, double newVertV) {
        //pos = newPos;
        //v = newV;
        //rot = newRot;
        //colV = newColV;
        //vertA = newVertA;
        //vertV = newVertV;
    }
    /**/
    
    
}
