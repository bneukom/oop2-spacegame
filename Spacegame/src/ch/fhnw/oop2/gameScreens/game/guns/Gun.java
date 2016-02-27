package ch.fhnw.oop2.gameScreens.game.guns;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.spacegame.CollisionListener;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.math.Vec2;

public abstract class Gun {

	private long currentCooldown;
	private final long cooldown;
	protected final GameScreen spaceGameScreen;
	protected CollisionListener collisionListener;
	protected GameEntity owner;

	public Gun(long cooldown, GameScreen spaceGameScreen, GameEntity owner, CollisionListener collisionListener) {
		super();
		this.cooldown = cooldown;
		this.spaceGameScreen = spaceGameScreen;
		this.owner = owner;
		this.collisionListener = collisionListener;
	}

	public void shoot(Vec2 position, Vec2 direction) {
		if (currentCooldown > cooldown) {
			doShoot(position, direction);
			currentCooldown = 0;
		}
	}
	
	public abstract void upgrade(int level);

	protected abstract void doShoot(Vec2 position, Vec2 direction);

	public void update(long deltaT) {
		currentCooldown += deltaT;
	}
}
