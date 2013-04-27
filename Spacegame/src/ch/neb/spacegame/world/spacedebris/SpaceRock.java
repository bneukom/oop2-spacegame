package ch.neb.spacegame.world.spacedebris;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Explosion;
import ch.neb.spacegame.world.World;

public class SpaceRock extends SpaceDebris {

	public SpaceRock(World world, Vec2 initialDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(world, Arts.rock, initialDirection, position, maxHealth, speed, angularSpeed);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		world.addEntity(new Explosion(world, new Animation(Arts.destroyedrock, 88, 80, 130, 1), new Vec2(position), new Vec2(direction)));
		world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 100, 1), new Vec2(position).translate(14, 14), new Vec2(1, 0)));
	}

}
