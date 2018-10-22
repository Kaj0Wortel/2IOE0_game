package src.Renderer;

import com.jogamp.opengl.GL3;
import src.glGUI.GUI;
import src.GS;
import src.tools.Binder;

import java.nio.IntBuffer;
import java.util.List;
import src.Assets.instance.Car;
import src.Shaders.ShaderProgram;

public class GUIRenderer {
    private IntBuffer vao;
    private int nrV;

    public GUIRenderer(GL3 gl, Car player) {
        float[] vertices = {
            0, 1,
            0, 0,
            1, 1,
            1, 0
        };
        vao = Binder.loadVAO(gl, vertices, 2);
        nrV = vertices.length / 2;
    }

    public void render(GL3 gl, Car player) {
        List<GUI> guis = GS.getGUIs();
        if (guis.isEmpty()) return;
        
        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL3.GL_DEPTH_TEST);
        gl.glEnable(GL3.GL_TEXTURE_2D);
        
        for (GUI gui : guis) {
            ShaderProgram guiShader = gui.getShader();
            guiShader.start(gl);
            gui.loadShaderData(gl, player);
            gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, nrV);
            guiShader.stop(gl);
        }
        
        gl.glDisable(GL3.GL_TEXTURE_2D);
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glDisable(GL3.GL_BLEND);
        
        
        gl.glDisableVertexAttribArray(0);
        gl.glBindVertexArray(0);
    }
    
    
}
