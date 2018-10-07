package src.Assets.instance;

import com.jogamp.opengl.GL3;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.GraphicsObject;
import src.Assets.OBJTexture;
import src.GS;
import src.Locker;
import src.Physics.PStructAction;
import src.Physics.PhysicsContext;
import src.Physics.Physics;
import src.Progress.ProgressManager;
import src.Shaders.ShaderProgram;
import src.tools.PosHitBox3f;


public abstract class Instance {
    
    /**
     * State class for instance variables.
     * All variables are final to prevent accidental state changes.
     * Do NOT set any variables inside this state (e.g. pos.x = 0).
     * This class should be used as a "cast away shell", meaning it should
     * be replaced by a new instance when variables are updated. This is
     * to prevent intermediate state changes.
     */
    final public static class State {
        final public PosHitBox3f box;
        final public float sizex;
        final public float sizey;
        final public float sizez;
        final public float rotx;
        final public float roty;
        final public float rotz;
        final public float integratedRotation;
        
        final public float velocity;
        final public float collisionVelocity;
        final public float verticalVelocity;
        
        
        public State(PosHitBox3f box, float sizex, float sizey, float sizez,
                float rotx, float roty, float rotz, float integratedRotation,
                float velocity, float collisionVelocity,
                float verticalVelocity) {
            this.box = box;
            this.sizex = sizex;
            this.sizey = sizey;
            this.sizez = sizez;
            this.rotx = rotx;
            this.roty = roty;
            this.rotz = rotz;
            this.integratedRotation = integratedRotation;
            this.velocity = velocity;
            this.collisionVelocity = collisionVelocity;
            this.verticalVelocity = verticalVelocity;
        }
        
        @Override
        public String toString() {
            return "";
        }
        
        
    }
    
    ProgressManager progress = new ProgressManager();
    
    protected State state;
    protected PhysicsContext physicsContext;
    
