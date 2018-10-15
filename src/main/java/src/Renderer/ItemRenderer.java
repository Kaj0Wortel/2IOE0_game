package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.ItemShader;

public class ItemRenderer {

    private ItemShader defaultShader;
    private Matrix4f projectionMatrix;

    public ItemRenderer(GL3 gl, Matrix4f projectionMatrix){
        defaultShader = new ItemShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL3 gl){
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,projectionMatrix);
        defaultShader.loadViewMatrix(gl, GS.camera.getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.camera.getPosition());
        defaultShader.loadTextures(gl);


        for(Instance asset : GS.getItems()){
            gl.glActiveTexture(gl.GL_TEXTURE0);
            gl.glBindTexture(gl.GL_TEXTURE_2D, asset.getModel().getTextureImg().getTexture());
            asset.draw(gl, defaultShader);
        }

        defaultShader.stop(gl);
    }
}
