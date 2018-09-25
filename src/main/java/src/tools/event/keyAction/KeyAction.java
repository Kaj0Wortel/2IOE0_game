
package src.tools.event.keyAction;


/**
 * 
 */
public abstract class KeyAction {
    final private int id;
    
    
    public KeyAction() {
        this(-1);
    }
    
    public KeyAction(int id) {
        this.id = id;
    }
    
    /**
     * @return the id of the key action.
     */
    public int getID() {
        return id;
    }
    
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should produce the save string of {@code this}.
     */
    @Override
    public abstract String toString();
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should be such that a new instance of
     * the same class as {@code this} with the same internal values will
     * produce the same hash.
     * 
     * This means for for any subclass the following must hold:
     * {@code (new ExtendedKeyAction([some data])).hashCode()
     *     == (new ExtendedKeyAction([some data])).hashCode()},
     * where in both case [some data] is equal.
     */
    @Override
    public abstract int hashCode();
    
    /**
     * {@inheritDoc}
     * 
     * Moreover, this function should be such that a new instance of
     * the same class as {@code this} with the same internal values will
     * be equal to {@code this}.
     * 
     * This means for for any subclass the following must hold:
     * {@code (new ExtendedKeyAction([some data]))
     *     .equals(new ExtendedKeyAction([some data])) == true},
     * where in both case [some data] is equal.
     */
    @Override
    public abstract boolean equals(Object obj);
    
    
}
