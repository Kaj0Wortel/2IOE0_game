
package src.tools;

import org.joml.Vector3f;


// Own imports


// Java imports


/**
 * 
 */
public class Box3f
        implements Cloneable {
    
    private Vector3f pos;
    private float dx;
    private float dy;
    private float dz;
    
    
    public Box3f() {
        this(new Vector3f());
    }
    
    public Box3f(Vector3f loc) {
        this(loc, 0, 0, 0);
    }
    
    public Box3f(float dx, float dy, float dz) {
        this(new Vector3f(), dx, dy, dz);
    }
    
    public Box3f(float x, float y, float z, float dx, float dy, float dz) {
        this(new Vector3f(x, y, z), dx, dy, dz);
    }
    
    /**
     * Full constructor.
     * 
     * @param pos the position of the box.
     * @param dx the length of the box on the x-axis.
     * @param dy the length of the box on the y-axis.
     * @param dz the length of the box on the z-axis.
     */
    public Box3f(Vector3f pos, float dx, float dy, float dz) {
        this.pos = pos;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
    
    /**
     *  Clone constructor.
     * 
     * @param box the box to clone.
     */
    public Box3f(Box3f box) {
        this.pos = new Vector3f(box.pos);
        this.dx = box.dx;
        this.dy = box.dy;
        this.dz = box.dz;
    }
    
    public Vector3f getPosition() {
        return pos;
    }
    
    /**
     * @return the location of this box.
     * 
     * Shorthand for {@link #getPosition()}.
     */
    public Vector3f pos() {
        return getPosition();
    }
    
    /**
     * @return the dx of the box.
     */
    public float getDX() {
        return dx;
    }
    
    /**
     * @return the dy of the box.
     */
    public float getDY() {
        return dy;
    }
    
    /**
     * @return the dz of the box.
     */
    public float getDZ() {
        return dz;
    }
    
    /**
     * Sets the position of the box.
     * 
     * @param pos the new position of the box.
     */
    public void setPosition(Vector3f pos) {
        this.pos = pos;
    }
    
    /**
     * Translates the box over the given vector.
     * 
     * @param add 
     */
    public void translate(Vector3f add) {
        pos.add(add);
    }
    
    /**
     * Reshapes the box to the given sizes.
     * 
     * @param dx
     * @param dy
     * @param dz 
     */
    public void reshape(float dx, float dy, float dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
    
    /**
     * Reshapes the x size of the box.
     * 
     * @param dx 
     */
    public void setDX(float dx) {
        this.dx = dx;
    }
    
    /**
     * Reshapes the y size of the box.
     * 
     * @param dy 
     */
    public void setDY(float dy) {
        this.dy = dy;
    }
    
    /**
     * Reshapes the z size of the box.
     * 
     * @param dz 
     */
    public void setDZ(float dz) {
        this.dz = dz;
    }
    
    /**
     * Checks whether two boxes intersect.
     * 
     * @param box the box to check the intersection with.
     * @return {@code true} if the two boxes intersect.
     *     {@code false} otherwise.
     */
    public boolean intersects(Box3f box) {
        return intersectsSegment(this.pos.x, box.pos.x, this.dx, box.dx) &&
                intersectsSegment(this.pos.y, box.pos.y, this.dy, box.dy) &&
                intersectsSegment(this.pos.z, box.pos.z, this.dz, box.dz);
    }
    
    /**
     * Checks whether the two given line segments intersect.
     * @param v1 the begin of the first line segment.
     * @param v2 the begin of the second line segment.
     * @param d1 the length of the first line segment.
     * @param d2 the length of the second line segment.
     * @return {@code true} if the two line segments intersect.
     *     {@code false} otherwise.
     */
    private boolean intersectsSegment(float v1, float v2, float d1, float d2) {
        return (v1 < v2 && v1 + d1 > v2) ||
                (v2 < v1 && v2 + d2 > v1) ||
                (v1 + d1 > v2 + d2 && v1 < v2 + d2) ||
                (v2 + d2 > v1 + d1 && v2 < v1 + d1);
                
    }
    
    @Override
    public Box3f clone() {
        return new Box3f(this);
    }
    
    
}
