package src.Assets.Items;

import src.Assets.instance.Instance;
import src.Physics.PStructAction;
import src.Physics.Physics;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;

public interface ItemInterface {
    void perform(Instance instance, PStructAction pStruct,
                 Physics.ModPhysicsContext pc, Physics.ModState s);

    public void activate(Instance instance, PStructAction pStruct,
            ModPhysicsContext pc, ModState s);
    
    int getType();
}