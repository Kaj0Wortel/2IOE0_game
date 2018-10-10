
package src.gui.menuList;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;


// Own imports


// Java imports


/**
 * 
 */
public class ConfigKeyMenuHeader
        extends MenuHeader
        implements SelectableItem {
    
    public ConfigKeyMenuHeader(Integer[] offsets) {
        super(new Contents[] {
            new HeaderSelector(),
            new TextContents("Player"),
            new TextContents("Action"),
            new EmptyContents(),        // change button
            new TextContents("Controller type"),
            new TextContents("Controller name"),
            new TextContents("Controller button"),
        }, offsets);
        
        SwingUtilities.invokeLater(() -> {
            ((HeaderSelector) contents.get(0)).setMenuBar(this);
        });
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
    
    @Override
    public boolean isSelected() {
        return ((SelectContents) contents.get(0)).isSelected();
    }
    
    @Override
    public void setSelected(boolean selected) {
        ((SelectContents) contents.get(0)).setSelected(selected);
    }
    
    
}
