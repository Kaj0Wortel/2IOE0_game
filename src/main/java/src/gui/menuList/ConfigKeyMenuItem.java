
package src.gui.menuList;

import java.util.Set;
import javax.swing.SwingUtilities;
import net.java.games.input.Component.Identifier;
import src.GS;
import src.gui.RacingButton;
import src.tools.MultiTool;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.KeyAction;


// Own imports


// Java imports


/**
 * 
 */
public class ConfigKeyMenuItem
        extends MenuItem {
    
    /**-------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    final public static int SPACING = 5;
    final public static String PRESS_A_KEY = "Press a key";
    
    
    /**-------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    private static class PlayerID
            extends FormContents {
        
        private KeyAction action;
        
        public PlayerID(KeyAction action) {
            super(SPACING, "" + action.getID());
            this.action = action;
        }
        
        @Override
        public void reload() {
            comps[0].setText("" +  action.getID());
        }
        
        /**
         * @param action the new action to be displayed.
         */
        public void setAction(KeyAction action) {
            this.action = action;
        }
        
        
    }
    
    
    
    public static class ActionName
            extends FormContents {
        
        private KeyAction action;
        
        
        public ActionName(KeyAction action) {
            super(SPACING, (action == null
                    ? ""
                    : action.getAction().toString()));
            this.action = action;
        }
        
        @Override
        public void reload() {
            if (action == null) comps[0].setText("");
            else comps[0].setText(action.getAction().toString());
        }
        
        /**
         * @param action the new action to be displayed.
         */
        public void setAction(KeyAction action) {
            this.action = action;
        }
        
        
    }
    
    
    
    public static class SetKeyButton
            extends JComponentContents<RacingButton> {
        
        private ConfigKeyMenuItem item;
        private int spacing = SPACING;
        
        
        public SetKeyButton() {
            super(new RacingButton("Change key"));
            
            comp.addActionListener((e) -> {
                if (item == null) return;
                item.setKey(null);
                
                new Thread("Change-key-bindings-thread") {
                    @Override
                    public void run() {
                        Set<ControllerKey> pressed;
                        do {
                            pressed = GS.keyDet.getKeysPressed();
                            MultiTool.sleepThread(10);
                            
                        } while (pressed.isEmpty());

                        // Get the first key of those that were pressed.
                        // Note that the iterator next call is save as
                        // there is at least one item, and this set
                        // won't be modified.
                        item.setKey(pressed.iterator().next());
                        item.reload();
                    }
                }.start();
            });
        }
        
        
        /**
         * @param item the new menu item of which the key will be set
         *     after the button has been pressed..
         */
        public void setMenuItem(ConfigKeyMenuItem item) {
            this.item = item;
        }

        @Override
        public void reload() { }
        
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            comp.setBounds(spacing, spacing,
                    width - 2*spacing, height - 2*spacing);
            
        }
        
        
    }
    
    
    
    private static class ControllerType
            extends TextContents {
        
        private ControllerKey key;
        
        
        public ControllerType(ControllerKey key) {
            super(key == null
                    ? PRESS_A_KEY
                    : key.getController().getType().toString());
            this.key = key;
        }
        
        
        @Override
        public void reload() {
            if (key == null) comp.setText(PRESS_A_KEY);
            else comp.setText(key.getController().getType().toString());
        }
        
        /**
         * @param key the new key to be displayed.
         */
        public void setKey(ControllerKey key) {
            this.key = key;
        }
        
        
    }
    
    
    private static class ControllerName
            extends TextContents {
        
        final private static String ALL_NAME = "All";
        
        private ControllerKey key;
        
        
        public ControllerName(ControllerKey key) {
            super(key == null ? PRESS_A_KEY : (key.typeOnly()
                    ? ALL_NAME
                    : key.getController().getName()));
            this.key = key;
        }
        
        
        @Override
        public void reload() {
            if (key == null) comp.setText(PRESS_A_KEY);
            else if (key.typeOnly()) comp.setText(ALL_NAME);
            else comp.setText(key.getController().getName());
        }
        
        /**
         * @param key the new key to be displayed.
         */
        public void setKey(ControllerKey key) {
            this.key = key;
        }
        
        
    }
    
    
    private static class ControllerButton
            extends TextContents {
        
        final private static String UNKNOWN_NAME = "Unknown";
        private ControllerKey key;
        
        
        public ControllerButton(ControllerKey key) {
            super(key.getComponent().getIdentifier() == null
                    ? UNKNOWN_NAME
                    : key.getComponent().getIdentifier().getName());
            this.key = key;
        }
        
        
        @Override
        public void reload() {
            if (key == null) comp.setText(PRESS_A_KEY);
            else {
                Identifier ident = key.getComponent().getIdentifier();
                if (ident == null) comp.setText(UNKNOWN_NAME);
                else comp.setText(ident.toString());
            }
        }
        
        /**
         * @param key the new key to be displayed.
         */
        public void setKey(ControllerKey key) {
            this.key = key;
        }
        
        
    }
    
    
    
    /**-------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    final private KeyAction action;
    private ControllerKey key;
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    public ConfigKeyMenuItem(KeyAction action, ControllerKey key) {
        super(new Contents[] {
            new PlayerID(action),
            new ActionName(action),
            new SetKeyButton(),
            new ControllerType(key),
            new ControllerName(key),
            new ControllerButton(key)
        });
        
        SwingUtilities.invokeLater(() -> {
            ((SetKeyButton) contents.get(1)).setMenuItem(this);
        });
        
        this.action = action;
        this.key = key;
    }
    
    
    /**
     * @return the action that was set for this menu item.
     */
    public KeyAction getAction() {
        return action;
    }
    
    /**
     * @return the key that activates the action.
     */
    public ControllerKey getKey() {
        return key;
    }
    
    /**
     * @param key the new key value.
     */
    protected void setKey(ControllerKey key) {
        this.key = key;
        ((ControllerType) contents.get(3)).setKey(key);
        ((ControllerName) contents.get(4)).setKey(key);
        ((ControllerButton) contents.get(5)).setKey(key);
        reload();
    }
    
    
}
