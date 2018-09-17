package src.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    Matrix4f viewMatrix = new Matrix4f();

    public Camera (Vector3f position, float pitch, float yaw, float roll){
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;

        calculateViewMatrix();
    }

    public void calculateViewMatrix(){
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(pitch), new Vector3f(1,0,0));
        viewMatrix.rotate((float)Math.toRadians(yaw), new Vector3f(0,1,0));
        viewMatrix.rotate((float)Math.toRadians(roll), new Vector3f(0,0,1));
        viewMatrix.translate(new Vector3f(position).negate());
    }

    public Matrix4f getViewMatrix(){
        calculateViewMatrix();
        return viewMatrix;
    }

    public void YawLeft(){
        yaw += 3f;
        yaw %= 360;
    }

    public void YawRight(){
        yaw -= 3f;
        yaw %= 360;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

}
