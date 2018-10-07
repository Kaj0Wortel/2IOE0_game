
package src.tools;


import javax.swing.SwingUtilities;
import org.joml.Matrix3f;
import org.joml.Vector3f;


/**
 * 
 */
public class PosHitBox3f
        extends Box3f {
    
    protected float rotx;
    protected float rotz;
    protected float roty;
    
    protected Vector3f relPos;
    
    
    public PosHitBox3f() {
        this(new Vector3f(), new Vector3f(), 0, 0, 0);
    }
    
    public PosHitBox3f(float x, float y, float z,
            float relX, float relY, float relZ,
            float dx, float dy, float dz) {
        this(new Vector3f(x, y, z), new Vector3f(relX, relY, relZ),
                dx, dy, dz);
    }
    
    public PosHitBox3f(float relX, float relY, float relZ,
            float dx, float dy, float dz) {
        this(new Vector3f(0, 0, 0), new Vector3f(relX, relY, relZ),
                dx, dy, dz);
    }
    
    /**
     * @param pos the position of the box.
     * @param relPos the relative position of the hit box.
     * @param dx the length of the box on the x-axis.
     * @param dy the length of the box on the y-axis.
     * @param dz the length of the box on the z-axis.
     */
    public PosHitBox3f(Vector3f pos, Vector3f relPos,
            float dx, float dy, float dz) {
        this(pos, relPos, new Vector3f(dx, dy, dz));
    }
    
    public PosHitBox3f(Vector3f pos, Vector3f relPos, Vector3f dim) {
        this(pos, relPos, dim, 0);
    }
    
    /**
     * Full constructor.
     * 
     * @param pos the position of the box.
     * @param relPos the relative position of the hit box.
     * @param dx the dimension of the box. Can be negative.
     */
    public PosHitBox3f(Vector3f pos, Vector3f relPos, Vector3f dim,
            float roty) {
        super(pos, dim);
        this.relPos = relPos;
        this.roty = roty;
    }
    
    /**
     * Clone constructor.
     * 
     * @param box the box to clone.
     */
    public PosHitBox3f(PosHitBox3f box) {
        super(box);
        this.relPos = new Vector3f(box.relPos);
    }
    
    
    /**
     * Scales the hit box using the given scalar.
     * 
     * @param scalar 
     */
    public void scaleHitBox(float scalar) {
        relPos.mul(scalar);
        dim.mul(scalar);
    }
    
    /**
     * Scales the hit box using the given x, y and z axis scalars.
     * 
     * @param x
     * @param y
     * @param z 
     */
    public void scaleHitBox(float x, float y, float z) {
        relPos.x *= x;
        relPos.y *= y;
        relPos.z *= z;
        
        dim.x *= x;
        dim.y *= y;
        dim.z *= z;
    }
    
    
    /**
     * Centers the hitbox around (0, 0, 0).
     */
    public void centerHitBox() {
        centerHitBox(new Vector3f());
    }
    
    /**
     * Centers the hitbox around the given center vector.
     * 
     * @param center the new center of the hitbox.
     */
    public void centerHitBox(Vector3f center) {
        relPos.x = center.x - (dim.x / 2);
        relPos.y = center.y - (dim.y / 2);
        relPos.z = center.z - (dim.z / 2);
    }
    
    /**
     * Sets the position to (0, 0, 0), but keeps the hitbox
     * at the same location.
     */
    public void setPosKeepHitBox() {
        setPosKeepHitBox(new Vector3f());
    }
    
    /**
     * Sets the position to the given center, but keeps the hitbox
     * at the same location.
     * 
     * @param the point to move the position to.
     */
    public void setPosKeepHitBox(Vector3f center) {
        relPos.x += pos.x - center.x;
        relPos.y += pos.y - center.y;
        relPos.z += pos.z - center.z;
        pos.x = center.x;
        pos.y = center.y;
        pos.z = center.z;
    }
    
    /**
     * @return the relative position of the hit box.
     */
    public Vector3f getRelPos() {
        return relPos;
    }
    
    /**
     * Shorthand for {@link #getRelPos()}.
     * 
     * @return the relative position of the hit box.
     * 
     * @see #getRelPos()
     */
    public Vector3f relPos() {
        return getRelPos();
    }
    
    /**
     * @return the actual position of the hit box.
     */
    public Box3f getHitBox() {
        return new Box3f(
                pos.x + relPos.x,
                pos.y + relPos.y,
                pos.z + relPos.z,
                dx(), dy(), dz());
    }
    
    /**
     * @return the x-rotation of the hitbox.
     */
    public float rotx() {
        return rotx;
    }
    
    /**
     * @return the y-rotation of the hitbox.
     */
    public float roty() {
        return roty;
    }
    
    /**
     * @return the z-rotation of the hitbox.
     */
    public float rotz() {
        return rotz;
    }
    
    /**
     * Sets the x-rotation of the hitbox.
     * 
     * @param rotx the new x-rotation.
     */
    public void setRotx(float rotx) {
        this.rotx = rotx;
    }
    
    /**
     * Sets the y-rotation of the hitbox.
     * 
     * @param roty the new y-rotation.
     */
    public void setRoty(float roty) {
        this.roty = roty;
    }
    
    /**
     * Sets the z-rotation of the hitbox.
     * 
     * @param rotz the new z-rotation.
     */
    public void setRotz(float rotz) {
        this.rotz = rotz;
    }
    
    /**
     * Converts the position and hit box using the given matrix.
     * 
     * @param mat the conversion matrix.
     * @return a new converted pos hit box.
     */
    public PosHitBox3f convert(Matrix3f mat) {
        return new PosHitBox3f(
                new Vector3f(pos).mul(mat),
                new Vector3f(relPos).mul(mat),
                new Vector3f(dim).mul(mat)
        );
    }
    
    @Override
    public PosHitBox3f clone() {
        return new PosHitBox3f(this);
    }
    
    public boolean intersects(PosHitBox3f box) {
        return
                intersectsSegment(this.pos.x + this.relPos.x,
                        box.pos.x + box.relPos.x, this.dx(), box.dx()) &&
                intersectsSegment(this.pos.y + this.relPos.y,
                        box.pos.y + box.relPos.y, this.dy(), box.dy()) &&
                intersectsSegment(this.pos.z + this.relPos.z,
                        box.pos.z + box.relPos.z, this.dz(), box.dz());
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "["
                + "pos=[" + pos.x + "," + pos.y + "," + pos.z + "], "
                + "relPos=[" + relPos.x + "," + relPos.y + "," + relPos.z + "], "
                + "dim=[" + dim.x + "," + dim.y + "," + dim.z + "]]";
    }
    
    
}
