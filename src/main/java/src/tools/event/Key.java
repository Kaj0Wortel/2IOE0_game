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


// Own imports
import src.tools.Cloneable;


// Java imports
import src.tools.MultiTool;
import java.awt.event.InputEvent;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


/**
 * Class containing shorter naming for keys all under one roof instead
 * of spread out over multiple classes.
 */
public class Key
        implements Cloneable {
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    // Alphabet
    final public static Key A = new Key(true, KeyEvent.VK_A); // = 65 = 'A'
    final public static Key B = new Key(true, KeyEvent.VK_B); // = 66 = 'B'
    final public static Key C = new Key(true, KeyEvent.VK_C); // = 67 = 'C'
    final public static Key D = new Key(true, KeyEvent.VK_D); // = 68 = 'D'
    final public static Key E = new Key(true, KeyEvent.VK_E); // = 69 = 'E'
    final public static Key F = new Key(true, KeyEvent.VK_F); // = 70 = 'F'
    final public static Key G = new Key(true, KeyEvent.VK_G); // = 71 = 'G'
    final public static Key H = new Key(true, KeyEvent.VK_H); // = 72 = 'H'
    final public static Key I = new Key(true, KeyEvent.VK_I); // = 73 = 'I'
    final public static Key J = new Key(true, KeyEvent.VK_J); // = 74 = 'J'
    final public static Key K = new Key(true, KeyEvent.VK_K); // = 75 = 'K'
    final public static Key L = new Key(true, KeyEvent.VK_L); // = 76 = 'L'
    final public static Key M = new Key(true, KeyEvent.VK_M); // = 77 = 'M'
    final public static Key N = new Key(true, KeyEvent.VK_N); // = 78 = 'N'
    final public static Key O = new Key(true, KeyEvent.VK_O); // = 79 = 'O'
    final public static Key P = new Key(true, KeyEvent.VK_P); // = 80 = 'P'
    final public static Key Q = new Key(true, KeyEvent.VK_Q); // = 81 = 'Q'
    final public static Key R = new Key(true, KeyEvent.VK_R); // = 82 = 'R'
    final public static Key S = new Key(true, KeyEvent.VK_S); // = 83 = 'S'
    final public static Key T = new Key(true, KeyEvent.VK_T); // = 84 = 'T'
    final public static Key U = new Key(true, KeyEvent.VK_U); // = 85 = 'U'
    final public static Key V = new Key(true, KeyEvent.VK_V); // = 86 = 'V'
    final public static Key W = new Key(true, KeyEvent.VK_W); // = 87 = 'W'
    final public static Key X = new Key(true, KeyEvent.VK_X); // = 88 = 'X'
    final public static Key Y = new Key(true, KeyEvent.VK_Y); // = 89 = 'Y'
    final public static Key Z = new Key(true, KeyEvent.VK_Z); // = 90 = 'Z'
    
    // Numbers
    final public static Key N0 = new Key(true, KeyEvent.VK_0); // = 48 = '0'
    final public static Key N1 = new Key(true, KeyEvent.VK_1); // = 49 = '1'
    final public static Key N2 = new Key(true, KeyEvent.VK_2); // = 50 = '2'
    final public static Key N3 = new Key(true, KeyEvent.VK_3); // = 51 = '3'
    final public static Key N4 = new Key(true, KeyEvent.VK_4); // = 52 = '4'
    final public static Key N5 = new Key(true, KeyEvent.VK_5); // = 53 = '5'
    final public static Key N6 = new Key(true, KeyEvent.VK_6); // = 54 = '6'
    final public static Key N7 = new Key(true, KeyEvent.VK_7); // = 55 = '7'
    final public static Key N8 = new Key(true, KeyEvent.VK_8); // = 56 = '8'
    final public static Key N9 = new Key(true, KeyEvent.VK_9); // = 57 = '9'
    
    // Arrow keys
    final public static Key LEFT  = new Key(true, KeyEvent.VK_LEFT); // = 37 = '%'
    final public static Key UP    = new Key(true, KeyEvent.VK_LEFT); // = 38 = '&'
    final public static Key RIGHT = new Key(true, KeyEvent.VK_LEFT); // = 39 = '\''
    final public static Key DOWN  = new Key(true, KeyEvent.VK_LEFT); // = 40 = '( '
    
    // Function keys
    final public static Key F1  = new Key(true, KeyEvent.VK_F1); // = 112
    final public static Key F2  = new Key(true, KeyEvent.VK_F2); // = 113
    final public static Key F3  = new Key(true, KeyEvent.VK_F3); // = 114
    final public static Key F4  = new Key(true, KeyEvent.VK_F4); // = 115
    final public static Key F5  = new Key(true, KeyEvent.VK_F5); // = 116
    final public static Key F6  = new Key(true, KeyEvent.VK_F6); // = 117
    final public static Key F7  = new Key(true, KeyEvent.VK_F7); // = 118
    final public static Key F8  = new Key(true, KeyEvent.VK_F8); // = 119
    final public static Key F9  = new Key(true, KeyEvent.VK_F9); // = 120
    final public static Key F10 = new Key(true, KeyEvent.VK_F10); // = 121
    final public static Key F11 = new Key(true, KeyEvent.VK_F11); // = 122
    final public static Key F12 = new Key(true, KeyEvent.VK_F12); // = 123
    final public static Key F13 = new Key(true, KeyEvent.VK_F13); // = 61440
    final public static Key F14 = new Key(true, KeyEvent.VK_F14); // = 61441
    final public static Key F15 = new Key(true, KeyEvent.VK_F15); // = 61442
    final public static Key F16 = new Key(true, KeyEvent.VK_F16); // = 61443
    final public static Key F17 = new Key(true, KeyEvent.VK_F17); // = 61444
    final public static Key F18 = new Key(true, KeyEvent.VK_F18); // = 61445
    final public static Key F19 = new Key(true, KeyEvent.VK_F19); // = 61446
    final public static Key F20 = new Key(true, KeyEvent.VK_F20); // = 61447
    final public static Key F21 = new Key(true, KeyEvent.VK_F21); // = 61448
    final public static Key F22 = new Key(true, KeyEvent.VK_F22); // = 61449
    final public static Key F23 = new Key(true, KeyEvent.VK_F23); // = 61450
    final public static Key F24 = new Key(true, KeyEvent.VK_F24); // = 61451
    
    // Other keys
    final public static Key ENTER   = new Key(true, KeyEvent.VK_ENTER); // = 10
    final public static Key SPACE   = new Key(true, KeyEvent.VK_SPACE); // = 32 = ' '
    final public static Key SHIFT   = new Key(true, KeyEvent.VK_SHIFT); // = 16
    final public static Key CTRL    = new Key(true, KeyEvent.VK_CONTROL); // = 46
    final public static Key WINDOWS = new Key(true, KeyEvent.VK_WINDOWS); // = 524
    final public static Key DOT     = new Key(true, KeyEvent.VK_PERIOD); // = 46
    final public static Key COMMA   = new Key(true, KeyEvent.VK_COMMA); // 44
    final public static Key SLASH   = new Key(true, KeyEvent.VK_SLASH); // = 47
    final public static Key BSLASH  = new Key(true, KeyEvent.VK_BACK_SLASH); // = 92
    final public static Key DEL     = new Key(true, KeyEvent.VK_DELETE); // = 127 = (DEL)
    final public static Key ESC     = new Key(true, KeyEvent.VK_ESCAPE); // = 27 = (ESC)
    final public static Key BACKSPACE = new Key(true, KeyEvent.VK_BACK_SPACE); // 8 = (BACKSPACE)
    final public static Key MINUS   = new Key(true, KeyEvent.VK_MINUS); // = 45 = '-'
    final public static Key EQUAL   = new Key(true, KeyEvent.VK_EQUALS); // = 61 = '='
    final public static Key GREATER = new Key(true, KeyEvent.VK_GREATER); // 160
    final public static Key LESS    = new Key(true, KeyEvent.VK_LESS); // = 153
    final public static Key CAPS_LOCK = new Key(true, KeyEvent.VK_CAPS_LOCK); // = 20
    
    // Masks
    final public static int SHIFT_MASK = InputEvent.SHIFT_DOWN_MASK;     // =      0b0000_0100_0000
    final public static int CTRL_MASK  = InputEvent.CTRL_DOWN_MASK;      // =      0b0000_1000_0000
    final public static int META_MASK  = InputEvent.META_DOWN_MASK;      // =      0b0001_0000_0000
    final public static int ALT_MASK   = InputEvent.ALT_DOWN_MASK;       // =      0b0010_0000_0000
    final public static int ALT_GRAPH  = InputEvent.ALT_GRAPH_DOWN_MASK; // = 0b0010_0000_0000_0000
    
    // Default values
    final public static int DEFAULT_MASK = 0;
    final public static boolean DEFAULT_KEY_RELEASE = false;
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    // Whether the key is immutable or not
    final private boolean immutable;
    
    // Stores the key value.
    private int key;
    
    // The modifier for this key
    private int mask;
    
    // Whether the key was released or pressed.
    private boolean onKeyRelease;
    
    
    /**-------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Converts a key event to a key.
     * 
     * @param e the key event.
     * @param onKeyRelease whether the key was released or pressed.
     */
    public Key(KeyEvent e) {
        this(e, DEFAULT_KEY_RELEASE);
    }
    
    public Key(KeyEvent e, boolean onKeyRelease) {
        this(true, e.getExtendedKeyCode(), e.getModifiers(), onKeyRelease);
    }
    
    /**
     * Clone constructor.
     * 
     * @param key the key to clone.
     */
    public Key(Key key) {
        this(key.immutable, key.key, key.mask, key.onKeyRelease);
    }
    
    /**
     * Creates a new key.
     * 
     * @param immutable whether this key should be immutable.
     * @param key value for this key.
     * @param modifier the modifier of the key.
     * @param onKeyRelease whether the key was released or pressed.
     */
    public Key(int key) {
        this(false, key);
    }
    
    public Key(int key, int mask) {
        this(false, key, mask, DEFAULT_KEY_RELEASE);
    }
    
    public Key(int key, boolean onKeyRelease) {
        this(false, key, DEFAULT_MASK, onKeyRelease);
    }
    
    public Key(int key, int mask, boolean onKeyRelease) {
        this(false, key, mask, onKeyRelease);
    }
    
    public Key(boolean immutable, int key) {
        this(immutable, key, DEFAULT_MASK, DEFAULT_KEY_RELEASE);
    }
    
    public Key(boolean immutable, int key, int mask, boolean onKeyRelease) {
        this.immutable = immutable;
        this.key = key;
        this.mask = mask;
        this.onKeyRelease = onKeyRelease;
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * @return whether this key is immutable
     */
    public boolean isImmutable() {
        return immutable;
    }
    
    /**
     * @return the key value.
     */
    public int getKey() {
        return key;
    }
    
    /**
     * @return the mask of this key.
     */
    public int getMask() {
        return mask;
    }
    
    /**
     * @return whether the key is being pressed or released.
     */
    public boolean isPressed() {
        return !onKeyRelease;
    }
    
    /**
     * Returns a keystroke of that key.
     */
    public KeyStroke toKeyStroke() {
        return KeyStroke.getKeyStroke(key, mask, onKeyRelease);
    }
    
    /**
     * Sets the mask of this key if not immutable
     * or having the same mask value.
     * 
     * @return a key having the same key value
     *     as {@code key} and the given mask.
     * 
     * Note that {@code this == setMask(this.getMask()) ==>
     *     this.mask == mask || !immutable}.
     */
    public Key setMask(int mask) {
        if (this.mask == mask) return this;
        
        if (immutable) {
            return new Key(key, mask, true);
            
        } else {
            this.mask = mask;
            return this;
        }
    }
    
    /**
     * Applies the mask to the key. The previous mask settings still remain.
     * 
     * @return a key having the same key value
     *     as {@code key} and the given mask.
     * 
     * Note that {@code this == setMask(this.getMask()) ==>
     *     this.mask == mask || !immutable}.
     */
    public Key applyMask(int mask) {
        if (this.mask == mask) return this;
        
        if (immutable) {
            return new Key(key, this.mask | mask, onKeyRelease);
            
        } else {
            this.mask |= mask;
            return this;
        }
    }
    
    /**
     * @return a new key having the same properties as {@code this}, but
     *     has the given key value.
     */
    public Key newKeyValue(int k) {
        if (k == key) return this;
        
        if (immutable) {
            return new Key(k, mask, onKeyRelease);
            
        } else {
            key = k;
            return this;
        }
    }
    
    /**
     * @return a new key having the same properties as {@code this}, but
     *     has the given onKeyRelease value.
     */
    public Key newOnKeyRelease(boolean okr) {
        if (onKeyRelease == okr) return this;
        
        if (immutable) {
            return new Key(key, mask, okr);
            
        } else {
            onKeyRelease = okr;
            return this;
        }
    }
    
    /**
     * Checks if two keys are equal. If just a number is given, compare
     * the number with the key value of the Key object.
     * 
     * @return whether the two given objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Key) {
            Key cmpKey = (Key) obj;
            return this.key == cmpKey.key &&
                    this.mask == cmpKey.mask &&
                    this.onKeyRelease == cmpKey.onKeyRelease;
            
        } else if (obj instanceof Number) {
            return ((int) key) == ((Number) obj).intValue();
            
        } else if (obj instanceof KeyStroke) {
            return KeyStroke.getKeyStroke((char) key, mask)
                .equals((KeyStroke) obj);
            
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(key, mask, onKeyRelease);
    }
    
    @Override
    public Key clone() {
        return new Key(this);
    }
    
    
}