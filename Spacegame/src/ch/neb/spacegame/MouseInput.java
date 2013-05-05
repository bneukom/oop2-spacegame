package ch.neb.spacegame;

import ch.neb.spacegame.math.Vec2;

public class MouseInput {

	private boolean[] isDown = new boolean[4];

	private Vec2 position = new Vec2(0, 0);


	public synchronized boolean isDown(int button) {
		return isDown[button];
	}

	public synchronized void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}

	public synchronized void step() {
	}

	private void clear(boolean[] states) {
		for (int i = 0; i < states.length; ++i) {
			states[i] = false;
		}
	}

	public synchronized Vec2 getPosition() {
		return position;
	}

	public synchronized void mouseDown(int button) {
		isDown[button] = true;
	}

	public synchronized void mouseUp(int button) {
		isDown[button] = false;
	}


}
