package src.Shaders;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import src.Assets.Light;
import src.Assets.Texture;
import src.GS;

public class DefaultShader
        extends ShaderProgram {

    final public static String FS = System.getProperty("file.separator");

    // Handy file paths.
    final public static String WORKING_DIR = System.getProperty("user.dir")
            + FS + "src" +  FS;

    final private static String VERTEX = GS.SHADER_DIR
            + "default_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR
            + "default_fragment.glsl";

    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;
    //private int lightPositionLocation;
    //private int lightColorLocation;
    //private int shininessLocation;
    //private int reflectivityLocation;
    private int timeLocation;
    private int matAmbientLoc;
    private int matDiffuseLoc;
    private int matSpecularLoc;
    private int matShininessLoc;

    public DefaultShader(GL2 gl) {
        super(gl, VERTEX, FRAGMENT);
    }

    @Override
    public void bindAttributes(GL2 gl) {
        super.bindAttr(gl, 0, "position");
        super.bindAttr(gl, 1, "tex");
        super.bindAttr(gl, 2, "normal");
    }

    @Override
    protected void getAllUniformLocations(GL2 gl) {
        projectionMatrixLocation = getUniformLocation(gl,"projectionMatrix");
        viewMatrixLocation = getUniformLocation(gl,"viewMatrix");
        modelMatrixLocation = getUniformLocation(gl, "modelMatrix");
        //lightPositionLocation = getUniformLocation(gl, "lightPosition");
        //lightColorLocation = getUniformLocation(gl, "lightColor");
        int matAmbientLoc = getUniformLocation(gl, "material.ambient");
        int matDiffuseLoc = getUniformLocation(gl, "material.diffuse");
        int matSpecularLoc = getUniformLocation(gl, "material.specular");
        int matShininessLoc = getUniformLocation(gl, "material.shininess");
        //shininessLocation = getUniformLocation(gl, "shininess");
        //reflectivityLocation = getUniformLocation(gl, "reflectivity");
        timeLocation = getUniformLocation(gl, "time");
        
        System.out.println("Projection location: " + projectionMatrixLocation);
        System.out.println("ViewMatrix Location: " + viewMatrixLocation);
        System.out.println("TransformationMatrix Location: " + modelMatrixLocation);
        //System.out.println("lightPosition: " + lightPositionLocation);
        //System.out.println("lightColor: " + lightColorLocation);
        //System.out.println("shininess: " + shininessLocation);
        //System.out.println("reflectivity: " + reflectivityLocation);
        System.out.println("material ambient location: " + matAmbientLoc);
        System.out.println("material diffuse location: " + matDiffuseLoc);
        System.out.println("material specular location: " + matSpecularLoc);
        System.out.println("material shininess location: " + matShininessLoc);
        System.out.println("time: " + timeLocation);
        
        /*
        this.setVec3("material.ambient",  1.0f, 0.5f, 0.31f);
        lightingShader.setVec3("material.diffuse",  1.0f, 0.5f, 0.31f);
        lightingShader.setVec3("material.specular", 0.5f, 0.5f, 0.5f);
        lightingShader.setFloat("material.shininess", 32.0f);
        /**/
    }

    @Override
    public void loadProjectionMatrix(GL2 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, projectionMatrixLocation, matrix);
    }

    @Override
    public void loadViewMatrix(GL2 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, viewMatrixLocation, matrix);
    }

    @Override
    public void loadModelMatrix(GL2 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, modelMatrixLocation, matrix);
    }
    
    public void loadLight(GL2 gl, Light[] light) {
        for (int i = 0; i < light.length; i++) {
            int lightPosLoc = getUniformLocation(gl, "light[" + i + "].position");
            int lightAmbientLoc = getUniformLocation(gl, "light[" + i + "].ambient");
            int lightDiffuseLoc = getUniformLocation(gl, "light[" + i + "].diffuse");
            int lightSpecularLoc = getUniformLocation(gl, "light[" + i + "].specular");
            
            if (lightPosLoc == -1 || lightAmbientLoc == -1 ||
                    lightDiffuseLoc == -1 || lightSpecularLoc == -1) {
                System.out.println("Could not add light " + i + ":");
                System.out.println("    location = " + lightPosLoc);
                System.out.println("    ambient  = " + lightAmbientLoc);
                System.out.println("    diffuse  = " + lightDiffuseLoc);
                System.out.println("    specular = " + lightSpecularLoc);
                return;
            }
            
            loadUniformVector4f(gl, lightPosLoc, light[i].getPosition());
            loadUniformVector4f(gl, lightAmbientLoc, light[i].getAmbient());
            loadUniformVector4f(gl, lightDiffuseLoc, light[i].getDiffuse());
            loadUniformVector4f(gl, lightSpecularLoc, light[i].getSpecular());
        }
    }

    @Override
    public void loadTexture(GL2 gl, Texture texture) {
        loadUniformVector4f(gl, matAmbientLoc, texture.getAmbient());
        loadUniformVector4f(gl, matDiffuseLoc, texture.getDiffuse());
        loadUniformVector4f(gl, matSpecularLoc, texture.getSpecular());
        loadUniformFloat(gl, matShininessLoc, texture.getShininess());
    }

    @Override
    public void loadTime(GL2 gl, int time) {
        loadUniformInt(gl,timeLocation,time);
    }
    
    
}
