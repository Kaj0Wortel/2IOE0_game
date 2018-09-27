package src.shadows;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.Light;
import src.Renderer.Camera;

public class ShadowRenderer {

    Light light;
    Camera camera;

    private int NEAR;
    private int FAR;

    public ShadowRenderer(GL3 gl, int NEAR, int FAR){
        this.NEAR = NEAR;
        this.FAR = FAR;

    }

    private Matrix4f getMVP(){
        return new Matrix4f();
    }

    private Matrix4f projectionMatrix(){
        return new Matrix4f().ortho(-10,10,-10,10, NEAR, FAR);
    }

}
