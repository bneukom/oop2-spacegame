package ch.neb.spacegame.world;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.bullets.Bullet;
import ch.neb.spacegame.world.spacedebris.SpaceDebris;
import ch.neb.spacegame.world.weapon.Gun;
import ch.neb.spacegame.world.weapon.LightningGun;
import ch.neb.spacegame.world.weapon.NormalGun;
import ch.neb.spacegame.world.weapon.RocketLauncher;

public class Player extends SpaceShip {

	private static final float MAX_SPEED = 0.55f;
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1);
	private static final BasicStroke BORDER_STROKE = new BasicStroke(2);
	private static final float MOVE_HOLD_EPSILON = 10f;
	public static final long COLLIDE_COOLDOWN = 1500;

	// initial value set, so player will be immune when the game starts
	private long timeSinceLastCollision = 3000;

	private final AffineTransform transform = new AffineTransform();
	private BufferedImage exhaustImage = Arts.exhaust;

	private float points = 0;
	private float totalXP = 0;
	private float nextLevelExperience = 10;
	private float power = 100;
	private float maxPower = 100;
	private boolean isPowerEnabled = false;
	private boolean isShieldEnabled = false;

	public Player(World world) {
		super(world, Arts.ship1, DEFAULT_SPEED, 500);

		drawHealth = false;

		// used for experience gain
		world.addSpawnListener(new SpawnListener() {

			@Override
			public void spawned(GameEntity entity) {
				if (entity instanceof Mob) {
					final Mob mob = (Mob) entity;
					mob.addKillListener(new KillListener() {

						@Override
						public void killed(GameEntity by) {
							if (by == Player.this) {
								increaseExperience(mob.getExperience());
							}
						}
					});
				}
			}
		});

		guns.add(new RocketLauncher(800, world, this, null, false, 4, 10));
		guns.add(new NormalGun(200, world, this, 4, 5));
		guns.add(new LightningGun(500, world, this, null, 100));
	}

	private void increaseExperience(float amount) {
		points += amount;
		totalXP += amount;
		if (totalXP >= nextLevelExperience) {
			onLevelUp();
			totalXP = totalXP - nextLevelExperience;
			nextLevelExperience = (float) (Math.pow(nextLevelExperience, 1.020) * nextLevelExperience / 4.5);
		}
	}

	private void onLevelUp() {
		for (Gun gun : guns) {
			gun.upgrade();

			maxPower += 25;
		}
	}

	public float getPoints() {
		return points;
	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		float x = position.x - camera.getX();
		float y = position.y - camera.getY();

		if (isShieldEnabled) {
			graphics.setColor(new Color(34, 40, 125, 135));
			graphics.fillOval((int) x - 7, (int) y - 7, (int) getWidth() + 13, (int) getHeight() + 13);
			graphics.setStroke(BORDER_STROKE);
			graphics.setColor(new Color(34, 40, 125, 235));
			graphics.drawOval((int) x - 7, (int) y - 7, (int) getWidth() + 13, (int) getHeight() + 13);
			graphics.setStroke(DEFAULT_STROKE);
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

	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		final Vec2 mousePosition = updateContext.mouseInput.getPosition();

		final Vec2 destination = Vec2.subtract(mousePosition, halfWidth);
		final Vec2 playerScreenPosition = Vec2.subtract(position, updateContext.camera.getPosition());

		direction = Vec2.subtract(destination, playerScreenPosition).normalize();

		float distance = Vec2.distance(playerScreenPosition, destination);

		boolean isMoving = false;

		// move forwards
		if (updateContext.keys.forward.isDown && distance > MOVE_HOLD_EPSILON) {
			isMoving = true;
			final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
			position.add(offset);
		}

		// move backwards
		if (updateContext.keys.backward.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 offset = Vec2.multiply(direction, -speed * updateContext.deltaT);
			position.add(offset);
		}

		// strafe left
		if (updateContext.keys.left.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 strafeLeft = new Vec2(-direction.y, direction.x);
			final Vec2 offset = Vec2.multiply(strafeLeft, speed * updateContext.deltaT);
			position.add(offset);
		}

		// strafe right
		if (updateContext.keys.right.isDown && distance > MOVE_HOLD_EPSILON && !isMoving) {
			isMoving = true;
			final Vec2 strafeLeft = new Vec2(direction.y, -direction.x);
			final Vec2 offset = Vec2.multiply(strafeLeft, speed * updateContext.deltaT);
			position.add(offset);
		}

		isShieldEnabled = false;
		if (updateContext.keys.shield.isDown && power > 0) {
			power -= (0.085 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power > 0) {
				isShieldEnabled = true;
			}
		}

		isPowerEnabled = false;
		if (updateContext.keys.powerBoost.isDown && power > 0 && isMoving && !isShieldEnabled) {
			power -= (0.075 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power != 0) {
				speed += 0.00018 * updateContext.deltaT;
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

		if (updateContext.mouseInput.isDown(1)) {
			shoot();
		}

		// regenerate power
		power += (0.02 * updateContext.deltaT);
		power = Math.min(maxPower, power);

		// regenerate health
		health += maxHealth / 200000 * updateContext.deltaT;
		health = Math.min(maxHealth, health);

		timeSinceLastCollision += updateContext.deltaT;
	}

	@Override
	public void onCollide(GameEntity other, World world) {
		super.onCollide(other, world);

		if (other instanceof SpaceDebris && timeSinceLastCollision > COLLIDE_COOLDOWN) {
			timeSinceLastCollision = 0;

			// collide with space debris, do lots of damage!
			doDamage(this, 80);
			
			if (!isShieldEnabled)
				world.addEntity(new Explosion(world, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), new Vec2(position), new Vec2(1, 0)));
		}

	}

	@Override
	public void doDamage(GameEntity attackee, float damage) {
		if (!isShieldEnabled)
			super.doDamage(attackee, damage);
	}

	public float getTotalExperience() {
		return totalXP;
	}

	public float getNextLevelExperience() {
		return nextLevelExperience;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return (other instanceof Mob && other != this) || other instanceof Bullet;
	}
}
