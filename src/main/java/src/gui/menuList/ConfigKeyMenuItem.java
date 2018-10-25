
package src.gui.menuList;


// Own imports
import src.GS;
import src.gui.RacingButton;
import src.tools.MultiTool;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.KeyAction;


// Java imports
import javax.swing.SwingUtilities;


// JInput imports
import net.java.games.input.Component.Identifier;
import src.gui.ListingPanel;


/**
 * 
 */
public class ConfigKeyMenuItem
        extends MenuItem
        implements SelectableItem {
    
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
            extends TextContents {
        
        private KeyAction action;
        
        public PlayerID(KeyAction action) {
            super("" + action.getID());
            this.action = action;
        }
        
        @Override
        public void reload() {
            comp.setText("" +  action.getID());
        }
        
        /**
         * @param action the new action to be displayed.
         */
        public void setAction(KeyAction action) {
            this.action = action;
        }
        
        
    }
    
    
    
    public static class ActionName
            extends TextContents {
        
        private KeyAction action;
        
        
        public ActionName(KeyAction action) {
            super(action == null
                    ? ""
                    : action.getAction().toString().toLowerCase().replaceAll("_", " "));
            this.action = action;
        }
        
        @Override
        public void reload() {
            if (action == null) comp.setText("");
            else comp.setText(action.getAction().toString());
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
        final private ListingPanel panel;
        private ConfigKeyMenuItem item;
        private int spacing = SPACING;
        
        
        public SetKeyButton(ListingPanel panel) {
            super(new RacingButton("Change key"));
            
            this.panel = panel;
            comp.addActionListener((e) -> {
                if (item == null) return;
                item.setKey(null);
                
                new Thread("Change-key-bindings-thread") {
                    @Override
                    public void run() {
                        MultiTool.sleepThread(250); // Reaction time delay
                        ControllerKey pressed = null;
                        while (pressed == null) {
                            for (ControllerKey key : GS.keyDet.getKeysPressed()) {
                                if (!key.isCenter()) {
                                    pressed = key;
                                    break;
                                }
                            }
                            
                            MultiTool.sleepThread(40); // Polling delay.
                        }
                        
                        if (panel.isTypeOnly()) {
                            pressed = new ControllerKey(
                                    pressed.getController(),
                                    pressed.getComponent(),
                                    pressed.getValue(),
                                    true
                            );
                        }
                        
                        // Get the first key of those that were pressed.
                        // Note that the iterator next call is save as
                        // there is at least one item, and this set
                        // won't be modified.
                        item.setKey(pressed);
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
            super(key == null ? "" : (key.getComponent().getIdentifier() == null
                    ? UNKNOWN_NAME
                    : key.getComponent().getIdentifier().getName()));
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
    public ConfigKeyMenuItem(KeyAction action, ControllerKey key,
            ListingPanel listingPanel) {
        super(new Contents[] {
            new SelectContents(),
            new PlayerID(action),
            new ActionName(action),
            new SetKeyButton(listingPanel),
            new ControllerType(key),
            new ControllerName(key),
            new ControllerButton(key)
        });
        
        SwingUtilities.invokeLater(() -> {
            ((SetKeyButton) contents.get(3)).setMenuItem(this);
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
        ((ControllerType) contents.get(4)).setKey(key);
        ((ControllerName) contents.get(5)).setKey(key);
        ((ControllerButton) contents.get(6)).setKey(key);
        reload();
    }
    
    @Override
    public boolean isSelected() {
        return ((SelectContents) contents.get(0)).isSelected();
    }
    
    @Override
    public void setSelected(boolean selected) {
        ((SelectContents) contents.get(0)).setSelected(selected);
    }
    
    
}
