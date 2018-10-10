
package src.gui.menuList;


// Own imports


// Java imports


/**
 * 
 */
public class ConfigKeyMenuHeader
        extends MenuHeader {
    
    public ConfigKeyMenuHeader(Integer[] offsets) {
        super(new Contents[] {
            new TextContents("Player"),
            new TextContents("Action"),
            new EmptyContents(),        // change button
            new TextContents("Controller type"),
            new TextContents("Controller name"),
            new TextContents("Controller button"),
        }, offsets);
    }
    
    
    
}
