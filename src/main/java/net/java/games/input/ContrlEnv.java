
/**
 * Use this package to extend {@link DefaultControllerEnvironment}.
 */
package net.java.games.input;


// Own imports
import src.tools.log.Logger;


// Java imports
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

/**
 * 
 */
public class ContrlEnv
        extends DefaultControllerEnvironment {
    
    final private static long UPDATE_TIME = 5000L;
    
    private Controller[] cachedContr = null;
    private Lock lock = new ReentrantLock();
    private Condition cachedControllersChanged = lock.newCondition();
    private Condition updateEnvironment = lock.newCondition();
    
    private List<Controller> added = new ArrayList<>();
    private List<Controller> removed = new ArrayList<>();
    
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public ContrlEnv() {
        
        new Thread("Controll-environment-updater-thread") {
            @Override
            public void run() {
                long t1 = System.currentTimeMillis();
                long t2;
                while (true) {
                    Logger.write("UPDATE!");
                    // Load controllers.
                    Controller[] c = ContrlEnv.super.getControllers();
                    
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
                    
                    // Update cached controllers.
                    cachedContr = c;
                    
                    // Signal that the controllers have changed.
                    lock.lock();
                    try {
                        cachedControllersChanged.signalAll();
                        
                    } finally {
                        lock.unlock();
                    }
                    
                    // Notifies the listeners of the possible changes.
                    notifyListener();
                    
                    // Wait for the next update cycle.
                    lock.lock();
                    try {
                        t2 = System.currentTimeMillis();
                        Logger.write("Time taken: " + (t2 - t1));
                        updateEnvironment.await(UPDATE_TIME,
                                TimeUnit.MILLISECONDS);
                        t1 = System.currentTimeMillis();
                        
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
        if (c1.getType() != c2.getType()) return false;
        if (c1.getPortType() != c2.getPortType()) return false;
        if (c1.getPortNumber() != c2.getPortNumber()) return false;
        if (!c1.getName().equals(c2.getName())) return false;
        
        // Compare sub-controllers.
        Controller[] c1Ctrls = c1.getControllers();
        Controller[] c2Ctrls = c2.getControllers();
        if (c1Ctrls.length != c2Ctrls.length) return false;
        for (int i = 0; i < c1Ctrls.length; i++) {
            if (!compareController(c1Ctrls[i], c2Ctrls[i])) return false;
        }
        
        // Compare components.
        Component[] c1Comps = c1.getComponents();
        Component[] c2Comps = c2.getComponents();
        if (c1Comps.length != c2Comps.length) return false;
        for (int i = 0; i < c1Comps.length; i++) {
            if (!c1Comps[i].toString().equals(c2Comps[i].toString()))
                return false;
        }
        
        return true;
    }
    
    @Override
    public Controller[] getControllers() {
        lock.lock();
        try {
            if (cachedContr == null) {
                updateEnvironment.signalAll();
                cachedControllersChanged.await();
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
            updateEnvironment.signalAll();
            
        } finally {
            lock.unlock();
        }
    }
    
    
}
