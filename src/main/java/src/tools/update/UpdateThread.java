/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.tools.update;


// Java imports
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// Own imports
import src.tools.log.Logger;


/**
 * Updating thread.
 * Use {@link scheduleTask(Runnable)} to schedule a task.
 * If 
 */
public class UpdateThread
        extends Thread {
    final private Deque<Runnable> requestQueue = new LinkedList<>();
    
    final private Lock lock = new ReentrantLock();
    final private Condition addedToQueue = lock.newCondition();
    final private Condition waitForEmpty = lock.newCondition();
    
    final private Lock interruptLock = new ReentrantLock();
    
    private static int id;
    private boolean terminate = false;
    
    
    public UpdateThread() {
        super("Update-thread-" + id++);
    }
    
    
    @Override
    public void run() {
        while (true) {
            /** Obtain the task. */
            Runnable r = null;
            lock.lock();
            try {
                // If the queue is empty, wait for a task.
                if (requestQueue.isEmpty()) {
                    waitForEmpty.signalAll();
                    addedToQueue.await();
                }
                
                if (terminate) {
                    cleanup();
                    return;
                }
                
                // Obtain and remove the task from the queue.
                r = requestQueue.pollFirst();
                
            } catch (InterruptedException e) {
                if (!terminate) {
                    Logger.write(new Object[] {
                        "Interrupted exception was caught in update thread:",
                        e
                    }, Logger.Type.ERROR);
                }
                
            } finally {
                lock.unlock();
            }
            
            /** Execute the task. */
            interruptLock.lock();
            try {
                if (r != null) r.run();
                
            } catch (Exception e) {
                // Note that no interrupts from {@link #interrupt()} from
                // this class can be thrown when the task is executing.
                if (!terminate) {
                    Logger.write(new Object[] {
                        "Exception caught in Update thread:",
                        e
                    }, Logger.Type.ERROR);
                    
                } else {
                    cleanup();
                    return;
                }
                
            } finally {
                interruptLock.unlock();
            }
        }
    }
    
    /**
     * Internal cleanup when terminated.
     */
    private void cleanup() {
        lock.lock();
        try {
            requestQueue.clear();
            // To prevent waiting threads for stopped scheduleTask thread.
            waitForEmpty.signalAll();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @param r the task to execute.
     */
    public void scheduleTask(Runnable r) {
        if (r == null) return;
        
        lock.lock();
        try {
            if (terminate) return;
            requestQueue.addLast(r);
            addedToQueue.signalAll();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @return the size of the current request queue.
     * 
     * Note that the event that is currently being executed
     * is NOT counted here.
     */
    public int size() {
        return requestQueue.size();
    }
    
    public void waitUntilDone() {
        lock.lock();
        try {
            if (terminate) return;
            if (requestQueue.isEmpty()) return;
            waitForEmpty.await();
            
        } catch (InterruptedException e) {
            Logger.write(new Object[] {
                "Exception while waiting for " + getName() + ":",
                e
            }, Logger.Type.WARNING);
            
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void interrupt() {
        interruptLock.lock();
        try {
            terminate = true;
            super.interrupt();
            
        } finally {
            interruptLock.unlock();
        }
    }
    
    
}
