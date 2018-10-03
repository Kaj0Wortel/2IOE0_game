
package src.Assets;

import com.jogamp.opengl.GL3;
import org.joml.Vector3f;
import src.OBJ.MTLObject;
import src.tools.Binder;
import src.tools.Box3f;

import java.nio.IntBuffer;
import java.util.List;


// Own imports


// Java imports


/**
 * 
 * <url>http://paulbourke.net/dataformats/obj/</url>
 */
public class OBJObject extends Object {
    float minX = 0;
    float maxX = 0;
    float minY = 0;
    float maxY = 0;
    float minZ = 0;
    float maxZ = 0;
    
    public OBJObject(String name) {
        super(name);
    }
    
    public void setData(GL3 gl, List<Float> vertices, List<Float> tex,
                        List<Float> normals, List<Integer> indices) {

        vao = Binder.loadVAO(gl,
                vertices, tex, normals, indices);
        nrV = indices.size();

    }

    public String getName() {
        return name;
    }
    
    @Override
    public IntBuffer getVao() {
        return vao;
    }
    
    @Override
    public int getNrV() {
        return nrV;
    }

    public void setVao(IntBuffer vao) {
        this.vao = vao;
    }

    public void setNrV(int nrV) {
        this.nrV = nrV;
    }

    public void setMltObject(MTLObject mltObject) {
        this.mltObject = mltObject;
    }

    @Override
    public Vector3f getCenteredPosition() {
        return null;
    }
    
    public void setMinMax(
            float minX, float maxX,
            float minY, float maxY,
            float minZ, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    public float getMinX() {
        return minX;
    }
    
    public float getMaxX() {
        return maxX;
    }
    
    public float getMinY() {
        return minY;
    }
    
    public float getMaxY() {
        return maxY;
    }
    
    public float getMinZ() {
        return minZ;
    }
    
    public float getMaxZ() {
        return maxZ;
    }
    
    public Box3f createBoundingBox() {
        return new Box3f(maxX - minX, maxY - minY, maxZ - minZ);
    }
    
    
}
