package ch.neb.spacegame.world.weapon;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.bullets.Bullet;

public class NormalGun extends Gun {

	public NormalGun(long cooldown, World world) {
		super(cooldown, world);
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {
		// shot normal shots
		float theta = (float) Math.atan2(direction.y, direction.x);
		final int shots = 15;
		final float thetaOffset = 0.03f;
		float startAngle = theta - (shots / 2) * thetaOffset;
		final BufferedImage bullet = Arts.bullet2;

		for (int i = 0; i < shots; ++i) {
			final Vec2 shootPosition = new Vec2(position);
			shootPosition.x -= bullet.getWidth() / 2;
			shootPosition.y -= bullet.getHeight() / 2;
			world.addEntity(new Bullet(world, bullet, new Vec2((float) Math.cos(startAngle), (float) Math.sin(startAngle)), shootPosition, 0.9f, 4));
			startAngle += thetaOffset;
		}
	}

}