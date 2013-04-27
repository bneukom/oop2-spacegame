package ch.neb.spacegame;

import ch.neb.spacegame.math.Vec2;

public class MouseInput {

	private boolean[] isDown = new boolean[4];
	private Vec2 position = new Vec2(0, 0);

	public boolean isDown(int button) {
		return isDown[button];
	}

	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}

	public Vec2 getPosition() {
		return position;
	}

	public void mouseDown(int button) {
		isDown[button] = true;
	}
	
	public void mouseUp(int button) {
		isDown[button] = false;
	}

}
