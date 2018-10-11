
package src.gui;


// Own imports
import src.tools.io.ImageManager;
import src.tools.observer.HashObservableInterface;


// Java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;


/**
 * 
 * 
 * @author Kaj Wortel & Ger Wortel
 */
public class TabMenuBar
        extends JPanel
        implements HashObservableInterface {
    final private static String TAB_ID = "GREPO_TABS";
    
    /**
     * Enum for the state of the tabs.
     */
    private static enum TabState {
        ACTIVE(0),
        DEFAULT(1),
        PRESSED(2);
        
        final private int val;
        
        private TabState(int val) {
            this.val = val;
        }
        
        public int getVal() {
            return val;
        }
    }
    
    
    /**
     * Class for representing the tabs of the menu.
     */
    protected class Tab
            extends JPanel {
        final private String text;
        private TabState state = TabState.DEFAULT;
        
        /**
         * Constructor.
         * 
         * @param text the text to be shown on the tab.
         */
        protected Tab(String text) {
            super(null);
            this.text = text;
            
            setOpaque(false);
            //setBackground(new Color(0, 0, 0, 0));
            
            addMouseListener(new MouseAdapter() {
                private boolean pressed = false;
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    requestActiveTab(Tab.this);
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    if (state != TabState.ACTIVE) {
                        state = TabState.PRESSED;
                        pressed = true;
                        repaint();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (state == TabState.PRESSED) {
                        state = TabState.DEFAULT;
                        repaint();
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (pressed && state != TabState.ACTIVE) {
                        state = TabState.PRESSED;
                        repaint();
                    }
                }
            });
        }
        
        /**
         * Updates the activity status.
         * @param active the new activity status.
         */
        protected void setActive(boolean active) {
            if (active) state = TabState.ACTIVE;
            else state = TabState.DEFAULT;
            repaint();
        }
        
        /**
         * @return the text of the tab.
         */
        protected String getText() {
            return text;
        }
        
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            BufferedImage[] sheet
                    = ImageManager.getSheet(TAB_ID)[state.getVal()];
            
            // Store original transformation matrix.
            AffineTransform original = g2d.getTransform();
            
            Rectangle[] pos = new Rectangle[] {
                new Rectangle(0, 0,
                        sideSize, getHeight()),
                new Rectangle(sideSize, 0,
                        getWidth() - 2*sideSize, getHeight()),
                new Rectangle(getWidth() - sideSize, 0,
                        sideSize, getHeight())
            };
            
            // Draw images.
            for (int i = 0; i < 3; i++) {
                BufferedImage img = sheet[i];
                Rectangle rec = pos[i];
                
                g2d.translate(rec.x, rec.y);
                g2d.scale(((double) rec.width) / img.getWidth(),
                        ((double) rec.height) / img.getHeight());
                g2d.drawImage(img, 0, 0, null);
                
                // Restore original transformation matrix.
                g2d.setTransform(original);
            }
            
            // Draw text.
            // Draw text in the middle of the button
            // Note that the text is centered on the middle of the text,
            // and not the base line when considering vertical allignment.
            g2d.setFont(TabMenuBar.this.getFont());
            FontMetrics fm = g2d.getFontMetrics();
            double textWidth = fm.stringWidth(getText());
            double textHeight = fm.getHeight();
            double ascent = fm.getAscent();
            
            g2d.drawString(text,
                    (int) ((this.getWidth() - textWidth) / 2),
                    (int) ((this.getHeight() * 0.85 - textHeight) / 2 + ascent));
        }
        
        
    }
    
    
    final private int emptyLeft;
    final private int emptyMiddle;
    final private int emptyRight;
    final private int sideSize;
    private List<Tab> tabs = new ArrayList<Tab>();
    private Tab activeTab = null;
    private int progress = 0;
    
    /**
     * Constructor.
     * 
     * @param emptyLeft the size of the left side that should remain empty.
     * @param emptyMiddle the empty space in the middle between two tabs.
     * @param emptyRight the size of the right side that should remain empty.
     * @param sideSize the size of the sides.
     */
    public TabMenuBar(int emptyLeft, int emptyMiddle, int emptyRight,
            int sideSize) {
        super(null);
        
        this.emptyLeft = emptyLeft;
        this.emptyMiddle = emptyMiddle;
        this.emptyRight = emptyRight;
        this.sideSize = sideSize;
        progress = getWidth() - emptyRight;
        
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }
    
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        
        progress = width - emptyRight;
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            progress -= tab.getWidth();
            tab.setLocation(progress, 0);
            progress -= emptyMiddle;
        }
    }
    
    /**
     * Adds a tab with the given name.
     * 
     * @param text the text of the tab.
     */
    protected void addTab(String text) {
        synchronized(tabs) {
            Tab tab = new Tab(text);
            
            FontRenderContext frc
                    = new FontRenderContext(new AffineTransform(),true,true);     
            Font font = getFont();
            int textWidth = (int) (font.getStringBounds(text, frc).getWidth());
            tab.setSize(textWidth + 2*sideSize, getHeight());
            
            progress -= tab.getWidth();
            tab.setLocation(progress, 0);
            progress -= emptyMiddle;
            
            this.add(tab);
            tabs.add(tab);
            repaint();
        }
    }
    
    /**
     * Function that is invoked to grant a tab activity rights.
     * 
     * @param source the tab that requests activity.
     */
    protected void requestActiveTab(Tab source) {
        if (source == null) return;
        Tab prevActive = activeTab;
        if (activeTab != null) activeTab.setActive(false);
        activeTab = source;
        activeTab.setActive(true);
        
        setChanged();
        notifyObservers(new Object[] {
            (prevActive == null ? null : prevActive.getText()),
            (activeTab == null ? null : activeTab.getText())
        });
    }
    
    /**
     * @param i index of the tab to set active
     */
    public void setActive(int i) {
        requestActiveTab(tabs.get(i));
    }
    
    /**
     * @return the size of the sides.
     */
    public int getSideSize() {
        return sideSize;
    }
    
    /**
     * @return the number of tabs.
     */
    public int getNumTabs() {
        return tabs.size();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        BufferedImage img = ImageManager.getImage(TAB_ID, 3, 0);
        
        // Store original transformation matrix.
        AffineTransform original = g2d.getTransform();
        
        g2d.scale(((double) getWidth()) / img.getWidth(),
                ((double) getHeight()) / img.getHeight());
        g2d.drawImage(img, 0, 0, null);
        
        // Restore original transformation matrix.
        g2d.setTransform(original);
    }
    
    
}
