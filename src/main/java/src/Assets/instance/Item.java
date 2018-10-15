
package src.Assets.instance;


// Java imports
import javax.swing.SwingUtilities;

// Own imports
import src.Assets.OBJTexture;
import src.GS;
import src.Locker;
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
            OBJTexture model, float internRotation,
            PhysicsContext physicConst) {
        this(box, size, rotx, roty, rotz, model, internRotation,
                physicConst, true);
    }
    
    public Item(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst, boolean addToUpdater) {
        this(box, size, size, size, rotx, roty, rotz, model,
                0, integratedRotation, 0, physicConst, addToUpdater);
    }
    
    public Item(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst, boolean addToUpdater) {
        this(box, sizex, sizey, sizez, rotx, roty, rotz, model,
                0, integratedRotation, 0, physicConst, addToUpdater);
    }
    
    public Item(PosHitBox3f box, float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz, OBJTexture model,
            float internRotx, float internRoty, float internRotz,
            PhysicsContext physicConst, boolean addToUpdater) {
        super(box, sizex, sizey, sizez, rotx, roty, rotz, model,
                internRotx, internRoty, internRotz, physicConst);
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
        updateItem(dt);
        prevTimeStamp = timeStamp;
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
    
    
    @Override
    public void destroy() {
        super.destroy();
        GS.removeItem(this);
    }
    
}
