
package src.Assets.instance;

import com.jogamp.opengl.GL3;
import org.joml.Vector3f;
import src.Assets.Items.ItemInterface;
import src.Assets.OBJCollection;
import src.Assets.OBJTexture;
import src.Assets.TextureImg;
import src.Assets.instance.Instance.State;
import src.Controllers.ItemController;
import src.GS;
import src.OBJ.LoadOBJ;
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;
import src.tools.log.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


// Own imports


// Java imports


/**
 * 
 */
public class ThrowingItemFactory {
    
    public static enum ItemType {
        GREEN_SHELL, RED_SHELL;
    }
    
    final private static Map<ItemType, ThrowingItem> sourceMap
             = new EnumMap<ItemType, ThrowingItem>(ItemType.class);
    
    private static boolean initCompleted = false;
    
    /**
     * Private constructor for static singleton design pattern.
     */
    private ThrowingItemFactory() { }
    
    
    public static ThrowingItem createItem(ItemType type, Car source) {
        return createItem(type, source.getState().box.pos(), source, null);
    }
    
    public static ThrowingItem createItem(ItemType type, Vector3f pos,
            Car source, Car target) {
        checkInit();
        if (type == null) return null;
        
        if (type == ItemType.RED_SHELL) {
            if (target == null) {
                for (Car car : GS.cars) {
                    if (car != source) {
                        target = car;
                        break;
                    }
                }
            }
            
            
        } else {
            target = null;
        }
        
        ThrowingItem item = sourceMap.get(type).clone();
        State is = item.getState();
        State ss = source.getState();
        item.setState(new State(new PosHitBox3f(
                new Vector3f(pos).add(new Vector3f(0, 5, 10)),
                new Vector3f(is.box.relPos()),
                new Vector3f(is.box.dim())),
                is.sizex, is.sizey, is.sizez,
                ss.rotx, ss.roty, ss.rotz,
                ss.internRotx, ss.internRoty, ss.internRotz,
                ss.internTrans, ss.velocity, 0, ss.verticalVelocity,
                true, false, 0, false, null, new CopyOnWriteArrayList<ItemInterface>())
        );
        item.setProgressManager(source.getProgressManager().clone());
        
        Logger.write(item.getState().isResetting);
        GS.addItem(item);
        new ItemController(item, source, target);
        
        return item;
    }
    
    /**
     * Checks whether the factory has been initialized.
     */
    private static void checkInit() {
        if (!initCompleted) throw new IllegalStateException(
                "Factory was not initialized.");
    }
    
    
    /**
     * Loads the models needed for all items.
     */
    public synchronized static void init(GL3 gl) {
        if (initCompleted) return;
        if (!sourceMap.isEmpty()) {
            sourceMap.clear();
        }
        
        OBJCollection col;
        OBJTexture texture;
        PosHitBox3f box;
        ThrowingItem item;
        
        // Test item cube: green shell
        col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        texture = new OBJTexture(col, new TextureImg(gl, "item_block.png", 5, 3f));
        box = col.createBoundingBox();
        item = new ThrowingItem(box, 1, 1, 1,
                0, -180, 0, texture,
                0, 0, 0, new PhysicsContext(),
                false); // TODO: change phycsics
        sourceMap.put(ItemType.GREEN_SHELL, item);
        
        // Test item cube.
        col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        texture = new OBJTexture(col, new TextureImg(gl, "item_block.png", 5, 3f));
        box = col.createBoundingBox();
        item = new ThrowingItem(box, 1, 1, 1,
                0, -180, 0, texture,
                0, 0, 0, new PhysicsContext(),
                false); // TODO: change phycsics
        sourceMap.put(ItemType.RED_SHELL, item);
        
        // TODO: add stuff
        
        initCompleted = true;
    }
    
    
}
