
package src.AI;


// Own imports


// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
@FunctionalInterface
public interface Processor<V> {
    
    public V process(String input);
    
}
