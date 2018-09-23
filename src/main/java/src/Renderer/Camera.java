package src.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Instance;

public class Camera {

    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    Matrix4f viewMatrix = new Matrix4f();

    int speed = 2;

    private Instance focusedOn;
    private Vector3f previousPosition;
    private boolean onPlayer;

    private float distanceToAsset = 15;
    private float angleAroundAsset = 0;

    public Camera (Vector3f position, float pitch, float yaw, float roll){
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.onPlayer = false;

        calculateViewMatrix();
    }

    public void calculateViewMatrix(){
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        Vector3f negPos = new Vector3f(-position.x, -position.y, -position.z);
        viewMatrix.translate(negPos);
    }

    public Matrix4f getViewMatrix(){
        calculateViewMatrix();
        return viewMatrix;
    }

    public void YawLeft(){
        yaw -= 5f;
        yaw %= 360;
    }

    public void YawRight(){
        yaw += 5f;
        yaw %= 360;
    }

    public void MoveForward(){
        position.x += speed * Math.sin((float) Math.toRadians(yaw));
        position.z -= speed * Math.cos((float) Math.toRadians(yaw));
    }

    public void MoveBackwards(){
        position.x -= speed * Math.sin((float) Math.toRadians(yaw));
        position.z += speed * Math.cos((float) Math.toRadians(yaw));
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setFocus(Instance instance){
        previousPosition = new Vector3f(position);
        focusedOn = instance;
        onPlayer = true;
        position = new Vector3f(instance.getPosition());
    }

    public void removeFocus(){
        focusedOn = null;
        onPlayer = false;
        position = new Vector3f(previousPosition);
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    public boolean isOnPlayer() {
        return onPlayer;
    }

    public void moveToInstance(){
        distanceToAsset -= 0.1f;
    }

    public void moveAwayFromInstance(){
        distanceToAsset += 0.1f;
    }

    public void rotateAroundAssetR(){
        angleAroundAsset += 0.1f;
    }

    public void rotateAroundAssetL(){
        angleAroundAsset -= 0.1f;
    }

    public void movePitchDown(){
        pitch -= 0.1f;
    }

    public void movePitchUp(){
        pitch += 0.1f;
    }

    public void calculateInstanceValues(){
        float angle = focusedOn.getRoty() + angleAroundAsset;
        float horDistance = (float) (distanceToAsset * Math.cos(Math.toRadians(pitch)));
        float verDistance = (float) (distanceToAsset * Math.sin(Math.toRadians(pitch)));
        System.out.println(verDistance);
        float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
        float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));
        position.x = focusedOn.getPosition().x + x;
        position.y = focusedOn.getPosition().y + verDistance;
        position.z = focusedOn.getPosition().z + z;
        this.yaw = 180 - (focusedOn.getRoty() + angleAroundAsset);
    }

}
