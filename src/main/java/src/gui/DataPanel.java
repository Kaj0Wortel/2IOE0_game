
package src.gui;


// Own imports


// Java imports
import javax.swing.JLayeredPane;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public abstract class DataPanel
        extends JLayeredPane {
    
    public DataPanel() {
        this("");
    }
    
    public DataPanel(String name) {
        setName(name);
    }
    
    public abstract void saveData();
    
    public abstract void loadData();
    
    
}
