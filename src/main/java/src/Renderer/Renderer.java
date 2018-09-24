
package src.Renderer;


// Jogamp imports

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Instance;
import src.Assets.Light;
import src.Controllers.PlayerController;
import src.GS;
import src.Shaders.DefaultShader;
import src.Shaders.ShaderProgram;
import src.Shaders.TerrainShader;
import src.Simulator;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL2ES2.GL_SHADING_LANGUAGE_VERSION;

// Own imports


public class Renderer implements GLEventListener {

    private Simulator simulator;
    private GL2 gl;
    private GLU glu;

    private final float FOV = 70;
    private final float NEAR = 0.1f;
    private final float FAR = 1000f;
    private float width = 1080;
    private float height = 720;


    private ShaderProgram currentShader;
    private ShaderProgram defaultShader;
    private ShaderProgram terrainShader;
    private Light light;

    private int counter = 0;

    public Renderer(Simulator simulator, float width, float height){
        this.simulator = simulator;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        this.gl = glAutoDrawable.getGL().getGL2();
        this.glu = new GLU();

        gl.glEnable(gl.GL_CULL_FACE);
        gl.glCullFace(gl.GL_BACK);

        light = new Light(new Vector3f(0f,50f,0f), new Vector3f(1f,1f,1f));

        System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

        simulator.setGL(gl);
        simulator.initAssets();
        GS.playerController = new PlayerController(simulator.getPlayer());

        defaultShader = new DefaultShader(gl);
        terrainShader = new TerrainShader(gl);
        currentShader = defaultShader;
        currentShader.start(gl);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        counter++;
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        gl.glClearColor(1f, 1f, 1f, 1f);

        renderObjects();
        renderTerrain();
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    private Matrix4f getProjectionMatrix(){
        float ratio = width / height;
        float y = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * ratio);
        float x = y / ratio;
        float delta = FAR - NEAR;

        Matrix4f pMatrix = new Matrix4f();
        pMatrix.m00(x);
        pMatrix.m11(y);
        pMatrix.m22(-((NEAR + FAR)/delta));
        pMatrix.m23(-1);
        pMatrix.m32(-(2*NEAR*FAR)/delta);
        pMatrix.m33(0);

        return pMatrix;
    }

    public void cleanup(){
        simulator.cleanup();
    }

    private void renderObjects(){
        currentShader = defaultShader;
        currentShader.start(gl);

        currentShader.loadProjectionMatrix(gl,getProjectionMatrix());
        currentShader.loadViewMatrix(gl,GS.getCamera().getViewMatrix());
        currentShader.loadLight(gl,light);
        currentShader.loadTime(gl, counter);
        currentShader.loadCameraPos(gl, GS.getCamera().getPosition());

        for(Instance asset : GS.getAssets()){
            currentShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            asset.draw(gl, currentShader);
        }

        currentShader.stop(gl);
    }

    private void renderTerrain(){
        currentShader = terrainShader;
        currentShader.start(gl);

        currentShader.loadProjectionMatrix(gl,getProjectionMatrix());
        currentShader.loadViewMatrix(gl,GS.getCamera().getViewMatrix());
        currentShader.loadLight(gl,light);
        currentShader.loadTime(gl, counter);
        currentShader.loadCameraPos(gl, GS.getCamera().getPosition());

        for(Instance asset : GS.getTerrain()){
            currentShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            asset.getModel().getTextureImg().bindTexture(gl);
            asset.draw(gl, currentShader);
        }

        currentShader.stop(gl);
    }

}
