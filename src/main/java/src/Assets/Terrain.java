package src.Assets;

import com.jogamp.opengl.GL3;
import org.joml.Vector3f;
import src.tools.Binder;

import java.nio.IntBuffer;


public class Terrain
        extends OBJObject {

    final private int NUMBER_OF_TILES = 32;
    final private int TOTAL_SIZE = 250;

    public Terrain(GL3 gl) {
        super("Terrain");
        vaos.add(generateTerrain(gl));
    }

    private IntBuffer generateTerrain(GL3 gl) {
        int total_vertices = NUMBER_OF_TILES * NUMBER_OF_TILES;
        float[] vertices = new float[3 * total_vertices];
        float[] normals = new float[3 * total_vertices];
        float[] tex = new float[2 * total_vertices];
        int[] indices = new int[6 * (NUMBER_OF_TILES - 1) * (NUMBER_OF_TILES - 1)];

        int currentVertex = 0;
        for(int i = 0; i < NUMBER_OF_TILES; i++) {
            for(int j = 0; j < NUMBER_OF_TILES; j++) {
                vertices[3*currentVertex] = j / (NUMBER_OF_TILES - 1) * TOTAL_SIZE;
                vertices[3*currentVertex+1] = 0;
                vertices[3*currentVertex+2] = i / (NUMBER_OF_TILES - 1) * TOTAL_SIZE;
                tex[2*currentVertex] = j / (NUMBER_OF_TILES - 1);
                tex[2*currentVertex+1] = i / (NUMBER_OF_TILES - 1);
                normals[3*currentVertex] = 0;
                normals[3*currentVertex+1] = 1;
                normals[3*currentVertex+2] = 0;
                currentVertex++;
            }
        }

        int index = 0;
        for(int i = 0; i < NUMBER_OF_TILES-1; i++) {
            for(int j = 0; j < NUMBER_OF_TILES-1; j++){
                int antiClock1 = (i * NUMBER_OF_TILES) + j;
                int antiClock2 = antiClock1 + 1;
                int antiClock3 = ((i+1) * NUMBER_OF_TILES) + j;
                int antiClock4 = antiClock3 + 1;
                indices[index++] = antiClock1;
                indices[index++] = antiClock3;
                indices[index++] = antiClock2;
                indices[index++] = antiClock2;
                indices[index++] = antiClock3;
                indices[index++] = antiClock4;
            }
        }

        nrVs.add(indices.length);
        return Binder.loadVAO(gl, vertices,tex,normals,indices);
    }
    
    @Override
    public Vector3f getCenteredPosition() {
        return new Vector3f(-(TOTAL_SIZE/2),0,-(TOTAL_SIZE/2));
    }
    
    
}
