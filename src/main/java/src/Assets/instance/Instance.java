package src.Assets.instance;

import com.jogamp.opengl.GL3;
import java.io.File;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.GraphicsObject;
import src.Assets.Items.ItemInterface;
import src.Assets.OBJTexture;
import src.GS;
import src.Locker;
import src.Physics.PStructAction;
import src.Physics.Physics;
import src.Physics.PhysicsContext;
import src.Progress.ProgressManager;
import src.Shaders.ShaderProgram;
import src.Shaders.ShadowShader;
import src.tools.PosHitBox3f;

import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import src.AI.AStarDataPack;
import src.AI.Processor;


public abstract class Instance
        extends Observable {
    
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
        final public float internRotx;
        final public float internRoty;
        final public float internRotz;
        final public Vector3f internTrans;
        
        final public float velocity;
        final public float collisionVelocity;
        final public float colAngle;
        final public float verticalVelocity;
        final public boolean onTrack;
        final public boolean inAir;
        final public int rIndex;
        final public boolean isResetting;
        
        final public ItemInterface curItem;
        final public List<ItemInterface> activeItems;
        
        public State(PosHitBox3f box, float sizex, float sizey, float sizez,
                float rotx, float roty, float rotz,
                float internRotx, float internRoty, float internRotz,
                Vector3f internTrans, float velocity, float collisionVelocity,
                float colAngle, float verticalVelocity, boolean onTrack, boolean inAir, 
                int rIndex, boolean isResetting, ItemInterface curItem,
                List<ItemInterface> activeItems) {
            this.box = box;
            this.sizex = sizex;
            this.sizey = sizey;
            this.sizez = sizez;
            this.rotx = rotx;
            this.roty = roty;
            this.rotz = rotz;
            this.internRotx = internRotx;
            this.internRoty = internRoty;
            this.internRotz = internRotz;
            this.internTrans = internTrans;
            this.velocity = velocity;
            this.collisionVelocity = collisionVelocity;
            this.colAngle = colAngle;
            this.verticalVelocity = verticalVelocity;
            this.onTrack = onTrack;
            this.inAir = inAir;
            this.rIndex = rIndex;
            this.isResetting = isResetting;
            this.curItem = curItem;
            this.activeItems = activeItems;
        }
        
        @Override
        public String toString() {
            return "";
        }
        
        
    }
    
    protected ProgressManager progress = new ProgressManager();
    protected boolean isDestroyed = false;
    
    protected State state;
    protected PhysicsContext physicsContext;
    
    protected OBJTexture model;
    protected long prevTimeStamp;
    protected boolean isAI;
    
    
    public Instance(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float internRoty, 
            PhysicsContext physicContext) {
        this(box, size, size, size, rotx, roty, rotz, model,
                0, internRoty, 0, physicContext);
    }
    
    public Instance(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz, OBJTexture model,
            float internRotx, float internRoty, float internRotz,
            PhysicsContext physicContext) {
        box.scaleHitBox(sizex, sizey, sizez);
        setState(new State(box, sizex, sizey, sizez,
                rotx, roty, rotz,
                internRotx, internRoty, internRotz, new Vector3f(), 0, 0, 0, 0, 
                true, false, 0, false, null, new CopyOnWriteArrayList<ItemInterface>()));
        
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
        
        // World coordinate system.
        transformationMatrix
                .rotate((float) Math.toRadians(s.box.roty()), 0, 1, 0)
                .rotate((float) Math.toRadians(s.box.rotx()), 1, 0, 0)
                .rotate((float) Math.toRadians(s.box.rotz()), 0, 0, 1);
        
        transformationMatrix.scale(s.sizex, s.sizey, s.sizez);
        transformationMatrix.translate(s.internTrans);
        /*
        transformationMatrix
                .rotate((float) Math.toRadians(s.roty), 0, 1, 0)
                .rotate((float) Math.toRadians(s.rotx), 1, 0, 0)
                .rotate((float) Math.toRadians(s.rotz), 0, 0, 1);
        
        transformationMatrix.scale(s.sizex, s.sizey, s.sizez);
        
        transformationMatrix
                .rotate((float) Math.toRadians(s.internRoty), 0, 1, 0)
                .rotate((float) Math.toRadians(s.internRotx), 1, 0, 0)
                .rotate((float) Math.toRadians(s.internRotz), 0, 0, 1);
        */
        
//        Vector3f orgVec1 = new Vector3f(
//                (float) Math.sin(Math.toRadians(s.rotx)),
//                (float) (Math.cos(Math.toRadians(s.rotz))
//                        + Math.cos(Math.toRadians(s.rotx))) / 2f,
//                (float) Math.sin(Math.toRadians(s.rotz))
//        ).normalize();
//        float angle1 = (float) Math.acos(new Vector3f(0, 1, 0).dot(orgVec1));
//        Vector3f rotVec1 = orgVec1.cross(new Vector3f(0, 1, 0));
//        transformationMatrix
//                .rotate((float) Math.toRadians(s.roty), 0, 1, 0)
//                .rotate(angle1, rotVec1);
        
        // Model coordinate system.
//        Vector3f orgVec2 = new Vector3f(
//                (float) Math.sin(Math.toRadians(s.internRotx)),
//                (float) (Math.cos(Math.toRadians(s.internRotz))
//                        + Math.cos(Math.toRadians(s.internRotx))) / 2f,
//                (float) Math.sin(Math.toRadians(s.internRotz))
//        ).normalize();
//        float angle2 = (float) Math.acos(new Vector3f(0, 1, 0).dot(orgVec2));
//        Vector3f rotVec2 = orgVec2.cross(new Vector3f(0, 1, 0));
//        transformationMatrix
//                .rotate((float) Math.toRadians(s.internRoty), 0, 1, 0)
//                .rotate(angle2, rotVec2);
        
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
                (s.rotx + rot) % 360, s.roty, s.rotz,
                s.internRotx, s.internRoty, s.internRotz,
                s.internTrans, s.velocity, s.collisionVelocity, s.colAngle,
                s.verticalVelocity, s.onTrack, s.inAir, s.rIndex, s.isResetting,
                s.curItem, s.activeItems));
    }
    
    @Deprecated
    public void roty() {
        roty(3f);
    }
    
    public void roty(float rot) {
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                s.rotx, (s.roty + rot) % 360, s.rotz,
                s.internRotx, s.internRoty, s.internRotz,
                s.internTrans, s.velocity, s.collisionVelocity, s.colAngle,
                s.verticalVelocity, s.onTrack, s.inAir, s.rIndex, s.isResetting,
                s.curItem, s.activeItems));
    }

    @Deprecated
    public void rotz() {
        roty(3f);
    }
    
    public void rotz(float rot) {
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                s.rotx, s.roty, (s.rotz + rot) % 360,
                s.internRotx, s.internRoty, s.internRotz,
                s.internTrans, s.velocity, s.collisionVelocity, s.colAngle,
                s.verticalVelocity, s.onTrack, s.inAir, s.rIndex, s.isResetting,
                s.curItem, s.activeItems));
    }
    
    public void rotate(float rotx, float roty, float rotz) {
        State s = state; // For sync.
        setState(new State(s.box, s.sizex, s.sizey, s.sizez,
                (s.rotx + rotx) % 360, (s.roty + roty) % 360, (s.rotz + rotz) % 360,
                s.internRotx, s.internRoty, s.internRotz,
                s.internTrans, s.velocity, s.collisionVelocity, s.colAngle,
                s.verticalVelocity, s.onTrack, s.inAir, s.rIndex, s.isResetting,
                s.curItem, s.activeItems));
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
                s.rotx, s.roty, s.rotz,
                s.internRotx, s.internRoty, s.internRotz,
                s.internTrans, s.velocity, s.collisionVelocity, s.colAngle,
                s.verticalVelocity, s.onTrack, s.inAir, s.rIndex, s.isResetting,
                s.curItem, s.activeItems));
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

        GraphicsObject obj = model.getAsset().get(0);
        for (int i = 0; i < obj.size(); i++) {
            if(shader.useMaterial()) shader.loadMaterial(gl,obj.getMaterials().get(i));
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

    public void draw(GL3 gl, ShadowShader shader) {
        shader.loadModelMatrix(gl, getTransformationMatrix());

        GraphicsObject obj = model.getAsset().get(0);
        for (int i = 0; i < obj.size(); i++) {
            gl.glBindVertexArray(obj.getVao(i));
            gl.glEnableVertexAttribArray(0);
            gl.glDrawElements(GL3.GL_TRIANGLES, obj.getNrV(i),
                    GL3.GL_UNSIGNED_INT, 0);
            gl.glDisableVertexAttribArray(0);

        }
        gl.glBindVertexArray(0);
    }
    
    /**
     * Sets the state of {@code this} instance.
     * 
     * @param state the new state.
     */
    public void setState(State state) {
        state.box.setRotx(state.rotx + state.internRotx);
        state.box.setRoty(state.roty + state.internRoty);
        state.box.setRotz(state.rotz + state.internRotz);
        this.state = state;
        
        setChanged();
        notifyObservers(state);
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
    
    public ProgressManager getProgressManager() {
        return progress;
    }
    
    public void setProgressManager(ProgressManager pm) {
        this.progress = pm;
    }
    
    public boolean isAI () {
        return isAI;
    }
    
    final private static File A_STAR_DATA = new File(GS.DATA_DIR + "AStarData.csv");
    final private static Processor<AStarDataPack> processor = (String data) -> {
        String[] split = data.split(";");
        return new AStarDataPack(
                new Vector3f(
                        Float.parseFloat(split[0]),
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2])
                ), Float.parseFloat(split[3]), Float.parseFloat(split[4])
        );
        
    };
    
    public void setAI(boolean ai) {
        if (ai != isAI) {
            this.isAI = ai;
            if (ai) {
                Physics.registerReader(this, A_STAR_DATA, processor);
            }
        }
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
        if (isDestroyed()) return;
        if (!isStatic()) {
            Set<Instance> collisions = GS.grid.getCollisions(this);
            Physics.calcPhysics(this, pStruct, physicsContext, state,
                    collisions, progress);
            
        } else {
            Physics.calcPhysics(this, pStruct, physicsContext, state,
                    null, progress);
        }
    }
    
    /**
     * Removes the instance and releases all resources connected to it.
     */
    public void destroy() {
        System.out.println("destroyed: " + this);
        isDestroyed = true;
        Locker.remove(this);
    }
    
    /**
     * @return {@code true} if this object is destroyed.
     *     {@code false} otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
    
    
}
