
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
import src.GS;
import src.Shaders.DefaultShader;
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


    private DefaultShader currentShader;
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
        gl.glEnable(gl.GL_NORMALIZE);

        light = new Light(new Vector3f(0f,20f,10f), new Vector3f(1f,1f,1f));

        System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

        simulator.setGL(gl);
        simulator.initAssets();
        currentShader = new DefaultShader(gl);
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

        currentShader.loadProjectionMatrix(gl,getProjectionMatrix());
        currentShader.loadViewMatrix(gl,GS.getCamera().getViewMatrix());
        currentShader.loadLight(gl,light);
        currentShader.loadTime(gl, counter);


        for(Instance asset : GS.getAssets()){
            asset.draw(gl, currentShader);
        }
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

}
