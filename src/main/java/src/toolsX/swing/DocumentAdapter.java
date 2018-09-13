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

package tools.swing;


// Java imports
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Adapter class for the {@link #DocumentListener} interface.
 * 
 * @author Kaj Wortel (0991586)
 */
@FunctionalInterface
public interface DocumentAdapter
        extends DocumentListener {
    
    @Override
    default public void insertUpdate(DocumentEvent e) {
        action(e);
    }
    
    @Override
    default public void removeUpdate(DocumentEvent e) {
        action(e);
    }
    
    @Override
    default public void changedUpdate(DocumentEvent e) {
        action(e);
    }
    
    /**
     * Bundeled action function.
     * 
     * @param e the document event.
     */
    public void action(DocumentEvent e);
    
}
