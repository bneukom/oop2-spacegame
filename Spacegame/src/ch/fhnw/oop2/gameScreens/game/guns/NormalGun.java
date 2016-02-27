package ch.fhnw.oop2.gameScreens.game.guns;

import java.awt.image.BufferedImage;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.bullets.Bullet;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class NormalGun extends Gun {

	private int shots = 3;
	private float damage;
	private BufferedImage bullet;

	public NormalGun(long cooldown, GameScreen spaceGameScreen, BufferedImage bullet, GameEntity owner, int initialShots, float damage) {
		super(cooldown, spaceGameScreen, owner, null);
		this.bullet = bullet;

		shots = initialShots;
		this.damage = damage;
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {
		// shot normal shots
		float theta = (float) Math.atan2(direction.y, direction.x);
		final float thetaOffset = 0.03f;
		float startAngle = theta - (shots / 2) * thetaOffset;

		for (int i = 0; i < shots; ++i) {
			final Vec2 shootPosition = new Vec2(position);
			shootPosition.x -= bullet.getWidth() / 2;
			shootPosition.y -= bullet.getHeight() / 2;
			spaceGameScreen.addEntity(new Bullet(spaceGameScreen, owner, collisionListener, bullet, new Vec2((float) Math.cos(startAngle), (float) Math.sin(startAngle)), shootPosition, 0.9f, damage));
			startAngle += thetaOffset;
		}
	}

	@Override
	public void upgrade(int level) {
		if (level % 3 == 0)
			shots += 2;
		else
			damage += 2;
	}

}
