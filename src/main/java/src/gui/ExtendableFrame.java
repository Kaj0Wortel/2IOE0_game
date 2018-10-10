
package src.gui;


// GAT imports
import src.GS;


// Java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * 
 * 
 * @author Kaj Wortel
 */
public abstract class ExtendableFrame<V extends JComponent>
        extends JPanel {
    final protected static int DEFAULT_SPACING = 20;
    
    final protected RacingButton button;
    final protected GrepoInternalFrame frame;
    
    protected V comp;
    protected int spacing = 10;
    
    
    /**
     * Constructor.
     * @param comp
     * @param buttonText 
     * 
     * @see #ExtendableFrame(JComponent, String, int).
     */
    public ExtendableFrame(V comp, String buttonText) {
        this(comp, buttonText, DEFAULT_SPACING);
    }
    
    /**
     * Constructor.
     * Creates a new extendable frame with the given component as contents.
     * 
     * @param comp the contents of the frame.
     * @param buttonText the text for the button.
     * @param spacing the spacing around the button and the component.
     */
    public ExtendableFrame(V comp, String buttonText, int spacing) {
        super(null);
        this.spacing = spacing;
        
        frame = new GrepoInternalFrame();
        frame.add(this.comp = comp);
        comp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFrame();
            }
        });
        
        button = new RacingButton(buttonText);
        button.addActionListener((ActionEvent e) -> {
            frame.setVisible(!frame.isVisible());
            updateFrame();
            repaint();
        });
        add(button);
        
        comp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFrame();
            }
        });
        
        updateFrame();
    }
    
    /**
     * Updates the bounds and parent of the frame.
     */
    protected void updateFrame() {
        if (GS.mainPanel == null) return;
        if (frame.getParent() != GS.mainPanel) GS.mainPanel.add(frame);
        // Set the size of the frame.
        Insets in = frame.getInsets();
        frame.setSize(comp.getWidth() + in.left + in.right,
                comp.getHeight() + in.top + in.bottom);
        
        frame.setLocation(SwingUtilities.convertPoint(
                this, calcFramePosition(), GS.mainPanel));
        frame.toFront();
    }
    
    /**
     * Default implementation of frame placement.
     */
    protected Point calcFramePosition() {
        return new Point(0, getHeight());
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        button.setBounds(spacing, spacing,
                width - 2*spacing, height - 2*spacing);
        if (frame.isVisible()) {
            updateFrame();
        }
    }
    
    /**
     * @return the spacing of this frame.
     */
    public int getSpacing() {
        return spacing;
    }
    
    /**
     * @param spacing the new spacing.
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
        updateFrame();
        setSize(getX(), getY());
    }
    
    /**
     * @return the current button to activate the frame
     */
    public RacingButton getButton() {
        return button;
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (comp != null) comp.setFont(font);
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (comp != null) comp.setBackground(color);
    }
    
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        if (comp != null) comp.setForeground(color);
    }
    
    @Override
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);
        if (comp != null) comp.setOpaque(opaque);
    }
    
    
}
