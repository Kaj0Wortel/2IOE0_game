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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Logger to combine multiple loggers.
 * 
 * @author Kaj Wortel (0991586)
 */
public class MultiLogger
        extends DefaultLogger {
    final private List<Logger> loggers;
    final private List<DefaultLogger> defLoggers;
    
    public MultiLogger(Logger... loggers) {
        if (loggers == null) {
            this.loggers = new ArrayList<Logger>(0);
            this.defLoggers = new ArrayList<DefaultLogger>(0);
            
        } else {
            this.loggers = new ArrayList<Logger>();
            this.defLoggers = new ArrayList<DefaultLogger>();
            for (int i = 0; i < loggers.length; i++) {
                if (loggers[i] instanceof DefaultLogger) {
                    this.defLoggers.add((DefaultLogger) loggers[i]);
                    
                } else {
                    this.loggers.add(loggers[i]);
                }
            }
        }
    }
    
    @Override
    protected void writeText(String msg)
            throws IOException {
        for (DefaultLogger defLog : defLoggers) {
            defLog.writeText(msg);
        }
    }
    
    @Override
    protected void writeE(Exception e, Type type, Date timeStamp) {
        for (Logger log : loggers) {
            log.writeE(e, type, timeStamp);
        }
        
        super.writeE(e, type, timeStamp);
    }
    
    @Override
    protected void writeO(Object obj, Type type, Date timeStamp) {
        for (Logger log : loggers) {
            log.writeO(obj, type, timeStamp);
        }
        
        super.writeO(obj, type, timeStamp);
    }
    
    @Override
    protected void flush() {
        for (DefaultLogger defLog : defLoggers) {
            defLog.flush();
        }
        
        for (Logger log : loggers) {
            log.flush();
        }
    }
    
    @Override
    protected void close() {
        for (DefaultLogger defLog : defLoggers) {
            defLog.close();
        }
        
        for (Logger log : loggers) {
            log.close();
        }
    }
    
    
    
}
