package src.Assets.skybox;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import src.GS;
import src.Shaders.SkyBoxShader;
import src.tools.Binder;
import src.tools.log.Logger;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Skybox {

    final public static String SKYBOX_DIR = GS.RESOURCE_DIR
            + "skyboximages" + GS.FS;

    private IntBuffer vao;
    private int nrV;
    private SkyBoxShader skyBoxShader;
    private int texture;

    private final float SIZE = 1000f;

    private final float[] skyBoxVertices = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
             SIZE,  SIZE, -SIZE,
             SIZE,  SIZE,  SIZE,
             SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
             SIZE, -SIZE, -SIZE,
             SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
             SIZE, -SIZE,  SIZE
    };

    private String[] files = {"right", "left", "top", "bottom", "back", "front"};

    public Skybox(GL3 gl) {
        this.vao = Binder.loadVAO(gl, skyBoxVertices, 3);
        this.texture = getCubeMap(gl);
        this.nrV = skyBoxVertices.length/3;
        this.skyBoxShader = new SkyBoxShader(gl);
    }
    
    public void draw(GL3 gl, Matrix4f projectionMatrix) {
        skyBoxShader.start(gl);

        skyBoxShader.loadProjectionMatrix(gl,projectionMatrix);
        skyBoxShader.loadViewMatrix(gl, getSkyboxViewMatrix());
        skyBoxShader.loadTexture(gl);

        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, texture);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, nrV);
        gl.glBindVertexArray(0);
        
        skyBoxShader.stop(gl);
    }

    private Matrix4f getSkyboxViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f(GS.camera.getViewMatrix());
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        return viewMatrix;
    }

    private int getCubeMap(GL3 gl){

        IntBuffer cubeMap = Buffers.newDirectIntBuffer(1);
        gl.glGenTextures(1,cubeMap);
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, cubeMap.get(0));

        for(int i = 0; i < files.length; i++){
            SkyboxTexurePart skyboxTexurePart = getPartialCubeMap(
                    SKYBOX_DIR + files[i] + ".png");
            gl.glTexImage2D(GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, 
                    GL3.GL_RGBA, skyboxTexurePart.getWidth(),
                    skyboxTexurePart.getHeight(),
                    0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE,
                    skyboxTexurePart.getData());
        }
        
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        return cubeMap.get(0);
    }

    private SkyboxTexurePart getPartialCubeMap(String file) {
        try (FileInputStream imgData = new FileInputStream(file)) {
            PNGDecoder pngDecoder = new PNGDecoder(imgData);
            int imgHeight = pngDecoder.getHeight();
            int imgWidth = pngDecoder.getWidth();
            ByteBuffer rawImgData = ByteBuffer.allocateDirect(4 * imgWidth * imgHeight);
            pngDecoder.decode(rawImgData, imgWidth * 4, PNGDecoder.Format.RGBA);
            rawImgData.flip();
            return new SkyboxTexurePart(rawImgData, imgWidth, imgHeight);
            
        } catch(Exception e){
            Logger.write(e);
            System.exit(-1);
        }
        
        return null;
    }
}


