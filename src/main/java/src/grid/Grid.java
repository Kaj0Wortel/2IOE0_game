
package src.grid;


// Own imports


// Java imports
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.joml.Vector3f;
import org.joml.Vector3i;
import src.Assets.instance.Instance;
import src.GS;
import src.tools.MultiTool;


/**
 * 
 */
public class Grid {
    final private float dx;
    final private float dy;
    final private float dz;
    final private float startX;
    final private float startY;
    final private float startZ;
    
    final private Map<Vector3i, Set<GridItem>> items
            = new ConcurrentHashMap<>();
    
    final private Lock lock = new ReentrantLock(true);
    
    
    /**
     * Creates a new grid, starting at {@code (0, 0, 0)}, which is split
     * into cubes with equal width, height and depth.
     * 
     * @param cube the size of the cube.
     */
    public Grid(float cube) {
        this(cube, cube, cube);
    }
    
    /**
     * Creates a new grid, staring at {@code (0, 0, 0)}, which is split
     * into beams with equal width and depth, and a set height.
     * 
     * @param square the width and height of the beam.
     * @param dy the height of the beam.
     */
    public Grid(float square, float dy) {
        this(square, dy, square);
    }
    
    /**
     * Creates a new grid, starting at {@code (0, 0, 0)}, which is
     * split into beams with the given width, height and depth.
     * 
     * @param dx the width of the beams.
     * @param dy the height of the beams.
     * @param dz the depth of the beams.
     */
    public Grid(float dx, float dy, float dz) {
        this(0, 0, 0, dx, dy, dz);
    }
    
    /**
     * Creates a new grid, starting at {@code (startX, startY, startZ)},
     * which is split into beams with the given width, height and depth.
     * 
     * @param startX the start x-coord.
     * @param startY the start y-coord.
     * @param startZ the start z-coord.
     * @param dx the width of the beams.
     * @param dy the height of the beams.
     * @param dz the depth of the beams.
     */
    public Grid(float startX, float startY, float startZ,
            float dx, float dy, float dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
    }
    
    
    /**
     * Calculates the hash vector of the given vector.
     * 
     * @param vec the vector to calculate the hash vector of.
     * @return the hash vector of {@code vec}.
     */
    private Vector3i calcHash(Vector3f vec) {
        return (vec == null
                ? null
                : calcHash(vec.x, vec.y, vec.z));
    }
    
    /**
     * Calculates the hash vector of the given coords.
     * 
     * @param x the x-coord to calculate the hash vector of.
     * @param y the y-coord to calculate the hash vector of.
     * @param z the z-coord to calculate the hash vector of.
     * @return the hash vector of the given coords.
     */
    private Vector3i calcHash(float x, float y, float z) {
        return new Vector3i(
                (int) ((x + startX) / dx),
                (int) ((y + startY) / dy),
                (int) ((z + startZ) / dz)
        );
    }
    
