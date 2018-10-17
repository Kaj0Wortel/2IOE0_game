
package src.tools.update;


// Own imports
import java.util.Collection;
import java.util.HashMap;
import src.Assets.instance.Instance;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;


// Java imports
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import src.Locker;
import src.Physics.PStructAction;
import src.tools.MultiTool;
import src.Progress.ProgressManager;


/**
 * Collision manager for managing dynamic collisions.
 * The collisions are executed after all all entities have been updated,
 * but before the camera is update.
 * A collision is ignored if a collision between the same two instances
 * was already added for this cycle.
 */
public class CollisionManager {
    
    
    public static class Collision {
        final protected Entry e1;
        protected Entry e2;
        final protected Instance other;
        
        
        public Collision(Instance source, Instance other,
                PStructAction pStruct, ModPhysicsContext mpc, ModState ms, ProgressManager progress) {
            this(new Entry(source, pStruct, mpc, ms, progress), other);
        }
        
        public Collision(Entry entry, Instance other) {
            this.e1 = entry;
            this.other = other;
        }
        
        
        /**
         * @return the first entry of the collision.
         */
        public Entry getEntry1() {
            return e1;
        }
        
        /**
         * @return the second entry of the collision.
         */
        public Entry getEntry2() {
            return e2;
        }
        
        /**
         * @inheritDoc
         * 
         * Note that each collision between the same two entries must
         * return the same hash code.
         */
        @Override
        public int hashCode() {
            return MultiTool.calcHashCode(e1.inst.hashCode() + other.hashCode());
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Collision)) return false;
            Collision col = (Collision) obj;
            return (col.e1.inst.equals(this.e1.inst) &&
                    col.other.equals(this.other)) ||
                    (col.e1.inst.equals(this.other) &&
                    col.other.equals(this.e1.inst));
        }
        
        
    }
    
    
    /**
     * Entry class for representing a token of a collision.
     * Each entry contains the source {@link Instance}, the
     * {@link PStructAction} from the controller, the {@link ModState}
     * of the instance for this cycle and the {@link ModPhysicsContext}
     * of the instance for this cycle.
     */
    public static class Entry {
        final public Instance inst;
        final public PStructAction pStruct;
        final public ModPhysicsContext mpc;
        final public ModState ms;
        final public ProgressManager progress;
        
        
        public Entry(Instance inst, PStructAction pStruct,
                ModPhysicsContext mpc, ModState ms, ProgressManager progress) {
            this.inst = inst;
            this.pStruct = pStruct;
            this.mpc = mpc;
            this.ms = ms;
            this.progress = progress;
        }
        
        @Override
        public int hashCode() {
            return MultiTool.calcHashCode(inst);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) return false;
            Entry entry = (Entry) obj;
            return entry.inst.equals(this.inst);
        }
        
        
    }
    
    
    /**
     * Iterator that obtains a lock for both instances of the collision.
     * 
     * WARNING!
     * This iterator <b>ONLY</b> obtains the lock for both instances.
     * The lock should be <b>MANUALLY CLOSED</b>.
     */
    private static class ColObtainLockIterator
            implements Iterator<Collision> {
        
        final private List<Collision> redoCol = new LinkedList<>();
        final private Iterator<Collision> colIt;
        final private Lock lock = new ReentrantLock(true);
        
        
        public ColObtainLockIterator(Iterable<Collision> colSet) {
            colIt = colSet.iterator();
        }
        
        
        @Override
        public boolean hasNext() {
            lock.lock();
            try {
                return colIt.hasNext() || redoCol.size() > 0;
                
            } finally {
                lock.unlock();
            }
        }
        
        @Override
        public Collision next() {
            lock.lock();
            try {
                // Check if a new collection is available
                while (colIt.hasNext()) {
                    Collision col = colIt.next();
                    if (checkAndLockCol(col)) return col;
                    else redoCol.add(col);
                }
                
                // Keep checking if a previously locked collision is available.
                // Otherwise throw a {@link NoSuchElementException}.
                while (!redoCol.isEmpty()) {
                    Iterator<Collision> it = redoCol.iterator();
                    while (it.hasNext()) {
                        Collision col = it.next();
                        if (checkAndLockCol(col)) {
                            it.remove();
                            return col;
                        }
                    }
                }
                
            } finally {
                lock.unlock();
            }
            
            return null;
        }
        
        /**
         * Checks if the locks of the instances of the given collision are
         * available. If so, lock them. If at least one of them cannot be
         * locked, then no locks are obtained.
         * 
         * @param col the collision object to check.
         * @return {@code true} if both locks were obtained.
         *     {@code false} otherwise.
         */
        private boolean checkAndLockCol(Collision col) {
            if (Locker.tryLock(col.e1.inst)) {
                if (Locker.tryLock(col.other)) {
                    return true;
                    
                } else {
                    Locker.unlock(col.e1.inst);
                    return false;
                }
                
            } else {
                return false;
            }
        }
        
        
    }
    
    
    private static Map<Collision, Collision> collisions = new HashMap<>();
    private static Set<Entry> reEvalEntry = new HashSet<>();
    final private static Lock lock = new ReentrantLock();
    
    /**
     * Adds the given collision for later processing.
     * 
     * @param c the collision to add.
     */
    public static void addCollision(Instance source, Instance other,
            PStructAction pStruct, ModPhysicsContext mpc, ModState ms,
            ProgressManager progress) {
        Entry entry = new Entry(source, pStruct, mpc, ms, progress);
        Collision col = new Collision(entry, other);
        lock.lock();
        try {
            reEvalEntry.add(entry);
            Collision existing = collisions.get(col);
            if (existing == null) {
                // The collision doesn't exist yet, so create a new entry.
                collisions.put(col, col);
                
            } else {
                // Note that if {@code existing != null} then
                // {@code existing.other == inst1} and
                // {@code existing.e1.inst == inst2}, under the assumption
                // that the same source class will never notify twice
                // for the same collision.
                existing.e2 = entry;
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @return the set containing all (dynamic) collisions
     *     of the current iteration.
     * 
     * Also resets the collision set.
     */
    public static Collection<Collision> getCollisions() {
        Collection<Collision> rtn = collisions.values();
        collisions = new HashMap<>();
        return rtn;
    }
    
    /**
     * @return the set containing all entries involved
     *     in a (dynamic) collision.
     * 
     * Also resets the instances set.
     */
    public static Set<Entry> getEntries() {
        Set<Entry> rtn = reEvalEntry;
        reEvalEntry = new HashSet<>();
        return rtn;
    }
    
    /**
     * @return a lock iterator that obtains the lock of both instances of
     *     a collision when calling the {@link Iterator#next()} function.
     * 
     * @see ColObtainLockIterator
     */
    public static Iterator<Collision> colLockIterator() {
        return new ColObtainLockIterator(getCollisions());
    }
    
    /**
     * @return an iterator over the instances that were involved
     *     in the collisions.
     */
    public static Iterator<Entry> entryIterator() {
        return getEntries().iterator();
    }
    
    
}
