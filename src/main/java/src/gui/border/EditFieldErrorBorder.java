
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
public class EditFieldErrorBorder
        extends IOBorder {
    
    public EditFieldErrorBorder() {
        this(new Insets(4, 4, 4, 4));
    }
    
    public EditFieldErrorBorder(Insets in) {
        super("ERROR_CORNERS", "ERROR_BARS", in);
    }
    
    
}
