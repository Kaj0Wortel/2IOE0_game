package src.glGUI;

import com.jogamp.opengl.GL3;
import javax.swing.SwingUtilities;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.TextureImg;
import src.GS;
import src.Shaders.ShaderProgram;
import src.tools.MultiTool;


public abstract class GUI {
    protected Vector2f position;
    protected Vector2f size;
    
    protected int[] textures;
    
    
    public GUI(GL3 gl, Vector2f position, Vector2f size,
            TextureImg... textures) {
        this(gl, position, size, MultiTool.<int[]>createObject(() -> {
            if (textures == null) return null;
            int[] rtn = new int[textures.length];
            for (int i = 0; i < textures.length; i++) {
                rtn[i] = textures[i].getTexture();
            }
            return rtn;
        }));
    }
    
    public GUI(GL3 gl, Vector2f position, Vector2f size, int... textures) {
        this.position = position;
        this.size = size;
        this.textures = (textures == null ? new int[0] : textures);
        
        SwingUtilities.invokeLater(() -> {
            GS.addGUI(this);
        });
    }

    public int[] getTextures() {
        return textures;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }

    public Matrix4f getTransformationMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(new Vector3f(position.x, position.y,0));
        matrix.scale(size.x, size.y, 1.0f);
        return matrix;
    }
    
    public abstract void loadShaderData(GL3 gl);
    
    public abstract ShaderProgram getShader();
    
    
}
