
package src.gui;


// Own imports
import src.gui.border.EditFieldErrorBorder;
import src.gui.border.EditFieldDefaultBorder;
import src.tools.swing.DocumentAdapter;


// Java imports
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class EditField
        extends JTextField {
    
    private boolean errorWasSet = false;
    
    public EditField() {
        this(null, null, 0);
    }
    
    public EditField(String text) {
        this(null, text, 0);
    }
    
    public EditField(int columns) {
        this(null, null, columns);
    }
    
    public EditField(String text, int columns) {
        this(null, text, columns);
    }
    
    public EditField(Document doc, String text, int columns) {
        super(doc, text, columns);
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                clearError();
            }
        });
        
        getDocument().addDocumentListener((DocumentAdapter)
                (DocumentEvent e) -> clearError());
        
        setDisabledTextColor(Color.GRAY);
        setBorder(new EditFieldDefaultBorder());
    }
    
    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        setBackground(enable
                ? Color.WHITE
                : new Color(220, 220, 220));
    }
    
    /**
     * Sets the error status of the field.
     */
    public void setError() {
        errorWasSet = true;
        if (!(getBorder() instanceof EditFieldErrorBorder)) {
            setBorder(new EditFieldErrorBorder());
        }
        
        // Reset the error was set flag.
        SwingUtilities.invokeLater(() -> {
            errorWasSet = false;
        });
    }
    
    /**
     * Clears the error status of the field.
     * 
     * @see #forceClearError()
     */
    public void clearError() {
        if (errorWasSet) return;
        forceClearError();
    }
    
    /**
     * Clears the error status of the field.
     * Use this function instead of {@link #clearError()} if
     * there are multiple calls in the same event.
     * 
     * @see #clearError()
     */
    public void forceClearError() {
        if (!(getBorder() instanceof EditFieldDefaultBorder)) {
            setBorder(new EditFieldDefaultBorder());
        }
    }
    
    
}
