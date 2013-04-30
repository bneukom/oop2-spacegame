package ch.neb.spacegame.world.enemies;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.SpaceShip;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.bullets.Bullet;
import ch.neb.spacegame.world.weapon.NormalGun;

public class EnemyShip extends SpaceShip {

	private static final int HEARING_DISTANCE = 800;
	private static final int SHOOT_DISTANCE = 400;
	private static final long CHANGE_DIRECTION_TIME = 3000;
	private long timeSinceLastDirectionChange = 0;
	private float maxSpeed;

	public EnemyShip(World world, BufferedImage image, float speed, float maxHealth) {
		super(world, image, speed, maxHealth);
		maxSpeed = speed;

		guns.add(new NormalGun(600, world, this, null, 1, 5));
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		final Player player = world.getPlayer();

		if (!player.isAlive())
			return;

		// fly to player
		float playerDistance = Vec2.distance(player.getPosition(), position);
		if (playerDistance < HEARING_DISTANCE) {
			final Vec2 newDirection = Vec2.subtract(player.getPosition(), position);
			newDirection.normalize();
			System.out.println(newDirection);
			direction.setTo(newDirection);

			// shoot if near enough
			if (playerDistance < SHOOT_DISTANCE)
				shoot();
		}

		// TODO smooth stop!
		if (playerDistance < 200) {
			speed = 0;
		} else {
			speed = maxSpeed;
		}

		timeSinceLastDirectionChange += updateContext.deltaT;

		if (timeSinceLastDirectionChange > CHANGE_DIRECTION_TIME) {
			// TODO smooth rotation!
			final float theta = (float) (Math.random() * 2 * Math.PI);
			direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
			direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

			timeSinceLastDirectionChange = 0;
		}

		final float x = position.x;
		final float y = position.x;

		// TODO smooth turning!
		if (x == 0 || y == 0 || x + getWidth() == world.width || y + getHeight() == world.height) {
			direction.multiply(-1);
		}

		// TODO into mob code?!
		// move
		final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
		position.add(offset);

	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Bullet;
	}

	@Override
	public float getExperience() {
		return 15;
	}

}
