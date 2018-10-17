
package src.gui.menuList;


// Own imports
import src.tools.observer.ObsInterface;
import src.tools.observer.Observer;


// Java imports
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import javax.swing.BorderFactory;


/**
 * Abstract class for menu items of {@link MenuLister}.
 * 
 * @author Kaj Wortel
 */
public abstract class MenuItem
        extends MenuBar
        implements Observer {
    
    /**-------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    protected static class MenuItemDivider
            extends MenuBar.DefaultDivider {
        
        protected MenuItemDivider(int offset) {
            super(offset, 3);
        }
        
    }
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    private MenuHeader header;
    private int index;
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    public MenuItem(String[] names) {
        super(names);
        setBorder();
    }
    
    public MenuItem(Contents[] contents) {
        super(contents);
        setBorder();
    }
    
    public MenuItem(List<Contents> contents) {
        super(contents);
        setBorder();
    }
    
    public MenuItem(String[] names, Integer[] offsets) {
        super(names, offsets);
        setBorder();
    }
    
    public MenuItem(Contents[] contents, Integer[] offsets) {
        super(contents, offsets);
        setBorder();
    }
    
    public MenuItem(List<Contents> contents, List<Integer> offsets) {
        super(contents, offsets);
        setBorder();
    }
    
    /**
     * Sets the border on initialization.
     */
    private void setBorder() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    public Divider createDivider(int offset) {
        return new MenuItemDivider(offset);
    }
    
    /**
     * @return the index of this menu item.
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Sets the index of this menu item in the list.
     * @param index 
     */
    public void setIndex(int index) {
        this.index = index;
        repaint();
    }
    
    /**
     * @return {@code true} if the index of this item is even.
     */
    public boolean isEven() {
        return (getIndex() % 2) == 0;
    }
    
    /**
     * @return the current header.
     */
    public MenuHeader getHeader() {
        return header;
    }
    
    /**
     * Attaches the given header.
     * 
     * @param header header to attach.
     */
    public void attachHeader(MenuHeader header) {
        if (this.header != null) detachHeader();
        this.header = header;
        if (header != null) {
            header.addObserver(this);
            setOffsets(header.getOffsets());
        }
    }
    
    /**
     * Detaches the menu item from the header.
     * Now no more updates from this header are accepted,
     * including offset changes.
     */
    public void detachHeader() {
        if (header != null) header.deleteObserver(this);
        header = null;
    }
    
    @Override
    public void update(ObsInterface source, Object obj) {
        if (!(source instanceof MenuHeader)) return;
        if (!obj.getClass().isArray()) return;
        Object[] arr = (Object[]) obj;
        if (arr.length < 2) return;
        
        if ("OFFSETS_CHANGED".equals(arr[0])) {
            if (!(arr[1] instanceof Integer[]) &&
                    !(arr[2] instanceof Integer[])) return;
            Integer[] offsets = (Integer[]) arr[1];
            setOffsets(offsets);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isEven()) g.setColor(new Color(255, 255, 255, 150));
        else g.setColor(new Color(0, 0, 0, 0));
        
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    
}
