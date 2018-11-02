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

package src.tools.log;


// Java imports


import java.util.Date;


/**
 * This is a log class that ignores all log action.
 */
public class NullLog
        extends Logger {
    // The only instance of this class.
    private static NullLog instance = new NullLog();
    
    /**
     * Only a single private constructor because of singleton design pattern.
     */
    private NullLog() { }
    
    /**
     * @return the only null log instance.
     */
    public static synchronized NullLog getInstance() {
        return instance;
    }
    
    @Override
    protected void writeE(Exception e, Date timeStamp) { }
    
    @Override
    protected void writeE(Exception e, Type type, Date timeStamp) { }
    
    @Override
    protected void writeO(Object obj, Date timeStamp) { }
    
    @Override
    protected void writeO(Object obj, Type type, Date timeStamp) { }
    
    @Override
    protected void writeOA(Object[] objArr, Date timeStamp) { }
    
    @Override
    protected void writeOA(Object[] objArr, Type type, Date timeStamp) { }
    
    @Override
    protected void close() { }
    
    @Override
    protected void flush() { }
    
}