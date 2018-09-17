
package src.Renderer;


// Jogamp imports

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import org.joml.Matrix4f;
import src.Assets.Instance;
import src.GS;
import src.Shaders.DefaultShader;
import src.Simulator;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_SHADING_LANGUAGE_VERSION;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;

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

    public Renderer(Simulator simulator, float width, float height){
        this.simulator = simulator;
        this.width = width;
        this.height = height;

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        this.gl = glAutoDrawable.getGL().getGL2();
        this.glu = new GLU();

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        gl.glEnable(GL_NORMALIZE);

        System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

        simulator.setGL(gl);
        simulator.initAssets();
        currentShader = new DefaultShader(gl);
        
        // tmp
        //LoadOBJ.load(GS.OBJ_DIR + "test.obj").bind(gl);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        gl.glClearColor(1f, 1f, 1f, 1f);
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        gl.glClearColor(1f, 1f, 1f, 1f);

        currentShader.start(gl);
        currentShader.loadProjectionMatrix(gl,getProjectionMatrix());
        currentShader.loadViewMatrix(gl,GS.getCamera().getViewMatrix());

        for(Instance asset : GS.getAssets()){
            asset.draw(gl, currentShader);
        }




        // tmp
        //LoadOBJ.load(GS.OBJ_DIR + "test.obj").draw(gl, glu);
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
