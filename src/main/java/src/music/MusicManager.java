
package src.music;


// Own packages

import src.GS;
import src.tools.MultiTool;
import src.tools.log.Logger;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Java packages


public class MusicManager {
    final public static int MUSIC_BACKGROUND = 1;
    final public static int MUSIC_SFX = 2;
    
    // Initialize the background and sound effect clips.
    final private static Map<File, ClipInfo> bgClipInfo = new HashMap<>();
    final private static Map<File, ClipInfo> sfxClipInfo = new HashMap<>();
    static {
        File backDir = new File(GS.MUSIC_DIR + "back" + GS.FS);
        MultiTool.forEachFile(backDir, false, (File file) -> {
            bgClipInfo.put(file, new ClipInfo(file, MUSIC_BACKGROUND));
        });
        
        File sfxDir = new File(GS.MUSIC_DIR + "sfx" + GS.FS);
        MultiTool.forEachFile(sfxDir, (File file) -> {
            sfxClipInfo.put(file, new ClipInfo(file, MUSIC_SFX));
        });
    }
    
    // Music manager lock.
    final private static Lock lock = new ReentrantLock();
    final private static Condition fadeThreadCondition = lock.newCondition();
    
    // The current background and sfx volume.
    private static float bgVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    
    // The background and sfx volume targets.
    private static float bgVolumeTarget = 1.0f;
    private static float sfxVolumeTarget = 1.0f;
    
    // The volume change rate.
    private static float bgChange = 0.01f;
    private static float sfxChange = 0.01f;
    
    // The only background clip that is running.
    private static Clip bgClip = null;
    // Set containing all sound effects that are currently playering.
    private static Set<Clip> sfxClips = new HashSet<>();
    
    // The default background music.
    private static ClipInfo defaultBG = null;
    
    // Map keeping track of the line listeners per clip.
    private static Map<Clip, LineListener> lineListeners = new HashMap<>();
    
