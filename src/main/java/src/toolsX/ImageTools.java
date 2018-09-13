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
//todo
package tools;


// Java imports
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


/**
 * Currently under development.
 * Use with care and check runtimes!
 */
@Deprecated
public class ImageTools {
    public static enum SimpleAction {
        // These three shoulc not be used since they are way much slower then the java version.
        // Internal note: try with Raster class.
        ROTATE_90_RIGHT, ROTATE_90_LEFT, ROTATE_180,
            MIRROR_VERTICAL, MIRROR_HORIZONTAL, MIRROR_DIAGONAL_1, MIRROR_DIAGONAL_2;
    }
    
    /* 
     * Manipulates an image in a simple way. Actions are:
     *  - rotate 90 degrees right
     *  - rotate 90 degrees left
     *  - rotate 180 degrees
     *  - mirror on the vertical axis
     *  - mirror on the horizontal axis
     *  - mirror on the diagonal (type 1) from the upper-left corner
     *    to the lower-right corner
     *  - mirror on the diagonal (type 2) from the lower-left corner
     *    to the upper-right corner
     */
    public static BufferedImage simpleAction(BufferedImage biIn,
                                             SimpleAction rotationType) {
        if (biIn == null) return null;
        if (rotationType == null) return biIn;
        
        BufferedImage biOut = null;
        //Graphics2D g2d = biOut.createGraphics();
        int width = biIn.getWidth();
        int height = biIn.getHeight();
        int type = (biIn.getType() == 13
                        ? BufferedImage.TYPE_4BYTE_ABGR
                        : biIn.getType());
        int[] rgb = new int[width * height];
        int[] newRgb = new int[width * height];
        
        // tmp
        long time1 = System.currentTimeMillis();
        // Get the abgr raster and manipulate it.
        biIn.getRGB(0, 0, width, height, rgb, 0, width);
        long time2 = System.currentTimeMillis();
        long time3 = 0;
        long time4 = 0;
        
        if (rotationType == SimpleAction.ROTATE_90_RIGHT) {
            biOut = new BufferedImage(height, width, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[x * height + (height - 1 - y)] = rgb[x + y * width];
                }
            }
            // tmp
            time3 = System.currentTimeMillis();
            biOut.setRGB(0, 0, height, width, newRgb, 0, height);
            time4 = System.currentTimeMillis();
            
        } else if (rotationType == SimpleAction.ROTATE_90_LEFT) {
            biOut = new BufferedImage(height, width, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[y + (width - 1 - x) * height] = rgb[x + y * width];
                }
            }
            
