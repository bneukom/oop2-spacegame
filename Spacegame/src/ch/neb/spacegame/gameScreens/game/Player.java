package ch.neb.spacegame.gameScreens.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Audio;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.bullets.Bullet;
import ch.neb.spacegame.gameScreens.game.guns.Gun;
import ch.neb.spacegame.gameScreens.game.guns.LaserGun;
import ch.neb.spacegame.gameScreens.game.guns.LightningGun;
import ch.neb.spacegame.gameScreens.game.guns.NormalGun;
import ch.neb.spacegame.gameScreens.game.guns.RocketLauncher;
import ch.neb.spacegame.gameScreens.game.spacedebris.SpaceDebris;
import ch.neb.spacegame.math.Vec2;

public class Player extends SpaceShip {

	private static final float MAX_SPEED = 0.75f;
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1);
	private static final BasicStroke BORDER_STROKE = new BasicStroke(2);
	private static final float MOVE_HOLD_EPSILON = 10f;
	public static final long COLLIDE_COOLDOWN = 1500;

	// initial value set, so player will be immune when the game starts
	private long timeSinceLastCollision = 3000;

	private final AffineTransform transform = new AffineTransform();
	private BufferedImage exhaustImage = Arts.exhaust;

	private int level = 0;
	private float points = 0;
	private float totalXP = 0;
	private float nextLevelExperience = 10;
	private float power = 100;
	private float maxPower = 100;
	private boolean isPowerEnabled = false;
	private boolean isShieldEnabled = false;

	// right click weapon
	private LaserGun laser;

	public Player(SpaceGameScreen spaceGameScreen) {
		super(spaceGameScreen, Arts.ship1, DEFAULT_SPEED, 600);

		laser = new LaserGun(0, spaceGameScreen, this, null);

		drawHealth = false;

		// used for experience gain
		spaceGameScreen.addSpawnListener(new SpawnListener() {

			@Override
			public void spawned(GameEntity entity) {
				if (entity instanceof Mob) {
					final Mob mob = (Mob) entity;
					mob.addKillListener(new KillListener() {

						@Override
						public void killed(GameEntity by) {
							if (by == Player.this) {
								// increase expierience gain every level a bit
								increaseExperience(mob.getExperience() * (1f + level / 6f));
							}
						}
					});
				}
			}
		});

		// Audio.changeVolumne("audio/laser.wav", -10.0f);

		guns.add(new NormalGun(200, spaceGameScreen, Arts.bullet2, this, 4, 10));
		guns.add(new LightningGun(500, spaceGameScreen, this, null, 10));
		guns.add(new RocketLauncher(800, spaceGameScreen, this, null, false, 2, 10));
	}

	private void increaseExperience(float amount) {
		points += amount;
		totalXP += amount;
		if (totalXP >= nextLevelExperience) {
			onLevelUp();
			totalXP = totalXP - nextLevelExperience;

			// exponential gain
			nextLevelExperience = (float) (Math.pow(nextLevelExperience, 1.025) + nextLevelExperience / 2);

			// linear gain
			// nextLevelExperience *= 1.8;
		}
	}

	private void onLevelUp() {
		level++;
		for (Gun gun : guns) {
			gun.upgrade(level);

			maxPower += 20;
			maxHealth += 10;
			health += maxHealth / 4; // add 1/4 of max hp 
			health = Math.min(health, maxHealth);
		}
	}

	public float getPoints() {
		return points;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		float x = position.x - updateContext.gameCamera.getX();
		float y = position.y - updateContext.gameCamera.getY();

		if (isShieldEnabled) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setColor(new Color(34, 40, 125, 135));
			graphics.fillOval((int) x - 7, (int) y - 7, (int) getWidth() + 13, (int) getHeight() + 13);
			graphics.setStroke(BORDER_STROKE);
			graphics.setColor(new Color(34, 40, 125, 235));
			graphics.drawOval((int) x - 7, (int) y - 7, (int) getWidth() + 13, (int) getHeight() + 13);
			graphics.setStroke(DEFAULT_STROKE);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		float width = Math.max(35, getWidth());
		graphics.setColor(Color.GREEN);
		graphics.fillRect((int) (x - 6), (int) y - 9, (int) ((health / maxHealth) * width), 3);
		graphics.setColor(Color.RED);
		graphics.fillRect((int) (x - 6), (int) y - 6, (int) ((power / maxPower) * width), 3);

		if (isPowerEnabled) {
			// same rotation as the ship, but other offset
			transform.setToIdentity();
			transform.rotate(Math.atan2(direction.y, direction.x) + Math.PI / 2, x + image.getWidth() / 2, y + image.getHeight() / 2);
			transform.translate(x + image.getWidth() / 2 - exhaustImage.getWidth() / 2, y + image.getHeight() - 3);

			graphics.drawImage(exhaustImage, transform, null);
		}

		// System.out.println(Math.toDegrees(Math.atan2(direction.y, direction.x)));

	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		final Vec2 mousePosition = updateContext.mouseInput.getPosition();

		final Vec2 destination = Vec2.subtract(mousePosition, halfWidth);
		final Vec2 playerScreenPosition = Vec2.subtract(position, updateContext.gameCamera.getPosition());

		direction = Vec2.subtract(destination, playerScreenPosition).normalize();

		float distance = Vec2.distance(playerScreenPosition, destination);

		boolean isMoving = false;

		// move forwards
		if (updateContext.keyboard.forward.isDown && distance > MOVE_HOLD_EPSILON) {
			isMoving = true;
			final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
			position.add(offset);
		}

		// move backwards
		if (updateContext.keyboard.backward.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 offset = Vec2.multiply(direction, -speed * updateContext.deltaT);
			position.add(offset);
		}

		// strafe left
		if (updateContext.keyboard.left.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 strafeLeft = new Vec2(-direction.y, direction.x);
			final Vec2 offset = Vec2.multiply(strafeLeft, speed * updateContext.deltaT);
			position.add(offset);
		}

		// strafe right
		if (updateContext.keyboard.right.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 strafeRight = new Vec2(direction.y, -direction.x);
			final Vec2 offset = Vec2.multiply(strafeRight, speed * updateContext.deltaT);
			position.add(offset);
		}

		isShieldEnabled = false;
		if (updateContext.keyboard.shield.isDown && power > 0) {
			power -= (0.085 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power > 0) {
				isShieldEnabled = true;
			}
		}

		isPowerEnabled = false;
		if (updateContext.keyboard.powerBoost.isDown && power > 0 && isMoving && !isShieldEnabled) {
			power -= (0.045 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power != 0) {
				speed += 0.00033 * updateContext.deltaT;
				speed = Math.min(speed, MAX_SPEED);
				isPowerEnabled = true;
			} else {
				speed -= 0.008;
				speed = Math.max(speed, DEFAULT_SPEED);
			}
		} else {
			speed -= 0.008;
			speed = Math.max(speed, DEFAULT_SPEED);
		}

		// TODO generalize played sound
		if (updateContext.mouseInput.isDown(1)) {
			shoot();

			Audio.loopSound("audio/laser.wav");
		} else {
			Audio.reset("audio/laser.wav");

		}

		laser.update(updateContext.deltaT);
		if (updateContext.mouseInput.isDown(3) && power > 0) {
			power -= (0.075 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power > 0) {
				final Vec2 shotPosition = new Vec2(position);
				shotPosition.x += getWidth() / 2;
				shotPosition.y += getHeight() / 2;
				laser.shoot(shotPosition, new Vec2(direction));
			}
		}

		// regenerate power
		power += (0.03 * updateContext.deltaT);
		power = Math.min(maxPower, power);

		// regenerate health
		health += maxHealth / 200000 * updateContext.deltaT;
		health = Math.min(maxHealth, health);

		timeSinceLastCollision += updateContext.deltaT;

	}

	@Override
	public void onCollide(GameEntity other, GameScreen spaceGameScreen) {
		super.onCollide(other, spaceGameScreen);

		if (other instanceof SpaceDebris && timeSinceLastCollision > COLLIDE_COOLDOWN) {
			timeSinceLastCollision = 0;

			// collide with space debris, does lots of damage!
			doDamage(this, 80, DamageType.COLLISION);

			if (!isShieldEnabled)
				spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), false, new Vec2(position), new Vec2(1, 0)));
		}

	}

	@Override
	public void doDamage(GameEntity attackee, float damage, DamageType type) {
		if (!isShieldEnabled)
			super.doDamage(attackee, damage, type);
		else
			super.doDamage(attackee, damage / 2, type); // only do half damage
	}

	public float getTotalExperience() {
		return totalXP;
	}

	public float getNextLevelExperience() {
		return nextLevelExperience;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		if (other instanceof Bullet) {
			return ((Bullet) other).getOwner() != this;
		}
		return (other instanceof Mob && other != this);
	}
}
