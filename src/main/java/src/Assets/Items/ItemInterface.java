package src.Items;

import src.Assets.instance.Instance;
import src.Physics.PStructAction;
import src.Physics.Physics;

public interface ItemInterface {
    void activate(Instance instance, PStructAction pStruct,
            Physics.ModPhysicsContext pc, Physics.ModState s);

    void perform(Instance instance, PStructAction pStruct,
            Physics.ModPhysicsContext pc, Physics.ModState s);
}