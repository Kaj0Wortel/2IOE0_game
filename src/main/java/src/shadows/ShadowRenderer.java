package src.shadows;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.Instance;
import src.GS;

import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;

public class ShadowRenderer {

    private static final int SHADOW_MAP_SIZE = 2048;
    private ShadowBoxTest shadowBox;
    private ShadowBuffer shadowBuffer;
    private ShadowShader shadowShader;

    private Matrix4f lightViewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    public ShadowRenderer(GL3 gl){
        shadowBox = new ShadowBoxTest(lightViewMatrix, GS.getCamera());
        shadowBuffer = new ShadowBuffer(gl, SHADOW_MAP_SIZE,SHADOW_MAP_SIZE);
        shadowShader = new ShadowShader(gl);
    }

    public void calculateMatrices(){
        getOrthoProjectionMatrix(shadowBox.getWidth(),shadowBox.getHeight(), shadowBox.getLength());
        getLightViewMatrix(new Vector3f(GS.getLights().get(0).getPosition()).negate(), shadowBox.getCenter());
    }

    public void render(GL3 gl){
        shadowBox.update();
        calculateMatrices();

        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        shadowBuffer.bindFrameBuffer(gl);
        shadowShader.start(gl);

        shadowShader.loadProjectionMatrix(gl,projectionMatrix);
        shadowShader.loadViewMatrix(gl, lightViewMatrix);

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

    public void getLightViewMatrix(Vector3f direction, Vector3f shadowBoxCenter){
        direction.normalize();
        shadowBoxCenter.negate();
        lightViewMatrix.identity();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix.translate(shadowBoxCenter);
    }

    public void getOrthoProjectionMatrix(float dx, float dy, float dz){
        projectionMatrix.identity();
        projectionMatrix.m00(2f / dx);
        projectionMatrix.m11(2f / dy);
        projectionMatrix.m22(-2f / dz);
        projectionMatrix.m33(1);
    }

}
