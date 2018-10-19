package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.TerrainShader;

public class TerrainRenderer {

    private TerrainShader terrainShader;
    private Matrix4f projectionMatrix;

    public TerrainRenderer(GL3 gl, Matrix4f projectionMatrix){
        this.terrainShader = new TerrainShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL3 gl){
        terrainShader.start(gl);

        terrainShader.loadProjectionMatrix(gl,projectionMatrix);
        terrainShader.loadViewMatrix(gl, GS.camera.getViewMatrix());
        terrainShader.loadLight(gl,GS.getLights().get(0));
        terrainShader.loadCameraPos(gl, GS.camera.getPosition());
        terrainShader.loadTexture(gl);

        for(Instance asset : GS.getTerrain()){
            terrainShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            gl.glActiveTexture(GL3.GL_TEXTURE0);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, asset.getModel().getTextureImg().getTexture());
            asset.draw(gl, terrainShader);
            gl.glDisable(GL3.GL_TEXTURE_2D);
        }

        terrainShader.stop(gl);
    }
}
