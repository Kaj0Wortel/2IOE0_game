
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

    final static public float NEAR = 0.1f;
    final static public float FAR = 2000f;
    public static float width = 1080;
    public static float height = 720;

    private ObjectRenderer objectRenderer;
    private TerrainRenderer terrainRenderer;
    private MaterialRenderer materialRenderer;
    private ItemRenderer itemRenderer;
    private WoodBoxRenderer woodBoxRenderer;
    private Map<Car, ShadowRenderer> shadowRenderers = new ConcurrentHashMap<>();
    private Map<Car, GUIRenderer> guiRenderers = new ConcurrentHashMap<>();

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

            objectRenderer = new ObjectRenderer(gl);
            materialRenderer = new MaterialRenderer(gl);
            terrainRenderer = new TerrainRenderer(gl);
            itemRenderer = new ItemRenderer(gl);
            woodBoxRenderer = new WoodBoxRenderer(gl);
            RacetrackShader racetrackShader = new RacetrackShader(gl);
                GS.getTrack().setShader(racetrackShader);
            for (Car player : GS.getPlayers()) {
                Camera cam = GS.getCam(player);
                cam.calcProjectionMatrix();
                guiRenderers.put(player, new GUIRenderer(gl, player));
                ShadowRenderer sr = new ShadowRenderer(gl, player,
                        NEAR, FAR, width, height);
                shadowRenderers.put(player, sr);
                GS.getTrack().setShadowMap(sr.getDepthTexture());
                player.setShadowMap(sr.getDepthTexture());
                player.setCarShaderVariables(new CarShader(gl));
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
            int partWidth = (GS.amtOfPlayers <= 1
                    ? GS.canvas.getWidth()
                    : GS.canvas.getWidth() / 2);
            int partHeight = (GS.amtOfPlayers <= 2
                    ? GS.canvas.getHeight()
                    : GS.canvas.getHeight() / 2);
            int playerCounter = 0;
            for (Car player : GS.getPlayers()) {
                Camera cam = GS.getCam(player);
                int xPos = (playerCounter % 2) * partWidth;
                int yPos = (GS.amtOfPlayers <= 2
                        ? 0
                        : Math.abs(1 - playerCounter / 2) * partHeight);
                gl.glViewport(xPos, yPos, partWidth, partHeight);
                gl.glScissor(xPos, yPos, partWidth, partHeight);

                ShadowRenderer sr = shadowRenderers.get(player);
                gl.glDisable(GL3.GL_SCISSOR_TEST);
                sr.render(gl, player);
                gl.glEnable(GL3.GL_SCISSOR_TEST);
                player.setShadowMap(sr.getDepthTexture());
                GS.getTrack().setShadowMap(sr.getDepthTexture());

                gl.glClear(GL3.GL_DEPTH_BUFFER_BIT | GL3.GL_COLOR_BUFFER_BIT);
                gl.glClearColor(0f, 0f, 1f, 1f);

                gl.glEnable(GL3.GL_CULL_FACE);
                gl.glCullFace(GL3.GL_BACK);

                //if (GS.getCam(player).hasFOVChange()) {
                    cam.calcProjectionMatrix();
                    GS.getCam(player).resetFOVChange();
                //}

                objectRenderer.render(gl, player);
                materialRenderer.render(gl, player);
                terrainRenderer.render(gl, player);
                itemRenderer.render(gl, player);
                woodBoxRenderer.render(gl, player);
                
                Matrix4f shadowMatrix = sr.getShadowMatrix();
                
                for (Car p : GS.getPlayers()) {
                    p.draw(gl, player, shadowMatrix);
                }

                gl.glDisable(GL3.GL_CULL_FACE);
                GS.getTrack().draw(gl, player, shadowMatrix);
                GS.getSkybox().draw(gl, player);
                guiRenderers.get(player).render(gl, player);

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

    public void cleanup(){
        simulator.cleanup();
    }

}
