package ch.neb.spacegame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {

	private static Map<String, Clip> audioCache = new HashMap<String, Clip>();

	public static void loopSound(String sound) {
		loadSound(sound).loop(Clip.LOOP_CONTINUOUSLY);
	}

	/**
	 * Plays the given sound file. The sound files are cached locally, if a sound is already beeing played, it will be stopped and played again.
	 * 
	 * @param sound
	 */
	public static void playSound(String sound) {
		Clip clip = loadSound(sound);

		if (!clip.isRunning()) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	public static Clip loadSound(String path) {
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
}
