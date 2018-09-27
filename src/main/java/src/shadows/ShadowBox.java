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
    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;

    public ShadowBox(Camera camera, Light light){
        this.camera = camera;
        this.light = light;
    }

    public void updateBoundingBox(){
        Vector4f[] vertices = new Vector4f[8];

        float HALF_WIDTH_NEAR_PLANE = (float) Math.tan((float) Math.toRadians(Renderer.FOV/2f)) * Renderer.NEAR;
        float HALF_HEIGHT_NEAR_PLANE = (float) HALF_WIDTH_NEAR_PLANE / getAspectRatio();

        vertices[0] = new Vector4f(-HALF_WIDTH_NEAR_PLANE, -HALF_HEIGHT_NEAR_PLANE, Renderer.NEAR,1);
        vertices[1] = new Vector4f(HALF_WIDTH_NEAR_PLANE, -HALF_HEIGHT_NEAR_PLANE, Renderer.NEAR,1);
        vertices[2] = new Vector4f(-HALF_WIDTH_NEAR_PLANE, HALF_HEIGHT_NEAR_PLANE, Renderer.NEAR,1);
        vertices[3] = new Vector4f(HALF_WIDTH_NEAR_PLANE, HALF_HEIGHT_NEAR_PLANE, Renderer.NEAR, 1);

        float HALF_WIDTH_FAR_PLANE = (float) Math.tan((float) Math.toRadians(Renderer.FOV/2f)) * Renderer.FAR;
        float HALF_HEIGHT_FAR_PLANE = (float) HALF_WIDTH_FAR_PLANE / getAspectRatio();

        vertices[4] = new Vector4f(-HALF_WIDTH_FAR_PLANE,-HALF_HEIGHT_FAR_PLANE, Renderer.FAR,1);
        vertices[5] = new Vector4f(HALF_WIDTH_FAR_PLANE, -HALF_HEIGHT_FAR_PLANE, Renderer.FAR,1);
        vertices[6] = new Vector4f(-HALF_WIDTH_FAR_PLANE, HALF_HEIGHT_FAR_PLANE, Renderer.FAR,1);
        vertices[7] = new Vector4f(HALF_WIDTH_FAR_PLANE, HALF_HEIGHT_FAR_PLANE, Renderer.FAR, 1);

        transformToWorldSpace(vertices);
        transformToEyeSpace(vertices);

        getMaxMinValues(vertices);
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
        for(Vector4f vector : vertices){
            vector.mul(lightMatrix);
        }
    }

    public Matrix4f getLightViewMatrix(){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setLookAt(light.getPosition(),new Vector3f(light.getPosition()).negate(), new Vector3f(0,1,0));
        return viewMatrix;
    }

    private void getMaxMinValues(Vector4f[] vertices){
        boolean f = true;
        for(Vector4f vector : vertices){
            if(f){
                minX = vector.x;
                maxX = vector.x;
                minY = vector.y;
                maxY = vector.y;
                minZ = vector.z;
                maxZ = vector.z;
                f = false;
            }else{
                if(vector.x > maxX){
                    maxX = vector.x;
                }else if(vector.x < minX){
                    minX = vector.x;
                }

                if(vector.y > maxY){
                    maxY = vector.y;
                }else if(vector.y < minY){
                    minY = vector.y;
                }

                if(vector.z > maxZ){
                    maxZ = vector.z;
                }else if(vector.z < minZ){
                     minZ = vector.z;
                }
            }
        }
    }

    public Matrix4f getOrthoProjectionMatrix(){
        return new Matrix4f().setOrtho(minX,maxX,minY,maxY,maxZ,minZ);
    }
}
