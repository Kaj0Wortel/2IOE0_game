package src.Assets;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import src.GS;

import java.io.File;
import java.io.IOException;
import src.tools.log.Logger;

public class TextureImg {

    private float shininess;
    private float reflectivity;
    private Texture texture;

    public TextureImg(GL3 gl, String filePath, float shininess, float reflectivity) {
        this.shininess = shininess;
        this.reflectivity = reflectivity;

        try {
            texture = TextureIO.newTexture(new File(GS.TEX_DIR + filePath), false);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            
        } catch (IOException e) {
            Logger.write(e);
        }

        texture.enable(gl);

    }

    public TextureImg(GL3 gl, String filePath) {
        this.shininess = -1;
        this.reflectivity = -1;
        try {
            texture = TextureIO.newTexture(new File(GS.TEX_DIR + filePath), false);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            
        } catch (IOException e) {
            Logger.write(e);
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

    public void bindTexture(GL3 gl){
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        texture.bind(gl);
    }

    public void disableTexture(GL3 gl){
        texture.disable(gl);
    }

    public int getTexture() {
        return texture.getTextureObject();
    }
}
