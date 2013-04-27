package ch.neb.spacegame.world;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.bullets.Bullet;
import ch.neb.spacegame.world.bullets.Rocket;

public class SpaceShip extends Mob {
	protected float currentCooldown;

	private float halfConeAngle = (float) Math.toRadians(30);

	public static float SHOOT_COOLDOWN = 175;

	public SpaceShip(BufferedImage image, World world) {
		super(world, image);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		currentCooldown -= updateContext.deltaT;
	}

	// TODO somehow use all available Guns and shoot them!!!!!!!!!!!!!!!!!!!!!
	public void shoot() {
		if (currentCooldown <= 0) {
			// shot normal shots
			float theta = (float) Math.atan2(direction.y, direction.x);
			final int shots = 15;
			final float thetaOffset = 0.03f;

			{
				float startAngle = theta - (shots / 2) * thetaOffset;
				final BufferedImage bullet = Arts.bullet2;

				for (int i = 0; i < shots; ++i) {
					final Vec2 shootPosition = new Vec2(position);
					shootPosition.x += getWidth() / 2;
					shootPosition.y += getHeight() / 2;
					shootPosition.x -= bullet.getWidth() / 2;
					shootPosition.y -= bullet.getHeight() / 2;
					world.addEntity(new Bullet(world, bullet, new Vec2((float) Math.cos(startAngle), (float) Math.sin(startAngle)), shootPosition, 0.9f, 4));
					startAngle += thetaOffset;
				}
			}

			// shoot rockets
			{
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

					shootPosition.x += getWidth() / 2;
					shootPosition.y += getHeight() / 2;
					shootPosition.x -= rocket.getWidth() / 2;
					shootPosition.y -= rocket.getHeight() / 2;

					final Vec2 startDirection;
					if (nearestMob != null) {
						float rocketThetaOffset = (float) (theta - 1.4 + (Math.random() * 2.8));
						startDirection = new Vec2((float) Math.cos(rocketThetaOffset), (float) Math.sin(rocketThetaOffset));
					} else {
						startDirection = new Vec2((float) Math.cos(startAngle), (float) Math.sin(startAngle));
					}

					world.addEntity(new Rocket(world, rocket, startDirection, nearestMob, shootPosition, speed + 0.15f, 10));
					startAngle += thetaOffset / 10;
				}

			}

			currentCooldown = SHOOT_COOLDOWN;
		}

	}
}
