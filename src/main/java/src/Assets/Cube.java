package src.Assets;

import com.jogamp.opengl.GL2;
import src.tools.Binder;

public class Cube extends Asset{
    public Cube(GL2 gl, Binder binder) {
        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };

        int[] indices = {
          0,1,3,3,1,2
        };

        float[] uv = {
                0,0,
                1,0,
                1,1,
                0,1
        };

        float[] normals = {
                0,0,1,
                0,0,1,
                0,0,1,
                0,0,1
        };

        super.setVao(binder.LoadToVAO(gl,vertices,uv,normals,indices));
        super.setNrV(indices.length);
    }
}
