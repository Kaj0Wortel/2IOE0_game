
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
        extends GridItemInstance
        implements Updateable {
    
    public static enum Type {
        SPACE_ROCK;
    }
    
    final private Type type;
    final private float ranRotx = GS.ranf(0.05f, 0.25f);
    final private float ranRoty = GS.ranf(0.05f, 0.25f);
    final private float ranRotz = GS.ranf(0.05f, 0.25f);
    
    public MaterialInstance(PosHitBox3f box, float size,
                            float rotx, float roty, float rotz,
                            OBJTexture model, float integratedRotation,
                            PhysicsContext physicConst, Type type) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
        this.type = type;
        
        if (GS.R.nextFloat() < 0.5) {
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
