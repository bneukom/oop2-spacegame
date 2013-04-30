package ch.neb.spacegame.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.weapon.Gun;

public class SpaceShip extends Mob {

	protected List<Gun> guns = new ArrayList<Gun>();

	public SpaceShip(BufferedImage image, World world) {
		super(world, image);
	}

	public SpaceShip(World world, BufferedImage image, float speed, float maxHealth) {
		super(world, image, speed, maxHealth);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);
		for (Gun gun : guns) {
			gun.update(updateContext.deltaT);
		}
	}

	public void shoot(Vec2 direction) {
		final Vec2 shotPosition = new Vec2(position);
		shotPosition.x += getWidth() / 2;
		shotPosition.y += getHeight() / 2;

		for (Gun gun : guns) {
			gun.shoot(shotPosition, direction);
		}
	}

	public void shoot() {
		shoot(new Vec2(direction));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 100, 1), new Vec2(position).translate(-14, -14), new Vec2(1, 0)));
		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 110, 1), new Vec2(position).translate(-19, -9), new Vec2(1, 0)));
		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 115, 1), new Vec2(position).translate(-5, -5), new Vec2(1, 0)));
	}

}
