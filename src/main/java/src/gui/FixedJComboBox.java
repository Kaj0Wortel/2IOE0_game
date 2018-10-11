
package src.gui;


// GAT imports


// Java imports
import java.awt.Component;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class FixedJComboBox<E>
        extends JComboBox<E> {
    private boolean opaque = isOpaque();
    
    
    public FixedJComboBox() {
        super();
    }
    
    public FixedJComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
    }
    
    public FixedJComboBox(E[] items) {
        super(items);
    }
    
    public FixedJComboBox(Vector<E> items) {
        super(items);
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        
        Component[] comps = getComponents();
        if (comps != null && comps.length >= 1) {
            Component arrow = comps[0];
            arrow.setSize(20, height);
            arrow.setLocation(width - arrow.getWidth(), 0);
        }
    }
    
    @Override
    public void setOpaque(boolean opaque) {
        //if (this.opaque == opaque) return;
        this.opaque = opaque;
        setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JComponent result = (JComponent) super
                        .getListCellRendererComponent(list, value, index,
                                isSelected, cellHasFocus);
                result.setOpaque(opaque);
                return result;
            }
        });
    }
    
    @Override
    public boolean isOpaque(){
        return opaque;
    }
    
    
}
