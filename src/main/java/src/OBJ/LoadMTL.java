
package src.OBJ;

// Java imports
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// Own imports
import static src.tools.io.BufferedReaderPlus.HASHTAG_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CONFIG;
import src.tools.io.BufferedReaderPlus;
import src.tools.log.Logger;


/**
 * 
 */
public class LoadMTL {
    final private static Map<String, MTLCollection> map
            = new ConcurrentHashMap<>();
    
    
    @SuppressWarnings({"null", "UnusedAssignment"})
    public static MTLCollection load(String fileName) {
        MTLCollection collection = map.get(fileName);
        if (collection != null) return collection;
        collection = new MTLCollection();
        
        Logger.write(new String[] {
            "",
            "==== Begin reading MTL file ====",
            "File = " + fileName
        }, Logger.Type.INFO);
        
        BufferedReaderPlus debug = null;
        try (BufferedReaderPlus brp = new BufferedReaderPlus(fileName,
                HASHTAG_COMMENT, TYPE_CONFIG)) {
            debug = brp;
            brp.setConfNameSeparator(" ");
            brp.setConfDataSeparator(" ");
            
            MTLObject mtl = null;
            while (brp.readNextConfLine()) {
                String[] data = brp.getData();
                
                if (brp.fieldEquals("newmtl")) {
                    collection.add(mtl = new MTLObject(data[0]));
                    
                } else if (brp.fieldEquals("Ka")) {
                    mtl.ambiant = new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    );
                    
                } else if (brp.fieldEquals("Kd")) {
                    mtl.diffuse = new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    );
                    
                } else if (brp.fieldEquals("Ks")) {
                    mtl.specular = new Vector3f(
                            Float.parseFloat(data[0]),
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    );
                    
                } else if (brp.fieldEquals("illum")) {
                    mtl.illumination = Integer.parseInt(data[0]);
                    
                } else if (brp.fieldEquals("d")) {
                    mtl.dissolve = Float.parseFloat(data[0]);
                    
                } else if (brp.fieldEquals("Ni")) {
                    mtl.opticalDensity = Float.parseFloat(data[0]);
                    
                } else {
                    Logger.write("Ignored field: " + brp.getField()
                            + ", line = " + brp.getLineCounter());
                }
            }
            
        } catch (IOException e) {
            Logger.write("Could not open file: " + fileName, Logger.Type.ERROR);
            
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            Logger.write(new Object[] {
                "The file \"" + fileName + "\" is corrupt! [line = "
                        + debug.getLineCounter() + "]",
                "Error: ",
                e
            }, Logger.Type.ERROR);
            
        } finally {
            Logger.write(new String[] {
                "==== Finished reading MTL file ====",
                "File = " + fileName,
                ""
            }, Logger.Type.INFO);
        }
        
        map.put(fileName, collection);
        return collection;
    }
    
    @SuppressWarnings("IncompatibleEquals")
    public static MTLObject load(String fileName, String name) {
        MTLCollection collection = load(fileName);
        for (MTLObject mtl : collection) {
            if (mtl.equals(name)) return mtl;
        }
        
        return null;
    }
    
    
    /**
     * Clears all mtl objects and collections and makes them available for GC.
     */
    static void clear() {
        for (MTLCollection col : map.values()) {
            col.clear();
        }
    }
    
}
