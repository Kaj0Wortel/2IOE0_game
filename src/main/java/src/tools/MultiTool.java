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

package src.tools;


// Own imports

import src.tools.log.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

// Java imports


/**
 * This class contains multiple handy methods.
 */
public class MultiTool {
    final public static String FS = System.getProperty("file.separator");
    final public static String LS = System.getProperty("line.separator");
    final public static String HTML_SPACE = "" + ((char) 0x00A0);
    
    
    /**
     * Converts a decimal integer to a 32 based number String.
     * 
     * @param dHex a decimal number.
     * @return a String containing the 32 based representation of {@code dHex}.
     */
    public static String dHexToString(int dHex) throws NumberFormatException {
        if (dHex < 0) throw new NumberFormatException
            ("Cannot convert a negative number.");
        
        String result = "";
        int heighestDHexNumber = 1;
        
        // Compute the length of the r.
        while (intPow(32, heighestDHexNumber++) - 1 < dHex) { }
        
        // Compute the r.
        for (int counter = 0; counter < heighestDHexNumber; counter++) {
            int part = (int)((double)(dHex) / Math.pow
                                 (32, heighestDHexNumber- (counter+1)));
            dHex -= part * Math.pow(32, heighestDHexNumber - (counter+1));
            result += singleDHexToString(part);
        }
        
        return result;
    }
    
    /**
     * Converts a 32 based number String to a decimal integer.
     * 
     * @param dHex 32 based number String.
     * @return an integer represented by the 32 based number of {@code dHex}.
     */
    public static int stringToDHex(String dHex) throws NumberFormatException {
        if (dHex == null) throw new NullPointerException("Input is null.");
        
        // Convert to upper case
        dHex = dHex.toUpperCase();
        
        // Compute the r.
        int result = 0;
        if (dHex.length() == 0) throw new NumberFormatException
            ("Invallid length. Expected: length > 0, but found length == 0.");
        
        for (int counter = 0; counter < dHex.length(); counter++) {
            result += intPow(32, dHex.length() - (counter+1))
                * singleStringToDHex(dHex.charAt(counter));
        }
        
        return result;
    }
    
    /**
     * Converts a single decimal number to a hexadecimal char.
     * 
     * @param dHex a decimal number (0 <= dHex < 32).
     * @return the 32 based representation of {@code dHex}.
     */
    private static char singleDHexToString(int dHex)
            throws NumberFormatException {
        if (dHex >= 0 && dHex <= 9) {
            return (char) (dHex + '0');
            
        } else if (dHex >= 10 && dHex <= 31) {
            return (char) (dHex - 10 + 'A');
            
        } else {
            throw new NumberFormatException
                ("Expected: 0 <= n <= 31, but found: " + dHex);
        }
    }
    
    /**
     * Converts a single 32 based char to a decimal number.
     * 
     * @param dHex a 32 based number ('0' <= dHex <= '9' || 'A' <= dHex <= 'V').
     * @return the decimal representation of {@code dHex}.
     */
    private static int singleStringToDHex(char dHex)
            throws NumberFormatException {
        if (dHex >= '0' && dHex <= '9') {
            return dHex - '0';
            
        } else if (dHex >= 'A' && dHex <= 'V') {
            return dHex - 'A' + 10;
            
        } else {
            throw new NumberFormatException
                ("Expected: '0' <= n <= '9' or 'A' <= n <= 'V'"
                     + ", but found: " + dHex);
        }
    }
    
    
    /**-------------------------------------------------------------------------
     * IO and file functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Lists all files in a dir.
     * @param rootDir the root dir from which the files will be listed.
     * @param listDirs whether the directories should be listed or not.
     *     Default is true.
     * @param pathSoFar the path that has been traversed so far.
     *     For expert use only!
     * 
     * @return an ArrayList containing an array of File objects of
     *     length 2 for which holds:
     *     - The first element contains the full path of the file.
     *     - The second element contains the path of the file relative to
     *       the given rootDir (excl. the rootdir and the file itself).
     * 
     * @throws IllegalArgumentException if the root dir is not a directory.
     * @throws IllegalStateException if a file found in a certain directory,
     *     is not located in that directory.
     * 
     * Furthermore is guarenteed that:
     * - If {@code listDirs}, then it holds for every directory that all its
     *   children (sub-dirs included) are listed directly below (higher index)
     *   its own entry.
     * - All files X that are part of a directory tree always occur
     *   in consecutive order (so no other directory trees that are not
     *   contained by the former one will be in this part of the array).
     * No other assumptions regarding file-order can be made on the output.
     * 
     * Note: ONLY use the THIRD function when you know what you are doing!
     */
    public static ArrayList<File[]> listFilesAndPathsFromRootDir(File rootDir)
            throws IllegalArgumentException, IllegalStateException {
        return listFilesAndPathsFromRootDir(rootDir, "", true);
    }
    
    public static ArrayList<File[]> listFilesAndPathsFromRootDir(
            File rootDir, boolean listDirs)
            throws IllegalArgumentException, IllegalStateException {
        return listFilesAndPathsFromRootDir(rootDir, "", listDirs);
    }
    
    public static ArrayList<File[]> listFilesAndPathsFromRootDir(
            File rootDir, String pathSoFar, boolean listDirs)
            throws IllegalArgumentException, IllegalStateException {
        
        if (rootDir.isFile()) {
            throw new IllegalArgumentException
                ("The file \"" + rootDir.getPath() + "\" is no dir.");
        }
        
        pathSoFar = (pathSoFar == null ? "" : pathSoFar);
        
        ArrayList<File[]> output = new ArrayList<File[]>();
        String root = rootDir.getPath();
        File[] listOfFiles = rootDir.listFiles();
        
        if (listDirs && !pathSoFar.equals("")) {
            output.add(new File[] {rootDir, new File(pathSoFar)});
        }
        
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!root.equals(listOfFiles[i].getPath()
                                 .substring(0, root.length()))) {
                throw new IllegalStateException
                    ("File \"" + listOfFiles[i]
                         + "\" could not be found in dir \"" + root + "\"");
            }
            
