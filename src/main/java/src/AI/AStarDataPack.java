
package src.AI;

import org.joml.Vector3f;


// Own imports


// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class AStarDataPack {
    
    final public Vector3f pos;
    final public float rot;
    final public float v;
    
    public AStarDataPack(Vector3f pos, float rot, float v) {
        this.pos = pos;
        this.rot = rot;
        this.v = v;
    }
    
    
}
