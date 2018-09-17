package src.Shaders;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
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

    public DefaultShader(GL2 gl) {
        super(gl, vertex, fragment);
    }

    @Override
    public void bindAttributes(GL2 gl) {
        super.bindAttr(gl, 0,"position");
        super.bindAttr(gl, 1, "tex");
        super.bindAttr(gl, 2, "normal");
    }

    @Override
    protected void getAllUniformLocations(GL2 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");
        lightPositionLocation = getUniformLocation(gl, "lightPosition");
        lightColorLocation = getUniformLocation(gl, "lightColor");

        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);
        System.out.println("Lightpos: " + lightPositionLocation);
        System.out.println("LightColor: " + lightColorLocation);
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
}
