package ch.neb.spacegame.world.spacedebris;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Explosion;
import ch.neb.spacegame.world.World;

public class SmallSpaceDebris extends SpaceDebris {

	public SmallSpaceDebris(final World world, Vec2 initialDirection, final Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(world, Arts.debris1, initialDirection, position, maxHealth, speed, angularSpeed);

		// only spawn explosions if killed by a game entity (not if for example out of bounds):
		addKillListener(new KillListener() {

			@Override
			public void killed(GameEntity by) {
				world.addEntity(new Explosion(world, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), false, new Vec2(position), new Vec2(1, 0)));
			}
		});
	}

}
