package src.Assets;

public class Texture {

    private float shininess;
    private float reflectivity;

    public Texture(float shininess, float reflectivity) {
        this.shininess = shininess;
        this.reflectivity = reflectivity;
    }

    public float getShininess() {
        return shininess;
    }

    public float getReflectivity() {
        return reflectivity;
    }
}
