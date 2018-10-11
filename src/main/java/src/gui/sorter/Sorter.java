
package src.gui.sorter;


// GAT imports
import src.gui.ExtendableFrame;
import src.gui.FixedJComboBox;
import src.gui.RacingButton;
import src.gui.menuList.MenuItem;


// Java imports
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * 
 * 
 * @author Kaj Wortel
 */
public abstract class Sorter<Item extends MenuItem>
        extends ExtendableFrame<Sorter.SorterContents<Item>> {
    
    final private static int SPACING = 5;
    
    
    /**
     * Rule class for representing possible sorting rules to be displayed.
     * 
     * @param <Item> the menu item used in the rule.
     */
    protected static class Rule<Item> {
        final private String name;
        final private Comparator<? super Item> compare;
        
        protected Rule(String name, Comparator<? super Item> compare) {
            this.name = name;
            this.compare = compare;
        }
        
        public Comparator<? super Item> getComparator() {
            return compare;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        
    }
    
    
    /**
     * Contents class for the frame.
     * @param <Item> the item used for the comparators and the ruleset.
     */
    protected static class SorterContents<Item>
            extends JPanel {
        
        final private static int BUTTON_HEIGHT = 30;
        
        
        /**
         * Item class for depicting a rule.
         */
        private class ItemPanel
                extends JPanel {
            
            final private static String DEFAULT_ORDER = "A..Z";
            final private static String REVERSED_ORDER = "Z..A";
            
            final private static int TYPE_LABEL_WIDTH = 40;
            final private static int TYPE_BOX_WIDTH = 100;
            final private static int ORDER_LABEL_WIDTH = 50;
            final private static int ORDER_BOX_WIDTH = 60;
            final private static int DELETE_SIZE = 20;
            
            final private static int RULE_WIDTH = TYPE_LABEL_WIDTH
                    + TYPE_BOX_WIDTH + ORDER_LABEL_WIDTH + ORDER_BOX_WIDTH
                    + DELETE_SIZE + 5*Sorter.SPACING;
            final private static int RULE_HEIGHT = 30;
            
            final private JLabel typeLabel;
            final private FixedJComboBox<Rule> typeBox;
            
            final private JLabel orderLabel;
            final private FixedJComboBox<String> orderBox;
            
            final private RacingButton delete;
            
            
            private ItemPanel() {
                super(null);
                
                // Labels.
                typeLabel = new JLabel("Item:");
                add(typeLabel);
                
                orderLabel = new JLabel("Order:");
                add(orderLabel);
                
                // Combo boxes.
                typeBox = new FixedJComboBox<>();
                for (Rule rule : rules) {
                    typeBox.addItem(rule);
                }
                add(typeBox);
                
                orderBox = new FixedJComboBox<>();
                orderBox.addItem(DEFAULT_ORDER);
                orderBox.addItem(REVERSED_ORDER);
                add(orderBox);
                
                delete = new RacingButton("X", new Insets(3, 3, 3, 3));
                delete.setForeground(new Color(255, 100, 100));
                delete.addActionListener((ActionEvent e) -> {
                    SorterContents.this.items.remove(this);
                    SorterContents.this.remove(this);
                    SorterContents.this.update();
                    SorterContents.this.repaint();
                });
                add(delete);
                
                setSize(RULE_WIDTH, RULE_HEIGHT);
                
                // Update properties.
                setOpaque(SorterContents.this.isOpaque());
                setBackground(SorterContents.this.getBackground());
                setForeground(SorterContents.this.getForeground());
                setFont(SorterContents.this.getFont());
            }
            
            @Override
            public void setBounds(int x, int y, int width, int height) {
                super.setBounds(x, y, width, height);
                
                typeLabel.setBounds(0, 0,
                        TYPE_LABEL_WIDTH, height);
                typeBox.setBounds(TYPE_LABEL_WIDTH + SPACING, 0,
                        TYPE_BOX_WIDTH, height);
                
                orderLabel.setBounds(TYPE_LABEL_WIDTH + TYPE_BOX_WIDTH
                        + 2*SPACING, 0,
                        ORDER_LABEL_WIDTH, height);
                orderBox.setBounds(TYPE_LABEL_WIDTH + TYPE_BOX_WIDTH
                        + ORDER_LABEL_WIDTH + 3*SPACING, 0,
                        ORDER_BOX_WIDTH, height);
                        
                delete.setBounds(width - DELETE_SIZE,
                        (height - DELETE_SIZE) / 2,
                        DELETE_SIZE, DELETE_SIZE);
            }
            
            
            @Override
            public void setOpaque(boolean opaque) {
                super.setOpaque(opaque);
                
                for (Component comp : getComponents()) {
                    if (comp instanceof JComponent)
                        ((JComponent) comp).setOpaque(opaque);
                }
            }
            
            @Override
            public void setBackground(Color color) {
                super.setBackground(color);
                
                for (Component comp : getComponents()) {
                    if (comp instanceof JComponent)
                        ((JComponent) comp).setBackground(color);
                }
            }
            
            @Override
            public void setForeground(Color color) {
                super.setForeground(color);
                
                for (Component comp : getComponents()) {
                    if (comp instanceof JComponent)
                        ((JComponent) comp).setForeground(color);
                }
            }
            
            @Override
            public void setFont(Font font) {
                super.setFont(font);
                
                for (Component comp : getComponents()) {
                    if (comp instanceof JComponent)
                        ((JComponent) comp).setFont(font);
                }
            }
            
            /**
             * @return a fresh comparator which compares according to the
             *     the current settings of this single item.
             */
            public Comparator<Item> createComparator() {
                boolean reversed = (REVERSED_ORDER.equals(
                        orderBox.getSelectedItem()));
                Rule rule = (Rule) typeBox.getSelectedItem();
                if (rule == null) return null;
                return (reversed
                        ? rule.getComparator().reversed()
                        : rule.getComparator());
            }
            
            
        }
        
        /**---------------------------------------------------------------------
         * Variables SorterContents.
         * ---------------------------------------------------------------------
         */
        final private List<Rule<Item>> rules;
        
        final private List<ItemPanel> items;
        
        final private RacingButton addButton;
        final private RacingButton sortButton;
        
        
        /**
         * Constructor.
         * 
         * @param rules the rule set that should be used for sorting.
         */
        protected SorterContents(List<Rule<Item>> rules) {
            super(null);
            this.rules = rules;
            
            items = new ArrayList<>();
            
            addButton = new RacingButton("Add rule");
            addButton.addActionListener((ActionEvent e) -> {
                ItemPanel panel = new ItemPanel();
                items.add(panel);
                add(panel);
                update();
            });
            add(addButton);
            
            sortButton = new RacingButton("Sort");
            add(sortButton);
        }
        
        
        /**
         * Updates the bounds of this panel and all its childern.
         */
        protected void update() {
            setSize(ItemPanel.RULE_WIDTH + 2*SPACING, 
                    2*SPACING + (ItemPanel.RULE_HEIGHT + SPACING) * items.size()
                            + BUTTON_HEIGHT);
            
            for (int i = 0; i < items.size(); i++) {
                ItemPanel panel = items.get(i);
                panel.setBounds(SPACING,
                        SPACING + (ItemPanel.RULE_HEIGHT + SPACING) * i,
                        panel.getWidth(), ItemPanel.RULE_HEIGHT);
            }
            
            addButton.setBounds((getWidth() + SPACING) / 2,
                    SPACING + (ItemPanel.RULE_HEIGHT + SPACING) * items.size(),
                    (getWidth() - 3*SPACING) / 2,
                    ItemPanel.RULE_HEIGHT);
            
            sortButton.setBounds(SPACING,
                    SPACING + (ItemPanel.RULE_HEIGHT + SPACING) * items.size(),
                    (getWidth() - 3*SPACING) / 2,
                    ItemPanel.RULE_HEIGHT);
        }
        
        /**
         * @return a comparator that represents all rules in this class.
         *     {@code null} if there are no rules available.
         */
        public Comparator<Item> createComparator() {
            Comparator<Item> comps = null;
            for (ItemPanel panel : items) {
                if (comps == null) comps = panel.createComparator();
                else comps.thenComparing(panel.createComparator());
            }
            return comps;
        }
        
        @Override
        public void setOpaque(boolean opaque) {
            super.setOpaque(opaque);
            
            for (Component comp : getComponents()) {
                if (comp instanceof JComponent)
                    ((JComponent) comp).setOpaque(opaque);
            }
        }
        
        @Override
        public void setBackground(Color color) {
            super.setBackground(color);
            
            for (Component comp : getComponents()) {
                if (comp instanceof JComponent)
                    ((JComponent) comp).setBackground(color);
            }
        }
        
        @Override
        public void setForeground(Color color) {
            super.setForeground(color);
            
            for (Component comp : getComponents()) {
                if (comp instanceof JComponent)
                    ((JComponent) comp).setForeground(color);
            }
        }
        
        @Override
        public void setFont(Font font) {
            super.setFont(font);
            
            for (Component comp : getComponents()) {
                if (comp instanceof JComponent)
                    ((JComponent) comp).setFont(font);
            }
        }
        
        
    }
    
    
    /**
     * Constructor.
     * 
     * @param rules list containing the rule set.
     * @param spacing the spacing around the frame. Default is 0.
     */
    public Sorter(List<Rule<Item>> rules) {
        this(rules, 0);
    }
    
    public Sorter(List<Rule<Item>> rules,
            int spacing) {
        
        super(new SorterContents<Item>(rules),
                "Sort options", spacing);
        
        SwingUtilities.invokeLater(() -> {
            updateContents();
        });
    }
    
    /**
     * @return a fresh comparator that compares the items
     *     according to the current settings.
     */
    public Comparator<Item> getComparator() {
        final Comparator<Item> comparators = comp.createComparator();
        
        if (comparators == null) {
            return (Item item1, Item item2) -> {
                return 0;
            };
            
        } else {
            return (Item item1, Item item2) -> {
                return comparators.compare(item1, item2);
            };
        }
    }
    
    /**
     * Updates the contents if there was a significant change.
     */
    public void updateContents() {
        comp.update();
    }
    
    /**
     * @return the sort button.
     */
    public RacingButton getSortButton() {
        return comp.sortButton;
    }
    
    @Override
    protected Point calcFramePosition() {
        return new Point(getWidth() - frame.getWidth(), getHeight());
    }
    
    
}
