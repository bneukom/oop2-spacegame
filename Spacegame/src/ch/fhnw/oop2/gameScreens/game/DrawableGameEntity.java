package ch.fhnw.oop2.gameScreens.game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class DrawableGameEntity extends GameEntity {
	protected BufferedImage image;
	private AffineTransform transform = new AffineTransform();
	protected Vec2 halfWidth;

	public DrawableGameEntity(GameScreen spaceGameScreen, BufferedImage image) {
		this(spaceGameScreen, image, DEFAULT_SPEED);
	}

	public DrawableGameEntity(GameScreen spaceGameScreen, BufferedImage image, float speed) {
		super(spaceGameScreen, speed);
		this.image = image;

		if (image != null)
			halfWidth = new Vec2(image.getWidth() / 2f, image.getHeight() / 2f);
		else
			halfWidth = new Vec2(0, 0);
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		if (image != null) {
			transform.setToIdentity();
			float screenX = position.x - updateContext.gameCamera.getX();
			float screenY = position.y - updateContext.gameCamera.getY();
			transform.rotate(Math.atan2(direction.y, direction.x) + Math.PI / 2, screenX + image.getWidth() / 2, screenY + image.getHeight() / 2);
			transform.translate(screenX, screenY);

			graphics.drawImage(image, transform, null);
		}
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

	}

	public float getWidth() {
		return image.getWidth();
	}

	public float getHeight() {
		return image.getHeight();
	}
}
