
package src.grid;

import org.joml.Vector3f;


// Own imports


// Java imports


/**
 * 
 */
public interface GridItem {
    
    /**
     * @return a vector representing the current location of the object.
     */
    public Vector3f getCurPosition();
    
    /**
     * @return a vector representing the previous location of the object.
     *     Use {@code null} if no previous location available.
     */
    public Vector3f getPrevPosition();
    
    /**
     * @return a simple char representation of the object.
     */
    public char getSimpleRepr();
    
}
