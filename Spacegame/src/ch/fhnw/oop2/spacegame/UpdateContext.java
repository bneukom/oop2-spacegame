package ch.fhnw.oop2.spacegame;

import ch.fhnw.oop2.gameScreens.AbstractCamera;


public class UpdateContext {
	public Keyboard keyboard;
	public MouseInput mouseInput;
	public long deltaT;
	public AbstractCamera gameCamera;
	public boolean isPaused;
}
