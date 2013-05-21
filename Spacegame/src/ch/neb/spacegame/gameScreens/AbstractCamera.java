package ch.neb.spacegame.gameScreens;

import java.awt.geom.Rectangle2D;

import ch.neb.spacegame.math.Vec2;

public abstract class AbstractCamera {
	protected Vec2 position = new Vec2(0, 0);
	public final float width;
	public final float height;
	private Rectangle2D.Float bounds;

	public AbstractCamera(float width, float height) {
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
	public abstract void setPosition(Vec2 position);

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
