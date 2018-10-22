package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.MaterialShader;

public class MaterialRenderer {

    private MaterialShader defaultShader;
    private Matrix4f projectionMatrix;

    public MaterialRenderer(GL3 gl, Matrix4f projectionMatrix){
        defaultShader = new MaterialShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL3 gl, Car player) {
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,projectionMatrix);
        defaultShader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCam(player).getPosition());

        for(Instance asset : GS.getMaterialAssets()){
            asset.draw(gl, defaultShader);
        }

        defaultShader.stop(gl);
    }
}
