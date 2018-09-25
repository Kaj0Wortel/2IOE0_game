package src.Assets;

import com.jogamp.opengl.GL3;
import org.joml.Vector3f;
import src.tools.Binder;

import java.nio.IntBuffer;

public class Terrain extends OBJObject{

    final private int number_of_tiles = 32;
    final private int total_size = 250;

    private IntBuffer vao;
    private int nrV;

    public Terrain(GL3 gl){
        super("Terrain");
        vao = generateTerrain(gl);
    }

    private IntBuffer generateTerrain(GL3 gl){
        int total_vertices = number_of_tiles * number_of_tiles;
        float[] vertices = new float[3 * total_vertices];
        float[] normals = new float[3 * total_vertices];
        float[] tex = new float[2 * total_vertices];
        int[] indices = new int[6 * (number_of_tiles - 1) * (number_of_tiles - 1)];

        int currentVertex = 0;
        for(int i = 0; i < number_of_tiles; i++){
            for(int j = 0; j < number_of_tiles; j++){
                vertices[3*currentVertex] = j / (number_of_tiles - 1) * total_size;
                vertices[3*currentVertex+1] = 0;
                vertices[3*currentVertex+2] = i / (number_of_tiles - 1) * total_size;
                tex[2*currentVertex] = j / (number_of_tiles - 1);
                tex[2*currentVertex+1] = i / (number_of_tiles - 1);
                normals[3*currentVertex] = 0;
                normals[3*currentVertex+1] = 1;
                normals[3*currentVertex+2] = 0;
                currentVertex++;
            }
        }

        int index = 0;
        for(int i = 0; i < number_of_tiles-1; i++){
            for(int j = 0; j < number_of_tiles-1; j++){
                int antiClock1 = (i * number_of_tiles) + j;
                int antiClock2 = antiClock1 + 1;
                int antiClock3 = ((i+1) * number_of_tiles) + j;
                int antiClock4 = antiClock3 + 1;
                indices[index++] = antiClock1;
                indices[index++] = antiClock3;
                indices[index++] = antiClock2;
                indices[index++] = antiClock2;
                indices[index++] = antiClock3;
                indices[index++] = antiClock4;
            }
        }

        nrV = indices.length;
        return Binder.loadVAO(gl, vertices,tex,normals,indices);
    }

    public IntBuffer getVao() {
        return vao;
    }

    public int getNrV() {
        return nrV;
    }

    public Vector3f getCenteredPosition() {
        return new Vector3f(-(total_size/2),0,-(total_size/2));
    }
}
