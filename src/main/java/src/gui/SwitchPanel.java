
package src.gui;


// Own imports


// Java imports
import src.GS;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * 
 * 
 * @author Kaj Wortel
 */
public class SwitchPanel
        extends JPanel {
    final private Map<String, JComponent> panelMap = new HashMap<>();
    private String activeID = null;
    
    public SwitchPanel() {
        super(null);
        setOpaque(false);
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        JComponent panel = getActivePanel();
        if (panel != null) panel.setBounds(0, 0, width, height);
    }
    
    /**
     * Sets the active panel 
     * @param i 
     */
    public void setActivePanel(String panelID) {
        // Get old panel.
        JComponent oldPanel = getActivePanel();
        // Get new panel.
        activeID = panelID;
        JComponent newPanel = getActivePanel();
        // Compare new and old. If equal, return.
        if (Objects.equals(oldPanel, newPanel)) return;
        
        // Remove (and save if available) the old panel.
        if (oldPanel instanceof DataPanel) ((DataPanel) oldPanel).saveData();
        if (oldPanel != null) remove(oldPanel);
        
        // Add the new panel.
        if (newPanel != null) {
            add(newPanel);
            newPanel.setBounds(0, 0, getWidth(), getHeight());
            if (newPanel instanceof DataPanel) {
                SwingUtilities.invokeLater(() -> {
                    ((DataPanel) newPanel).loadData();
                });
            }
        }
        
        if (GS.mainPanel != null) GS.mainPanel.closeAllInternalFrames();
        
        repaint();
    }
    
    /**
     * @return the currently active panel.
     */
    public JComponent getActivePanel() {
        return panelMap.get(activeID);
    }
    
    /**
     * Adds the given panel to the map.
     * 
     * @param panel the panel to add.
     * @param panelID the id of the panel.
     */
    public void addPanel(JComponent panel) {
        addPanel(panel, panel.getName());
    }
    
    /**
     * Adds a panel with the given id.
     * @param panel
     * @param panelID 
     */
    public void addPanel(JComponent panel, String panelID) {
        panelMap.put(panelID, panel);
    }
    
    
}
