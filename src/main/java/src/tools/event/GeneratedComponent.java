
package src.tools.event;

import net.java.games.input.Component;


// Own imports


// Java imports


/**
 * 
 * 
 */
public class GeneratedComponent
        implements Component {
    
    final private Identifier ident;
    final private float value;
    
    public GeneratedComponent(Identifier ident, float value) {
        this.ident = ident;
        this.value = value;
    }
    
    
    @Override
    public Identifier getIdentifier() {
        return ident;
    }
    
    @Override
    public boolean isRelative() {
        return false;
    }
    
    @Override
    public boolean isAnalog() {
        return false;
    }
    
    @Override
    public float getDeadZone() {
        return 0.0f;
    }
    
    @Override
    public float getPollData() {
        return value;
    }
    
    @Override
    public String getName() {
        return toString();
    }
    
    
}
