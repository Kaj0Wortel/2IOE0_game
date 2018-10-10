
package src.gui.border;


// Own imports
import src.tools.swing.IOBorder;


// Java imports
import java.awt.Insets;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class EditFieldDefaultBorder
        extends IOBorder {
    
    public EditFieldDefaultBorder() {
        this(new Insets(3, 3, 3, 3));
    }
    
    public EditFieldDefaultBorder(Insets in) {
        super("DEFAULT_CORNERS", "DEFAULT_BARS", in);
    }
    
    
}
