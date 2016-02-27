package ch.fhnw.oop2.spacegame;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {

	private Map<Integer, Key> keyMappings = new HashMap<>();

	public final class Key {
		public boolean isDown = false;
		public boolean typed = false;
	}

	public Key escape = new Key();
	public Key forward = new Key();
	public Key backward = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key powerBoost = new Key();
	public Key shield = new Key();
	public Key pause = new Key();
	public Key showFps = new Key();
	public Key sound = new Key();
	public Key fullscreen = new Key();

	public Keyboard() {
		keyMappings.put(KeyEvent.VK_ESCAPE, escape);
		keyMappings.put(KeyEvent.VK_W, forward);
		keyMappings.put(KeyEvent.VK_UP, forward);
		keyMappings.put(KeyEvent.VK_S, backward);
		keyMappings.put(KeyEvent.VK_DOWN, backward);
		keyMappings.put(KeyEvent.VK_LEFT, left);
		keyMappings.put(KeyEvent.VK_A, left);
		keyMappings.put(KeyEvent.VK_RIGHT, right);
		keyMappings.put(KeyEvent.VK_D, right);
		keyMappings.put(KeyEvent.VK_SPACE, shield);
		keyMappings.put(KeyEvent.VK_SHIFT, powerBoost);
		keyMappings.put(KeyEvent.VK_P, pause);
		keyMappings.put(KeyEvent.VK_I, showFps);
		keyMappings.put(KeyEvent.VK_O, sound);
		keyMappings.put(KeyEvent.VK_L, fullscreen);
	}

	public void update() {
		for (Key key : keyMappings.values()) {
			key.typed = false;
		}
	}

	public synchronized void keyDown(int keyCode) {
		final Key key = keyMappings.get(keyCode);
		if (key != null) {
			key.isDown = true;
		}
	}

	public synchronized void keyUp(int keyCode) {
		final Key key = keyMappings.get(keyCode);
		if (key != null) {
			key.isDown = false;
		}
	}

	public synchronized void keyTyped(int keyCode) {
		final Key key = keyMappings.get(keyCode);
		if (key != null) {
			key.typed = true;
		}
	}

}
