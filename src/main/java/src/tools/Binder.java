package src.tools;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import src.Assets.Asset;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Binder {

    ArrayList<IntBuffer> vaos = new ArrayList<>();
    ArrayList<IntBuffer> vbos = new ArrayList<>();

    public Asset LoadAndCreateToVAO(GL2 gl, float[] vertices, float[] uv, float[] normals, int[] indices){
        IntBuffer vao = createVAO(gl);
        storeToIndicesList(gl, IntBuffer.wrap(indices));
        storeToAttributeList(gl,0, 3,FloatBuffer.wrap(vertices));
        storeToAttributeList(gl,1, 2,FloatBuffer.wrap(uv));
        storeToAttributeList(gl, 2, 3,FloatBuffer.wrap(normals));

        unbindVAO(gl);

        return new Asset(vao, indices.length);
    }

    public IntBuffer LoadToVAO(GL2 gl, float[] vertices, float[] uv, float[] normals, int[] indices){
        IntBuffer vao = createVAO(gl);
        storeToIndicesList(gl, IntBuffer.wrap(indices));
        storeToAttributeList(gl,0, 3,FloatBuffer.wrap(vertices));
        storeToAttributeList(gl,1, 2,FloatBuffer.wrap(uv));
        storeToAttributeList(gl, 2, 3,FloatBuffer.wrap(normals));
        unbindVAO(gl);

        return vao;
    }

    public IntBuffer createVAO(GL2 gl){
        IntBuffer ID = Buffers.newDirectIntBuffer(1);
        vaos.add(ID);
        gl.glGenVertexArrays(1,ID);
        gl.glBindVertexArray(ID.get(0));
        return ID;
    }

    public void storeToAttributeList(GL2 gl, int attrNum, int size,FloatBuffer data){
        IntBuffer vbo = Buffers.newDirectIntBuffer(1);
        vbos.add(vbo);
        gl.glGenBuffers(1,vbo);
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(gl.GL_ARRAY_BUFFER, data.capacity() * Buffers.SIZEOF_FLOAT, data, gl.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(attrNum,size,gl.GL_FLOAT,false,0,0);
    }

    public void storeToIndicesList(GL2 gl, IntBuffer indices){
        IntBuffer vbo = Buffers.newDirectIntBuffer(1);
        vbos.add(vbo);
        gl.glGenBuffers(1,vbo);
        gl.glBindBuffer(gl.GL_ELEMENT_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(gl.GL_ELEMENT_ARRAY_BUFFER,indices.capacity() * Buffers.SIZEOF_INT, indices,gl.GL_STATIC_DRAW);

    }

    public void unbindVAO(GL2 gl){
        gl.glBindVertexArray(0);
    }

    public void clean(GL2 gl){
        for(IntBuffer i : vaos){
            gl.glDeleteVertexArrays(1,i);
        }
        for(IntBuffer j : vbos){
            gl.glDeleteBuffers(1,j);
        }
    }
}
