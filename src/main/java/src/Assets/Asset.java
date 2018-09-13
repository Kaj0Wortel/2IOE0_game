package src.Assets;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.IntBuffer;
    
public class Asset {

    private IntBuffer vao;
    private int nrV;

    public Asset(IntBuffer vao, int nrV){
        this.vao = vao;
        this.nrV = nrV;
    }

    public Asset(){

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
}
