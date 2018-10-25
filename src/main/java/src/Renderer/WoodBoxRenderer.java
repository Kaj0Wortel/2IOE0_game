package src.Renderer;

import com.jogamp.opengl.GL3;
import src.Assets.instance.Car;
import src.Assets.instance.WoodBox;
import src.GS;
import src.Shaders.WoodBoxShader;

public class WoodBoxRenderer {

    private WoodBoxShader defaultShader;

    public WoodBoxRenderer(GL3 gl){
        defaultShader = new WoodBoxShader(gl);
    }

    public void render(GL3 gl, Car player) {
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl, GS.getCam(player).getProjectionMatrix());
        defaultShader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCam(player).getPosition());
        defaultShader.loadTextures(gl);


        for(WoodBox box : GS.getBoxes()) {
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, box.getModel().getTextureImg().getTexture());
            gl.glEnable(GL3.GL_TEXTURE_2D);
            box.draw(gl, defaultShader);
            gl.glDisable(GL3.GL_TEXTURE_2D);
        }

        defaultShader.stop(gl);
    }
}
