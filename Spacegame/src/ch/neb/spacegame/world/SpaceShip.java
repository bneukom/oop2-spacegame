package ch.neb.spacegame.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

	public void shoot() {
		final Vec2 shotPosition = new Vec2(position);
		shotPosition.x += getWidth() / 2;
		shotPosition.y += getHeight() / 2;

		final Vec2 shotDirection = new Vec2(direction);

		for (Gun gun : guns) {
			gun.shoot(shotPosition, shotDirection);
		}
	}
}
