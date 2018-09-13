
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
    
    public void bind(GL2 gl) {
        for (OBJObject obj : objObjects) {
            //obj.bind(gl);
        }
    }
    
    public void unbind(GL2 gl) {
        for (OBJObject obj : objObjects) {
            obj.unbind(gl);
        }
    }
    
    public void draw(GL2 gl, GLU glu) {
        for (OBJObject obj : objObjects) {
            obj.draw(gl, glu);
        }
    }
    
    /**
     * Clears all obj objects and makes them available for GC.
     */
    void clear() {
        for (OBJObject obj : objObjects) {
            obj.clear();
        }
        
        objObjects.clear();
        objObjects = null;
    }
    
    
}
