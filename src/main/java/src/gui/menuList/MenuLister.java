
package src.gui.menuList;


// Own imports
import src.gui.menuList.MenuBar.Contents;
import src.tools.MultiTool;
import src.tools.MultiTool.BoolEvaluator;
import src.tools.observer.HashObservableInterface;


// Java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;


/**
 * Class for making a nice lister.
 * 
 * @author Kaj Wortel
 */
public class MenuLister<Item extends MenuItem>
        extends JPanel
        implements HashObservableInterface, Iterable<Item> {
    
    final private JScrollPane scroll;
    final private JPanel itemPanel;
    
    private MenuHeader header;
    private List<Item> items = new ArrayList<>();
    private int headerHeight = 35;
    private int itemHeight = 40;
    
    private int itemsPerPage = 20;
    private int page = 1;
    
    
    /**-------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Constructor.
     * 
     * Creates an empty menu lister without header.
     */
    public MenuLister() {
        this((MenuHeader) null);
    }
    
    /**
     * Constructor.
     * 
     * Creates an empty menu lister with a header
     * with the given header names.
     * 
     * @param headerNames
     * @param sizes 
     */
    public MenuLister(String[] headerNames) {
        this(headerNames, MultiTool.<Integer[]>createObject(() -> {
            Integer[] sizes = new Integer[headerNames.length];
            Arrays.fill(sizes, 100);
            return sizes;
        }));
    }
    
    public MenuLister(String[] headerNames, Integer[] sizes) {
        this(new MenuHeader(headerNames, sizes));
    }
    
    /**
     * Constructor.
     * 
     * Creates an empty menu lister with a header
     * with the given contents.
     * 
     * @param contents
     * @param sizes 
     */
    public MenuLister(Contents[] contents) {
        this(contents, MultiTool.<Integer[]>createObject(() -> {
            Integer[] sizes = new Integer[contents.length];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = 100 * (i + 1);
            }
            return sizes;
        }));
    }
    
    public MenuLister(Contents[] contents, Integer[] offsets) {
        this(new MenuHeader(contents, offsets));
    }
    
    public MenuLister(MenuHeader header) {
        super(null);
        
        scroll = new JScrollPane(itemPanel = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() {
                return getSize();
            }
        });
        
        scroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        
        scroll.getViewport().setLayout(null);
        scroll.getViewport().addChangeListener((ChangeEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                updateItemLocs();
            });
        });
        add(scroll);
        
        SwingUtilities.invokeLater(() -> {
            setHeader(header);
        });
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateItemLocs();
    }
    
    /**
     * Updates the location of the header and the items.
     */
    protected void updateItemLocs() {
        if (header != null) header.setSize(getWidth(), headerHeight);
        if (scroll == null) return;
        
        if (header == null) {
            scroll.setBounds(0, 0,
                    getWidth(), getHeight());
            
        } else if (header != null) {
            scroll.setBounds(0, header.getHeight(),
                    getWidth(), getHeight() - header.getHeight());
        }
        
        if (items != null) {
            synchronized(items) {
                int begin = itemsPerPage * (page - 1);
                int end = itemsPerPage * page;
                
                // Set the size and location of the visible items.
                List<Item> shownList = getShownItemList();
                for (int i = begin; i < shownList.size() && i < end; i++) {
                    shownList.get(i)
                            .setBounds(0, itemHeight * (i - begin),
                                    itemPanel.getWidth(), itemHeight);
                }
                
                // This part should be executed later since the scroll pane
                // must update its scroll bars first.
                SwingUtilities.invokeLater(() -> {
                    if (itemPanel != null) {
                        JScrollBar bar = scroll.getVerticalScrollBar();
                        int width = scroll.getWidth() - (bar.isVisible()
                                ? bar.getWidth()
                                : 0);
                        // (is last page ? items/page
                        //    - (#items that can fit on all pages - #items)
                        //    : item/page) * item height.
                        int height = (page == getTotalPages()
                                ? (1 - page) * itemsPerPage
                                + getShownItemList().size()
                                : itemsPerPage) * itemHeight;
                        itemPanel.setSize(width, height);
                    }
                });
            }
        }
    }
    
    /**
     * @return the height of each item.
     */
    public int getItemHeight() {
        return itemHeight;
    }
    
    /**
     * Sets the height of each item.
     * 
     * @param height the new height.
     */
    public void setItemHeight(int height) {
        this.itemHeight = height;
        updateItemLocs();
    }
    
    /**
     * Reloads the header and all items.
     */
    public void reload() {
        if (header != null) header.reload();
        if (items != null) {
            synchronized(items) {
                for (Item item : items) {
                    item.reload();
                }
            }
        }
    }
    
    /**
     * Sets the given header as header.
     * 
     * @param header 
     */
    public void setHeader(MenuHeader header) {
        if (this.header != null) {
            remove(header);
            header.setMenuLister(null);
        }
        
        if ((this.header = header) != null) {
            header.setMenuLister(this);
            synchronized(items) {
                for (Item item : items) {
                    item.attachHeader(header);
                }
            }
            
            header.setLocation(0, 0);
            header.setSize(getWidth(), headerHeight);
            
            header.setOpaque(isOpaque());
            header.setBackground(getBackground());
            header.setForeground(getForeground());
            header.setFont(getFont());
            
            add(header);
        }
        
        updateItemLocs();
    }
    
    /**
     * Adds the given item.
     * 
     * @param item 
     */
    public void addItem(Item item) {
        int oldPages;
        
        synchronized(items) {
            oldPages = getTotalPages();
            if (item != null) {
                items.add(item);
                if (isFilterShown()) filteredItems.add(item);
                item.setMenuLister(this);
                item.setIndex(items.size() - 1);
                item.attachHeader(header);
            
                item.setOpaque(isOpaque());
                item.setBackground(getBackground());
                item.setForeground(getForeground());
                item.setFont(getFont());
                
                if (getTotalPages() == page) {
                    itemPanel.add(item);
                }
            }
        }
        
        checkPage(oldPages);
        updateItemLocs();
    }
    
    /**
     * Adds all given items.
     * 
     * @param newItems the new items to be added.
     */
    public void addAllItems(Collection<Item> newItems) {
        int oldPages;
        
        synchronized(items) {
            oldPages = getTotalPages();
            
            int add = (oldPages == page
                    ? itemsPerPage * page - getShownItemList().size()
                    : 0);
            
            items.addAll(newItems);
            if (isFilterShown()) filteredItems.addAll(newItems);
            
            for (Item item : newItems) {
                item.setMenuLister(this);
                item.setIndex(items.size() - 1);
                item.attachHeader(header);
                
                item.setOpaque(isOpaque());
                item.setBackground(getBackground());
                item.setForeground(getForeground());
                item.setFont(getFont());
                
                if (add > 0) {
                    itemPanel.add(item);
                    item.setIndex(itemsPerPage - add--);
                }
            }
        }
        
        checkPage(oldPages);
        updateItemLocs();
    }
    
    /**
     * Removes the given item from the list.
     * More exacly, removes the first occurance of the given item
     * from the lister.
     * 
     * @param item the item to remove.
     * @return {@code true} iff the item was remved. False otherwise.
     */
    public boolean removeItem(Item item) {
        boolean found = false;
        int oldPages;
        
        synchronized(items) {
            if (item == null) return false;
            oldPages = getTotalPages();
            
            for (int i = 0; i < items.size(); i++) {
                Item curItem = items.get(i);
                
                if (found) {
                    curItem.setIndex(i);
                    
                } else if (item.equals(curItem)) {
                    found = true;
                    i--;
                    curItem.detachHeader();
                    curItem.setMenuLister(null);
                    itemPanel.remove(item);
                    if (isFilterShown()) filteredItems.remove(item);
                }
            }
        }
        
        checkPage(oldPages);
        updateItemLocs();
        return found;
    }
    
    /**
     * Removes the item at the given index.
     * 
     * @param i the index of the item to be removed.
     * @return the removed item.
     * @throws IndexOutOfBoundsException iff
     *     the given index in not in the correct range.
     */
    public Item removeItem(int index)
            throws IndexOutOfBoundsException {
        int oldPages;
        Item item;
        
        synchronized(items) {
            oldPages = getTotalPages();
            removeItemRange(getShownItemList());
            
            item = items.remove(index);
            if (isFilterShown()) filteredItems.remove(item);
            
            if (item != null) {
                item.detachHeader();
                item.setMenuLister(null);
                itemPanel.remove(item);
                
                // todo: invallid setting of index.
                List<Item> shownItems = getShownItemList();
                for (int i = index; i < shownItems.size(); i++) {
                    shownItems.get(i).setIndex(i);
                }
            }
            
            addItemRange(getShownItemList());
        }
        
        checkPage(oldPages);
        updateItemLocs();
        return item;
    }
    
    /**
     * Removes all given items.
     */
    public void removeAllItems(Collection<Item> removeItems) {
        if (removeItems == null) return;
        int oldPages = getTotalPages();
        synchronized(items) {
            removeItemRange(getShownItemList());
            
            items.removeAll(removeItems);
            if (isFilterShown()) filteredItems.removeAll(removeItems);
            
            for (Item item : removeItems) {
                if (item == null) continue;
                item.detachHeader();
                item.setMenuLister(null);
                itemPanel.remove(item);
            }
            
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setIndex(i);
            }
            
            changePage(Math.min(getTotalPages(), page));
            addItemRange(getShownItemList());
        }
        
        checkPage(oldPages);
        updateItemLocs();
    }
    
    /**
     * Removes all items.
     */
    public void removeAllItems() {
        int oldPages;
        
        synchronized(items) {
            oldPages = getTotalPages();
            for (Item item : items) {
                item.detachHeader();
                item.setMenuLister(null);
                itemPanel.remove(item);
            }
            
            items.clear();
            if (isFilterShown()) filteredItems.clear();
        }
        
        checkPage(oldPages);
        updateItemLocs();
    }
    
    /**
     * @param i the index of the item.
     * @return the item at the given index.
     * @throws IndexOutOfBoundsException iff
     *     the given index in not in the correct range.
     */
    public Item getItem(int i)
            throws IndexOutOfBoundsException {
        return items.get(i);
    }
    
    /**
     * @return the list containing all items in the list, in order.
     */
    public List<Item> getItems() {
        return items;
    }
    
    /**
     * @param item the item to search for.
     * @return the index of the given item. {@code -1} if no such item exists.
     */
    public int getIndexOf(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) return i;
        }
        
        return -1;
    }
    
    /**
     * @return the number of items in this menu lister.
     */
    public int countItems() {
        return items.size();
    }
    
    /**
     * @return the list containing all items of this menu lister.
     * 
     * Note of caution:
     * This list should not be modified externally.
     */
    public List<Item> getItemList() {
        return items;
    }
    
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
    
    @Override
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);
        if (header != null) header.setOpaque(opaque);
        if (itemPanel != null) itemPanel.setOpaque(opaque);
        if (scroll != null) {
            scroll.setOpaque(opaque);
            scroll.getViewport().setOpaque(opaque);
        }
        
        if (items != null) {
            for (MenuItem item : items) {
                item.setOpaque(opaque);
            }
        }
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (header != null) header.setBackground(color);
        if (itemPanel != null) itemPanel.setBackground(color);
        if (scroll != null) {
            scroll.setBackground(color);
            scroll.getViewport().setBackground(color);
        }
        
        if (items != null) {
            for (MenuItem item : items) {
                item.setBackground(color);
            }
        }
    }
    
    @Override
    public void setForeground(Color color) {
        super.setBackground(color);
        if (header != null) header.setForeground(color);
        if (itemPanel != null) itemPanel.setForeground(color);
        if (scroll != null) {
            scroll.setForeground(color);
            scroll.getViewport().setForeground(color);
        }
        
        if (items != null) {
            for (MenuItem item : items) {
                item.setForeground(color);
            }
        }
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (header != null) header.setFont(font);
        if (itemPanel != null) itemPanel.setFont(font);
        if (scroll != null) {
            scroll.setFont(font);
            scroll.getViewport().setFont(font);
        }
        
        if (items != null) {
            for (MenuItem item : items) {
                item.setFont(font);
            }
        }
    }
    
    /**
     * Deletes all selected items on the screen.
     */
    public void deleteSelection() {
        if (items == null) return;
        synchronized(items) {
            List<Item> removeItems = new ArrayList<>();
            for (Item item : items) {
                if (!(item instanceof SelectableItem)) continue;
                if (((SelectableItem) item).isSelected())
                    removeItems.add(item);
            }
            removeAllItems(removeItems);
        }
        
        if (header != null && header instanceof SelectableItem) {
            ((SelectableItem) header).setSelected(false);
        }
    }
    
    /**
     * @return a list containing all selected items.
     */
    public List<Item> getSelection() {
        List<Item> selected = new ArrayList<>(0);
        if (items == null) return selected;
        synchronized(items) {
            int begin = itemsPerPage * (page - 1);
            int end = itemsPerPage * page;
            for (int i = begin; i < items.size() && i < end; i++) {
                Item item = items.get(i);
                if (!(item instanceof SelectableItem)) continue;
                if (((SelectableItem) item).isSelected()) selected.add(item);
            }
        }
        
        return selected;
    }
    
    /**
     * @return the amount of selected items.
     */
    public int countSelection() {
        if (items == null) return 0;
        synchronized(items) {
            int counter = 0;
            int begin = itemsPerPage * (page - 1);
            int end = itemsPerPage * page;
            for (int i = begin; i < items.size() && i < end; i++) {
                Item item = items.get(i);
                if (!(item instanceof SelectableItem)) continue;
                if (((SelectableItem) item).isSelected()) counter++;
            }
            return counter;
        }
    }
    
    /**
     * Scrolls to the top of the city list.
     */
    public void toTop() {
        if (scroll == null) return;
        JScrollBar bar = scroll.getVerticalScrollBar();
        bar.setValue(0);
    }
    
    /**
     * Scrolls to the bottom of the city list.
     */
    public void toBottom() {
        if (scroll == null) return;
        JScrollBar bar = scroll.getVerticalScrollBar();
        bar.setValue(Integer.MAX_VALUE);
    }
    
    /**
     * @return the amount of items.
     */
    public int length() {
        return items.size();
    }
    
    
    /**-------------------------------------------------------------------------
     * Item controll functions.
     * -------------------------------------------------------------------------
     */
    private boolean useFilter = false;
    private BoolEvaluator<Item> filter;
    private List<Item> filteredItems = new ArrayList<Item>();
    
    
    /**
     * Adds all items of the given list between the given indices
     * to the item panel.
     * 
     * @param begin the starting index.
     * @param end the ending index.
     * @param list the list to get the items from.
     * 
     * Note that if {@code begin <= end} then no items will be added.
     */
    private void addItemRange(List<Item> list) {
        addItemRange(itemsPerPage * (page - 1), itemsPerPage * page, list);
    }
    
    private void addItemRange(int begin, int end, List<Item> list) {
        for (int i = begin; i < list.size() && i < end; i++) {
            Item item = list.get(i);
            itemPanel.add(item);
            item.setIndex(i - begin);
        }
    }
    
    /**
     * Removes all items of the given list between the given indices
     * to the item panel.
     * 
     * @param begin the starting index.
     * @param end the ending index.
     * @param list the list to get the items from.
     * 
     * Note that if {@code begin <= end} then no items will be removed.
     */
    private void removeItemRange(List<Item> list) {
        removeItemRange(itemsPerPage * (page - 1), itemsPerPage * page, list);
    }
    
    private void removeItemRange(int begin, int end, List<Item> list) {
        for (int i = begin; i < list.size() && i < end; i++) {
            itemPanel.remove(list.get(i));
        }
    }
    
    /**
     * Updates the items that are made visible/invisible because of
     * page changing and/or items per page changing.
     * 
     * @param oldItemsPerPage
     * @param oldPage
     */
    private void updateItemsPaging(int oldItemsPerPage, int oldPage) {
        synchronized(items) {
            int beginOld = oldItemsPerPage * (oldPage - 1);
            int endOld = oldItemsPerPage * oldPage;
            
            removeItemRange(beginOld, endOld, getShownItemList());
            addItemRange(getShownItemList());
        }
        
        updateItemLocs();
    }
    
    /**
     * @return the total amount of pages there are.
     */
    public int getTotalPages() {
        return Math.max(1, (int) (Math.ceil(
                getShownItemList().size() / (double) itemsPerPage)));
    }
    
    /**
     * @return the items that are shown per page.
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }
    
    /**
     * Sets the amount of items per page.
     * Also resets the current page to 0.
     */
    public void setItemsPerPage(int amt) {
        if (amt < 0) throw new IllegalArgumentException(
                "Expected an amount > 0, but found: " + amt);
        synchronized(items) {
            int oldItems = itemsPerPage;
            int oldPage = page;
            int oldMaxPages = getTotalPages();
            itemsPerPage = amt;
            changePage(1);
            checkPage(oldMaxPages);
            updateItemsPaging(oldItems, oldPage);
        }
    }
    
    /**
     * Private method to change the page and notify the observers accordingly.
     * No other actions are taken besides those two.
     * 
     * @param newPage the new page number.
     */
    private void changePage(int newPage) {
        int oldPage = page;
        page = newPage;
        
        if (oldPage != newPage) {
            setChanged();
            notifyObservers(new Object[] {"PAGE_CHANGED", oldPage, page});
        }
    }
    
    /**
     * Checks whether there was a page change.
     * If so, notify the observers.
     * 
     * @param oldPages the previous amount of pages.
     */
    private void checkPage(int oldPages) {
        int newPages = getTotalPages();
        
        if (newPages != oldPages) {
            setChanged();
            notifyObservers(new Object[] {"TOTAL_PAGES_CHANGED",
                oldPages, newPages});
        }
    }
    
    /**
     * @return the current page number.
     */
    public int getPage() {
        System.out.println(page);
        return page;
    }
    
    /**
     * @return the list that should be used for showing items.
     */
    private List<Item> getShownItemList() {
        return (useFilter ? filteredItems : items);
    }
    
    /**
     * Sets the page number.
     * 
     * @param page the new page number. Must be between 1 and the maximum
     *     amount of page numbers.
     */
    public void setPage(int page) {
        int max = getTotalPages();
        if (page < 0 || page > max) throw new IllegalArgumentException(
                "Expected a page number between 1 and " + max
                        + ", but found: " + page);
        
        synchronized(items) {
            int oldPage = this.page;
            changePage(page);
            updateItemsPaging(itemsPerPage, oldPage);
        }
    }
    
    /**
     * Sets the next page as current page.
     */
    public void nextPage() {
        setPage(page + 1);
    }
    
    /**
     * @return true iff the current page is not the last page.
     */
    public boolean hasNextPage() {
        return page + 1 <= getTotalPages();
    }
    
    /**
     * Sets the previous page as current page.
     */
    public void prevPage() {
        setPage(page - 1);
    }
    
    /**
     * @return true iff the current page is not the first page.
     */
    public boolean hasPrevPage() {
        return page > 1;
    }
    
    /**
     * Sorts the items with the given comparator.
     * 
     * @param comp the comparator to sort the items.
     */
    public void sort(Comparator<Item> comp) {
        synchronized(items) {
            removeItemRange(getShownItemList());
            Collections.sort(getShownItemList(), comp);
            addItemRange(getShownItemList());
            
            updateItemLocs();
        }
    }
    
    /**
     * Sets, applies and shows the given filter.
     * 
     * @param filter the new filter.
     */
    public void filter(BoolEvaluator<Item> filter) {
        synchronized(items) {
            setFilter(filter);
            if (!useFilter) showFilter(true);
            else applyFilter();
        }
    }
    
    /**
     * Sets the given filter.
     * 
     * @param filter the new filter
     */
    public void setFilter(BoolEvaluator<Item> filter) {
        synchronized(items) {
            this.filter = filter;
        }
    }
    
    /**
     * Applies the filter on the item list.
     * This function clears the filtered items and recalculates
     * the items.
     * If no filter is applied or if the filter is not shown,
     * then no action is taken.
     * Also resets the page to 1.
     */
    public void applyFilter() {
        synchronized(items) {
            if (filter == null || !isFilterShown()) return;
            removeItemRange(filteredItems);
            
            filteredItems.clear();
            for (Item item : items) {
                if (filter.evaluate(item)) filteredItems.add(item);
            }
            
            changePage(1);
            addItemRange(filteredItems);
        }
        
        updateItemLocs();
    }
    
    /**
     * Re-applies the filters to the already filtered list.
     * If no filter is applied or if the filter is not shown,
     * then no action is taken.
     * Also resets the page to 1.
     */
    public void refilter() {
        synchronized(items) {
            if (filter == null || !isFilterShown()) return;
            
            removeItemRange(filteredItems);
            
            List<Item> remove = new ArrayList<Item>();
            for (int i = 0; i < filteredItems.size(); i++) {
                Item item = filteredItems.get(i);
                if (!filter.evaluate(item)) remove.add(item);
            }
            
            changePage(1);
            filteredItems.removeAll(remove);
            
            addItemRange(filteredItems);
        }
        
        updateItemLocs();
    }
    
    /**
     * Adds the given filter to the current filter.
     * If there is no current filter, sets the given filter as current.
     * 
     * @param addFilter 
     */
    public void addFilter(BoolEvaluator<Item> addFilter) {
        synchronized(items) {
            if (filter == null) filter = addFilter;
            else {
                filter = (Item item) -> {
                    return filter.evaluate(item) && addFilter.evaluate(item);
                };
            }
        }
    }
    
    /**
     * @return {@code true} if the filtered items are currently shown.
     *     {@code false} otherwise.
     */
    public boolean isFilterShown() {
        return useFilter;
    }
    
    /**
     * Sets which view should be shown: the default view or the filtered view.
     * 
     *  - If the view is equal to the current view, no action is taken.
     *  - If the view is changed to the filtered view, then a clean
     *    filtered view is calculated and shown.
     *  - If the view is changeed to the default view, then the filtered view
     *    is cleared and the default view will be shown.
     * 
     * In both cases where {@code showFilter != useFilter},
     * the page number is resetted to 1.
     * 
     * @param showFilter whether the filter should be shown.
     */
    public void showFilter(boolean showFilter) {
        synchronized(items) {
            if (showFilter == useFilter) return;
            removeItemRange(getShownItemList());
            
            if (useFilter = showFilter) {
                applyFilter();
                
            } else {
                filteredItems.clear();
                changePage(1);
                addItemRange(getShownItemList());
            }
        }
        
        updateItemLocs();
    }
    
    /**
     * Excecutes an action for each visible item.
     * 
     * @param action the action to be executed.
     */
    public void forEachVisibleItem(Consumer<? super Item> action) {
        synchronized(items) {
            int begin = itemsPerPage * (page - 1);
            int end = itemsPerPage * page;
            
            for (int i = begin; i < items.size() && i < end; i++) {
                action.accept(items.get(i));
            }
        }
    }
    
    
    
    
    
    /*
    public static void main(String[] args) {
        JFrame frame = new JFrame("MenuLister test frame");
        frame.setLayout(null);
        frame.setSize(1000, 800);
        //frame.getContentPane().setBackground(Color.RED);
        
        MenuLister<CityMenuItem> lister = new MenuLister(new CityMenuHeader(
                new Integer[] {
            50, 150, 250, 350, 450, 550, 650, 750, 850
        }));
        lister.setSize(950, 700);
        lister.setLocation(20, 20);
        
        //CityMenuItem cmi = new CityMenuItem(new City("test city"));
        //lister.addItem(cmi);
        lister.setBackground(new Color(240, 240, 138, 255));
        //lister.setOpaque(true);
        
        for (int i = 0; i < 10; i++) {
            lister.addItem(new CityMenuItem(City.createDefault("test city " + i)));
        }
        frame.add(lister);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }
    /**/
    
    
}
