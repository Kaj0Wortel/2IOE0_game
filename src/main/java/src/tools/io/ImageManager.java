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


// Own imports

import src.GS;
import src.tools.MultiTool;
import src.tools.log.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// Java imports


/**
 * Function class for easier handeling of images.
 * Applies dynamic loading of requested image sheets and
 * unloads images sheets that were not used within the time limit.
 * Allows pausing the timer.
 * 
 * Uses the static singleton design pattern.
 * 
 * @author Kaj Wortel (0991586)
 */
public class ImageManager {
    final private static Map<String, Token> tokenMap = new HashMap<>();
    final private static DelayQueue<Delay> queue = new DelayQueue<>();
    
    // By default, wait 1 minute before deleting sheets.
    private static long removeDelay = 60;
    private static TimeUnit timeUnit = TimeUnit.SECONDS;
    
    /**
     * Token class for the active images.
     */
    private abstract static class Token {
        final private String fileName;
        final private String idName;
        
        /**
         * Constructor.
         * 
         * @param shortFileName the short file name location of the sheet.
         * @param idName the id name of the sheet.
         * 
         * For more info, see {@link LoadImages2#loadImage(String, String,
         * int, int, int, int, int, int)}
         */
        private Token(String shortFileName, String idName) {
            this.fileName = GS.IMG_DIR + shortFileName;
            this.idName = idName;
        }
        
        
        public String getFileName() {
            return fileName;
        }
        
