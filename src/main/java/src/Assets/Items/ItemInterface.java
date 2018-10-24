package src.Assets.Items;

import src.Assets.instance.Instance;
import src.Physics.PStructAction;
import src.Physics.Physics;

public interface ItemInterface {
    void perform(Instance instance, PStructAction pStruct,
                 Physics.ModPhysicsContext pc, Physics.ModState s);

    int getType();
}