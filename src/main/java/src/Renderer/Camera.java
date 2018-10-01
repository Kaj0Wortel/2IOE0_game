package src.Renderer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.instance.Instance;
import src.Assets.instance.Instance.State;

public class Camera {
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;
    
    Matrix4f viewMatrix = new Matrix4f();
    
    int speed = 2;
    
    final private Lock lock = new ReentrantLock();
    
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
    
    public void calculateViewMatrix() {
        lock.lock();
        try {
            viewMatrix.identity();
            viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
            viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
            viewMatrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
            Vector3f negPos = new Vector3f(-position.x, -position.y, -position.z);
            viewMatrix.translate(negPos);
            
        } finally {
            lock.unlock();
        }
    }
    
    public Matrix4f getViewMatrix(){
        calculateViewMatrix();
        return viewMatrix;
    }
    
    public void yaw(float amt) {
        lock.lock();
        try {
            yaw += amt;
            yaw %= 360;
            
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            position.x += speed * Math.sin((float) Math.toRadians(yaw)) * factor;
            position.z -= speed * Math.cos((float) Math.toRadians(yaw)) * factor;
            
        } finally {
            lock.unlock();
        }
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
        try {
            this.position = new Vector3f(position);
            
        } finally {
            lock.unlock();
        }
    }
    
    public void setFocus(Instance instance) {
        lock.lock();
        try {
            previousPosition = new Vector3f(position);
            focusedOn = instance;
            onPlayer = true;
            position = new Vector3f(instance.getPosition());
            pitch = 20;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void removeFocus() {
        lock.lock();
        try {
            focusedOn = null;
            onPlayer = false;
            position = new Vector3f(previousPosition);
            this.yaw = 0;
            this.pitch = 0;
            this.roll = 0;
            
        } finally {
            lock.unlock();
        }
    }
    
    public boolean isOnPlayer() {
        lock.lock();
        try {
            return onPlayer;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void moveToInstance() {
        lock.lock();
        try {
            distanceToAsset -= 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void moveAwayFromInstance() {
        lock.lock();
        try {
            distanceToAsset += 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void rotateAroundAssetR() {
        lock.lock();
        try {
            angleAroundAsset += 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void rotateAroundAssetL() {
        lock.lock();
        try {
            angleAroundAsset -= 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void movePitchDown() {
        lock.lock();
        try {
            pitch -= 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void movePitchUp() {
        lock.lock();
        try {
            pitch += 0.1f;
            
        } finally {
            lock.unlock();
        }
    }
    
    public void calculateInstanceValues() {
        lock.lock();
        try {
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
            
        } finally {
            lock.unlock();
        }
    }
    
    
}
