
package src.gui;


// Own imports


// Java imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import src.GS;
import src.tools.io.ImageManager;
import src.tools.update.Updateable;
import src.tools.update.Updater;


/**
 * 
 */
public class GUIPanel
        extends JPanel
        implements Updateable {
    
    private int counter = 0;
    
    public GUIPanel() {
        super(null);
        setBackground(new Color(1, 1, 1, 0));
        setOpaque(false);
        
        SwingUtilities.invokeLater(() -> {
            Updater.addTask(this);
        });
    }
    
    @Override
    public void performUpdate(long timeStamp)
            throws InterruptedException {
        if (counter++ > 5) {
            counter = 0;
            repaint();
        }
    }
    
    @Override
    public Priority getPriority() {
        return Priority.ONLY_WHEN_RUNNING;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        BufferedImage img = ImageManager.getImage("GAME_ICON", 0, 0);
        paintImage(g2d, img, 100, 100, 100, 100, 0);
    }
    
    public void paintImage(Graphics2D g2d, Image img, double x, double y,
            double width, double height, double angle) {
        AffineTransform original = g2d.getTransform();
        
        g2d.translate(x, y);
        g2d.scale(width / getWidth(), height / getHeight());
        g2d.rotate(angle);
        g2d.drawImage(img, 0, 0, null);
        
        g2d.setTransform(original);
    }
    
    
}
