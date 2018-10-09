
package src.tools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.joml.Matrix3f;
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
     * Rotates the bar on the given axis over the given angle.
     * 
     * @param angle
     * @param xAxis
     * @param yAxis
     * @param zAxis 
     */
    public void rotate(float angle, float xAxis, float yAxis, float zAxis) {
        mul(new Matrix3f().rotate(angle, xAxis, yAxis, zAxis));
    }
    
    /**
     * Translates the bar by the given vector.
     * 
     * @param vec 
     */
    public void translate(Vector3f vec) {
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    points[x][y][z].add(vec);
                }
            }
        }
    }
    
    /**
     * Scales all points with the given scalar.
     * 
     * @param scalar 
     */
    public void scale(float scalar) {
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    points[x][y][z].mul(scalar);
                }
            }
        }
    }
    
    /**
     * Multiplies a matrix to all points.
     * 
     * @param mat 
     */
    public void mul(Matrix3f mat) {
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    points[x][y][z].mul(mat);
                }
            }
        }
    }
    
    public Planef[] generatePlanes() {
        return new Planef[] {
            new Planef(points[0][0][0],
                    new Vector3f().add(points[0][0][0]).sub(points[1][0][0])),
            new Planef(points[0][0][0],
                    new Vector3f().add(points[0][0][0]).sub(points[0][1][0])),
            new Planef(points[0][0][0],
                    new Vector3f().add(points[0][0][0]).sub(points[0][0][1]))
        };
    }
    
    public Polygon2f project(Planef plane) {
        return project(plane, new Vector3f(
                plane.c - plane.b,
                plane.a - plane.c,
                plane.b - plane.a));
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
        
        // Translate and rotate all points to the plane with normal
        // {@code n = (0, 0, 1)} and has origin {@code o = (0, 0, 0)}.
        Matrix3f mat = new Matrix3f();
        Vector3f target = new Vector3f(0, 0, 1);
        
        Vector3f rotVec = new Vector3f(n).cross(target);
        if (rotVec.x != 0 || rotVec.y != 0 || rotVec.z != 0) {
            rotVec.normalize();
            Vector2f rotVecProj = new Vector2f(rotVec.x, rotVec.y);
            Vector2f targetProj = new Vector2f(1, 0);

            // Note that {@code target.length() == 1}, {@code n.length() == 1},
            // and {@code rotVec.length() == 1}, hence no division by the multiple
            // of their lengths is needed.
            float angle = (float) Math.acos(n.dot(target));
            float angle2d = (float) (Math.acos(
                    rotVecProj.dot(targetProj)) % (0.5f*Math.PI));
            mat.rotate(angle2d, 0, 0, 1).rotate(angle, rotVec);
            
        } else if (n.z < 0) {
            mat.rotate((float) Math.PI, 1, 0, 0);
        }
        
        Vector2f[] polyPoints = new Vector2f[8];
        for (int i = 0; i < pointsOnPlane.length; i++) {
            Vector3f p = pointsOnPlane[i].mul(mat);
            polyPoints[i] = new Vector2f(p.x, p.y);
        }
        
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
        
        //Vector3f origin = new Vector3f(1, 3, 6);
        Vector3f origin = new Vector3f(0, 0, 0);
        Planef plane = new Planef(origin, new Vector3f(0, 0, -2));
        Polygon2f poly = bar.project(plane, origin);
        Color c = new Color(50, 100, 255);
        poly.visualizeDots(panel, c);
        poly.visualizeLines(panel, c);
        System.out.println(poly);
        System.out.println(Arrays.toString(poly.calcTangents()));
        System.out.println(Arrays.toString(poly.calcNormals()));
        
        origin = new Vector3f(0.1f, 0, 0);
        plane = new Planef(origin, new Vector3f(0, 0.1f, -2));
        poly = bar.project(plane, origin);
        c = Color.YELLOW;
        poly.visualizeDots(panel, c);
        poly.visualizeLines(panel, c);
        System.out.println(poly);
        System.out.println(Arrays.toString(poly.calcTangents()));
        System.out.println(Arrays.toString(poly.calcNormals()));
    }
    
    
}
