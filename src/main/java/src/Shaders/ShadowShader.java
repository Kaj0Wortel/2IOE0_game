package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.OBJ.MTLObject;

public class ShadowShader
        extends ShaderProgram {

    final public static String FS = System.getProperty("file.separator");

    // Handy file paths.
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src" +  FS;

    final public static String SHADERS_DIR = WORKING_DIR + "Shaders" + FS + "ShaderFiles" + FS;

    final private static String VERTEX = SHADERS_DIR + "shadow_vertex.glsl";
    final private static String FRAGMENT = SHADERS_DIR + "shadow_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    public ShadowShader(GL3 gl) {
        super(gl, VERTEX, FRAGMENT);

    }

    @Override
    public void bindAttributes(GL3 gl) {

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
    public void loadVars(GL3 gl) {
        
    }

    @Override
    public void loadProjectionMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, projectionMatrixLocation, matrix);
    }

    @Override
    public void loadViewMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, modelMatrixLocation, matrix);
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

    @Override
    public void loadTextureLightValues(GL3 gl, float shininess, float reflectivity){

    }

    @Override
    public void loadTime(GL3 gl, int time) {

    }

    @Override
    public void loadCameraPos(GL3 gl, Vector3f cameraPos) {

    }

    @Override
    public void loadTextures(GL3 gl) {
        
    }
    

}
