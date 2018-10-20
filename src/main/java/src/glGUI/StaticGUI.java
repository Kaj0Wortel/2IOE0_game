
package src.glGUI;

import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import src.Assets.TextureImg;
import src.Shaders.ShaderProgram;
import src.Shaders.StaticGUIShader;


// Own imports


// Java imports


/**
 * 
 */
public class StaticGUI
        extends GUI {
    
    final private StaticGUIShader shader;
    
    public StaticGUI(GL3 gl, Vector2f position, Vector2f size) {
        super(gl, position, size,
                new TextureImg(gl,"test_icon.png"),
                new TextureImg(gl, "test_border.png"),
                new TextureImg(gl, "numbers2.png")
        );
        
        shader = new StaticGUIShader(gl);
    }
    
    @Override
    public void loadShaderData(GL3 gl) {
        shader.loadTextures(gl);
        shader.loadVars(gl);
        shader.loadTime(gl, (int) (System.currentTimeMillis() / 1000L)); // TODO
        shader.loadModelMatrix(gl, getTransformationMatrix());

        for (int i = 0; i < textures.length; i++) {
            gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textures[i]);
        }
    }
    
    @Override
    public ShaderProgram getShader() {
        return shader;
    }
    
    
}
