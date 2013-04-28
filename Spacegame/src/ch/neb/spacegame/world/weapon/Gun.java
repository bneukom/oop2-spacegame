package ch.neb.spacegame.world.weapon;

import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.World;

public abstract class Gun {

	private long currentCooldown;
	private final long cooldown;
	protected final World world;

	public Gun(long cooldown, World world) {
		super();
		this.cooldown = cooldown;
		this.world = world;
	}

	public void shoot(Vec2 position, Vec2 direction) {
		if (currentCooldown > cooldown) {
			doShoot(position, direction);
			currentCooldown = 0;
		}
	}

	protected abstract void doShoot(Vec2 position, Vec2 direction);

	public void update(long deltaT) {
		currentCooldown += deltaT;
	}
}
