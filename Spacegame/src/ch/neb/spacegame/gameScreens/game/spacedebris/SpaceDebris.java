package ch.neb.spacegame.gameScreens.game.spacedebris;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.Mob;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.gameScreens.game.bullets.Bullet;
import ch.neb.spacegame.math.Vec2;

public abstract class SpaceDebris extends Mob {


	private Vec2 movementDirection;

	private float angularSpeed;

	protected long age;

	protected static long MIN_AGE_TO_DIE = 10000;
	protected static final int MIN_DISTANCE = 1000;

	public SpaceDebris(GameScreen spaceGameScreen, BufferedImage image, Vec2 movementDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
		super(spaceGameScreen, image, speed, maxHealth);
		this.movementDirection = movementDirection;
		this.position = position;
		this.angularSpeed = angularSpeed;

	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		age += updateContext.deltaT;

		// always fly to the same direction
		final Vec2 offset = Vec2.multiply(movementDirection, speed * updateContext.deltaT);
		position.add(offset);

		final float theta = angularSpeed * updateContext.deltaT;

		direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
		direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

	}
	
	
	@Override
	public float getExperience() {
		return 1;
	}

	public void setMovementDirection(Vec2 d) {
		this.movementDirection = d;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Bullet || other instanceof Player;
	}

}
