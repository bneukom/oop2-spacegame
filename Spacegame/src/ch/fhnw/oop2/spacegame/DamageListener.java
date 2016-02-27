package ch.fhnw.oop2.spacegame;

import ch.fhnw.oop2.gameScreens.game.Mob;

/**
 * Gets notified after a {@link Mob} has been hit.
 * 
 */
public interface DamageListener {
	public void damageRecieved(GameEntity attackee, float amount);
}
