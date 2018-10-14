package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.GS;

public class FrustrumBox {

    private float width;
    private float height;
    private float depth;
    private Vector3f center = new Vector3f();

    private float FOV;
    private float NEAR;
    private float FAR;
    private float windowWidth;
    private float windowHeight;

    private float SHADOW_DISTANCE = 300;
    private float nearHeight, nearWidth, farHeight, farWidth;

    public FrustrumBox(float FOV, float NEAR, float FAR, float windowWidth, float windowHeight){
        this.FOV = FOV;
        this.NEAR = NEAR;
        this.FAR = FAR;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        width = 0;
        height = 0;
        depth = 0;

        calculateBoundingBox2();
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

    public void calculateBoundingBox2(){
        calculateWidthsAndHeights();
        Matrix4f rotation = GS.camera.getRotatonMatrix();

        Vector4f UP = new Vector4f(0,1,0,1);
        Vector4f upVector4 = new Vector4f(rotation.transform(UP));
        Vector3f upVector = new Vector3f(upVector4.x, upVector4.y, upVector4.z);
        Vector4f forwardVector4 = new Vector4f(0,0,-1,1);
        rotation.transform(forwardVector4);
        Vector3f forwardVector = new Vector3f(forwardVector4.x, forwardVector4.y, forwardVector4.z);

        Vector3f toFar = new Vector3f(forwardVector);
        toFar.mul(SHADOW_DISTANCE);
        Vector3f toNear = new Vector3f(forwardVector);
        toNear.mul(NEAR);
        Vector3f centerNear = toNear.add(GS.camera.getPosition());
        Vector3f centerFar = toFar.add(GS.camera.getPosition());

        Vector3f rightVector = new Vector3f(); forwardVector.cross(upVector, rightVector);
        Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
        Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
        Vector3f farTop = new Vector3f(centerFar).add(new Vector3f(upVector.x * farHeight,
                upVector.y * farHeight, upVector.z * farHeight));
        Vector3f farBottom = new Vector3f(centerFar).add(new Vector3f(downVector.x * farHeight,
                downVector.y * farHeight, downVector.z * farHeight));
        Vector3f nearTop = new Vector3f(centerNear).add( new Vector3f(upVector.x * nearHeight,
                upVector.y * nearHeight, upVector.z * nearHeight));
        Vector3f nearBottom = new Vector3f(centerNear).add( new Vector3f(downVector.x * nearHeight,
                downVector.y * nearHeight, downVector.z * nearHeight));
        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);

        getBoundingBox(points);
    }

        private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
            Vector3f point = new Vector3f(); startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width), point);
            Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
            GS.getLights().get(0).getRotationMatrix().transform(point4f);
            return point4f;
        }

    private void transform(Vector4f[] vertices, Matrix4f matrix){
        for(Vector4f v : vertices){
            matrix.transform(v);
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

        maxZ += 10;

        System.out.println("DIMENSIONS");
        System.out.println("X");
        System.out.println(minX);
        System.out.println(maxX);
        System.out.println("Y");
        System.out.println(minY);
        System.out.println(maxY);
        System.out.println("Z");
        System.out.println(minZ);
        System.out.println(maxZ);
        System.out.println("POS");
        System.out.println(GS.player.getPosition());
        System.out.println("DONE");

        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;

        Vector4f centerInLightSpace = new Vector4f((minX + maxX)/2, (minY + maxY)/2, (maxZ+minZ)/2,1);
        GS.getLights().get(0).getRotatinMatrixInverse().transform(centerInLightSpace);
        center = new Vector3f(centerInLightSpace.x,centerInLightSpace.y,centerInLightSpace.z);
        System.out.println(center);
    }

    private float getAspectRatio(){
        return windowWidth/windowHeight;
    }

    public Matrix4f getOrthographicProjectionMatrix(){
        Matrix4f ortho = new Matrix4f();
        ortho.m00(2/width);
        ortho.m11(2/height);
        ortho.m22(-2/depth);
        ortho.m33(1);

        return ortho;
    }

    public Matrix4f getLightViewMatrix(){
        Matrix4f lightRotationMatrix = new Matrix4f(GS.getLights().get(0).getRotationMatrix());
        lightRotationMatrix.translate(new Vector3f(-center.x,-center.y,-center.z));
        return lightRotationMatrix;
    }

    private void calculateWidthsAndHeights() {
        farHeight = (float) (2 * SHADOW_DISTANCE * Math.tan(Math.toRadians(FOV/2)));
        nearHeight = (float) (2 * NEAR
                * Math.tan(Math.toRadians(FOV/2)));
        farWidth = farHeight * getAspectRatio();
        nearWidth = nearHeight * getAspectRatio();
    }
}
