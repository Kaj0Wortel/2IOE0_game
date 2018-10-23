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

package src.tools.io;


// Java imports
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides an easy way of processing a data file.
 * Has the following functionality:
 *  - Keeps track of a line counter.
 *  - Supports custom comment (single line and multiple line).
 *  - Supports csv file handeling:
 *    - Format: data;data;data; (etc.)
 *    - Read single cell.
 *    - Read line of cells.
 *  - Supports conf/config file handeling:
 *    - Format: name=data,data,data (etc.)
 *    - Read name field.
 *    - Read data field.
 *    - Changeable delimiters for name and data fields.
 *  - Supports stream marking.
 *  - Supports easy pre and post line processing for extended classes.
 *  - Can be used as a wrapper for readers.
 */
public class BufferedReaderPlus
        extends BufferedReader
        implements Closeable {
    
    // Types of supported comments.
    final public static int NO_COMMENT = -1;
    final public static int HASHTAG_COMMENT = 0;
    final public static int JAVA_SINGLE_LINE_COMMENT = 1;
    final public static int JAVA_COMMENT = 2;
    final public static int DOCUMENT_WITH_LINKS_COMMENT = 3;
    final public static int HTML_COMMENT = 4;
    
    // Types of supported readers.
    final public static int TYPE_ALL = -1;
    final public static int TYPE_DEFAULT = 0;
    final public static int TYPE_CSV = 1;
    final public static int TYPE_CONFIG = 2;
    
    // Current linecounter in the file.
    protected int lineCounter = 0;
    
    // true iff in a multipleLineComment block.
    protected boolean multipleLineCommentActive = false;
    
    // Temporary storage for the mark option.
    protected int markedLineCounter = 0;
    protected boolean markedMultipleLineCommentActive = false;
    protected String markedBufferedLine = "";
    
    // Stores the current line when reading cells (csv filetypes only).
    protected String bufferedLine = "";
    
    // Stores the current comment String.
    private String singleCommentString;
    private String multipleCommentStartString;
    private String multipleCommentEndString;
    
    // Field and data storage of the conf file type.
    private String confFieldName = null;
    private String[] confData = null;
    String confNameSeparator = "=";
    String confDataSeparator = ",";
    
    // The type of reader.
    private int type;
    
    
    /**-------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Constructors with String to file location.
     * These constructors only convert the given String to a FileReader
     *     and let the other constructor handle the rest.
     */
    // For the constructors with supported comments.
    public BufferedReaderPlus(String file)
            throws FileNotFoundException {
        this(new FileReader(file));
    }
    
    public BufferedReaderPlus(String file, int commentType)
            throws FileNotFoundException {
        this(new FileReader(file), commentType);
    }
    
    public BufferedReaderPlus(String file, int commentType, int type)
            throws FileNotFoundException {
        this(new FileReader(file), commentType, type);
    }
    
    // For constructors with custom comments.
    public BufferedReaderPlus(String file, String singleComment)
            throws FileNotFoundException {
        this(new FileReader(file), singleComment);
    }
    
    public BufferedReaderPlus(String file, String singleComment, int type)
            throws FileNotFoundException {
        this(new FileReader(file), singleComment, type);
    }
    
    public BufferedReaderPlus(String file, String multipleCommentStart,
                              String multipleCommentEnd)
            throws FileNotFoundException {
        this(new FileReader(file), multipleCommentStart, multipleCommentEnd);
    }
    
    public BufferedReaderPlus(String file, String multipleCommentStart,
                              String multipleCommentEnd, int type)
            throws FileNotFoundException {
        this(new FileReader(file), multipleCommentStart, multipleCommentEnd,
                type);
    }
    
    public BufferedReaderPlus(String file, String singleComment,
                              String multipleCommentStart,
                              String multipleCommentEnd)
            throws FileNotFoundException {
        this(new FileReader(file), singleComment, multipleCommentStart,
                multipleCommentEnd);
    }
    
    public BufferedReaderPlus(String file, String singleComment,
                              String multipleCommentStart,
                              String multipleCommentEnd, int type)
            throws FileNotFoundException {
        this(new FileReader(file), singleComment, multipleCommentStart,
                multipleCommentEnd, type);
    }
    
    
    /**
     * Constructors with supported comments.
     * 
     * @param reader the used reader object.
     * @param commentType the supported type of comment used.
     *     Must be one of {@link #NO_COMMENT}, {@link #HASHTAG_COMMENT},
     *     {@link #JAVA_SINGLE_LINE_COMMENT}, {@link #JAVA_COMMENT},
     *     {@link #DOCUMENT_WITH_LINKS_COMMENT} or {@link #HTML_COMMENT}.
     * @param type the type of file that will be read. Must be one of
     *     {@link #TYPE_ALL}, {@link #TYPE_DEFAULT}, {@link #TYPE_CSV},
     *     or {@link #TYPE_CONFIG}.
     */
    public BufferedReaderPlus(Reader reader) {
        this(reader, NO_COMMENT);
    }
    
    public BufferedReaderPlus(Reader reader, int commentType) {
        this(reader, commentType, TYPE_DEFAULT);
    }
    
    public BufferedReaderPlus(Reader reader, int commentType, int type) {
        super(reader);
        this.type = type;
        
        if (commentType == HASHTAG_COMMENT) {
            singleCommentString = "#";
            
        } else if (commentType == JAVA_SINGLE_LINE_COMMENT) {
            singleCommentString = "//";
            
        } else if (commentType == JAVA_COMMENT) {
            singleCommentString = "//";
            multipleCommentStartString = "/*";
            multipleCommentEndString = "*/";
            
        } else if (commentType == DOCUMENT_WITH_LINKS_COMMENT) {
            singleCommentString = "/:\\";
            
        } else if (commentType == HTML_COMMENT) {
            multipleCommentStartString = "<!--";
            multipleCommentEndString = "-->";
        }
    }
    
    /**
     * Constructors with custom comments.
     * 
     * @param reader the used reader object.
     * @param singleComment the String used to indicate a single line comment.
     *     Default is "".
     * @param multipleCommentStart the String used to indicate the start of
     *     a multiple line comment. Default is "".
     * @param multipleCommentEnd the String used to indicate the end of
     *     a multiple line comment. Default is "".
     * @param type the type of the reader.
     */
    // Single line only
    public BufferedReaderPlus(Reader reader, String singleComment) {
        this(reader, singleComment, null, null, TYPE_DEFAULT);
    }
    
    public BufferedReaderPlus(Reader reader, String singleComment,
                              int type) {
        this(reader, singleComment, null, null, TYPE_DEFAULT);
    }
    
    // Multiple line only
    public BufferedReaderPlus(Reader reader, String multipleCommentStart,
                              String multipleCommentEnd) {
        this(reader, null, multipleCommentStart, multipleCommentEnd,
                TYPE_DEFAULT);
    }
    
    public BufferedReaderPlus(Reader reader, String multipleCommentStart,
                              String multipleCommentEnd, int type) {
        this(reader, null, multipleCommentStart, multipleCommentEnd, type);
    }
    
    // Both comment types
    public BufferedReaderPlus(Reader reader, String singleComment,
                              String multipleCommentStart,
                              String multipleCommentEnd) {
        this(reader, singleComment, multipleCommentStart, multipleCommentEnd,
             TYPE_DEFAULT);
    }
    
    // Full constructor
    public BufferedReaderPlus(Reader reader, String singleComment,
                              String multipleCommentStart,
                              String multipleCommentEnd, int type) {
        super(reader);
        this.type = type;
        
        singleCommentString = singleComment;
        multipleCommentStartString = multipleCommentStart;
        multipleCommentEndString = multipleCommentEnd;
    }
    
    
    /**-------------------------------------------------------------------------
     * Read line fuctions.
     * -------------------------------------------------------------------------
     */
    /**
     * Reads a single line.
     * 
     * @returns the next line of the file.
     * @throws IOException when no read actions are allowed from the file.
     */
    @Override
    public String readLine()
            throws IOException {
        lineCounter++;
        return super.readLine();
    }
    
    /**
     * Reads a line while skipping empty lines comments.
     * 
     * @returns the next processed line of the file.
     * @throws IOException when no read actions are allowed from the file.
     */
    public String readProcessedLine()
            throws IOException {
        String line;
        
        do {
            // Read the line
            line = readLine();
            
            // Process the line
            line = postProcessLine(processLine(preProcessLine(line)));
            
            // Check for null
            if (line != null) {
                // Remove starting and trailing spaces
                line = line.trim();
                // Remove trailing semi-colons and remove staring and
                // trailing spaces if it is a csv file.
                if (supportsCSV()) {
                    line = removeCSVTrailings(line).trim();
                }
            }
            
        } while (line != null && line.equals(""));
        return line;
    }
    
    /**
     * Pre-processes the given line.
     * Is supposed to be overridden to do operations before
     *     processing the line.
     * 
     * @param line text to be processed.
     * @return processed line.
     */
    protected String preProcessLine(String line) {
        return line;
    }
    
    /**
     * Post-processes the given line.
     * Is supposed to be overriden to do operations after processing the line.
     * 
     * @param lien text to be processed.
     * @return processed line.
     */
    protected String postProcessLine(String line) {
        return line;
    }
    
    /**
     * Processes the given line.
     * Used for ignoring single and multiple line comments.
     * 
     * @param line text to be processed
     * @return processed line.
     */
    private String processLine(String line) {
        // If the input is null, nothing can be processed.
        if (line == null) return null;
        
        boolean hasSingleLineComment
            = !(singleCommentString == null ||
                singleCommentString.equals(""));
        boolean hasMultipleLineComment
            = !(multipleCommentStartString == null ||
                multipleCommentStartString.equals("") ||
                multipleCommentEndString == null ||
                multipleCommentEndString.equals(""));
        
        // If there are no processing actions, simply return the line.
        if (!hasSingleLineComment && !hasMultipleLineComment) return line;
        
        // Marks the begin of a multiple line comment.
        int beginMLC = 0;
        
        for (int pointer = 0; pointer < line.length(); pointer++) {
            if (multipleLineCommentActive) {
                // Should not occur.
                if (!hasMultipleLineComment) {
                    multipleLineCommentActive = false;
                    pointer--;
                    continue;
                }
                
                // Checks if there could be a multiple line comment ending.
                if (line.length() - pointer
                        - multipleCommentEndString.length() >= 0) {
                    // Checks for multiple line comment ending.
                    if (line.startsWith(multipleCommentEndString, pointer)) {
                        String preCommentLine = beginMLC == 0
                                ? ""
                                : line.substring(0, beginMLC);
                        String postCommentLine = pointer == line.length()
                                ? ""
                                : line.substring(pointer
                                        + multipleCommentEndString.length());
                        pointer = preCommentLine.length();
                        line = preCommentLine + postCommentLine;
                        multipleLineCommentActive = false;
                    }
                    
                } else {
                    // If not, ignore the rest of the line.
                    return (beginMLC == 0 ? "" : line.substring(0, beginMLC));
                }
                
            } else {
                // No multiple line comment is active.
                
                if (hasSingleLineComment) {
                    // Checks if there could be a single line comment.
                    if (line.length() - pointer
                            - singleCommentString.length() >= 0) {
                        // Checks for single line comment.
                        if (line.startsWith(singleCommentString, pointer)) {
                            return line.substring(0, pointer);
                        }
                    }
                }
                
                if (hasMultipleLineComment) {
                    // Checks if there could be a multiple line comment start.
                    if (line.length() - pointer
                            - multipleCommentStartString.length() >= 0) {
                        // Checks for multiple line comment start.
                        if (line.startsWith(multipleCommentStartString,
                                pointer)) {
                            beginMLC = pointer;
                            multipleLineCommentActive = true;
                            pointer += multipleCommentStartString.length() - 1;
                        }
                    }
                }
            }
        }
        
        return line;
    }
    
    
    /**-------------------------------------------------------------------------
     * CSV file functions.
     * -------------------------------------------------------------------------
     */
    /**
     * @return whether the reader supports the csv file type series.
     */
    public boolean supportsCSV() {
        return type == TYPE_ALL || type == TYPE_CSV;
    }
    
    /**
     * Removes trailing semi-colons from csv-files
     * 
     * @param line the line to remove the trainling semi-colons from.
     * @return a line without trailing semi-colons.
     */
    @SuppressWarnings("empty-statement")
    public static String removeCSVTrailings(String line) {
        if (line == null) return null;
        if (line.length() == 0) return "";
        
        // iterate backwards
        int i = line.length() - 1;
        for (; i >= 0 && line.charAt(i) == ';'; i--);
        return (i == -1 ? "" : line.substring(0, i + 1));
    }
    
    /**
     * Reads a processed cell from a csv file.
     * 
     * @param processed whether to use processed lines as input or not.
     *     Default is true.
     * @param lineBlock whether to return null if the end of the line has been
     *     reached (true), or to take the next line (false).
     * @return the data of a single cell from the file. Returns null iff
     *     EOF reached or if lineBlock == true and EOL reached.
     * @throws IllegalStateException iff the reader was not configured
     *     as a csv file type.
     * @throws IOException iff the data could not be retrieved from the file.
     * 
     * If no line was buffered, pick the next one iff lineBlock == false.
     *     Otherwise return null.
     */
    public String readCSVCell()
            throws IllegalStateException, IOException {
        return readCSVCell(true);
    }
    
    public String readCSVCell(boolean processed)
            throws IllegalStateException, IOException {
        return readCSVCell(processed, false);
    }
    
    public String readCSVCell(boolean processed, boolean lineBlock)
            throws IllegalStateException, IOException {
        if (!supportsCSV())
            throw new IllegalStateException(
                    "Reader is not configured to read \".csv\" file types.");
        
        // Update the buffer if nessecary
        if (bufferedLine == null || bufferedLine.length() == 0) {
            // If the bufferlength == 0, read new line if allowed.
            // Otherwise return null.
            if (lineBlock) return null;
            
            // Reads the next line
            bufferedLine = (processed ? readProcessedLine() : readLine());
            
            // Check for EOF
            if (bufferedLine == null) return null;
        }
        
        // Read the buffer
        String line = bufferedLine;
        
        // Takes the substring of the beginning of the line till
        // the first semi-colon.
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ';') {
                bufferedLine = line.substring(i + 1);
                return line.substring(0, i).trim();
            }
        }
        
        // If no semi-colon is in the line, return the full line and
        // set bufferedLine to "".
        bufferedLine = "";
        return line;
    }
    
    /**
     * Reads either all remaining processed cells on the buffered line or all
     *     cells on a new line from a csv file.
     * 
     * @param processed whether to use processed lines as input or not.
     * @param lineBlock whether to return null if the end of the line has
     *     been reached (true),
     *     or to take the next line (false).
     * @return the data of each cell in an array in increasing order.
     *     Returns null iff EOF reached or if lineBlock == true and EOL reached.
     * @throws IllegalStateException iff the reader was not configured
     *     as a csv file type.
     * @throws IOException iff the data could not be retrieved from the file.
     * 
     * Note:
     * If no line was buffered, pick the next one iff lineBlock == false.
     * Otherwise return null.
     */
    public List<String> readCSVLine(boolean processed)
            throws IllegalStateException, IOException {
        return readCSVLine(processed, false);
    }
    
    public List<String> readCSVLine(boolean processed, boolean lineBlock)
            throws IllegalStateException, IOException {
        if (!supportsCSV())
            throw new IllegalStateException(
                    "Reader is not configured to read \".csv\" file types.");
        
        // Read and clear the buffer
        String line = bufferedLine;
        bufferedLine = "";
        
        // Checks the buffer length.
        if (line.length() == 0) {
            // If the bufferlength == 0, read new line iff allowed.
            // Otherwise return null.
            if (lineBlock) return null;
            
            // Reads the next line
            line = (processed ? readProcessedLine() : readLine());
            
            // Check for the null-value
            if (line == null) return null;
        }
        
        List<String> cells = new ArrayList<String>();
        int prevColon = 0;
        
        // Puts all Strings separated by a semi-colon in the ArrayList.
        // If no semi-colon is present, return the full line as
        // one element in the ArrayList
        for (int i = 0; i <= line.length(); i++) {
            if (i == line.length() || 
                line.charAt(i) == ';') {
                
                if (processed) {
                    cells.add(line.substring(prevColon, i).trim());
                    
                } else {
                    cells.add(line.substring(prevColon, i));
                }
                
                prevColon = i + 1;
            }
        }
        
        return cells;
    }
    
    
    /**-------------------------------------------------------------------------
     * CONFIG file functions.
     * -------------------------------------------------------------------------
     */
    /**
     * @return whether the reader supports the conf file type series.
     */
    public boolean supportsConf() {
        return type == TYPE_ALL || type == TYPE_CONFIG;
    }
    
    /**
     * @param sep the new conf file type name separator.
     */
    public void setConfNameSeparator(String sep) {
        confNameSeparator = sep;
    }
    
    /**
     * @param sep the new conf file type data separator.
     */
    public void setConfDataSeparator(String sep) {
        confDataSeparator = sep;
    }
    
    /**
     * Reads the next field.
     * The field name can be retrieved via {@link #getField()} and 
     * the data can be retrieved via {@link #getData()}.
     * 
     * @param processed whether to use processed lines as input or not.
     *     Default is {@code true}.
     * @return {@code true} if EOF was not yet reached.
     */
    public boolean readNextConfLine()
            throws IOException {
        return readNextConfLine(true);
    }
    
    public boolean readNextConfLine(boolean processed)
            throws IllegalStateException, IOException {
        if (!supportsConf()) throw new IllegalStateException(
                "Reader is not configured to read \".conf\" file types.");
        
        String line = (processed ? readProcessedLine() : readLine());
        if (line == null) {
            confFieldName = null;
            confData = null;
            return false;
        }
        
        String[] split = line.split(confNameSeparator);
        
        if (split.length == 0) {
            // Empty line.
            confFieldName = null;
            confData = null;
            
        } else if (split.length == 1) {
            // Field name only.
            confFieldName = split[0];
            confData = new String[0];
            
        } else if (split.length == 2) {
            // Field with data.
            confFieldName = split[0];
            confData = split[1].split(confDataSeparator);
            
        } else if (split.length > 2) {
            // Field with data containing the field name separator.
            confFieldName = split[0];
            confData = line.substring(confFieldName.length() + 1)
                    .split(confDataSeparator);
        }
        
        return true;
    }
    
    /**
     * @return the name of the field.
     *     {@code null} if no previous field available
     *     (not yet started or EOF).
     */
    public String getField() {
        return confFieldName;
    }
    
    /**
     * @param name the name of the field to check for.
     * @return {@code true} iff the given name equals the current field.
     *     {@code false} otherwise.
     * 
     * More specifically, {@code true} is returned if, and only if,
     * {@code name == null ? field == null : name.equals(field)}.
     */
    public boolean fieldEquals(String name) {
        return (name == null
                ? confFieldName == null
                : name.equals(confFieldName));
    }
    
    /**
     * @return array containing the values of the field.
     *     {@code null} if no previous field available,
     *     and an empty array if no previous data available
     *     (not yet started or EOF).
     */
    public String[] getData() {
        return confData;
    }
    
    
    /**-------------------------------------------------------------------------
     * Other functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Marks the present location in the steam.
     * After reading 'readAheadLimit' characters, attempting to reset
     *     the stream may fail. See {@link BufferedReader#mark(int)}.
     * 
     * @param readAheadLimit number of characters that will be at most read
     *     before reset() is called.
     * @throws IOException if the current reader is not initialized
     *     for stream marking.
     */
    @Override
    public void mark(int readAheadLimit)
            throws IOException {
        markedLineCounter = lineCounter;
        markedMultipleLineCommentActive = multipleLineCommentActive;
        markedBufferedLine = bufferedLine;
        
        super.mark(readAheadLimit);
    }
    
    /**
     * Resets to the last marked point in the stream.
     * See {@link BufferedReader#mark(int)}.
     * 
     * @throws IOException if the current reader is not initialized for stream marking.
     */
    @Override
    public void reset()
            throws IOException {
        lineCounter = markedLineCounter;
        multipleLineCommentActive = markedMultipleLineCommentActive;
        bufferedLine = markedBufferedLine;
        
        super.reset();
    }
    
    /**
     * @return the current linecounter.
     */
    public int getLineCounter() {
        return lineCounter;
    }
    
    /**
     * Not supported function.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    @Deprecated
    public int read()
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Operation was not supported");
    }
    
    /**
     * Not supported function.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    @Deprecated
    public int read(char[] cbuf, int off, int len)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Operation was not supported");
    }
    
    
}



