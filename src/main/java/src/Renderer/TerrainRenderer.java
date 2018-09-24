package src.Renderer;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import src.Assets.Instance;
import src.GS;
import src.Shaders.TerrainShader;

public class TerrainRenderer {

    private TerrainShader terrainShader;
    private Matrix4f projectionMatrix;

    public TerrainRenderer(GL2 gl, Matrix4f projectionMatrix){
        this.terrainShader = new TerrainShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL2 gl){
        terrainShader.start(gl);

        terrainShader.loadProjectionMatrix(gl,projectionMatrix);
        terrainShader.loadViewMatrix(gl, GS.getCamera().getViewMatrix());
        terrainShader.loadLight(gl,GS.getLights().get(0));
        terrainShader.loadCameraPos(gl, GS.getCamera().getPosition());

        for(Instance asset : GS.getTerrain()){
            terrainShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            asset.getModel().getTextureImg().bindTexture(gl);
            asset.draw(gl, terrainShader);
        }

        terrainShader.stop(gl);
    }
}
