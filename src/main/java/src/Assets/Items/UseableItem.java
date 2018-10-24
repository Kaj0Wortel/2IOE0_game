package src.Assets.Items;

import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.Physics;

import static src.Assets.Items.UseableItem.ITEM.*;

public class UseableItem implements ItemInterface {

    enum ITEM {SLOWDOWN, SPEEDUP, INVERTKEYS}

    private ITEM type;

    public UseableItem(){
        switch (GS.rani(0,3)){
            case 0:
                type = SLOWDOWN;
                break;
            case 1:
                type = SPEEDUP;
                break;
            case 2:
                type = INVERTKEYS;
                break;
            default:
                type = SPEEDUP;
                break;
        }
    }

    @Override
    public void activate(Instance instance, PStructAction pStruct, Physics.ModPhysicsContext pc, Physics.ModState s) {

    }

    @Override
    public void perform(Instance instance, PStructAction pStruct, Physics.ModPhysicsContext pc, Physics.ModState s){

    }


    public ITEM getItem() {
        return type;
    }

    @Override
    public String toString(){
        return type.toString();
    }
}
