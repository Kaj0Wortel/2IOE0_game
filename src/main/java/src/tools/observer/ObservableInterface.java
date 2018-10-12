/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2018 by Kaj Wortel - all rights reserved                    *
 * Contact: kaj.wortel@gmail.com                                             *
 *                                                                           *
 * This file is part of the tools project, which can be found on github:     *
 * https://github.com/Kaj0Wortel/tools                                       *
 *                                                                           *
 * It is allowed to use, (partially) copy and modify this file               *
 * in any way for private use only by using this header.                     *
 * It is not allowed to redistribute any (modifed) versions of this file     *
 * without my permission.                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package src.tools.observer;


// Java imports
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Observer interface for when an object that already extends another
 * class can still become an observer.
 * 
 * DEPRECATED:
 * Since interfaces have static initialisation, this class can only be
 * used for exactly one static observer.
 * Use {@link HashObservableInterface} instead.
 */
@Deprecated
public interface ObservableInterface
        extends ObsInterface {
    // List containing all observers. The CopyOnWriteArrayList is used
    // because it automatically achieves synchronization by copying the
    // array when modified.
    final public static List<Observer> obs = new CopyOnWriteArrayList<>();
    
    // Whether there was a modification.
    // An array is used since the interface does only supports final values.
    final public static boolean[] modified = {false};
    
    /**
     * Adds the given observer to the list of observers.
     * 
     * @param o oberver to be added.
     * @throws NullPointerException iff {@code obs == null}.
     */
    @Override
    default public void addObserver(Observer o) {
        obs.add(o);
    }
    
    /**
     * Deletes the given observer from the list of observers.
     * 
     * @param o observer to be deleted.
     */
    @Override
    default public void deleteObserver(Observer o) {
        obs.remove(o);
    }
    
    /**
     * This method should be invoked to notify all observers of a change.
     * 
     * @param arg the updated object.
     * 
     * Implementation via {@link #notifyObservers(Object arg)}.
     */
    @Override
    default public void notifyObservers() {
        notifyObservers(null);
    }
    
    /**
     * This method should be invoked to notify all observers of a change
     * in the given object.
     * 
     * @param arg the updated object.
     */
    @Override
    default public void notifyObservers(Object arg) {
        boolean mod;
        synchronized(this) {
            mod = modified[0];
        }
        
        if (mod) {
            obs.forEach(observer -> observer.update(this, arg));
        }
    }
    
    /**
     * Deletes all observers from the list of observers.
     */
    @Override
    default public void deleteObservers() {
        obs.clear();
    }
    
    /**
     * Set that there was a change.
     */
    @Override
    default void setChanged() {
        synchronized(this) {
            modified[0] = true;
        }
    }
    
    /**
     * Set that there was no change.
     */
    @Override
    default void clearChagned() {
        synchronized(this) {
            modified[0] = false;
        }
    }
    
    /**
     * @return whether there was a change.
     */
    @Override
    default public boolean hasChanged() {
        synchronized(this) {
            return modified[0];
        }
    }
    
    /**
     * @return the number of observers of this object.
     */
    @Override
    default public int countObservers() {
        return obs.size();
    }
    
    
}