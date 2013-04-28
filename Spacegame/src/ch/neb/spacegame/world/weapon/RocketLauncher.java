package ch.neb.spacegame.world.weapon;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.bullets.Rocket;

public class RocketLauncher extends Gun {

	private float halfConeAngle = (float) Math.toRadians(25);

	public RocketLauncher(long cooldown, World world, CollisionListener collisionListener) {
		super(cooldown, world, collisionListener);
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {

		float theta = (float) Math.atan2(direction.y, direction.x);
		final int shots = 15;
		final float thetaOffset = 0.03f;

		// search a target in a given cone where the rocket is
		// flying
		Mob nearestMob = null;
		float shortestDistance = Float.MAX_VALUE;
		for (GameEntity entity : world.getGameEntities()) {
			if (entity instanceof Mob && !(entity instanceof Player)) {
				final Mob mob = (Mob) entity;
				final Vec2 mobPosition = mob.getPosition();
				final Vec2 rocketToMobVector = Vec2.subtract(mobPosition, position);
				float angle = (float) Math.acos(rocketToMobVector.dot(direction) / (rocketToMobVector.getLength() * direction.getLength()));

				if (angle < halfConeAngle) {
					float distance = Vec2.distance(mobPosition, position);
					if (distance < shortestDistance && distance < 900) {
						shortestDistance = distance;
						nearestMob = mob;
					}
				}
			}
		}

		float startAngle = theta - (shots / 2) * thetaOffset;

		for (int i = 0; i < shots; ++i) {
			final BufferedImage rocket = Arts.rocket;
			final Vec2 shootPosition = new Vec2(position);

			shootPosition.x -= rocket.getWidth() / 2;
			shootPosition.y -= rocket.getHeight() / 2;

			final Vec2 startDirection;
			if (nearestMob != null) {
				float rocketThetaOffset;
				if (Math.random() < 0.5f) {
					rocketThetaOffset = (float) (theta - 1.4f + Math.random() * 0.4);
				} else {
					rocketThetaOffset = (float) (theta + 1.4f - Math.random() * 0.4);
				}
				startDirection = new Vec2((float) Math.cos(rocketThetaOffset), (float) Math.sin(rocketThetaOffset));
			} else {
				startDirection = new Vec2((float) Math.cos(startAngle), (float) Math.sin(startAngle));
			}

			world.addEntity(new Rocket(world, collisionListener, rocket, startDirection, nearestMob, shootPosition,
					(float) (GameEntity.DEFAULT_SPEED + (Math.random() * 0.1f) - 0.05f), 10));
			startAngle += thetaOffset / 50;
		}
	}

}
