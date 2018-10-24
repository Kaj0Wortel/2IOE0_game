
package src.glGUI;

import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import src.Assets.TextureImg;
import src.Assets.instance.Car;
import src.GS;
import src.Progress.ProgressManager;
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
                new TextureImg(gl, "Speedometer.png"),
                new TextureImg(gl, "Panel2.png"),
                new TextureImg(gl, "items.png"),
                new TextureImg(gl, "positions.png"),
                new TextureImg(gl, "numbers2.png"),
                new TextureImg(gl, "lap.png"),
                new TextureImg(gl, "youwon.png"),
                new TextureImg(gl, "You_lose.png")
        );
        
        shader = new StaticGUIShader(gl);
    }
    
    @Override
    public void loadShaderData(GL3 gl, Car car) {
        shader.loadTextures(gl);
        shader.loadVars(gl);
        shader.loadTime(gl, (int) (GS.time / 1000L));
        shader.loadModelMatrix(gl, getTransformationMatrix());
        ProgressManager pm = car.getProgressManager();
        shader.loadLapData(gl, Math.min(pm.lap, pm.lapTotal), pm.lapTotal);
        shader.loadItemNum(gl, car.getItem());
        shader.loadPositionNum(gl, car.getRacePosition());
        shader.loadFinished(gl, car.isFinished());
        if(car.isFinished()) shader.loadFirst(gl, car.isFirst());
        
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
