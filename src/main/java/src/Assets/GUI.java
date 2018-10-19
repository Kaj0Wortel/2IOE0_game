package src.Assets;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.tools.update.Updateable;

public class GUI {
    private int texture;
    private Vector2f position;
    private Vector2f size;
    
    private TextureImg test;

    public GUI(GL3 gl, int texture, Vector2f position, Vector2f size) {
        this.texture = texture;
        this.position = position;
        this.size = size;
        this.test = new TextureImg(gl, "test_border.png");
    }

    public int[] getTextures() {
        return new int[] {texture, test.getTexture()};
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
        matrix.rotate((float) Math.toRadians(180), 0, 0, 1);
        return matrix;
    }
    
    
}
