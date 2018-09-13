
package src.OBJ;

// Jogamp imports

import src.GS;
import src.tools.MultiTool;
import src.tools.io.BufferedReaderPlus;
import src.tools.log.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static src.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CONFIG;

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
            while (brp.readNextConfLine()) {
                String[] data = brp.getFieldData();
                
                if (brp.fieldEquals("v")) {
                    obj.geomVert.add(Float.parseFloat(data[0]));
                    obj.geomVert.add(Float.parseFloat(data[1]));
                    obj.geomVert.add(Float.parseFloat(data[2]));
                    
                } else if (brp.fieldEquals("vt")) {
                    obj.texVert.add(Float.parseFloat(data[0]));
                    obj.texVert.add(Float.parseFloat(data[1]));
                    obj.texVert.add(Float.parseFloat(data[2]));

                    
                } else if (brp.fieldEquals("vn")) {
                    obj.normVert.add(Float.parseFloat(data[0]));
                    obj.normVert.add(Float.parseFloat(data[1]));
                    obj.normVert.add(Float.parseFloat(data[2]));
                    /*
                } else if (brp.fieldEquals("vp")) {
                    obj.paramVert.add(new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    ));
                    */
                } else if (brp.fieldEquals("f")) {
                    int[] vert = new int[data.length];
                    int[] tex = new int[data.length];
                    int[] norm = new int[data.length];
                    
                    for (int i = 0; i < data.length; i++) {
                        String[] elems = data[i].split("/");
                        vert[i] = Integer.parseInt(elems[0]);
                        tex[i] = ("".equals(elems[1])
                                ? 0
                                : Integer.parseInt(elems[1]));
                        norm[i] = Integer.parseInt(elems[2]);
                    }
                    obj.faceList.add(new FaceElement(vert, tex, norm));
                    
                } else if (brp.fieldEquals("o")) {
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
