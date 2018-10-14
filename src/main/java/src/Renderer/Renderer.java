
package src.Renderer;


// Jogamp imports

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import org.joml.Matrix4f;
import src.Controllers.PlayerController;
import src.GS;
import src.Shaders.RacetrackShader;
import src.Simulator;
import src.shadows.ShadowRenderer;

import static com.jogamp.opengl.GL2ES2.GL_SHADING_LANGUAGE_VERSION;

// Own imports


public class Renderer
        implements GLEventListener {

    private Simulator simulator;
    private GL3 gl;
    private GLU glu;

    private static float fov = 70;
    private static boolean fovChange = false;
    private final float NEAR = 0.1f;
    private final float FAR = 2000f;
    private float width = 1080;
    private float height = 720;

    private Matrix4f projectionMatrix;

    private ObjectRenderer objectRenderer;
    private TerrainRenderer terrainRenderer;
    private MaterialRenderer materialRenderer;
    private ItemRenderer itemRenderer;
    private GUIRenderer guiRenderer;
    private ShadowRenderer shadowRenderer;

    public Renderer(Simulator simulator, float width, float height){
        this.simulator = simulator;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        this.gl = glAutoDrawable.getGL().getGL3();
        this.glu = new GLU();
        
        gl.glViewport(0, 0, GS.WIDTH, GS.HEIGHT);

        System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

        simulator.setGL(gl);
        simulator.initAssets();
        GS.playerController = new PlayerController(GS.player, 1);

        getProjectionMatrix();
        objectRenderer = new ObjectRenderer(gl,projectionMatrix);
        materialRenderer = new MaterialRenderer(gl, projectionMatrix);
        terrainRenderer = new TerrainRenderer(gl,projectionMatrix);
        itemRenderer = new ItemRenderer(gl, projectionMatrix);
        guiRenderer = new GUIRenderer(gl);
        shadowRenderer = new ShadowRenderer(gl, fov, NEAR, FAR, width, height);
        GS.getTrack().setShadowMap(shadowRenderer.getDepthTexture());

        RacetrackShader racetrackShader = new RacetrackShader(gl);
        GS.getTrack().setShaderAndRenderMatrices(racetrackShader, projectionMatrix,
                GS.camera.getViewMatrix());

        gl.glEnable(GL3.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        //gl.glViewport(0, 0, GS.canvas.getWidth(), GS.canvas.getHeight());
        shadowRenderer.render(gl);

        gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
        gl.glClearColor(1f, 1f, 1f, 1f);

        gl.glEnable(GL3.GL_CULL_FACE);
        gl.glCullFace(GL3.GL_BACK);

        if (fovChange){
            getProjectionMatrix();
            fovChange = false;
        }

        objectRenderer.render(gl);
        materialRenderer.render(gl);
        terrainRenderer.render(gl);
        itemRenderer.render(gl);

        gl.glDisable(GL3.GL_CULL_FACE);
        GS.getTrack().draw(gl, shadowRenderer.getShadowMatrix());
        GS.getSkybox().draw(gl, projectionMatrix);
        guiRenderer.render(gl);

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y,
            int width, int height) {
        
    }

    private void getProjectionMatrix(){
        float ratio = width / height;
        float y = (float) (1f / Math.tan(Math.toRadians(fov/2f)));
        float x = y / ratio;
        float delta = FAR - NEAR;

        Matrix4f pMatrix = new Matrix4f();
        pMatrix.m00(x);
        pMatrix.m11(y);
        pMatrix.m22(-((NEAR + FAR)/delta));
        pMatrix.m23(-1);
        pMatrix.m32(-(2*NEAR*FAR)/delta);
        pMatrix.m33(0);

        projectionMatrix = new Matrix4f((pMatrix));
    }

    public void cleanup(){
        simulator.cleanup();
    }

    public static void changeFOV(float fovOffset){
        fov = 70 + fovOffset;
        fovChange = true;
        //System.out.println("fov Change " + fov);
    }

}
