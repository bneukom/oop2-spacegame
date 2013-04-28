package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;

/**
 * A mob represents a game entity which can be drawn and destroyed.
 */
public class Mob extends DrawableGameEntity {

	protected float health;
	protected float maxHealth;
	protected boolean drawHealth = true;

	public Mob(World world, BufferedImage image) {
		this(world, image, DEFAULT_SPEED);
	}

	public Mob(World world, BufferedImage image, float speed) {
		this(world, image, speed, -1);
	}

	public Mob(World world, BufferedImage image, float speed, float maxHealth) {
		super(world, image, speed);

		this.maxHealth = maxHealth;
		this.health = maxHealth;
	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		float x = position.x - camera.getX();
		float y = position.y - camera.getY();

		if (drawHealth) {
			graphics.setColor(Color.GREEN);
			float width = Math.max(20, getWidth());
			graphics.fillRect((int) (x - (width - getWidth())), (int) y, (int) ((health / maxHealth) * width), 5);
		}
	}

	public boolean isAlive() {
		return health > 0;
	}

	public void doDamage(float damage) {
		health -= damage;
		if (health <= 0) {
			world.removeEntity(this);
		}
		health = Math.max(health, 0);
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getHealth() {
		return health;
	}
}
