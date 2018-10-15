package src.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.instance.Instance;
import src.Assets.instance.Instance.State;

import static java.lang.Float.max;
import static java.lang.Math.signum;
import java.util.Observable;
import java.util.Observer;
import src.GS;

public class Camera
        implements Observer {
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

    private float previousRotation = 0;
    private float currentRotation = 0;
    private boolean rubberBandEnabled = true;
    private float maxRubberAngle = 15f;
    private float rubberSmoothness = 0.25f;
    private float rubberSpeed = 0.15f;
    private float minRubberDistance = 25f;
    private float rubberVelocityFactor = 0.5f;
    private float fovVelocityFactor = 2.0f;
    
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
    public void yawLeft() {
        yaw(-5f);
    }
    
    @Deprecated
    public void yawRight() {
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
    public void moveBackwards() {
        move(-1.0f);
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }
    
    public void setFocus(Instance instance) {
        if (instance == null || instance.equals(focusedOn)) return;
        if (focusedOn != null) {
            focusedOn.deleteObserver(this);
            previousPosition = new Vector3f(position);
        }
        focusedOn = instance;
        onPlayer = true;
        position = new Vector3f(instance.getPosition());
        pitch = 20;
        instance.addObserver(this);
    }
    
    public void removeFocus() {
        if (focusedOn != null) {
            focusedOn.deleteObserver(this);
            focusedOn = null;
        }
        onPlayer = false;
        position = new Vector3f(previousPosition);
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    public void rubberBand(){
        boolean turned = false;
        if(focusedOn == null && rubberBandEnabled) {

        } else {
            angleAroundAsset *=-1;
            currentRotation = focusedOn.getRoty();
            //if(currentRotation < 0) currentRotation *= -1;
            //System.out.println(focusedOn.getRotz());
            distanceToAsset = max(focusedOn.getState().velocity*rubberVelocityFactor + minRubberDistance, minRubberDistance);
            pitch = max(focusedOn.getState().velocity*0.4f + 20, 20);
            if(angleAroundAsset >= -maxRubberAngle && angleAroundAsset <= maxRubberAngle){
                if (-rubberSmoothness > currentRotation - previousRotation || currentRotation - previousRotation  > rubberSmoothness){
                    angleAroundAsset = angleAroundAsset + signum(currentRotation-previousRotation) * rubberSpeed;
                    turned = true;
                } else {
                    if(!turned) {
                        angleAroundAsset -= signum(angleAroundAsset) * rubberSpeed;
                    }
                    turned = false;
                }
            } else{
                angleAroundAsset -= signum(angleAroundAsset) * 0.15f;
            }
            if (Math.abs(angleAroundAsset) < 0.01)
                angleAroundAsset = 0;
            //System.out.println(currentRotation - previousRotation);
            //System.out.println(currentRotation);
            previousRotation = currentRotation;
            angleAroundAsset *=-1;
        }
        float targetPitch = 20 + focusedOn.getRotz();
        //System.out.println("targetPitch" + targetPitch);
        if (Math.abs(targetPitch - pitch) > 0.01f)
        pitch = targetPitch;
        //System.out.println("pitch" + pitch);

    }

    public void speedFOV(){
        Renderer.changeFOV(fovVelocityFactor * focusedOn.getState().velocity);
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
    
    public void calculateInstanceValues() {
        rubberBand();
        speedFOV();
        State state = focusedOn.getState();
        float angle = state.roty + angleAroundAsset;
        float horDistance = (float) (distanceToAsset * Math.cos(Math.toRadians(pitch)));
        float verDistance = (float) (distanceToAsset * Math.sin(Math.toRadians(pitch)));
        float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
        float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));
        position.x = state.box.pos().x + x;
        position.y = state.box.pos().y + verDistance;
        position.z = state.box.pos().z + z;
        this.yaw = - (state.roty + angleAroundAsset);
        this.yaw %= 360;

    }

    public Matrix4f getViewMatrixInverse(){
        Matrix4f viewMatrixRotation = new Matrix4f();
        viewMatrixRotation.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrixRotation.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrixRotation.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        viewMatrixRotation.transpose();

        Matrix4f viewMatrixTranslation = new Matrix4f();
        viewMatrixTranslation.translate(new Vector3f(position.x, position.y, position.z));

        Matrix4f result = new Matrix4f();
        viewMatrixTranslation.mul(viewMatrixRotation, result);

        return result;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Instance) ||
                !(arg instanceof State)) return;
        Instance source = (Instance) o;
        State s = (State) arg;
        calculateInstanceValues();
    }
    
    
}
