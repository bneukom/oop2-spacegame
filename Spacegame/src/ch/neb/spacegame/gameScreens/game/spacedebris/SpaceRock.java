package ch.neb.spacegame.gameScreens.game.spacedebris;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Arts;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.KillListener;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.Explosion;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.math.Vec2;

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
