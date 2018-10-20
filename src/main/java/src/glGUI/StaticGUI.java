
package src.glGUI;

import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import src.Assets.TextureImg;


// Own imports


// Java imports


/**
 * 
 */
public class StaticGUI
        extends GUI {
    
    public StaticGUI(GL3 gl, Vector2f position, Vector2f size) {
        super(gl, position, size,
                new TextureImg(gl,"test_icon.png"),
                new TextureImg(gl, "test_border.png"),
                new TextureImg(gl, "numbers2.png")
        );
    }
    
    
}
