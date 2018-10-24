
package src.tools;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.joml.Matrix3f;
import org.joml.Planef;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.testing.VisualAStar;


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
        this(pos, relPos, dim, 0, 0, 0);
    }
    
    /**
     * Full constructor.
     * 
     * @param pos the position of the box.
     * @param relPos the relative position of the hit box.
     * @param dx the dimension of the box. Can be negative.
     */
    public PosHitBox3f(Vector3f pos, Vector3f relPos, Vector3f dim,
            float rotx, float roty, float rotz) {
        super(pos, dim);
        this.relPos = relPos;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
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
        return intersectsRot(box);
    }
    
    /**
     * Calculates whether two hitboxes intersect, without rotation.
     * 
     * @param box the box to check whether it intersects with.
     * @return {@code true} if {@code this} intersects with {@code box}.
     * 
     * Note hit boxes which only share one point, line or plane are
     * NOT intersecting.
     */
    public boolean intersectsNoRot(PosHitBox3f box) {
        return
                intersectsSegment(this.pos.x + this.relPos.x,
                        box.pos.x + box.relPos.x, this.dx(), box.dx()) &&
                intersectsSegment(this.pos.y + this.relPos.y,
                        box.pos.y + box.relPos.y, this.dy(), box.dy()) &&
                intersectsSegment(this.pos.z + this.relPos.z,
                        box.pos.z + box.relPos.z, this.dz(), box.dz());
    }
    
    /**
     * Calculates whether two hitboxes intersect, with rotation.
     * 
     * @param box the box to check whether it intersects with.
     * @return {@code true} if {@code this} intersects with {@code box}.
     * 
     * Uses the separate axis test theorem:
     * <url>https://gamedevelopment.tutsplus.com/tutorials/collision-detection-
     *      using-the-separating-axis-theorem--gamedev-169</url>
     */
    public boolean intersectsRot(PosHitBox3f box) {
        Matrix3f rotMatThis = calcRotMatrix();
        Matrix3f rotMatBox = box.calcRotMatrix();
        
        Bar3f thisBar = new Bar3f(new Vector3f(this.relPos),
                new Vector3f(this.dim.x, 0, 0),
                new Vector3f(0, this.dim.y, 0),
                new Vector3f(0, 0, this.dim.z));
        thisBar.mul(rotMatThis);
        thisBar.translate(this.pos);
        
        Bar3f boxBar = new Bar3f(new Vector3f(box.relPos),
                new Vector3f(box.dim.x, 0, 0),
                new Vector3f(0, box.dim.y, 0),
                new Vector3f(0, 0, box.dim.z));
        boxBar.mul(rotMatBox);
        boxBar.translate(box.pos);
        
        Planef[][] checkPlanes = new Planef[][] {
            thisBar.generatePlanes(),
            boxBar.generatePlanes()
        };
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < checkPlanes[i].length; j++) {
                
                Polygon2f thisProj2d = thisBar.project(checkPlanes[i][j]);
                Polygon2f boxProj2d = boxBar.project(checkPlanes[i][j]);
                
                /*
                VisualAStar panel = new VisualAStar(new Rectangle(-20, -20, 40, 40));
                panel.setForeground(Color.RED);
                panel.addLine(new Point2D.Double(-50, 0), new Point2D.Double(50, 0));
                panel.setForeground(Color.GREEN);
                panel.addLine(new Point2D.Double(0, -50), new Point2D.Double(0, 50));
                
                Color c = new Color(40, 70, 255);
                thisProj2d.visualizeDots(panel, c);
                thisProj2d.visualizeLines(panel, c);
                c = Color.YELLOW;
                boxProj2d.visualizeDots(panel, c);
                boxProj2d.visualizeLines(panel, c);
                */
                
                Vector2f[][] checkNorms = new Vector2f[][] {
                    thisProj2d.calcNormals(),
                    boxProj2d.calcNormals()
                };
                for (int ii = 0; ii < 2; ii++) {
                    for (int jj = 0; jj < checkNorms[ii].length; jj++) {
                        float[] thisProj1d = thisProj2d.project(
                                checkNorms[ii][jj]);
                        float[] boxProj1d = boxProj2d.project(
                                checkNorms[ii][jj]);
                        if (!intersects(thisProj1d, boxProj1d)) return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private boolean intersects(float[] ps1, float[] ps2) {
        float min1 = Float.POSITIVE_INFINITY;
        float max1 = Float.NEGATIVE_INFINITY;
        float min2 = Float.POSITIVE_INFINITY;
        float max2 = Float.NEGATIVE_INFINITY;
        
        for (int i = 0; i < ps1.length; i++) {
            min1 = Math.min(min1, ps1[i]);
            max1 = Math.max(max1, ps1[i]);
        }
        for (int i = 0; i < ps2.length; i++) {
            min2 = Math.min(min2, ps2[i]);
            max2 = Math.max(max2, ps2[i]);
        }
        
        return intersectsSegment(min1, min2, max1 - min1, max2 - min2);
    }
    
    /**
     * @return the rotation matrix of this hitbox.
     */
    public Matrix3f calcRotMatrix() {
        return new Matrix3f()
                .rotate((float) Math.toRadians(roty), 0, 1, 0)
                .rotate((float) Math.toRadians(rotx), 1, 0, 0)
                .rotate((float) Math.toRadians(rotz), 0, 0, 1);
    }
    
    /**
     * @return the center of the hit box in view space.
     */
    public Vector3f calcHitBoxCenterViewSpace() {
        Vector3f center = new Vector3f(
                dim.x / 2 - relPos.x,
                dim.y / 2 - relPos.y,
                dim.z / 2 - relPos.z
        );
        return center.mul(calcRotMatrix()).add(pos);
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "["
                + "pos=[" + pos.x + "," + pos.y + "," + pos.z + "], "
                + "relPos=[" + relPos.x + "," + relPos.y + "," + relPos.z + "], "
                + "dim=[" + dim.x + "," + dim.y + "," + dim.z + "]]";
    }
    
    
    // tmp
    public static void main(String[] args) {
        PosHitBox3f box1 = new PosHitBox3f(
                new Vector3f(2, 2, 2),
                new Vector3f(-2, -2, -2),
                new Vector3f(4, 4, 4),
                0, 0, 0);
        PosHitBox3f box2 = new PosHitBox3f(
                new Vector3f(2, 2, 2),
                new Vector3f(-2, -2, -2),
                new Vector3f(4, 4, 4),
                45, 45, 45);
        System.out.println(box1.intersectsRot(box2));
        System.out.println(box1.intersects(box2));
    }
    
    
}
