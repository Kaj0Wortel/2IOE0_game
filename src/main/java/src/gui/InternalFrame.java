
package src.gui;


// Own imports
import src.tools.swing.IOBorder;


// Java imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class InternalFrame
        extends JInternalFrame {
    
    public InternalFrame() {
        super(null, false, false, false, false);
        setLayout(null);
        
        // To ensure that {@link #getInsets()} returns a correct value.
        pack();
        
        // Remove the title bar.
        ((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        
        // Add a new border.
        setBorder(new IOBorder("BUTTON_001_CORNERS", "BUTTON_001_BARS",
                new Insets(5, 5, 5, 5)));
        
        // Set the background color.
        setBackground(new Color(255, 225, 160, 255));
        
        // Set the opacity.
        setOpaque(true);
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.setColor(new Color(255, 0, 0, 255));
        //g.drawRect(0, 0, getWidth(), getHeight());
    }
    
    
    public static void main(String[] args) {
        new InternalFrame();
    }
    
}
