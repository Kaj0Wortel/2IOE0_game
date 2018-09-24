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


// Java imports
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import src.tools.MultiTool;

import src.tools.log.Logger;


public class AccurateTimerTool {
    /**
     * Enum for how to handle the frame rate.
     */
    public static enum FPSState {
        MANUAL, AUTO;
    }
    private FPSState fpsState = FPSState.MANUAL;
    
    private Thread updateThread;
    private int id;
    
    
    // The tasks to be executed.
    final private Runnable[] tasks;
    // The initial delay.
    private long delay;
    // The timer interval.
    private long interval;
    // The target interval.
    private long targetInterval;
    
    // The start timestamp of the timer for the current iteration.
    private Long startTime;
    
    // The pause timestamp of the timer. If there was no pause in
    // this iteration then it is equal to the start timestamp.
    private Long pauseTime;
    
    // The current state of the timer
    public enum TimerState {
        RUNNING, PAUSED, CANCELED
    }
    private TimerState timerState = TimerState.CANCELED;
    
    // Whether the execution is still being performed.
    public boolean running = false;
    
    // Keeps track of how many cycles must pass before the
    // additative increase is replaced multiplicative increase.
    public int waitMul = 0;
    
    // Denotes the thread priority for this timer.
    private int priority = Thread.NORM_PRIORITY;
    
    
    /**--------------------------------------------------------------------------------------------------------
     * Constructor
     * --------------------------------------------------------------------------------------------------------
     */
    /**
     * @param r the action that will be executed when the timer ends.
     * @param delay the time in ms before the first exectution of
     *     {@code r.run()}.
     * @param interval the time in ms which is between two executions of
     *     {@code r.run()}.
     */
    public AccurateTimerTool(Runnable... rs) {
        this(0L, 1000L, rs);
    }
    
    public AccurateTimerTool(long interval, Runnable... rs) {
        this(0L, interval, rs);
    }
    
    public AccurateTimerTool(long delay, long interval, Runnable... rs) {
        // Update the values to the values in this class
        this.tasks = rs;
        this.delay = delay;
        this.interval = interval;
        
        
        this.delay = delay;
        
        // For the first iteration is the start time modified because then
        // there are no problems with the pause/resume functions if the
        // timer is still in the initial delay.
        startTime = System.currentTimeMillis() + delay - interval;
        
        // By default, set the target interval to the given interval.
        targetInterval = interval;
    }
    
    
    /**--------------------------------------------------------------------------------------------------------
     * Functions
     * --------------------------------------------------------------------------------------------------------
     */
    /**
     * Create a new timer task from the given runnable.
     * Also updates the start time and the pause time.
     * 
     * @param rs the tasks to be executed. Is allowed to be null,
     * but this is not effective.
     * 
     * Handles the fps rate using M/AIMD (multiplicative/additative increase,
     * multiplicative decrease). Decreases when the executing task cannot
     * keep up with the speed of the timer. Increases when the
     * executing task can keep up with the speed of the timer and the
     * targetInterval has not yet been reached.
     *//*
    private TimerTask createTimerTask(Runnable... rs) {
        return new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                //Logger.write("START");
                boolean wasRunning;
                synchronized(AccurateTimerTool.this) {
                    wasRunning = running;
                    running = true;
                }
                
                // Update the timestamps of the start and pause time.
                startTime = System.currentTimeMillis();
                pauseTime = startTime; // To ensure equal timestamps.
                
                if (wasRunning) {
                    //Logger.write("QUIT");
                    
                    if (fpsState == FPSState.AUTO) {
                        waitMul += 10;
                        setInterval((long) Math.ceil((interval * 1.05)));
                    }
                    
                    return;
                    
                } else {
                    if (fpsState == FPSState.AUTO) {
                        if (interval > targetInterval) {
                            if (--waitMul <= 0) {
                                waitMul = 0;
                                setInterval(Math.min(
                                        (long) (interval * 0.95),
                                        targetInterval));
                            } else {
                                setInterval(interval - 1);
                            }
                            
                        } else if (interval < targetInterval) {
                            setInterval(targetInterval);
                        }
                    }
                }
                
                // Run the function(s) on a new thread.
                new Thread("Timer-update") {
                    @Override
                    public void run() {
                        Thread.currentThread().setPriority(priority);
                        if (rs != null) {
                            for (Runnable r : rs) {
                                r.run();
                            }
                        }
                        
                        synchronized(AccurateTimerTool.this) {
                            running = false;
                        }
                        //Logger.write("END");
                    }
                }.start();
            }
        };
    }
    /**/
    /**
     * (Re)-starts the timer.
     * If the timer is already running, purge the timer and create a new timer.
     */
    public void start() {
        // Update the timeState
        if (timerState != TimerState.RUNNING) {
            timerState = TimerState.RUNNING;
            
            startTime = System.currentTimeMillis();
            pauseTime = System.currentTimeMillis();
            updateThread = createUpdateThread(++id);
            updateThread.start();
        }
    }
    
    /**
     * Pauses the timer.
     * If the timer is paused or stopped, no acion is taken.
     */
    public void pause() {
        if (timerState == TimerState.PAUSED ||
                timerState == TimerState.CANCELED) return;
        
        id++;
        
        // Set the pause time stamp
        pauseTime = System.currentTimeMillis();
        
        // Update the timeState
        timerState = TimerState.PAUSED;
    }
    
