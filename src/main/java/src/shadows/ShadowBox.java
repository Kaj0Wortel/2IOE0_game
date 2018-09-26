package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import src.Renderer.Camera;

public class ShadowBox {

    private Camera camera;
    private Matrix4f lightViewMatrix;

    public ShadowBox(Camera camera, Matrix4f lightViewMatrix){
        this.camera = camera;
        this.lightViewMatrix = lightViewMatrix;
    }

    public Vector4f[] getFrustrumVertices(){

    }
}
