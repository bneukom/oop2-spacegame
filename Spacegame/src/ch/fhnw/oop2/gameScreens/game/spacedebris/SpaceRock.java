package ch.fhnw.oop2.gameScreens.game.spacedebris;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.Explosion;
import ch.fhnw.oop2.gameScreens.game.Player;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.Arts;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.KillListener;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class SpaceRock extends GameSpaceDebris {

	public SpaceRock(final GameScreen spaceGameScreen, Player player, Vec2 initialDirection, final Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(spaceGameScreen, player, Arts.rock, initialDirection, position, maxHealth, speed, angularSpeed);

		// only spawn explosions if killed by a game entity (not if for example out of bounds):
		addKillListener(new KillListener() {

			@Override
			public void killed(GameEntity by) {
				spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.destroyedrock, 88, 80, 130, 1), false, new Vec2(position), new Vec2(direction)));
				spaceGameScreen
						.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.explosion, 48, 48, 100, 1), true, new Vec2(position).translate(14, 14), new Vec2(1, 0)));
			}
		});
	}

}
