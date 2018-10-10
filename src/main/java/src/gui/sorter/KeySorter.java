
package src.gui.sorter;


// Own imports
import src.gui.menuList.ConfigKeyMenuItem;


// Java imports
import java.util.Arrays;


/**
 * 
 */
public class KeySorter
        extends Sorter<ConfigKeyMenuItem> {
    
    
    public KeySorter() {
        super(Arrays.<Rule<ConfigKeyMenuItem>>asList(
                new Rule<ConfigKeyMenuItem>("Player", (ConfigKeyMenuItem item1,
                        ConfigKeyMenuItem item2) -> {
                    int id1 = item1.getAction().getID();
                    int id2 = item2.getAction().getID();
                    return Integer.compare(id1, id2);
                }),
                new Rule<ConfigKeyMenuItem>("Action", (ConfigKeyMenuItem item1,
                        ConfigKeyMenuItem item2) -> {
                    String name1 = item1.getAction().toString();
                    String name2 = item2.getAction().toString();
                    return name1.compareTo(name2);
                }),
                new Rule<ConfigKeyMenuItem>("Controller type",
                        (ConfigKeyMenuItem item1, ConfigKeyMenuItem item2) -> {
                    boolean isNull1 = item1.getKey().getIdentifier() == null;
                    boolean isNull2 = item2.getKey().getIdentifier() == null;
                    if (isNull1 ^ isNull2) {
                        return (isNull1 ? -1 : 1);
                        
                    } else if (isNull1 || isNull2) {
                        return 0;
                        
                    } else {
                        String name1 = item1.getKey().getIdentifier().getName();
                        String name2 = item2.getKey().getIdentifier().getName();
                        return name1.compareTo(name2);
                    }
                }),
                new Rule<ConfigKeyMenuItem>("Controller name",
                        (ConfigKeyMenuItem item1, ConfigKeyMenuItem item2) -> {
                    String name1 = item1.getKey().getController().getName();
                    String name2 = item2.getKey().getController().getName();
                    return name1.compareTo(name2);
                })
        ));
    }
    
    
}
