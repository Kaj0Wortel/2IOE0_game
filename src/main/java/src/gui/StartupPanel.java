
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
    final private FixedJComboBox<Integer> players;
    final private FixedJComboBox<Boolean> useAI;
    
    
    public StartupPanel() {
        super(null);
        setName("startup panel");
        
        keyBindings = new RacingButton("Key bindings");
        add(keyBindings);
        keyBindings.addActionListener((e) -> {
            GS.mainPanel.getSwitchPanel().setActivePanel("key config");
            keyBindings.reset();
        });
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                update();
            }
        });
        
        players = new FixedJComboBox<Integer>();
        players.addItem(1);
        players.addItem(2);
        players.addItem(3);
        players.addItem(4);
        add(players);
        
        useAI = new FixedJComboBox<Boolean>();
        useAI.addItem(true);
        useAI.addItem(false);
        add(useAI);
        
        start = new RacingButton("Start");
        add(start);
        start.addActionListener((e) -> {
            GS.startRendering(
                    (Integer) players.getSelectedItem(),
                    (Boolean) useAI.getSelectedItem());
            start.reset();
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
        
        players.setLocation(keyBindings.getX(),
                keyBindings.getY() + keyBindings.getHeight() + SPACING);
        players.setSize(getWidth() / 5, getHeight() / 20);
        
        useAI.setLocation(players.getX(),
                players.getY() + players.getHeight() + SPACING);
        useAI.setSize(getWidth() / 5, getHeight() / 20);
    }
    
}
