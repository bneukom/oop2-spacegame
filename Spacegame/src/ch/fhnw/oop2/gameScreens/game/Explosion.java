package ch.fhnw.oop2.gameScreens.game;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.Audio;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.math.Random;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class Explosion extends AnimatedGameEntity {

	public Explosion(GameScreen spaceGameScreen, Animation animation, boolean playSound, Vec2 position, Vec2 direction) {
		super(spaceGameScreen, animation, position, direction);

		if (playSound)
			Audio.playSound(Random.selectRandom("audio/explosion1.wav", "audio/explosion2.wav", "audio/explosion3.wav"));
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		if (animation.getLoops() >= 1) {
			spaceGameScreen.removeEntity(this);
		}
	}

}