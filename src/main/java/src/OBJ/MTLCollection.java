
package src.OBJ;


// Own imports


// Java imports

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 */
public class MTLCollection
        implements Iterable<MTLObject> {
    List<MTLObject> mtlObjects = new ArrayList<>();
    
    
    @Override
    public Iterator<MTLObject> iterator() {
        return mtlObjects.iterator();
    }
    
    public void add(MTLObject obj) {
        mtlObjects.add(obj);
    }
    
    @SuppressWarnings("IncompatibleEquals")
    public MTLObject get(String str) {
        for (MTLObject mtl : mtlObjects) {
            if (mtl.equals(str)) return mtl;
        }
        
        return null;
    }
    
    
}