            if (listOfFiles[i].isFile()) {
                output.add(new File[] {listOfFiles[i],
                    new File(pathSoFar + listOfFiles[i].getParent()
                                 .substring(root.length()))});
                
            } else {
                output.addAll
                    (listFilesAndPathsFromRootDir
                         (listOfFiles[i], pathSoFar
                              + listOfFiles[i].getPath()
                              .substring(root.length()), listDirs));
            }
        }
        
        return output;
    }
    
    /**
     * Available actions constants for {@link #fileTreeAction(File, File,
     * BoolComparable , int, CopyOption...)}.
     */
    final public static int MOVE_FILES = 0;
    final public static int COPY_FILES = 1;
    final public static int DELETE_FILES = 2;
    
    /**
     * Moves an entire file tree from {@code soruce} to {@code target}.
     * 
     * @param source the source file/directory of the move.
     * @param target the target file/directory of the move.
     * @param cmp the comparator for ignoring files.
     * @param options the copy options for moving the files.
     * @throws IOException
     * 
     * Also see:
     *  - {@link Files#move(Path, Path, CopyOption...)}.
     *  - {@link #fileTreeAction(File, File, BoolComparable , int,
     *    CopyOption)}
     */
    public static void moveFileTree(File source, File target, 
            CopyOption... options)
            throws IOException {
        moveFileTree(source, target, null, options);
    }
    
    public static void moveFileTree(File source, File target,
            BoolEvaluator<File> cmp, CopyOption... options)
            throws IOException {
        fileTreeAction(source, target, cmp, MOVE_FILES, options);
    }
    
    /**
     * Copies an entire file tree from {@code soruce} to {@code target}.
     * 
     * @param source the source file/directory of the copy.
     * @param target the target file/directory of the copy.
     * @param cmp the comparator for ignoring files.
     * @param options the copy options for moving the files.
     * @throws IOException
     * 
     * Also see:
     *  - {@link Files#copy(Path, Path, CopyOption...)}
     *  - {@link #fileTreeAction(File, File, BoolComparable , int,
     *    CopyOption...)}
     */
    public static void copyFileTree(File source, File target, 
            CopyOption... options)
            throws IOException {
        copyFileTree(source, target, null, options);
    }
    
    public static void copyFileTree(File source, File target,
            BoolEvaluator<File> cmp, CopyOption... options)
            throws IOException {
        fileTreeAction(source, target, cmp, COPY_FILES, options);
    }
    
    /**
     * Deletes the entire file tree {@code soruce}.
     * 
     * @param source the source file/directory of the delete.
     * @param cmp the comparator for ignoring files.
     * @throws IOException
     * 
     * Also see:
     *  - {@link File#delete()}
     *  - {@link #fileTreeAction(File, File, BoolComparable , int,
     *    CopyOption...)}
     */
    public static void deleteFileTree(File source)
            throws IOException {
        copyFileTree(source, null);
    }
    
    public static void deleteFileTree(File source, BoolEvaluator<File> cmp)
            throws IOException {
        fileTreeAction(source, null, cmp, DELETE_FILES);
    }
    
    /**
     * Performs an action on an entire file tree.
     * The actions must be one of:
     *  - {@link #MOVE_FILES#MOVE_FILE}
     *  - {@link #COPY_FILES}
     *  - {@link #DELETE_FILES}
     * 
     * Each file is processed if, and only if
     * {@code (cmp == null || !.evaluate(file))}
     * holds for that file.
     * 
     * The comparable is also applied on directories. In this case if
     * {@code (cmp != null && cmp.evaluate(dir))}
     * holds, then all files and sub-directories in that directory are ignored.
     * 
     * Note that ignored files will not be processed.
     * 
     * @param source the root of the source file tree.
     * @param target the root of the target file tree.
     * @param cmp the comparator for ingoring files/directories.
     * @param action the action to exectute for each file.
     * @param options the copy options used for copying files.
     * @throws IOException 
     * 
     * Also see:
     *  - {@link Files#move(Path, Path, CopyOption...)}.
     *  - {@link Files#copy(Path, Path, CopyOption...)}
     *  - {@link File#delete()}
     */
    private static void fileTreeAction(File source, File target,
            BoolEvaluator<File> cmp, int action, CopyOption... options)
            throws IOException {
        if (!source.exists()) return;
        
        if (source.isFile()) {
            if (cmp == null || !cmp.evaluate(source)) {
                if (action == MOVE_FILES) {
                    Files.move(source.toPath(), target.toPath(), options);
                    
                } else if (action == COPY_FILES) {
                    Files.copy(source.toPath(), target.toPath(), options);
                    
                } else if (action == DELETE_FILES) {
                    source.delete();
                }
            }
            
        } else {
            if (target != null) target.mkdirs();
            
            for (File file : source.listFiles()) {
                if (cmp == null || !cmp.evaluate(file)) {
                    File newRoot = new File(
                            source.getPath() + FS + file.getName());
                    File newTarget = (target == null ? null : new File(
                            target.getPath() + FS + file.getName()));
                    
                    fileTreeAction(newRoot, newTarget, cmp, action, options);
                    
                    if (action == MOVE_FILES || action == DELETE_FILES) {
                        if (!file.isFile()) file.delete();
                    }
                }
            }
        }
    }
    
    /**
     * Deletes all files in a directory, or deletes a single file
     * if {@code file} is a file.
     * 
     * @param dir the directory to delete all files of.
     * @param deleteSelf whether to delete the directory itself.
     */
    public static void deleteAll(File dir, boolean deleteSelf) {
        if (dir.isFile()) {
            if (deleteSelf) dir.delete();
            return;
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteAll(file, true);
            }
        }
        
        if (deleteSelf) dir.delete();
    }
    
    /**
     * Executes an action for each file starting at the given
     * root directory.
     * If the root is a file, then it is accepted by default.
     * If the root is a directory, then
     * 
     * @param root the root to start performing the actions.
     * @param listDirs whether to perform the action on directories.
     * @param action the action to perform.
     * 
     * Note:
     * If directories should be used in actions, they are always listed before
     * any other files within that directory.
     */
    public static void forEachFile(File root, Consumer<File> action) {
        forEachFile(root, true, action);
    }
    
    public static void forEachFile(File root, boolean listDirs,
            Consumer<File> action) {
        if (root.isFile()) {
            action.accept(root);
            
        } else {
            if (listDirs) action.accept(root);
            
            File[] files = root.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (!file.isFile()) {
                    forEachFile(file, listDirs, action);

                } else {
                    action.accept(file);
                }
            }
        }
    }
    
    /**
     * Checks if a given number is in the array.
     * 
     * @param array array to look in.
     * @param number number to look for.
     * @return true iff the number is in the array.
     */
    public static boolean isInArray(int[] array, int number) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == number) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a given generic value is in the array.
     * 
     * @param array the array to look in.
     * @param value the value to look for.
     * @return true iff value is in the array.
     */
    public static <T> boolean isInArray(T[] array, T value) {
        for (T entry : array) {
            if (value.equals(entry)) return true;
        }
        
        return false;
    }
    
    /**
     * Converts an ArrayList to an array.
     * 
     * @param array the array to be converted.
     * @return an ArrayList containing every element of {@code array} and
     *     in the same order.
     */
    public static <T> ArrayList<T> toArrayList(T[] array) {
        if (array == null) return null;
        
        ArrayList<T> list = new ArrayList<T>(array.length);
        
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        
        return list;
    }
    
    /**
     * Converts a List to an array.
     * 
     * @param list the input list
     * @param classValue the input/output class type
     * @param start the first element that will be put in the array.
     * @param end the last element that will NOT be put in the array.
     * @return the elements from the output array in the same order as in the
     *     input List, and null iff the given list or class are null.
     * @throws IllegalArgumentException iff start < end.
     */
    @SuppressWarnings("unchecked")
    public static <A, B extends A> A[] listToArray(List<B> list,
                                                   Class<B> classValue) {
        return listToArray(list, classValue, 0);
    }
    
    @SuppressWarnings("unchecked")
    public static <A, B extends A> A[] listToArray(
            List<B> list, Class<B> classValue, int start) {
        return listToArray(list, classValue, start, list.size());
    }
    
    @SuppressWarnings("unchecked")
    public static <A, B extends A> A[] listToArray
            (List<B> list,Class<B> classValue, int start, int end)
           throws IllegalArgumentException {
        if (list == null || classValue == null) return null;
        if (start >= end) throw new IllegalArgumentException
            ("start(" + start + ") > end(" + end + ").");
        
        A[] array = (A[]) Array.newInstance(classValue, end - start);
        
        for (int i = start; i < list.size() && i < end; i++) {
            array[i - start] = list.get(i);
        }
        
        return (A[]) array;
    }
    
    /**
     * Converts any array to an ArrayList
     * 
     * @param array the input array
     * @param classValue the input/output class type
     * @return the elements from the output ArrayList in the same order as
     *     in the input array.
     *     Returns null iff the given array or class are null.
     * 
     * WARNING! THIS FUNCTION HAS NOT BEEN EXTENSIVLY TESTED!
     * If you get class cast exceptions (e.g. cannot convert/cast
     * Object[] to XXX[]), here's you problem.
     * 
     * Note to me: Easier default function might exist. First look
     * at those before actually trying to fix this.
     */
    @SuppressWarnings("unchecked")
    public static <A, B extends A> ArrayList<A> arrayToArrayList(
            B[] array, Class<B> classValue) {
        if (array == null) return null;
        
        List<A> list = new ArrayList<A>(array.length);
        
        for (int i = 0; i < array.length; i++) {
            list.add((A) array[i]);
        }
        
        return (ArrayList<A>) list;
    }
    
    /**
     * Makes a copy of an arrayList.
     * 
     * @param list ArrayList to copy
     * @deprecated Use the clone constructor of collection instead of this
     *     elaborate method (e.g. {@code new ArrayList<>(list)}.)
     * WARNING! THIS FUNCTION HAS NOT BEEN EXTENSIVLY TESTED!
     * 
     * Note to me: Easier default function might exist. First look
     * at those before actually trying to fix this.
     */
    @Deprecated
    public static <T> ArrayList<T> copyArrayList(ArrayList<T> list) {
        if (list == null) return null;
        
        ArrayList<T> newList = new ArrayList<T>(list.size());
        
        for (int i = 0; i < list.size(); i++) {
            newList.add(list.get(i));
        }
        
        return newList;
    }
    
    /**
     * Makes a copy of an array.
     * 
     * @param array array to copy.
     * WARNING! THIS FUNCTION HAS NOT BEEN EXTENSIVLY TESTED!
     * 
     * Note to me: Easier default function might exist. First look
     * at those before actually trying to fix this.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyArray(T[] array) {
        if (array == null) return null;
        
        T[] newArray = (T[]) Array.newInstance
            (array.getClass().getComponentType(), array.length);
        
        for (int i = 0; i < array.length; i++) {
            newArray[i] = (T) array[i];
        }
        
        return (T[]) newArray;
    }
    
    /**
     * Sleeps the current thread.
     * Mainly used to avoid the annoying catch statement.
     * 
     * @param time time in ms that the thread sleeps.
     * 
     * @see Thead#sleep(long)
     */
    public static void sleepThread(long time) {
        try {
            Thread.sleep(time);
            
        } catch (InterruptedException e) {
            System.err.println(e);
        }   
    }
    
    /**
     * Fires an ActionEvent for all ActionListeners in the array.
     * Uses as time the current time stamp.
     * 
     * See fireActioneEvents(String, long, int, ActionListener[]) for more info.
     */
    public static void fireActionEvents(Object source, String command,
                                        int modifiers, ActionListener[] als) {
        fireActionEvents(source, command, System.currentTimeMillis(),
                         modifiers, als);
    }
    
    /**
     * Fires an ActionEvent for all ActionListeners currently listening.
     * Uses another thread for execution.
     * 
     * @param source the source of the event.
     * @param command the command used for the event.
     * @param when the time when the event occured.
     * @param modifiers the modifiers for the event.
     * @param als array containing the ActionListeners that
     *     need to be notified of the event.
     */
    public static void fireActionEvents(final Object source,
                                        final String command, final long when,
                                        final int modifiers,
                                        final ActionListener[] als) {
        if (als == null) return;
        
        new Thread(source.getClass().getName() + " ActionEvent") {
            @Override
            public void run() {
                ActionEvent e = new ActionEvent(source,
                                                ActionEvent.ACTION_PERFORMED,
                                                command, when, modifiers);
                
                for (int i = 0; i < als.length; i++) {
                    if (als[i] == null) continue;
                    
                    als[i].actionPerformed(e);
                }
            }
        }.start();
    }
    
    /**
     * Converts a double to a String, having 'decimals' decimals.
     * 
     * @param num the number to be converted.
     * @param the number of decimals.
     * @return String representation of a double, having 'decimals' decimals.
     */
    public static String doubleToStringDecimals(double num, int decimals) {
        if (decimals < 0) throw new IllegalArgumentException
            ("Number of decimals was negative: " + decimals);
        
        String number = Double.toString(num);
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '.') {
                if (decimals == 0) {
                    return number.substring(0, i);
                    
                } else if (number.length() > i + decimals) {
                    return number.substring(0, i + decimals + 1);
                    
                } else {
                    while (number.length() < i + decimals + 1) {
                        number += "0";
                    }
                }
                
                return number;
            }
        }
        
        // '.' was not found
        number += ".";
        for (int i = 0; i < decimals; i++) {
            number += "0";
        }
        
        return number;
    }
    
    /**
     * Converts an Integer to a String, with zero's filled
     * till the n'th position.
     * 
     * @param i number to be converted.
     * @param n the length of the number + number of leading zeroes.
     * 
     * If the length of the number is bigger then n, then the full number
     * is returned.
     */
    public static String fillZero(int num, int n)
            throws NumberFormatException {
        return fillZero(num, n, 10);
    }
        
    public static String fillZero(int num, int n, int base)
            throws NumberFormatException {
        String number = Integer.toString(num, base);
        
        while (number.length() < n) {
            number = "0" + number;
        }
        
        return number;
    }
    
    /**
     * Adds spaces to the left of the given text till the total length
     * of the text is equal to the given size.
     * If the initial length of the text is longer then the given size,
     * no action is taken.
     * 
     * @param text text to process.
     * @param size length of the text.
     */
    public static String fillSpaceLeft(String text, int size) {
        for (int i = text.length(); i < size; i++) {
            text = " " + text;
        }
        
        return text;
    }
    
    /**
     * Adds spaces to the right of the given text till the total length
     * of the text is equal to the given size.
     * If the initial length of the text is longer then the given size,
     * no action is taken.
     * 
     * @param text text to process.
     * @param size length of the text.
     */
    public static String fillSpaceRight(String text, int size) {
        for (int i = text.length(); i < size; i++) {
            text = text + " ";
        }
        
        return text;
    }
    
    /**
     * Removes all spaces from the input String.
     * 
     * @param input String from which the spaces should be removed.
     * @return {@code input}, but then without any spaces.
     */
    public static String removeSpaces(String input) {
        // Remove all spaces from the formula.
        StringBuilder noSpaceSB = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char val = input .charAt(i);
            if (val != ' ') {
                noSpaceSB.append(val);
            }
        }
        
        return noSpaceSB.toString();
    }
    
    /**
     * Converts all spaces in the input String to spaces that
     * are visible in html.
     * 
     * @param text text to process.
     */
    public static String toHTMLSpace(String text) {
        return text.replaceAll(" ", HTML_SPACE);
    }
    
    /**
     * @param str string to check
     * @return true iff the string only contains numbers.
     * 
     * Note:
     * A null or empty string give false by default.
     */
    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) return false;
        boolean dotFound = false;
        
        for (int i = 0; i < str.length(); i++) {
            if (!isNumber(str.charAt(i))) {
                if (!dotFound && str.charAt(i) == '.') {
                    dotFound = true;
                    
                } else {
                    return false;
                }
            }
            
        }
        
        return true;
    }
    
    /**
     * @param n character to check.
     * @return whether the given character is a number
     */
    public static boolean isNumber(char n) {
        return n >= '0' && n <= '9';
    }
    
    /**
     * Calculates the power of a base.
     * Calculates the power significantly faster compared to
     * {@link Math#pow(double, double)}, but only accepts integer powers.
     * 
     * Benchmarks compared to {@link Math#pow(double, double):
     * - 2.4 times faster for very small powers an bases
     *   (e.g. between -50 and 50).
     * - 2.5 times faster for small powers and bases
     *   (e.g. between -10,000 and +10,000).
     * - 2.5 times faster for medium powers and bases
     *   (e.g. between +-10,000,000 and +-20,000,000).
     * - 2.6 times faster for large powers and bases
     *   (e.g. between +-1,073,741,823 and +-2,147,483,647).
     * - 2.6 times faster for random powers and bases
     *   (e.g. between -2,145,338,308 and +2,145,338,308).
     * - 5.7 times faster for very small powers and large bases
     *   (e.g. powers between -50 and 50,
     *         bases between +-1,073,741,823 and +-2,147,483,647).
     * - 2.6 times faster for large powers and very small bases
     *   (e.g. powers between +-1,073,741,823 and +-2,147,483,647,
     *         bases between -50 and 50).
     * 
     * Note 1:
     * The results that did't differ more then 2 of the least significant
     * digits from the result generated by the other method.
     * Note 2:
     * For each test the sample size was 214,748,360.
     * 
     * @param base the base to use.
     * @param pow the power to use.
     * @result the result of base ^ pow.
     */
    public static double intPow(double base, int pow) {
        if (pow > 0) {
            double r = base;
            double f = 1;
            
            while (pow != 1) {
                if (pow % 2 == 0) {
                    r *= r;
                    pow /= 2;
                    
                } else if (pow % 3 == 0) {
                    r *= r * r;
                    pow /= 3;
                    
                } else if (pow % 5 == 0) {
                    r *= r * r * r * r;
                    pow /= 5;
                    
                } else {
                    f *= r;
                    pow -= 1;
                }
            }
            
            return f * r;
            
        } else if (pow < 0) {
            return 1.0 / intPow(base, -pow);
            
        } else {
            return 1.0;
        }
    }
    
    // Benchmark function.
    /*
    public static void main(String[] args) {
        int n = Integer.MAX_VALUE / 100;
        System.out.println("n = " + n);
        Random r = new Random();
        
        long timeQuick = 0;
        long timeJava = 0;
        
        for (int c = 0; c < 10; c++) {
            double[] bases = new double[n];
            int[] powers = new int[n];
            for (int i = 0; i < n; i++) {
                //bases[i] = r.nextDouble() * 100 - 50;
                //powers[i] = r.nextInt(100) - 50;
                
                //bases[i] = (r.nextBoolean() ? 1 : -1) * (r.nextDouble() * 10_000_000 + 10_000_000);
                //powers[i] = (r.nextBoolean() ? 1 : -1) * (r.nextInt(10_000_000) + 10_000_000);
                
                //bases[i] = (r.nextBoolean() ? 1 : -1) * (r.nextDouble() * 10_000_000 + 10_000_000);
                //powers[i] = (r.nextBoolean() ? 1 : -1) * (r.nextInt(10_000_000) + 10_000_000);
                
                //bases[i] = (r.nextBoolean() ? 1 : -1) * (r.nextDouble() * Integer.MAX_VALUE / 2 + Integer.MAX_VALUE / 2);
                //powers[i] = (r.nextBoolean() ? 1 : -1) * (r.nextInt(Integer.MAX_VALUE / 2 ) + Integer.MAX_VALUE / 2 );
                
                bases[i] = (r.nextDouble() * 2 - 1) * (Integer.MAX_VALUE / 1.001);
                powers[i] = (int) ((r.nextDouble() * 2 - 1) * (Integer.MAX_VALUE / 1.001));
                
            }
            
            long beginA = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                intPow(bases[i], powers[i]);
            }
            long endA = System.currentTimeMillis();
            long dA = endA - beginA;
            timeQuick += dA;
            System.out.println("intPow: " + dA);
            System.out.println("Total time: " + timeQuick);
            
            
            long beginB = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                Math.pow(bases[i], powers[i]);
            }
            long endB = System.currentTimeMillis();
            long dB = endB - beginB;
            timeJava += dB;
            System.out.println("Math.pow: " + dB);
            System.out.println("Total time: " + timeJava);
            
            System.out.println("ratio = " + ((double) dB) / (double) dA);
            System.out.println("-----------------");
        }
        System.out.println("ratio = " + ((double) timeJava) / (double) timeQuick);
    }*/
    
    /**
     * Tries to acquire the lock. If the time limit was reached, throws
     * an {@link IllegalStateException}.
     * 
     * @param time the time in milliseconds the lock will be tried.
     * @param msg the logging message for the 
     * @return {@code true} if the lock was aquired within the time limit
     *     without being interrupted. {@code false} if there was an
     *     {@link InterruptedException}.
     * 
     * Note that this only handles the acquiring the lock. The implementing
     * code still should unlock the lock, preferrable using a try-finally
     * construction.
     */
    public static boolean tryLock(long time, String msg, Lock lock, Class<?> c)
            throws IllegalStateException {
        try {
            if (!lock.tryLock(time, TimeUnit.MILLISECONDS)) {
                Logger.write("Error in " + c.getName() + ": lock was in use.",
                        Logger.Type.ERROR);
                throw new IllegalStateException(
                        c.getName() + " lock was in use.");
            }
            
        } catch (InterruptedException ex) {
            Logger.write(msg);
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if a given object is an array.
     * Returns true if so, false otherwise.
     * 
     * @param obj the Object to be tested.
     * @return true if {@code obj} is an array. False otherwise.
     */
    public static boolean isArray(Object obj) {
        if (obj == null) return false;
        return obj.getClass().isArray();
    }
    
    /**
     * Randomly shuffles an array.
     * 
     * @param in the input array.
     * @param rnd the used Random object.
     */
    public static <V> V[] shuffleArray(V[] in) {
        return shuffleArray(in, new Random());
    }
    
    public static <V> V[] shuffleArray(V[] in, Random rnd) {
        for (int i = in.length; i > 1; i--) {
            swap(in, i - 1, rnd.nextInt(in.length));
        }
        
        return in;
    }
    
    /**
     * Makes a copy of a Class array, keeping the type variable.
     * 
     * @param array Class array with type to copy.
     * WARNING! THIS FUNCTION HAS NOT BEEN EXTENSIVLY TESTED YET!
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T>[] copyArray(Class<T>[] array) {
        if (array == null) return null;
        
        Class<T>[] newArray = (Class<T>[]) new Class[array.length];
        
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        
        return newArray;
    }
    
    /**
     * Performs an in place swap between two elements of an array.
     * 
     * @param arr the array where the swap occurs.
     * @param i the first element of the swap.
     * @param j the second element of the swap.
     * @return {@code arr}, but then with the elements {@code i}
     *     and {@code j} swapped.
     * @throws throws ArrayIndexOutOfBoundsException if {@code i} or {@code j}
     *     are invallid indices of {@code arr}.
     */
    public static <V> V[] swap(V[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        if (i == j) return arr;
        V tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return (V[]) arr;
    }
    
    /**
     * Compares two arrays. Does a deep check.
     * 
     * @param a array to compare.
     * @param b array to compare.
     * @return whether the two given arrays {@code a} and {@code b} are
     *     fully identical.
     */
    public static boolean compareDeepArray(Object[] a, Object[] b) {
        if (a == null || b == null) return a == b;
        if (a.length != b.length) return false;
        
        for (int i = 0; i < a.length; i++) {
            boolean isArrayA = a[i].getClass().isArray();
            boolean isArrayB = b[i].getClass().isArray();
            
            if (isArrayA && isArrayB) {
                // If both are an array, recursivly determine whether
                // they are equal.
                if (!compareDeepArray(safeObjArrCast(a[i]),
                                      safeObjArrCast(b[i]))) {
                    return false;
                }
                
            } else if (isArrayA ^ isArrayB) {
                // If either of them is an array, they can never be equal.
                return false;
                
            } else {
                // If both are plain elements, simply check for equality.
                if (!a[i].equals(b[i])) return false;
            }
        }
        
        return true;
    }
    
    /**
     * Makes a deep clone of the given value.
     * If the value is an array, recursivly make a clone of each element
     * and put them in a new array.
     * 
     * Supported types:
     * - {@code value} contains toolsX.Cloneable elements, then each element is
     *   simply cloned by invoking the clone method.
     * - {@code value} contains java.lang.Cloneable elements, then each element
     *   is cloned by bypassing the private access modifier of clone method.
     *   Note that the clone method should be overridden by that class.
     * - {@code value} contains a primitive data type (boolean, char, byte,
     *   short, int, long, float, double) or is a String.
     * 
     * @param value the value to be cloned.
     * @return a clone of the value. This means that {@code value != \return
     *     && value.equals(\return)} will hold afterwards (assuming that the
     *     equals method is implemented correctly).
     * @throws IllegalStateException iff
     *     the clone method could not terminate normally.
     * @throws UnsupportedOperationException iff
     *     the provided value does not contain one of the supported types.
     */
    @SuppressWarnings({"BooleanConstructorCall", "BoxingBoxedValue",
        "RedundantStringConstructorCall"})
    public static <V> V deepClone(V value)
            throws IllegalStateException, UnsupportedOperationException {
        if (value == null) return null;
        
        if (value.getClass().isArray()) {
            return (V) deepArrayClone(safeObjArrCast(value));
            
        } else {
            if (value instanceof Cloneable) {
                return (V) ((Cloneable) value).clone();
                
            } else if (value instanceof java.lang.Cloneable) {
                try {
                    Method clone = value.getClass().getMethod("clone");
                    clone.setAccessible(false);
                    return (V) clone.invoke(value);
                    
                } catch (NoSuchMethodException |
                         SecurityException |
                         IllegalAccessException e) {
                    // When the method was not reacheable.
                    Logger.write(new Object[] {
                        "Unaccessable clone method of object \""
                            + value.toString() + "\"!", e
                    });
                    
                    throw new IllegalStateException
                        ("Could not finish cloning! Last element: "
                             + value.toString());
                    
                } catch (InvocationTargetException e) {
                    // When the clone method threw an exception.
                    Logger.write(new Object[] {
                        "An error occured while cloning the object \""
                            + value.toString() + "\":", e.getCause()
                    });
                    
                    throw new IllegalStateException
                        ("An error occured while cloning the object \""
                            + value.toString() + "\".");
                }
                
            } else {
                if (value instanceof Boolean) {
                    return (V) new Boolean((Boolean) value);
                    
                } else if (value instanceof Character) {
                    return (V) new Character((Character) value);
                    
                } else if (value instanceof Byte) {
                    return (V) new Byte((Byte) value);
                    
                } else if (value instanceof Short) {
                    return (V) new Short((Short) value);
                    
                } else if (value instanceof Integer) {
                    return (V) new Integer((Integer) value);
                    
                } else if (value instanceof Long) {
                    return (V) new Long((Long) value);
                    
                } else if (value instanceof Float) {
                    return (V) new Float((Float) value);
                    
                } else if (value instanceof Double) {
                    return (V) new Double((Double) value);
                    
                } else if (value instanceof String) {
                    return (V) new String((String) value);
                }
                
                // For anything else.
                throw new UnsupportedOperationException
                    ("Expected a cloneable object, but found: "
                         + value.getClass().toString());
            }
            
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] deepArrayClone(T[] objArr)
            throws IllegalStateException, IllegalArgumentException {
        T[] newObjArr = (T[]) Array.newInstance(objArr
                                                    .getClass()
                                                    .getComponentType(),
                                                objArr.length);
        for (int i = 0; i < objArr.length; i++) {
            newObjArr[i] = (T) deepClone(objArr[i]);
        }
        
        return (T[]) newObjArr;
    }
    
    /**
     * Calculates a hash code for an object with the given dependant objects.
     * 
     * @param objArr immutable dependant variables of a class.
     * @return a hash code for the class, given the dependant objects.
     * 
     * Note: this method will always return the same number if the input
     * objects are equal. This also holds for null objects.
     * If no objects are given (i.e. {@code objArr == null}),
     * then 0 is returned.
     */
    public static int calcHashCode(Object... objArr) {
        if (objArr == null) return 0;
        
        int result = 41;
        
        for (Object obj : objArr) {
            int c;
            
            if (obj == null) {
                c = 0;
                
            } else if (obj instanceof Boolean) {
                c = ((Boolean) obj ? 1 : 0);
                
            } else if (obj instanceof Byte ||
                       obj instanceof Character ||
                       obj instanceof Short ||
                       obj instanceof Integer) {
                c = (int) obj;
                
            } else if (obj instanceof Long) {
                c = (int) Math.pow((Long) obj, (Long) obj >>> 32);
                
            } else if (obj instanceof Float) {
                c = Float.floatToIntBits((Float) obj);
                
            } else if (obj instanceof Double) {
                Long value = Double.doubleToLongBits((Double) obj);
                c = (int) Math.pow(value, value >>> 32);
                
            } else if (isArray(obj)) {
                c = calcHashCode(safeObjArrCast(obj));
                
            } else {
                c = obj.hashCode();
            }
            
            result = 37 * result + c;
        }
        
        return result;
    }
    
    /**
     * Calculates the dimensions of the array.
     * 
     * @param obj array to calculate the dimensions of.
     * @param isEqual whether each sub-level of the array as the same dimension.
     *     (So new {@code int[5][5]} yields true, but
     *      {@code int[][] {int[4], int[5]}} yields false).
     * @return an array containing the dimensions of the array, where the
     *     lowest index denotes the topmost level. When the array is unequal,
     *     the maximum value for each level is taken.
     *     (so {@code int[][] {int[4], int[5]}} yields {@code int[] {2, 5}}).
     */
    public static int[] calcDimArray(Object obj) {
        return calcDimArray(obj, false);
    }
    
    public static int[] calcDimArray(Object obj, boolean isEqual) {
        return calcDimArray(obj, isEqual, calcDepthArray(obj) - 1);
    }
    
    private static int[] calcDimArray(Object obj, boolean isEqual, int depth) {
        if (obj == null || depth < 0) return new int[0];
        
        if (obj.getClass().isArray()) {
            int[] dim = new int[depth + 1];
            dim[0] = Array.getLength(obj);
            
            if (Array.getLength(obj) == 0) {
                return dim;
            }
            
            if (isEqual) {
                int[] oldDim = calcDimArray(Array.get(obj, 0),
                                            isEqual, depth - 1);
                for (int i = 0; i < oldDim.length; i++) {
                    dim[i + 1] = oldDim[i];
                }
                
                return dim;
                
            } else {
                for (int i = 0; i < Array.getLength(obj); i++) {
                    int[] oldDim = calcDimArray(Array.get(obj, i),
                                                isEqual, depth - 1);
                    for (int j = 0; j < oldDim.length; j++) {
                        if (oldDim[j] > dim[j + 1]) {
                            dim[j + 1] = oldDim[j];
                        }
                    }
                }
                
                return dim;
                
            }
            
        } else {
            return new int[0];
        }
    }
    
    /**
     * Calculates the depth of the given array.
     * 
     * @param obj the array to calculate the depth of.
     * @return the depth of the given array.
     */
    public static int calcDepthArray(Object obj) {
        if (obj == null) return -1;
        String name = obj.getClass().getName();
        
        int depth = -1;
        while (depth < name.length() && name.charAt(++depth) == '[') {}
        
        return depth;
    }
    
    /**
     * Safely casts an Object to an Object[].
     * 
     * @param obj object to be casted.
     * @return a safly casted version of obj.
     * @throws IllegalArgumentException iff obj is not an array.
     * 
     * Note: if obj is a 1D array of a primative type (e.g. int[]),
     * then a new {@code Object[]} is created that contains the same values.
     */
    public static Object[] safeObjArrCast(Object obj) {
        if (!obj.getClass().isArray()) {
            throw new IllegalArgumentException
                ("Expected an array, but found: " + obj.getClass().getName());
        }
        
        try {
            // Try to cast to {@code Object[]}.
            return (Object[]) obj;
            
        } catch (ClassCastException e) {
            // If it fails (only in case of a 1D primative array),
            // Create a new Object[] and copy the values of obj.
            int length = Array.getLength(obj);
            Object[] objArr = new Object[length];
            for (int i = 0; i < length; i++) {
                objArr[i] = Array.get(obj, i);
            }
            
            return objArr;
        }
    }
    
    /**
     * Creates a map from a single (key, value) pair.
     * 
     * @param key the key of the map
     * @param value the value of the map
     * @return 
     */
    public static <K, V> Map<K, V> createMap(K key, V value) {
        Map<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }
    
    /**
     * Calculates whether two rectangles with their corresponding
     * location and size intersect. Boundaries are included.
     * 
     * @param x1 x-coord of rectangle 1.
     * @param y1 y-coord of rectangle 1.
     * @param w1 width of rectangle 1.
     * @param h1 height of rectangle 1.
     * @param x2 x-coord of rectangle 2.
     * @param y2 y-coord of rectangle 2.
     * @param w2 width of rectangle 2.
     * @param h2 height of rectangle 2.
     * @return whether the two rectangle intersect.
     */
    public static boolean intersects(int x1, int y1, int w1, int h1,
                                      int x2, int y2, int w2, int h2) {
        return ((x1 <= x2 && x1 + w1 >= x2) ||
                (x1 <= x2 + w2 && x1 + w1 >= x2 + w2)) &&
               ((y1 <= y2 && y1 + h1 >= y2) ||
                (y1 <= y2 + h2 && y1 + h1 >= y2 + h2));
    }
    
    /**
     * Counts the number of wrapped lines in the given text component.
     * All credits go to:
     * <url>http://tech.chitgoks.com/2012/03/21/get-line-count-of-jtextarea
     * -including-word-wrap-lines/</url>
     * 
     * @param txtComp the component to count the lines of.
     * @return the number of lines in the component, including wrapped lines.
     */
    public static int countWrappedLines(JTextComponent txtComp) {
	Font font = txtComp.getFont();
        FontMetrics fontMetrics = txtComp.getFontMetrics(font);
        int fontHeight = fontMetrics.getHeight();
        int lineCount;
        try {
            int height = txtComp.modelToView(txtComp.getDocument()
                    .getEndPosition().getOffset() - 1).y;
            lineCount = height / fontHeight + 1;
            
        } catch (BadLocationException e) {
            Logger.write(e);
            lineCount = 0;
        }
        
        return lineCount;
    }
    
    /**
     * Adds all elements of the given array to the given collection.
     * 
     * @param <V> the class type of the array elements.
     * @param <C> the collection class type.
     * @param col the collection.
     * @param array the array.
     * @return {@code col}.
     */
    public static <V, C extends Collection<? super V>> C addAll(
            C col, V[] array) {
        if (col == null || array == null) throw new NullPointerException();
        forEach(array, (V v) -> col.add(v));
        return col;
    }
    
    /**
     * Executes the given action for each element of the given array.
     * 
     * @param <V> the type of elements in the array.
     * @param array the array the for each statement will be applied.
     * @param action the action that is executed for each element.
     */
    public static <V> void forEach(V[] array, Consumer<? super V> action) {
        if (action == null) throw new NullPointerException();
        for (int i = 0; i < array.length; i++) {
            action.accept(array[i]);
        }
    }
    
    /**
     * Interface for a consumer that includes the source.
     * See {@link Consumer} for more info.
     * Should be used in constructors.
     * 
     * @param <S> the class of the source.
     * @param <V> the class of the value.
     */
    @FunctionalInterface
    public static interface SourceConsumer<S, V> {
        public void accept(S source, V value);
    }
    
    /**
     * Executes an action for each element of a collection.
     * Usage is identical compared to {@link Iterable#forEach(Consumer)}.
     * For more info look there.
     * Should be used in constructors.
     * 
     * @param <V> the class of the value.
     * @param <C> the class of the collection.
     * @param col the collection.
     * @param action the action that will be executed for all elements.
     */
    public static <V, C extends Collection<V>> void forEach(
            C col, SourceConsumer<? super C, ? super V> action) {
        for (V v : col) {
            action.accept(col, v);
        }
    }
    
    /**
     * Interface that can be used to create a target class from another
     * source object.
     * Should be used in constructors.
     * 
     * @param <S> the class of the source.
     * @param <T> the class of the target.
     * @param <V> the class of the value.
     */
    @FunctionalInterface
    public static interface CreatorConsumer<S, T, V> {
        public void accept(S source, T target, V value);
    }
    
    /**
     * Function to create a collection from an input array.
     * Should be used in constructors.
     * 
     * @param <V> the class of the value.
     * @param <C> the class of the collection.
     * @param source the source array.
     * @param dest the destination collection.
     * @param action the action to execute.
     * @return {@code dest}.
     */
    public static <V, C extends Collection<?>> C createCollection(
            V[] source, C dest, CreatorConsumer<V[], C, V> action) {
        for (V v : source) {
            action.accept(source, dest, v);
        }
        
        return dest;
    }
    
    /**
     * Interface that to execute some code to
     * generate an element in the constructor.
     * 
     * @param <V> the class of the returned value.
     */
    @FunctionalInterface
    public static interface ConstructorExecutor<V> {
        public V execute();
    }
    
    /**
     * Function to use the {@link ConstructorExecutor} class.
     * Should be used in constructors.
     * 
     * @param <V> the class of the value.
     * @param exe the executor.
     * @return the value that was generated by the executor.
     */
    public static <V> V createObject(ConstructorExecutor<V> exe) {
        return exe.execute();
    }
    
    
    /**
     * Comparable interface that returns a {@code boolean}
     * instead of an {@code int}.
     * 
     * @param <V> the value class to evaluate.
     */
    @FunctionalInterface
    public static interface BoolEvaluator<V> {
        public boolean evaluate(V v);
    }
    
    /**
     * Prints the current stack trace on a different thread such that it can
     * be used in swing-environments with short event-burst with no stuttering.
     * 
     * @param stream the stream that will be used to print the stack trace on.
     * @param thread the thread to print the stack trace of.
     */
    public static void printStackTrace() {
        printStackTrace(System.err, Thread.currentThread());
    }
    
    public static void printStackTrace(PrintStream stream) {
        printStackTrace(stream, Thread.currentThread());
    }
    
    public static void printStackTrace(Thread thread) {
        printStackTrace(System.err, thread);
    }
    
    public static void printStackTrace(PrintStream stream, Thread thread) {
        final StackTraceElement[] ste = thread.getStackTrace();
        new Thread() {
            @Override
            public void run() {
                synchronized(stream) {
                    stream.println();
                    stream.println(Arrays.toString(ste)
                            .replaceAll(", ", "," + LS));
                }
            }
        }.start();
    }
    
    /**
     * Logs the current stack trace.
     * 
     * @param thread the thread to get the stacktrace of.
     */
    public static void logStackTrace() {
        logStackTrace(Thread.currentThread());
    }
    
    public static void logStackTrace(Thread thread) {
        final StackTraceElement[] ste = thread.getStackTrace();
        Logger.write(LS + Arrays.toString(ste).replaceAll(", ", "," + LS));
    }
    
    
    /**
     * Simple class that iterates over an array.
     * Has no modification checks.
     */
    public static class ArrayIterator<V>
            implements Iterator<V> {
        final private V[] array;
        private int i = 0;
        
        public ArrayIterator(V[] array) {
            this.array = array;
        }
        
        @Override
        public boolean hasNext() {
            return i < array.length;
        }
        
        @Override
        public V next() {
            if (!hasNext())
                throw new NoSuchElementException("No next element available! "
                        + "(length = " + array.length + ").");
            
            return array[i++];
        }
        
        @Override
        public void remove() {
            array[i - 1] = null;
        }
        
    }
    
    
    /**
     * Class that iterates over multiple iterators consecutively.
     * @param <V> class value of the iterators.
     */
    public static class MultiIterator<V>
            implements Iterator<V> {
        
        // The iterators.
        final private Iterator<? extends V>[] its;
        // Iterator counter.
        private int counter = 0;
        
        public MultiIterator(Iterator<? extends V>... its) {
            if (its == null) throw new NullPointerException("Null iterator!");
            this.its = its;
        }
        
        @Override
        public boolean hasNext() {
            while (counter < its.length && !its[counter].hasNext()) {
                counter++;
            }
            return its[counter].hasNext();
        }
        
        @Override
        public V next() {
            if (!hasNext()) throw new NoSuchElementException(
                    "No more elements!");
            return its[counter].next();
        }
        
        
    }
    
    
    /**
     * Iterator class for returning all elements in a collection or an
     * iterable at random.
     * 
     * @param <V> the type of the returned classes.
     */
    public static class RandomIterator<V>
            implements Iterator<V> {
        
        private Iterator<V> it;
        
        
        /**
         * @param iterable the iterable to create this iterator of.
         */
        public RandomIterator(Iterable<V> iterable) {
            List<V> list = new ArrayList<V>();
            for (V value : iterable) {
                list.add(value);
            }
            Collections.shuffle(list);
            it = list.iterator();
        }
        
        /**
         * @param col the collection to create this iterator of.
         */
        public RandomIterator(Collection<V> col) {
            V[] arr = (V[]) new Object[col.size()];
            int i = 0;
            for (V value : col) {
                arr[i++] = value;
            }
            shuffleArray(arr);
            it = new ArrayIterator(arr);
        }
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
        
        @Override
        public V next() {
            return it.next();
        }
        
        @Override
        public void remove() {
            it.remove();
        }
        
        
    }
    
    
}