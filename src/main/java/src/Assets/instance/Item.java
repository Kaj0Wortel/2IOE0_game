
package src.Assets.instance;


// Java imports
import javax.swing.SwingUtilities;

// Own imports
import src.Assets.OBJTexture;
import src.Physics.PStructAction;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.tools.PosHitBox3f;
import src.tools.update.Updateable;
import src.tools.update.Updater;


/**
 * 
 */
public abstract class Item
        extends GridItemInstance
        implements Updateable {
    
    public Item(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        this(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst, true);
    }
    
    public Item(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst, boolean addToUpdater) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
        if (addToUpdater) {
            SwingUtilities.invokeLater(() -> {
                Updater.addTask(this);
            });
        }
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Is final since it should not be overridden.
     */
    @Override
    final public void performUpdate(long timeStamp) {
        long dt = timeStamp - prevTimeStamp;
        prevTimeStamp = timeStamp;
        updateItem(dt);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Is final since it should not be overridden.
     */
    @Override
    final public void ignoreUpdate(long timeStamp) {
        long dt = timeStamp - prevTimeStamp;
        prevTimeStamp = timeStamp;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Is final since it should not be overridden.
     */
    @Override
    final public void update(long timeStamp)
            throws InterruptedException {
        Updateable.super.update(timeStamp);
    }
    
    @Override
    public Updateable.Priority getPriority() {
        return Updateable.Priority.ONLY_WHEN_RUNNING;
    }
    
    public abstract void physicsAtCollision(Instance source,
            PStructAction pStruct, ModPhysicsContext physConst, ModState s);
    
    
    public abstract void updateItem(long dt);
    
    
}
