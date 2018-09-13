
package src.music;


// Own packages

import src.tools.MultiTool;
import src.tools.log.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// Java packages


public class ClipInfo {
    final private File file;
    final private int type;
    
    /**
     * Constructor.
     * Creats a new clip info object that can create a new clip
     * 
     * @param file the file the music is stored. Must be a .wav file.
     * @param type the type of music. Is one of {@link #MUSIC_BACKGROUND} or
     *     {@link #MUSIC_SFX}.
     */
    public ClipInfo(File file, int type) {
        this.file = file;
        this.type = type;
    }
    
    
    /**
     * @return a fresh clip of the music clip described by this object.
     */
    public Clip createClip() {
        Clip clip = null;
        try {
            AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
        } catch (UnsupportedAudioFileException | IOException
                | LineUnavailableException e) {
            Logger.write(e);
        }
        
        return clip;
    }
    
    /**
     * @return the file the music is stored.
     */
    public File getFile() {
        return file;
    }
    
    /**
     * @return the type of music. Is one of {@link #MUSIC_BACKGROUND} or
     *     {@link #MUSIC_SFX}.
     */
    public int getType() {
        return type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof String)) return false;
        return obj.equals(file.getName());
    }
    
    @Override
    public int hashCode() {
        // the {@code * 103} is added to increase the distance between
        // the two types (note that 103 is prime).
        return MultiTool.calcHashCode(file, type * 103);
    }
    
    
}