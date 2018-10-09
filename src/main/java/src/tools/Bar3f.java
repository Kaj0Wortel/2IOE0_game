
package src.tools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.joml.Planef;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.testing.VisualAStar;


// Own imports


// Java imports


/**
 * 
 */
public class Bar3f {
    final private Vector3f[][][] points = new Vector3f[2][2][2];
    
    public Bar3f(Vector3f point, Vector3f vx, Vector3f vy, Vector3f vz) {
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    Vector3f p = new Vector3f(point);
                    if (x == 1) p.add(vx);
                    if (y == 1) p.add(vy);
                    if (z == 1) p.add(vz);
                    points[x][y][z] = p;
                }
            }
        }
    }
    
    /**
     * 
     * @param plane
     * @param o
     * @return 
     */
    public Polygon2f project(Planef plane, Vector3f o) {
        Vector3f n = new Vector3f(plane.a, plane.b, plane.c).normalize();
        Vector3f[] pointsOnPlane = new Vector3f[8];
        
        // Map all points of this bar on a plane.
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    Vector3f p = points[x][y][z];
                    // Same order
                    //int i = x + 2*y + 4*z;
                    // Polygon order
                    int i = y + z + (y == 0 && z == 1 ? 2 : 0);
                    if (x == 1) i = 4 + Math.abs(i - 3);
                    pointsOnPlane[i] =
                            p.sub(n.mul(p.sub(o, new Vector3f()).dot(n),
                                    new Vector3f()), new Vector3f()).sub(o);
                }
            }
        }
        
        
        
        Vector2f targetNXYComp = new Vector2f(0, 1);
        
        // Rotation on the x-axis.
        Vector2f nYComp = new Vector2f(n.y, n.z);
        float cosAngleX;
        if (n.y == 0) {
            cosAngleX = 1f;
            
        } else {
            cosAngleX = nYComp.dot(targetNXYComp) /
                    (nYComp.length() * targetNXYComp.length());
        }
        
        // Rotation on the y-axis.
        Vector2f nXComp = new Vector2f(n.x, n.z);
        float cosAngleY;
        if (n.x == 0) {
            cosAngleY = 1f;
            
        } else {
            cosAngleY = nXComp.dot(targetNXYComp) /
                    (nXComp.length() * targetNXYComp.length());
        }
        
        
        //float angleX = (float) Math.acos(cosAngleX);
        //float angleY = (float) Math.acos(cosAngleY);
        //System.out.println(angleX + ", " + angleY);
        
        System.out.println(cosAngleX + ", " + cosAngleY);
        float xComp = (float) Math.sqrt(1 - cosAngleX * cosAngleX);
        float yComp = (float) Math.sqrt(1 - cosAngleY * cosAngleY);
        System.out.println(xComp + ", " + yComp);
        
        System.out.println(Arrays.toString(pointsOnPlane));
        Vector2f[] polyPoints = new Vector2f[8];
        for (int i = 0; i < pointsOnPlane.length; i++) {
            Vector3f p = pointsOnPlane[i];
            polyPoints[i] = new Vector2f(
                    xComp * p.x + cosAngleX * p.z,
                    yComp * p.y + cosAngleY * p.z);
        }
        
        
        /*
        // Translate and rotate all points to the plane with normal
        // {@code n = (0, 0, 1)} and has origin {@code o = (0, 0, 0)}.
        
        // Rotation matrix.
        Matrix3f rotMat = new Matrix3f();
        Vector2f targetNXYComp = new Vector2f(0, 1);
        
        // Rotation on the x-axis.
        Vector2f nYComp = new Vector2f(n.y, n.z);
        float cosAngleX;
        if (n.y == 0) {
            cosAngleX = 1f;
            
        } else {
            cosAngleX = nYComp.dot(targetNXYComp) /
                    (nYComp.length() * targetNXYComp.length());
        }
        rotMat.rotate(-(float) Math.acos(cosAngleX), 1, 0, 0);
        n.mul(rotMat);
        
        // Rotation on the y-axis.
        Vector2f nXComp = new Vector2f(n.x, n.z);
        float cosAngleY;
        if (n.x == 0) {
            cosAngleY = 1f;
            
        } else {
            cosAngleY = nXComp.dot(targetNXYComp) /
                    (nXComp.length() * targetNXYComp.length());
        }
        rotMat.rotate(-(float) Math.acos(cosAngleY), 0, 1, 0);
        
        // Execute the translation and rotation.
        Vector2f[] polyPoints = new Vector2f[8];
        for (int i = 0; i < 8; i++) {
            Vector3f p = pointsOnPlane[i];
            p.mul(rotMat);
            polyPoints[i] = new Vector2f(p.x, p.y).sub(o);
        }
        */
        return new Polygon2f(polyPoints);
    }
    
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "["
                + formatPoint(0, 0, 0) + ","
                + formatPoint(1, 0, 0) + ","
                + formatPoint(0, 1, 0) + ","
                + formatPoint(1, 1, 0) + ","
                + formatPoint(0, 0, 1) + ","
                + formatPoint(1, 0, 1) + ","
                + formatPoint(0, 1, 1) + ","
                + formatPoint(1, 1, 1)
                + "]";
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @return 
     */
    private String formatPoint(int x, int y, int z) {
        Vector3f p = points[x][y][z];
        return "p" + x + y + z + "=(" + p.x + "," + p.y + "," + p.z + ")";
    }
    
    
    // tmp
    public static void main(String[] args) {
        Bar3f bar = new Bar3f(
                new Vector3f(),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 3, 0),
                new Vector3f(0, 0, 6));
        System.out.println(bar);
        
        VisualAStar panel = new VisualAStar(new Rectangle(-8, -8, 16, 16));
        panel.setForeground(Color.RED);
        panel.addLine(new Point2D.Double(-50, 0), new Point2D.Double(50, 0));
        panel.setForeground(Color.GREEN);
        panel.addLine(new Point2D.Double(0, -50), new Point2D.Double(0, 50));
        
        Vector3f origin = new Vector3f(0, 0, 0);
        Planef plane = new Planef(origin, new Vector3f(1, 1, 0));
        Polygon2f poly = bar.project(plane, origin);
        Color c = new Color(50, 100, 255);
        poly.visualizeDots(panel, c);
        poly.visualizeLines(panel, c);
        System.out.println(poly);
        System.out.println(poly.getTangents());
        
        origin = new Vector3f(1, 3, 6);
       // plane = new Planef(origin, new Vector3f(1, 0, 0));
        poly = bar.project(plane, origin);
        c = Color.YELLOW;
        poly.visualizeDots(panel, c);
        poly.visualizeLines(panel, c);
        System.out.println(poly);
        System.out.println(poly.getTangents());
    }
    
    
}
