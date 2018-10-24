
package src.Assets.instance;


// Own imports

import javax.swing.SwingUtilities;
import src.Assets.OBJTexture;
import static src.Assets.instance.MaterialInstance.Type.SPACE_ROCK;
import src.GS;
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;
import src.tools.update.Updateable;
import src.tools.update.Updater;

// Java imports


/**
 * 
 */
public class MaterialInstance
        extends Instance
        implements Updateable {
    
    public static enum Type {
        SPACE_ROCK, PLANET, BANNER;
    }
    
    final private Type type;
    final private float ranRotx = GS.ranf(0.05f, 0.25f);
    final private float ranRoty = GS.ranf(0.05f, 0.25f);
    final private float ranRotz = GS.ranf(0.05f, 0.25f);
    
    
    public MaterialInstance(PosHitBox3f box, float size,
                            float rotx, float roty, float rotz, OBJTexture model,
                            float internRotx, float internRoty, float internRotz,
                            PhysicsContext physicConst, Type type) {
        super(box, size, size, size, rotx, roty, rotz, model,
                internRotx, internRoty, internRotz, physicConst);
        this.type = type;
        
        if (type == Type.PLANET || GS.R.nextFloat() < 0.5) {
            SwingUtilities.invokeLater(() -> {
                Updater.addTask(this);
            });
        }
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public void performUpdate(long timeStamp)
            throws InterruptedException {
        long dt = timeStamp - prevTimeStamp;
        
        if (type == SPACE_ROCK) {
            rotate(ranRotx, ranRoty, ranRotz);
        } else if (type == Type.PLANET) {
            rotate(0.2f, 0.2f, 0.2f);
        }
        
        prevTimeStamp = timeStamp;
    }
    
    @Override
    public void ignoreUpdate(long timeStamp)
            throws InterruptedException {
        prevTimeStamp = timeStamp;
    }
    
    @Override
    public Priority getPriority() {
        return Priority.ONLY_WHEN_RUNNING;
    }
    
    
}
