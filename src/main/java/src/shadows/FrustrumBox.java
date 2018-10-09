package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.GS;

public class FrustrumBox {

    private float width;
    private float height;
    private float depth;
    public Vector3f eye;
    private Vector3f center;

    private float FOV;
    private float NEAR;
    private float FAR;
    private float windowWidth;
    private float windowHeight;

    public FrustrumBox(float FOV, float NEAR, float FAR, float windowWidth, float windowHeight){
        this.FOV = FOV;
        this.NEAR = NEAR;
        this.FAR = FAR;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        width = 0;
        height = 0;
        depth = 0;

        calculateBoundingBox();
    }


    public void calculateBoundingBox(){
        float hhn = (float) Math.tan(Math.toRadians(FOV/2) * NEAR);
        float hwn = getAspectRatio() * hhn;
        float hhf = (float) Math.tan(Math.toRadians(FOV/2) * FAR);
        float hwf = getAspectRatio() * hhf;

        Vector4f[] eyeVertices = new Vector4f[] {
        new Vector4f(-hwn,hhn,NEAR,1),
        new Vector4f(hwn,hhn,NEAR,1),
        new Vector4f(-hwn,-hhn,NEAR,1),
        new Vector4f(hwn,-hhn,NEAR,1),

        new Vector4f(-hwf,hhf,FAR,1),
        new Vector4f(hwf,hhf,FAR,1),
        new Vector4f(-hwf,-hhf,FAR,1),
        new Vector4f(hwf,-hhf,FAR,1)
        };

        transform(eyeVertices, GS.camera.getViewMatrixInverse());
        transform(eyeVertices, GS.getLights().get(0).getRotationMatrix());

        getBoundingBox(eyeVertices);

    }

    private void transform(Vector4f[] vertices, Matrix4f matrix){
        for(Vector4f v : vertices){
            v.mul(matrix);
        }
    }

    private void getBoundingBox(Vector4f[] vertices){
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;
        float minX = 0;
        float minY = 0;
        float minZ = 0;

        boolean first = true;
        for(Vector4f v : vertices){
            if(first){
                first = false;
                maxX = v.x;
                minX = v.x;
                maxY = v.y;
                minY = v.y;
                maxZ = v.z;
                minZ = v.z;
            }else{
                if(v.x > maxX){
                    maxX = v.x;
                }else if(v.x < minX){
                    minX = v.x;
                }
                if(v.y > maxY){
                    maxY = v.y;
                }else if(v.y < minY){
                    minY = v.y;
                }
                if(v.z > maxZ){
                    maxZ = v.z;
                }else if(v.z < minZ){
                    minZ = v.z;
                }
            }
        }

        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;
        Vector4f eyeInLightSpace = new Vector4f((minX + maxX)/2, (minY + maxY)/2, maxZ,1);
        eyeInLightSpace.mul(GS.getLights().get(0).getRotatinMatrixInverse());
        eye = new Vector3f(eyeInLightSpace.x,eyeInLightSpace.y,eyeInLightSpace.z);

        Vector4f centerInLightSpace = new Vector4f((minX + maxX)/2, (minY + maxY)/2, (maxZ + minZ)/2,1);
        centerInLightSpace.mul(GS.getLights().get(0).getRotatinMatrixInverse());
        center = new Vector3f(centerInLightSpace.x,centerInLightSpace.y,centerInLightSpace.z).negate();
    }

    private float getAspectRatio(){
        return windowWidth/windowHeight;
    }

    public Matrix4f getOrthographicProjectionMatrix(){
        Matrix4f ortho = new Matrix4f();
        ortho.m00(2/width);
        ortho.m11(2/height);
        ortho.m22(2/depth);

        Matrix4f trans = new Matrix4f();
        trans.translate(center);
        trans.m22(-1);

        Matrix4f result = new Matrix4f();
        return ortho.mul(trans,result);
    }

    public Matrix4f getLightViewMatrix(){
        Matrix4f lightRotationMatrix = new Matrix4f(GS.getLights().get(0).getRotationMatrix());
        lightRotationMatrix.translate(new Vector3f(-eye.x, -eye.y, -eye.z));
        return lightRotationMatrix;
    }
}
