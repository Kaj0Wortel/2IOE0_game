package src.Renderer;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import src.Assets.Instance;
import src.GS;
import src.Shaders.DefaultShader;

public class ObjectRenderer {

    private DefaultShader defaultShader;
    private Matrix4f projectionMatrix;

    public ObjectRenderer(GL2 gl, Matrix4f projectionMatrix){
        defaultShader = new DefaultShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL2 gl){
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,projectionMatrix);
        defaultShader.loadViewMatrix(gl, GS.getCamera().getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.getCamera().getPosition());

        for(Instance asset : GS.getAssets()){
            defaultShader.loadTextureLightValues(gl, asset.getModel().getTextureImg().getShininess(), asset.getModel().getTextureImg().getReflectivity());
            asset.draw(gl, defaultShader);
        }

        defaultShader.stop(gl);
    }
}
