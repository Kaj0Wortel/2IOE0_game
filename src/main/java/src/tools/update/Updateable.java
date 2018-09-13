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
import src.GS;


/**
 * Interface for when a class should be updated using an update function.
 * 
 * @author Kaj Wortel (0991586)
 */
public interface Updateable {
    public enum Priority {
        UPDATE_ALWAYS,
        ONLY_WHEN_RUNNING,
        ONLY_WHEN_PAUSED,
        EXCEPT_WHEN_STOPPED,
        UPDATE_NEVER;
    }
    
    default public void update(long timeStamp)
            throws InterruptedException {
        Priority p = getPriority();
        if (p == Priority.UPDATE_ALWAYS) {
            performUpdate(timeStamp);
            
        } else if (p == Priority.ONLY_WHEN_RUNNING) {
            if (GS.getGameState() == GS.GameState.PLAYING) {
                performUpdate(timeStamp);
                
            } else {
                ignoreUpdate(timeStamp);
            }
            
        } else if (p == Priority.ONLY_WHEN_PAUSED) {
            if (GS.getGameState() == GS.GameState.PAUSED) {
                performUpdate(timeStamp);
                
            } else {
                ignoreUpdate(timeStamp);
            }
            
        } else if (p == Priority.EXCEPT_WHEN_STOPPED) {
            if (GS.getGameState() != GS.GameState.STOPPED) {
                performUpdate(timeStamp);
                
            } else {
                ignoreUpdate(timeStamp);
            }
            
        } else if (p == Priority.UPDATE_NEVER) {
            ignoreUpdate(timeStamp);
        }
    }
    
    /**
     * Update method.
     * 
     * @param timeStamp the timestamp on which the update event occured.
     */
    public void performUpdate(long timeStamp)
            throws InterruptedException;
    
    /**
     * Function that is invoked when an update was ignored.
     * 
     * @param timeStamp the timestamp on which the update event occured.
     */
    default public void ignoreUpdate(long timeStamp)
            throws InterruptedException {
    }
    
    /**
     * @return the priority of the update.
     */
    public Priority getPriority();
    
    
}
