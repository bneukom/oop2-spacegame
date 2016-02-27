package ch.fhnw.oop2.gameScreens.menu;

import ch.fhnw.oop2.gameScreens.AbstractCamera;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class MenuCamera extends AbstractCamera {

	public MenuCamera(float width, float height) {
		super(width, height);
		
		position = new Vec2(0, 0);
	}

	@Override
	public void setPosition(Vec2 position) {

	}

}
