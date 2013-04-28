package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.weapon.NormalGun;
import ch.neb.spacegame.world.weapon.RocketLauncher;

public class Player extends SpaceShip {

	private static final float MOVE_HOLD_EPSILON = 10f;
	public static final long COLLIDE_COOLDOWN = 1500;

	private long timeSinceLastCollision = 0;

	private float experience = 0;
	private float nextLevelExperience = 10;

	public Player(World world) {
		super(world, Arts.playerShip, DEFAULT_SPEED, 100);

		drawHealth = false;

		final CollisionListener xpListener = new CollisionListener() {

			@Override
			public void onCollide(GameEntity a, GameEntity b) {
				if (b instanceof Mob) {
					final Mob mob = (Mob) b;
					if (!mob.isAlive()) {
						System.out.println("DEATH COLLIDE");
						increaseExperience(1);
					}
				}
			}
		};

		// guns.add(new RocketLauncher(800, world, xpListener));
		guns.add(new NormalGun(200, world, xpListener));
	}

	private void increaseExperience(float amount) {
		System.out.println("AMOUNT: " + amount);
		experience += amount;
		if (experience >= nextLevelExperience) {
			experience = experience - nextLevelExperience;
			nextLevelExperience = nextLevelExperience * 2;
		}
	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		float x = position.x - camera.getX();
		float y = position.y - camera.getY();

		float width = Math.max(35, getWidth());
		graphics.setColor(Color.GREEN);
		graphics.fillRect((int) (x - 10), (int) y - 6, (int) ((health / maxHealth) * width), 3);
		graphics.setColor(Color.RED);
		graphics.fillRect((int) (x - 10), (int) y - 3, (int) ((maxHealth / maxHealth) * width), 3);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 100, 1), new Vec2(position).translate(14, 14), new Vec2(1, 0)));
		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 110, 1), new Vec2(position).translate(19, 9), new Vec2(1, 0)));
		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 115, 1), new Vec2(position).translate(5, 5), new Vec2(1, 0)));
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		final Vec2 mousePosition = updateContext.mouseInput.getPosition();

		final Vec2 destination = Vec2.subtract(mousePosition, halfWidth);
		final Vec2 playerScreenPosition = Vec2.subtract(position, updateContext.camera.getPosition());

		direction = Vec2.subtract(destination, playerScreenPosition).normalize();

		float distance = Vec2.distance(playerScreenPosition, destination);

		// move forwards
		if (updateContext.keys.forward.isDown && distance > MOVE_HOLD_EPSILON) {
			final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
			position.add(offset);
		}

		// move backwards
		if (updateContext.keys.backward.isDown && distance > MOVE_HOLD_EPSILON) {
			final Vec2 offset = Vec2.multiply(direction, -speed * updateContext.deltaT);
			position.add(offset);
		}

		if (updateContext.mouseInput.isDown(1)) {
			shoot();
		}

		timeSinceLastCollision += updateContext.deltaT;
	}

	@Override
	public void onCollide(GameEntity other, World world) {
		super.onCollide(other, world);

		if (timeSinceLastCollision > COLLIDE_COOLDOWN) {
			timeSinceLastCollision = 0;
			doDamage(10);
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
		return other instanceof Mob && other != this;
	}
}
