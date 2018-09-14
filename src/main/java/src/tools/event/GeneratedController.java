/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.tools.event;


// Own imports


// JInput imports
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;


// Java imports
import src.GS;


/**
 * 
 */
public class GeneratedController
        implements Controller {
    
    final private String key;
    private String name;
    private Controller controller;
    
    
    public GeneratedController(String key, String name) {
        this.name = name;
        this.key = key;
        
        if (!checkController()) {
            ControllerEnvironment.getDefaultEnvironment()
                    .addControllerListener(new ControllerListener() {
                @Override
                public void controllerRemoved(ControllerEvent e) {
                    checkController();
                }
                
                @Override
                public void controllerAdded(ControllerEvent e) {
                    checkController();
                }
            });
        }
    }
    
    /**
     * Checks if the controller is not {@code null}. If so, try to set it.
     * 
     * @return {@code true} iff {@code controller != null}.
     */
    private boolean checkController() {
        if (controller != null) return true;
        if (GS.keyDet == null) return false;
        if ((controller = GS.keyDet.getControllerFromString(key)) != null) {
            name = controller.getName();
            return true;
            
        } else return false;
    }
    
    
    @Override
    public Controller[] getControllers() {
        if (checkController()) return controller.getControllers();
        else return new Controller[0];
    }
    
    @Override
    public Type getType() {
        if (checkController()) return controller.getType();
        else return Type.UNKNOWN;
    }
    
    @Override
    public Component[] getComponents() {
        if (checkController()) return controller.getComponents();
        else return new Component[0];
    }
    
    @Override
    public Component getComponent(Identifier id) {
        if (checkController()) return controller.getComponent(id);
        else return null;
    }
    
    @Override
    public Rumbler[] getRumblers() {
        if (checkController()) return controller.getRumblers();
        else return new Rumbler[0];
    }
    
    @Override
    public boolean poll() {
        if (checkController()) return controller.poll();
        else return false;
    }
    
    @Override
    public void setEventQueueSize(int size) {
        if (checkController()) controller.setEventQueueSize(size);
    }
    
    @Override
    public EventQueue getEventQueue() {
        if (checkController()) return controller.getEventQueue();
        else return new EventQueue(0);
    }
    
    @Override
    public PortType getPortType() {
        if (checkController()) return controller.getPortType();
        else return PortType.UNKNOWN;
    }
    
    @Override
    public int getPortNumber() {
        if (checkController()) return controller.getPortNumber();
        else return 0;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    
}
