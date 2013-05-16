package ch.neb.spacegame.world.spacedebris;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Explosion;
import ch.neb.spacegame.world.World;

public class SpaceRock extends SpaceDebris {

	public SpaceRock(final World world, Vec2 initialDirection, final Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(world, Arts.rock, initialDirection, position, maxHealth, speed, angularSpeed);

		// only spawn explosions if killed by a game entity (not if for example out of bounds):
		addKillListener(new KillListener() {

			@Override
			public void killed(GameEntity by) {
				world.addEntity(new Explosion(world, new Animation(Arts.destroyedrock, 88, 80, 130, 1), false, new Vec2(position), new Vec2(direction)));
				world.addEntity(new Explosion(world, new Animation(Arts.explosion, 48, 48, 100, 1), true, new Vec2(position).translate(14, 14), new Vec2(1, 0)));
			}
		});
	}

}
