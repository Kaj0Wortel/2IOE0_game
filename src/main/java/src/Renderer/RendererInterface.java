
package src.Renderer;


import com.jogamp.opengl.GL3;
import src.Assets.instance.Car;


/**
 * 
 */
public interface RendererInterface {
    
    public void render(GL3 gl, Car player);
    
}
