package src.Shaders;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Assets.Light;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class ShaderProgram {

    private int ID;

    public ShaderProgram(GL2 gl, String vertex, String fragment){
        ID = CreateShader(gl,LoadShaderFile(vertex),LoadShaderFile(fragment));

        getAllUniformLocations(gl);
    }

    protected abstract void bindAttributes(GL2 gl);

    protected abstract void getAllUniformLocations(GL2 gl);

    public abstract void loadModelMatrix(GL2 gl, Matrix4f matrix);

    public abstract void loadViewMatrix(GL2 gl, Matrix4f matrix);

    public abstract void loadProjectionMatrix(GL2 gl, Matrix4f matrix4f);

    public abstract void loadTextureLightValues(GL2 gl, float shininess, float reflectivity);

    public abstract void loadTime(GL2 gl, int time);

    public abstract void loadCameraPos(GL2 gl, Vector3f cameraPos);

    public abstract void loadLight(GL2 gl, Light light);

    public int getUniformLocation(GL2 gl, String uni){
        return gl.glGetUniformLocation(ID,uni);
    }

    public void bindAttr(GL2 gl, int attr, String name){
        gl.glBindAttribLocation(ID,attr,name);
    }


    public int CreateShader(GL2 gl, String[] vertex, String[] fragment){
        int program = gl.glCreateProgram();
        int vertexShader = CompileShader(gl, vertex, GL2.GL_VERTEX_SHADER);
        int fragmentShader = CompileShader(gl, fragment, GL2.GL_FRAGMENT_SHADER);

        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);

        bindAttributes(gl);

        gl.glLinkProgram(program);
        System.out.println("Link status:");
        IntBuffer b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program,gl.GL_LINK_STATUS,b);
        //Error handling
        if(b.get(0) == gl.GL_FALSE){
            LogHandling(gl,program);
            System.out.println("Unsuccesful.");
        }else{
            System.out.println("Successful.");
        }
        gl.glValidateProgram(program);
        System.out.println("Validate status");
        b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program,gl.GL_VALIDATE_STATUS,b);
        //Error handling
        if(b.get(0) == gl.GL_FALSE){
            LogHandling(gl,program);
            System.out.println("Unsuccessful.");
        }else{
            System.out.println("Successful.");
        }

        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return program;
    }

    private int CompileShader(GL2 gl, String[] shaderInput, int type){
        int shaderID = gl.glCreateShader(type);

        gl.glShaderSource(shaderID,1,shaderInput,null);
        gl.glCompileShader(shaderID);

        ErrorHandling(gl,shaderID,type);

        return shaderID;
    }

    private String[] LoadShaderFile(String location){
        String s = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            String line = reader.readLine();
            while(line != null){
                line += "\n";
                s += line + "\n";
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[] {s};
    }

    public int getProgramID(){
        return this.ID;
    }

    private void ErrorHandling(GL2 gl,int shaderID,int type){
        //Error handling
        IntBuffer status = Buffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderID, GL2ES2.GL_COMPILE_STATUS, status);
        if(status.get(0) == gl.GL_FALSE){
            IntBuffer length_of_error = Buffers.newDirectIntBuffer(1);
            gl.glGetShaderiv(shaderID, gl.GL_INFO_LOG_LENGTH, length_of_error);

            ByteBuffer error = Buffers.newDirectByteBuffer(length_of_error.get(0));
            gl.glGetShaderInfoLog(shaderID, length_of_error.get(),null, error);

            System.out.println("Failed to compile " + (type == gl.GL_VERTEX_SHADER ? "Vertex Shader" : "Fragment Shader"));
            String result = "";
            for(int i = 0; i < length_of_error.get(0); i++){
                result += (char)error.get(i);
            }
            System.out.println(result);
        }else{
            System.out.println("Succesfully compiled the " + (type == gl.GL_VERTEX_SHADER ? "Vertex Shader" : "Fragment Shader"));
        }
    }

    private void LogHandling(GL2 gl, int program){
        ByteBuffer str = Buffers.newDirectByteBuffer(100);
        gl.glGetProgramInfoLog(program,str.capacity(),null,str);
        String s = "";
        for(int i = 0; i < str.capacity(); i++){
            s += (char) str.get(i);
        }
        System.out.println(s);
    }

    public void loadUniformMatrix(GL2 gl, int location, Matrix4f matrix){
        FloatBuffer m = Buffers.newDirectFloatBuffer(16);
        matrix.get(m);

        gl.glUniformMatrix4fv(location,1,false,m);
    }

    public void loadUniformVector(GL2 gl, int location, Vector3f vector){
        gl.glUniform3f(location, vector.x,vector.y,vector.z);
    }

    public void loadUniformFloat(GL2 gl, int location, float fl){
        gl.glUniform1f(location,fl);
    }

    public void loadUniformInt(GL2 gl, int location, int in){
        gl.glUniform1i(location,in);
    }

    public void stop(GL2 gl){
        gl.glUseProgram(0);
    }

    public void start(GL2 gl){
        gl.glUseProgram(ID);
    }

    public void cleanUp(GL2 gl){
        stop(gl);
        gl.glDeleteProgram(ID);
    }
}
