package ch.fhnw.oop2.spacegame;

public interface KillListener {
	/**
	 * Called after a {@link GameEntity} has been killed.
	 * 
	 * @param by
	 *            the attacker
	 */
	public void killed(GameEntity by);
}
