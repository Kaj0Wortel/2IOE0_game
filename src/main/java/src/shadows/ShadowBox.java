package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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

        float widthNear = 2 * (float) Math.tan((float) Math.toRadians(Renderer.FOV/2f)) * Renderer.NEAR;
        float heightNear = (float) widthNear/ getAspectRatio();
        float widthFar = 2 * (float) Math.tan((float) Math.toRadians(Renderer.FOV/2f)) * Renderer.FAR;
        float heightFar = (float) widthFar / getAspectRatio();
        Vector3f UP = new Vector3f(0,1,0).mul(camera.getRotationMatrix());
        Vector3f DOWN = new Vector3f(UP).negate();
        Vector3f FORWARD = new Vector3f(0,0,-1).mul(camera.getRotationMatrix());
        Vector3f RIGHT = new Vector3f(1,0,0).mul(camera.getRotationMatrix());
        Vector3f LEFT = new Vector3f(RIGHT).negate();
        Vector3f centerNear = new Vector3f(light.getPosition()).add(new Vector3f(FORWARD).mul(Renderer.NEAR));
        Vector3f centerFar = new Vector3f(light.getPosition()).add(new Vector3f(FORWARD).mul(Renderer.FAR));

        Vector3f FARTOP = new Vector3f(centerFar).add(new Vector3f(UP).mul(heightFar/2));
        Vector3f FARBOTTOM = new Vector3f(centerFar).add(new Vector3f(DOWN).mul(heightFar/2));
        Vector3f NEARTOP = new Vector3f(centerNear).add(new Vector3f(UP).mul(heightNear/2));
        Vector3f NEARBOTTOM = new Vector3f(centerNear).add(new Vector3f(DOWN).mul(heightNear/2));

        vertices[0] = calculateCorner(LEFT, FARTOP, widthFar);
        vertices[1] = calculateCorner(RIGHT, FARTOP, widthFar);
        vertices[2] = calculateCorner(LEFT, FARBOTTOM, widthFar);
        vertices[3] = calculateCorner(RIGHT, FARBOTTOM, widthFar);

        vertices[4] = calculateCorner(LEFT, NEARTOP, widthNear);
        vertices[5] = calculateCorner(RIGHT, NEARTOP, widthNear);
        vertices[6] = calculateCorner(LEFT, NEARBOTTOM, widthNear);
        vertices[7] = calculateCorner(RIGHT, NEARBOTTOM, widthNear);

        transformToLightSpace(vertices);

        getMaxMinValues(vertices);
    }

    private Vector4f calculateCorner(Vector3f direction, Vector3f topbottom, float width){
        Vector3f result = new Vector3f(topbottom).add(new Vector3f(direction).mul(width/2));
        return new Vector4f(result,1.0f);
    }

    private float getAspectRatio(){
        return Renderer.width / Renderer.height;
    }

    private void transformToLightSpace(Vector4f[] vertices){
        Matrix4f lightViewMatrix = getLightViewRotationMatrix();

        for(Vector4f vector : vertices){
            vector.mul(lightViewMatrix);
        }

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

    private Matrix4f getLightViewRotationMatrix(){
        Vector3f direction = new Vector3f(light.getPosition()).negate().normalize();
        Matrix4f lightViewMatrix = new Matrix4f();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        return lightViewMatrix;
    }

    public Matrix4f getLightViewMatrix(){
        Vector3f direction = new Vector3f(light.getPosition()).negate().normalize();
        Matrix4f lightViewMatrix = new Matrix4f();
        float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix.translate(getCenter().negate());

        return lightViewMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix(){
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00(2f / (maxX - minX));
        projectionMatrix.m11(2f / (maxY - minY));
        projectionMatrix.m22(-2f / (maxZ - minZ));
        projectionMatrix.m33(1);
        return projectionMatrix;
    }

    private Vector3f getCenter(){
        float centerX = (maxX + minX)/2f;
        float centerY = (maxY + minY)/2f;
        float centerZ = (maxZ + minZ)/2f;
        Vector4f center = new Vector4f(centerX,centerY,centerZ,1.0f);
        center.mul(new Matrix4f(getLightViewRotationMatrix()).invert());

        return new Vector3f(center.x,center.y,center.z);
    }
}
