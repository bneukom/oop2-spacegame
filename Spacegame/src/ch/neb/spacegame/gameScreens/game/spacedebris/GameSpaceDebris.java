package ch.neb.spacegame.gameScreens.game.spacedebris;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.math.Vec2;

public class GameSpaceDebris extends SpaceDebris {

	private Player player;

	public GameSpaceDebris(GameScreen spaceGameScreen, Player player, BufferedImage image, Vec2 movementDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(spaceGameScreen, image, movementDirection, position, maxHealth, speed, angularSpeed);
		this.player = player;
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		// remove if too far away from player
		if (age > MIN_AGE_TO_DIE) {
			float distance = Vec2.distance(player.getPosition(), getPosition());
			if (distance > MIN_DISTANCE) {
				spaceGameScreen.removeEntity(this);
			}
		}
	}

}
