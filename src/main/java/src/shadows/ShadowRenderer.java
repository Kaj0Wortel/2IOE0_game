package src.shadows;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.Renderer.Camera;

public class ShadowRenderer {

    Light light;
    Camera camera;


    public ShadowRenderer(GL3 gl){

    }

    private Matrix4f getMVP(){
        return new Matrix4f();
    }

    private Matrix4f viewMatrix(){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(light.getPosition(),new Vector3f(light.getPosition()).negate(), new Vector3f(0,1,0));
        return viewMatrix;
    }

    private Matrix4f projectionMatrix(){
        return new Matrix4f();
    }

}
