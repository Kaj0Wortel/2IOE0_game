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
    }

    private void generateFBO(GL3 gl){
        fbo = Buffers.newDirectIntBuffer(1);
        gl.glGenFramebuffers(1, fbo);
        gl.glDrawBuffer(gl.GL_NONE);
        gl.glReadBuffer(gl.GL_NONE);
    }

    private void generateDepthAttachment(GL3 gl){
        depthAttachment = Buffers.newDirectIntBuffer(1);
        gl.glGenTextures(1,depthAttachment);
        gl.glBindTexture(gl.GL_TEXTURE_2D,depthAttachment.get(0));
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_DEPTH_COMPONENT16, width, height, 0, gl.GL_DEPTH_COMPONENT, gl.GL_FLOAT, null);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
        gl.glFramebufferTexture(gl.GL_FRAMEBUFFER, gl.GL_DEPTH_ATTACHMENT, depthAttachment.get(0), 0);
    }

    private void bindFrameBuffer(GL3 gl){
        gl.glBindTexture(gl.GL_TEXTURE_2D, depthAttachment.get(0));
        gl.glBindFramebuffer(gl.GL_DRAW_FRAMEBUFFER, fbo.get(0));
        gl.glViewport(0,0,width,height);
    }

    private void unbindFrameBuffer(GL3 gl){
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER,0);
        gl.glViewport(0,0, GS.width, GS.height);
    }
}