        /**
         * @return the id name of this token.
         */
        public String getIDName() {
            return idName;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Token)) return false;
            Token token = (Token) obj;
            return fileName.equals(token.fileName) &&
                    idName.equals(token.idName);
        }
        
        @Override
        public int hashCode() {
            return MultiTool.calcHashCode(fileName, idName);
        }
        
        /**
         * @return the image sheet represented by this token.
         */
        public abstract BufferedImage[][] getSheet();
        
        
    }
    
    
    /**
     * Token class for unequally divided images on a sheet.
     * Uses {@link LoadImages2#loadImage(String, String, Rectangle[][])}
     * for reading the images.
     */
    private static class UnequalToken
            extends Token {
        final private Rectangle[][] recs;
        
        private UnequalToken(String shortFileName, String idName,
                Rectangle[][] recs) {
            super(shortFileName, idName);
            
            this.recs = recs;
        }

        @Override
        public BufferedImage[][] getSheet() {
            try {
                return LoadImages2.ensureLoadedAndGetImage(
                        getFileName(), getIDName(), recs
                );
                
            } catch (IOException | IllegalArgumentException e) {
                Logger.write(e);
                return null;
            }
        }
        
        
    }
    
    
    /**
     * Token class for equally divided images on a sheet.
     * Uses {@link LoadImages2#loadImage(String, String, int, int, int,
     * int, int, int)} for reading the images.
     */
    private static class EqualToken
            extends Token {
        final private int startX;
        final private int startY;
        final private int endX;
        final private int endY;
        final private int width;
        final private int height;
        
        final private int numImgWidth;
        final private int numImgHeight;
        
        
        /**
         * Constructor.
         * 
         * For more info, see {@link LoadImages2#loadImage(String, String,
         * int, int, int, int, int, int)}
         */
        private EqualToken(String shortFileName, String idName,
                int startX, int startY,
                int endX, int endY,
                int width, int height) {
            super(shortFileName, idName);
            
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.width = width;
            this.height = height;
            
            this.numImgWidth = (endX - startX) / width;
            this.numImgHeight = (endY - startY) / height;
        }
        
        @Override
        public BufferedImage[][] getSheet() {
            try {
                return LoadImages2.ensureLoadedAndGetImage(
                        getFileName(), getIDName(),
                        startX, startY,
                        endX, endY,
                        width, height);
                
            } catch (IOException | IllegalArgumentException e) {
                Logger.write(e);
                return null;
            }
        }
        
        /**
         * Determines the width, counted as the number of images.
         * 
         * @return the width of the sheet, counted as the number of images.
         */
        public int getNumImgWidth() {
            return numImgWidth;
        }
        
        /**
         * Determines the height, counted as the number of images.
         * 
         * @return the height of the sheet, counted as the number of images.
         */
        public int getNumImgHeight() {
            return numImgHeight;
        }
        
        
    }
    
    /**
     * Class for keeping track of the delays.
     */
    private static class Delay
            implements Delayed {
        final private long delayMillis;
        final private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        final private Token token;
        final private long startTimeMillis = System.currentTimeMillis();
        
        private Delay(long delay, TimeUnit timeUnit, Token token) {
            this.delayMillis = this.timeUnit.convert(delay, timeUnit);
            this.token = token;
        }
        
        @Override
        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(delayMillis
                    - (System.currentTimeMillis() - startTimeMillis),
                    this.timeUnit);
        }
        
        @Override
        public int compareTo(Delayed delayed) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS)
                    - delayed.getDelay(TimeUnit.MILLISECONDS));
        }
        
        /**
         * @return the token represented by this delay class.
         */
        public Token getToken() {
            return token;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Delay)) return false;
            Delay delay = (Delay) obj;
            return token.equals(delay.token);
        }
        
        
    }
    
    
    // Private constructor due to the static single design pattern.
    private ImageManager() { }
    
    
    /**
     * Sets the delay after which an inactive image sheet will be deleted.
     * Note that requesting the image sheet will reset the timer.
     * 
     * @param delay the new delay.
     * 
     * Note that the change WILL NOT take effect for already queued tokens.
     * Request them again via {@link getImage(String).}
     */
    public static void setDelay(long delay, TimeUnit timeUnit) {
        if (delay <= 0)
            throw new IllegalArgumentException(
                    "Expected a delay bigger then 0, but found " + delay);
        removeDelay = delay;
        ImageManager.timeUnit = timeUnit;
    }
    
    /**
     * Registers a image sheet for later use.
     * 
     * @param shortFileName the short file name of the image sheet
     *     (e.g. starting at the {@code img} directory).
     * @param idName the name that can be used for later refference.
     * @param startX the pixel x-coordinate of the start location in the image.
     *     (incl.) Default is 0.
     * @param startY the pixel y-coordinate of the start location in the image.
     *     (incl.) Default is 0.
     * @param endX the pixel x-coordinate of the end location of the image.
     *     (excl.) Default is -1.
     * @param endY the pixel y-coordinate of the end location of the image.
     *     (excl.) Default is -1.
     * @param width the width of each subimage.
     * @param height the height of each subimage.
     * 
     * @see LoadImages2#loadImage(String, String, int, int, int, int, int, int)
     * 
     * Note: when there already exists an id with the same name,
     * but a different token, the previous token WILL BE OVERWRITTEN!
     * Choose your ID's with caution!
     */
    public static void registerSheet(String shortFileName, String idName,
            int width, int height) {
        registerSheet(shortFileName, idName, 0, 0, -1, -1, width, height);
    }
    
    public static void registerSheet(String shortFileName, String idName,
            int startX, int startY,
            int endX, int endY,
            int width, int height) {
        tokenMap.put(idName,
                new EqualToken(shortFileName, idName,
                        startX, startY,
                        endX, endY,
                        width, height));
    }
    
    /**
     * Registers a image sheet for later use.
     * 
     * @param shortFileName the short file name of the image sheet
     *     (e.g. starting at the {@code img} directory).
     * @param idName the name that can be used for later refference.
     * @param recs the locations and sizes of the images to load.
     * 
     * Note: when there already exists an id with the same name,
     * but a different token, the previous token WILL BE OVERWRITTEN!
     * Choose your ID's with caution!
     * 
     * @see LoadImages2.loadImage(String, String, Rectangle[][])
     */
    public static void registerSheet(String shortFileName, String idName,
            Rectangle[][] recs) {
        tokenMap.put(idName, new UnequalToken(shortFileName, idName, recs));
    }
    
    /**
     * @param idName the id name the sheet was registered with.
     * @return the sheet that was registered with the given id name.
     * @throws NoSuchElementException iff the sheet was not yet registered.
     */
    public static BufferedImage[][] getSheet(String idName)
            throws NoSuchElementException {
        if (idName == null) return null;
        
        Token token = tokenMap.get(idName);
        if (token == null)
            throw new NoSuchElementException(
                    "The token has not yet been registered. Req. id: "
                            + idName);
        Delay delay = new Delay(removeDelay, timeUnit, token);
        // Remove earlier queued delays for this image.
        queue.remove(delay);
        queue.add(delay);
        
        return token.getSheet();
    }
    
    /**
     * @param idName the id name of the sheet.
     * @param x the x-coord of the image to be returned.
     * @param y the y-coord of the image to be returned.
     * @return the image at the sheet denoted by {@code idName} at
     *     the coords {@code (x, y)}.
     */
    public static BufferedImage getImage(String idName, int x, int y) {
        if (idName == null) return null;
        
        BufferedImage[][] sheet = getSheet(idName);
        imgCoordBoundCheck(idName, sheet, x, y);
        
        return sheet[x][y];
    }
    
    /**
     * @param idName the id name of the sheet.
     * @param i the index for horizontal retrieval of the image.
     * @return the image at the sheet denoted by {@code idName} at index
     *     {@code i}, where the 2D array is checked left to right, then
     *     up to down.
     * 
     * See {@link #getVertImage(String, int)} for getting an image
     * the other way around.
     * See {@link getImage(String, int, int)} for getting an image
     * by direct x- and y-coords.
     */
    public static BufferedImage getHoriImage(String idName, int i) {
        if (idName == null) return null;
        
        BufferedImage[][] sheet = getSheet(idName);
        imgCoordBoundCheck(idName, sheet, i);
        
        int width = sheet.length;
        return sheet[i % width][i / width];
    }
    
    /**
     * @param idName the id name of the sheet.
     * @param i the index for vertical retrieval of the image.
     * @return the image at the sheet denoted by {@code idName} at index
     *     {@code i}, where the 2D array is checked left to up to down, then
     *     left to right.
     * 
     * See {@link #getHoriImage(String, int)} for getting an image
     * the other way around.
     * See {@link getImage(String, int, int)} for getting an image
     * by direct x- and y-coords.
     */
    public static BufferedImage getVertImage(String idName, int i) {
        if (idName == null) return null;
        
        BufferedImage[][] sheet = getSheet(idName);
        imgCoordBoundCheck(idName, sheet, i);
        
        int height = sheet[0].length;
        return sheet[i / height][i % height];
    }
    
    /**
     * Checks whether the given index {@code i} is a vallid for
     * the given sheet {@code sheet}.
     * 
     * @param idName the id name of the image. Only for debugging purposes.
     * @param sheet the sheet to check the index of.
     * @param i the value representing the index.
     */
    private static void imgCoordBoundCheck(String idName,
            BufferedImage[][] sheet, int i) {
        int width = sheet.length;
        if (width == 0)
            throw new IllegalStateException(
                    "Empty image array found (width == 0), idName=" + idName);
        
        int height = sheet[0].length;
        if (height == 0)
            throw new IllegalStateException(
                    "Empty image array found (height == 0), idName=" + idName);
        
        if (i < 0)
            throw new IllegalArgumentException(
                    "Expected an index larger equal then 0, but found: " + i
                            + ". idName=" + idName);
        
        if (i > width * height)
            throw new IllegalArgumentException(
                    "Expected an index less or equal then width(" + width
                            + ") * height(" + height + ") = "
                            + (width * height) + ", but found index " + i
                            + ". idName=" + idName);
    }
    /**
     * Checks whether the given coords {@code x} and {@code y} are vallid
     * indices for the given sheet {@code sheet}.
     * 
     * @param idName the id name of the image. Only for debugging purposes.
     * @param sheet the sheet to check the index of.
     * @param x the x-coord of the image.
     * @param y the y-coord of the image.
     */
    private static void imgCoordBoundCheck(String idName,
            BufferedImage[][] sheet, int x, int y) {
        
        if (sheet == null)
            throw new NullPointerException(
                    "Sheet found was null. idName=" + idName);
        
        int width = sheet.length;
        if (width == 0)
            throw new IllegalStateException(
                    "Empty image array found (width == 0), idName=" + idName);
        
        int height = sheet[0].length;
        if (height == 0)
            throw new IllegalStateException(
                    "Empty image array found (height == 0), idName=" + idName);
        
        if (x < 0)
            throw new IllegalArgumentException(
                    "Expected an x-coord larger equal then 0, but found: " + x
                            + ". idName=" + idName);
        if (y < 0)
            throw new IllegalArgumentException(
                    "Expected an y-coord larger equal then 0, but found: " + y
                            + ". idName=" + idName);
        
        if (x > width)
            throw new IllegalArgumentException(
                    "Expected an x-coord less or equal to the width(" + width
                            + "), but found x-coord " + x
                            + ". idName=" + idName);
        if (y > height)
            throw new IllegalArgumentException(
                    "Expected an y-coord less or equal to the height(" + height
                            + "), but found y-coord " + y
                            + ". idName=" + idName);
    }
    
    /**
     * Clears both all loaded images and all registered images.
     */
    public static void clearAll() {
        queue.clear();
        tokenMap.clear();
        LoadImages2.clear();
    }
    
    /**
     * @param idName the id name of the sheet.
     * @return the width of the sheet, counted as the number of images.
     * @throws UnsupportedOperationException iff corresponding image
     *     is unequal.
     */
    public static int getNumImgWidth(String idName) {
        Token token = tokenMap.get(idName);
        if (token instanceof EqualToken) {
            return ((EqualToken) token).getNumImgWidth();
            
        } else {
            throw new UnsupportedOperationException(
                    "Operation is not supported for unequal"
                            + "distributed images.");
        }
    }
    
    /**
     * @param idName the id name of the sheet.
     * @return the height of the sheet, counted as the number of images.
     * @throws UnsupportedOperationException iff corresponding image
     *     is unequal.
     */
    public static int getNumImgHeight(String idName) {
        Token token = tokenMap.get(idName);
        if (token instanceof EqualToken) {
            return ((EqualToken) token).getNumImgHeight();
        } else {
            throw new UnsupportedOperationException(
                    "Operation is not supported for unequal "
                            + "distributed images.");
        }
    }
    
    /**
     * @param idName the id name of the sheet.
     * @return the total number of images in the sheet.
     */
    public static int getNumImg(String idName) {
        return getNumImgWidth(idName) * getNumImgHeight(idName);
    }
    
    /*
    public static void main(String[] args) {
        registerSheet("items.png", "ITEMS", 0, 0, -1, -1, 32, 32);
        
        JFrame frame = new JFrame("test");
        frame.setLayout(null);
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(ImageManager.getVertImage("ITEMS", 15), 0, 0, null);
            }
        };
        frame.add(panel);
        
        frame.setSize(500, 500);
        frame.setLocation(0, 0);
        panel.setSize(450, 450);
        panel.setLocation(25, 25);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }*/
    
    
    /**
     * Upon static class initialisation, create an inactive thread that
     * waits until an image becomes invallid and removes this image from RAM.
     */
    static {
        new Thread(ImageManager.class.getName() + " Thread") {
            @Override
            public void run() {
                // The update thread should have minimal priority.
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                
                while (true) {
                    try {
                        Delay delay = queue.poll(1, TimeUnit.DAYS);
                        if (delay == null) continue;
                        LoadImages2.removeImage(delay.getToken().getIDName());
                        
                    } catch (InterruptedException e) {
                        Logger.write(e);
                    }
                }
            }
        }.start();
    }
    
    
}
