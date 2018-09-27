package src.Physics;

// Java imports
import java.awt.geom.Point2D;
import org.joml.Vector3f;

public class PStruct { // Physics struct
    //public Point2D.Double pos;
    public Vector3f pos;
    public double v;
    public double rot;
    public double colV;
    public double vertA;
    public double vertV;
    
    public PStruct (Vector3f newPos, double newV, double newRot, double newColV,
                    double newVertA, double newVertV) {
        pos = newPos;
        v = newV;
        rot = newRot;
        colV = newColV;
        vertA = newVertA;
        vertV = newVertV;
    }
}
