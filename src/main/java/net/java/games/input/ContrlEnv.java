
/**
 * Use this package to extend {@link DefaultControllerEnvironment}.
 */
package net.java.games.input;


// Own imports
import src.tools.log.Logger;


// Java imports
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import src.GS;
import src.Locker;


/**
 * 
 */
public class ContrlEnv
        extends DefaultControllerEnvironment {
    
    private long updateInterval = 5000L;
    private boolean autoUpdateEnabled = true;
    private boolean manualUpdate = false;
    
    private Controller[] cachedContr = new Controller[0];
    
    private Lock lock = new ReentrantLock(true);
    private Condition requestUpdate = lock.newCondition();
    private Condition environmentUpdated = lock.newCondition();
    
    private List<Controller> added = new ArrayList<>();
    private List<Controller> removed = new ArrayList<>();
    
    //private Deque<CEPair> eventQueue = new LinkedList<>();
    //private Lock eventLock = new ReentrantLock(true);
    
    /*
    public static class CEPair {
        final private Controller c;
        final private Event e;
        
        public CEPair(Controller c, Event e) {
            this.c = c;
            this.e = e;
        }
        
        public Controller getController() {
            return c;
        }
        
        /**
         * @return the event 
         *//*
        public Event getEvent() {
            return e;
        }
        
        
    }
    /**/
    
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public ContrlEnv() {
        setAutoUpdate(false);
        
        new Thread("Controll-environment-updater-thread") {
            @Override
            public void run() {
                // The update thread should have minimal priority.
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                
                while (true) {
                    // tmp
                    Logger.write("Update controller list");
                    
                    // Kill the thread of the previous controller levent queue.
                    final Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (final Thread thread : threadSet) {
                        final String name = thread.getClass().getName();
                        if (name.equals("net.java.games.input.RawInputEventQueue$QueueThread")) {
                            thread.interrupt();
                            try {
                                thread.join();
                                
                            } catch (final InterruptedException e) {
                                thread.interrupt();
                            }
                        }
                    }
                    
                    // Load controllers.
                    Controller[] c;
                    try {
                        // When the VM is shutting down, an invocation exception
                        // might occur. Since this is not important, 
                        // simple exit the updating loop and prevent spamming
                        // the error in the console and giving you
                        // a heart attack every single time.
                        c = ContrlEnv.super.getControllers();
                        
                        // Because the compiler is complaining...
                        if (null != null)
                            throw new InvocationTargetException(null);
                        
                    } catch (InvocationTargetException e) {
                        break;
                    }
                    /*
                    eventLock.lock();
                    try {
                        // Add the events of the removed controllers such that
                        // no events are missed.
                        addEvents(cachedContr);
                        
                    } finally {
                        eventLock.unlock();
                    }*/
                    
                    // Notify listeners for added devices.
                    if (cachedContr == null) {
                        added = new ArrayList<>(Arrays.asList(c));
                        // tmp
                        for (int i = 0; i < c.length; i++) {
                            System.out.println("new controller: " + c[i]);
                        }
                        
                    } else {
                        setDiff(cachedContr, c);
                    }
                    
                    // Update cached controllers and signal that they have
                    // been changed. Notify the listeners afterwards.
                    lock.lock();
                    try {
                        Locker.lock(GS.keyDet);
                        try {
                            cachedContr = c;
                            environmentUpdated.signalAll();
                            notifyListener();

                        } finally {
                            Locker.unlock(GS.keyDet);
                        }
                        
                    } finally {
                        lock.unlock();
                    }
                    
                    // Wait for the next update cycle.
                    lock.lock();
                    try {
                        if (autoUpdateEnabled) {
                            requestUpdate.await(updateInterval,
                                    TimeUnit.MILLISECONDS);
                            System.out.println("02");
                            
                        } else {
                            requestUpdate.await();
                            System.out.println("03");
                        }
                        
                    } catch (InterruptedException e) {
                        Logger.write(e);
                        
                    } finally {
                        lock.unlock();
                    }
                    
                    // Remove loaded controllers to initiate a reload.
                    try {
                        Field field = DefaultControllerEnvironment.class
                                .getDeclaredField("controllers");
                        field.setAccessible(true);
                        field.set(ContrlEnv.this, null);
                        
                        field = DefaultControllerEnvironment.class
                                .getDeclaredField("loadedPluginNames");
                        field.setAccessible(true);
                        field.set(ContrlEnv.this, new ArrayList<>());
                        
                    } catch (NoSuchFieldException |
                            IllegalAccessException e) {
                        Logger.write(e);
                    }
                }
            }
        }.start();
        
        // Set this as the default environment.
        SwingUtilities.invokeLater(() -> {
            try {
                Field field = ControllerEnvironment.class
                        .getDeclaredField("defaultEnvironment");
                field.setAccessible(true);
                field.set(this, this);

            } catch (NoSuchFieldException |
                    IllegalAccessException e) {
                Logger.write(e);
            }
        });
    }
    
    /**
     * Adds the events of given controllers.
     * @param controllers 
     *//*
    private void addEvents(Controller[] controllers) {
        Event e = new Event();
        for (Controller c : controllers) {
            c.poll();
            EventQueue eq = c.getEventQueue();
            while (eq.getNextEvent(e)) {
                eventQueue.add(new CEPair(c, e));
            }
        }
    }
    
    /**
     * @return a deque with all events since the last
     *     time this method was called.
     *//*
    public Deque<CEPair> getEvents() {
        if (!eventLock.tryLock()) return null;
        try {
            addEvents(getControllers());
            Deque<CEPair> curQueue = eventQueue;
            eventQueue = new LinkedList<>();
            return curQueue;
            
        } finally {
            eventLock.unlock();
        }
    }
    /**/
    
    /**
     * Stores the difference between the previous and next controll array.
     * 
     * @param prev the previous controller array.
     * @param next the next controller array.
     */
    private void setDiff(Controller[] prev, Controller[] next) {
        List<Controller> p = new ArrayList<>(Arrays.asList(prev));
        List<Controller> n = new ArrayList<>(Arrays.asList(next));
        
        // Update the new controller list.
        // Iterate backwards to improve deletion speed of the arraylist
        // and to prevent index-corrections.
        for (int i = n.size() - 1; i >= 0; i--) {
            Controller contr = n.get(i);
            
            boolean found = false;
            for (int j = p.size() - 1; j >= 0; j--) {
                if (compareController(p.get(j), contr)) {
                    p.remove(j);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                added.add(contr);
                System.out.println("New controller: " + contr);
            }
            
            n.remove(i);
        }
        
        // Flip the added elements.
        Collections.reverse(added);
        
        // All controllers that were not in the next list are not removed
        // in the previous list.
        removed = p;
        // tmp
        removed.forEach((Controller c) -> {
            System.out.println("Removed controller: " + c);
        });
    }
    
    /**
     * Compares two controllers.
     * 
     * @param c1 the first controller to compare.
     * @param c2 the second controller to compare.
     * @return {@code true} if both controllers are identical.
     *     {@code false} otherwise.
     */
    public static boolean compareController(Controller c1, Controller c2) {
        // Null check.
        if (c1 == null || c2 == null) return c1 == c2;
        
        // Simple comparisons.
        //Logger.write("  00");
        if (c1.getType() != c2.getType()) return false;
        //Logger.write("  01");
        if (c1.getPortType() != c2.getPortType()) return false;
        //Logger.write("  02");
        if (c1.getPortNumber() != c2.getPortNumber()) return false;
        //Logger.write("  03");
        if (!c1.getName().equals(c2.getName())) return false;
        //Logger.write("  04");
        
        // Compare sub-controllers.
        Controller[] c1Ctrls = c1.getControllers();
        Controller[] c2Ctrls = c2.getControllers();
        if (c1Ctrls.length != c2Ctrls.length) return false;
        for (int i = 0; i < c1Ctrls.length; i++) {
            if (!compareController(c1Ctrls[i], c2Ctrls[i])) return false;
        }
        //Logger.write("  05");
        
        // Compare components.
        Component[] c1Comps = c1.getComponents();
        Component[] c2Comps = c2.getComponents();
        if (c1Comps.length != c2Comps.length) return false;
        for (int i = 0; i < c1Comps.length; i++) {
            if (!c1Comps[i].toString().equals(c2Comps[i].toString()))
                return false;
        }
        //Logger.write("  06");
        
        return true;
    }
    
    @Override
    public Controller[] getControllers() {
        lock.lock();
        try {
            if (cachedContr == null) {
                requestUpdate.signal();
            }

        } finally {
            lock.unlock();
            return cachedContr;
        }
    }
    
    /**
     * Does the same as {@link #getControllers()}, but now waits for the
     * update thread to generate at least the initial controllers.
     * This ensures that the returned value will never be an empty array.
     * 
     * @return the controllers that are currently connected.
     */
    public Controller[] waitForController() {
        lock.lock();
        try {
            if (cachedContr == null) {
                requestUpdate.signal();
                environmentUpdated.await();
            }

        } finally {
            lock.unlock();
            return cachedContr;
        }
    }
    
    /**
     * Updates the listeners with the added and removed controllers.
     */
    protected void notifyListener() {
        for (Controller c : added) {
            
            fireControllerAdded(c);
        }
        
        for (Controller c : removed) {
            fireControllerRemoved(c);
        }
        
        Logger.write("Added  : " + added);
        Logger.write("Removed: " + removed);
        
        added.clear();
        removed.clear();
    }
    
    /**
     * Forces the environment to be fetched immediately.
     */
    public void forceUpdate() {
        lock.lock();
        try {
            if (!manualUpdate) requestUpdate.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @return {@code true} if auto updates are enabled.
     */
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }
    
    /**
     * @param enable whether the update should occur automatically.
     * 
     * Might not return immediately.
     */
    public void setAutoUpdate(boolean enable) {
        if (enable != autoUpdateEnabled) {
            lock.lock();
            try {
                if (autoUpdateEnabled = enable) {
                    requestUpdate.signal();
                }
                
            } finally {
                lock.unlock();
            }
        }
    }
    
    /**
     * @return the update interval in milliseconds.
     */
    public long getUpdateInterval() {
        return updateInterval;
    }
    
    /**
     * @param interval the new update interval in milliseconds.
     */
    public void setUpdateInterval(long interval) {
        updateInterval = interval;
    }
    
    /**
     * @return {@code false} if the manual updates are enabled.
     */
    public boolean isManualUpdateEnabled() {
        return manualUpdate;
    }
    
    /**
     * @param enable whether to ignore all directly called updates.
     * 
     * Note that auto updates will still occur.
     */
    public void setManualUpdate(boolean enable) {
        manualUpdate = enable;
    }
    
    
}
