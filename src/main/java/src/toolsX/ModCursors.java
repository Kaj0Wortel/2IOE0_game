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

package tools;


// Java imports
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Provides an easy way for retrieving
 * cursors used in a GUI application.
 * 
 * Is thread safe.
 */
public class ModCursors {
    // Map where the cursors will be stored.
    final public static ConcurrentHashMap<String, Cursor> cursors
        = new ConcurrentHashMap<String, Cursor>();
    
    /**
     * Provided cursors.
     */
    final public static Cursor CROSSHAIR_CURSOR
        = new Cursor(Cursor.CROSSHAIR_CURSOR);
    
    final public static Cursor DEFAULT_CURSOR
        = new Cursor(Cursor.DEFAULT_CURSOR);
    
    final public static Cursor HAND_CURSOR
        = new Cursor(Cursor.HAND_CURSOR);
    
    final public static Cursor MOVE_CURSOR
        = new Cursor(Cursor.MOVE_CURSOR);
    
    final public static Cursor TEXT_CURSOR
        = new Cursor(Cursor.TEXT_CURSOR);
    
    final public static Cursor WAIT_CURSOR
        = new Cursor(Cursor.WAIT_CURSOR);
    
    // Provided resize cursors
    final public static Cursor N_RESIZE_CURSOR
        = new Cursor(Cursor.N_RESIZE_CURSOR);
    
    final public static Cursor NE_RESIZE_CURSOR
        = new Cursor(Cursor.NE_RESIZE_CURSOR);
    
    final public static Cursor E_RESIZE_CURSOR
        = new Cursor(Cursor.E_RESIZE_CURSOR);
    
    final public static Cursor SE_RESIZE_CURSOR
        = new Cursor(Cursor.SE_RESIZE_CURSOR);
    
    final public static Cursor S_RESIZE_CURSOR
        = new Cursor(Cursor.S_RESIZE_CURSOR);
    
    final public static Cursor SW_RESIZE_CURSOR
        = new Cursor(Cursor.SW_RESIZE_CURSOR);
    
    final public static Cursor W_RESIZE_CURSOR
        = new Cursor(Cursor.W_RESIZE_CURSOR);
    
    final public static Cursor NW_RESIZE_CURSOR
        = new Cursor(Cursor.NW_RESIZE_CURSOR);
    
    
    /**
     * This is a static singleton class. No instances should be made.
     */
    private ModCursors() { }
    
    /**
     * Creates a cursor from an image.
     * 
     * @param name the name of the cursor.
     * @param bi the image of the cursor.
     * @param x the x hot spot of the cursor.
     * @param y the y hot spot of the cursor.
     * @throws IllegalArgumentException if {@code cursors.contains(name)}.
     * 
     * @see replaceCursor(String, Image, int, int).
     */
    public static void loadCursor(String name, Image bi)
            throws IllegalArgumentException {
        loadCursor(name, bi, 0, 0);
    }
    
    public static void loadCursor(String name, Image bi, int x, int y)
            throws IllegalArgumentException {
        if (cursors.contains(name)) {
            throw new IllegalArgumentException(
                    "There already exists a cursor with the name: " + name);
        }
        
        replaceCursor(name, bi, x, y);
    }
    
    /**
     * Replaces the cursor of the given name by the new image.
     * 
     * @param name the name of the cursor.
     * @param bi the image of the cursor.
     * @param x the x hot spot of the cursor.
     * @param y the y hot spot of the cursor.
     * @return the removed cursor.
     */
    public static Cursor replaceCursor(String name, Image bi) {
        return replaceCursor(name, bi, 0, 0);
    }
    
    public static Cursor replaceCursor(String name, Image bi, int x, int y) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Cursor newCursor = tk.createCustomCursor(bi, new Point(x, y), name);
        return cursors.put(name, newCursor);
    }
    
    /**
     * @param name the name of the cursor to be returned.
     * @return the cursor stored at the given name.
     */
    public static Cursor getCursor(String name) {
        return cursors.get(name);
    }
    
    /**
     * Removes the given cursor.
     * 
     * @param name the name of the cursor to be removed.
     * @return the removed cursor.
     */
    public static Cursor removeCursor(String name) {
        return cursors.remove(name);
    }
    
    /**
     * Clears all cursors from the list.
     */
    public static void clear() {
        cursors.clear();
    }
    
}