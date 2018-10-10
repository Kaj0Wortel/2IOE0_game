
package src.gui;


// Own imports
import src.gui.menuList.MenuItem;
import src.gui.menuList.MenuLister;
import src.gui.sorter.Sorter;
import src.gui.filter.Filter;


// Java imports
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public abstract class ListingPanel<Item extends MenuItem,
        Lister extends MenuLister<Item>>
        extends DataPanel {
    
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    final private static int BUTTON_WIDTH = 150;
    final private static int BUTTON_HEIGHT = 40;
    final private static int SPACING = 10;
    
    final private static int SMALL_BUTTON_WIDTH = 40;
    final private static int SMALL_BUTTON_HEIGHT = 30;
    final private static int SMALL_SPACING = 15;
    
    final private static int FORM_WIDTH = 75;
    
    
    /**-------------------------------------------------------------------------
     * GUI.
     * -------------------------------------------------------------------------
     */
    final private RacingButton add;
    final private RacingButton delete;
    
    final private RacingButton nextPage;
    final private RacingButton prevPage;
    final private RacingButton goPage;
    final private FixedJComboBox<Integer> itemsPerPage;
    final private JLabel pages;
    final private EditField pageSearch;
    
    final protected Lister lister;
    final protected Filter<Item> filter;
    final protected Sorter<Item> sorter;
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    /**
     * Constructor.
     * 
     * @param lister the lister that is used for listing the items.
     * @param sorter the sorter panel that is used for sorting the items.
     * @param filter the filter panel that is used for filtering the items.
     */
    public ListingPanel(String tabName, Lister lister,
            Sorter<Item> sorter,
            Filter<Item> filter) {
        super(tabName);
        
        this.lister = lister;
        lister.setLocation(SPACING, 2*SPACING + BUTTON_HEIGHT);
        add(lister);
        lister.setOpaque(false);
        lister.setBackground(new Color(255, 225, 160, 255));
        
        add = new RacingButton("Add");
        add.setLocation(SPACING, SPACING);
        add.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        add.addActionListener((ActionEvent e) -> {
            userAddItem();
        });
        add(add);
        
        delete = new RacingButton("Delete selection");
        delete.setLocation(2*SPACING + BUTTON_WIDTH, SPACING);
        delete.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        delete.addActionListener((ActionEvent e) -> {
            int amt = lister.countSelection();
            if (amt <= 0) {
                JOptionPane.showMessageDialog(this,
                        "No  items selected!", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                String msg = "Are you sure that you want to delete " + amt
                        + (amt == 1 ? " item?" : " items?");
                if (JOptionPane.showOptionDialog(this, msg, "Delete items?", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, new Object[] {"Yes", "No"}, "No")
                        == JOptionPane.YES_OPTION) {
                    
                    List<Item> deleted = lister.getSelection();
                    lister.removeAllItems(deleted);
                    SwingUtilities.invokeLater(() -> {
                        revalidate();
                        repaint();
                    });
                    
                    userDeletedAction(deleted);
                }
            }
            
        });
        add(delete);
        
        this.sorter = sorter;
        sorter.setLocation(3*SPACING + 2*BUTTON_WIDTH, SPACING);
        sorter.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        
        sorter.getSortButton().addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                lister.sort(sorter.getComparator());
            });
        });
        sorter.setOpaque(false);
        add(sorter);
        
        this.filter = filter;
        filter.setLocation(4*SPACING + 3*BUTTON_WIDTH, SPACING);
        filter.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        filter.getGenerateButton().addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                lister.filter(filter.getFilter());
            });
        });
        filter.getCancelButton().addActionListener((ActionEvent e) -> {
            lister.showFilter(false);
        });
        filter.setOpaque(false);
        add(filter);
        
        nextPage = new RacingButton("▶");
        nextPage.setSize(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT);
        nextPage.addActionListener((ActionEvent e) -> {
            if (lister.hasNextPage()) lister.nextPage();
        });
        add(nextPage);
        
        prevPage = new RacingButton("◀");
        prevPage.setSize(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT);
        prevPage.addActionListener((e) -> {
            if (lister.hasPrevPage()) lister.prevPage();
        });
        add(prevPage);
        
        pages = new JLabel(" / " + lister.getTotalPages(), SwingConstants.LEFT);
        pages.setSize(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT);
        add(pages);
        
        pageSearch = new EditField("1");
        pageSearch.setSize(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT);
        pageSearch.setHorizontalAlignment(SwingConstants.CENTER);
        add(pageSearch);
        
        lister.addObserver((obs, obj) -> {
            if (obj == null || !obj.getClass().isArray()) return;
            Object[] arr = (Object[]) obj;
            if (arr.length != 3) return;
            if ("PAGE_CHANGED".equals(arr[0])) {
                pageSearch.setText(Integer.toString(lister.getPage()));
                
            } else if ("TOTAL_PAGES_CHANGED".equals(arr[0])) {
                pages.setText(" / " + lister.getTotalPages());
            }
        });
        
        goPage = new RacingButton("Go");
        goPage.setSize(SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT);
        goPage.addActionListener((e) -> {
            try {
                int page = Integer.parseInt(pageSearch.getText());
                if (page <= 0 || page > lister.getTotalPages()) {
                    pageSearch.setError();
                } else {
                    lister.setPage(page);
                }
                
            } catch (NumberFormatException ex) {
                pageSearch.setError();
            }
        });
        add(goPage);
        
        itemsPerPage = new FixedJComboBox<Integer>(new Integer[] {
            13, 25, 50, 100, 200
        });
        itemsPerPage.setOpaque(false);
        itemsPerPage.setSize(FORM_WIDTH, SMALL_BUTTON_HEIGHT);
        itemsPerPage.addActionListener((e) -> {
            Integer value = (Integer) itemsPerPage.getSelectedItem();
            if (value != null) lister.setItemsPerPage(value);
        });
        itemsPerPage.setSelectedItem(25);
        add(itemsPerPage);
        
        
        loadData();
        repaint();
        revalidate();
    }
    
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        lister.setSize(width - 2*SPACING,
                height - BUTTON_HEIGHT - 3*SPACING);
        
        nextPage.setLocation(width - SMALL_SPACING - SMALL_BUTTON_WIDTH,
                SMALL_SPACING);
        prevPage.setLocation(width - 2*SMALL_SPACING - 2*SMALL_BUTTON_WIDTH,
                SMALL_SPACING);
        
        itemsPerPage.setLocation(width - 3*SMALL_SPACING - 2*SMALL_BUTTON_WIDTH
                - FORM_WIDTH, SMALL_SPACING);
        goPage.setLocation(width - 4*SMALL_SPACING - 3*SMALL_BUTTON_WIDTH
                - FORM_WIDTH, SMALL_SPACING);
        pages.setLocation(width - 4*SMALL_SPACING - 4*SMALL_BUTTON_WIDTH
                - FORM_WIDTH, SMALL_SPACING);
        pageSearch.setLocation(width - 4*SMALL_SPACING - 5*SMALL_BUTTON_WIDTH
                - FORM_WIDTH, SMALL_SPACING);
    }
    
    @Override
    final public void loadData() {
        SwingUtilities.invokeLater(() -> {
            loadAction();
            lister.reload();
        });
    }
    
    @Override
    final public void saveData() {
        SwingUtilities.invokeLater(() -> {
            saveAction();
        });
    }
    
    /**
     * Initializes the lister.
     * Can also be used to reset the lister.
     */
    public abstract void loadAction();
    
    /**
     * Saves the data of this panel.
     */
    public abstract void saveAction();
    
    /**
     * Function that is called after removing an item from the list.
     */
    public abstract void userDeletedAction(List<Item> deleted);
            
    /**
     * Function that is called to add an item to the list.
     */
    public abstract void userAddItem();
    
    
}
