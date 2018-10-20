package src.Renderer;

import com.jogamp.opengl.GL3;
import src.glGUI.GUI;
import src.GS;
import src.Shaders.StaticGUIShader;
import src.tools.Binder;

import java.nio.IntBuffer;
import java.util.List;

public class GUIRenderer {
    private IntBuffer vao;
    private int nrV;
    private StaticGUIShader guiShader;

    public GUIRenderer(GL3 gl) {
        guiShader = new StaticGUIShader(gl);
        float[] vertices = {
            0, 1,
            0, 0,
            1, 1,
            1, 0
        };
        vao = Binder.loadVAO(gl, vertices, 2);
        nrV = vertices.length / 2;
    }

    public void render(GL3 gl) {
        List<GUI> guis = GS.getGUIs();
        if (guis.isEmpty()) return;
        
        guiShader.start(gl);
        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        guiShader.loadTextures(gl);
        guiShader.loadVars(gl);
        guiShader.loadTime(gl, (int) (System.currentTimeMillis() / 1000L)); // TODO

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL3.GL_DEPTH_TEST);
        gl.glEnable(GL3.GL_TEXTURE_2D);
        
        for (GUI gui : guis) {
            guiShader.loadModelMatrix(gl, gui.getTransformationMatrix());
            
            int[] textures = gui.getTextures();
            for (int i = 0; i < textures.length; i++) {
                gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
                gl.glBindTexture(GL3.GL_TEXTURE_2D, textures[i]);
            }
            
            gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, nrV);
        }
        
        gl.glDisable(GL3.GL_TEXTURE_2D);
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glDisable(GL3.GL_BLEND);
        
        
        gl.glDisableVertexAttribArray(0);
        gl.glBindVertexArray(0);
        guiShader.stop(gl);
    }

    public void cleanup(GL3 gl){
        guiShader.cleanUp(gl);
    }
}
