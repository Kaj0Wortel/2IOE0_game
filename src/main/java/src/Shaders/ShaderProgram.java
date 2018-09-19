package src.Shaders;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector4f;
import src.Assets.Texture;

public abstract class ShaderProgram {

    private int id;

    public ShaderProgram(GL2 gl, String vertex, String fragment){
        id = CreateShader(gl, loadShaderFile(vertex), loadShaderFile(fragment));
        getAllUniformLocations(gl);
    }

    protected abstract void bindAttributes(GL2 gl);

    protected abstract void getAllUniformLocations(GL2 gl);

    public abstract void loadModelMatrix(GL2 gl, Matrix4f matrix);

    public abstract void loadViewMatrix(GL2 gl, Matrix4f matrix);

    public abstract void loadProjectionMatrix(GL2 gl, Matrix4f matrix4f);

    public abstract void loadTexture(GL2 gl, Texture texture);

    public abstract void loadTime(GL2 gl, int time);

    public int getUniformLocation(GL2 gl, String uni){
        return gl.glGetUniformLocation(id, uni);
    }

    public void bindAttr(GL2 gl, int attr, String name){
        gl.glBindAttribLocation(id, attr, name);
    }


    public int CreateShader(GL2 gl, String[] vertex, String[] fragment) {
        int program = gl.glCreateProgram();
        int vertexShader = compileShader(gl, vertex, GL2.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(gl, fragment, GL2.GL_FRAGMENT_SHADER);

        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);

        bindAttributes(gl);

        gl.glLinkProgram(program);
        System.out.println("Link status:");
        IntBuffer b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL2.GL_LINK_STATUS,b);
        //Error handling
        if(b.get(0) == GL2.GL_FALSE) {
            logHandling(gl,program);
            System.out.println("Unsuccesful.");
        } else {
            System.out.println("Successful.");
        }
        
        gl.glValidateProgram(program);
        System.out.println("Validate status:");
        b = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL2.GL_VALIDATE_STATUS,b);
        //Error handling
        if(b.get(0) == GL2.GL_FALSE){
            logHandling(gl,program);
            System.out.println("Unsuccessful.");
        }else{
            System.out.println("Successful.");
        }

        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return program;
    }

    private int compileShader(GL2 gl, String[] shaderInput, int type) {
        int shaderID = gl.glCreateShader(type);

        gl.glShaderSource(shaderID, 1, shaderInput,null);
        gl.glCompileShader(shaderID);

        errorHandling(gl, shaderID, type);

        return shaderID;
    }
    
    /**
     * Reads a shader program from a file.
     * 
     * @param fileName the name of the file to get the shader from.
     * @return a string array where the first element
     *     contains the shader program.
     */
    private static String[] loadShaderFile(String fileName) {
        // Use string builder insteaad of string to reduce runtime.
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader
                = new BufferedReader(new FileReader(fileName))) {
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }

        return new String[] {sb.toString()};
    }

    /**
     * @return the id of this shader program.
     */
    public int getProgramID(){
        return this.id;
    }

    private void errorHandling(GL2 gl,int shaderID,int type) {
        //Error handling
        IntBuffer status = Buffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderID, GL2ES2.GL_COMPILE_STATUS, status);
        if (status.get(0) == GL2.GL_FALSE) {
            IntBuffer length_of_error = Buffers.newDirectIntBuffer(1);
            gl.glGetShaderiv(shaderID, GL2.GL_INFO_LOG_LENGTH, length_of_error);

            ByteBuffer error = Buffers.newDirectByteBuffer(length_of_error.get(0));
            gl.glGetShaderInfoLog(shaderID, length_of_error.get(),null, error);

            System.out.println("Failed to compile "
                    + (type == GL2.GL_VERTEX_SHADER
                            ? "Vertex Shader"
                            : "Fragment Shader"));
            String result = "";
            for(int i = 0; i < length_of_error.get(0); i++){
                result += (char)error.get(i);
            }
            System.out.println(result);
            
        } else {
            System.out.println("Succesfully compiled the "
                    + (type == GL2.GL_VERTEX_SHADER
                            ? "Vertex Shader"
                            : "Fragment Shader"));
        }
    }

    private void logHandling(GL2 gl, int program){
        ByteBuffer str = Buffers.newDirectByteBuffer(100);
        gl.glGetProgramInfoLog(program,str.capacity(),null,str);
        String s = "";
        for(int i = 0; i < str.capacity(); i++){
            s += (char) str.get(i);
        }
        System.out.println(s);
    }
    
    /**
     * Loads a matrix at the given location in {@code this} shader program.
     * 
     * @param gl
     * @param location
     * @param vector 
     */
    public void loadUniformMatrix(GL2 gl, int location, Matrix4f matrix){
        FloatBuffer m = Buffers.newDirectFloatBuffer(16);
        matrix.get(m);

        gl.glUniformMatrix4fv(location,1,false,m);
    }
    
    /**
     * Loads a vector at the given location in {@code this} shader program.
     * 
     * @param gl
     * @param location
     * @param vector 
     */
    public void loadUniformVector3f(GL2 gl, int location, Vector3f vector){
        gl.glUniform3f(location, vector.x,vector.y,vector.z);
    }
    
    /**
     * Loads a vector at the given location in {@code this} shader program.
     * 
     * @param gl
     * @param location
     * @param vector 
     */
    public void loadUniformVector4f(GL2 gl, int location, Vector4f vector){
        gl.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }
    
    /**
     * Loads a float at the given location in {@code this} shader program.
     * 
     * @param gl
     * @param location
     * @param vector 
     */
    public void loadUniformFloat(GL2 gl, int location, float fl){
        gl.glUniform1f(location,fl);
    }
    
    /**
     * Loads an integer at the given location in {@code this} shader program.
     * 
     * @param gl
     * @param location
     * @param vector 
     */
    public void loadUniformInt(GL2 gl, int location, int in){
        gl.glUniform1i(location,in);
    }

    /**
     * Stops {@code this} shader program.
     * @param gl 
     */
    public void stop(GL2 gl){
        gl.glUseProgram(0);
    }
    
    /**
     * Starts {@code this }shader program.
     * @param gl 
     */
    public void start(GL2 gl){
        gl.glUseProgram(id);
    }
    
    /**
     * Stops and deletes {@code this} program.
     * 
     * @param gl 
     */
    public void cleanUp(GL2 gl){
        stop(gl);
        gl.glDeleteProgram(id);
    }
    
    
}
