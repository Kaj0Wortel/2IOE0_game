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

    private float speed;
    private float rotationSpeed;

    private OBJTexture model;

    public Instance(Vector3f position, float size, float rotx, float roty, float rotz, OBJTexture model) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.model = model;
        this.speed = 1;
        this.rotationSpeed = 1f;
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

    @Deprecated
    public void rotx() {
        rotx(3f);
    }

    public void rotx(float rot) {
        rotx += rot;
        rotx %= 360;
    }

    @Deprecated
    public void roty() {
        roty(3f);
    }
    
    public void roty(float rot) {
        roty += rot;
        roty %= 360;
    }

    @Deprecated
    public void moveup() {
        moveup(0.1f);
    }

    public void moveup(float amt) {
        position.y += amt;
    }

    public void draw(GL2 gl, ShaderProgram shader){
        shader.loadModelMatrix(gl, getTransformationMatrix());
        shader.loadTextureLightValues(gl, model.getTexture().getShininess(),
                model.getTexture().getReflectivity());
        
        gl.glBindVertexArray(model.getAsset().getVao().get(0));
        gl.glEnableVertexAttribArray(0);
        //gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glDrawElements(GL2.GL_TRIANGLES, model.getAsset().getNrV(),
                GL2.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        //gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void moveForward(){
        position.x += speed * Math.sin((float) Math.toRadians(roty));
        position.z -= speed * Math.cos((float) Math.toRadians(roty));
    }

    public void moveBackwards(){
        position.x -= speed * Math.sin((float) Math.toRadians(roty));
        position.z += speed * Math.cos((float) Math.toRadians(roty));
    }

    public void turnLeft(){
        System.out.println("hi");
        roty += rotationSpeed;
        roty %= 360;
    }

    public void turnRight(){
        roty -= rotationSpeed;
        roty %= 360;
    }

    public float getRotx() {
        return rotx;
    }

    public float getRoty() {
        return roty;
    }

    public float getRotz() {
        return rotz;
    }
}
