package src.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.instance.Instance;
import src.Assets.instance.Instance.State;

import static java.lang.Math.signum;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import src.tools.log.Logger;


public class Camera
        implements Observer {
    
    public static enum CameraMode {
        DEFAULT, FIRST_PERSON, BACK, HIGH_UP;
    }
    
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;
    private float fov = 70;
    private float fovOffset;
    private boolean fovChange;
    
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

    private Vector3f highPosition = new Vector3f(400.0f, 70.0f, 1000.0f);
    private float highPitch = 90.0f;
    private float highYaw = 0.0f;
    private float highRoll = 0.0f;

    private Lock lock = new ReentrantLock();
    private CameraMode cameraMode = CameraMode.DEFAULT;
    private Matrix4f projectionMatrix;
    
    
    public Camera (Vector3f position, float pitch, float yaw, float roll){
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.onPlayer = false;

        calculateViewMatrix();
    }
    
    public Matrix4f calculateViewMatrix() {
        lock.lock();
        try {
            return new Matrix4f()
                    .rotate((float) Math.toRadians(pitch), 1, 0, 0)
                    .rotate((float) Math.toRadians(yaw),   0, 1, 0)
                    .rotate((float) Math.toRadians(roll),  0, 0, 1)
                    .translate(new Vector3f(position).negate());
            
        } finally {
            lock.unlock();
        }
    }
    
    public Matrix4f getViewMatrix() {
        return calculateViewMatrix();
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
        System.out.println("I am called for some reason every frame");
    }
    
    public void removeFocus() {
        if (focusedOn != null) {
            focusedOn.deleteObserver(this);
            focusedOn = null;
            onPlayer = false;
            position = new Vector3f(previousPosition);
            this.yaw = 0;
            this.pitch = 0;
            this.roll = 0;
        }
    }

    public void rubberBand() {
        boolean turned = false;
        if (focusedOn != null && rubberBandEnabled) {
            angleAroundAsset *= -1;
            currentRotation = focusedOn.getRoty();
            //if(currentRotation < 0) currentRotation *= -1;
            //System.out.println(focusedOn.getRotz());
            distanceToAsset = Math.max(focusedOn.getState()
                    .velocity * rubberVelocityFactor + minRubberDistance, minRubberDistance);
            
            if (cameraMode == CameraMode.DEFAULT) {
                pitch = Math.max(focusedOn.getState().velocity * 0.4f + 20, 20);
                
            } else if (cameraMode == CameraMode.FIRST_PERSON) {
                pitch = 0;
                
            } else if (cameraMode == CameraMode.BACK) {
                pitch = Math.min(-focusedOn.getState().velocity * 0.4f + 20, 20);
            } else if (cameraMode == CameraMode.HIGH_UP){
                pitch = highPitch;
            }
            
            if (angleAroundAsset >= -maxRubberAngle && angleAroundAsset <= maxRubberAngle) {
                if (-rubberSmoothness > currentRotation - previousRotation ||
                        currentRotation - previousRotation  > rubberSmoothness) {
                    angleAroundAsset = angleAroundAsset
                            + signum(currentRotation - previousRotation) * rubberSpeed;
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
            angleAroundAsset *= -1;
        }
        
        float targetPitch = 0;
        if (cameraMode == CameraMode.DEFAULT) {
            targetPitch = 20 - focusedOn.getRotx();

        } else if (cameraMode == CameraMode.FIRST_PERSON) {
            targetPitch = 0;

        } else if (cameraMode == CameraMode.BACK) {
            targetPitch = 20 + focusedOn.getRotx();
        }
        
        //System.out.println("targetPitch" + targetPitch);
        if (Math.abs(targetPitch - pitch) > 0.01f)
            pitch = targetPitch;
        //System.out.println("pitch" + pitch);
    }

    public void speedFOV() {
        changeFOV(fovVelocityFactor * focusedOn.getState().velocity);
    }
    
    public boolean isOnPlayer() {
        return onPlayer;
    }
    
    @Deprecated
    public void moveToInstance() {
        distanceToAsset -= 0.1f;
    }
    
    @Deprecated
    public void moveAwayFromInstance() {
        distanceToAsset += 0.1f;
    }
    
    @Deprecated
    public void rotateAroundAssetR() {
        angleAroundAsset += 0.1f;
    }
    
    @Deprecated
    public void rotateAroundAssetL() {
        angleAroundAsset -= 0.1f;
    }
    
    @Deprecated
    public void movePitchDown() {
        pitch -= 0.1f;
    }
    
    @Deprecated
    public void movePitchUp() {
        pitch += 0.1f;
    }
    
    public void calculateInstanceValues() {
        lock.lock();
        try {

            speedFOV();
            rubberBand();
            State state = focusedOn.getState();
            float angle = state.roty + angleAroundAsset;
            
            if (cameraMode == CameraMode.DEFAULT) {
                float horDistance = (float) (distanceToAsset * Math.cos(Math.toRadians(pitch)));
                float verDistance = (float) (distanceToAsset * Math.sin(Math.toRadians(pitch)));
                float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
                float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));
                
                position.x = state.box.pos().x + x;
                position.y = state.box.pos().y + verDistance;
                position.z = state.box.pos().z + z;
                this.yaw = -(state.roty + angleAroundAsset);
                
            } else if (cameraMode == CameraMode.FIRST_PERSON) {
                this.pitch = -state.rotx;
                this.yaw = -(state.roty + angleAroundAsset);
                this.roll = state.rotz;
                
                float horDistance = (float) (distanceToAsset * Math.cos(Math.toRadians(pitch)));
                float verDistance = (float) Math.abs(distanceToAsset * Math.sin(Math.toRadians(pitch)));
                float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
                float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));
                
                position.x = state.box.pos().x - x * 0.25f;
                position.y = state.box.pos().y + 3f + verDistance * 0.25f;
                position.z = state.box.pos().z - z * 0.25f;
                
            } else if (cameraMode == CameraMode.BACK) {
                float horDistance = (float) (distanceToAsset * Math.cos(Math.toRadians(pitch)));
                float verDistance = (float) (distanceToAsset * Math.sin(Math.toRadians(pitch)));
                float x = (float) (horDistance * Math.sin(Math.toRadians(angle)));
                float z = (float) (horDistance * Math.cos(Math.toRadians(angle)));

                position.x = state.box.pos().x - x;
                position.y = state.box.pos().y + verDistance;
                position.z = state.box.pos().z - z;
                this.yaw = -(state.roty + angleAroundAsset) + 180;

            } else if (cameraMode == CameraMode.HIGH_UP){
                position.x = highPosition.x;
                position.y = highPosition.y;
                position.z = highPosition.z;
                //System.out.println("Camera values 1: x: "+position.x+" y: "+position.y+" z: "+position.z+" yaw: "+yaw+" roll: "+roll+" pitch: "+pitch);

                this.yaw = highYaw;
                this.pitch = highPitch;
                this.roll = highRoll;
                angleAroundAsset = 0;

                //System.out.println("Camera values 2: x: "+position.x+" y: "+position.y+" z: "+position.z+" yaw: "+yaw+" roll: "+roll+" pitch: "+pitch);

            } else {
                Logger.write("Unknown camera mode: " + cameraMode,
                        Logger.Type.ERROR);
                System.exit(-1);
            }
            this.yaw %= 360;
            
        } finally {
            lock.unlock();
        }
    }

    public Matrix4f getViewMatrixInverse() {
        lock.lock();
        try {
            Matrix4f viewMatrixRotation = new Matrix4f()
                    .rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0))
                    .rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0))
                    .rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1))
                    .transpose();

            Matrix4f viewMatrixTranslation = new Matrix4f()
                    .translate(position);

            return viewMatrixTranslation.mul(viewMatrixRotation);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Instance) ||
                !(arg instanceof State)) return;
        calculateInstanceValues();
        calcProjectionMatrix();
    }

    public CameraMode getCameraMode() {
        return cameraMode;
    }
    
    public void setCameraMode(CameraMode cameraMode) {
        this.cameraMode = cameraMode;
    }
    
    public void cycleNextCameraMode() {
        if (cameraMode == null) {
            cameraMode = CameraMode.DEFAULT;
        } else {
            CameraMode[] values = CameraMode.values();
            cameraMode = values[(cameraMode.ordinal() + 1) % values.length];
        }
        
        if (cameraMode == CameraMode.HIGH_UP) cycleNextCameraMode();
    }

    public void changeFOV(float fovOffset){
        fov = 70 + fovOffset / 1.3f;
        fovChange = true;
    }
    
    public boolean hasFOVChange() {
        return fovChange;
    }
    
    public void resetFOVChange() {
        fovChange = false;
    }
    
    public float fov() {
        return fov;
    }
    
    public void calcProjectionMatrix() {
        float ratio = Renderer.width / Renderer.height;
        float y = (float) (1f / Math.tan(Math.toRadians(fov/2f)));
        float x = y / ratio;
        float delta = Renderer.FAR - Renderer.NEAR;

        lock.lock();
        try {
            projectionMatrix = new Matrix4f();
            projectionMatrix.m00(x);
            projectionMatrix.m11(y);
            projectionMatrix.m22(-((Renderer.NEAR + Renderer.FAR)/delta));
            projectionMatrix.m23(-1);
            projectionMatrix.m32(-(2*Renderer.NEAR*Renderer.FAR)/delta);
            projectionMatrix.m33(0);
            
        } finally {
            lock.unlock();
        }
    }
    
    public Matrix4f getProjectionMatrix() {
        lock.lock();
        try {
            return new Matrix4f(projectionMatrix);
            
        } finally {
            lock.unlock();
        }
    }
    
    
}
