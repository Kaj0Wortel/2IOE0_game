
package src.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import src.GS;
import src.gui.filter.KeyFilter;
import src.gui.menuList.ConfigKeyMenuHeader;
import src.gui.menuList.ConfigKeyMenuItem;
import src.gui.menuList.MenuLister;
import src.gui.sorter.KeySorter;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.KeyAction;


// Own imports


// Java imports


/**
 * 
 */
public class ConfigKeysPanel
        extends ListingPanel<ConfigKeyMenuItem, MenuLister<ConfigKeyMenuItem>> {
    
    public ConfigKeysPanel() {
        super("key config", new MenuLister<ConfigKeyMenuItem>(
                new ConfigKeyMenuHeader(
                    new Integer[] {
                        40, 40, 40, 80, 120, 40
                    })),
                new KeySorter(),
                new KeyFilter()
        );
        
        
    }
    
    
    private void init() {
        lister.removeAllItems();
        
        List<ConfigKeyMenuItem> items = new ArrayList<>();
        
        Iterator<Map.Entry<KeyAction, List<ControllerKey>>> it
                = GS.getKeyIterator();
        while (it.hasNext()) {
            Map.Entry<KeyAction, List<ControllerKey>> entry = it.next();
            KeyAction action = entry.getKey();
            for (ControllerKey key : entry.getValue()) {
                items.add(new ConfigKeyMenuItem(action, key));
            }
        }
        
        lister.addAllItems(items);
        lister.revalidate();
        lister.repaint();
        
        if (filter != null) filter.updateContents();
    }
    
    @Override
    public void loadAction() {
        GS.reloadKeyMap();
        init();
        lister.reload();
    }
    
    @Override
    public void saveAction() {
        // TODO
    }
    
    @Override
    public void userDeletedAction(List deleted) {
        // TODO
    }
    
    @Override
    public void userAddItem() {
        List<Enum> actions = KeyAction.getAllActions();
        Enum[] options = actions.toArray(new Enum[actions.size()]);
        Enum option = null;
        do {
            option = (Enum) JOptionPane.showInputDialog(this, 
                    "Select action",
                    "Adding new key binding",
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, 
                    options[0]);
        } while (option == null);
        
        int id = 0;
        Integer[] players = new Integer[GS.MAX_PLAYERS];
        for (int i = 0; i < GS.MAX_PLAYERS; i++) {
            players[i] = i + 1;
        }
        
        do {
            id = (Integer) JOptionPane.showInputDialog(this, 
                    "Select player",
                    "Adding new key binding",
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    players, 
                    players[0]);
            
        } while (id != 0);
        
        KeyAction keyAction = KeyAction.createKeyAction(id, option);
        lister.addItem(new ConfigKeyMenuItem(keyAction, null));
        SwingUtilities.invokeLater(() -> {
            lister.setPage(lister.getTotalPages());
            lister.toBottom();
            revalidate();
            repaint();
        });
    }
    
    
}
