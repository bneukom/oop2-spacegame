package ch.neb.spacegame.world.weapon;

import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.World;

public abstract class Gun {

	private long currentCooldown;
	private final long cooldown;
	protected final World world;
	protected CollisionListener collisionListener;
	protected GameEntity owner;

	public Gun(long cooldown, World world, GameEntity owner, CollisionListener collisionListener) {
		super();
		this.cooldown = cooldown;
		this.world = world;
		this.owner = owner;
		this.collisionListener = collisionListener;
	}

	public void shoot(Vec2 position, Vec2 direction) {
		if (currentCooldown > cooldown) {
			doShoot(position, direction);
			currentCooldown = 0;
		}
	}
	
	public abstract void upgrade();

	protected abstract void doShoot(Vec2 position, Vec2 direction);

	public void update(long deltaT) {
		currentCooldown += deltaT;
	}
}
