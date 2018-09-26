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

    private float distanceToAsset = 30;
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
    
    public void yaw(float amt) {
        yaw += amt;
        yaw %= 360;
    }

    @Deprecated
    public void yawLeft(){
        yaw(-5f);
    }
    
    @Deprecated
    public void yawRight(){
        yaw(5f);
    }
    
    /**
     * Moves the camera forward or backwards.
     * A positive factor let's it move forward, negative backwards.
     * One should use {@code 1.0f} or {@code -1.0f} for default behaviour.
     * 
     * @param factor 
     */
    public void move(float factor) {
        position.x += speed * Math.sin((float) Math.toRadians(yaw)) * factor;
        position.z -= speed * Math.cos((float) Math.toRadians(yaw)) * factor;
    }
    
    @Deprecated
    public void moveForward() {
        move(1.0f);
    }
    
    @Deprecated
    public void moveBackwards(){
        move(-1.0f);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public void setFocus(Instance instance){
        previousPosition = new Vector3f(position);
        focusedOn = instance;
        onPlayer = true;
        position = new Vector3f(instance.getPosition());
        pitch = 20;
    }

    public void removeFocus() {
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
        float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
        float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));
        position.x = focusedOn.getPosition().x + x;
        position.y = focusedOn.getPosition().y + verDistance;
        position.z = focusedOn.getPosition().z + z;
        this.yaw = - (focusedOn.getRoty() + angleAroundAsset);
        this.yaw %= 360;

    }

    public Matrix4f getInverseViewMatrix(){
        return new Matrix4f(viewMatrix).invert();
    }

}
