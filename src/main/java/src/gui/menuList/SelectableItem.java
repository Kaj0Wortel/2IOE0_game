
package src.gui.menuList;


/**
 * Interface to support selectable menu items.
 * 
 * @author Kaj Wortel
 */
public interface SelectableItem {
    /**
     * @return whether this menu item is selected or not.
     */
    public boolean isSelected();
    
    /**
     * @param selected the new selection of the menu item.
     */
    public void setSelected(boolean selected);
    
    
}
