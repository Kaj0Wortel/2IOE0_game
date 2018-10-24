package src.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.Assets.instance.Car;
import src.GS;
import src.Renderer.Camera;

public class FrustrumBox {

    private float width;
    private float height;
    private float depth;
    private Vector3f center = new Vector3f();

    private float near;
    private float far;
    private float windowWidth; // TODO
    private float windowHeight;

    final private float SHADOW_DISTANCE = 300;

    public FrustrumBox(Car player, float NEAR, float FAR,
            float windowWidth, float windowHeight) {
        this.near = NEAR;
        this.far = FAR;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        width = 0;
        height = 0;
        depth = 0;

        update(player);
    }

    public void update(Car player) {
        Camera cam = GS.getCam(player);
        float hhn = (float) Math.tan(Math.toRadians(cam.fov()/2)) * near;
        float hwn = getAspectRatio() * hhn;
        float hhf = (float) Math.tan(Math.toRadians(cam.fov()/2)) * SHADOW_DISTANCE;
        float hwf = getAspectRatio() * hhf;
        
        Vector4f[] eyeVertices = new Vector4f[] {
            new Vector4f(-hwn,  hhn, -near, 1),
            new Vector4f( hwn,  hhn, -near, 1),
            new Vector4f(-hwn, -hhn, -near, 1),
            new Vector4f( hwn, -hhn, -near, 1),
            
            new Vector4f(-hwf,  hhf, -SHADOW_DISTANCE, 1),
            new Vector4f( hwf,  hhf, -SHADOW_DISTANCE, 1),
            new Vector4f(-hwf, -hhf, -SHADOW_DISTANCE, 1),
            new Vector4f( hwf, -hhf, -SHADOW_DISTANCE, 1)
        };
        
        transform(eyeVertices, GS.getCam(player).getViewMatrixInverse());
        transform(eyeVertices, GS.getLights().get(0).getRotationMatrix());
        
        getBoundingBox(eyeVertices);
    }

    private void transform(Vector4f[] vertices, Matrix4f matrix) {
        for(Vector4f v : vertices) {
            matrix.transform(v);
        }
    }

    private void getBoundingBox(Vector4f[] vertices) {
        float maxX = Float.NEGATIVE_INFINITY;
        float minX = Float.POSITIVE_INFINITY;
        
        float maxY = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        
        float maxZ = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        
        for(Vector4f v : vertices) {
            maxX = Math.max(maxX, v.x);
            minX = Math.min(minX, v.x);
            
            maxY = Math.max(maxY, v.y);
            minY = Math.min(minY, v.y);
            
            maxZ = Math.max(maxZ, v.z);
            minZ = Math.min(minZ, v.z);
        }
        
        minZ -= 50; // Some random increment that fixes everything.
        
        width  = maxX - minX;
        height = maxY - minY;
        depth  = maxZ - minZ;

        Vector4f centerInLightSpace = new Vector4f(
                (minX + maxX) / 2,
                (minY + maxY) / 2,
                (minZ + maxZ) / 2,
                1);
        GS.getLights().get(0)
                .getRotationMatrixInverse()
                .transform(centerInLightSpace);
        center = new Vector3f(
                centerInLightSpace.x,
                centerInLightSpace.y,
                centerInLightSpace.z);
    }

    private float getAspectRatio() {
        return windowWidth / windowHeight;
    }

    public Matrix4f getOrthographicProjectionMatrix() {
        Matrix4f ortho = new Matrix4f();
        ortho.m00( 2 / width);
        ortho.m11( 2 / height);
        ortho.m22(-2 / depth);
        ortho.m33(1);

        return ortho;
    }

    public Matrix4f getLightViewMatrix() {
        return new Matrix4f(GS.getLights().get(0).getRotationMatrix())
                .translate(new Vector3f(-center.x, -center.y, -center.z));
    }
}
