
package src.Assets.Items;

// Java imports
import java.util.EnumMap;

// Own imports
import static src.Assets.Items.UseableItem.ITEM.INVERTKEYS;
import static src.Assets.Items.UseableItem.ITEM.SLOWDOWN;
import static src.Assets.Items.UseableItem.ITEM.SPEEDUP;
import src.Assets.instance.Instance;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.Physics.ModPhysicsContext;
import src.Physics.Physics.ModState;
import src.music.MusicManager;
import src.tools.log.Logger;


public class UseableItem
        implements ItemInterface {

    public enum ITEM {
        SPEEDUP, SLOWDOWN, INVERTKEYS;
    }
    final private static EnumMap<ITEM, Float> animationTimes
            = new EnumMap<>(ITEM.class);

    static {
        animationTimes.put(SLOWDOWN, 10f);
        animationTimes.put(SPEEDUP, 2f);
        animationTimes.put(INVERTKEYS, 50f);
    }

    private ITEM type;
    private float timer = 0;
    private static boolean slow = false;
    private static boolean fast = false;
    private static boolean confuse = false;


    public UseableItem() {
        int i = GS.rani(0,2);
        System.out.println(i);
        switch (i){
            case 0:
                type = SLOWDOWN;
                slow = true;
                break;
            case 1:
                type = SPEEDUP;
                fast = true;
                break;
            case 2:
                type = INVERTKEYS;
                confuse = true;
                break;
            default:
                type = SLOWDOWN;
                slow = true;
                break;
        }
    }

    @Override
    public void activate(Instance instance, PStructAction pStruct,
            ModPhysicsContext pc, ModState s) {
        switch(type){
            case SPEEDUP:{
                pc.linAccel += 2f;// not refined
                pc.maxLinearVelocity = 100;
                if (fast) {
                    fast = false;
                    MusicManager.play("go_fast.wav", MusicManager.MUSIC_SFX);
                }
                break;
            }
            case SLOWDOWN:{
                s.velocity -= 0.15f;
                if (slow) {
                    slow = false;
                    MusicManager.play("too_slow.wav", MusicManager.MUSIC_SFX);
                }
                break;
            }
            case INVERTKEYS:{
                pStruct.turn *= -1;
                if (confuse) {
                    confuse = false;
                    MusicManager.play("trol.wav", MusicManager.MUSIC_SFX);
                }
                break;
            }
        }
    }

    @Override
    public void perform(Instance instance, PStructAction pStruct,
            ModPhysicsContext pc, ModState s) {
        float dt = pStruct.dt / 160f;
        timer += dt;

        if (timer > animationTimes.get(type)) {
            s.activeItems.remove(this);
            Logger.write(s.activeItems);
        } else {
            activate(instance,pStruct,pc,s);
            Logger.write(s.activeItems);
        }
    }


    public ITEM getItem() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public int getType() {
        return type.ordinal();
    }

}
