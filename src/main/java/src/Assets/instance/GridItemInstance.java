
package src.Assets.instance;


// Own imports
import src.Assets.OBJTexture;
import src.GS;
import src.Physics.PhysicsContext;
import src.grid.GridItem;


// Java imports
import org.joml.Vector3f;
import src.Physics.PStructAction;
import src.tools.Box3f;


/**
 * 
 */
public abstract class GridItemInstance
        extends Instance
        implements GridItem {
    
    protected Vector3f prevPosition = null;

    public GridItemInstance(Box3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation, 
                physicConst);
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
