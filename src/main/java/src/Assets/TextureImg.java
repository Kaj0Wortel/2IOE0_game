package src.Assets;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import src.GS;

import java.io.File;
import java.io.IOException;

public class TextureImg {

    private float shininess;
    private float reflectivity;
    private Texture texture;

    public TextureImg(GL2 gl, String file_path, float shininess, float reflectivity) {
        this.shininess = shininess;
        this.reflectivity = reflectivity;

        try {
            texture = TextureIO.newTexture(new File(GS.TEX_DIR + file_path), false);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        texture.enable(gl);

    }

    public TextureImg(float shininess, float reflectivity){
        this.shininess = shininess;
        this.reflectivity = reflectivity;
    }

    public float getShininess() {
        return shininess;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void bindTexture(GL2 gl){
        texture.bind(gl);
    }

    public void disableTexture(GL2 gl){
        texture.disable(gl);
    }
}
