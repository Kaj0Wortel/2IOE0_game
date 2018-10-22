package src.racetrack;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.TextureImg;
import src.GS;
import src.Shaders.RacetrackShader;

import src.Shaders.ShadowShader;
import java.nio.IntBuffer;
import org.joml.Matrix3f;
import src.Assets.instance.Car;

public abstract class Track {

    final protected static int NR_SEGMENT_VERTICES_COL = 150;
    final protected static int NR_SEGMENTS_VERTICES_ROW = 17; //Must be odd
    protected int laneWidth = 7;
    protected int scalePoints = 10;

    protected Vector3f[] controlPoints;
    protected int nrOfSegments;

    final protected Vector3f position;
    final protected float size;
    final protected float rotx;
    final protected float roty;
    final protected float rotz;

    protected IntBuffer vao;
    protected int nrV;
    protected TextureImg texture;
    protected TextureImg bumpmap;
    protected int shadowMap;

    protected RacetrackShader shader;

    public Track(Vector3f position, float size, float rotx, float roty,
            float rotz, TextureImg texture, TextureImg bumpmap) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.texture = texture;
        this.bumpmap = bumpmap;
    }

    public abstract void setShader(RacetrackShader shader);

    public abstract Vector3f getPoint(int segment, float t);
    public abstract Vector3f getTangent(int segment, float t);
    public abstract int getSize();
    public abstract int getWidth();
    public abstract void setShadowMap(int shadowMap);

    public void setControlPoints(Vector3f[] controlPoints){
        this.controlPoints = controlPoints;
    }

    public void setSegments(int nr) {
        nrOfSegments = nr;
    }

    public void setVAOValues(IntBuffer vao, int nrV){
        this.vao = vao;
        this.nrV = nrV;
    }

    public IntBuffer getVao() {
        return vao;
    }

    public int getNrV() {
        return nrV;
    }
    
    public int getNrOfSegments() {
        return nrOfSegments;
    }

    public void draw(GL3 gl, Car player, Matrix4f shadowMatrix) {
        prepare(gl, player);
        shader.loadShadowMatrix(gl, shadowMatrix);

        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        
        gl.glDrawElements(GL3.GL_TRIANGLES, nrV,
                GL3.GL_UNSIGNED_INT, 0);
        
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }

    public Matrix4f getTransformationMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotate((float) Math.toRadians(rotx), 1, 0, 0)
                .rotate((float) Math.toRadians(roty), 0, 1, 0)
                .rotate((float) Math.toRadians(rotz), 0, 0, 1)
                .scale(size, size, size);
    }
    
    /**
     * Calculates the normal of the point.
     * 
     * @param tangent the tangent of the point.
     * @return the normal of the point.
     */
    public static Vector3f calcNormal(Vector3f tangent) {
        Vector3f sideVector = new Vector3f(tangent);
        sideVector.rotateY((float) (0.5 * Math.PI));
        sideVector.y = 0;
        sideVector.normalize();
        return tangent.cross(sideVector);
    }

    private void prepare(GL3 gl, Car player) {
        shader.start(gl);

        shader.loadProjectionMatrix(gl, GS.getCam(player).getProjectionMatrix());
        //shader.loadProjectionMatrix(gl, projectionMatrix);
        shader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        //shader.loadViewMatrix(gl, viewMatrix);
        shader.loadLight(gl,GS.getLights().get(0));
        shader.loadCameraPos(gl, GS.getCam(player).getPosition());
        shader.loadTextures(gl);

        shader.loadModelMatrix(gl, getTransformationMatrix());
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, texture.getTexture());
        gl.glActiveTexture(GL3.GL_TEXTURE1);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, bumpmap.getTexture());
        gl.glActiveTexture(GL3.GL_TEXTURE2);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, shadowMap);
        gl.glEnable(GL3.GL_TEXTURE_2D);
    }

    public void draw(GL3 gl, ShadowShader shader){
        prepare(gl, shader);

        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glDrawElements(GL3.GL_TRIANGLES, nrV,
                GL3.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisable(GL3.GL_TEXTURE_2D);
        gl.glBindVertexArray(0);
    }

    private void prepare(GL3 gl, ShadowShader shader){
        shader.loadModelMatrix(gl, getTransformationMatrix());
    }
    
    
}
