package src.Renderer;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.instance.Instance;
import src.GS;
import src.Shaders.DefaultShader;

public class ObjectRenderer {

    private DefaultShader defaultShader;
    private Matrix4f projectionMatrix;

    public ObjectRenderer(GL3 gl, Matrix4f projectionMatrix){
        defaultShader = new DefaultShader(gl);
        this.projectionMatrix = projectionMatrix;
    }

    public void render(GL3 gl){
        defaultShader.start(gl);

        defaultShader.loadProjectionMatrix(gl,projectionMatrix);
        defaultShader.loadViewMatrix(gl, GS.camera.getViewMatrix());
        defaultShader.loadLight(gl,GS.getLights().get(0));
        defaultShader.loadCameraPos(gl, GS.camera.getPosition());

        for(Instance asset : GS.getAssets()){
            asset.draw(gl, defaultShader);
        }

        GS.testRaceTrack.draw(gl, defaultShader);

        defaultShader.stop(gl);
    }
}
