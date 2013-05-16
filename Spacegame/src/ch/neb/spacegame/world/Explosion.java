package ch.neb.spacegame.world;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Audio;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Random;
import ch.neb.spacegame.math.Vec2;

public class Explosion extends AnimatedGameEntity {

	public Explosion(World world, Animation animation, boolean playSound, Vec2 position, Vec2 direction) {
		super(world, animation, position, direction);

		if (playSound)
			Audio.playSound(Random.selectRandom("audio/explosion1.wav", "audio/explosion2.wav", "audio/explosion3.wav"));
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		if (animation.getLoops() >= 1) {
			world.removeEntity(this);
		}
	}

}