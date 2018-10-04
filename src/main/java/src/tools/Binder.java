package src.tools;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Binder {

    static ArrayList<IntBuffer> vaos = new ArrayList<>();
    static ArrayList<IntBuffer> vbos = new ArrayList<>();

    public static IntBuffer loadVAO(GL3 gl, float[] vertices, float[] uv,
                                    float[] normals, int[] indices) {

        IntBuffer vao = createVAO(gl);

        storeToIndicesList(gl, Buffers.newDirectIntBuffer(indices));
        storeToAttributeList(gl, 0, 3, Buffers.newDirectFloatBuffer(vertices));
        storeToAttributeList(gl, 1, 2, Buffers.newDirectFloatBuffer(uv));
        storeToAttributeList(gl, 2, 3, Buffers.newDirectFloatBuffer(normals));
        
        unbindVAO(gl);

        return vao;
    }

    public static IntBuffer loadVAO(GL3 gl, List<Float> v, List<Float> u,
                                    List<Float> n, List<Integer> i) {

        float[] vertices = toFloatArray(v);
        float[] uv = toFloatArray(u);
        float[] normals = toFloatArray(n);
        int[] indices = toIntegerArray(i);

        IntBuffer vao = createVAO(gl);

        storeToIndicesList(gl, Buffers.newDirectIntBuffer(indices));
        storeToAttributeList(gl, 0, 3, Buffers.newDirectFloatBuffer(vertices));
        storeToAttributeList(gl, 1, 2, Buffers.newDirectFloatBuffer(uv));
        storeToAttributeList(gl, 2, 3, Buffers.newDirectFloatBuffer(normals));

        unbindVAO(gl);

        return vao;
    }

    public static IntBuffer loadVAO(GL3 gl, float[] vertices, int size) {

        IntBuffer vao = createVAO(gl);

        storeToAttributeList(gl, 0,  size, Buffers.newDirectFloatBuffer(vertices));

        unbindVAO(gl);

        return vao;
    }

    private static IntBuffer createVAO(GL3 gl) {
        IntBuffer ID = Buffers.newDirectIntBuffer(1);
        vaos.add(ID);
        gl.glGenVertexArrays(1, ID);
        gl.glBindVertexArray(ID.get(0));
        return ID;
    }

    private static void storeToAttributeList(GL3 gl, int attrNum, int size,
                                             FloatBuffer data) {

        IntBuffer vbo = Buffers.newDirectIntBuffer(1);
        vbos.add(vbo);
        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                data.capacity() * Buffers.SIZEOF_FLOAT, data,
                GL2.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(attrNum, size, GL2.GL_FLOAT, false, 0, 0);
    }

    private static void storeToIndicesList(GL3 gl, IntBuffer indices) {
        IntBuffer vbo = Buffers.newDirectIntBuffer(1);
        vbos.add(vbo);
        gl.glGenBuffers(1,vbo);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,
                indices.capacity() * Buffers.SIZEOF_INT, indices, GL2.GL_STATIC_DRAW);

    }

    public static void unbindVAO(GL3 gl) {
        gl.glBindVertexArray(0);
    }

    public void clean(GL3 gl) {
        for(IntBuffer i : vaos){
            gl.glDeleteVertexArrays(1, i);
        }
        for(IntBuffer j : vbos) {
            gl.glDeleteBuffers(1, j);
        }
    }

    private static float[] toFloatArray(List<Float> floats) {
        float[] floatar = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            floatar[i] = floats.get(i);
        }
        return floatar;
    }

    private static int[] toIntegerArray(List<Integer> integers) {
        int[] intar = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            intar[i] = integers.get(i);
        }
        return intar;
    }
}
