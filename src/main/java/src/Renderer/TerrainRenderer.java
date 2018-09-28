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

        for(Instance asset : GS.getTerrain()){
            terrainShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            asset.getModel().getTextureImg().bindTexture(gl);
            asset.draw(gl, terrainShader);
        }

        terrainShader.stop(gl);
    }
}