    protected OBJTexture model;
    protected long prevTimeStamp;
    
    
    public Instance(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation, 
            PhysicsContext physicContext) {
        this(box, size, size, size, rotx, roty, rotz, model,
                integratedRotation, physicContext);
    }
    public Instance(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz, OBJTexture model,
            float integratedRotation, PhysicsContext physicContext) {
        box.scaleHitBox(sizex, sizey, sizez);
        setState(new State(box, sizex, sizey, sizez, rotx, roty, rotz,
                integratedRotation, 0, 0, 0));
        
        this.model = model;
        this.physicsContext = physicContext;
        
        SwingUtilities.invokeLater(() -> {
            Locker.add(this);
        });
    }
    
    
    public Matrix4f getTransformationMatrix() {
        State s = state; // For sync.
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(s.box.pos());
        transformationMatrix.rotate((float) Math.toRadians(s.rotx), 1, 0, 0);
        transformationMatrix.rotate(
                (float) Math.toRadians(s.roty + s.integratedRotation), 0, 1, 0);
        transformationMatrix.rotate((float) Math.toRadians(s.rotz), 0, 0, 1);
        transformationMatrix.scale(s.sizex, s.sizey, s.sizez);
        
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
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                (s.rotx + rot) % 360, s.roty, s.rotz, s.integratedRotation,
                s.velocity, s.collisionVelocity,
                s.verticalVelocity));
    }

    @Deprecated
    public void roty() {
        roty(3f);
    }
    
    public void roty(float rot) {
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                s.rotx, (s.roty + rot) % 360, s.rotz, s.integratedRotation,
                s.velocity, s.collisionVelocity,
                s.verticalVelocity));
    }

    @Deprecated
    public void rotz() {
        roty(3f);
    }
    
    public void rotz(float rot) {
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                s.rotx, s.roty, (s.rotz + rot) % 360, s.integratedRotation,
                s.velocity, s.collisionVelocity,
                s.verticalVelocity));
    }

    @Deprecated
    public void moveup() {
        moveup(0.1f);
    }
    
    @Deprecated
    public void moveup(float amt) {
        State s = state; // For sync.
        PosHitBox3f newBox = s.box.clone();
        newBox.translate(new Vector3f(0, amt, 0));
        setState(new State(newBox, s.sizex, s.sizey, s.sizez,
                s.rotx, s.roty, s.rotz, s.integratedRotation,
                s.velocity, s.collisionVelocity,
                s.verticalVelocity));
    }

    /**
     * Draws {@code this} using the given shader.
     * 
     * @param gl
     * @param shader 
     */
    public void draw(GL3 gl, ShaderProgram shader) {
        shader.loadModelMatrix(gl, getTransformationMatrix());
        shader.loadTextureLightValues(gl, model.getTextureImg().getShininess(),
                model.getTextureImg().getReflectivity());
        
        GraphicsObject obj = model.getAsset();
        for (int i = 0; i < obj.size(); i++) {
            gl.glBindVertexArray(obj.getVao(i));
            gl.glEnableVertexAttribArray(0);
            gl.glEnableVertexAttribArray(1);
            gl.glEnableVertexAttribArray(2);
            gl.glDrawElements(GL3.GL_TRIANGLES, obj.getNrV(i),
                    GL3.GL_UNSIGNED_INT, 0);
            gl.glDisableVertexAttribArray(0);
            gl.glDisableVertexAttribArray(1);
            gl.glDisableVertexAttribArray(2);
        }
        gl.glBindVertexArray(0);
    }
    
    /**
     * Sets the state of {@code this} instance.
     * 
     * @param state the new state.
     */
    public void setState(State state) {
        this.state = state;
    }
    
    /**
     * @return the current state of {@code this} instance.
     *     The returned state is immutable.
     */
    public State getState() {
        return state;
    }
    
    /**
     * @return the current bounding box of {@code this} instance.
     * 
     * When using more than this data from {@code this} instance,
     * use {@link #getState()} instead.
     */
    public PosHitBox3f getBox() {
        return state.box;
    }
    
    /**
     * @return the current position of {@code this} instance.
     * 
     * When using more than this data from {@code this} instance,
     * use {@link #getState()} instead.
     */
    public Vector3f getPosition() {
        return state.box.pos();
    }
    
    /**
     * @return the current rotation around the x-axis of {@code this} instance.
     * 
     * When using more than this data from {@code this} instance,
     * use {@link #getState()} instead.
     */
    public float getRotx() {
        return state.rotx;
    }
    
    /**
     * @return the current rotation around the y-axis of {@code this} instance.
     * 
     * When using more than this data from {@code this} instance,
     * use {@link #getState()} instead.
     */
    public float getRoty() {
        return state.roty;
    }
    
    /**
     * @return the current rotation around the z-axis of {@code this} instance.
     * 
     * When using more than this data from {@code this} instance,
     * use {@link #getState()} instead.
     */
    public float getRotz() {
        return state.rotz;
    }
    
    /**
     * @param other
     * @return {@code true} if the two instances collide.
     *     {@code false} otherwise.
     */
    public boolean intersectsWith(Instance other) {
        return state.box.intersects(other.state.box);
    }
    
    /**
     * @return {@code true} if the instance is static (i.e. not moveable).
     *     {@code false} otherwise.
     */
    public abstract boolean isStatic();
    
    /**
     * Calculates the movement of the instance with the given action.
     * 
     * @param pStruct the action to execute.
     */
    public void movement(PStructAction pStruct) {
        if (!isStatic()) {
            Set<Instance> collisions = GS.grid.getCollisions(this);
            Physics.calcPhysics(this, pStruct, physicsContext, state,
                    collisions, progress);
            
        } else {
            Physics.calcPhysics(this, pStruct, physicsContext, state, null, progress);
        }
    }
    
    /**
     * Removes the instance and releases all resources connected to it.
     */
    public void destroy() {
        // TODO
    }
    
    
}
