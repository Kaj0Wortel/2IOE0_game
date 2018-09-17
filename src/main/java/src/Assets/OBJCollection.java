
package src.Assets;


// Own imports


// Java imports

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 */
public class OBJCollection
        implements Iterable<OBJObject> {
    List<OBJObject> objObjects = new ArrayList<>();
    
    
    @Override
    public Iterator<OBJObject> iterator() {
        return objObjects.iterator();
    }
    
    public void add(OBJObject obj) {
        objObjects.add(obj);
    }
    
    /**
     * Clears all obj objects and makes them available for GC.
     */
    public void clear() {
        for (OBJObject obj : objObjects) {

        }
        
        objObjects.clear();
        objObjects = null;
    }
    
    
}
