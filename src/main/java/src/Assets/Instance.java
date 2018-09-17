package src.Assets;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Shaders.ShaderProgram;

public class Instance {

    private Vector3f position;
    private float size;
    private float rotx;
    private float roty;
    private float rotz;

    private OBJTexture model;

    public Instance(Vector3f position, float size, float rotx, float roty, float rotz, OBJTexture model) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.model = model;
    }

    public Matrix4f getTransformationMatrix(){
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(position);
        transformationMatrix.rotate((float) Math.toRadians(rotx),1,0,0);
        transformationMatrix.rotate((float) Math.toRadians(roty),0,1,0);
        transformationMatrix.rotate((float) Math.toRadians(rotz),0,0,1);
        transformationMatrix.scale(size,size,size);

        return transformationMatrix;
    }

    public OBJTexture getModel() {
        return model;
    }

    public void rotx(){
        rotx += 3f;
    }

    public void roty(){
        roty += 3f;
    }

    public void draw(GL2 gl, ShaderProgram shader){
        shader.loadModelMatrix(gl, getTransformationMatrix());

        gl.glBindVertexArray(model.getAsset().getVao().get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glDrawElements(GL2.GL_TRIANGLES, model.getAsset().getNrV(), gl.GL_UNSIGNED_INT,0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }
}
