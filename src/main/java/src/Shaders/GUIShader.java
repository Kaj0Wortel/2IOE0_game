package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;

public class GUIShader extends ShaderProgram {

    final public static String FS = System.getProperty("file.separator");

    // Handy file paths.
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src" +  FS;

    final public static String SHADERS_DIR = WORKING_DIR + "Shaders" + FS + "ShaderFiles" + FS;

    final private static String vertex = SHADERS_DIR + "gui_vertex.glsl";
    final private static String fragment = SHADERS_DIR + "gui_fragment.glsl";

    private int transformationMatrixLocation;
    private int textureLocation;

    public GUIShader(GL3 gl) {
        super(gl, vertex, fragment);
    }

    @Override
    protected void bindAttributes(GL3 gl) {
        bindAttr(gl,0,"position");

    }

    @Override
    protected void getAllUniformLocations(GL3 gl) {
        transformationMatrixLocation = getUniformLocation(gl, "transformationMatrix");
        textureLocation = getUniformLocation(gl, "gui");

        System.out.println("Transformation Matrix: " + transformationMatrixLocation);
        System.out.println("TextureLocation: " + textureLocation);
    }

    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, transformationMatrixLocation, matrix);
    }

    @Override
    public void loadViewMatrix(GL3 gl, Matrix4f matrix) {

    }

    @Override
    public void loadProjectionMatrix(GL3 gl, Matrix4f matrix4f) {

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
        loadUniformInt(gl, textureLocation, 0);
    }
}
