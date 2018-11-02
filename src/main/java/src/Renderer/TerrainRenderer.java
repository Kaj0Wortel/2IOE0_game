package src.Renderer;

import com.jogamp.opengl.GL3;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.TerrainShader;

public class TerrainRenderer
        implements RendererInterface{

    private TerrainShader terrainShader;

    public TerrainRenderer(GL3 gl) {
        this.terrainShader = new TerrainShader(gl);
    }

    @Override
    public void render(GL3 gl, Car player) {
        terrainShader.start(gl);

        terrainShader.loadProjectionMatrix(gl, GS.getCam(player).getProjectionMatrix());
        terrainShader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        terrainShader.loadLight(gl,GS.getLights().get(0));
        terrainShader.loadCameraPos(gl, GS.getCam(player).getPosition());
        terrainShader.loadTextures(gl);

        for(Instance asset : GS.getTerrain()){
            terrainShader.loadTextureLightValues(gl,
                    asset.getModel().getTextureImg().getShininess(),
                    asset.getModel().getTextureImg().getReflectivity());
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D,
                    asset.getModel().getTextureImg().getTexture());
            asset.draw(gl, terrainShader);
            gl.glDisable(GL3.GL_TEXTURE_2D);
        }

        terrainShader.stop(gl);
    }
}
