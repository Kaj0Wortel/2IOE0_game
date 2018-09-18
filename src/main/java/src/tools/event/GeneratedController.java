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
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;


// Java imports


/**
 * 
 */
public class GeneratedController
        implements Controller {
    
    final private String name;
    final private Type type;
    
    public GeneratedController(String name) {
        this(name, Type.UNKNOWN);
    }
    
    public GeneratedController(Type type) {
        this("", type);
    }
    
    public GeneratedController(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    
    @Override
    public Controller[] getControllers() {
        return new Controller[0];
    }
    
    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public Component[] getComponents() {
        return new Component[0];
    }
    
    @Override
    public Component getComponent(Identifier id) {
        return null;
    }
    
    @Override
    public Rumbler[] getRumblers() {
        return new Rumbler[0];
    }
    
    @Override
    public boolean poll() {
        return false;
    }
    
    @Override
    public void setEventQueueSize(int size) { }
    
    @Override
    public EventQueue getEventQueue() {
        return new EventQueue(0);
    }
    
    @Override
    public PortType getPortType() {
        return PortType.UNKNOWN;
    }
    
    @Override
    public int getPortNumber() {
        return 0;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    
}
