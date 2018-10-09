
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
    
    public Set<Vector2f> getTangents() {
        Set<Vector2f> tangents = new HashSet<>();
        if (points.length < 2) return tangents;
        
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
            Vector2f negTang = new Vector2f().add(tang.negate());
            tang.negate();
            
            System.out.println("tang: " + tang + ", neg tang: " + negTang);
            if (!tangents.contains(tang) &&
                    !tangents.contains(negTang)) {
                tangents.add(tang);
            }
            prev = p;
        }
        Vector2f p = points[0];
        Vector2f tang = new Vector2f().add(p).sub(prev);
        if (Math.abs(tang.x) != 0 || Math.abs(tang.y) != 0) {
            // Trick to prevent {@code -0 != 0} errors.
            Vector2f negTang = new Vector2f(tang.negate());
            System.out.println("tang: " + tang + ", neg tang: " + negTang);
            if (!tangents.contains(tang) &&
                    !tangents.contains(negTang)) {
                tangents.add(tang);
            }
        }
        
        return tangents;
    }
    
    
}
