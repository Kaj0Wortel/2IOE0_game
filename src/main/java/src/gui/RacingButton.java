

package src.gui;


// Own imports
import src.tools.swing.Button;
import src.tools.swing.IOBorder;


// Java imports
import java.awt.Insets;


/**
 * Button that has the look and feel of Grepolis.
 * 
 * @author Kaj Wortel (0991586)
 */
public class RacingButton
        extends Button {
    
    public RacingButton(String name) {
        this(name, new Insets(5, 5, 5, 5));
    }
    
    public RacingButton(String name, Insets in) {
        /*
        super(name, "BUTTON_BACK",
                new IOBorder("BUTTON_CORNERS",
                        "BUTTON_BARS", in
                )
        );*/
        // tmp
        super(name, "BUTTON_001_BACK",
                new IOBorder("BUTTON_001_CORNERS",
                        "BUTTON_001_BARS", in
                )
        );
        
        //setForeground(new Color(225, 225, 200));
    }
}
