package src.Shaders;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;

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

    public DefaultShader(GL2 gl) {
        super(gl, vertex, fragment);
    }

    @Override
    public void bindAttributes(GL2 gl) {
        super.bindAttr(gl, 0,"position");
    }

    @Override
    protected void getAllUniformLocations(GL2 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");

        System.out.println(projectionMatrixLocation);
        System.out.println(viewMatrixLocation);
        System.out.println(modelMatrixLocation);
    }

    public void loadProjectionMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, projectionMatrixLocation, matrix);
    }

    public void loadViewMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    public void loadModelMatrix(GL2 gl, Matrix4f matrix){
        loadUniformMatrix(gl, modelMatrixLocation, matrix);
    }
}
