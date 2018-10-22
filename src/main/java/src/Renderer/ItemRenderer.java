package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.ItemShader;

public class ItemRenderer {

    private ItemShader defaultShader;

    public ItemRenderer(GL3 gl){
        defaultShader = new ItemShader(gl);
    }

    public void render(GL3 gl, Car player) {
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl, GS.getCam(player).getProjectionMatrix());
        defaultShader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCam(player).getPosition());
        defaultShader.loadTextures(gl);


        for(Instance asset : GS.getItems()){
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, asset.getModel().getTextureImg().getTexture());
            gl.glEnable(GL3.GL_TEXTURE_2D);
            asset.draw(gl, defaultShader);
            gl.glDisable(GL3.GL_TEXTURE_2D);
        }

        defaultShader.stop(gl);
    }
}
