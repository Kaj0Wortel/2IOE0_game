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
        float[] vertices = { -1, 1, -1, -1, 1, 1, 1, -1};
        vao = Binder.loadVAO(gl, vertices);
        nrV = vertices.length/2;
    }

    public void render(GL3 gl){
        guiShader.start(gl);
        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glActiveTexture(gl.GL_TEXTURE0);
        guiShader.loadTexture(gl);

        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(gl.GL_DEPTH_TEST);

        for(GUI gui : GS.getGUIs()){
            guiShader.loadModelMatrix(gl, gui.getTransformationMatrix());
            gl.glBindTexture(gl.GL_TEXTURE_2D, gui.getTexture());
            gl.glDrawArrays(gl.GL_TRIANGLE_STRIP,0, nrV);
        }

        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glDisable(gl.GL_BLEND);


        gl.glDisableVertexAttribArray(0);
        gl.glBindVertexArray(0);
        guiShader.stop(gl);
    }

    public void cleanup(GL3 gl){
        guiShader.cleanUp(gl);
    }
}
