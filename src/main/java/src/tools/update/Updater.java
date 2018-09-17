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
import src.Locker;
import src.tools.log.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import src.tools.MultiTool.RandomIterator;

// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class Updater {
    // The number of available threads. The {@code -1} is because the
    // graphics part also needs one thread. Other (mainly sleeping or waiting)
    // update threads from other classes are ignored.
    final private static int NUM_THREADS = Runtime.getRuntime()
            .availableProcessors() - 1;
    final private static Set<Updateable> updateSet = new HashSet<>();
    
    final private static TimerTool tt = new TimerTool(() -> {
        // Obtain the time stamp.
        long timeStamp = System.currentTimeMillis();
        // Update the keys. -> replace by separate timer/thread.
        //if (GS.keyDet != null) GS.keyDet.update();
        
        synchronized(updateSet) {
            // Distribute the tasks evenly and randomly over the
            // available threads.
            int tasksPerThread = updateSet.size() / NUM_THREADS + 1;
            int counter = 0;
            List<Updateable> threadUpdates = new ArrayList<>(tasksPerThread);
            List<Thread> threads = new ArrayList<>();
            int num = 0;
            
            Iterator<Updateable> it = new RandomIterator(updateSet);
            while (it.hasNext()) {
                Updateable updateable = it.next();
                
                if (++counter <= tasksPerThread) {
                    threadUpdates.add(updateable);
                    
                } else {
                    // Create a new update thread.
                    Thread thread = createUpdateThread(threadUpdates,
                            timeStamp, num++);
                    // Start and store the thread.
                    thread.start();
                    threads.add(thread);
                    // Reset counter and update list for
                    // the next iteration.
                    threadUpdates = new ArrayList<>();
                    counter = 0;
                }
            }
            if (!threadUpdates.isEmpty()) {
                // Create a new update thread.
                Thread thread = createUpdateThread(threadUpdates,
                        timeStamp, num++);
                // Start and store the thread.
                thread.start();
                threads.add(thread);
            }
            
            // Wait for the other threads to terminate.
            for (Thread thread : threads) {
                try {
                    thread.join();
                    
                } catch (InterruptedException e) {
                    Logger.write("Attempted to interrupte update thread!",
                            Logger.Type.ERROR);
                }
            }
        }
    });
    
    /** 
     * Creates an update thread.
     * 
     * @param threadUpdates the updateables to update.
     * @param timeStamp the timestamp the update occured.
     * @return a fresh update thread.
     */
    private static Thread createUpdateThread(List<Updateable> threadUpdates,
            long timeStamp, int num) {
        return new Thread("Update-thread-" + num) {
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public void run() {
                // Update the updateables. If the updateable is locked,
                // try it later again.
                List<Updateable> doLater = new ArrayList<Updateable>();
                for (Updateable up : threadUpdates) {
                    if (!updateUpdateable(up, timeStamp)) doLater.add(up);
                }
                
                // Re-do all updateables that were locked the first time.
                // If it is locked again, skip it, as it takes too long
                // to wait for all of them.
                // With this are also deadlocks avoided.
                for (Updateable up : doLater) {
                    if (!updateUpdateable(up, timeStamp)) {
                        Logger.write(new String[] {
                            "Skipped update of Updateable " + up.toString() 
                                + ".",
                            "Reason: lock was in use."
                        }, Logger.Type.WARNING);
                    }
                }
            }
        };
    }
    
    /**
     * Updates a single updateable with the given timestamp.
     * 
     * @param up the updateable to update.
     * @param timeStamp the timestamp to update the updateable with.
     * @return {@code false} if the updateable was locked.
     *     {@code true} otherwise.
     */
    @SuppressWarnings("UseSpecificCatch")
    private static boolean updateUpdateable(Updateable up, long timeStamp) {
        try {
            if (Locker.tryLock(up)) {
                try {
                    up.update(timeStamp);
                    
                } finally {
                    Locker.unlock(up);
                }
            } else return false;

        } catch (Exception e) { // Play it safe to catch all types.
            Logger.write(new Object[] {
                "Exception occured in updateable:",
                e,
                "Updateable:",
                up.toString()
            }, Logger.Type.ERROR);
        }
        
        return true;
    }
    
    // Setup timer tool.
    static {
        tt.setFPSState(TimerTool.FPSState.AUTO);
        tt.setTargetFPS(60);
    }
    
    
    /**
     * Private constructor because of static singleton design pattern.
     */
    private Updater() { }
    
    
    /**
     * Adds a updateable to the updater.
     * 
     * @param up the updateable to remove.
     */
    public static void addTask(Updateable up) {
        SwingUtilities.invokeLater(() -> {
            synchronized(updateSet) {
                updateSet.add(up);
                Locker.add(up);
            }
        });
    }
    
    /**
     * Removes an updateable from the updater.
     * 
     * @param up the updateable to be removed.
     */
    public static void removeTask(Updateable up) {
        SwingUtilities.invokeLater(() -> {
            synchronized(updateSet) {
                updateSet.remove(up);
                Locker.remove(up);
            }
        });
    }
    
    /**
     * @see TimerTool#start()
     */
    public static void start() {
        tt.start();
        Logger.write("Updater started!", Logger.Type.INFO);
        
        // tmp
        fpsTracker.start();
    }
    
    // tmp
    private static TimerTool fpsTracker = new TimerTool(1000, 1000, () -> {
        Logger.write("Fps = " + getFPS());
    });
    
    /**
     * @see TimerTool#pause()
     */
    public static void pause() {
        tt.pause();
        Logger.write("Updater paused!", Logger.Type.INFO);
    }
    
    /**
     * @see TimerTool#resume()
     */
    public static void resume() {
        tt.resume();
        Logger.write("Updater resumed!", Logger.Type.INFO);
    }
    
    /**
     * @see TimerTool#cancel()
     */
    public static void cancel() {
        tt.cancel();
        Logger.write("Updater canceled!", Logger.Type.INFO);
    }
    
    /**
     * @param interval the new interval to be set.
     * 
     * @see TimerTool#setInterval(long)
     */
    public static void setInterval(long interval) {
        tt.setInterval(interval);
    }
    
    /**
     * @param interval the new frames per second (FPS) to be set.
     * 
     * @see TimerTool#setInterval(long)
     */
    public static void setFPS(double fps) {
        tt.setFPS(fps);
    }
    
    /**
     * @return the current interval.
     * 
     * @see TimerTool#getInterval()
     */
    public static long getInterval() {
        return tt.getInterval();
    }
    
    /**
     * @return the current fps.
     * 
     * @see TimerTool#getFPS()
     */
    public static double getFPS() {
        return tt.getFPS();
    }
    
    /**
     * @param interval the new target interval.
     * 
     * @see TimerTool#setTargetInterval(long)
     */
    public static void setTargetInterval(long interval) {
        tt.setTargetInterval(interval);
    }
    
    /**
     * @param fps the new target fps.
     * 
     * @see TimerTool#setTargetFPS(double)
     */
    public static void setTargetFPS(double fps) {
        tt.setTargetFPS(fps);
    }
    
    
}
