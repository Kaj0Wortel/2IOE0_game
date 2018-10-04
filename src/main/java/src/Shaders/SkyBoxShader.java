package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;

public class SkyBoxShader extends ShaderProgram {

    final public static String FS = System.getProperty("file.separator");

    // Handy file paths.
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src" +  FS;

    final public static String SHADERS_DIR = WORKING_DIR + "Shaders" + FS + "ShaderFiles" + FS;

    final private static String vertex = SHADERS_DIR + "skybox_vertex.glsl";
    final private static String fragment = SHADERS_DIR + "skybox_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int cubeMapLocation;

    public SkyBoxShader(GL3 gl) {
        super(gl, vertex, fragment);
    }

    @Override
    protected void bindAttributes(GL3 gl) {
        bindAttr(gl,0,"position");

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

    public void loadTexture(GL3 gl){
        loadUniformInt(gl, cubeMapLocation, 0);
    }
}
