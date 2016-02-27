package ch.neb.spacegame.gameScreens.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.oop2.spacegame.DamageListener;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.KillListener;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;

/**
 * A mob represents a game entity which can be drawn and destroyed.
 */
public class Mob extends DrawableGameEntity {

	protected float health;
	protected float maxHealth;
	protected boolean drawHealth = true;
	private List<KillListener> killListeners = new ArrayList<>();
	private List<DamageListener> damageListeners = new ArrayList<>();

	public Mob(GameScreen spaceGameScreen, BufferedImage image) {
		this(spaceGameScreen, image, DEFAULT_SPEED);
	}

	public Mob(GameScreen spaceGameScreen, BufferedImage image, float speed) {
		this(spaceGameScreen, image, speed, -1);
	}

	public Mob(GameScreen spaceGameScreen, BufferedImage image, float speed, float maxHealth) {
		super(spaceGameScreen, image, speed);

		this.maxHealth = maxHealth;
		this.health = maxHealth;
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		float x = position.x - updateContext.gameCamera.getX();
		float y = position.y - updateContext.gameCamera.getY();

		if (drawHealth) {
			graphics.setColor(Color.GREEN);
			float width = Math.max(20, getWidth());
			graphics.fillRect((int) (x - (width - getWidth())), (int) y, (int) ((health / maxHealth) * width), 5);
		}
	}

	public boolean isAlive() {
		return health > 0;
	}
	
	public void heal(float amount) {
		health += amount;
		health = Math.min(maxHealth, health);
	}

	public void doDamage(GameEntity attackee, float damage, DamageType type) {
		if (health == 0)
			return;

		health -= damage;
		for (DamageListener damageListener : damageListeners) {
			damageListener.damageRecieved(attackee, damage);
		}
		
		if (health <= 0) {
			spaceGameScreen.removeEntity(this);
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
	
	public enum DamageType {
		COLLISION, GUN
	}
}
