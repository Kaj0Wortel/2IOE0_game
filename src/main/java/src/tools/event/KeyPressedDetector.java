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

package src.tools.event;


// Java imports
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;


/**
 * A KeyListener class that detects which keys were pressed since the
 * last update. To prevent missing key presses, this also includes keys
 * that were pressed and released between the two updates.
 */
public class KeyPressedDetector
        extends KeyAdapter {
    // List containing all keys that are currently pressed.
    protected Set<Key> keysCurPressed = new HashSet<Key>();
    
    // List containing all keys that were at least once pressed between the
    // last update and now.
    protected Set<Key> keysPressedSinceLastUpdate = new HashSet<Key>();
    
    // List containing all keys that were pressed between the last two updates.
    // This list is only updated by the update method, and not via events.
    protected Set<Key> keysPressedHistory = new HashSet<Key>();
    
    
    /**
     * Default constructor.
     */
    public KeyPressedDetector() {
        this(null);
    }
    
    /**
     * Constructor.
     * 
     * @param comp the component to listen to.
     */
    public KeyPressedDetector(Component comp) {
        if (comp != null) {
            SwingUtilities.invokeLater(() -> {
                comp.addKeyListener(this);
            });
        }
    }
    
    
    /**
     * This method is called when a key was pressed.
     * Adds the pressed key to both {@link #keysCurPressed} and
     * {@link #keysPressedSinceLastUpdate}.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        Key key = new Key(e);
        keysCurPressed.add(key);
        keysPressedSinceLastUpdate.add(key);
    }
    
    /**
     * This method is called when a key was released.
     * Removes the pressed key from {@code keysCurPressed}.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keysCurPressed.remove(new Key(e));
    }
    
    
    /**
     * Checks if the key was pressed.
     * 
     * @param key key-value to check for.
     * @return true iff the given key was pressed between the two last updates.
     */
    public boolean wasPressed(Key key) {
        return keysPressedHistory.contains(key);
    }
    
    /**
     * @return all keys that were pressed at least once between the two
     *     last updates. Returned set should not be modified externally.
     * 
     * Note:
     * Unsafe return on purpose for speedup.
     * Do not modify the returned set!
     */
    public Set<Key> getKeysPressed() {
        return keysPressedHistory;
    }
    
    /**
     * Clears all lists.
     */
    public void reset() {
        keysCurPressed.clear();
        keysPressedSinceLastUpdate.clear();
        keysPressedHistory.clear();
    }
    
    /**
     * Updates the lists to their new status.
     */
    public void update() {
        keysPressedHistory = keysPressedSinceLastUpdate;
        keysPressedSinceLastUpdate = new HashSet<Key>(keysCurPressed); // TODO concurrent mod exception here!
    }
    
    
}