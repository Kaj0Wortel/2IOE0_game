package src.Assets;

import org.joml.Vector4f;

public class Texture {
    final private Vector4f ambient;
    final private Vector4f diffuse;
    final private Vector4f specular;
    final private float shininess;

    //private float shininess;
    //private float reflectivity;
    /*
    public Texture(float shininess, float reflectivity, int a) {
        this.shininess = shininess;
        this.reflectivity = reflectivity;
    }*/
    public Texture(Vector4f ambient, Vector4f diffuse, Vector4f specular,
            float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
    
    /*
    public float getReflectivity() {
        return reflectivity;
    }
    */
    
    public Vector4f getAmbient() {
        return ambient;
    }
    
    public Vector4f getDiffuse() {
        return diffuse;
    }
    
    public Vector4f getSpecular() {
        return specular;
    }
    
    public float getShininess() {
        return shininess;
    }
    
    
}
