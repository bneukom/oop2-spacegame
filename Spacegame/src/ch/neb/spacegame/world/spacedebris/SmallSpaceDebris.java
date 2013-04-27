package ch.neb.spacegame.world.spacedebris;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Explosion;
import ch.neb.spacegame.world.World;

public class SmallSpaceDebris extends SpaceDebris {

	public SmallSpaceDebris(World world, Vec2 initialDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(world, Arts.debris1, initialDirection, position, maxHealth, speed, angularSpeed);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		world.addEntity(new Explosion(world, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), new Vec2(position), new Vec2(1, 0)));
	}

}
