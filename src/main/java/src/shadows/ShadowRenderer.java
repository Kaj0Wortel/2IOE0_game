package src.shadows;

import com.jogamp.opengl.GL3;
import src.Assets.Instance;
import src.GS;

import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;

public class ShadowRenderer {

    private static final int SHADOW_MAP_SIZE = 2048;
    private ShadowBox shadowBox;
    private ShadowBuffer shadowBuffer;
    private ShadowShader shadowShader;

    public ShadowRenderer(GL3 gl){
        shadowBox = new ShadowBox(GS.getCamera(),GS.getLights().get(0));
        shadowBuffer = new ShadowBuffer(gl, SHADOW_MAP_SIZE,SHADOW_MAP_SIZE);
        shadowShader = new ShadowShader(gl);
    }

    public void update(){
        shadowBox.updateBoundingBox();
    }

    public void render(GL3 gl){
        shadowBox.updateBoundingBox();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(1f, 1f, 1f, 1f);

        shadowBuffer.bindFrameBuffer(gl);
        shadowShader.start(gl);

        shadowShader.loadProjectionMatrix(gl,shadowBox.getOrthoProjectionMatrix());
        shadowShader.loadViewMatrix(gl, shadowBox.getLightViewMatrix());

        for(Instance asset : GS.getAssets()){
            asset.draw(gl, shadowShader);
        }

        for(Instance asset : GS.getTerrain()){
            asset.draw(gl, shadowShader);
        }

        shadowShader.stop(gl);

        shadowBuffer.unBindFrameBuffer(gl);
    }

    public ShadowBuffer getShadowBuffer(){
        return shadowBuffer;
    }
}
