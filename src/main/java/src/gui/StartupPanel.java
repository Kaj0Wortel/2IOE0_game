
package src.gui;


// Own imports

// Java imports
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import src.GS;


/**
 * 
 */
public class StartupPanel
        extends JPanel {
    
    final private static int SPACING = 10;
    
    final private RacingButton start;
    final private RacingButton keyBindings;
    
    public StartupPanel() {
        super(null);
        setName("startup panel");
        
        start = new RacingButton("Start");
        add(start);
        start.addActionListener((e) -> {
            GS.startRendering();
        });
        
        keyBindings = new RacingButton("Key bindings");
        add(keyBindings);
        keyBindings.addActionListener((e) -> {
            GS.mainPanel.getSwitchPanel().setActivePanel("key config");
        });
        
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                update();
            }
        });
        
        update(); 
        repaint();
    }
    
    private void update() {
        start.setLocation(100, 100);
        start.setSize(getWidth() / 5, getHeight() / 5);
        
        keyBindings.setLocation(start.getX(),
                start.getY() + start.getHeight() + SPACING);
        keyBindings.setSize(getWidth() / 5, getHeight() / 5);
    }
    
    
}