    /**
     * Resumes a paused timer
     * If the timer is running or canceled, no action is taken.
     */
    public void resume() {
        if (timerState == TimerState.RUNNING ||
                timerState == TimerState.CANCELED) return;
        
        // The current time.
        long curTime = System.currentTimeMillis();
        
        // Calculate the initial delay.
        long timeBeforeRun = interval - (pauseTime - startTime);
        long startDelay = (timeBeforeRun < 0 ? 0 : timeBeforeRun);
        
        // Update the start time stamp.
        startTime = curTime - timeBeforeRun;
        
        updateThread = createUpdateThread(++id);
        updateThread.start();
        delay = startDelay;
        
        // Update the timeState
        timerState = TimerState.RUNNING;
    }
    
    /**
     * Cancels a running or canceled timer.
     */
    public void cancel() {
        // Kill the current timer.
        if (timerState == TimerState.RUNNING) {
            id++;
        }
        
        // Update the timeState
        timerState = TimerState.CANCELED;
    }
    
    /**
     * Sets a new interval for the timer.
     * 
     * @param interval the new interval to be set.
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }
    
    /**
     * Sets the interval using frame rates.
     * Uses {@link #setInterval(long)} for the implementation.
     * 
     * @param fps the new fps.
     */
    public void setFPS(double fps) {
        setInterval((long) (1000 / fps));
    }
    
    /**
     * Sets the target interval.
     * When using the auto-fps mode, this value will be used
     * even when a shorter interval is possible.
     * Also sets the current interval to the target interval
     * to improve convergence.
     * 
     * @param interval the new target interval.
     */
    public void setTargetInterval(long interval) {
        targetInterval = interval;
        this.interval = interval;
    }
    
    /**
     * Sets the target frame rate.
     * 
     * @param fps the new target frame rate.
     */
    public void setTargetFPS(double fps) {
        setTargetInterval((long) (1000 / fps));
    }
    
    /**
     * @return the current interval between two updates.
     */
    public long getInterval() {
        return interval;
    }
    
    /**
     * @return the current number of frames per second.
     */
    public double getFPS() {
        return 1000.0 / interval;
    }
    
    /**
     * Sets the state for handeling the frame rate.
     * 
     * @param state the new state.
     */
    public void setFPSState(FPSState state) {
        synchronized(fpsState) {
            fpsState = state;
        }
    }
    
    /**
     * @return the current state for handeling the frame rate.
     */
    public FPSState getFPSState() {
        synchronized(fpsState) {
            return fpsState;
        }
    }
    
    /**
     * @param priority the new update thread priority.
     * 
     * Note: should be within the range of {@link Thread#MIN_PRIORITY} and
     * {@link Thread#MAX_PRIORITY}. Default is {@link Thread#NORM_PRIORITY}.
     * 
     * @see Thread#setPriority(int)
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
     * @return the current priority of the update thread.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * @return the current state of the timer.
     */
    public TimerState getState() {
        return timerState;
    }
    
    private Lock lock = new ReentrantLock(true);
    private Condition stopWaiting = lock.newCondition();
    private long prevTime;
    
    private boolean waiting = false;
    private Thread createUpdateThread(int threadID) {
        return new Thread("update-thread") {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                
                while (System.currentTimeMillis() - pauseTime < delay) {
                    MultiTool.sleepThread(1);
                }
                
                while (threadID == id) {
                    //Logger.write("START");
                    
                    // Update the timestamps of the start and pause time.
                    startTime += interval;
                    pauseTime = startTime;
                    //prevTime = System.currentTimeMillis();
                    
                    
                    boolean wasRunning;
                    lock.lock();
                    try {
                        wasRunning = running;
                        
                    } finally {
                        lock.unlock();
                    }
                    
                    if (wasRunning) {
                        lock.lock();
                        try {
                            if (waiting) {
                                Logger.write("QUIT");

                                if (fpsState == FPSState.AUTO) {
                                    waitMul += 10;
                                    setInterval((long) Math.ceil((interval * 1.05)));
                                }
                                continue;

                            } else {
                                waiting = true;
                                try {
                                    stopWaiting.await();
                                    
                                } catch (InterruptedException e) {
                                    Logger.write(new Object[] {
                                        "Accurate-timer was interrupted:",
                                        e
                                    }, Logger.Type.ERROR);
                                    return;
                                } finally {
                                    waiting = false;
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                        
                    } else {
                        if (fpsState == FPSState.AUTO) {
                            if (interval > targetInterval) {
                                if (--waitMul <= 0) {
                                    waitMul = 0;
                                    setInterval(Math.min(
                                            (long) (interval * 0.95),
                                            targetInterval));
                                } else {
                                    setInterval(interval - 1);
                                }
                                
                            } else if (interval < targetInterval) {
                                setInterval(targetInterval);
                            }
                        }
                    }
                    
                    
                    long curTime = System.currentTimeMillis();
                    System.out.println(curTime - prevTime);
                    prevTime = curTime;
                    
                    
                    // Run the function(s) on a new thread.
                    new Thread("Timer-update") {
                        @Override
                        public void run() {
                            Thread.currentThread().setPriority(priority);
                            if (tasks != null) {
                                for (Runnable r : tasks) {
                                    r.run();
                                }
                            }
                            
                            lock.lock();
                            try {
                                running = false;
                                stopWaiting.signalAll();
                                
                            } finally {
                                lock.unlock();
                            }
                        }
                    }.start();
                    
                    while (System.currentTimeMillis() - pauseTime < interval) {
                        MultiTool.sleepThread(1);
                    }
                }
            }
        };
    }
    
    
}