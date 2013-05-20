package ch.neb.spacegame;

import ch.neb.spacegame.world.Mob;

/**
 * Gets notified after a {@link Mob} has been hit.
 * 
 */
public interface DamageListener {
	public void damageRecieved(GameEntity attackee, float amount);
}
