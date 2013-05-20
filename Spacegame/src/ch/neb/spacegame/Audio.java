package ch.neb.spacegame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Audio {

	private static Map<String, Clip> audioCache = new HashMap<String, Clip>();
	private static boolean enabled = true;

	public static void loopSound(String sound) {
		if (enabled)
			loadSound(sound).loop(Clip.LOOP_CONTINUOUSLY);
	}

	/**
	 * Plays the given sound file. The sound files are cached locally, if a sound is already beeing played, it will be stopped and played again.
	 * 
	 * @param sound
	 */
	public static void playSound(String sound) {
		if (enabled) {
			Clip clip = loadSound(sound);

			if (!clip.isRunning()) {
				clip.setFramePosition(0);
				clip.start();
			}
		}
	}

	public static void stop() {
		for (Clip clip : audioCache.values()) {
			clip.stop();
		}
	}

	public synchronized static void start() {
		if (enabled) {
			for (Clip clip : audioCache.values()) {
				clip.start();
			}
		}

	}

	public static synchronized void setEnabled(boolean isEnabled) {
		enabled = isEnabled;
		if (!enabled) {
			stop();
		}
	}

	private static Clip loadSound(String path) {
		if (audioCache.get(path) != null) {
			return audioCache.get(path);
		}

		try {
			File file = new File(SpaceGame.class.getClassLoader().getResource(path).toURI());
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			audioCache.put(path, clip);
			return clip;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static void toggleEnabled() {
		setEnabled(!enabled);

		System.out.println(enabled);
	}

	public static void reset(String string) {
		final Clip clip = audioCache.get(string);
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
		}

	}

	public static void changeVolumne(String string, float v) {
		Clip loadSound = loadSound(string);
		final FloatControl gainControl = (FloatControl) loadSound.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(v);
	}
}
