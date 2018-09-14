
package src;


// Own imports


// Java imports

import src.tools.log.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import src.tools.MultiTool;


/**
 * Class for providing global locks for all registered objects.
 */
public class Locker {
    final private static long DEFAULT_TIMEOUT = 1L;
    final private static Map<Object, Lock> lockMap
            = new ConcurrentHashMap<>();
    
    
    // Private constructor for the static singleton design pattern.
    private Locker() { }
    
    /**
     * Locks the given object.
     * USE WITH EXTREME CARE!
     * This function may cause a deadlock if the object is continuesly
     * accessed by another thread.
     * 
     * @param obj the object to lock.
     * 
     * @see Lock#lock()
     */
    public static void lock(Object obj) {
        Lock lock;
        synchronized(lockMap) {
            lock = lockMap.get(obj);
        }
        
        if (lock == null) {
            Logger.write("Tried to access lock of object " + obj.toString()
                    + ", but no lock was set!", Logger.Type.WARNING);
            MultiTool.logStackTrace();
            
        } else lock.lock();
    }
    
    /**
     * Tries to lock the given object.
     * 
     * @param obj the object to lock.
     * @return {@code true} if the lock was acquired,
     *         {@code false} otherwise.
     * 
     * This function will return instantly.
     * 
     * @see Lock#tryLock()
     */
    public static boolean tryLock(Object obj) {
        Lock lock;
        synchronized(lockMap) {
            lock = lockMap.get(obj);
        }
        
        if (lock == null) {
            Logger.write("Tried to access lock of object " + obj.toString()
                    + ", but no lock was set!", Logger.Type.WARNING);
            MultiTool.logStackTrace();
            return true;
            
        } else {
            return !lock.tryLock();
        }
    }
    
    /**
     * Tries to lock the given object within the given timelimit.
     * 
     * @param obj the object to lock.
     * @param millis the timelimit in milliseconds.
     * @return {@code false} if the timelimit was exceeded.
     *     {@code true} otherwise.
     * @throws InterruptedException if the thread was interrupted.
     * 
     * @see Lock#tryLock(long, TimeUnit)
     */
    public static boolean tryLock(Object obj, long millis)
            throws InterruptedException {
        Lock lock;
        synchronized(lockMap) {
            lock = lockMap.get(obj);
        }
        
        if (lock == null) {
            Logger.write("Tried to access lock of object " + obj.toString()
                    + ", but no lock was set!", Logger.Type.WARNING);
            MultiTool.logStackTrace();
            return true;
            
        } else {
            return !lock.tryLock(millis, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Unlocks the lock of the given object.
     * 
     * @param obj the object to unlock.
     * 
     * @see Lock#unlock()
     */
    public static void unlock(Object obj) {
       synchronized(lockMap) {
           Lock lock = lockMap.get(obj);
           if (lock != null) lock.unlock();
       }
    }
    
    /**
     * Adds the given object to the locker.
     * 
     * @param obj the object to add.
     */
    public static void add(Object obj) {
        synchronized(lockMap) {
            if (lockMap.get(obj) == null) {
                lockMap.put(obj, new ReentrantLock());
                Logger.write("Added lock for: " + obj);
            }
        }
    }
    
    /**
     * Removes the given object from the locker.
     * 
     * @param obj the object to remove.
     */
    public static void remove(Object obj) {
        synchronized(lockMap) {
            if (lockMap.get(obj) != null) lockMap.remove(obj);
        }
    }
    
    
}
