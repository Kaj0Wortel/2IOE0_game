package src.Assets;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f color;

    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Matrix4f getRotationMatrix() {
        Vector3f lightdir = new Vector3f(position).negate();
        lightdir.normalize();
        float pitch = (float) Math.acos(new Vector2f(lightdir.x, lightdir.z).length());
        float yaw = (float) Math.toDegrees((float) Math.atan(lightdir.x / lightdir.z));
        yaw = (lightdir.z > 0 ? yaw - 180 : yaw);

        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate((float) pitch, new Vector3f(1, 0, 0));
        rotationMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));

        return rotationMatrix;
    }

    public Matrix4f getRotationMatrixInverse(){
        return new Matrix4f(getRotationMatrix()).invert();
    }
}
