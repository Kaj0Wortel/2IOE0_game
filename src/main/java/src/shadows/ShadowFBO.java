
package src.shadows;


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import src.GS;

import java.nio.IntBuffer;


public class ShadowFBO {

    private int width;
    private int height;

    private IntBuffer fbo;
    private IntBuffer depthAttachment;

    public ShadowFBO(GL3 gl, int width, int height){
        this.width = width;
        this.height = height;
        generateFBO(gl);
        generateDepthAttachment(gl);
        unbindFrameBuffer(gl);
    }

    private void generateFBO(GL3 gl) {
        fbo = Buffers.newDirectIntBuffer(1);
        gl.glGenFramebuffers(1, fbo);
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fbo.get(0));
        gl.glDrawBuffer(GL3.GL_NONE);
        gl.glReadBuffer(GL3.GL_NONE);
    }

    private void generateDepthAttachment(GL3 gl) {
        depthAttachment = Buffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, depthAttachment);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, depthAttachment.get(0));
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_DEPTH_COMPONENT16,
                width, height, 0,
                GL3.GL_DEPTH_COMPONENT, GL3.GL_FLOAT, null);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glFramebufferTexture(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT,
                depthAttachment.get(0), 0);
        //gl.glEnable(GL3.GL_TEXTURE_2D);
    }
    
    public void bindFrameBuffer(GL3 gl) {
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        gl.glEnable(GL3.GL_TEXTURE_2D);
        gl.glBindFramebuffer(GL3.GL_DRAW_FRAMEBUFFER, fbo.get(0));
        gl.glViewport(0, 0, width, height);
    }
    
    public void unbindFrameBuffer(GL3 gl) {
        gl.glDisable(GL3.GL_TEXTURE_2D);
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
        gl.glViewport(0, 0, GS.canvas.getWidth(), GS.canvas.getHeight());
    }
    
    public IntBuffer getDepthAttachment() {
        return depthAttachment;
    }
}
