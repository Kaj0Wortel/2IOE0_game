
package src.Assets;


// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector3f;


// Own imports
import src.tools.Binder;
import src.tools.Box3f;
import src.OBJ.MTLObject;


// Java imports
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * <url>http://paulbourke.net/dataformats/obj/</url>
 */
public class OBJObject
        extends GraphicsObject {
    private float minX = 0;
    private float maxX = 0;
    private float minY = 0;
    private float maxY = 0;
    private float minZ = 0;
    private float maxZ = 0;
    
    

    protected List<MTLObject> mtlObjects = new ArrayList<>();
    protected List<IntBuffer> vaos = new ArrayList<>();
    protected List<Integer> nrVs = new ArrayList<>();
    
    
    public OBJObject(String name) {
        super(name);
    }
    
    public void addData(GL3 gl, List<Float> vertices, List<Float> tex,
                        List<Float> normals, List<Integer> indices,
                        MTLObject mtl) {

        vaos.add(Binder.loadVAO(gl, vertices, tex, normals, indices));
        nrVs.add(indices.size());
    }

    public String getName() {
        return name;
    }
    
    @Override
    public List<IntBuffer> getVao() {
        return vaos;
    }
    
    @Override
    public int getVao(int id) {
        return getVao().get(id).get(0);
    }
    
    @Override
    public List<Integer> getNrV() {
        return nrVs;
    }
    
    @Override
    public int getNrV(int id) {
        return getNrV().get(id);
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
    
    @Override
    public int size() {
        return mtlObjects.size();
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
