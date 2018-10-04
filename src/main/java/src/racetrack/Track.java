package src.racetrack;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.TextureImg;
import src.GS;
import src.Shaders.RacetrackShader;

import java.nio.IntBuffer;

public abstract class Track {

    protected Vector3f[] control_points;
    protected int nr_of_segments;

    final protected Vector3f position;
    final protected float size;
    final protected float rotx;
    final protected float roty;
    final protected float rotz;

    protected IntBuffer vao;
    protected int nrV;
    protected TextureImg texture;

    protected RacetrackShader shader;
    protected Matrix4f projectionMatrix;
    protected Matrix4f viewMatrix;

    public Track(Vector3f position, float size, float rotx, float roty, float rotz, TextureImg texture) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.texture = texture;
    }

    public abstract void setShaderAndRenderMatrices(RacetrackShader shader, Matrix4f projectionMatrix, Matrix4f viewMatrix);

    public abstract Vector3f getPoint(int segment, float t);

    public void setControl_points(Vector3f[] control_points){
        this.control_points = control_points;
    }

    public void setSegments(int nr){
        nr_of_segments = nr;
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

    public void draw(GL3 gl){
        prepare(gl);

        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glDrawElements(GL2.GL_TRIANGLES, nrV,
                GL2.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }

    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(position);
        transformationMatrix.rotate((float) Math.toRadians(rotx), 1, 0, 0);
        transformationMatrix.rotate(
                (float) Math.toRadians(roty), 0, 1, 0);
        transformationMatrix.rotate((float) Math.toRadians(rotz), 0, 0, 1);
        transformationMatrix.scale(size, size, size);

        return transformationMatrix;
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
    

    private void prepare(GL3 gl){
        shader.start(gl);

        shader.loadProjectionMatrix(gl,projectionMatrix);
        shader.loadViewMatrix(gl, GS.camera.getViewMatrix());
        shader.loadLight(gl,GS.getLights().get(0));
        shader.loadCameraPos(gl, GS.camera.getPosition());

        shader.loadModelMatrix(gl, getTransformationMatrix());
        texture.bindTexture(gl);
    }
    
    
}
