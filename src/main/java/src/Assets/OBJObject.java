
package src.Assets;

import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import src.OBJ.MTLObject;
import src.tools.Binder;

import java.nio.IntBuffer;
import java.util.List;


// Own imports


// Java imports


/**
 * 
 * <url>http://paulbourke.net/dataformats/obj/</url>
 */
public class OBJObject extends Object {
    
    public OBJObject(String name) {
        super(name);
    }
    
    public void setData(GL2 gl, List<Float> vertices, List<Float> tex,
                        List<Float> normals, List<Integer> indices) {

        vao = Binder.loadVAO(gl,
                toFloatArray(vertices), toFloatArray(tex),
                toFloatArray(normals), toIntegerArray(indices));
        nrV = indices.size();

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

    public String getName() {
        return name;
    }

    public IntBuffer getVao() {
        return vao;
    }

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
}
