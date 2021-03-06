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


// Java imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import src.Controllers.CameraController;
import src.GS;
import src.Physics.Physics;
import src.tools.MultiTool;
import src.tools.MultiTool.RandomIterator;
import src.tools.update.CollisionManager.Collision;
import src.tools.update.CollisionManager.Entry;


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class Updater {
    // The number of available threads. The {@code -1} is because the
    // graphics part also needs one thread. Other (mainly sleeping or waiting)
    // scheduleTask threads from other classes are ignored.
    final private static int NUM_THREADS = Math.max(1, Runtime.getRuntime()
            .availableProcessors() - 1);
    final private static Set<Updateable> updateSet = new HashSet<>();
    final private static List<UpdateThread> updateThreads = new ArrayList<>();
    
    @SuppressWarnings("UseSpecificCatch")
    final private static AccurateTimerTool tt = new AccurateTimerTool(() -> {
        // Obtain the time stamp.
        long timeStamp = System.currentTimeMillis();
        
        
        /** Update updateables. */
        synchronized(updateSet) {
            // Distribute the tasks evenly and randomly over the
            // available threads.
            if (updateThreads == null || updateThreads.isEmpty()) {
                Logger.write("Updater has no update threads!",
                        Logger.Type.ERROR);
                return;
            }
            
            int tasksPerThread = (int) Math.ceil(
                    ((double) updateSet.size()) / updateThreads.size());
            int counter = 0;
            List<Updateable> tasks = new ArrayList<>(tasksPerThread);
            UpdateThread updater = updateThreads.get(0);
            int threadCounter = 0;
            
            Iterator<Updateable> it = new RandomIterator(updateSet);
            while (it.hasNext()) {
                Updateable updateable = it.next();
                tasks.add(updateable);
                
                if (tasks.size() > tasksPerThread) {
                    // Create a new scheduleTask thread.
                    updater.scheduleTask(createUpdateRunnable(tasks, timeStamp));
                    if (++counter < updateThreads.size()) {
                        updater = updateThreads.get(counter);
                        
                    } else {
                        Logger.write(
                                "Wrong distribution amoung update threads!",
                                Logger.Type.WARNING);
                    }
                    
                    // Reset counter and scheduleTask list for
                    // the next iteration.
                    tasks = new ArrayList<>();
                }
            }
            
            if (!tasks.isEmpty()) {
                updater.scheduleTask(createUpdateRunnable(tasks, timeStamp));
            }
            
            // Wait for the update threads to terminate.
            for (UpdateThread thread : updateThreads) {
                thread.waitUntilDone();
            }
        }
        
        
        /** Determine collisions with double non-static collisions. */
        final Iterator<Collision> colIt = CollisionManager.colLockIterator();
        for (UpdateThread updater : updateThreads) {
            updater.scheduleTask(() -> {
                while (colIt.hasNext()) {
                    Collision col = colIt.next();
                    if (col == null) return;
                    try {
                        Physics.exeCollision(col);
                        
                    } finally {
                        Locker.unlock(col.e1.inst);
                        Locker.unlock(col.other);
                    }
                }
            });
        }
        
        // Wait for the update threads to terminate.
        for (UpdateThread thread : updateThreads) {
            thread.waitUntilDone();
        }
        
        
        /** Update objects that had a double non-static collision. */
        int tasksPerThread = (int) Math.ceil(
                ((double) updateSet.size()) / updateThreads.size());
        int counter = 0;
        List<Entry> tasks = new ArrayList<>(tasksPerThread);
        UpdateThread updater = updateThreads.get(0);
        int threadCounter = 0;
        
        final Iterator<Entry> instIt = CollisionManager.entryIterator();
        while (instIt.hasNext()) {
            Entry entry = instIt.next();
            tasks.add(entry);

            if (tasks.size() > tasksPerThread) {
                // Create a new scheduleTask thread.
                final List<Entry> t = tasks;
                updater.scheduleTask(() -> {
                    for (Entry e : t) {
                        Physics.calcAndUpdatePhysics(e);
                    }
                });
                if (++counter < updateThreads.size()) {
                    updater = updateThreads.get(counter);

                } else {
                    Logger.write(
                            "Wrong distribution among update threads!",
                            Logger.Type.WARNING);
                }
                
                // Reset counter and scheduleTask list for
                // the next iteration.
                tasks = new ArrayList<>();
            }
        }

        if (!tasks.isEmpty()) {
            final List<Entry> t = tasks;
            updater.scheduleTask(() -> {
                for (Entry e : t) {
                    Physics.calcAndUpdatePhysics(e);
                }
            });
        }
        
        // Wait for the update threads to terminate.
        for (UpdateThread thread : updateThreads) {
            thread.waitUntilDone();
        }
        
        /** Update Camera. */
        CameraController camContr = GS.cameraController;
        if (camContr == null) return;
        try {
            Locker.lock(camContr);
            try {
                camContr.update(timeStamp);
                
            } finally {
                Locker.unlock(camContr);
            }
            
        } catch (Exception e) { // Play it safe to catch all types.
            Logger.write(new Object[] {
                "Exception occured in Camera controller:",
                e,
            }, Logger.Type.ERROR);
        }
    });
    static {
        tt.setPriority(Thread.MAX_PRIORITY);
    }
    
    /** 
     * Creates an scheduleTask thread.
     * 
     * @param threadUpdates the updateables to scheduleTask.
     * @param timeStamp the timestamp the scheduleTask occured.
     * @return a fresh scheduleTask thread.
     */
    @SuppressWarnings("UseSpecificCatch")
    private static Runnable createUpdateRunnable(
            List<Updateable> threadUpdates, long timeStamp) {
        return () -> {
            //Logger.write("scheduleTask thread" + num + " begin");
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
                        "Skipped update of Updateable " + up.toString() + ".",
                        "Reason: lock was in use."
                    }, Logger.Type.WARNING);
                }
            }
        };
    }
    
    /**
     * Updates a single updateable with the given timestamp.
     * 
     * @param up the updateable to scheduleTask.
     * @param timeStamp the timestamp to scheduleTask the updateable with.
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
        tt.setFPSState(AccurateTimerTool.FPSState.AUTO);
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
     * @see AccurateTimerTool#start()
     */
    public static void start() {
        Logger.write("Updater started!", Logger.Type.INFO);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            UpdateThread ut = new UpdateThread();
            ut.start();
            updateThreads.add(ut);
        }
        
        tt.start();
    }
    
    /**
     * @see AccurateTimerTool#pause()
     */
    public static void pause() {
        tt.pause();
        Logger.write("Updater paused!", Logger.Type.INFO);
    }
    
    /**
     * @see AccurateTimerTool#resume()
     */
    public static void resume() {
        tt.resume();
        Logger.write("Updater resumed!", Logger.Type.INFO);
    }
    
    /**
     * @see AccurateTimerTool#cancel()
     */
    public static void cancel() {
        tt.cancel();
        Logger.write("Updater canceled!", Logger.Type.INFO);
    }
    
    /**
     * @param interval the new interval to be set.
     * 
     * @see AccurateTimerTool#setInterval(long)
     */
    public static void setInterval(long interval) {
        tt.setInterval(interval);
    }
    
    /**
     * @param interval the new frames per second (FPS) to be set.
     * 
     * @see AccurateTimerTool#setInterval(long)
     */
    public static void setFPS(double fps) {
        tt.setFPS(fps);
    }
    
    /**
     * @return the current interval.
     * 
     * @see AccurateTimerTool#getInterval()
     */
    public static long getInterval() {
        return tt.getInterval();
    }
    
    /**
     * @return the current fps.
     * 
     * @see AccurateTimerTool#getFPS()
     */
    public static double getFPS() {
        return tt.getFPS();
    }
    
    /**
     * @param interval the new target interval.
     * 
     * @see AccurateTimerTool#setTargetInterval(long)
     */
    public static void setTargetInterval(long interval) {
        tt.setTargetInterval(interval);
    }
    
    /**
     * @param fps the new target fps.
     * 
     * @see AccurateTimerTool#setTargetFPS(double)
     */
    public static void setTargetFPS(double fps) {
        tt.setTargetFPS(fps);
    }
    
    
}
