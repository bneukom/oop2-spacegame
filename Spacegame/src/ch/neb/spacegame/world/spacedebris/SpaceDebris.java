package ch.neb.spacegame.world.spacedebris;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.bullets.Bullet;

public abstract class SpaceDebris extends Mob {

	private Vec2 initialDirection;

	private float angularSpeed;

	private long age;

	public static long MIN_AGE_TO_DIE = 10000;

	public SpaceDebris(World world, BufferedImage image, Vec2 initialDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(world, image, speed, maxHealth);
		this.initialDirection = initialDirection;
		this.position = position;
		this.angularSpeed = angularSpeed;

	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		age += updateContext.deltaT;

		// always fly to the same direction
		final Vec2 offset = Vec2.multiply(initialDirection, speed * updateContext.deltaT);
		position.add(offset);

		final float theta = angularSpeed * updateContext.deltaT;

		direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
		direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

		// remove if out of world bounds
		if (age > MIN_AGE_TO_DIE && position.x + getWidth() < 0 || position.y + getHeight() < 0 || position.x > world.width || position.y > world.height) {
			world.removeEntity(this);
		}
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Bullet || other instanceof Player;
	}

}
