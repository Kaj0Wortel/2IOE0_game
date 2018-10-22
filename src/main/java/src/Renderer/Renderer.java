
package src.Renderer;


// Jogamp imports

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import org.joml.Matrix4f;
import src.GS;
import src.Shaders.CarShader;
import src.Shaders.RacetrackShader;
import src.Simulator;
import src.shadows.ShadowRenderer;

import static com.jogamp.opengl.GL2ES2.GL_SHADING_LANGUAGE_VERSION;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import src.Assets.instance.Car;
import src.tools.log.Logger;

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
    private Map<Car, ShadowRenderer> shadowRenderers = new ConcurrentHashMap<>();

    public Renderer(Simulator simulator, float width, float height) {
        this.simulator = simulator;
        this.width = width;
        this.height = height;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void init(GLAutoDrawable glAutoDrawable) {
        try {
            this.gl = glAutoDrawable.getGL().getGL3();
            this.glu = new GLU();

            gl.glViewport(0, 0, GS.WIDTH, GS.HEIGHT);

            System.out.println(gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

            simulator.setGL(gl);
            simulator.initAssets();

            getProjectionMatrix();
            objectRenderer = new ObjectRenderer(gl,projectionMatrix);
            materialRenderer = new MaterialRenderer(gl, projectionMatrix);
            terrainRenderer = new TerrainRenderer(gl, projectionMatrix);
            itemRenderer = new ItemRenderer(gl, projectionMatrix);
            guiRenderer = new GUIRenderer(gl);
            RacetrackShader racetrackShader = new RacetrackShader(gl);

            Logger.write(GS.getPlayers());
            if (GS.getPlayers().isEmpty()) System.out.println("NO PLAYERS!");
            for (Car player : GS.getPlayers()) {
                ShadowRenderer sr = new ShadowRenderer(gl, player, fov, NEAR, FAR, width, height);
                shadowRenderers.put(player, sr);
                GS.getTrack().setShadowMap(sr.getDepthTexture());
                player.setShadowMap(sr.getDepthTexture());
                player.setCarShaderVariables(new CarShader(gl), projectionMatrix);

                GS.getTrack().setShaderAndRenderMatrices(racetrackShader,
                        projectionMatrix, GS.getCam(player).getViewMatrix());
            }

            gl.glEnable(GL3.GL_DEPTH_TEST);
            gl.glEnable(GL3.GL_SCISSOR_TEST);
            
        } catch (Exception e) {
            Logger.write(e);
            System.exit(-1);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void display(GLAutoDrawable glAutoDrawable) {
        try {
            if (GS.getPlayers().isEmpty()) return;
            int partWidth = GS.canvas.getWidth() / GS.getNumPlayers();
            int playerCounter = 0;
            for (Car player : GS.getPlayers()) {
                gl.glViewport(0, 0, partWidth, GS.canvas.getHeight());
                gl.glScissor(playerCounter * partWidth, 0,
                        partWidth, GS.canvas.getHeight());

                shadowRenderers.get(player).render(gl, player);

                gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
                gl.glClearColor(0f, 0f, 1f, 1f);

                gl.glEnable(GL3.GL_CULL_FACE);
                gl.glCullFace(GL3.GL_BACK);

                if (fovChange){
                    getProjectionMatrix();
                    fovChange = false;
                }

                objectRenderer.render(gl, player);
                materialRenderer.render(gl, player);
                terrainRenderer.render(gl, player);
                itemRenderer.render(gl, player);
                
                for (Car p : GS.getPlayers()) {
                    p.draw(gl, player, shadowRenderers.get(player).getShadowMatrix());
                }

                gl.glDisable(GL3.GL_CULL_FACE);
                GS.getTrack().draw(gl, player, shadowRenderers.get(player).getShadowMatrix());
                GS.getSkybox().draw(gl, player, projectionMatrix);
                guiRenderer.render(gl, player);

                playerCounter++;
            }
            
            gl.glViewport(0, 0, GS.canvas.getWidth(), GS.canvas.getHeight());
            
        } catch (Exception e) {
            Logger.write(e);
            System.exit(-1);
        }
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
        fov = 70 + fovOffset/1.3f;
        fovChange = true;
        //System.out.println("fov Change " + fov);
    }

}
