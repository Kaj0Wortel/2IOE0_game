
package src.gui.menuList;


// Own imports
import src.GS;
import src.gui.EditField;
import src.gui.FixedJComboBox;
import src.gui.RacingButton;
import src.gui.GrepoInternalFrame;
import src.tools.MultiTool;
import src.tools.observer.HashObservableInterface;
import src.tools.observer.Observer;


// Java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


/**
 * Abstract class for menu items of {@link MenuLister}.
 * 
 * @author Kaj Wortel
 */
public abstract class MenuBar
        extends JPanel
        implements HashObservableInterface, Observer {
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    final protected static int DIVIDER_WIDTH = 3;
    
    
    /**-------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    public abstract static class Divider
            extends JPanel
            implements HashObservableInterface {
        
        private int offset;
        private int width;
        
        /**
         * Constructor.
         * 
         * @param offset the offset of the divider.
         * @param width the width of the divider.
         * 
         * Note that the offset is here defined as the center of the
         * divider, and NOT the same as {@link #getX()}.
         */
        public Divider(int offset, int width) {
            super(null);
            
            super.setBounds(this.offset = offset, 0,
                    this.width = width, 0);
        }
        
        /**
         * @return the offset of the divider.
         */
        public int getOffset() {
            return offset;
        }
        
        /**
         * @param offset the new offset of the divider.
         * 
         * Note that the location of the divider is set AFTER the update, 
         * but the offset is set BEFORE the update.
         */
        public void setOffset(int offset) {
            int oldOffset = this.offset;
            setSilentOffset(offset);
            
            if (oldOffset != offset) {
                setChanged();
                notifyObservers(new Object[] {
                    "OFFSET_CHANGED", oldOffset, offset
                });
            }
        }
        
        /**
         * Sets the offset, but doesn't notify it's observers.
         * Use this method only in observers.
         * 
         * @param offset the new offset of the divider.
         */
        protected void setSilentOffset(int offset) {
            this.offset = offset;
        }
        
        @Override
        public int getWidth() {
            return width;
        }
        
        /**
         * Sets the width of the divider.
         * 
         * @param width the new width of the divider.
         */
        public void setWidth(int width) {
            int oldWidth = this.width;
            setSilentWidth(width);
            
            if (oldWidth != width) {
                setChanged();
                notifyObservers(new Object[] {
                    "WIDTH_CHANGED", oldWidth, width
                });
            }
        }
        
        /**
         * Does the same as {@link #setWidth(int,int)}, but does
         * not notify the observers.
         * 
         * @param width the new width of the divider.
         */
        protected void setSilentWidth(int width) {
            this.width = width;
        }
        
        /**
         * Method that is called when the bounds of the outer
         * {@link MenuBar} class is changed.
         * 
         * @param bar the {@code MenuBar} object the call came from.
         */
        protected void update(MenuBar bar) {
            setBounds(offset - width / 2, 0,
                    width, bar.getHeight());
        }
        
        
    }
    
    
    /**
     * Default implementation of the {@link Divider} class.
     */
    public static class DefaultDivider
            extends Divider {
        
        public DefaultDivider(int offset) {
            this(offset, 5);
        }
        
        public DefaultDivider(int offset, int width) {
            super(offset, width);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        
    }
    
    
    /**
     * Abstract class for the contents of the menu item.
     */
    protected abstract static class Contents<T>
            extends JPanel {
        
        private MenuBar menuBar;
        private Observer obs;
        
        public Contents() {
            super(null);
        }
        
        /**
         * @return the contents of this object.
         */
        public abstract T getContents();
        
        /**
         * Reloads the contents.
         */
        public abstract void reload();
        
        /**
         * @return the current menu bar.
         */
        final public MenuBar getMenuBar() {
            return menuBar;
        }
        
        /**
         * @param menuBar the new current menu bar.
         */
        final public void setMenuBar(MenuBar menuBar) {
            if (this.menuBar != null && obs != null)
                this.menuBar.deleteObserver(obs);
            this.menuBar = menuBar;
            if (menuBar != null && obs != null) menuBar.addObserver(obs);
        }
        
        /**
         * Dispatches an event to the current menu bar.
         * 
         * @param obj argument of the event.
         */
        final protected void dispatchEvent(Object[] obj) {
            if (menuBar != null) menuBar.dispatchEvent(obj);
        }
        
        /**
         * @param obs the new observer of the menu bar.
         */
        final protected void setMenuBarObserver(Observer obs) {
            if (this.obs != null && menuBar != null)
                menuBar.deleteObserver(this.obs);
            this.obs = obs;
            if (menuBar != null) menuBar.addObserver(obs);
        }
        
        
    }
    
    
    /**
     * Empty contents class. Can be used as spacing or end filler.
     */
    public static class EmptyContents
            extends Contents<Void> {
        
        @Override
        public Void getContents() {
            return (Void) null;
        }
        
        @Override
        public void reload() { }
        
        
    }
    
    
    /**
     * Generic implementation of the {@link Contents} class.
     * In this way, a component can be added in an easy way.
     * @param <C> the class of the component that is added.
     */
    public abstract static class JComponentContents<C extends JComponent>
            extends Contents<C> {
        
        protected C comp;
        
        public JComponentContents(C comp) {
            this.comp = comp;
            add(comp);
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
        
        @Override
        public C getContents() {
            return comp;
        }
        
        
    }
    
    
    /**
     * Simple implementation of the contents class.
     * Shows the given text in a label as contents.
     */
    public static class TextContents
            extends JComponentContents<JLabel> {
        
        public TextContents(String name) {
            super(new JLabel(name));
            comp.setFont(getFont());
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            comp.setVerticalAlignment(SwingConstants.CENTER);
        }
        
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            comp.setBounds(0, 0, width, height);
        }
        
        /**
         * @return the text of the label.
         */
        public String getText() {
            return comp.getText();
        }
        
        /**
         * Sets the text of the label.
         * 
         * @param text the text of the label
         */
        public void setText(String text) {
            comp.setText(text);
        }
        
        @Override
        public void reload() { } 
        
        
    }
    
    
    /**
     * Simple implementation of the contents class.
     * Shows a checkbox as contents.
     */
    public static class SelectContents
            extends JComponentContents<JCheckBox>
            implements SelectableItem {
        
        /**
         * Constructor.
         */
        public SelectContents() {
            this(false);
        }
        
        /**
         * Constructor.
         * Creates a JCheckBox that is 1.4 times as big as the default one.
         * 
         * @param selected the default selection option for the checkbox.
         */
        protected SelectContents(boolean selected) {
            super(new JCheckBox());
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            comp.setSelected(selected);
        }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            comp.setBounds(0, 0, width, height);
        }
        
        /**
         * @return whether {@code comp} is selected.
         */
        @Override
        public boolean isSelected() {
            return (comp != null ? comp.isSelected() : false);
        }
        
        /**
         * @param selected the new selection of {@code comp}.
         */
        @Override
        public void setSelected(boolean selected) {
            if (comp != null) comp.setSelected(selected);
        }
        
        @Override
        public void reload() { }
        
        
    }
    
    /**
     * Simple implementation of the contents class.
     * Shows a combo box.
     */
    public abstract static class SelectorContents<V>
            extends JComponentContents<FixedJComboBox<V>> {
        private int spacing;
        
        public SelectorContents(int spacing, V... values) {
            super(values == null
                    ? new FixedJComboBox<V>()
                    : new FixedJComboBox<V>(values));
            this.spacing = spacing;
            if (values == null) reload();
        }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            comp.setBounds(spacing, spacing,
                    width - 2*spacing, height - 2*spacing);
        }
        
        /**
         * Sets the selected item of the combo box.
         * 
         * @param item the item to be selected.
         * 
         * @see FixedJComboBox#setSelectedItem(Object).
         */
        public void setSelectedItem(V item) {
            comp.setSelectedItem(item);
        }
        
        
        /**
         * @return the selected item of the combo box.
         * 
         * @see FixedJComboBox#getSelectedItem().
         */
        public V getSelectedItem() {
            return (V) comp.getSelectedItem();
        }
        
        @Override
        public void setOpaque(boolean opaque) {
            super.setOpaque(opaque);
            if (comp == null) return;
            comp.setOpaque(opaque);
        }
        
        
    }
    
    
    /**
     * Generic implementation of the {@link Contents} class.
     * In this way, multiple component can be added in an easy way.
     * @param <C> the class of the component that is added.
     */
    public abstract static class MultiJComponentContents<C extends JComponent>
            extends Contents<C[]>{
        
        protected C[] comps;
        protected int spacing;
        
        
        /**
         * Constructor.
         * 
         * @param comps
         * @param spacing 
         */
        public MultiJComponentContents(C[] comps, int spacing) {
            this.comps = comps;
            this.spacing = spacing;
            MultiTool.forEach(comps, (C comp) -> {
                if (comp != null) add(comp);
            });
        }
        
        
        /**
         * @return the spacing between the components.
         */
        public int getSpacing() {
            return spacing;
        }
        
        /**
         * @param spacing the new spacing between the components.
         */
        public void setSpacing(int spacing) {
            this.spacing = spacing;
            repaint();
        }
        
        @Override
        public void setFont(Font font) {
            super.setFont(font);
            if (comps != null) {
                MultiTool.forEach(comps, (C comp) -> {
                    if (comp != null) comp.setFont(font);
                });
            }
        }
        
        @Override
        public void setBackground(Color color) {
            super.setBackground(color);
            if (comps != null) {
                MultiTool.forEach(comps, (C comp) -> {
                    if (comp != null) comp.setBackground(color);
                });
            }
        }
        
        @Override
        public void setForeground(Color color) {
            super.setForeground(color);
            if (comps != null) {
                MultiTool.forEach(comps, (C comp) -> {
                    if (comp != null) comp.setForeground(color);
                });
            }
        }
        
        @Override
        public void setOpaque(boolean opaque) {
            super.setOpaque(opaque);
            if (comps != null) {
                MultiTool.forEach(comps, (C comp) -> {
                    if (comp != null) comp.setOpaque(opaque);
                });
            }
        }
        
        @Override
        public C[] getContents() {
            return comps;
        }
        
        
    }
    
    
    /**
     * Simple implementation of the contents class.
     * Shows a group of text fields.
     */
    public abstract static class FormContents
            extends MultiJComponentContents<EditField> {
        
        public FormContents(int spacing, String... conts) {
            super(new EditField[conts.length], spacing);
            
            for (int i = 0; i < comps.length; i++) {
                add(comps[i] = new EditField(conts[i]));
            }
        }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            if (comps == null) return;
            
            int amt = comps.length;
            int compWidth = (width - spacing) / amt - spacing;
            int compHeight = (height - 2*spacing);
            for (int i = 0; i < amt; i++) {
                JTextField field = comps[i];
                if (field == null) continue;
                
                field.setBounds(spacing + (compWidth + spacing) * i, spacing,
                        compWidth, compHeight);
            }
        }
        
        
    }
    
    
    /**
     * Generic implementation of the {@link Contents} class.
     * This class adds the provided component to an aditional internal
     * frame that is opened and closed by a button.
     * 
     * @param <C> the class of the component that is added.
     */
    public abstract static class FrameContents<C extends JComponent>
            extends Contents<C> {
        
        final protected GrepoInternalFrame frame;
        final protected RacingButton button;
        protected C comp;
        protected int spacing;
        
        public FrameContents(String buttonText, C comp) {
            this(buttonText, comp, 10);
        }
        
        public FrameContents(String buttonText, C comp, int spacing) {
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
            
            SwingUtilities.invokeLater(() -> {
                updateFrame();
            });
        }
        
        /**
         * Updates the bounds and parent of the frame.
         */
        private void updateFrame() {
            if (GS.mainPanel == null) return;
            if (frame.getParent() != GS.mainPanel) GS.mainPanel.add(frame);
            // Set the size of the frame.
            Insets in = frame.getInsets();
            frame.setSize(comp.getWidth() + in.left + in.right,
                    comp.getHeight() + in.top + in.bottom);
                
            // Set the location of the frame.
            Point loc = new Point(getWidth() - frame.getWidth(),
                    getHeight());
            frame.setLocation(SwingUtilities.convertPoint(
                    this, loc, GS.mainPanel));
            frame.toFront();
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
        
        @Override
        public C getContents() {
            return comp;
        }
        
        /**
         * @return the button in the content field.
         */
        public RacingButton getButton() {
            return button;
        }
        
        /**
         * @return the internal frame used for the separate actions.
         */
        public GrepoInternalFrame getInternalFrame() {
            return frame;
        }
        
        
    }
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    private MenuLister lister;
    
    protected List<Divider> dividers = new ArrayList<Divider>();
    protected List<Contents> contents = new ArrayList<Contents>();
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    /**
     * Constructors without default width for each component.
     */
    public MenuBar(String[] names) {
        this(names, MultiTool.<Integer[]>createObject(() -> {
            Integer[] offsets = new Integer[names.length - 1];
            Arrays.fill(offsets, 0);
            return offsets;
        }));
    }
    
    public MenuBar(Contents[] contents) {
        this(contents, MultiTool.<Integer[]>createObject(() -> {
            Integer[] offsets = new Integer[contents.length - 1];
            Arrays.fill(offsets, 0);
            return offsets;
        }));
    }
    
    public MenuBar(List<Contents> contents) {
        this(contents, MultiTool.<List<Integer>>createObject(() -> {
            List<Integer> offsets = new ArrayList<>(contents.size() - 1);
            for (int i = 0; i < offsets.size(); i++) {
                offsets.set(i, 0);
            }
            return offsets;
        }));
    }
    
    /**
     * Constructor.
     * 
     * @param names the names of the fields.
     * @param offsets list containing the getOffsets of each divider,
     *     in order from left to right.
     * 
     * Note that since each offset element corresponds with each divider
     * element and there is one divider less than contents.
     */
    public MenuBar(String[] names, Integer[] offsets) {
        this(MultiTool.<List<Contents>>createObject(() -> {
            List<Contents> list = new ArrayList<>();
            MultiTool.forEach(names, (String name) -> {
                list.add(new TextContents(name));
            });
            return list;
        }), false, MultiTool.addAll(new ArrayList<Integer>(), offsets));
    }
    
    /**
     * Constructor.
     * 
     * @param contents list containing the bar contents.
     * @param offsets list containing the getOffsets of each divider,
     *     in order from left to right.
     * 
     * Note that since each offset element corresponds with each divider
     * element and there is one divider less than contents.
     */
    public MenuBar(Contents[] contents, Integer[] offsets) {
        this(MultiTool.addAll(new ArrayList<Contents>(), contents), false,
                MultiTool.addAll(new ArrayList<Integer>(), offsets));
    }
    
    /**
     * Constructor.
     * 
     * @param contents list containing the bar contents.
     * @param offsets list containing the getOffsets of each divider,
     *     in order from left to right.
     * 
     * Note that since each offset element corresponds with each divider
     * element and there is one divider less than contents.
     */
    public MenuBar(List<Contents> contents, List<Integer> offsets) {
        this(contents, true, offsets);
    }
    
    /**
     * Private constructor.
     * 
     * @param contents the contents to be added.
     * @param clone whether to clone the contents list.
     * @param offsets list containing the getOffsets of each divider,
     *     in order from left to right.
     */
    private MenuBar(List<Contents> contents, boolean clone,
            List<Integer> offsets) {
        super(null);
        if (contents.size() != offsets.size() + 1)
            throw new IllegalArgumentException(
                    "The amount of contents (" + contents.size()
                            + ") is not equal to the amount of offsets ("
                            + offsets.size() + ") + 1.");
        
        if (clone) this.contents = new ArrayList<>(contents);
        else this.contents = contents;
        
        for (int i = 0; i < offsets.size(); i++) {
            Divider div = createDivider(offsets.get(i));
            dividers.add(div);
            add(div);
        }
        
        for (Contents c : contents) {
            add(c);
        }
        
        SwingUtilities.invokeLater(() -> {
            dividers.forEach((div) -> {
                div.addObserver(this);
                div.setOpaque(false);
            });
            
            updateContents();
        });
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates the dividers used in the bar.
     * If the implementer of this class wants to create use different
     * divider class, then this funcion should be overridden and the
     * new divider should be returned.
     * 
     * Note that returning {@code null} will result in a failing constructor.
     * 
     * @param offset the offset where the divider will be placed.
     * @return a {@link Divider}.
     */
    protected Divider createDivider(int offset) {
        return new DefaultDivider(offset);
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        updateContents();
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        
        if (dividers != null) dividers.forEach((Divider div) -> {
            div.setFont(font);
        });
        
        if (contents != null) contents.forEach((Contents c) -> {
            c.setFont(font);
        });
    }
    
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        
        if (dividers != null) dividers.forEach((Divider div) -> {
            div.setBackground(color);
        });
        
        if (contents != null) contents.forEach((Contents c) -> {
            c.setBackground(color);
        });
    }
    
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        
        if (dividers != null) dividers.forEach((Divider div) -> {
            div.setForeground(color);
        });
        
        if (contents != null) contents.forEach((Contents c) -> {
            c.setForeground(color);
        });
    }
    
    @Override
    public void setOpaque(boolean opaque) {
        super.setOpaque(opaque);
        
        if (dividers != null) dividers.forEach((Divider div) -> {
            div.setOpaque(opaque);
        });
        
        if (contents != null) contents.forEach((Contents c) -> {
            c.setOpaque(opaque);
        });
    }
    
    /**
     * @return the list containing all dividers.
     */
    public List<Divider> getDividers() {
        return dividers;
    }
    
    /**
     * @return the list containing all contents.
     */
    public List<Contents> getContents() {
        return contents;
    }
    
    /**
     * @return an array containing the getOffsets of the dividers
     of this menu bar.
     */
    public Integer[] getOffsets() {
        Integer[] offsets = new Integer[dividers.size()];
        for (int i = 0; i < dividers.size(); i++) {
            offsets[i] = dividers.get(i).getOffset();
        }
        
        return offsets;
    }
    
    /**
     * Updates the size of the contents and dividers.
     * 
     * @param offsets the new offsets of the dividers of this menu bar.
     */
    public void setOffsets(Integer[] offsets) {
        for (int i = 0; i < offsets.length && i < dividers.size(); i++) {
            dividers.get(i).setOffset(offsets[i]);
        }
        
        updateContents();
        
        setChanged();
        notifyObservers(new Object[] {"OFFSETS_CHANGED", offsets});
    }
    
    /**
     * Does the same as {@link #setOffsets(Integer[])},
     * but doesn't notify the observers.
     * 
     * @see #setOffsets(Integer[])
     */
    protected void setSilentOffsets(Integer[] offsets) {
        List<Divider> divs = getDividers();
        
        for (int i = 0; i < offsets.length && i < dividers.size(); i++) {
            dividers.get(i).setSilentOffset(offsets[i]);
        }
        
        updateContents();
    }
    
    /**
     * Updates the size and location of the contents.
     */
    public void updateContents() {
        dividers.forEach((div) -> div.update(this));
        
        for (int i = 0; i < contents.size(); i++) {
            int x = (i == 0
                    ? 0
                    : dividers.get(i - 1).getX()
                    + dividers.get(i - 1).getWidth());
            
            int endX = (i == contents.size() - 1
                    ? getWidth()
                    : dividers.get(i).getX());
            int width = endX - x;
            contents.get(i).setBounds(x, 0, width, getHeight());
        }
    }
    
    /**
     * Sets the menu lister this item is part of.
     * 
     * @param lister the new menu lister.
     */
    protected void setMenuLister(MenuLister lister) {
        this.lister = lister;
    }
    
    /**
     * @return the menu lister this item is part of.
     */
    public MenuLister getMenuLister() {
        return lister;
    }
    
    /**
     * Used for internal event dispatching.
     * 
     * @param obj 
     */
    private void dispatchEvent(Object[] obj) {
        setChanged();
        notifyObservers(obj);
    }
    
    /**
     * Reloads all contents of this menu bar.
     */
    public void reload() {
        if (contents == null) return;
        for (Contents c : contents) {
            if (c != null) c.reload();
        }
    }
    
    
}
