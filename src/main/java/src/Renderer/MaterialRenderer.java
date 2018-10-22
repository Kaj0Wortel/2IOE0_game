package src.Renderer;

import com.jogamp.opengl.GL3;
import src.Assets.instance.Car;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.MaterialShader;

public class MaterialRenderer {

    private MaterialShader defaultShader;

    public MaterialRenderer(GL3 gl) {
        defaultShader = new MaterialShader(gl);
    }

    public void render(GL3 gl, Car player) {
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,GS.getCam(player).getProjectionMatrix());
        defaultShader.loadViewMatrix(gl, GS.getCam(player).getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCam(player).getPosition());

        for(Instance asset : GS.getMaterialAssets()){
            asset.draw(gl, defaultShader);
        }

        defaultShader.stop(gl);
    }
}
