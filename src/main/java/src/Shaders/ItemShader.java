package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.GS;
import src.OBJ.MTLObject;

public class ItemShader
        extends ShaderProgram {
    // Handy file paths.
    final private static String VERTEX = GS.SHADER_DIR + "item_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR + "item_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;
    private int lightPositionLocation;
    private int lightColorLocation;
    private int shininessLocation;
    private int reflectivityLocation;
    private int timeLocation;
    private int cameraPosLocation;
    private int textureLocation;

    public ItemShader(GL3 gl) {
        super(gl, VERTEX, FRAGMENT);
    }

    @Override
    public void bindAttributes(GL3 gl) {
        bindAttr(gl, 0,"position");
        bindAttr(gl, 1, "tex");
        bindAttr(gl, 2, "normal");
    }

    @Override
    protected void getAllUniformLocations(GL3 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");
        lightPositionLocation = getUniformLocation(gl, "lightPosition");
        lightColorLocation = getUniformLocation(gl, "lightColor");
        shininessLocation = getUniformLocation(gl, "shininess");
        reflectivityLocation = getUniformLocation(gl, "reflectivity");
        timeLocation = getUniformLocation(gl, "time");
        cameraPosLocation = getUniformLocation(gl, "camera");
        textureLocation = getUniformLocation(gl, "itemTexture");


        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);
        System.out.println("Lightpos: " + lightPositionLocation);
        System.out.println("LightColor: " + lightColorLocation);
        System.out.println("Shininess: " + shininessLocation);
        System.out.println("Reflectivity: " + reflectivityLocation);
        System.out.println("CameraPos: " + cameraPosLocation);
        System.out.println("TextureLocation: " + textureLocation);
    }
    
    @Override
    public void loadVars(GL3 gl) {
        
    }

    @Override
    public void loadProjectionMatrix(GL3 gl, Matrix4f matrix){
        loadUniformMatrix(gl, projectionMatrixLocation, matrix);
    }

    @Override
    public void loadViewMatrix(GL3 gl, Matrix4f matrix){
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix){
        loadUniformMatrix(gl, modelMatrixLocation, matrix);
    }

    @Override
    public void loadLight(GL3 gl, Light light){
        loadUniformVector(gl, lightPositionLocation,light.getPosition());
        loadUniformVector(gl, lightColorLocation, light.getColor());
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
        loadUniformFloat(gl, shininessLocation, shininess);
        loadUniformFloat(gl, reflectivityLocation, reflectivity);
    }

    @Override
    public void loadTextures(GL3 gl){
        loadUniformInt(gl, textureLocation, 0);
    }

    @Override
    public void loadTime(GL3 gl, int time){
        loadUniformInt(gl,timeLocation,time);
    }

    @Override
    public void loadCameraPos(GL3 gl, Vector3f cameraPos){
        loadUniformVector(gl, cameraPosLocation, cameraPos);
    }
    
    
}
