package ch.fhnw.oop2.gameScreens.game.bullets;

import java.awt.image.BufferedImage;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.Explosion;
import ch.fhnw.oop2.gameScreens.game.Mob;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.Arts;
import ch.fhnw.oop2.spacegame.CollisionListener;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class Rocket extends Bullet {

	private float maxSpeed = 1.6f;
	private Mob target;
	private boolean ignoreOtherTargets;
	private boolean targetDead;

	public Rocket(GameScreen spaceGameScreen, GameEntity owner, boolean ignoreOtherTargets, CollisionListener collisionListener, BufferedImage image, Vec2 direction, Mob target, Vec2 position,
			float initialspeed, float damage) {
		super(spaceGameScreen, owner, collisionListener, image, direction, position, initialspeed, damage);
		this.ignoreOtherTargets = ignoreOtherTargets;
		this.target = target;
	}

	@Override
	public void update(UpdateContext updateContext) {
		speed += Math.max(Math.pow(age / 6000, 2), 0.0002);
		speed = Math.min(speed, maxSpeed);

		// aim for target
		if (target != null /* && target.getHealth() > 0 */) {
			final Vec2 targetPosition = target.getPosition();

			if (Vec2.distance(position, targetPosition) < 40 && target.getHealth() <= 0) {
				targetDead = true;
			}

			if (!targetDead) {
				// aim for the target but only change direction in small steps (and
				// make it dependent on the speed so if the rocket is slow, it
				// cannot turn as fast).
				final Vec2 rocketToMobVector = Vec2.subtract(targetPosition, position).normalize().multiply(0.019f * updateContext.deltaT * (speed + 0.4f));
				direction.add(rocketToMobVector).normalize();
			}

		}

		super.update(updateContext);
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		if (ignoreOtherTargets)
			return other == target;
		else
			return super.shouldCollide(other);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.smallexplosion, 23, 23, 1, 100, 1), false, new Vec2(position), new Vec2(1, 0)));
	}

}
