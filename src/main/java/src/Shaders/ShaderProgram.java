package src.Shaders;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;
import src.OBJ.MTLObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import src.GS;

public abstract class ShaderProgram {

    private int id;

    public ShaderProgram(GL3 gl, String vertex, String fragment) {
        System.out.println(GS.LS + getClass().getName());
        id = CreateShader(gl, loadShaderFile(vertex),loadShaderFile(fragment));
        getAllUniformLocations(gl);
    }

    protected abstract void bindAttributes(GL3 gl);

    protected abstract void getAllUniformLocations(GL3 gl);

    public abstract void loadModelMatrix(GL3 gl, Matrix4f matrix);

    public abstract void loadViewMatrix(GL3 gl, Matrix4f matrix);

    public abstract void loadProjectionMatrix(GL3 gl, Matrix4f matrix4f);

    public abstract void loadTextureLightValues(GL3 gl, float shininess, float reflectivity);

    public abstract void loadTime(GL3 gl, int time);

    public abstract void loadCameraPos(GL3 gl, Vector3f cameraPos);

    public abstract void loadLight(GL3 gl, Light light);

    public abstract void loadMaterial(GL3 gl, MTLObject mtl);

    public abstract boolean useMaterial();

    public int getUniformLocation(GL3 gl, String uni){
        return gl.glGetUniformLocation(id, uni);
    }

    public void bindAttr(GL3 gl, int attr, String name){
        gl.glBindAttribLocation(id, attr, name);
    }


    public int CreateShader(GL3 gl, String[] vertex, String[] fragment){
        int program = gl.glCreateProgram();
        int vertexShader = compileShader(gl, vertex, GL2.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(gl, fragment, GL2.GL_FRAGMENT_SHADER);

        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);

        bindAttributes(gl);

        gl.glLinkProgram(program);
        System.out.println("Link status:");
        IntBuffer b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, b);
        
        //Error handling
        if (b.get(0) == GL3.GL_FALSE){
            logHandling(gl, program);
            System.out.println("Unsuccesful.");
        } else {
            System.out.println("Successful.");
        }
        
        gl.glValidateProgram(program);
        System.out.println("Validate status");
        b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL3.GL_VALIDATE_STATUS, b);
        if (b.get(0) == GL3.GL_FALSE) {
            logHandling(gl, program);
            System.out.println("Unsuccessful.");
        } else {
            System.out.println("Successful.");
        }

        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return program;
    }

    private int compileShader(GL3 gl, String[] shaderInput, int type){
        int shaderID = gl.glCreateShader(type);

        gl.glShaderSource(shaderID,1,shaderInput,null);
        gl.glCompileShader(shaderID);

        errorHandling(gl,shaderID,type);

        return shaderID;
    }

    private String[] loadShaderFile(String location){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(location))) {
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new String[] {sb.toString()};
    }

    public int getProgramID() {
        return this.id;
    }

    private void errorHandling(GL3 gl,int shaderID,int type){
        //Error handling
        IntBuffer status = Buffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderID, GL2ES2.GL_COMPILE_STATUS, status);
        if(status.get(0) == GL3.GL_FALSE){
            IntBuffer lengthOfError = Buffers.newDirectIntBuffer(1);
            gl.glGetShaderiv(shaderID, GL3.GL_INFO_LOG_LENGTH, lengthOfError);

            ByteBuffer error = Buffers.newDirectByteBuffer(lengthOfError.get(0));
            gl.glGetShaderInfoLog(shaderID, lengthOfError.get(), null, error);

            System.out.println("Failed to compile "
                    + (type == GL3.GL_VERTEX_SHADER
                            ? "Vertex Shader"
                            : "Fragment Shader"));
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < lengthOfError.get(0); i++){
                sb.append((char) error.get(i));
            }
            System.out.println(sb.toString());
        } else {
            System.out.println("Succesfully compiled the " 
                    + (type == GL3.GL_VERTEX_SHADER
                            ? "Vertex Shader"
                            : "Fragment Shader"));
        }
    }

    private void logHandling(GL3 gl, int program){
        ByteBuffer str = Buffers.newDirectByteBuffer(100);
        gl.glGetProgramInfoLog(program,str.capacity(),null,str);
        String s = "";
        for(int i = 0; i < str.capacity(); i++){
            s += (char) str.get(i);
        }
        System.out.println(s);
    }

    public void loadUniformMatrix(GL3 gl, int location, Matrix4f matrix){
        FloatBuffer m = Buffers.newDirectFloatBuffer(16);
        matrix.get(m);

        gl.glUniformMatrix4fv(location,1,false,m);
    }

    public void loadUniformVector(GL3 gl, int location, Vector3f vector){
        gl.glUniform3f(location, vector.x,vector.y,vector.z);
    }

    public void loadUniformFloat(GL3 gl, int location, float fl){
        gl.glUniform1f(location,fl);
    }

    public void loadUniformInt(GL3 gl, int location, int in){
        gl.glUniform1i(location, in);
    }

    public void stop(GL3 gl){
        gl.glUseProgram(0);
    }

    public void start(GL3 gl){
        gl.glUseProgram(id);
    }

    public void cleanUp(GL3 gl){
        stop(gl);
        gl.glDeleteProgram(id);
    }
}
