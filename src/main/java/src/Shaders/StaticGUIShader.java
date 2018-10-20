
package src.Shaders;


import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.GS;
import src.OBJ.MTLObject;


public class StaticGUIShader
        extends ShaderProgram {
    // Handy file paths.
    final private static String VERTEX = GS.SHADER_DIR + "gui_static_vertex.glsl";
    final private static String FRAGMENT = GS.SHADER_DIR + "gui_static_fragment.glsl";
    
    // Variable locations.
    private int transformationMatrixLoc;
    private int screenRatioLoc;
    private int itemNumLoc;
    private int positionNumLoc;
    private int time1Loc;
    private int time2Loc;
    private int time3Loc;
    private int time4Loc;
    
    // Image locations.
    private int speedMeterLoc;
    private int itemBoxLoc;
    private int itemLoc;
    private int positionsLoc;
    private int numbersLoc;
    
    
    public StaticGUIShader(GL3 gl) {
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
        itemNumLoc = getUniformLocation(gl, "itemNum");
        positionNumLoc = getUniformLocation(gl, "positionNum");
        time1Loc = getUniformLocation(gl, "time1");
        time2Loc = getUniformLocation(gl, "time2");
        time3Loc = getUniformLocation(gl, "time3");
        time4Loc = getUniformLocation(gl, "time4");
        
        speedMeterLoc = getUniformLocation(gl, "speedMeter");
        itemBoxLoc = getUniformLocation(gl, "itemBox");
        itemLoc = getUniformLocation(gl, "item");
        positionsLoc = getUniformLocation(gl, "positions");
        numbersLoc = getUniformLocation(gl, "numbers");
        
        // Variable locations.
        System.out.println("Transformation Matrix: " + transformationMatrixLoc);
        System.out.println("screenRatioLoc: " + screenRatioLoc);
        System.out.println("itemNumLoc: " + itemNumLoc);
        System.out.println("positionNumPos: " + positionNumLoc);
        System.out.println("time1Loc: " + time1Loc);
        System.out.println("time2Loc: " + time2Loc);
        System.out.println("time3Loc: " + time3Loc);
        System.out.println("time4Loc: " + time4Loc);
        
        // Image locations.
        System.out.println("speedMeterLoc: " + speedMeterLoc);
        System.out.println("itemBoxLoc: " + itemBoxLoc);
        System.out.println("itemLoc: " + itemLoc);
        System.out.println("positionsLoc: " + positionsLoc);
        System.out.println("numbersLoc: " + numbersLoc);
    }
    
    @Override
    public void loadVars(GL3 gl) {
        float ratio = ((float) GS.canvas.getWidth()) / GS.canvas.getHeight();
        loadUniformFloat(gl, screenRatioLoc, ratio);
    }
    
    public void loadItemNum(GL3 gl, int num) {
        loadUniformInt(gl, itemNumLoc, num);
    }
    
    public void loadPositionNum(GL3 gl, int num) {
        loadUniformInt(gl, positionNumLoc, num);
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
        // Minutes.
        loadUniformInt(gl, time1Loc, (time / 600) % 6 );
        loadUniformInt(gl, time2Loc, (time / 60 ) % 10);
        // Seconds.
        loadUniformInt(gl, time3Loc, (time / 10) % 6);
        loadUniformInt(gl, time4Loc, time % 10);
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

    @Override
    public boolean useMaterial() {
        return false;
    }

    @Override
    public void loadTextures(GL3 gl) {
        loadUniformInt(gl, speedMeterLoc, 0);
        loadUniformInt(gl, itemBoxLoc, 1);
        loadUniformInt(gl, itemLoc, 2);
        loadUniformInt(gl, positionsLoc, 3);
        loadUniformInt(gl, numbersLoc, 4);
    }
    
    
}
