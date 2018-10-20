
package src.glGUI;

import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import src.Assets.TextureImg;
import src.Shaders.ShaderProgram;
import src.Shaders.SpeedNeedleGUIShader;


// Own imports


// Java imports


/**
 * 
 */
public class SpeedNeedleGUI
        extends GUI {
    
    final private SpeedNeedleGUIShader shader;
    
    public SpeedNeedleGUI(GL3 gl, Vector2f position, Vector2f size) {
        super(gl, position, size,
                new TextureImg(gl,"needle.png")
        );
        
        shader = new SpeedNeedleGUIShader(gl);
    }
    
    float angle = 0;
    @Override
    public void loadShaderData(GL3 gl) {
        shader.loadTextures(gl);
        shader.loadVars(gl);
        angle += 0.05;
        angle %= Math.PI * 2;
        //System.out.println(angle);
        shader.loadAngle(gl, angle);
        //shader.loadAngle(gl, 0);
        //shader.loadAngle(gl, (float) Math.PI / 2);
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
