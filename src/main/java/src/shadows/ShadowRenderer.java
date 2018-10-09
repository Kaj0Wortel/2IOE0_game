package src.shadows;

import com.jogamp.opengl.GL3;
import src.Assets.instance.Instance;
import src.GS;

public class ShadowRenderer {

    private static final int shadowMapSize = 2048;

    private ShadowShader shadowShader;
    public FrustrumBox frustrumBox;
    private ShadowFBO shadowFBO;

    public ShadowRenderer(GL3 gl, float FOV, float NEAR, float FAR, float width, float height){
        shadowShader = new ShadowShader(gl);
        shadowFBO = new ShadowFBO(gl, shadowMapSize, shadowMapSize);
        frustrumBox = new FrustrumBox(FOV, NEAR, FAR, width, height);

    }

    public void render(GL3 gl){
        gl.glClearColor(0,0,0,0);
        gl.glEnable(gl.GL_DEPTH_TEST);
        frustrumBox.calculateBoundingBox();
        shadowFBO.bindFrameBuffer(gl);
        shadowShader.start(gl);

        shadowShader.loadProjectionMatrix(gl,frustrumBox.getOrthographicProjectionMatrix());
        shadowShader.loadViewMatrix(gl, frustrumBox.getLightViewMatrix());

        for(Instance asset : GS.getAssets()){
            asset.draw(gl, shadowShader);
        }

        for(Instance asset : GS.getMaterialAssets()){
            asset.draw(gl, shadowShader);
        }

        //GS.getTrack().draw(gl);

        shadowShader.stop(gl);
        shadowFBO.unbindFrameBuffer(gl);
    }

    public int getDepthTexture(){
        return shadowFBO.getDepthAttachment().get(0);
    }
}
