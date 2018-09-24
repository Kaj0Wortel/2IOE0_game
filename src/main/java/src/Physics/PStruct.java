package src.Physics;

// Java imports
import java.awt.geom.Point2D;

public class PStruct { // Physics struct
    public Point2D.Double pos;
    public double v;
    public double rot;
    
    public PStruct (Point2D.Double newPos, double newV, double newRot) {
        pos = newPos;
        v = newV;
        rot = newRot;
    }
}
