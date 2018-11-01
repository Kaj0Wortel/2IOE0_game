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


package src.toolsX.observer;


/**
 * Observer interface for when an object that already extends another
 * class can still become an observer.
 * 
 * @author Kaj Wortel (0991586)
 */
public interface ObsInterface {
    /**
     * Adds the given observer to the list of observers.
     * 
     * @param o oberver to be added.
     * @throws NullPointerException iff {@code obs == null}.
     */
    public void addObserver(Observer o);
    
    /* 
     * Deletes the given observer from the list of observers.
     * 
     * @param o observer to be deleted.
     * 
     * Implementation via {@link Observer#deleteObserver(Observer)}.
     */
    public void deleteObserver(Observer o);
    
    /**
     * This method should be invoked to notify all observers of a change.
     * 
     * @param arg the updated object.
     */
    public void notifyObservers();
    
    /**
     * This method should be invoked to notify all observers of a change
     * in the given object.
     * 
     * @param arg the updated object.
     */
    public void notifyObservers(Object arg);
    
    /**
     * Deletes all observers from the list of observers.
     */
    public void deleteObservers();
    
    /**
     * Set that there was a change.
     */
    void setChanged();
    
    /**
     * Set that there was no change.
     */
    void clearChagned();
    
    /**
     * @return whether there was a change.
     */
    public boolean hasChanged();
    
    /**
     * @return the number of observers of this object.
     */
    public int countObservers();
    
    
}
