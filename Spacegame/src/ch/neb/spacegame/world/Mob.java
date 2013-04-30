package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.neb.spacegame.Camera;
import ch.neb.spacegame.DamageListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;

/**
 * A mob represents a game entity which can be drawn and destroyed.
 */
public class Mob extends DrawableGameEntity {

	protected float health;
	protected float maxHealth;
	protected boolean drawHealth = true;
	private List<KillListener> killListeners = new ArrayList<>();
	private List<DamageListener> damageListeners = new ArrayList<>();

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

	public void doDamage(GameEntity attackee, float damage) {
		if (health == 0)
			return;

		health -= damage;
		for (DamageListener damageListener : damageListeners) {
			damageListener.damageRecieved(attackee, damage);
		}
		
		if (health <= 0) {
			world.removeEntity(this);
			for (KillListener killListener : killListeners) {
				killListener.killed(attackee);
			}
		}
		health = Math.max(health, 0);
	}

	public void addDamageListener(DamageListener damageListener) {
		damageListeners.add(damageListener);
	}
	public void addKillListener(KillListener killListener) {
		killListeners.add(killListener);
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getHealth() {
		return health;
	}

	/**
	 * Returns the amount of experience gained when this mob gets killed. Default value is 1.
	 * 
	 * @return
	 */
	public float getExperience() {
		return 1;
	}
}
