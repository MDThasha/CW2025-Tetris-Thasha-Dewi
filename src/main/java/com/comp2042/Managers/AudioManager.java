package com.comp2042.Managers;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/** Simple audio manager using javax.sound.sampled for short sound effects (Clip) and background music.
 * <p>Provides methods to load and play named sound effects, load/play/pause/stop background music,
 * and control global volumes. </p> */
public class AudioManager {
    // Sound effects
    /** Global volume for sound effects (range 0.0 - 1.0). default effect .8*/
    private static float volume = 0.8f;
    /** Map of loaded sound effect Clips keyed by a logical name. */
    private static final Map<String, Clip> audioClips = new HashMap<>();

    // Background music
    /** Clip used to play background music (single track). */
    private static Clip backgroundMusic;
    /** Volume for background music (range 0.0 - 1.0). Lower by default to avoid loud music jump scare. */
    private static float musicVolume = 0.1f; // Lower default volume for music
    /** Flag indicating whether the background music is currently playing. */
    private static boolean isMusicPlaying = false;

    /** Return the current global sound-effect volume.
     * @return volume in the range 0.0 - 1.0 */
    public static float getVolume() {
        return volume;
    }

    /** Load a sound effect from the given resource path and store it under the provided name.
     * <p> The method attempts to open the resource as an AudioInputStream and create a Clip which is
     * cached for later playback via {@link #playSound(String)}.</p>
     * @param name logical name to reference this sound effect
     * @param resourcePath classpath resource path to the audio file */
    public static void loadSound(String name, String resourcePath) {
        try {
            URL resource = AudioManager.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("Could not find audio resource: " + resourcePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Set initial volume
            setClipVolume(clip, volume);

            audioClips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    /** Load and start background music from a classpath resource.
     * <p>The loaded clip is looped continuously. If loading or playback fails, the error is printed
     * to stderr and the method returns quietly.</p>
     * @param resourcePath classpath resource path to the background audio file (e.g. "/audio/bg.wav")*/
    public static void loadBackgroundMusic(String resourcePath) {
        try {
            URL resource = AudioManager.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("Could not find background music: " + resourcePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(resource);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);

            // Set initial music volume
            setClipVolume(backgroundMusic, musicVolume);

            // Loop continuously
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true;
            System.out.println("Background music loaded and playing");
        } catch (Exception e) {
            System.err.println("Error loading background music");
            e.printStackTrace();
        }
    }

    /** Ensure background music is playing (start looping if not already playing).
     * <p>This will call {@code loop(Clip.LOOP_CONTINUOUSLY)} if the background clip exists and was stopped.</p>*/
    public static void playBackgroundMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true;
        }
    }

    /** Set the background music volume.
     * @param value volume in range 0.0 - 1.0 */
    public static void setMusicVolume(float value) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, value));
        if (backgroundMusic != null) {
            setClipVolume(backgroundMusic, musicVolume);
        }
    }

    /** Play a previously loaded sound effect by name from the beginning.
     * <p>If the named clip is not found this method does nothing.</p>
     * @param name the logical name used when loading the sound */
    public static void playSound(String name) {
        Clip clip = audioClips.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /** Set the global sound-effect volume and apply it to all loaded Clips.
     * @param value volume set*/
    public static void setVolume(float value) {
        volume = value;
        for (Clip clip : audioClips.values()) {
            setClipVolume(clip, value);
        }
    }

    /** Convert a normalized volume (0.0 - 1.0) to the Clip's MASTER_GAIN dB value and apply it.
     * <p>Performs a logarithmic conversion to decibels and clamps to the control's supported range.</p>
     * @param clip the Clip whose volume should be adjusted
     * @param volumeLevel normalized volume in range 0.0 - 1.0 */
    private static void setClipVolume(Clip clip, float volumeLevel) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) { // makesure got clip
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // volume control interface
            float dB = (float) (Math.log(volumeLevel) / Math.log(10.0) * 20.0); // convert linear to decibles dB = 20 * log10(volumeLevel)
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)); // Clamp dB value to supported range
            gainControl.setValue(dB); // aplpy the setting to volume
        }
    }
}