
package src.OBJ;


// Own imports


// Java imports


/**
 * 
 */
public class FaceElement {
    final private int[] vert;
    final private int[] tex;
    final private int[] norm;
    
    
    FaceElement(int[] vert, int[] tex, int[] norm) {
        this.vert = vert;
        this.tex = tex;
        this.norm = norm;
    }
    
    
    /**
     * @return the pointer to the geometric vertex.
     */
    public int[] getVert() {
        return vert;
    }
    
    /**
     * @return the pointer to the texture vertex.
     */
    public int[] getTex() {
        return tex;
    }
    
    /**
     * @return the pointer to the normal vertex.
     */
    public int[] getNorm() {
        return norm;
    }
    
    
}
