package src.shadows;

import src.Shaders.ShadowShader;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.GS;

public class ShadowRenderer {

    final private static int SHADOW_MAP_SIZE = 4096;

    private ShadowShader shadowShader;
    public FrustrumBox frustrumBox;
    private ShadowFBO shadowFBO;
    
    
    public ShadowRenderer(GL3 gl, Car player, float fov, float near, float far,
            float width, float height) {
        shadowShader = new ShadowShader(gl);
        shadowFBO = new ShadowFBO(gl, player, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
        frustrumBox = new FrustrumBox(player, fov, near, far, width, height);
    }
    
    
    public void render(GL3 gl, Car player) {
        frustrumBox.update(player);
        shadowFBO.bindFrameBuffer(gl);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        shadowShader.start(gl);

        gl.glEnable(GL3.GL_DEPTH_TEST);

        shadowShader.loadProjectionMatrix(gl, frustrumBox.getOrthographicProjectionMatrix());
        shadowShader.loadViewMatrix(gl, frustrumBox.getLightViewMatrix());

        for(Instance asset : GS.getAssets()) {
            asset.draw(gl, shadowShader);
        }

        for(Instance item : GS.getItems()) {
            item.draw(gl, shadowShader);
        }

        for(Instance asset : GS.getMaterialAssets()) {
            asset.draw(gl, shadowShader);
        }

        for(Instance asset : GS.getTerrain()){
            asset.draw(gl, shadowShader);
        }

        GS.getTrack().draw(gl, shadowShader);
        player.draw(gl, shadowShader);

        shadowShader.stop(gl);
        shadowFBO.unbindFrameBuffer(gl, player);
    }

    public int getDepthTexture() {
        return shadowFBO.getDepthAttachment().get(0);
    }

    public Matrix4f getShadowMatrix() {
        return new Matrix4f()
                .mul(offsetMatrox())
                .mul(frustrumBox.getOrthographicProjectionMatrix())
                .mul(frustrumBox.getLightViewMatrix());
    }

    private Matrix4f offsetMatrox() {
        return new Matrix4f()
                .translate(new Vector3f(0.5f, 0.5f, 0.5f))
                .scale(new Vector3f(0.5f, 0.5f, 0.5f));
    }

    public FrustrumBox getFrustrumBox() {
        return frustrumBox;
    }
    
    
}
