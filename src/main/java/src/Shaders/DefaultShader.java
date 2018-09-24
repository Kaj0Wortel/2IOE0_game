package src.Shaders;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;

public class DefaultShader extends ShaderProgram {

    final public static String FS = System.getProperty("file.separator");

    // Handy file paths.
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src" +  FS;

    final public static String SHADERS_DIR = WORKING_DIR + "Shaders" + FS + "ShaderFiles" + FS;

    final private static String vertex = SHADERS_DIR + "default_vertex.glsl";
    final private static String fragment = SHADERS_DIR + "default_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;
    private int lightPositionLocation;
    private int lightColorLocation;
    private int shininessLocation;
    private int reflectivityLocation;
    private int timeLocation;
    private int cameraPosLocation;

    public DefaultShader(GL2 gl) {
        super(gl, vertex, fragment);
    }

    @Override
    public void bindAttributes(GL2 gl) {
        bindAttr(gl, 0,"position");
        bindAttr(gl, 1, "tex");
        bindAttr(gl, 2, "normal");
    }

    @Override
    protected void getAllUniformLocations(GL2 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");
        lightPositionLocation = getUniformLocation(gl, "lightPosition");
        lightColorLocation = getUniformLocation(gl, "lightColor");
        shininessLocation = getUniformLocation(gl, "shininess");
        reflectivityLocation = getUniformLocation(gl, "reflectivity");
        timeLocation = getUniformLocation(gl, "time");
        cameraPosLocation = getUniformLocation(gl, "camera");

        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);
        System.out.println("Lightpos: " + lightPositionLocation);
        System.out.println("LightColor: " + lightColorLocation);
        System.out.println("Shininess: " + shininessLocation);
        System.out.println("Reflectivity: " + reflectivityLocation);
        System.out.println("CameraPos: " + cameraPosLocation);
    }

    @Override
    public void loadProjectionMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, projectionMatrixLocation, matrix);
    }

    @Override
    public void loadViewMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    @Override
    public void loadModelMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, modelMatrixLocation, matrix);
    }

    public void loadLight(GL2 gl, Light light){
        loadUniformVector(gl, lightPositionLocation,light.getPosition());
        loadUniformVector(gl, lightColorLocation, light.getColor());
    }

    public void loadTextureLightValues(GL2 gl, float shininess, float reflectivity){
        loadUniformFloat(gl, shininessLocation, shininess);
        loadUniformFloat(gl, reflectivityLocation, reflectivity);
    }

    public void loadTime(GL2 gl, int time){
        loadUniformInt(gl,timeLocation,time);
    }

    public void loadCameraPos(GL2 gl, Vector3f cameraPos){
        loadUniformVector(gl, cameraPosLocation, cameraPos);
    }
}