    // When muted, no music will be played.
    private static boolean mute = false;
    
    
    /**-------------------------------------------------------------------------
     * Music fade thread.
     * -------------------------------------------------------------------------
     */
    final private static Thread fadeThread = new Thread("Music-fade-thread") {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    
                    /*
                    
                    public static void setSFXVolume(float gain) {
                        sfxVolume = sfxVolumeTarget = transformGain(gain);

                        for (Clip clip : sfxClips) {
                            setClipVolume(sfxVolume, clip);
                        }
                    }
                    */
                    boolean done;
                    try {
                        boolean bgDone;
                        if (bgClip == null) {
                            // No background clip was set.
                            bgVolume = bgVolumeTarget;
                            bgDone = true;
                            
                        } else if (bgVolume == bgVolumeTarget) {
                            // Value is already at the target,
                            // so no action needed.
                            bgDone = true;
                            
                        } else if (Math.abs(bgVolume - bgVolumeTarget)
                                < bgChange) {
                            // The next update will complete the cycle.
                            setClipVolume(bgVolume = bgVolumeTarget, bgClip);
                            bgDone = true;
                            
                        } else {
                            // Current and target is different,
                            // more then one update needed.
                            bgDone = false;
                            if (bgVolume < bgVolumeTarget) {
                                bgVolume += bgChange;
                            } else {
                                bgVolume -= bgChange;
                            }
                            setClipVolume(bgVolume, bgClip);
                        }
                        
                        boolean sfxDone;
                        if (sfxVolume == sfxVolumeTarget) {
                            // Value is already at the target,
                            // so no action needed.
                            sfxDone = true;
                            
                        } else if (Math.abs(sfxVolume - sfxVolumeTarget)
                                < sfxChange) {
                            // The next update will complete the cycle.
                            for (Clip clip : sfxClips) {
                                setClipVolume(sfxVolume = sfxVolumeTarget,
                                        clip);
                            }
                            sfxDone = true;
                            
                        } else {
                            // Current and target is different,
                            // more then one update needed.
                            sfxDone = false;
                            if (sfxVolume < sfxVolumeTarget) {
                                sfxVolume += sfxChange;
                            } else {
                                sfxVolume -= sfxChange;
                            }
                            
                            for (Clip clip : sfxClips) {
                                setClipVolume(sfxVolume, clip);
                            }
                        }
                        
                        if (done = (bgDone && sfxDone)) {
                            fadeThreadCondition.await();
                        }
                        
                    } finally {
                        lock.unlock();
                    }
                    
                    if (!done) Thread.sleep(50);
                    
                } catch (InterruptedException e) {
                    Logger.write("Attempted to interrupte music-fade-thread",
                            Logger.Type.ERROR);
                }
            }
        }
    };
    static {
        fadeThread.start();
    }
    
    
    /**-------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    // Private constructor for the static singleton design pattern.
    private MusicManager() { }
    
    
    /**-------------------------------------------------------------------------
     * Fuctions.
     * -------------------------------------------------------------------------
     */
    /**
     * Plays the given file as the given type.
     * 
     * @param fileName the file name of the music to play.
     * @param type the type of music. Must be one of {@lin #MUSIC_BACKGROUND}
     *     or {@link #MUSIC_SFX}.
     * @return {@code true} if the file starts playing.
     * 
     * Note: the file name should ONLY be the name of the file, so no path.
     */
    public static boolean play(String fileName, int type) {
        File file = new File(GS.MUSIC_DIR
                + (type == MUSIC_SFX ? "sfx" : "back")
                + GS.FS + fileName);
        ClipInfo ci = (type == MUSIC_SFX
                ? sfxClipInfo.get(file)
                : bgClipInfo.get(file));
        if (ci == null) return false;
        
        play(ci.createClip(), type);
        return true;
    }
    
    /**
     * Plays the given clip as the given type.
     * 
     * @param clip the clip to play.
     * @param type the type of the clip is.
     */
    private static void play(Clip clip, int type) {
        lock.lock();
        try {
            LineListener ll;
            if (type == MUSIC_SFX) {
                ll = (LineEvent e) -> {
                    if (e.getType() != LineEvent.Type.STOP) return;
                    LineListener oldLL = lineListeners.remove(clip);
                    clip.removeLineListener(oldLL);
                    sfxClips.remove(clip);
                };
                
                sfxClips.add(clip);
                setClipVolume(sfxVolume, clip);
                
            } else {
                ll = (LineEvent e) -> {
                    if (e.getType() != LineEvent.Type.STOP) return;
                    clip.setFramePosition(0);
                    clip.start();
                };
                stopBackground();
                bgClip = clip;
                setClipVolume(bgVolume, clip);
            }
            
            clip.addLineListener(ll);
            lineListeners.put(clip, ll);
            clip.start();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Stops the background music.
     */
    public static void stopBackground() {
        lock.lock();
        try {
            stopClip(bgClip);
            bgClip = null;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Stops all sound effects.
     */
    public static void stopAllSFX() {
        lock.lock();
        try {
            for (Clip sfxClip : sfxClips) {
                sfxClips.remove(sfxClip);
                sfxClip.stop();
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * @param clip the clip to stop playing.
     */
    private static void stopClip(Clip clip) {
        if (clip != null) {
            LineListener ll = lineListeners.get(clip);
            if (ll != null) {
                clip.removeLineListener(ll);
                lineListeners.remove(clip);
            }
            
            clip.stop();
        }
    }
    
    /**
     * @return {@code true} if all music is muted.
     */
    public static boolean isMuted() {
        return mute;
    }
    
    /**
     * (Un-)mutes all music.
     * 
     * @param mute whether to mute or unmute the music.
     */
    public static void mute(boolean mute) {
        if (MusicManager.mute == mute) return;
        if (MusicManager.mute = mute) {
            stopBackground();
            stopAllSFX();
            
        } else {
            if (defaultBG != null) {
                play(defaultBG.createClip(), MUSIC_BACKGROUND);
            }
        }
    }
    
    /**
     * Scales the ratio value to a logarithmic scale instead of linear.
     * 
     * @param ratio the ratio of the volume. Should be between {@code 0.0f}
     *     and {@code 1.0f}.
     * @return the transformed value.
     */
    private static float transformGain(float ratio) {
        if (ratio > 1.0f) ratio = 1.0f;
        if (ratio < 0.0f) ratio = 0.0f;
        
        // To factor in the logarithmic effects of sound.
        return (float) (Math.log(49*ratio + 1) / Math.log(50.0));
    }
    
    /**
     * Sets the volume of the given clip.
     * 
     * @param volume the volume to set the clip at.
     * @param clip the clip to set the volume of.
     */
    private static void setClipVolume(float volume, Clip clip) {
        FloatControl control = (FloatControl)
                clip.getControl(FloatControl.Type.MASTER_GAIN);
        float max = control.getMaximum();
        float min = control.getMinimum();
        control.setValue(min + (max - min) * volume);
    }
    
    /**
     * Sets the volume of the background music.
     * 
     * @param gain the volume of the background music.
     *     Should be between {@code 0.0f} and {@code 1.0f}.
     */
    public static void setBackgroundVolume(float gain) {
        lock.lock();
        try {
            bgVolume = bgVolumeTarget = transformGain(gain);
            setClipVolume(bgVolume, bgClip);
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Fades the background music to the given volume.
     * 
     * @param gain the target volume of the background music.
     */
    public static void fadeBackgroundTo(float gain) {
        bgVolumeTarget = transformGain(gain);
        lock.lock();
        try {
            fadeThreadCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Sets the volume of the sfx music.
     * 
     * @param gain the volume of the sfx music.
     *     Should be between {@code 0.0f} and {@code 1.0f}.
     */
    public static void setSFXVolume(float gain) {
        lock.lock();
        try {
            sfxVolume = sfxVolumeTarget = transformGain(gain);
            
            for (Clip clip : sfxClips) {
                setClipVolume(sfxVolume, clip);
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Fades the sound effects to the given volume.
     * 
     * @param gain the target volume of the sound effects.
     */
    public static void fadeSFXTo(float gain) {
        sfxVolumeTarget = transformGain(gain);
        lock.lock();
        try {
            fadeThreadCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Sets the default background music.
     * @param fileName the file name of the music file to set as default
     *     background music.
     * @return {@code true} if the given music file was set as default.
     */
    public static boolean setDefaultBackground(String fileName) {
        if (fileName == null) {
            stopBackground();
            return true;
            
        } else {
            ClipInfo li = bgClipInfo.get(new File(GS.MUSIC_DIR + "back"
                    + GS.FS + fileName));
            if (li != null) {
                defaultBG = li;
                play(fileName, MUSIC_BACKGROUND);
                return true;
            }
            
            return false;
        }
    }
    
    public static void main(String[] args) {
        GS.init();
        // note to self: sfx mute is not working correctly!
        MusicManager.setDefaultBackground("intro.wav");
        MusicManager.play("intro.wav", MUSIC_BACKGROUND);
        MusicManager.play("molten_mind.wav", MUSIC_SFX);
        
        MultiTool.sleepThread(3000);
        MusicManager.fadeBackgroundTo(0.0f);
        MusicManager.fadeSFXTo(0.0f);
        MultiTool.sleepThread(3000);
        MusicManager.fadeBackgroundTo(1.0f);
        MusicManager.fadeSFXTo(1.0f);
        MultiTool.sleepThread(3000);
        MusicManager.setBackgroundVolume(0.5f);
        
        MultiTool.sleepThread(3000);
        MusicManager.mute(true);
        MultiTool.sleepThread(3000);
        MusicManager.mute(false);
        
        MultiTool.sleepThread(1000);
        MusicManager.play("molten_mind.wav", MUSIC_SFX);
        MultiTool.sleepThread(1000);
        MusicManager.setSFXVolume(0.5f);
        MultiTool.sleepThread(1000);
        MusicManager.setSFXVolume(1.0f);
        MultiTool.sleepThread(1000);
    }
    
    
}