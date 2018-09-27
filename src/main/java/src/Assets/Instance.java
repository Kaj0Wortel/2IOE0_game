package src.Assets;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Physics.PStruct;
import src.Physics.Physics;
import src.Shaders.ShaderProgram;


public class Instance {

    private Vector3f position;
    private float size;
    private float rotx;
    private float roty;
    private float rotz;
    private float integratedRotation;

    private float velocity;
    private float collisionVelocity;
    private float verticalVelocity;
    private float rotationSpeed;

    private OBJTexture model;

    public Instance(Vector3f position, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.model = model;
        this.velocity = 0;
        this.collisionVelocity = 0;
        this.verticalVelocity = 0;
        this.integratedRotation = integratedRotation;
        this.rotationSpeed = 5f;
    }

    public Matrix4f getTransformationMatrix(){
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(position);
        transformationMatrix.rotate((float) Math.toRadians(rotx), 1, 0, 0);
        transformationMatrix.rotate(
                (float) Math.toRadians(roty + integratedRotation), 0, 1, 0);
        transformationMatrix.rotate((float) Math.toRadians(rotz), 0, 0, 1);
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

    public void draw(GL3 gl, ShaderProgram shader){
        shader.loadModelMatrix(gl, getTransformationMatrix());
        shader.loadTextureLightValues(gl, model.getTextureImg().getShininess(),
                model.getTextureImg().getReflectivity());
        
        gl.glBindVertexArray(model.getAsset().getVao().get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glDrawElements(GL2.GL_TRIANGLES, model.getAsset().getNrV(),
                GL2.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }

    public Vector3f getPosition() {
        return position;
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
    
    public void movement(int turn, int acc, double vertV, long dt) {
        Physics physics = new Physics();
        
        // TODO: INPUT
        double linearAcceleration = 0.4;
        double rotationalVelocity = Math.PI/10;
        double maxLinearVelocity = 5;
        double frictionConstant = 0.2;
        double gravity = -1;
        
        // Physiscs requires roty to be in degrees
        roty = (float) Math.toRadians(roty);
        
        // TEMP: do not jump if already jumping
        if (verticalVelocity == 0)
            verticalVelocity += vertV;

        PStruct curStruct = new PStruct(new Vector3f(
                -position.z, -position.x, position.y), velocity, roty, 
                collisionVelocity, gravity, verticalVelocity);
        //System.out.println("turn: " +turn+ ", acc: " +acc+ "");
        curStruct = physics.calcPhysics(turn, acc, linearAcceleration, 
                rotationalVelocity, maxLinearVelocity, dt / 160f, 
                frictionConstant, curStruct);
        // 3D-2D conversion (might change physics to directly support 3D input)
        roty = (float) curStruct.rot;
        velocity = (float) curStruct.v;
        position.z = -curStruct.pos.x;
        position.x = -curStruct.pos.y;
        position.y = curStruct.pos.z;
        collisionVelocity = (float) curStruct.colV;
        verticalVelocity = (float) curStruct.vertV;
        //System.out.println(/*velocity + */": (" + -position.z + ", " + -position.x 
        //        + "), "/* + roty*/);
        
        // Instance requires roty to be stored in degrees
        roty = (float) (Math.toDegrees(roty) % 360);
    }
    
    
}
