
package game_2IOE0.OBJ;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


// Own imports


// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class OBJObject {
    final private String name;
    
    /**
     * <url>http://paulbourke.net/dataformats/obj/</url>
     */
    List<Float> geomVert = new ArrayList<>();
    List<Float> texVert = new ArrayList<>();
    List<Float> normVert = new ArrayList<>();
    List<FaceElement> faceList = new ArrayList<>();
    
    MTLObject mltObject = null;
    
    
    OBJObject(String name) {
        this.name = name;
    }
    
    IntBuffer vboIDs = null;


    /*
    public void bind(GL2 gl) {
        vboIDs = Renderer.createVBOIDs(gl, 3);
        
        // Create vertex VBO buffer.
        FloatBuffer vertexBuffer
                = Buffers.newDirectFloatBuffer(geomVert.size() * 3);
        for (int i = 0; i < geomVert.size(); i++) {
            Vector3f vec = geomVert.get(i);
            vertexBuffer.put(vec.x);
            vertexBuffer.put(vec.y);
            vertexBuffer.put(vec.z);
        }
        Renderer.bindVBO(gl, vboIDs.get(0), 0, 3, geomVert.size() * 3, false,
                vertexBuffer);

        // Create texture VBO buffer.
        FloatBuffer texBuffer
                = Buffers.newDirectFloatBuffer(texVert.size() * 3);
        for (int i = 0; i < texVert.size(); i++) {
            Vector3f vec = texVert.get(i);
            texBuffer.put(vec.x);
            texBuffer.put(vec.y);
            texBuffer.put(vec.z);
        }
        Renderer.bindVBO(gl, vboIDs.get(1), 1, 3, texVert.size() * 3, false,
                texBuffer);
        
        // Create normal VBO buffer.
        FloatBuffer normBuffer
                = Buffers.newDirectFloatBuffer(normVert.size() * 3);
        for (int i = 0; i < normVert.size(); i++) {
            Vector3f vec = normVert.get(i);
            normBuffer.put(vec.x);
            normBuffer.put(vec.y);
            normBuffer.put(vec.z);
        }
        Renderer.bindVBO(gl, vboIDs.get(2), 2, 3, normVert.size() * 3, false,
                normBuffer);
        
        
        /*
        // Create vertex VAO buffers.
        FaceElement face = faceList.get(0);
        int amt = face.getVert().length * 3;
        IntBuffer vertexVAO
                = Buffers.newDirectIntBuffer(faceList.size() * amt);
        IntBuffer texVAO
                = Buffers.newDirectIntBuffer(faceList.size() * amt);
        IntBuffer normVAO
                = Buffers.newDirectIntBuffer(faceList.size() * amt);
        for (int i = 0; i < faceList.size(); i++) {
            face = faceList.get(0);
            int[] vert = face.getVert();
            int[] tex = face.getTex();
            int[] norm = face.getNorm();
            for (int j = 0; j < amt; j++) {
                vertexVAO.put(vert[j]);
                texVAO.put(tex[j]);
                normVAO.put(norm[j]);
            }
        }
        
        IntBuffer vaoIDs = Renderer.createVAOID(gl, 3);
        /*
        Renderer.bindVAO(gl, vboIDs.get(1), 0, 3, faceList.size() * amt, false,
                vertexBuffer);
        Renderer.bindVAO(gl, vboIDs.get(2), 0, 3, faceList.size() * amt, false,
                vertexBuffer);
        Renderer.bindVAO(gl, vboIDs.get(3), 0, 3, faceList.size() * amt, false,
                vertexBuffer);
        */
        
        /*
        if (VBOID != null || VAOID != null) return;
        VAOID = Renderer.createVAOID(gl);
        VBOID = Renderer.createVBOID(gl);
        
        FloatBuffer vertexBuffer
                = Buffers.newDirectFloatBuffer(geomVert.size() * 3);
        for (int i = 0; i < geomVert.size(); i++) {
            Vector3f vec = geomVert.get(i);
            vertexBuffer.put(vec.x);
            vertexBuffer.put(vec.y);
            vertexBuffer.put(vec.z);
        }
        Renderer.bindVAO(gl, VBOID, vertexBuffer);
        
        // TODO:
        IntBuffer faceBuffer
                = Buffers.newDirectIntBuffer(faceList.size() * 3);
        for (int i = 0; i < faceList.size(); i++) {
            FaceElement fe = faceList.get(i);
            faceBuffer.put(fe.getVert());
            faceBuffer.put(fe.getTex());
            faceBuffer.put(fe.getNorm());
        }
        Renderer.bindVBO(gl, VBOID, geomVert.size(), vertexBuffer);
        
        /*
        gl.glGenVertexArrays(VBOID, vertexArray);
        gl.glBindVertexArray(0);
        
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, 0, 0);
        
        gl.glEnableVertexAttribArray(VBOID);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
        gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, true, 0, 0);
        
        */
        /*
        // Create and bind VBO
        gl.glGenBuffersARB(1, VBOVertices, 0);  // Get A Valid Name
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOVertices[0]);  // Bind The Buffer

        // Load The Data
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3 * 
                BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW_ARB);

        // Generate And Bind The Texture Coordinate Buffer
        gl.glGenBuffersARB(1, VBOTexCoords, 0);  // Get A Valid Name
        gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, VBOTexCoords[0]); // Bind The Buffer
        // Load The Data
        gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 2 * 
                BufferUtil.SIZEOF_FLOAT, texCoords, GL.GL_STATIC_DRAW_ARB);

        // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
        clear();
        */

    public void unbind(GL2 gl) {
        
    }
    
    public void draw(GL2 gl, GLU glu) {
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
        
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);
        
        gl.glBindVertexArray(0);
        gl.glBindVertexArray(1);
        gl.glBindVertexArray(2);
    }
    
    /**
     * Clears this obj object and makes it available for GC.
     */
    void clear() {
        geomVert.clear();
        texVert.clear();
        normVert.clear();
        faceList.clear();
        
        geomVert = null;
        texVert = null;
        normVert = null;
        faceList = null;
        mltObject = null;
    }
    
    public void setData(List<Float> vertices, List<Float> normals,
            List<Float> tex, List<Integer> indices) {
    }
    
    public float[] getAsFloats(List<Float> lst){
        float[] floats = new float[lst.size()];
        for(int i = 0; i < lst.size(); i++){
            floats[i] = lst.get(i);
        }

        return floats;
    }

    /*
    private void processFaceElements(){
        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> tex = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for(FaceElement face : faceList){
            for(int i = 0; i < face.getVert().length; i++){
                processVertex(vertices,normals,tex,indices,i,face);
            }

        }

    }

    private void processVertex(List<Float> vertices, List<Float> normals, List<Float> tex, List<Integer> indices, int vertexNum, FaceElement face){
        int vertIndex = face.getVert()[vertexNum] - 1;
        if(vertices.get(vertIndex) != null) {
            vertIndex = geomVert.size()/3;
            geomVert.add(geomVert.get(face.getVert()[vertexNum]));
            geomVert.add(geomVert.get(face.getVert()[vertexNum+1]));
            geomVert.add(geomVert.get(face.getVert()[vertexNum+2]));
        }
        vertices.add(vertIndex, geomVert.get(face.getVert()[vertexNum]));
        vertices.add(vertIndex+1, geomVert.get(face.getVert()[vertexNum+1]));
        vertices.add(vertIndex+2, geomVert.get(face.getVert()[vertexNum+2]));
        normals.add(vertIndex, normVert.get(face.getNorm()[vertexNum]));
        normals.add(vertIndex+1, normVert.get(face.getNorm()[vertexNum+1]));
        normals.add(vertIndex+2, normVert.get(face.getNorm()[vertexNum+2]));
        tex.add(vertIndex, texVert.get(face.getVert()[vertexNum]));
        tex.add(vertIndex+1, texVert.get(face.getVert()[vertexNum+1]));
        indices.add(vertIndex);
    }*/
}
