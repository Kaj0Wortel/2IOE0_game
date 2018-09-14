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

package src.tools.font;


// Own packages

import src.GS;
import src.tools.MultiTool;
import src.tools.log.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

// Java packages


/**
 * Auto-loads all the nessecary fonts for the application at start-up.
 * 
 * @author Kaj Wortel (0991586)
 */
public class FontLoader {
    final private static String STATIC_PATH = GS.FONT_DIR;
    final public static Hashtable<String, Font> fonts = new Hashtable<>();
    
    // The default font.
    private static Font defaultFont = (Font) UIManager.get("Label.font");
    
    
    /* -------------------------------------------------------------------------
     * Constructor
     * -------------------------------------------------------------------------
     */
    /* 
     * This is a singleton class. No instances should be made.
     */
    @Deprecated
    private FontLoader() { }
    
    
    /* -------------------------------------------------------------------------
     * Functions
     * -------------------------------------------------------------------------
     */
    /**
     * Loads a font from a file.
     * 
     * @param localPath the path of the font file relative to the font directoy.
     * @param path the path to the font file.
     * @param style the style of teh font. Should be one of: {@code Font.PLAIN},
     *     {@code Font.ITALIC}, {@code Font.BOLD} or
     *     {@code Font.BOLD + Font.ITALIC}
     */
    public static Font loadLocalFont(String localPath, int style) {
        return loadFont(STATIC_PATH + localPath, style);
    }
    
    public static Font loadFont(String path, int style) {
        return loadFont(path, style, Font.TRUETYPE_FONT);
    }
    
    protected static Font loadFont(String path, int style, int type) {
        Font font = null;
        
        try (FileInputStream fis = new FileInputStream(path)) {
            //font = Font.createFont(type, fis).deriveFont(style, 12F);
            font = Font.createFont(type, fis).deriveFont(12F);
            fonts.put(path, font);
            
        } catch (FontFormatException | IOException e) {
            Logger.write(e);
        }
        
        return font;
    }
    
    /**
     * @param fontName the name of the font.
     * @return the font.
     */
    public static Font getLocalFont(String localName) {
        return getFont(STATIC_PATH + localName);
    }
    
    public static Font getFont(String fontName) {
        return fonts.get(fontName);
    }
    
    /**
     * Registers the given font.
     * When successfull, the font can be used in the application
     * for e.g. html code.
     * 
     * @param font the font to be registered
     */
    public static boolean registerFont(Font font) {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .registerFont(font);
    }
    
    /**
     * Sets the default font of the application.
     * Should be called BEFORE the initialization of any swing objects
     * to take effect.
     * 
     * @param font the font that should be the default from now on.
     */
    public static void setDefaultFont(Font font) {
        defaultFont = font;
        
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
    }
    
    /**
     * @return the default font.
     */
    public static Font getDefaultFont() {
        return defaultFont;
    }
    
    
    /**
     * Registers all fonts in the fonts folder.
     * 
     * @see #registerFont(Font) for more info about registering a font.
     */
    public static void init() {
        Font[] allFonts = null;
        
        Logger.write(new String [] {
            "",
            "========== START LOADING FONTS =========="
        }, Logger.Type.INFO);
        ArrayList<File[]> files = MultiTool.listFilesAndPathsFromRootDir(
                new File(STATIC_PATH), false);
        
        for (File[] file : files) {
            String fontLoc = file[0].toString();
            
            if (fontLoc.endsWith(".ttf")) {
                String fontString = fontLoc.toLowerCase();
                
                int style = (fontString.contains("bold") ||
                             fontString.endsWith("b.ttf") ||
                             fontString.endsWith("bi.ttf") ||
                             fontString.endsWith("ib.ttf") ? Font.BOLD : 0) |
                    (fontString.contains("italic") ||
                     fontString.contains("it") ||
                     fontString.endsWith("i.ttf") ||
                     fontString.endsWith("bi.ttf") ||
                     fontString.endsWith("ib.ttf") ? Font.ITALIC : 0);
                if (style == 0) style = Font.PLAIN;
                /*
                System.out.println(style == Font.PLAIN ? "PLAIN"
                        : style == Font.BOLD ? "BOLD"
                                : style == Font.ITALIC ? "ITALIC"
                                        : style == (Font.BOLD | Font.ITALIC)
                                                ? "BOLD + ITALIC"
                                                : "ERROR!");
                */
                
                Font font = loadFont(file[0].toString(), style,
                        Font.TRUETYPE_FONT);
                if (font == null) {
                    Logger.write("A null font has been created: "
                            + file[0].toString(), Logger.Type.ERROR);
                    
                } else if (!registerFont(font)) {
                    if (allFonts == null) {
                        allFonts = GraphicsEnvironment
                                .getLocalGraphicsEnvironment().getAllFonts();
                    }
                    
                    boolean isRegistered = false;
                    for (Font checkFont : allFonts) {
                        if (checkFont.getName().equals(font.getName())) {
                            isRegistered = true;
                            break;
                        }
                    }
                    
                    if (isRegistered) {
                        Logger.write("Font was already registered: "
                                + file[0].toString(), Logger.Type.WARNING);
                        
                    } else {
                        Logger.write("Could not register font: "
                                + file[0].toString(), Logger.Type.ERROR);
                    }
                    
                } else {
                    Logger.write("Successfully loaded font: "
                            + file[0].toString(), Logger.Type.INFO);
                }
            } else {
                Logger.write("Ignored file: " 
                        + file[0].toString(), Logger.Type.INFO);
            }
            
        }
        
        Logger.write(new String[] {
            "========== FINISHED LOADING FONTS ==========", 
            ""
        }, Logger.Type.INFO);
    }
    
    
}
