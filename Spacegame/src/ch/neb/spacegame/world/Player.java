package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Graphics2D;
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
import ch.neb.spacegame.world.weapon.NormalGun;
import ch.neb.spacegame.world.weapon.RocketLauncher;

public class Player extends SpaceShip {

	private static final float MOVE_HOLD_EPSILON = 10f;
	public static final long COLLIDE_COOLDOWN = 1500;

	private long timeSinceLastCollision = 0;

	private final AffineTransform transform = new AffineTransform();
	private BufferedImage exhaustImage = Arts.exhaust;

	private float points = 0;
	private float experience = 0;
	private float nextLevelExperience = 10;
	private float power = 100;
	private float maxPower = 100;
	private boolean isPowerEnabled = false;

	public Player(World world) {
		super(world, Arts.playerShip, DEFAULT_SPEED, 500);

		drawHealth = false;

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

		guns.add(new RocketLauncher(800, world, this, null));
		guns.add(new NormalGun(200, world, this, null, 4, 5));
	}

	private void increaseExperience(float amount) {
		points += amount;
		experience += amount;
		if (experience >= nextLevelExperience) {
			onLevelUp();
			experience = experience - nextLevelExperience;
			nextLevelExperience = (float) (Math.pow(nextLevelExperience, 1.020) * nextLevelExperience / 4.5);
		}
	}

	private void onLevelUp() {
		for (Gun gun : guns) {
			gun.upgrade();

			maxPower += 10;
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

		float width = Math.max(35, getWidth());
		graphics.setColor(Color.GREEN);
		graphics.fillRect((int) (x - 6), (int) y - 6, (int) ((health / maxHealth) * width), 3);
		graphics.setColor(Color.RED);
		graphics.fillRect((int) (x - 6), (int) y - 3, (int) ((power / maxPower) * width), 3);

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
		if (updateContext.keys.backward.isDown && distance > MOVE_HOLD_EPSILON) {
			isMoving = true;
			final Vec2 offset = Vec2.multiply(direction, -speed * updateContext.deltaT);
			position.add(offset);
		}

		isPowerEnabled = false;
		if (updateContext.keys.powerBoost.isDown && power > 0 && isMoving) {
			power -= (0.075 * updateContext.deltaT);
			power = Math.max(0, power);

			if (power != 0) {
				speed += 0.00022 * updateContext.deltaT;
				isPowerEnabled = true;
			} else {
				speed -= 0.003;
				speed = Math.max(speed, DEFAULT_SPEED);
			}
		} else {
			speed -= 0.003;
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
			doDamage(this, 40);
			world.addEntity(new Explosion(world, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), new Vec2(position), new Vec2(1, 0)));
		}

	}

	public float getExperience() {
		return experience;
	}

	public float getNextLevelExperience() {
		return nextLevelExperience;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return (other instanceof Mob && other != this) || other instanceof Bullet;
	}
}
