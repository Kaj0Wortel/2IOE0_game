
package src.Renderer;


// Jogamp imports

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import org.joml.Matrix4f;
import src.Controllers.PlayerController;
import src.GS;
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

    private Matrix4f projectionMatrix;

    private ObjectRenderer objectRenderer;
    private TerrainRenderer terrainRenderer;

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

        System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

        simulator.setGL(gl);
        simulator.initAssets();
        GS.playerController = new PlayerController(simulator.getPlayer());

        getProjectionMatrix();
        objectRenderer = new ObjectRenderer(gl,projectionMatrix);
        terrainRenderer = new TerrainRenderer(gl,projectionMatrix);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        gl.glClearColor(1f, 1f, 1f, 1f);

        objectRenderer.render(gl);
        terrainRenderer.render(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    private void getProjectionMatrix(){
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

        projectionMatrix = new Matrix4f((pMatrix));
    }

    public void cleanup(){
        simulator.cleanup();
    }

}
