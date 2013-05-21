package ch.neb.spacegame.gameScreens.menu;

import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.math.Vec2;

public class MenuCamera extends AbstractCamera {

	public MenuCamera(float width, float height) {
		super(width, height);
		
		position = new Vec2(0, 0);
	}

	@Override
	public void setPosition(Vec2 position) {

	}

}
