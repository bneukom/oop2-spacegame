package ch.fhnw.oop2.gameScreens.game;

import java.awt.geom.Rectangle2D;

import ch.fhnw.oop2.gameScreens.AbstractCamera;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class GameCamera extends AbstractCamera {
	private Vec2 position = new Vec2(0, 0);
	public final float width;
	public final float height;
	private Rectangle2D.Float bounds;

	public GameCamera(float width, float height) {
		super(width, height);
		this.width = width;
		this.height = height;
		this.bounds = new Rectangle2D.Float(position.x, position.y, width, height);
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public float leftDistance(Vec2 p) {
		return p.x - position.x;
	}

	public float rightDistance(Vec2 p) {
		return (position.x + width) - p.x;
	}

	public float topDistance(Vec2 p) {
		return p.y - position.y;
	}

	/**
	 * Sets the center point of the camera to the specific location.
	 * 
	 * @param position
	 */
	public void setPosition(Vec2 position) {
		// center
		float x = position.x - width / 2;
		float y = position.y - height / 2;

		this.bounds.x = x;
		this.bounds.y = y;
		this.position.setTo(x, y);
	}

	public boolean isInView(float x, float y, float width, float height) {
		return bounds.intersects(x, y, width, height);
	}

	public boolean isInView(Rectangle2D.Float rectangle) {
		return bounds.intersects(rectangle);
	}

	public Vec2 getPosition() {
		return position;
	}
}
