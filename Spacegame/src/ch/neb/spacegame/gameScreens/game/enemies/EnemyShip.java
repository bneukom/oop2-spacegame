package ch.neb.spacegame.gameScreens.game.enemies;

import java.awt.image.BufferedImage;

import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;
import ch.neb.spacegame.gameScreens.game.SpaceShip;
import ch.neb.spacegame.gameScreens.game.bullets.Bullet;
import ch.neb.spacegame.math.Vec2;

// TODO collision resolve?
public class EnemyShip extends SpaceShip {

	private static final int HEARING_DISTANCE = 5000;
	private static final int SHOOT_DISTANCE = 400;
	private static final long CHANGE_DIRECTION_TIME = 3000;
	private long timeSinceLastDirectionChange = 0;
	private float maxSpeed;
	private Player player;

	public EnemyShip(GameScreen spaceGameScreen, Player player, BufferedImage image, float speed, float maxHealth) {
		super(spaceGameScreen, image, speed, maxHealth);
		this.player = player;
		maxSpeed = speed;
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		if (!player.isAlive())
			return;

		// fly to player
		float playerDistance = Vec2.distance(player.getPosition(), position);
		if (playerDistance < HEARING_DISTANCE) {
			final Vec2 newDirection = Vec2.subtract(player.getPosition(), position);
			newDirection.normalize();
			direction.setTo(newDirection);

			// shoot if near enough
			if (playerDistance < SHOOT_DISTANCE)
				shoot();
		}

		// TODO smooth stop!
		if (playerDistance < 200) {
			speed = 0;
		} else {
			speed = maxSpeed;
		}

		timeSinceLastDirectionChange += updateContext.deltaT;

		if (timeSinceLastDirectionChange > CHANGE_DIRECTION_TIME) {
			// TODO smooth rotation!
			final float theta = (float) (Math.random() * 2 * Math.PI);
			direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
			direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

			timeSinceLastDirectionChange = 0;
		}

		// TODO into mob code?!
		// move
		final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
		position.add(offset);

	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		// disabled friendly fire
		if (other instanceof Bullet) {
			Bullet bullet = (Bullet) other;
			return !(bullet.getOwner() instanceof EnemyShip);
		}
		return false;
	}

	@Override
	public float getExperience() {
		return 10;
	}

}
