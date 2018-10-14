package src.shadows;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.instance.Instance;
import src.GS;

public class ShadowRenderer {

    private static final int shadowMapSize = 4096;

    private ShadowShader shadowShader;
    public FrustrumBox frustrumBox;
    private ShadowFBO shadowFBO;

    public ShadowRenderer(GL3 gl, float FOV, float NEAR, float FAR, float width, float height){
        shadowShader = new ShadowShader(gl);
        shadowFBO = new ShadowFBO(gl, shadowMapSize, shadowMapSize);
        frustrumBox = new FrustrumBox(FOV, NEAR, FAR, width, height);

    }

    public void render(GL3 gl){
        frustrumBox.calculateBoundingBox2();
        shadowFBO.bindFrameBuffer(gl);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        shadowShader.start(gl);

        gl.glEnable(gl.GL_DEPTH_TEST);

        shadowShader.loadProjectionMatrix(gl,frustrumBox.getOrthographicProjectionMatrix());
        shadowShader.loadViewMatrix(gl, frustrumBox.getLightViewMatrix());

        for(Instance asset : GS.getAssets()){
            asset.draw(gl, shadowShader);
        }

        for(Instance item : GS.getItems()){
            item.draw(gl, shadowShader);
        }

        for(Instance asset : GS.getMaterialAssets()){
            asset.draw(gl, shadowShader);
        }

        GS.getTrack().draw(gl, shadowShader);

        shadowShader.stop(gl);
        shadowFBO.unbindFrameBuffer(gl);
    }

    public int getDepthTexture(){
        return shadowFBO.getDepthAttachment().get(0);
    }

    public Matrix4f getShadowMatrix(){
        Matrix4f shadowMatrix = new Matrix4f();
        shadowMatrix.mul(offsetMatrox());
        shadowMatrix.mul(frustrumBox.getOrthographicProjectionMatrix());
        shadowMatrix.mul(frustrumBox.getLightViewMatrix());
        return shadowMatrix;
    }

    private Matrix4f offsetMatrox(){
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f,0.5f,0.5f));
        offset.scale(new Vector3f(0.5f,0.5f,0.5f));
        return offset;
    }
}
