
package src.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


// Own imports


// Java imports


/**
 * 
 */
public class ScaleCanvas
        extends JPanel {
    
    final private Component comp;
    
    public ScaleCanvas(Component comp) {
        super(null);
        this.comp = comp;
        SwingUtilities.invokeLater(() -> {
            this.add(comp);
        });
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Copy graphics paint state.
        AffineTransform original = g2d.getTransform();
        
        // Apply scaling.
        g2d.scale(((double) comp.getWidth()) / getWidth(),
                ((double) comp.getHeight()) / getHeight()); // Maybe they should be flipped
        // Paint panel + component.
        super.paintComponent(g2d);
        
        // Revert graphics paint state.
        g2d.setTransform(original);
    }
    
    
}
