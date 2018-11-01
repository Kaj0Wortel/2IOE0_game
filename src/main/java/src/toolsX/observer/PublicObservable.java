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


// Java imports
import java.util.Observable;
import java.util.Observer;


/**
 * Identical to the {@link java.util.Observable}, but then with
 * public functions.
 */
public class PublicObservable
        extends Observable {
    @Override
    public void addObserver(Observer o) {
        super.addObserver(o);
    }
    
    @Override
    public void clearChanged() {
        super.clearChanged();
    }
    
    @Override
    public int countObservers() {
        return super.countObservers();
    }
    
    @Override
    public void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }
    
    @Override
    public void deleteObservers() {
        super.deleteObservers();
    }
    
    @Override
    public boolean hasChanged() {
        return super.hasChanged();
    }
    
    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }
    
    @Override
    public void setChanged() {
        super.setChanged();
    }
    
    
}
