package ch.neb.spacegame.world.bullets;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Explosion;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.World;

public class Rocket extends Bullet {

	private float maxSpeed = 1.9f;
	private Mob target;

	public Rocket(World world, BufferedImage image, Vec2 direction, Mob target, Vec2 position, float initialspeed, float damage) {
		super(world, image, direction, position, initialspeed, damage);
		this.target = target;
	}

	@Override
	public void update(UpdateContext updateContext) {
		speed += Math.max(Math.pow(age / 6000, 2), 0.0002);
		speed = Math.min(speed, maxSpeed);

		// aim for target
		if (target != null && target.getHealth() > 0) {
			final Vec2 targetPosition = target.getPosition();

			// aim for the target but only change direction in small steps
			final Vec2 rocketToMobVector = Vec2.subtract(targetPosition, position).normalize().multiply(0.009f * updateContext.deltaT);

			direction.add(rocketToMobVector).normalize();
		}

		super.update(updateContext);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		world.addEntity(new Explosion(world, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), new Vec2(position), new Vec2(1, 0)));
	}

}
