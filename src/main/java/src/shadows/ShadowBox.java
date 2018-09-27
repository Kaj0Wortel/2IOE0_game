package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.Assets.Light;
import src.Renderer.Camera;
import src.Renderer.Renderer;

public class ShadowBox {

    private Camera camera;
    private Light light;

    public ShadowBox(Camera camera, Light light){
        this.camera = camera;
        this.light = light;
    }

    private Vector4f[] getFrustrumVertices(){
        Vector4f[] vertices = new Vector4f[8];

        float HALF_WIDTH_NEAR_PLANE = (float) Math.tan((float) Math.toRadians(Renderer.FOV) * Renderer.NEAR);
        float HALF_HEIGHT_NEAR_PLANE = (float) HALF_WIDTH_NEAR_PLANE / getAspectRatio();

        vertices[0] = new Vector4f(-HALF_HEIGHT_NEAR_PLANE,0,-HALF_WIDTH_NEAR_PLANE,1);
        vertices[1] = new Vector4f(HALF_HEIGHT_NEAR_PLANE, 0, -HALF_WIDTH_NEAR_PLANE,1);
        vertices[2] = new Vector4f(-HALF_HEIGHT_NEAR_PLANE, 0, HALF_WIDTH_NEAR_PLANE,1);
        vertices[3] = new Vector4f(HALF_HEIGHT_NEAR_PLANE, 0, HALF_WIDTH_NEAR_PLANE, 1);

        float HALF_WIDTH_FAR_PLANE = (float) Math.tan((float) Math.toRadians(Renderer.FOV) * Renderer.NEAR);
        float HALF_HEIGHT_FAR_PLANE = (float) HALF_WIDTH_FAR_PLANE / getAspectRatio();

        vertices[4] = new Vector4f(-HALF_HEIGHT_FAR_PLANE,0,-HALF_WIDTH_FAR_PLANE,1);
        vertices[5] = new Vector4f(HALF_HEIGHT_FAR_PLANE, 0, -HALF_WIDTH_FAR_PLANE,1);
        vertices[6] = new Vector4f(-HALF_HEIGHT_FAR_PLANE, 0, HALF_WIDTH_FAR_PLANE,1);
        vertices[7] = new Vector4f(HALF_HEIGHT_FAR_PLANE, 0, HALF_WIDTH_FAR_PLANE, 1);

        transformToWorldSpace(vertices);
        transformToEyeSpace(vertices);

    }

    private float getAspectRatio(){
        return Renderer.width / Renderer.height;
    }

    private void transformToWorldSpace(Vector4f[] vertices){
        Matrix4f invertedCameraMatrix = camera.getInverseViewMatrix();
        for(Vector4f vector : vertices){
            vector.mul(invertedCameraMatrix);
        }
    }

    private void transformToEyeSpace(Vector4f[] vertices){
        Matrix4f lightMatrix = getLightViewMatrix();
    }

    private Matrix4f getLightViewMatrix(){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.lookAt(light.getPosition(),new Vector3f(light.getPosition()).negate(), new Vector3f(0,1,0));
        return viewMatrix;
    }
}
