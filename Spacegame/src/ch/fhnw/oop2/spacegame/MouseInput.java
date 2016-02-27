package ch.fhnw.oop2.spacegame;

import ch.neb.spacegame.math.Vec2;

public class MouseInput {

	private boolean[] isDown = new boolean[4];
	private boolean[] clicked = new boolean[4];

	private Vec2 position = new Vec2(0, 0);
	private float yOffset;

	public synchronized boolean isDown(int button) {
		return isDown[button];
	}
	
	public synchronized boolean wasClicked(int button) {
		return clicked[button];
	}

	public synchronized void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y - yOffset;
	}

	public synchronized void update() {
		for (int clickedIndex = 0; clickedIndex < clicked.length; ++clickedIndex) {
			clicked[clickedIndex] = false;
		}
	}
	
	public void setYOffset(float yOffset) {
		this.yOffset = yOffset;
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

	public synchronized void mouseClicked(int button) {
		clicked[button] = true;
	}

}
