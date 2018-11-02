
package src.gui;


// Own imports
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import src.GS;
import src.tools.io.ImageManager;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import src.tools.event.Key;
import src.tools.ModCursors;
import src.tools.swing.Button;
import src.tools.swing.IOBorder;

// Java imports


/**
 * Main panel class for adding all components.
 * extends {@link JLayeredPane} to enable layering between the panels.
 */
public class MainPanel
        extends JLayeredPane {
    
    // GUI
    final private JFrame frame;
    
    private boolean isInited = false;
    
    private boolean isInFullScreen = false;
    
    private int oldX;
    private int oldY;
    private int oldWidth;
    private int oldHeight;
    
    final private Button exit;
    final private Button fullScreen;
    final private Button minimize;
    final private IOBorder border;
    
    final private SwitchPanel switchPanel;
    
    
    /**
     * Constructor.
     */
    public MainPanel() {
        frame = new JFrame(GS.APP_NAME);
        frame.setLayout(null);
        frame.setIconImage(ImageManager.getImage(GS.FRAME_ICON, 0, 0));
        frame.getContentPane().setBackground(Color.GREEN);
        frame.setUndecorated(true);
        frame.setSize(1250, 750);
        setOpaque(false);
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(frame.getWidth(), frame.getHeight());
                
                if (!GS.isFullScreen()) {
                    oldX = getX();
                    oldY = getY();
                    oldWidth = getWidth();
                    oldHeight = getHeight();
                }
            }
        });
        Insets in = new Insets(40, 10, 10, 10);
        setBorder(border = new IOBorder("CORNERS", "BARS", in));
        
        MouseAdapter ma = new MouseAdapter() {
            final private static int CENTER     = 0b0000_0000;
            final private static int UP         = 0b0000_0001;
            final private static int RIGHT      = 0b0000_0010;
            final private static int DOWN_RIGHT = 0b0000_0110;
            final private static int DOWN       = 0b0000_0100;
            final private static int DOWN_LEFT  = 0b0000_1100;
            final private static int LEFT       = 0b0000_1000;
            
            private int side = 0;
            private int startMouseX = 0;
            private int startMouseY = 0;
            private int startX = 0;
            private int startY = 0;
            private int startWidth = 0;
            private int startHeight = 0;
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (side == CENTER) return;
                
                Point loc = e.getLocationOnScreen();
                int dx = loc.x - startMouseX;
                int dy = loc.y - startMouseY;
                
                if (side == UP) {
                    frame.setLocation(startX + dx, startY + dy);
                    
                } else {
                    int x = startX;
                    int y = startY;
                    int w = startWidth;
                    int h = startHeight;
                    if ((side & RIGHT) != 0) {
                        w += dx;
                    }
                    if ((side & DOWN) != 0) {
                        h += dy;
                    }
                    if ((side & LEFT) != 0) {
                        w -= dx;
                        x += dx;
                    }
                    
                    frame.setBounds(x, y, w, h);
                }
                update();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                Point loc = e.getLocationOnScreen();
                startMouseX = loc.x;
                startMouseY = loc.y;
                side = checkCoords(e.getX(), e.getY());
                if (side != CENTER) {
                    startX = frame.getX();
                    startY = frame.getY();
                    startWidth = frame.getWidth();
                    startHeight = frame.getHeight();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                side = 0;
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int s = checkCoords(e.getX(), e.getY());
                if (s == CENTER || s == UP) {
                    frame.setCursor(ModCursors.DEFAULT_CURSOR);
                    
                } else if (s == LEFT || s == RIGHT) {
                    frame.setCursor(ModCursors.E_RESIZE_CURSOR);
                    
                } else if (s == DOWN) {
                    frame.setCursor(ModCursors.S_RESIZE_CURSOR);
                    
                } else if (s == DOWN_LEFT) {
                    frame.setCursor(ModCursors.SW_RESIZE_CURSOR);
                    
                } else if (s == DOWN_RIGHT) {
                    frame.setCursor(ModCursors.SE_RESIZE_CURSOR);
                }
            }
            
            /**
             * Checks on which side the given coords are on.
             * Works on component-relative coordinates only.
             * 
             * @param x
             * @param y
             * @return the side.
             */
            private int checkCoords(int x, int y) {
                Insets in = getInsets();
                if (x > in.left && x < frame.getWidth() - in.right &&
                        y > in.top && y < frame.getHeight() - in.bottom) {
                    return CENTER;
                    
                } else if (y < in.top) {
                    return UP;
                }
                
                boolean left = (x <= in.left);
                boolean down = (y >= frame.getHeight() - in.bottom);
                boolean right = (x >= frame.getWidth() - in.right);
                
                int result = 0;
                if (left) result |= LEFT;
                if (down) result |= DOWN;
                if (right) result |= RIGHT;
                return result;
            }
        };
        
        frame.addMouseListener(ma);
        frame.addMouseMotionListener(ma);
        
        // Create exit button.
        exit = new Button("", "BUTTONS_EXIT");
        exit.addActionListener((e) -> {
            frame.dispatchEvent(new WindowEvent(
                    frame, WindowEvent.WINDOW_CLOSING));
        });
        add(exit);
        exit.setSize(22, 22);
        
        // Create full screen button.
        fullScreen = new Button("", "BUTTONS_FULL_SCREEN");
        fullScreen.addActionListener((e) -> {
            GS.setFullScreen(true);
        });
        add(fullScreen);
        fullScreen.setSize(22, 22);
        
        // Create minimize button.
        minimize = new Button("", "BUTTONS_MINIMIZE");
        minimize.addActionListener((e) -> {
            minimize.reset();
            minimize.repaint();
            frame.setState(Frame.ICONIFIED);
        });
        add(minimize);
        minimize.setSize(22, 22);
        
        // Set keybindings for exiting full screen.
        getInputMap().put(Key.ESC.toKeyStroke(), "full_screen");
        getActionMap().put("full_screen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GS.setFullScreen(false);
            }
        });
        
        // Create switch panel.
        switchPanel = new SwitchPanel();
        add(switchPanel);
        
        JComponent[] panels = new JComponent[] {
            new StartupPanel(),
            new ConfigKeysPanel()
        };
        
        for (JComponent panel : panels) {
            switchPanel.addPanel(panel);
        }
        switchPanel.setActivePanel(panels[0].getName());
        
        
        SwingUtilities.invokeLater(() -> {
            frame.add(this);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
        
        // Schedule repaint action to ensure correct drawing.
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> {
                update();
                repaint();
            });
        });
        
        isInited = true;
    }
    
    
    /**
     * This method should be called to ensure that the size and
     * location of the frame are correctly shown.
     */
    public void update() {
        // Update full screen settings if needed.
        if (GS.isFullScreen() && !isInFullScreen) {
            isInFullScreen = true;
            
            // Store previous state.
            oldX = frame.getX();
            oldY = frame.getY();
            oldWidth = frame.getWidth();
            oldHeight = frame.getHeight();
            
            // Set in full screen modus.
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(0, 0);
            frame.setSize((int) screenSize.getWidth(),
                    (int) screenSize.getHeight());// -1 is tmp!
            setBorder(null);
            remove(exit);
            remove(fullScreen);
            remove(minimize);
            
        } else if (!GS.isFullScreen() && isInFullScreen) {
            isInFullScreen = false;
            
            frame.setLocation(oldX, oldY);
            frame.setSize(oldWidth, oldHeight);
            setBorder(border);
            add(exit);
            add(fullScreen);
            add(minimize);
        }
        
        SwingUtilities.invokeLater(() -> {
            // Update button location.
            exit.setLocation(getWidth() - exit.getWidth() - 11, 10);
            fullScreen.setLocation(exit.getX() - fullScreen.getWidth() - 6, 10);
            minimize.setLocation(fullScreen.getX() - minimize.getWidth() - 6, 10);
            
            // Set the size of the panel.
            Insets in = getInsets();
            super.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            
            // Set the size and location of the switch panel.
            int w = getWidth() - in.left - in.right;
            int h = getHeight() - in.top - in.bottom;
            
            for (Component comp : getComponents()) {
                if ((comp instanceof JInternalFrame) || comp == exit ||
                        comp == fullScreen || comp == minimize) continue;
                comp.setBounds(in.left, in.top, w, h);
            }

            repaint();
        });
    }
    
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        if (isInited) update();
    }
    
    /**
     * Closes all {@link JInternalFrame}s added to this layered pane.
     */
    public void closeAllInternalFrames() {
        for (Component comp : getComponents()) {
            if (comp instanceof JInternalFrame) {
                comp.setVisible(false);
            }
        }
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (!isInFullScreen) {
            // Store original graphics data.
            Paint paint = g2d.getPaint();
            Font font = g2d.getFont();
            AffineTransform original = g2d.getTransform();
            
            // Paint the text.
            g2d.setPaint(new Color(228, 211, 183));
            g2d.drawString(frame.getTitle(), 50, 25);

            // Paint the image.
            Image img = frame.getIconImage();
            if (img != null) {
                g2d.translate(12, 8);
                g2d.scale(25.0 / img.getWidth(null), 25.0 / img.getHeight(null));
                g2d.drawImage(img, 0, 0, null);
            }

            // Restore original graphics data.
            g2d.setPaint(paint);
            g2d.setFont(font);
            g2d.setTransform(original);
        }
    }
    
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        for (Component comp : getComponents()) {
            if (comp != null) comp.setFont(font);
        }
    }
    
    /**
     * @return the switch panel of the main panel.
     */
    public SwitchPanel getSwitchPanel() {
        return switchPanel;
    }
    
    public void showSwitchPanel(boolean show) {
        if (show) add(switchPanel);
        else remove(switchPanel);
    }
    
    
}
