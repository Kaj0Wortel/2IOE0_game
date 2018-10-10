
package src.gui.menuList;


// Own imports
import src.tools.ModCursors;
import src.tools.observer.HashObservableInterface;
import src.tools.observer.ObsInterface;


// Java imports
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * The menu header of the menu lister.
 * 
 * @author Kaj Wortel
 */
public class MenuHeader
        extends MenuBar
        implements HashObservableInterface {
    
    /**-------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    /**
     * The default divider class of the menu header.
     */
    public class MenuHeaderDivider
            extends MenuBar.DefaultDivider {
        
        protected MenuHeaderDivider(int offset, int width) {
            super(offset, 5);
            
            MouseAdapter ma = new MouseAdapter() {
                private boolean mousePressed = false;
                private Point prevLoc = null;
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    JFrame frame = (JFrame) SwingUtilities.
                            getWindowAncestor(MenuHeaderDivider.this);
                    if (frame != null) {
                        frame.setCursor(ModCursors.E_RESIZE_CURSOR);
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    JFrame frame = (JFrame) SwingUtilities.
                            getWindowAncestor(MenuHeaderDivider.this);
                    if (frame != null) {
                        frame.setCursor(ModCursors.DEFAULT_CURSOR);
                    }
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        mousePressed = true;
                        prevLoc = getPoint(e);
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        mousePressed = false;
                    }
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (mousePressed) {
                        Point curLoc = getPoint(e);
                        int dx = curLoc.x - prevLoc.x;
                        prevLoc = curLoc;
                        setOffset(getOffset() + dx);
                    }
                }
                
                /**
                 * @param e the mouse event to get from.
                 * @return the point relative to the outer {@link MenuHeader}.
                 */
                private Point getPoint(MouseEvent e) {
                    return SwingUtilities.convertPoint(MenuHeaderDivider.this,
                            e.getPoint(), MenuHeader.this);
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }
        
    }
    
    
    /**
     * Class for placing a checkbox in the item that (de-)selects all
     * items in the lister.
     */
    public static class HeaderSelector
            extends SelectContents {
        
        public HeaderSelector() {
            super();
            
            comp.addActionListener((ActionEvent e) -> {
                MenuBar bar = getMenuBar();
                if (bar == null) return;
                MenuLister lister = bar.getMenuLister();
                if (lister == null) return;
                boolean isSelected = isSelected();
                
                lister.forEachVisibleItem((item) -> {
                    if (!(item instanceof SelectableItem)) return;
                    ((SelectableItem) item).setSelected(isSelected);
                });
            });
        }
        
        
    }
    
    
    /**-------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    public MenuHeader(String[] headerNames, Integer[] sizes) {
        super(headerNames, sizes);
    }
    
    public MenuHeader(Contents[] contents, Integer[] sizes) {
        super(contents, sizes);
    }
    
    public MenuHeader(List<Contents> contents, List<Integer> sizes) {
        super(contents, sizes);
    }
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    protected Divider createDivider(int offset) {
        return new MenuHeaderDivider(offset, 5);
    }
    
    /**
     * Observer for the dividers.
     * 
     * When overriding this function, one should use
     * {@code super.update(source, obj)}
     * in the overridden function to keep the default behavour working.
     */
    @Override
    public void update(ObsInterface source, Object obj) {
        if (!(source instanceof Divider)) return;
        Divider s = (Divider) source;
        
        if (!obj.getClass().isArray()) return;
        Object[] arr = (Object[]) obj;
        if (arr.length < 3 ||
                !(arr[0] instanceof String) ||
                !(arr[1] instanceof Integer) ||
                !(arr[2] instanceof Integer)) return;
        
        List<Contents> cons = getContents();
        List<Divider> divs = getDividers();
        if (cons == null || divs == null) return;
        
        int oldValue = (int) arr[1];
        int newValue = (int) arr[2];
        if ("OFFSET_CHANGED".equals(arr[0]) ||
                "WIDTH_CHANGED".equals(arr[0])) {
            int dx = 0;
            
            boolean found = false;
            for (int loc = 0; loc < divs.size(); loc++) {
                if (found) {
                    Divider d = divs.get(loc);
                    d.setSilentOffset(d.getOffset() + dx);
                }
                
                if (divs.get(loc).equals(source)) {
                    // Check if the new offset is allowed.
                    // Check for max window size.
                    if (newValue > getWidth() - s.getWidth()) {
                        s.setSilentOffset(newValue
                                = getWidth() - s.getWidth());
                        
                        // Check for min window size.
                    } else if (newValue < 0) {
                        s.setSilentOffset(newValue = 0);
                    }
                    
                    // Check for previous divider (if available).
                    if (loc != 0 && divs.size() > 1) {
                        Divider prev = divs.get(loc - 1);
                        int min = prev.getOffset()
                                + prev.getWidth();
                        
                        if (newValue < min) {
                            s.setSilentOffset(newValue = min);
                        }
                    }   
                    
                    // Set the delta value.
                    dx = newValue - oldValue;
                    
                    // Set found.
                    found = true;
                }
            }
            
            // Update all components.
            updateContents();
            
            // Notify observers.
            setChanged();
            notifyObservers(new Object[] {
                "OFFSETS_CHANGED", getOffsets()
            });
            
        }
    }
    
    
}
