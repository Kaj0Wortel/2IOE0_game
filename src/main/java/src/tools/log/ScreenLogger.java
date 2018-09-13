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

package src.tools.log;


// Own imports

import src.GS;
import src.tools.MultiTool;
import src.tools.event.Key;
import src.tools.font.FontLoader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

// Java imports


/**
 * Logs the data to a GUI.
 * @author Kaj Wortel (0991586)
 */
public class ScreenLogger
        extends DefaultLogger {
    // The spacing on the left side.
    final protected static int SPACING = 5;
    
    // Default title.
    final protected static String DEFAULT_TITLE = "Logger";
    
    // GUI needed for the logger.
    private static JFrame frame;
    private JPanel panel;
    private JCheckBox checkBox;
    private JScrollPane scroll;
    private JTextArea text;
    
    /**
     * Constructor.
     * 
     * @param title the title of the frame.
     * @param actionMap mapping from the keys to runnable functions.
     *     When a key is pressed, the corresponding function is executed.
     * @param key when only one action is required, this is the key that
     *     invokes the action.
     * @param run when only one action is required, this is the action that
     *     is invoked.
     */
    public ScreenLogger() {
        this(DEFAULT_TITLE);
    }
    
    public ScreenLogger(String title) {
        this(title, null);
    }
    
    public ScreenLogger(Key key, Runnable run) {
        this(DEFAULT_TITLE, key, run);
    }
    
    public ScreenLogger(String title, Key key, Runnable run) {
        this(title, MultiTool.createMap(key, run));
    }
    
    public ScreenLogger(Map<Key, Runnable> map) {
        this(DEFAULT_TITLE, map);
    }
    
    public ScreenLogger(String title, Map<Key, Runnable> map) {
        frame = new JFrame(title);
        frame.setLayout(null);
        Rectangle windowSize = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getMaximumWindowBounds();
        
        frame.setSize((int) (windowSize.getWidth() / 3),
                (int) windowSize.getHeight());
        frame.setLocation((int) windowSize.getWidth() - frame.getWidth(), 0);
        frame.setUndecorated(true);
        
        panel = new JPanel(null);
        frame.add(panel);
        panel.setBackground(Color.BLACK);
        
        checkBox = new JCheckBox("Auto scolling");
        checkBox.setSelected(true);
        panel.add(checkBox);
        checkBox.setBackground(Color.BLACK);
        checkBox.setForeground(Color.WHITE);
        checkBox.setLocation(SPACING, 0);
        checkBox.setSize(150, 20);
        checkBox.setOpaque(true);
        
        text = new JTextArea();
        text.setLocation(SPACING, SPACING);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setBackground(Color.BLACK);
        text.setForeground(Color.GREEN);
        
        SwingUtilities.invokeLater(() -> {
            text.setFont(FontLoader.getLocalFont("cousine" + GS.FS
                    + "Cousine-Regular.ttf").deriveFont(13F));
        });
        
        scroll = new JScrollPane(text);
        panel.add(scroll);
        scroll.setLocation(SPACING, checkBox.getHeight() + SPACING);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants
                .VERTICAL_SCROLLBAR_ALWAYS);
        
        if (map != null) {
            KeyListener kl = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    Runnable r = map.get(new Key(e, false));
                    if (r != null) r.run();
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                    Runnable r = map.get(new Key(e, true));
                    if (r != null) r.run();
                }
            };
            text.addKeyListener(kl);
            checkBox.addKeyListener(kl);
        }
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Insets in = frame.getInsets();
                    panel.setSize(frame.getWidth() - in.left - in.right,
                            frame.getHeight() - in.top - in.bottom);
                    
                    scroll.setSize(panel.getWidth() - SPACING,
                            panel.getHeight() - checkBox.getHeight() - SPACING);
                });
            }
        });
        
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) { }
            @Override
            
            public void insertUpdate(DocumentEvent e) { }
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    @Override
    protected void writeText(String msg) {
        text.append(msg);
        
        if (checkBox.isSelected()) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = scroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });
        }
    }
    
    @Override
    protected void flush() { }
    
    @Override
    protected void close() { }
    
    
    // tmp
    public static void main(String[] args) {
        // To load the GS class
        System.out.println(GS.FS); 
        
        
        tmpThread(1).start();
        tmpThread(2).start();
        tmpThread(3).start();
        tmpThread(4).start();
        tmpThread(5).start();
        tmpThread(6).start();
        tmpThread(7).start();
        tmpThread(8).start();
        MultiTool.sleepThread(2000);
        Logger.write("          |");
        Logger.write("MMMMMMMMMM|");
    }
    
    public static Thread tmpThread(int val) {
        return new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    Logger.write("Thread = " + val + ", i = " + i);
                }
            }
        };
    }
    
}
