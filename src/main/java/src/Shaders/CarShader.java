package src.Shaders;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.GS;
import src.OBJ.MTLObject;

public class CarShader
        extends ShaderProgram {
    // Handy file paths.
    final private static String VERTEX = GS.SHADER_DIR + "car_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR + "car_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;
    private int lightPositionLocation;
    private int lightColorLocation;
    private int shininessLocation;
    private int reflectivityLocation;
    private int timeLocation;
    private int cameraPosLocation;

    private int materialAmbientLocation;
    private int materialDiffuseLocation;
    private int materialSpecularLocation;
    private int illuminationLocation;

    private int shadowMapLocation;
    private int shadowMatrixLocation;

    public CarShader(GL3 gl) {
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

        materialAmbientLocation = getUniformLocation(gl, "matAmbient");
        materialDiffuseLocation = getUniformLocation(gl, "matDiffuse");
        materialSpecularLocation = getUniformLocation(gl, "matSpecular");
        illuminationLocation = getUniformLocation(gl, "illum");

        shadowMapLocation = getUniformLocation(gl, "shadowMap");
        shadowMatrixLocation = getUniformLocation(gl, "shadowMatrix");


        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);
        System.out.println("Lightpos: " + lightPositionLocation);
        System.out.println("LightColor: " + lightColorLocation);
        System.out.println("Shininess: " + shininessLocation);
        System.out.println("Reflectivity: " + reflectivityLocation);
        System.out.println("CameraPos: " + cameraPosLocation);
        System.out.println("Ambient: " + materialAmbientLocation);
        System.out.println("Diffuse: " + materialDiffuseLocation);
        System.out.println("Specular: " + materialSpecularLocation);
        System.out.println("Illumination: " + illuminationLocation);
        System.out.println("ShadowMap: "+ shadowMapLocation);
        System.out.println("ShadowMatrix: " + shadowMatrixLocation);
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

    public void loadLight(GL3 gl, Light light){
        loadUniformVector(gl, lightPositionLocation,light.getPosition());
        loadUniformVector(gl, lightColorLocation, light.getColor());
    }

    @Override
    public void loadMaterial(GL3 gl, MTLObject mtl) {
        loadUniformFloat(gl, shininessLocation, mtl.shininess);
        loadUniformVector(gl, materialAmbientLocation, mtl.ambiant);
        loadUniformVector(gl, materialDiffuseLocation, mtl.diffuse);
        loadUniformVector(gl, materialSpecularLocation, mtl.specular);
        loadUniformInt(gl, illuminationLocation, mtl.illumination);
    }

    @Override
    public boolean useMaterial() {
        return true;
    }

    @Override
    public void loadTextureLightValues(GL3 gl, float shininess, float reflectivity){
        loadUniformFloat(gl, shininessLocation, shininess);
        loadUniformFloat(gl, reflectivityLocation, reflectivity);
    }

    @Override
    public void loadTime(GL3 gl, int time){
        loadUniformInt(gl,timeLocation,time);
    }

    @Override
    public void loadCameraPos(GL3 gl, Vector3f cameraPos){
        loadUniformVector(gl, cameraPosLocation, cameraPos);
    }

    public void loadTextures(GL3 gl){
        loadUniformInt(gl, shadowMapLocation, 0);
    }

    public void loadShadowMatrix(GL3 gl, Matrix4f shadowMatrix){
        loadUniformMatrix(gl, shadowMatrixLocation, shadowMatrix);
    }
}