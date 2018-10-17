
package src.OBJ;

// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.OBJCollection;
import src.Assets.OBJObject;
import src.GS;
import src.tools.io.BufferedReaderPlus;
import src.tools.log.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static src.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CONFIG;

// Java imports
// Own imports



/**
 * 
 */
public class LoadOBJ {
    final private static Map<String, OBJCollection> map
            = new ConcurrentHashMap<>();
    
    // Private constructor for static singleton design pattern.
    private LoadOBJ() { }
    
    
    @SuppressWarnings({"null", "UnusedAssignment"})
    public static OBJCollection load(GL3 gl, String fileName) {
        
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
            MTLObject mtl = null;
            List<Vector3f> verts = new ArrayList<>();
            List<Vector2f> texs = new ArrayList<>();
            List<Vector3f> norms = new ArrayList<>();
            
            List<Float> vertsBuf = new ArrayList<>();
            List<Float> texsBuf = new ArrayList<>();
            List<Float> normsBuf = new ArrayList<>();
            List<Integer> facesBuf = new ArrayList<>();
            
            float minX = 0;
            float maxX = 0;
            float minY = 0;
            float maxY = 0;
            float minZ = 0;
            float maxZ = 0;
            
            int locCounter = 0;
            
            Map<FaceElement, Integer> faces = new HashMap<>();
            
            while (brp.readNextConfLine()) {
                String[] data = brp.getData();
                
                if (brp.fieldEquals("v")) {
                    Vector3f v = new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    );
                    verts.add(v);
                    minX = Math.min(minX, v.x);
                    maxX = Math.max(maxX, v.x);
                    minY = Math.min(minY, v.y);
                    maxY = Math.max(maxY, v.y);
                    minZ = Math.min(minZ, v.z);
                    maxZ = Math.max(maxZ, v.z);
                    
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
                        int vPointer = Integer.parseInt(elems[0]) - 1;
                        int tPointer = ("".equals(elems[1])
                                ? -1
                                : Integer.parseInt(elems[1]) - 1);
                        int nPointer = Integer.parseInt(elems[2]) - 1;

                        FaceElement face
                                = new FaceElement(vPointer, tPointer, nPointer);
                        
                        Integer loc = faces.get(face);
                        // tmp hot-fix
                        
                        
                        // The location didn't exist before, so add a new
                        // location to the buffers.
                        if (loc == null) {
                            // Set the current face location to the next
                            // available slot.
                            loc = locCounter++;
                            
                            // Since this face element doesn't not yet exist,
                            // add it to the map.
                            faces.put(face, loc);
                            
                            Vector3f vert = verts.get(vPointer);
                            vertsBuf.add(vert.x);
                            vertsBuf.add(vert.y);
                            vertsBuf.add(vert.z);
                            
                            if (tPointer != -1) {
                                Vector2f tex = texs.get(tPointer);
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
                        obj.addData(gl, vertsBuf, texsBuf, normsBuf,
                                facesBuf, mtl);
                        obj.setMinMax(minX, maxX, minY, maxY, minZ, maxZ);
                        
                        vertsBuf = new ArrayList<>();
                        texsBuf = new ArrayList<>();
                        normsBuf = new ArrayList<>();
                        facesBuf = new ArrayList<>();
                        faces.clear();
                        locCounter = 0;
                    }
                    collection.add(obj = new OBJObject(data[0]));
                    
                } else if (brp.fieldEquals("usemtl")) {
                    if (mtl != null) {
                        obj.addData(gl, vertsBuf, texsBuf, normsBuf,
                                facesBuf, mtl);
                        obj.setMinMax(minX, maxX, minY, maxY, minZ, maxZ);
                        
                        vertsBuf = new ArrayList<>();
                        texsBuf = new ArrayList<>();
                        normsBuf = new ArrayList<>();
                        facesBuf = new ArrayList<>();
                        faces.clear();
                        locCounter = 0;
                    }
                    mtl = mtlCol.get(data[0]);
                    
                } else if (brp.fieldEquals("mtllib")) {
                    mtlCol = LoadMTL.load(GS.OBJ_DIR + data[0]);
                    
                } else {
                    Logger.write("Ignored field: " + brp.getField()
                            + ", line = " + brp.getLineCounter());
                }
            }
            if (obj != null) {
                obj.addData(gl, vertsBuf, texsBuf, normsBuf, facesBuf, mtl);
                obj.setMinMax(minX, maxX, minY, maxY, minZ, maxZ);
            }
            
        } catch (IOException e) {
            Logger.write("Could not open file: " + fileName,
                    Logger.Type.ERROR);

        } catch (IndexOutOfBoundsException e) {
            Logger.write("The file \"" + fileName + "\" is corrupt! [line = "
                    + debug.getLineCounter() + "]", Logger.Type.ERROR);

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
    
}
