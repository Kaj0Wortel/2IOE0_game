
package src.Assets.instance;


// Own imports
import javax.swing.SwingUtilities;
import src.Assets.OBJTexture;
import src.GS;
import src.Physics.PhysicsContext;
import src.grid.GridItem;


// Java imports
import org.joml.Vector3f;
import src.Physics.PStructAction;
import src.tools.PosHitBox3f;


/**
 * 
 */
public abstract class GridItemInstance
        extends Instance
        implements GridItem {
    
    protected Vector3f prevPosition = null;
    
    
    public GridItemInstance(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation, 
                physicConst);
        SwingUtilities.invokeLater(() -> {
            GS.grid.update(this);
        });
    }
    
    public GridItemInstance(PosHitBox3f box,
            float sizex, float sizey, float sizez,
            float rotx, float roty, float rotz, OBJTexture model,
            float internRotx, float internRoty, float internRotz,
            PhysicsContext physicConst) {
        super(box, sizex, sizey, sizez, rotx, roty, rotz, model,
                internRotx, internRoty, internRotz, physicConst);
        SwingUtilities.invokeLater(() -> {
            GS.grid.update(this);
        });
    }
    
    
    @Override
    public Vector3f getCurPosition() {
        return state.box.pos();
    }
    
    @Override
    public Vector3f getPrevPosition() {
        return prevPosition;
    }
    
    @Override
    public char getSimpleRepr() {
        return '-';
    }
    
    @Override
    public void setState(State state) {
        prevPosition = state.box.pos();
        super.setState(state);
        GS.grid.update(this);
    }
    
    @Override
    public void movement(PStructAction pStruct) {
        super.movement(pStruct);
    }
    
    
}
