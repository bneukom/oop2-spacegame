package ch.fhnw.oop2.gameScreens.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.guns.Gun;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.Arts;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class SpaceShip extends Mob {

	protected List<Gun> guns = new ArrayList<Gun>();

	public SpaceShip(BufferedImage image, GameScreen spaceGameScreen) {
		super(spaceGameScreen, image);
	}

	public SpaceShip(GameScreen spaceGameScreen, BufferedImage image, float speed, float maxHealth) {
		super(spaceGameScreen, image, speed, maxHealth);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);
		for (Gun gun : guns) {
			gun.update(updateContext.deltaT);
		}

	}

	public void addGun(Gun gun) {
		this.guns.add(gun);
	}

	public void shoot(Vec2 direction) {
		final Vec2 shotPosition = new Vec2(position);
		shotPosition.x += getWidth() / 2;
		shotPosition.y += getHeight() / 2;

		for (Gun gun : guns) {
			gun.shoot(shotPosition, direction);
		}
	}

	public void shoot() {
		shoot(new Vec2(direction));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.explosion, 48, 48, 100, 1), true, new Vec2(position).translate(-14, -14), new Vec2(1, 0)));
		spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.explosion, 48, 48, 110, 1), false, new Vec2(position).translate(-19, -9), new Vec2(1, 0)));
		spaceGameScreen.addEntity(new Explosion(spaceGameScreen, new Animation(Arts.explosion, 48, 48, 115, 1), false, new Vec2(position).translate(-5, -5), new Vec2(1, 0)));
	}

}