    /**
     * Updates the given item.
     * This update function should be called every time
     * the position has been updated.
     * 
     * @param item the item to be updated.
     */
    public void update(GridItem item) {
        Vector3f cur = item.getCurPosition();
        Vector3f prev = item.getPrevPosition();
        Vector3i curHash = calcHash(cur);
        Vector3i prevHash = calcHash(prev);
        //System.out.println(cur);
        //System.out.println(curHash);
        
        lock.lock();
        try {
            removeHash(item, prevHash);
            
            // Add the new item. If no set is available for that location,
            // create it and add the item.
            Set<GridItem> curSet = items.get(curHash);
            if (curSet == null) {
                curSet = new HashSet<GridItem>();
                curSet.add(item);
                items.put(curHash, curSet);
                
            } else {
                curSet.add(item);
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    
    /**
     * Removes the given item from the {@code items} set,
     * using the current location.
     * 
     * @param item the item to remove.
     */
    public void removeUsingCur(GridItem item) {
        Vector3f cur = item.getCurPosition();
        Vector3i curHash = calcHash(cur);
        
        lock.lock();
        try {
            removeHash(item, curHash);
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes the given item from the {@code items} set,
     * using the previous location.
     * 
     * @param item the item to remove.
     */
    public void removeUsingPrev(GridItem item) {
        Vector3f cur = item.getCurPosition();
        Vector3i curHash = calcHash(cur);
        
        lock.lock();
        try {
            removeHash(item, curHash);
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes the given item from the item set, assuming it has the
     * given hash. If it is the last item at that location,
     * remove the entire set.
     * 
     * @param hash the hash to check for.
     * @param item the item to remove.
     * 
     * One should use {@link #lock} before using this function.
     */
    private void removeHash(GridItem item, Vector3i hash) {
        if (hash == null) return;
        
        Set<GridItem> prevSet = items.get(hash);
        if (prevSet != null) {
            prevSet.remove(item);
            if (prevSet.isEmpty()) {
                items.remove(hash);
            }
        }
    }
    
    /**
     * @param x the location x-coord.
     * @param y the location y-coord.
     * @param z the location z-coord.
     * @return an array containing all items at the given location coords.
     *     Is never null.
     * 
     * @see #getItemsAtCell(int, int, int)
     */
    public GridItem[] getItemsAtLocation(float x, float y, float z) {
        return getItemsAtCell(
                (int) ((x + startX) / dx),
                (int) ((y + startY) / dy),
                (int) ((z + startZ) / dz)
        );
    }
    
    /**
     * @param x the cell x-coord.
     * @param y the cell y-coord.
     * @param z the cell z-coord.
     * @return an array containing all items at the given cell coords.
     *     Is never null.
     */
    public GridItem[] getItemsAtCell(int x, int y, int z) {
        Vector3i hash = new Vector3i(x, y, z);
        GridItem[] cell = null;
        
        lock.lock();
        try {
            Set<GridItem> set = items.get(hash);
            if (set == null) return new GridItem[0];
            cell = set.toArray(new GridItem[set.size()]);
            
        } finally {
            lock.unlock();
        }
        
        return cell;
    }
    
    /**
     * @param x1 a cell x-coord.
     * @param x2 a cell x-coord.
     * @param y1 a cell y-coord.
     * @param y2 a cell x-coord.
     * @param z1 a cell z-coord.
     * @param z2 a cell z-coord.
     * @return a 4D array representing the items in the cells cells between
     *     the given ranges.
     * 
     * @see #getItemsInCellRange(int, int, int, int, int, int)
     */
    public GridItem[][][][] getItemsInLocationRange(
            float x1, float x2,
            float y1, float y2,
            float z1, float z2) {
        return getItemsInCellRange(
                (int) ((x1 + startX) / dx), (int) ((x2 + startX) / dx),
                (int) ((y1 + startY) / dy), (int) ((y2 + startY) / dy),
                (int) ((z1 + startZ) / dz), (int) ((z2 + startZ) / dz)
        );
    }
    
    /**
     * Gets all items in the given range.
     * The returned value {@code cells} is such that {@code cells[x][y][z][i]}
     * represents the i'th item at the cell that is {@code x} cells in
     * the x-axis, {@code y} cells in the y-axis and {@code z} cells in
     * the z-axis relative to the minimum given coordinates.
     * 
     * @param x1 a cell x-coord.
     * @param x2 a cell x-coord.
     * @param y1 a cell y-coord.
     * @param y2 a cell x-coord.
     * @param z1 a cell z-coord.
     * @param z2 a cell z-coord.
     * @return a 4D array representing the items in the cells cells between
     *     the given ranges.
     */
    public GridItem[][][][] getItemsInCellRange(
            int x1, int x2,
            int y1, int y2,
            int z1, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        
        GridItem[][][][] cells = new GridItem[maxX - minX + 1]
                [maxY - minY + 1][maxZ - minZ + 1][];
        
        int cx = 0;
        for (int x = minX; x <= maxX; x++, cx++) {
            int cy = 0;
            for (int y = minY; y <= maxY; y++, cy++) {
                int cz = 0;
                for (int z = minZ; z <= maxZ; z++, cz++) {
                    cells[cx][cy][cz] = getItemsAtCell(x, y, z);
                }
            }
        }
        
        return cells;
    }
    
    public Set<Instance> getCollisions(Instance source) {
        Set<Instance> collidingSet = new HashSet<>();
        
        // The center hash.
        Vector3i ch = calcHash(source.getState().box.pos());
        
        // No locking is needed for the outer loops as this is only retrieval.
        // Note that double object removal is directly implemented by using
        // a set to store the data.
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3i hash;
                    if (x == 0 && y == 0 && z == 0) {
                        hash = ch;
                        
                    } else {
                        hash = new Vector3i(
                                ch.x + x,
                                ch.y + y,
                                ch.z + z);
                    }
                    
                    // The lock here is needed because of possible
                    // modifications while retrieving.
                    lock.lock();
                    try {
                        Set<GridItem> set = items.get(hash);
                        if (set == null) continue;
                        for (GridItem item : set) {
                            if (item == source) continue;
                            if (!(item instanceof Instance)) continue;
                            Instance instance = (Instance) item;
                            if (source.intersectsWith(instance)) {
                                collidingSet.add(instance);
                            }
                        }
                        
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
        
        return collidingSet;
    }
    
    /**
     * Debug function to visualize the grid.
     * 
     * @param cells
     * @param coord
     * @param estLength the estimated maximal length of the number
     *     of items in the axis described by {@code axis}.
     * @return 
     */
    public static String visualize(GridItem[][][][] cells, int axis,
            int estLength) {
        if (cells.length == 0 || cells[0].length == 0 || cells[0][0].length == 0) {
            return "Visualisation of empty cells.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(" ===== Begin visualization of grid ===== " + GS.LS);
        
        if (axis == 0) {
            String divider = MultiTool.fillRight("+", '-', estLength + 1);
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < cells[0][0].length; i++) {
                s.append(divider);
            }
            s.append('+');
            s.append(GS.LS);
            divider = s.toString();
            
            for (int x = 0; x < cells.length; x++) {
                sb.append("x = " + x + GS.LS);
                sb.append(divider);
                for (int y = 0; y < cells[x].length; y++) {
                    sb.append("|");
                    for (int z = 0; z < cells[x][y].length; z++) {
                        int c = 0;
                        for (int i = 0; i < cells[x][y][z].length; i++) {
                            c++;
                            GridItem item = cells[x][y][z][i];
                            if (item == null) {
                                sb.append(' ');
                            } else {
                                sb.append(item.getSimpleRepr());
                            }
                        }
                        for (; c < estLength; c++) {
                            sb.append(' ');
                        }
                        sb.append("|");
                    }
                    sb.append(GS.LS);
                    sb.append(divider);
                }
            }
            
        } else if (axis == 1) {
            return "todo";
            
        } else {
            return "todo";
        }
        
        sb.append(" ===== End visualization of grid =====");
        return sb.toString();
    }
    
    
    
    
    /// TMP BELOW --------------------------------------------------------------
    public static void main(String[] args) {
        Grid grid = new Grid(1, 2, 3);
        TestGridItem[] items = new TestGridItem[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = new TestGridItem();
            grid.update(items[i]);
        }
        for (int i = 0; i < 4; i++) {
            // Print.
            GridItem[][][][] cells = grid.getItemsInCellRange(
                    -i, i,
                    -i, i,
                    -i, i);
            System.out.println(visualize(cells, 0, 10));
            
            // Update.
            for (int j = 0; j < items.length; j++) {
                items[j].update();
                grid.update(items[j]);
            }
        }
        
        // Remove from grid.
        for (int i = 0; i < items.length; i++) {
            grid.removeUsingPrev(items[i]);
        }
        // Print.
        GridItem[][][][] cells = grid.getItemsInCellRange(
                -4, 4,
                -4, 4,
                -4, 4);
        System.out.println(visualize(cells, 0, 10));
    }
    
    public static class TestGridItem
            implements GridItem {
        final private static Random r = new Random();
        private static int idCounter = 0;
        final private int id = idCounter++;
        private Vector3f curLoc = new Vector3f(0, 0, 0);
        private Vector3f prevLoc = null;
        
        @Override
        public Vector3f getCurPosition() {
            return curLoc;
        }
        
        @Override
        public Vector3f getPrevPosition() {
            return prevLoc;
        }
        
        @Override
        public char getSimpleRepr() {
            return (char) ('A' + id);
        }
        
        public void update() {
            prevLoc = curLoc;
            curLoc = new Vector3f(
                    curLoc.x + r.nextInt(3) - 1,
                    curLoc.y + r.nextInt(3) - 1,
                    curLoc.z + r.nextInt(3) - 1
            );
            
            //System.out.println(curLoc);
        }
        
        
    }
    
    
}
