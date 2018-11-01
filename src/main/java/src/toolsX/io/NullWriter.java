/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2018 by Kaj Wortel - all rights reserved                *
 * Contact: kaj.wortel@gmail.com                                         *
 *                                                                       *
 * This file is part of the tools project, which can be found on github: *
 * https://github.com/Kaj0Wortel/tools                                   *
 *                                                                       *
 * It is allowed to use, (partially) copy and modify this file           *
 * in any way for private use only by using this header.                 *
 * It is not allowed to redistribute any (modifed) versions of this file *
 * without my permission.                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package toolsX.io;


// Java imports
import java.io.Writer;


/**
 * This is a writer class that ignores all data written to it.
 */
public class NullWriter
        extends Writer {
    
    public NullWriter() { }
    
    @Override
    public void close() { }
    
    @Override
    public void flush() { }
    
    @Override
    public void write(char[] c, int i, int i2) { }
}