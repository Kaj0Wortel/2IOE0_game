package src.shadows;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.Shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    public ShadowShader(GL3 gl, String vertex, String fragment) {
        super(gl, vertex, fragment);
    }

    @Override
    protected void bindAttributes(GL3 gl) {
        bindAttr(gl, 0,"position");
    }

    @Override
    protected void getAllUniformLocations(GL3 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");

        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);

    }

    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl,modelMatrixLocation,matrix);
    }

    @Override
    public void loadViewMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl,viewMatrixLocation,matrix);
    }

    @Override
    public void loadProjectionMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl,projectionMatrixLocation,matrix);
    }

    @Override
    public void loadTextureLightValues(GL3 gl, float shininess, float reflectivity) {

    }

    @Override
    public void loadTime(GL3 gl, int time) {

    }

    @Override
    public void loadCameraPos(GL3 gl, Vector3f cameraPos) {

    }

    @Override
    public void loadLight(GL3 gl, Light light) {

    }
}
