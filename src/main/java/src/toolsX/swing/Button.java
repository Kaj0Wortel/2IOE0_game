/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2018 by Kaj Wortel - all rights reserved                    *
 * Contact: kaj.wortel@gmail.com                                             *
 *                                                                           *
 * This file is part of the tools project, which can be found on github:     *
 * https://github.com/Kaj0Wortel/tools                                       *
 *                                                                           *
 * It is allowed to use, (partially) copy and modify this file               *
 * in any way for private use only by using this header.                     *
 * It is not allowed to redistribute any (modifed) versions of this file     *
 * without my permission.                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package toolsX.swing;


// Own imports

import src.tools.io.ImageManager;
import tools.observer.HashObservableInterface;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class Button
        extends AbstractButton
        implements HashObservableInterface {
    /**
     * Enum for keeping track of the current state.
     */
    public static enum State {
        DEFAULT(0),
        ROLL_OVER(1),
        PRESSED(2),
        DISABLED(3);
        
        final private int val;
        
        private State(int i) {
            val = i;
        }
        
        private int getVal() {
            return val;
        }
        
    }
    
    private State state = State.DEFAULT;
    private String backID;
    
    
    /**
     * Constructors.
     * 
     * @param name the displayed text on the button. Default is {@code ""}.
     * @param backID the id of the image used as background.
     *     Default is {@code null}.
     * @param border the border of the button. Default is {@code null}.
     */
    public Button() {
        this("");
    }
    
    public Button(String name) {
        this(name, null, null);
    }
    
    public Button(String name, Border border) {
        this(name, null, border);
    }
    
    public Button(String name, String backID) {
        this(name, backID, null);
    }
    
    public Button(String name, String backID, Border border) {
        super();
        setLayout(null);
        
        setText(name);
        this.backID = backID;
        setBorder(border);
        
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (state != State.DISABLED) {
                    State old = state;
                    state = State.ROLL_OVER;
                    setChanged();
                    notifyObservers(new Object[] {
                        "STATE_CHANGED", old, state
                    });
                    repaint();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (state != State.DISABLED) {
                    State old = state;
                    state = State.PRESSED;
                    setChanged();
                    notifyObservers(new Object[] {
                        "STATE_CHANGED", old, state
                    });
                    fireActionPerformed(new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED, e.paramString(),
                            e.getWhen(), e.getModifiers()));
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (state != State.DISABLED) {
                    if (state == State.PRESSED) {
                        State old = state;
                        state = State.ROLL_OVER;
                        setChanged();
                        notifyObservers(new Object[] {
                            "STATE_CHANGED", old, state
                        });
                        updateBorderState();
                        repaint();
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (state != State.DISABLED) {
                    State old = state;
                    state = State.DEFAULT;
                    setChanged();
                    notifyObservers(new Object[] {
                        "STATE_CHANGED", old, state
                    });
                    repaint();
                }
            }
        });
    }
    
    /**
     * @return the state of the button.
     */
    public State getState() {
        return state;
    }
    
    /**
     * Sets the state of the button
     * @param newState 
     */
    public void setState(State newState) {
        State old = state;
        state = newState;
        setChanged();
        notifyObservers(new Object[] {
            "STATE_CHANGED", old, state
        });
    }
    
    /**
     * Resets the button to the default state.
     * This action might be required when a window is iconified.
     */
    public void reset() {
        state = State.DEFAULT;
    }
    
    /**
     * Updates the state of the border.
     */
    private void updateBorderState() {
        Border b = getBorder();
        if (b != null && b instanceof tools.swing.IOBorder) {
            tools.swing.IOBorder ioB = (tools.swing.IOBorder) b;
            ioB.setState(state.getVal());
        }
    }
    
    /**
     * Sets the id of the background.
     * Setting to {@code null} means no image background.
     * 
     * @param id the new id.
     */
    public void setBackgroundID(String id) {
        backID = id;
    }
    
    /**
     * @return the background id.
     */
    public String getBackgroundID() {
        return backID;
    }
    
    @Override
    public void repaint() {
        updateBorderState();
        super.repaint();
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background.
        BufferedImage back = ImageManager.getImage(backID, state.getVal(), 0);
        if (back != null) {
            // Store original transformation.
            AffineTransform original = g2d.getTransform();
            Insets in = getInsets();
            
            g2d.translate(in.left, in.top);
            g2d.scale(((double) getWidth() - in.left - in.right)
                            / back.getWidth(),
                    ((double) getHeight() - in.top - in.bottom)
                            / back.getHeight());
            
            g2d.drawImage(back, 0, 0, null);
            
            // Restore original transformation.
            g2d.setTransform(original);
        }
        
        paintText(g2d);
    }
    
    /**
     * Paints the text on this panel.
     * 
     * @param g the graphics object.
     */
    protected void paintText(Graphics2D g2d) {
        // Draw text in the middle of the button
        // Note that the text is centered on the middle of the text,
        // and not the base line when considering vertical allignment.
        FontMetrics fm = g2d.getFontMetrics();
        //Rectangle2D bounds = fm.getStringBounds(getText(), g2d);
        double textWidth = fm.stringWidth(getText());
        double textHeight = fm.getHeight();
        double ascent = fm.getAscent();
        
        g2d.setFont(getFont());
        g2d.drawString(getText(),
                (int) ((getWidth() - textWidth) / 2),
                (int) ((getHeight() - textHeight) / 2 + ascent));
        
    }
    
    // tmp
    public static void main(String[] args) {
        // Register image sheets.
        final String FS = System.getProperty("file.separator");
        
        ImageManager.registerSheet("menu" + FS
                + "IOBorder_img_TYPE_001.png", "MENU_CORNERS",
                0, 0, 64, 32, 16, 16);
        ImageManager.registerSheet("menu" + FS
                + "IOBorder_img_TYPE_001.png", "MENU_SIDES",
                0, 32, 64, 64, 16, 16);
        ImageManager.registerSheet("menu" + FS
                + "button_background.png", "BUTTON_BACK",
                16, 16);
        
        JFrame frame = new JFrame("button test frame");
        frame.setLayout(null);
        frame.setSize(700, 700);
        frame.getContentPane().setBackground(Color.RED);
        
        Button button = new Button("test", "BUTTON_BACK", new tools.swing.IOBorder(
                "MENU_CORNERS", "MENU_SIDES", new Insets(5, 5, 5, 5)
        ));
        button.setLocation(50, 50);
        button.setSize(200, 150);
        frame.add(button);
        button.addActionListener((e) -> {
            System.out.println("pressed");
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    
}
