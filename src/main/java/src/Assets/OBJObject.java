
package src.Assets;


// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector3f;
import src.OBJ.MTLObject;
import src.tools.Binder;
import src.tools.PosHitBox3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

// Own imports
// Java imports


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
        vaos.add(Binder.loadVAO(gl,
                toFloatArray(vertices), toFloatArray(tex),
                toFloatArray(normals), toIntegerArray(indices)));
        nrVs.add(indices.size());
        mtlObjects.add(mtl);
    }

    private float[] toFloatArray(List<Float> floats){
        float[] floatar = new float[floats.size()];
        for(int i = 0; i < floats.size(); i++){
            floatar[i] = floats.get(i);
        }
        return floatar;
    }

    private int[] toIntegerArray(List<Integer> integers){
        int[] intar = new int[integers.size()];
        for(int i = 0; i < integers.size(); i++){
            intar[i] = integers.get(i);
        }
        return intar;
    }




    /*
    public void addData(GL3 gl, List<Float> vertices, List<Float> tex,
                        List<Float> normals, List<Integer> indices,
                        MTLObject mtl) {
        vaos.add(Binder.loadVAO(gl, vertices, tex, normals, indices));
        nrVs.add(indices.size());
    }*/

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
    public List<MTLObject> getMaterials() {
        return mtlObjects;
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
    
    public PosHitBox3f createBoundingBox() {
        return new PosHitBox3f(
                new Vector3f(), // Pos
                new Vector3f(minX, minY, minZ), // Rel pos
                new Vector3f(maxX - minX, maxY - minY, maxZ - minZ)); // Dim
    }
    
    
}
