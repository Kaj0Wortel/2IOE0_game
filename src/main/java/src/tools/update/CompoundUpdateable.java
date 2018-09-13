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

package src.tools.update;


// Own imports


// Java imports

import src.tools.log.Logger;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;


/**
 * This class can be used for easy adding an removing multiple updateables
 * repeatedly. The priority of the given updateables is ignored, and only
 * the priority of this class is used.
 * Also, this class can be used to ensure the order of execution, which is
 * the order returned by the default iterator of the collection.
 * This class can also be used to group updateables and exectue them together
 * when using {@link Updater.Type#SEP_THREAD}.
 * 
 * @author Kaj Wortel (0991586)
 */
public class CompoundUpdateable
        implements Updateable {
    
    final private Priority prior;
    final private Collection<Updateable> ups;
    
    public CompoundUpdateable(Priority prior) {
        this(prior, null);
    }
    
    public CompoundUpdateable(Priority prior, Collection<Updateable> ups) {
        this.prior = prior;
        if (ups == null) this.ups = new HashSet<Updateable>();
        else this.ups = ups;
    }
    
    @Override
    public void performUpdate(long timeStamp) {
        synchronized(ups) {
            for (Updateable up : ups) {
                try {
                    up.performUpdate(timeStamp);

                } catch (Exception e) {
                    Logger.write(new Object[] {
                        "Exception occured in updateable:",
                        e,
                        "Updateable: ",
                        up.toString()
                    }, Logger.Type.ERROR);
                }
            }
        }
    }
    
    @Override
    public void ignoreUpdate(long timeStamp) {
        synchronized(ups) {
            for (Updateable up : ups) {
                try {
                    up.ignoreUpdate(timeStamp);

                } catch (Exception e) {
                    Logger.write(new Object[] {
                        "Exception occured in updateable:",
                        e,
                        "Updateable: ",
                        up.toString()
                    }, Logger.Type.ERROR);
                }
            }
        }
    }
    
    @Override
    public Priority getPriority() {
        return prior;
    }
    
    /**
     * Adds the given updateable.
     * 
     * @param up the updateable to be added.
     */
    public void add(Updateable up) {
        SwingUtilities.invokeLater(() -> {
            synchronized(ups) {
                ups.add(up);
            }
        });
    }
    
    /**
     * Adds the given updateables.
     * 
     * @param addUps the updateables to be added.
     */
    public void addAll(Collection<Updateable> addUps) {
        SwingUtilities.invokeLater(() -> {
            synchronized(ups) {
                ups.addAll(addUps);
            }
        });
    }
    
    /**
     * Removes the given updateable.
     * 
     * @param up the updateable to be removed.
     */
    public void remove(Updateable up) {
        SwingUtilities.invokeLater(() -> {
            synchronized(ups) {
                ups.remove(up);
            }
        });
    }
    
    /**
     * Removes the given updateables.
     * 
     * @param addUps the updateables to be removed.
     */
    public void remvoeAll(Collection<Updateable> addUps) {
        SwingUtilities.invokeLater(() -> {
            synchronized(ups) {
                ups.removeAll(addUps);
            }
        });
    }
    
    /**
     * Removes all updateables
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            synchronized(ups) {
                ups.clear();
            }
        });
    }
    
}
