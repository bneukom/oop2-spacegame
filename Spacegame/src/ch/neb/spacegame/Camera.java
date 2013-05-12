package ch.neb.spacegame;

import java.awt.geom.Rectangle2D;

import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.World;

public class Camera {
	private Vec2 position = new Vec2(0, 0);
	public final float width;
	public final float height;
	private Rectangle2D.Float bounds;
	private World world;

	public Camera(float width, float height, World world) {
		this.width = width;
		this.height = height;
		this.world = world;
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
		// bounds adjusting
		// float x = Math.max(position.x - width / 2, 0);
		// float y = Math.max(position.y - height / 2, 0);
		//
		// x = Math.min(x, world.width - width);
		// y = Math.min(y, world.width - height);
		//
		// this.bounds.x = x;
		// this.bounds.y = y;
		// this.position.setTo(x, y);

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
