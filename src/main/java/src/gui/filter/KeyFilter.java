
package src.gui.filter;


// Own imports
import java.awt.event.ActionEvent;
import net.java.games.input.ContrlEnv;
import net.java.games.input.Controller;
import src.GS;
import src.gui.FixedJComboBox;
import src.gui.menuList.ConfigKeyMenuItem;
import src.tools.MultiTool.BoolEvaluator;
import src.tools.event.keyAction.KeyAction;


// JInput imports
import net.java.games.input.ControllerEnvironment;


/**
 * 
 */
public class KeyFilter
        extends Filter<ConfigKeyMenuItem> {
    
    /**-------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    /**
     * Filter class for the player id.
     */
    public static class PlayerIDPanel
            extends ItemPanel<FixedJComboBox<Integer>, ConfigKeyMenuItem> {
        
        public PlayerIDPanel() {
            super(new FixedJComboBox<Integer>(), "Player");
            
            comp.addActionListener((ActionEvent e) -> {
                setSelected(true);
            });
        }
        
        @Override
        public BoolEvaluator<ConfigKeyMenuItem> createFilter() {
            return (ConfigKeyMenuItem item) -> {
                int id1 = item.getAction().getID();
                Integer id2 = (Integer) comp.getSelectedItem();
                return id1 == id2;
            };
        }
        
        @Override
        public void update() {
            boolean isSelected = isSelected();
            Integer selectedPlayer = (Integer) comp.getSelectedItem();
            comp.removeAllItems();
            
            for (int i = 0; i < GS.MAX_PLAYERS; i++) {
                comp.addItem(i);
            }
            
            if (selectedPlayer != null) comp.setSelectedItem(selectedPlayer);
            setSelected(isSelected);
        }
        
        
    }
    
    
    /**
     * Filter class for the action.
     */
    public static class ActionPanel
            extends ItemPanel<FixedJComboBox<Enum>, ConfigKeyMenuItem> {
        
        public ActionPanel() {
            super(new FixedJComboBox<Enum>(), "Action");
            
            comp.addActionListener((ActionEvent e) -> {
                setSelected(true);
            });
        }
        
        @Override
        public BoolEvaluator<ConfigKeyMenuItem> createFilter() {
            return (ConfigKeyMenuItem item) -> {
                Enum enum1 = item.getAction().getAction();
                Enum enum2 = (Enum) comp.getSelectedItem();
                return enum1 == enum2;
            };
        }
        
        @Override
        public void update() {
            boolean isSelected = isSelected();
            Enum selectedAction = (Enum) comp.getSelectedItem();
            comp.removeAllItems();
            
            for (Enum e : KeyAction.getAllActions()) {
                comp.addItem(e);
            }
            
            if (selectedAction != null) comp.setSelectedItem(selectedAction);
            setSelected(isSelected);
        }
        
        
    }
    
    
    /**
     * Filter class for the type of controller.
     */
    public static class ControllerType
            extends ItemPanel<FixedJComboBox<String>, ConfigKeyMenuItem> {
        
        
        public ControllerType() {
            super(new FixedJComboBox<String>(), "Controller type");
            
            comp.addActionListener((ActionEvent e) -> {
                setSelected(true);
            });
        }
        
        @Override
        public BoolEvaluator<ConfigKeyMenuItem> createFilter() {
            return (ConfigKeyMenuItem item) -> {
                String type1 = item.getKey().getController().getType().toString();
                String type2 = (String) comp.getSelectedItem();
                return type1.equals(type2);
            };
        }
        
        @Override
        public void update() {
            boolean isSelected = isSelected();
            String selectedController = (String) comp.getSelectedItem();
            comp.removeAllItems();
            
            Controller[] controllers = ((ContrlEnv) ControllerEnvironment
                    .getDefaultEnvironment()).getControllers();
            for (Controller c : controllers) {
                comp.addItem(c.getType().toString());
            }
            
            if (selectedController != null)
                comp.setSelectedItem(selectedController);
            setSelected(isSelected);
        }
        
        
    }
    
    
    /**
     * Filter class for the name of the controller.
     */
    public static class ControllerName
            extends ItemPanel<FixedJComboBox<String>, ConfigKeyMenuItem> {
        
        
        public ControllerName() {
            super(new FixedJComboBox<String>(), "Controller name");
            
            comp.addActionListener((ActionEvent e) -> {
                setSelected(true);
            });
        }
        
        @Override
        public BoolEvaluator<ConfigKeyMenuItem> createFilter() {
            return (ConfigKeyMenuItem item) -> {
                String type1 = item.getKey().getController().getName();
                String type2 = (String) comp.getSelectedItem();
                return type1.equals(type2);
            };
        }
        
        @Override
        public void update() {
            boolean isSelected = isSelected();
            String selectedController = (String) comp.getSelectedItem();
            comp.removeAllItems();
            
            Controller[] controllers = ((ContrlEnv) ControllerEnvironment
                    .getDefaultEnvironment()).getControllers();
            for (Controller c : controllers) {
                comp.addItem(c.getName());
            }
            
            if (selectedController != null)
                comp.setSelectedItem(selectedController);
            setSelected(isSelected);
        }
        
        
    }
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    public KeyFilter() {
        super(new ItemPanel[] {
            new PlayerIDPanel(),
            new ActionPanel(),
            new ControllerType(),
            new ControllerName()
        });
        
        // Set size.
        comp.setSize(4*SPACING + CHECK_BOX_WIDTH + LABEL_WIDTH + COMP_WIDTH,
                6*SPACING + 5*GEN_HEIGHT);
    }
    
    
}
