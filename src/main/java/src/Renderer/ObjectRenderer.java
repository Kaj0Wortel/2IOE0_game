package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.Instance;
import src.GS;
import src.Shaders.DefaultShader;

import java.nio.IntBuffer;

public class ObjectRenderer {

    private DefaultShader defaultShader;
    private Matrix4f projectionMatrix;

    public ObjectRenderer(GL3 gl, Matrix4f projectionMatrix){
        defaultShader = new DefaultShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL3 gl, IntBuffer shadowMap){
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,projectionMatrix);
        defaultShader.loadViewMatrix(gl, GS.getCamera().getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCamera().getPosition());
        defaultShader.loadTextures(gl);
        gl.glActiveTexture(gl.GL_TEXTURE1);
        gl.glBindTexture(gl.GL_TEXTURE_2D, shadowMap.get(0));
        for(Instance asset : GS.getAssets()){
            asset.draw(gl, defaultShader);
        }

        defaultShader.stop(gl);
    }
}
