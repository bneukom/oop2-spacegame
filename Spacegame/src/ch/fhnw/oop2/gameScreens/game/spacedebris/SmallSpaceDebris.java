package ch.fhnw.oop2.gameScreens.game.spacedebris;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.Explosion;
import ch.fhnw.oop2.gameScreens.game.Player;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.Arts;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.KillListener;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class SmallSpaceDebris extends GameSpaceDebris {

	public SmallSpaceDebris(final GameScreen spaceGameScreen, Player player, Vec2 initialDirection, final Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(spaceGameScreen, player, Arts.debris1, initialDirection, position, maxHealth, speed, angularSpeed);

		// only spawn explosions if killed by a game entity (not if for example out of bounds):
		addKillListener(new KillListener() {

			@Override
			public void killed(GameEntity by) {
				spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), false, new Vec2(position), new Vec2(1, 0)));
			}
		});
	}

}
