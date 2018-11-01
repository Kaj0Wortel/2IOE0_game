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

package toolsX.log;


// Java packages

import src.tools.log.DefaultLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


/**
 * Logs the data to an OutputStream.
 */
public class StreamLogger extends DefaultLogger {
    // The stream used to output the log data.
    private OutputStream stream;
    
    // The used charset
    private Charset charset = Charset.forName("UTF-8");
    
    
    /**-------------------------------------------------------------------------
     * Constructor
     * -------------------------------------------------------------------------
     */
    /**
     * @param outputStream the output stream to log to.
     *     The default is {@code System.out}.
     */
    private StreamLogger() {
        this(System.out);
    }
    
    private StreamLogger(OutputStream outputStream) {
        super();
        stream = outputStream;
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions
     * -------------------------------------------------------------------------
     */
    public OutputStream getStream() {
        return stream;
    }
    
    @Override
    public void writeText(String text) throws IOException {
        if (stream != null) {
            stream.write(text.getBytes(charset));
        }
    }
    
    @Override
    protected void close() {
        try {
            if (stream != null) stream.close();
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    @Override
    protected void flush() { }
    
    
}