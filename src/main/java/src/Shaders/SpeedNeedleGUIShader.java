
package src.Shaders;


import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.GS;
import src.OBJ.MTLObject;


public class SpeedNeedleGUIShader
        extends ShaderProgram {
    // Handy file paths.
    final private static String VERTEX = GS.SHADER_DIR + "gui_needle_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR + "gui_needle_fragment.glsl";

    private int transformationMatrixLoc;
    private int screenRatioLoc;
    private int angleCosLoc;
    private int angleSinLoc;
    
    private int speedNeedleLoc;

    
    public SpeedNeedleGUIShader(GL3 gl) {
        super(gl, VERTEX, FRAGMENT);
    }

    
    @Override
    protected void bindAttributes(GL3 gl) {
        bindAttr(gl, 0, "position");
    }
    
    @Override
    protected void getAllUniformLocations(GL3 gl) {
        transformationMatrixLoc = getUniformLocation(gl, "transformationMatrix");
        screenRatioLoc = getUniformLocation(gl, "screenRatio");
        angleSinLoc = getUniformLocation(gl, "sinAngle");
        angleCosLoc = getUniformLocation(gl, "cosAngle");
        
        speedNeedleLoc = getUniformLocation(gl, "speedNeedle");
        
        System.out.println("Transformation Matrix: " + transformationMatrixLoc);
        System.out.println("screenRatioLoc: " + screenRatioLoc);
        System.out.println("angleSinLoc: " + angleSinLoc);
        System.out.println("angleCosLoc: " + angleCosLoc);
        
        System.out.println("speedNeedleLoc: " + speedNeedleLoc);
    }
    
    @Override
    public void loadVars(GL3 gl) {
        float ratio = ((float) GS.canvas.getWidth()) / GS.canvas.getHeight();
        loadUniformFloat(gl, screenRatioLoc, ratio);
    }
    
    @Override
    public void loadModelMatrix(GL3 gl, Matrix4f matrix) {
        loadUniformMatrix(gl, transformationMatrixLoc, matrix);
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

    @Override
    public void loadMaterial(GL3 gl, MTLObject mtl) {
        
    }
    
    /**
     * Loads the sine and cosine of the angle to the shader.
     * The angle must be in radians.
     * 
     * @param gl
     * @param angle 
     */
    public void loadAngle(GL3 gl, float angle) {
        loadUniformFloat(gl, angleSinLoc, (float) Math.sin(angle));
        loadUniformFloat(gl, angleCosLoc, (float) Math.cos(angle));
    }

    @Override
    public boolean useMaterial() {
        return false;
    }

    @Override
    public void loadTextures(GL3 gl) {
        loadUniformInt(gl, speedNeedleLoc, 0);
    }
    
    
}
