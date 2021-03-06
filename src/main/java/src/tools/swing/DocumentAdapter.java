
package src.tools.swing;


// Java imports
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Adapter class for the {@link #DocumentListener} interface.
 * 
 * @author Kaj Wortel (0991586)
 */
@FunctionalInterface
public interface DocumentAdapter
        extends DocumentListener {
    
    @Override
    default public void insertUpdate(DocumentEvent e) {
        action(e);
    }
    
    @Override
    default public void removeUpdate(DocumentEvent e) {
        action(e);
    }
    
    @Override
    default public void changedUpdate(DocumentEvent e) {
        action(e);
    }
    
    /**
     * Bundeled action function.
     * 
     * @param e the document event.
     */
    public void action(DocumentEvent e);
    
}
