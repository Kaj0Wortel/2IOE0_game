
package src.gui;


// Java imports
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


// Own imports
import src.GS;
import src.gui.filter.KeyFilter;
import src.gui.menuList.ConfigKeyMenuHeader;
import src.gui.menuList.ConfigKeyMenuItem;
import src.gui.menuList.MenuLister;
import src.gui.sorter.KeySorter;
import src.tools.event.ControllerKey;
import static src.tools.event.ControllerKey.DEFAULT_GET_COMP_MODE;
import src.tools.event.keyAction.KeyAction;
import src.tools.log.Logger;


/**
 * 
 */
public class ConfigKeysPanel
        extends ListingPanel<ConfigKeyMenuItem, MenuLister<ConfigKeyMenuItem>> {
    
    private Lock lock = new ReentrantLock();
    
    
    public ConfigKeysPanel() {
        super("key config", new MenuLister<ConfigKeyMenuItem>(
                new ConfigKeyMenuHeader(
                    new Integer[] {
                        40, 120, 240, 390, 590, 800
                    })),
                new KeySorter(),
                new KeyFilter()
        );
        
        back.addActionListener((e) -> {
            GS.mainPanel.getSwitchPanel().setActivePanel("startup panel");
        });
        
        setBackground(Color.YELLOW);
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
                items.add(new ConfigKeyMenuItem(action, key, this));
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
        new Thread("saving-key-bindings-trhead") {
            @Override
            public void run() {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                        GS.KEYS_CONFIG))) {
                    writeKeys(bw);
                    
                } catch (IOException e) {
                    Logger.write(e);
                }
            }
        }.start();
    }
    
    private void writeKeys(BufferedWriter bw)
            throws IOException {
        Map<KeyAction, List<ControllerKey>> map
                = new HashMap<>();
        
        lock.lock();
        try {
            ConfigKeyMenuItem[] items = lister.getItemList().toArray(
                    new ConfigKeyMenuItem[lister.length()]);
            
            for (ConfigKeyMenuItem item : items) {
                KeyAction action = item.getAction();
                ControllerKey key = item.getKey();
                if (action == null || key == null) continue;
                
                ControllerKey.setCompMode(DEFAULT_GET_COMP_MODE);
                List<ControllerKey> list = map.get(action);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(action, list);
                }
                
                list.add(key);
            }
            
        } finally {
            lock.unlock();
        }
        
        for (Map.Entry<KeyAction, List<ControllerKey>> entry : map.entrySet()) {
            KeyAction action = entry.getKey();
            List<ControllerKey> keys = entry.getValue();
            if (action == null || keys == null) continue;
            for (ControllerKey key : keys) {
                if (key == null) continue;
                bw.write(key.toString() + GS.LS);
            }
            
            bw.write(action.toString() + GS.LS + GS.LS);
        }
        
        GS.setKeyMap(map);
    }
    
    @Override
    public void userDeletedAction(List deleted) {
    }
    
    @Override
    public void userAddItem() {
        List<Enum> actions = KeyAction.getAllActions();
        Enum[] options = actions.toArray(new Enum[actions.size()]);
        Enum option = (Enum) JOptionPane.showInputDialog(this, 
                "Select action",
                "Adding new key binding",
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]);
        if (option == null) return;
        
        Integer[] players = new Integer[GS.MAX_PLAYERS];
        for (int i = 0; i < GS.MAX_PLAYERS; i++) {
            players[i] = i + 1;
        }
        
        Integer id = (Integer) JOptionPane.showInputDialog(this, 
                "Select player",
                "Adding new key binding",
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                players, 
                players[0]);
        if (id == null) return;
        
        KeyAction keyAction = KeyAction.createKeyAction(id, option);
        lister.addItem(new ConfigKeyMenuItem(keyAction, null, this));
        SwingUtilities.invokeLater(() -> {
            lister.setPage(lister.getTotalPages());
            lister.toBottom();
            revalidate();
            repaint();
        });
    }
    
    
}
