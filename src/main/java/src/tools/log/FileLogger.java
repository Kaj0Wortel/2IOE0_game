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

import java.io.*;


/**
 * Class for logging data.
 * All functions are thread safe.
 */
public class FileLogger
        extends DefaultLogger {
    // The default log file.
    final protected static File DEFAULT_LOG_FILE
        = new File(System.getProperty("user.dir") + "\\toolsX\\log\\log.log");
    
    // The default log file to write to.
    protected File logFile;
    
    // The writer used to write the log data to the file.
    private PrintWriter writer;
    
    
    /**-------------------------------------------------------------------------
     * Constructor
     * -------------------------------------------------------------------------
     */
    /**
     * @param fileName name of the file to log to.
     * @param file file to log to.
     */
    public FileLogger() {
        this(DEFAULT_LOG_FILE);
    }
    
    public FileLogger(String fileName) {
        this(new File(fileName));
    }
    
    public FileLogger(File file) {
        super();
        logFile = file;
    }
    
    
    /**-------------------------------------------------------------------------
     * Functions
     * -------------------------------------------------------------------------
     */
    /**
     * Checks if the writer non-null and opened.
     * - If the writer is {@code null}, a new writer is created and overwrites
     *   the old file of the file and write the header.
     * - If the writer is not {@code null} but is closed, a new writer
     *   is created and appends to the file.
     * After calling this method it is guaranteed that {@code writer != null}.
     * 
     * @throws IOException iff either {@code writer == null} or
     *     {@code writer.isClosed()}, and no new writer could be created.
     *     
     * 
     * Also see: {@link writeHeader()}.
     */
    protected void checkWriter()
            throws IOException {
        if (writer != null || !isClosed()) return;
        
        createWriter(writer != null);
    }
    
    /**
     * Creates a new writer.
     * 
     * @param append whether to append to the current log file or to
     *     overwrite the file.
     */
    protected void createWriter(boolean append)
            throws IOException {
        // Close any previously open writers.
        close();
        
        // Create the new writer.
        writer = new PrintWriter
            (new BufferedWriter(new FileWriter(logFile, append)));
        
        // Add a header for clean files.
        if (!append) writeHeader();
    }
    
    @Override
    protected void writeText(String text) throws IOException {
        // Check if the writer is active.
        checkWriter();
        
        // Write the text
        writer.print(text);
    }
    
    /**
     * Sets the log file.
     * 
     * @param fileName the name of the new log file.
     * @param file the name of the log file.
     * @param append whether to append to the new log file.
     */
    protected void setFile(String fileName, boolean append) {
        setFile(new File(fileName), append);
    }
    
    protected void setFile(File file, boolean append) {
        try {
            // Close the previous log file.
            close();
            
            // Set the new log file.
            logFile = file;
            
            // Create a new writer.
            createWriter(append);
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    /**
     * Clears the log file.
     */
    protected void clear() {
        try {
            // Close the previous log file.
            close();
            
            // Create a new writer.
            createWriter(false);
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    @Override
    protected void close() {
        if (writer != null) writer.close();
        writer = null;
    }
    
    /**
     * @return whether the log file is closed.
     */
    protected boolean isClosed() {
        return writer == null;
    }
    
    @Override
    protected void flush() {
        if (writer != null) writer.flush();
    }
    
}