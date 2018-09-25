
package src.OBJ;

import src.tools.MultiTool;


// Own imports


// Java imports


/**
 * 
 */
public class FaceElement {
    final private int vPointer;
    final private int tPointer;
    final private int nPointer;
    
    
    public FaceElement(int vPointer, int tPointer, int nPointer) {
        this.vPointer = vPointer;
        this.tPointer = tPointer;
        this.nPointer = nPointer;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FaceElement)) return false;
        FaceElement face = (FaceElement) obj;
        return this.vPointer == face.vPointer &&
                this.tPointer == face.tPointer &&
                this.nPointer == face.nPointer;
    }
    
    @Override
    public int hashCode() {
        return MultiTool.calcHashCode(vPointer, tPointer, nPointer);
    }
    
    
}
