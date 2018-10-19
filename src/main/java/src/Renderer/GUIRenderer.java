package src.Renderer;

import com.jogamp.opengl.GL3;
import src.Assets.GUI;
import src.GS;
import src.Shaders.GUIShader;
import src.tools.Binder;

import java.nio.IntBuffer;

public class GUIRenderer {

    private GUI guiTemplate;
    private IntBuffer vao;
    private int nrV;
    private GUIShader guiShader;

    public GUIRenderer(GL3 gl) {
        guiShader = new GUIShader(gl);
        float[] vertices = {-1, 1, -1, -1, 1, 1, 1, -1};
        vao = Binder.loadVAO(gl, vertices, 1);
        nrV = vertices.length/2;
    }

    public void render(GL3 gl) {
        GUI gui = GS.getGUI();
        if (gui == null) return;
        
        guiShader.start(gl);
        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        guiShader.loadTexture(gl);

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL3.GL_DEPTH_TEST);
        
        guiShader.loadModelMatrix(gl, gui.getTransformationMatrix());
        int[] textures = gui.getTextures();
        for (int i = 0; i < textures.length; i++) {
            gl.glActiveTexture(GL3.GL_TEXTURE0 + i);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, textures[i]);
        }
        
        gl.glEnable(GL3.GL_TEXTURE_2D);
        
        gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, nrV);
        
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
