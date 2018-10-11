
package src.tools;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import org.joml.Vector2f;
import src.testing.VisualAStar;


// Own imports


// Java imports


/**
 * 
 */
public class Polygon2f {
    public Vector2f[] points;
    
    public Polygon2f(Vector2f[] points) {
        this.points = points;
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[");
        
        boolean first = true;
        for (int i = 0; i < points.length; i++) {
            if (first) first = false;
            else sb.append(",");
            
            sb.append("p");
            sb.append(i);
            sb.append("=(");
            sb.append(points[i].x);
            sb.append(",");
            sb.append(points[i].y);
            sb.append(")");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    
    
    public void visualizeDots(VisualAStar panel, Color c) {
        for (int i = 0; i < points.length; i++) {
            Vector2f vec = points[i];
            panel.setForeground(c);
            panel.addPoint(new Point2D.Double(vec.x, vec.y));
        }
    }
    
    public void visualizeLines(VisualAStar panel, Color c) {
        if (points.length < 2) return;
        Vector2f prev = points[0];
        for (int i = 1; i < points.length; i++) {
            Vector2f vec = points[i];
            panel.addLine(
                    new Point2D.Double(prev.x, prev.y),
                    new Point2D.Double(vec.x, vec.y));
            prev = vec;
        }
        
        panel.addLine(
                new Point2D.Double(prev.x, prev.y),
                new Point2D.Double(points[0].x, points[0].y));
    }
    
    public Vector2f[] calcTangents() {
        if (points.length < 2) return new Vector2f[0];
        Set<Vector2f> tangents = new HashSet<>();
        Set<String> check = new HashSet<>();
        
        Vector2f prev = points[0];
        for (int i = 1; i < points.length; i++) {
            Vector2f p = points[i];
            
            // Must be in this way to prevent {@code -0 != 0} errors.
            Vector2f tang = new Vector2f().add(p).sub(prev);
            if (Math.abs(tang.x) == 0 && Math.abs(tang.y) == 0) {
                prev = p;
                continue;
            } else tang.normalize();
            
            // Trick to prevent {@code -0 != 0} errors.
            Vector2f negTang = new Vector2f().add(tang.negate(new Vector2f()));
            
            if (!check.contains(tang.toString()) &&
                    !check.contains(negTang.toString())) {
                tangents.add(tang);
                check.add(tang.toString());
            }
            prev = p;
        }
        Vector2f p = points[0];
        Vector2f tang = new Vector2f().add(p).sub(prev);
        if (Math.abs(tang.x) != 0 || Math.abs(tang.y) != 0) {
            // Trick to prevent {@code -0 != 0} errors.
            Vector2f negTang = new Vector2f(tang.negate());
            if (!check.contains(tang.toString()) &&
                    !check.contains(negTang.toString())) {
                tangents.add(tang);
            }
        }
        
        return tangents.toArray(new Vector2f[tangents.size()]);
    }
    
    public Vector2f[] calcNormals() {
        Vector2f[] tangents = calcTangents();
        Vector2f[] normals = new Vector2f[tangents.length];
        
        for (int i = 0; i < tangents.length; i++) {
            Vector2f t = tangents[i];
            normals[i] = new Vector2f(-t.y, t.x);
        }
        
        return normals;
    }
    
    /**
     * 
     * @param line
     * @return 
     */
    public float[] project(Vector2f line) {
        Vector2f n = new Vector2f(line.y, -line.x).normalize();
        Vector2f[] pointsOnLine = new Vector2f[points.length];
        for (int i = 0; i < points.length; i++) {
            Vector2f p = points[i];
            pointsOnLine[i] = p.sub(n.mul(p.sub(line, new Vector2f()).dot(n),
                    new Vector2f()), new Vector2f());
        }
        float[] rtn = new float[pointsOnLine.length];
        for (int i = 0; i < pointsOnLine.length; i++) {
            Vector2f p = pointsOnLine[i];
            if (Math.abs(line.x) == 0 || Math.abs(line.y) == 0) {
                if (Math.abs(line.x) != 0) {
                    rtn[i] = p.x / line.x;
                    
                } else if (Math.abs(line.y) != 0) {
                    rtn[i] = p.y / line.y;
                    
                } else {
                    throw new IllegalArgumentException(
                            "Expected a line, but found (0, 0)!");
                }
                
            } else {
                if (Math.abs(line.x) > Math.abs(line.y)) {
                    rtn[i] = p.x / line.x;
                    
                } else {
                    rtn[i] = p.y / line.y;
                }
            }
        }
        
        return rtn;
    }
    
    
}
