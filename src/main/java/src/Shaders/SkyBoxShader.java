package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.GS;
import src.OBJ.MTLObject;

public class SkyBoxShader
        extends ShaderProgram {
    // Handy file paths.
    final private static String VERTEX = GS.SHADER_DIR + "skybox_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR + "skybox_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int cubeMapLocation;

    public SkyBoxShader(GL3 gl) {
        super(gl, VERTEX, FRAGMENT);
    }

    @Override
    protected void bindAttributes(GL3 gl) {
        bindAttr(gl, 0, "position");

    }

    @Override
    protected void getAllUniformLocations(GL3 gl) {
        projectionMatrixLocation = getUniformLocation(gl, "projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl, "viewMatrix");
        cubeMapLocation = getUniformLocation(gl, "cubeMap");

        System.out.println("Transformation Matrix: " + projectionMatrixLocation);
        System.out.println("View Matrix: " + viewMatrixLocation);
        System.out.println("TextureLocation: " + cubeMapLocation);
    }

    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix) {

    }

    @Override
    public void loadViewMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    @Override
    public void loadProjectionMatrix(GL3 gl, Matrix4f matrix4f) {
        loadUniformMatrix(gl, projectionMatrixLocation, matrix4f);
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

    @Override
    public void loadMaterial(GL3 gl, MTLObject mtl) {

    }

    @Override
    public boolean useMaterial() {
        return false;
    }

    public void loadTexture(GL3 gl){
        loadUniformInt(gl, cubeMapLocation, 0);
    }
}
