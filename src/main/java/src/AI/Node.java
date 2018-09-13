package src.AI;


// Java imports
import java.awt.geom.Point2D;


/**
 *
 * @author s152102
 */
public class Node {
    public Point2D.Double pos; // Node position
    public double v; // position velocity
    public double a; // position acceleration: -max, 0, max
    public double rot; // Rotation: east = 0, north = pi/2
    public double rotV; // Rotation velocity: -max,0,max
    public double g; // Distance from start (known)
    public double h; // Distance to target (calculated)
    public Node parentNode; // Parent node position
       
    public Node (Point2D.Double newPos, double newV, double newA,
            double newRot, double newRotV, double newG, double newH,
            Node newParentNode) {
        pos = newPos;
        v = newV;
        a = newA;
        rot  = newRot;
        rotV = newRotV;
        g = newG;
        h = newH;
        parentNode = newParentNode;
    }
}
