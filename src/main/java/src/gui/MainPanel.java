
package src.gui;


// Own imports
import src.GS;
import src.tools.io.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

// Java imports


/**
 * Main panel class for adding all components.
 * extends {@link JLayeredPane} to enable layering between the panels.
 */
public class MainPanel
        extends JLayeredPane {
    
    // GUI
    final private JFrame frame;
    
    // The stored state to remember which state
    // was active when exiting full screen.
    private int storedState = Frame.NORMAL;
    
    private int oldX;
    private int oldY;
    private int oldWidth;
    private int oldHeight;
    
    
    /**
     * Constructor.
     */
    public MainPanel() {
        frame = new JFrame(GS.APP_NAME);
        frame.setIconImage(ImageManager.getImage(GS.FRAME_ICON, 0, 0));
        frame.getContentPane().setBackground(Color.GREEN);
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(frame.getWidth(), frame.getHeight());
                
                if (!GS.isFullScreen()) {
                    oldX = getX();
                    oldY = getY();
                    oldWidth = getWidth();
                    oldHeight = getHeight();
                }
            }
        });
        
        SwingUtilities.invokeLater(() -> {
            frame.add(this);
            frame.setSize(1000, 750);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
    
    
    /**
     * This method should be called to ensure that the size and
     * location of the frame are correctly shown.
     */
    public void update() {
        // To ensure that the frame is visible.
        frame.setVisible(true);
        
        // Update full screen settings.
        if (GS.isFullScreen()) {
            if (!frame.isUndecorated()) {
                storedState = frame.getExtendedState();
                frame.dispose();
                frame.setUndecorated(true);
                frame.setVisible(true);
            }
            
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(0, 0);
            frame.setSize((int) screenSize.getWidth(),
                    (int) screenSize.getHeight() - 1);// -1 is tmp!
            
        } else {
            if (frame.isUndecorated()) {
                frame.dispose();
                frame.setUndecorated(false);
                frame.setVisible(true);
                SwingUtilities.invokeLater(() -> {
                    frame.setExtendedState(storedState);
                });
            }
            
            if (storedState == Frame.NORMAL) {
                // Using {@link setBounds(int, int, int, int) to prevent
                // changing the width and height when relocating the frame.
                frame.setBounds(oldX, oldY,
                        oldWidth, oldHeight);
            }
        }
        
        // Set the size of the panel.
        Insets in = frame.getInsets();
        super.setBounds(0, 0,
                frame.getWidth() - in.left - in.right,
                frame.getHeight() - in.top - in.bottom);
    }
    
    
    /**
     * Closes all {@link JInternalFrame}s added to this layered pane.
     */
    public void closeAllInternalFrames() {
        for (Component comp : getComponents()) {
            if (comp instanceof JInternalFrame) {
                comp.setVisible(false);
            }
        }
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    
}
