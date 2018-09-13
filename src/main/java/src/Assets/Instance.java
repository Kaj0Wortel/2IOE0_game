package src.Assets;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Vector;

public class Instance {

    private Vector3f position;
    private float size;
    private float rotx;
    private float roty;
    private float rotz;

    private AssetTexture model;

    public Instance(Vector3f position, float size, float rotx, float roty, float rotz, AssetTexture model) {
        this.position = position;
        this.size = size;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.model = model;
    }

    public Matrix4f getTransformationMatrix(){
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(position);
        transformationMatrix.rotate((float) Math.toRadians(rotx),1,0,0);
        transformationMatrix.rotate((float) Math.toRadians(roty),0,1,0);
        transformationMatrix.rotate((float) Math.toRadians(rotz),0,0,1);
        transformationMatrix.scale(size,size,size);

        return transformationMatrix;
    }

    public AssetTexture getModel() {
        return model;
    }

    public void rotx(){
        rotx += 3f;
    }

    public void roty(){
        roty += 3f;
    }
}
