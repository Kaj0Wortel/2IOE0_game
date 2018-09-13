
package game_2IOE0.OBJ;

// Jogamp imports
import game_2IOE0.GS;
import game_2IOE0.tools.MultiTool;
import game_2IOE0.tools.io.BufferedReaderPlus;
import game_2IOE0.tools.log.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static game_2IOE0.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static game_2IOE0.tools.io.BufferedReaderPlus.TYPE_CONFIG;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;

// Own imports
// Java imports



/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class LoadOBJ {
    final private static Map<String, OBJCollection> map
            = new ConcurrentHashMap<>();
    
    // Private constructor for static singleton design.
    private LoadOBJ() { }
    
    @SuppressWarnings({"null", "UnusedAssignment"})
    public static OBJCollection load(String fileName) {
        
        OBJCollection collection = map.get(fileName);
        if (collection != null) return collection;
        else collection = new OBJCollection();
        
        Logger.write(new String[] {
            "",
            "==== Begin reading OBJ file ====",
            "File = " + fileName
        }, Logger.Type.INFO);
        
        MTLCollection mtlCol = null;
        
        BufferedReaderPlus debug = null;
        try (BufferedReaderPlus brp = new BufferedReaderPlus(fileName,
                HASHTAG_COMMENT, TYPE_CONFIG)) {
            debug = brp;
            brp.setConfNameSeparator(" ");
            brp.setConfDataSeparator(" ");
            
            OBJObject obj = null;
            List<Vector3f> verts = new ArrayList<>();
            List<Vector2f> texs = new ArrayList<>();
            List<Vector3f> norms = new ArrayList<>();
            
            List<Float> vertsBuf = new ArrayList<>();
            List<Float> texsBuf = new ArrayList<>();
            List<Float> normsBuf = new ArrayList<>();
            List<Integer> facesBuf = new ArrayList<>();
            
            int locCounter = 0;
            
            Map<FaceElement, Integer> faces = new HashMap<>();
            
            while (brp.readNextConfLine()) {
                String[] data = brp.getFieldData();
                
                if (brp.fieldEquals("v")) {
                    verts.add(new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    ));
                    
                } else if (brp.fieldEquals("vt")) {
                    texs.add(new Vector2f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1])
                    ));
                    
                } else if (brp.fieldEquals("vn")) {
                    norms.add(new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    ));
                    
                } else if (brp.fieldEquals("f")) {
                    for (int i = 0; i < data.length; i++) {
                        String[] elems = data[i].split("/");
                        int vPointer = Integer.parseInt(elems[0]);
                        int tPointer = ("".equals(elems[1])
                                ? -1
                                : Integer.parseInt(elems[1]));
                        int nPointer = Integer.parseInt(elems[2]);
                        
                        FaceElement face
                                = new FaceElement(vPointer, tPointer, nPointer);
                        
                        Integer loc = faces.get(face);
                        // The location didn't exist before, so add a new
                        // location to the buffers.
                        if (loc == null) {
                            // Set the current face location to the next
                            // available slot.
                            loc = locCounter++;
                            
                            Vector3f vert = verts.get(vPointer);
                            vertsBuf.add(vert.x);
                            vertsBuf.add(vert.y);
                            vertsBuf.add(vert.z);
                            
                            Vector2f tex = null;
                            if (tPointer != -1) {
                                texsBuf.add(tex.x);
                                texsBuf.add(tex.y);
                            }
                            
                            Vector3f norm = norms.get(nPointer);
                            normsBuf.add(norm.x);
                            normsBuf.add(norm.y);
                            normsBuf.add(norm.z);
                        }
                        
                        // Add the position to the faces buffer.
                        facesBuf.add(loc);
                    }
                    
                } else if (brp.fieldEquals("o")) {
                    if (obj != null) {
                        obj.setData(vertsBuf, texsBuf, normsBuf, facesBuf);
                    }
                    
                    collection.add(obj = new OBJObject(data[0]));
                    
                } else if (brp.fieldEquals("usemtl")) {
                    obj.mltObject = mtlCol.get(data[0]);
                    
                } else if (brp.fieldEquals("mtllib")) {
                    mtlCol = LoadMTL.load(GS.OBJ_DIR + data[0]);
                    
                } else {
                    Logger.write("Ignored field: " + brp.getFieldName()
                            + ", line = " + brp.getLineCounter());
                }
            }
            
        } catch (IOException e) {
            Logger.write("Could not open file: " + fileName);
            
        } catch (IndexOutOfBoundsException e) {
            Logger.write("The file \"" + fileName + "\" is corrupt! [line = "
                    + debug.getLineCounter() + "]");
            
        } finally {
            Logger.write(new String[] {
                "==== Finished reading OBJ file ====",
                "File = " + fileName,
                ""
            }, Logger.Type.INFO);
        }
        
        map.put(fileName, collection);
        return collection;
    }
    
    /**
     * Clears all obj objects and collections and makes them available for GC.
     */
    public static void clear() {
        for (OBJCollection col : map.values()) {
            col.clear();
        }
        LoadMTL.clear();
    }
    
    public static void main(String[] args) {
        GS.init();
        MultiTool.sleepThread(100);
        LoadOBJ.load(GS.OBJ_DIR + "test.obj");
    }
    
}