            biOut.setRGB(0, 0, height, width, newRgb, 0, height);
            
        } else if (rotationType == SimpleAction.ROTATE_180) {
            biOut = new BufferedImage(width, height, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[(height - 1 - y) * width + (width - 1 - x)]
                        = rgb[x + y * width];
                }
            }
            
            biOut.setRGB(0, 0, width, height, newRgb, 0, width);
            
        } else if (rotationType == SimpleAction.MIRROR_VERTICAL) {
            biOut = new BufferedImage(width, height, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[y * width + (width - 1 - x)] = rgb[x + y * width];
                }
            }
            
            biOut.setRGB(0, 0, width, height, newRgb, 0, width);
            
        } else if (rotationType == SimpleAction.MIRROR_HORIZONTAL) {
            biOut = new BufferedImage(width, height, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[(height - 1 - y) * width + x] = rgb[x + y * width];
                }
            }
            
            biOut.setRGB(0, 0, width, height, newRgb, 0, width);
            
        } else if (rotationType == SimpleAction.MIRROR_DIAGONAL_1) {
            biOut = new BufferedImage(height, width, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[y + x * height] = rgb[x + y * width];
                }
            }
            
            biOut.setRGB(0, 0, height, width, newRgb, 0, height);
            
        } else if (rotationType == SimpleAction.MIRROR_DIAGONAL_2) {
            biOut = new BufferedImage(height, width, type);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    newRgb[(height - 1 - y) + (width - 1 - x) * height]
                        = rgb[x + y * width];
                }
            }
            
            // Set the manipulated agbr raster to the output
            biOut.setRGB(0, 0, height, width, newRgb, 0, height);
        }
        //System.out.println((time2 - time1) + ", " + (time3 - time2) + ", " + (time4 - time3));
        
        return biOut;
    }
    
    /* 
     * Turns an image over an angle.
     * The angle is in radians! Use Math.toRadians(double deg) for conversions.
     * 
     * @param biIn image on which the rotation has to be used on.
     * @param angle the angle on radians the image has to be rotated over.
     * @param type denotes the algorithm used. Must be one of:
     *  - AffineTransformOp.TYPE_NEAREST_NEIGHBOR
     *  - AffineTransformOp.TYPE_BILINEAR
     *  - AffineTransformOp.TYPE_BICUBIC
     * 
     * @return a new BufferedImage containing the rotated image of biIn
     *     without corner-loss. The size of the image depends both on the size
     *     of the biIn and the angle.
     */
    public static BufferedImage rotateImage(BufferedImage biIn, double angle, int type) {
        int width  = biIn.getWidth();
        int height = biIn.getHeight();
        int newWidth  = (int) (width * Math.abs(Math.cos(angle)) + height * Math.abs(Math.sin(angle)) + 0.5);
        int newHeight = (int) (width * Math.abs(Math.sin(angle)) + height * Math.abs(Math.cos(angle)) + 0.5);
        int usedWidth  = (width  >= newWidth  ? width  : newWidth );
        int usedHeight = (height >= newHeight ? height : newHeight);
        
        BufferedImage biNew = new BufferedImage(usedWidth, usedHeight, BufferedImage.TYPE_4BYTE_ABGR);
        
        Graphics2D g2d = biNew.createGraphics();
        g2d.drawImage(biIn, (usedWidth - width) / 2, (usedHeight - height) / 2, null);
        g2d.dispose();
        
        BufferedImage turnedImage = new AffineTransformOp
            (AffineTransform.getRotateInstance
                 (angle, usedWidth/2, usedHeight/2), type
            ).filter(biNew, null);
        
        if (newWidth == usedWidth && newHeight == usedHeight) {
            return turnedImage;
            
        } else {
            BufferedImage biOut = new BufferedImage(newWidth , newHeight , BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2dFinal = biOut.createGraphics();
            g2dFinal.drawImage(turnedImage, -(usedWidth - newWidth) / 2, -(usedHeight - newHeight) / 2, null);
            /*
            g2dFinal.setStroke(new BasicStroke(5));
            g2dFinal.drawLine(0       , 0        , newWidth, 0        );
            g2dFinal.drawLine(0       , 0        , 0       , newHeight);
            g2dFinal.drawLine(newWidth, 0        , newWidth, newHeight);
            g2dFinal.drawLine(0       , newHeight, newWidth, newHeight);
            */
            
            //g2dFinal.drawImage(turnedImage, 0, 0, null);
            g2dFinal.dispose();
            
            return biOut;
        }
    }
    /*
    public static BufferedImage rotateImage(BufferedImage biIn, double angle, int type) {
        int width  = biIn.getWidth();
        int height = biIn.getHeight();
        double cosAngleAbs = Math.abs(Math.cos(angle));
        double sinAngleAbs = Math.abs(Math.sin(angle));
        
        int newWidth  = (int) (width * cosAngleAbs + height * sinAngleAbs + 0.5);
        int newHeight = (int) (width * sinAngleAbs + height * cosAngleAbs + 0.5);
        
        int usedWidth  = Math.max(width, newWidth);
        int usedHeight = Math.max(height, newHeight);
        
        // Create a new image that is turned without corner loss.
        BufferedImage biExtraSpace = new BufferedImage(usedWidth, usedHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = biExtraSpace.createGraphics();
        // tmp
        g2d.fillRect(0, 0, usedWidth, usedHeight);
        
        // width, height
        g2d.drawImage(biIn, (usedWidth - Math.min(width, newWidth)) / 2, (usedHeight - Math.min(height, newHeight)) / 2, null);
        g2d.dispose();
        
        BufferedImage turnedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
        
        System.out.println("old: " + width + ", " + height);
        System.out.println("new: " + newWidth + ", " + newHeight);
        System.out.println("used: " + usedWidth + ", " + usedHeight);
        
        return new AffineTransformOp
            (AffineTransform.getRotateInstance
                 (angle, usedWidth/2, usedHeight/2), type
            ).filter(biExtraSpace, turnedImage);
        //return turnedImage;
        /*System.out.println("old: " + width + ", " + height);
        System.out.println("new: " + newWidth + ", " + newHeight);
        System.out.println("used: " + usedWidth + ", " + usedHeight);
        System.out.println("img: " + turnedImage.getWidth() + ", " + turnedImage.getHeight());
        return turnedImage;*/
        
        // Now cut the extra white of the corners.
        /*BufferedImage result = new BufferedImage(newWidth / 2, newHeight / 2, BufferedImage.TYPE_4BYTE_ABGR);
        
        g2d = result.createGraphics();
        g2d.drawImage(turnedImage,
                      (newWidth - turnedImage.getWidth()) / 2,
                      (newHeight - turnedImage.getHeight()) / 2,
                      null);
        g2d.fillRect(0, 0, result.getWidth(), result.getHeight());
        g2d.dispose();*/
        
        //int dWidth = (turnedImage.getWidth() - newWidth);
        //int dHeight = (turnedImage.getHeight() - newHeight);
        //return turnedImage.getSubimage(0, 0,
        //                               turnedImage.getWidth() - dWidth, turnedImage.getHeight() - dHeight);// + dWidth, newHeight + dHeight);
        
        /*
        System.out.println("old: " + width + ", " + height);
        System.out.println("new: " + newWidth + ", " + newHeight);
        System.out.println("new: " + usedWidth + ", " + usedHeight);
        System.out.println("img: " + turnedImage.getWidth() + ", " + turnedImage.getHeight());
        
        if (newWidth == turnedImage.getWidth() && newHeight == turnedImage.getHeight()) {
            return turnedImage;
            
        } else {
            BufferedImage biOut = new BufferedImage(newWidth, newHeight , BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2dFinal = biOut.createGraphics();
            g2dFinal.fillRect(0, 0, newWidth, newHeight);
            g2dFinal.drawImage(turnedImage, (newWidth - turnedImage.getWidth()) / 2, (newHeight - turnedImage.getHeight()) / 2, null);
            g2dFinal.dispose();
            
            return biOut;
        }*//*
    }*/

    
    
    /* 
     * Converts an Image object to an BufferedImage object in a resource
     * efficient way.
     * 
     * @param img the image to be converted
     * @param type the new image type iff a new {@code BufferedImage} object
     *     is created.
     * @return {@code null} if {@code img == null},
     *     the same object if img is an instance of {@code BufferedImage}, or
     *     a new {@code BufferedImage} containing the image of imgferedImage} 
     *         generated by {@code imageDeepCopy(Image, int)} in the other cases.
     */
    public static BufferedImage toBufferedImage(Image img) {
        return toBufferedImage(img, BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    public static BufferedImage toBufferedImage(Image img, int type) {
        if (img == null)  return null;
        if (img instanceof BufferedImage) return (BufferedImage) img; 
        return imageDeepCopy(img, type);
    }
    
    /* 
     * Makes a deep copy of an image.
     * Use this to ensure that the given Image object is a new instance.
     * Note that {@code source != imageDeepCopy(source)} always holds.
     * 
     * @param source the image to be converted
     * @param type the type of the new BufferedImage
     * @return a new BufferedImage of the source
     */
    public static BufferedImage imageDeepCopy(Image source) {
        return imageDeepCopy(source, BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    public static BufferedImage imageDeepCopy(Image source, int type) {
        if (source == null) return null;
        
        BufferedImage clone = new BufferedImage(source.getWidth(null),
                                                source.getHeight(null),
                                                type);
        Graphics2D g2d = clone.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
       
        return clone;
    }
    
    
    
    
    
    /*
    public static void tmpSetup() {
        JFrame mainFrame = new JFrame("test");
        mainFrame.setLayout(null);
        mainFrame.setSize(500, 500);
        mainFrame.setLocation(1000, 200);
        
        final String image = System.getProperty("user.dir") + "\\toolsX\\black_square.png";
        
        try {
            LoadImages2.loadImage(image, "testImage");
            
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        JPanel testPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                try {
                    BufferedImage img1 = LoadImages2.ensureLoadedAndGetImage(image, "testImage")[0][0];
                    BufferedImage img2 = LoadImages2.ensureLoadedAndGetImage(image, "testImage")[0][0];
                    
                    //BufferedImage img2 = simpleAction(LoadImages2.ensureLoadedAndGetImage(image, "testImage")[0][0], SimpleAction.ROTATE_90_RIGHT);
                    
                    long time1 = System.currentTimeMillis();
                    BufferedImage img3 = simpleAction(img1, SimpleAction.ROTATE_90_RIGHT);
                    g.drawImage(img3, 0, 0, null);
                    long time2 = System.currentTimeMillis();
                    BufferedImage img4 = rotateImage(img2, Math.toRadians(90), AffineTransformOp.TYPE_BILINEAR);
                    g.drawImage(img4, img3.getWidth() + 10, 0, null);
                    long time3 = System.currentTimeMillis();
                    
                    //System.out.println((time2 - time1) + ", " + (time3 - time2));
                    //System.out.println(time1 + ", " + time2 + ", " + time3);
                    
                    
                    //g.drawImage(img2, 0, 0, null);
                    
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        testPanel.setSize(500, 500);
        testPanel.setLocation(0, 0);
        mainFrame.add(testPanel);
        
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.repaint();
    }
    
    
    public static void main(String[] args) {
        tmpSetup();
    }*/
}