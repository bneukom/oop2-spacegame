package ch.neb.spacegame;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Keys {

	private Map<Integer, Key> keyMappings = new HashMap<>();

	public final class Key {
		public boolean isDown = false;
	}

	public Key exit = new Key();
	public Key forward = new Key();
	public Key backward = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key powerBoost = new Key();
	public Key shield = new Key();

	public Keys() {
		keyMappings.put(KeyEvent.VK_ESCAPE, exit);
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
	}

	public void keyDown(int keyCode) {
		final Key key = keyMappings.get(keyCode);
		if (key != null) {
			key.isDown = true;
		}
	}

	public void keyUp(int keyCode) {
		final Key key = keyMappings.get(keyCode);
		if (key != null) {
			key.isDown = false;
		}
	}

}
