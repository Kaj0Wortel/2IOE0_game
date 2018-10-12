
package src.tools;


import org.joml.Vector3f;


/**
 * 
 */
public class Box3f
        implements Cloneable {
    
    protected Vector3f pos;
    protected Vector3f dim;
    
    
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
     * Constructor.
     * 
     * @param pos the position of the box.
     * @param dx the length of the box on the x-axis.
     * @param dy the length of the box on the y-axis.
     * @param dz the length of the box on the z-axis.
     */
    public Box3f(Vector3f pos, float dx, float dy, float dz) {
        this(pos, new Vector3f(dx, dy, dz));
    }
    
    /**
     * Full constructor.
     * 
     * @param pos the position of the box.
     * @param dim the dimensions of the box.
     */
    public Box3f(Vector3f pos, Vector3f dim) {
        this.pos = pos;
        this.dim = dim;
    }
    
    
    /**
     *  Clone constructor.
     * 
     * @param box the box to clone.
     */
    public Box3f(Box3f box) {
        this.pos = new Vector3f(box.pos);
        this.dim = new Vector3f(box.dim);
    }
    
    /**
     * @return the location of this box.
     */
    public Vector3f getPosition() {
        return pos;
    }
    
    /**
     * Shorthand for {@link #getPosition()}.
     * 
     * @return the location of this box.
     * 
     * @see #getposition()
     */
    public Vector3f pos() {
        return getPosition();
    }
    
    /**
     * @return the dimension of this box.
     */
    public Vector3f getDimension() {
        return dim;
    }
    
    /**
     * Shorthand for {@link #getDimension()}.
     * 
     * @return the dimension of this box.
     * 
     * @see #getDimension()
     */
    public Vector3f dim() {
        return getDimension();
    }
    
    /**
     * @return the dx of the box.
     */
    public float getDX() {
        return dim.x;
    }
    
    /**
     * Shorthand for {@link #getDX()}.
     * 
     * @return the dx of the box.
     * 
     * @see #getDX()
     */
    public float dx() {
        return getDX();
    }
    
    /**
     * @return the dy of the box.
     */
    public float getDY() {
        return dim.y;
    }
    
    /**
     * Shorthand for {@link #getDY()}.
     * 
     * @return the dy of the box.
     * 
     * @see #getDY()
     */
    public float dy() {
        return getDY();
    }
    
    /**
     * @return the dz of the box.
     */
    public float getDZ() {
        return dim.z;
    }
    
    /**
     * Shorthand for {@link #getDZ()}.
     * 
     * @return the dz of the box.
     * 
     * @see #getDZ()
     */
    public float dz() {
        return getDZ();
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
        this.dim.x = dx;
        this.dim.y = dy;
        this.dim.z = dz;
    }
    
    /**
     * Reshapes the x size of the box.
     * 
     * @param dx 
     */
    public void setDX(float dx) {
        this.dim.x = dx;
    }
    
    /**
     * Reshapes the y size of the box.
     * 
     * @param dy 
     */
    public void setDY(float dy) {
        this.dim.y = dy;
    }
    
    /**
     * Reshapes the z size of the box.
     * 
     * @param dz 
     */
    public void setDZ(float dz) {
        this.dim.z = dz;
    }
    
    /**
     * Checks whether two boxes intersect.
     * 
     * @param box the box to check the intersection with.
     * @return {@code true} if the two boxes intersect.
     *     {@code false} otherwise.
     */
    public boolean intersects(Box3f box) {
        return intersectsSegment(this.pos.x, box.pos.x, this.dx(), box.dx()) &&
                intersectsSegment(this.pos.y, box.pos.y, this.dy(), box.dy()) &&
                intersectsSegment(this.pos.z, box.pos.z, this.dz(), box.dz());
    }
    
    /**
     * Checks whether the two given line segments intersect.
     * 
     * @param v1 the begin of the first line segment.
     * @param v2 the begin of the second line segment.
     * @param d1 the length of the first line segment.
     * @param d2 the length of the second line segment.
     * @return {@code true} if the two line segments intersect.
     *     {@code false} otherwise.
     */
    protected boolean intersectsSegment(float v1, float v2, float d1, float d2) {
        float p11 = Math.min(v1, v1 + d1);
        float p12 = Math.max(v1, v1 + d1);
        float p21 = Math.min(v2, v2 + d2);
        float p22 = Math.max(v2, v2 + d2);
        
        return (p11 <= p21 && p12 > p21) ||
                (p11 < p22 && p12 >= p22) ||
                (p21 <= p11 && p22 > p11) ||
                (p21 < p12 && p22 >= p12);
        /*
        return (p11 < p21 && p12 > p21) ||
                (p21 < p11 && p22 > p11) ||
                (p12 > p22 && p11 < p22) ||
                (p22 > p12 && p21 < p12);*/
    }
    
    @Override
    public Box3f clone() {
        return new Box3f(this);
    }
    
    
}
