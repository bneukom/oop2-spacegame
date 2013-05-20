package ch.neb.spacegame.world.guns;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.bullets.Rocket;

public class RocketLauncher extends Gun {

	private float halfConeAngle = (float) Math.toRadians(25);
	private int shots;
	private float damage;
	private Mob target;
	private boolean ignoreOtherTargets;

	public RocketLauncher(long cooldown, World world, GameEntity owner, Mob target, boolean ignoreOtherTargets, int initialShots, float damage) {
		super(cooldown, world, owner, null);
		this.target = target;
		this.ignoreOtherTargets = ignoreOtherTargets;
		this.shots = initialShots;
		this.damage = damage;
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {
		float theta = (float) Math.atan2(direction.y, direction.x);
		final float thetaOffset = 0.03f;

		Mob targetToHit = null;

		if (target == null) {

			// if no target has been set, search a target in a given cone where the rocket is flying
			float shortestDistance = Float.MAX_VALUE;
			for (GameEntity entity : world.getGameEntities()) {

				if (entity instanceof Mob && entity != owner) {
					final Mob mob = (Mob) entity;
					final Vec2 mobPosition = mob.getPosition();
					final Vec2 rocketToMobVector = Vec2.subtract(mobPosition, position);
					float angle = (float) Math.acos(rocketToMobVector.dot(direction) / (rocketToMobVector.getLength() * direction.getLength()));

					if (angle < halfConeAngle) {
						float distance = Vec2.distance(mobPosition, position);
						if (distance < shortestDistance && distance < 900) {
							shortestDistance = distance;
							targetToHit = mob;
						}
					}
				}
			}
		} else {
			// try to hit the predefined target
			targetToHit = target;
		}

		float startAngle = theta - (shots / 2) * thetaOffset;

		for (int i = 0; i < shots; ++i) {
			final BufferedImage rocket = Arts.rocket2;
			final Vec2 shootPosition = new Vec2(position);

			shootPosition.x -= rocket.getWidth() / 2;
			shootPosition.y -= rocket.getHeight() / 2;

			final Vec2 startDirection;
			if (targetToHit != null) {
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

			world.addEntity(new Rocket(world, owner, ignoreOtherTargets, collisionListener, rocket, startDirection, targetToHit, shootPosition, (float) (GameEntity.DEFAULT_SPEED
					+ (Math.random() * 0.1f) - 0.05f), damage));
			startAngle += thetaOffset / 50;
		}
	}

	@Override
	public void upgrade(int level) {
		if (level % 2 == 0)
			shots += 1;
		else 
			damage += 4;
	}

}
