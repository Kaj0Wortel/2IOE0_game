package src.Assets;


import org.joml.Vector4f;


public class Light {

    private Vector4f position;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;

    public Light(Vector4f position, Vector4f ambient,
            Vector4f diffuse, Vector4f specular) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public Vector4f getPosition() {
        return position;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }
    
    
}
