package ch.neb.spacegame.world;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.weapon.NormalGun;
import ch.neb.spacegame.world.weapon.RocketLauncher;

public class Player extends SpaceShip {

	private static final float EPSILON = 10f;
	public static final long COLLIDE_COOLDOWN = 1500;
	private long timeSinceLastCollision = 0;

	public Player(World world) {
		super(world, Arts.playerShip, DEFAULT_SPEED, 100);

		guns.add(new RocketLauncher(800, world));
		guns.add(new NormalGun(200, world));
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
		if (updateContext.keys.forward.isDown && distance > EPSILON) {
			final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
			position.add(offset);
		}

		// move backwards
		if (updateContext.keys.backward.isDown && distance > EPSILON) {
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

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Mob && other != this;
	}
}
