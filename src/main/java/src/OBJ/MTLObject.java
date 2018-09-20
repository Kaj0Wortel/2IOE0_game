

package src.OBJ;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import org.joml.Vector3f;


// Own imports


// Java imports


/**
 * 
 */
public class MTLObject {
    
    /**
     * <url>http://paulbourke.net/dataformats/mtl/</url>
     */
    final public static int AMBIENT_OFF = 0;
    final public static int AMBIENT_ON = 1;
    final public static int HIGHLIGHT_ON = 2;
    final public static int RAY_ON = 3;
    final public static int GLASS_ON_RAY_ON = 4;
    final public static int FRESNEL_ON_RAY_ON = 5;
    final public static int REF_ON_FRESNEL_OFF_RAY_ON = 6;
    final public static int REF_ON_FRESNEL_ON_RAY_ON = 7;
    final public static int REF_ON_RAY_OFF = 8;
    final public static int GLASS_ON_RAY_OFF = 9;
    final public static int CAST_SHADOW = 10;
    
    
    final private String name;
    
    Vector3f ambiant;
    Vector3f diffuse;
    Vector3f specular;
    int illumination;
    float dissolve;
    float opticalDensity;
    
    
    MTLObject(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(name);
            
        } else if (obj instanceof MTLObject) {
            MTLObject mtl = (MTLObject) obj;
            return this.name.equals(mtl.name);
            
        } else return false;
    }
    
    public void draw(GL2 gl, GLU glu) {
        
    }
    
    /**
     * Clears this mtl object and makes it available for GC.
     */
    void clear() {
        ambiant = null;
        diffuse = null;
        specular = null;
    }
    
    
}
