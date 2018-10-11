package src.AI;

// Java imports
import java.awt.geom.Point2D;
import src.tools.MultiTool;

public class FinalNode {
    
        public Point2D.Double pos;  // Node position
    public double v;            // position velocity
    public double a;            // position acceleration: -max, 0, max
    public double rot;          // Rotation: east = 0, north = pi/2
    public double rotV;         // Rotation velocity: -max,0,max
    public double vertV;        // vertical velocity
    public double g;            // Distance from start (known)
    public double h;            // Distance to target (calculated)
    public int nextCP;          // currently travelling to CP(nextCP*2, nextCP*2 + 1)
    public FinalNode parentNode;// Parent node position

       
    
    /**
     * Creates a point from the given data.
     * @param newPos
     * @param newV
     * @param newA
     * @param newRot
     * @param newRotV
     * @param newVertV
     * @param newG
     * @param newH
     * @param newNextCP
     * @param newParentNode 
     */
    public FinalNode (Point2D.Double newPos, double newV, double newA,
            double newRot, double newRotV, double newVertV, double newG, 
            double newH, int newNextCP, FinalNode newParentNode) {
        pos = newPos;
        v = newV;
        a = newA;
        rot  = newRot;
        rotV = newRotV;
        vertV = newVertV;
        g = newG;
        h = newH;
        nextCP = newNextCP;
        parentNode = newParentNode;
    }
    
    /**
     * Creates a node from the data of the given array,
     * such that for any Node {@code n} it holds that:
     * {@code n.equals(new Node(n.toArray()))}.
     * 
     * @param arr the input array. Must be non-null and have a length of 9.
     */
    public FinalNode(double[] arr) {
        if (arr == null)
            throw new IllegalArgumentException(
                    "Expected a non-null array, but found null!");
        if (arr.length != 9)
            throw new IllegalArgumentException(
                    "Expected an array of length 9, but found length "
                            + arr.length + "!");
        
        pos = new Point2D.Double(arr[0], arr[1]);
        v = arr[2];
        a = arr[3];
        rot = arr[4];
        rotV = arr[5];
        vertV = arr[6];
        g = arr[7];
        h = arr[8];
        nextCP = (int) arr[9];
    }
    
    /**
     * @return an array representation of this node.
     * 
     * The array contains the data from {@code this Node} in the
     * same order as the fields are delared in this class.
     * Note that the parent node data is ommited here.
     */
    public double[] toArray() {
        return new double[] {
            pos.x,
            pos.y,
            v,
            a,
            rot,
            rotV,
            vertV,
            g,
            h,
            nextCP
        };
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof FinalNode)) return false;
        FinalNode node = (FinalNode) obj;
        return pos.equals(node.pos) &&
                v == node.v &&
                a == node.a &&
                rot == node.rot &&
                rotV == node.rotV &&
                vertV == node.vertV &&
                g == node.g &&
                h == node.h &&
                nextCP == node.nextCP;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + "pos=(" + pos.x + "," + pos.y + "),"
                + "v=" + v
                + "a=" + a
                + "rot=" + rot
                + "rotv=" + rotV
                + "vertV=" + vertV
                + "g=" + g
                + "h=" + h
                + "nextCP=" + nextCP
                + "hasParent=" + (parentNode != null)
                + "]";
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Note that this functions does NOT take the parent node in account
     * as this would result in recursive behaviour.
     * Also {@code parentNode == null} is ommited as nodes loaded from
     * a file are oterwise unequal.
     */
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(pos, v, a, rot, rotV, vertV, g, h, nextCP);
    }
}