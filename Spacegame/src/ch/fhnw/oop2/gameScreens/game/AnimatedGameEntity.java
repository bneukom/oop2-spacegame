package ch.fhnw.oop2.gameScreens.game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.spacegame.Animation;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class AnimatedGameEntity extends GameEntity {

	protected Animation animation;
	private AffineTransform transform = new AffineTransform();
	protected Vec2 halfWidth;

	public AnimatedGameEntity(GameScreen spaceGameScreen, Animation animation, Vec2 position) {
		this(spaceGameScreen, animation, position, new Vec2(1, 0));
	}

	public AnimatedGameEntity(GameScreen spaceGameScreen, Animation animation, Vec2 position, Vec2 direction) {
		super(spaceGameScreen, position, direction);
		this.animation = animation;
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);
		animation.update(updateContext.deltaT);
	}

	@Override
	public float getWidth() {
		return animation.getFrameWidth();
	}

	@Override
	public float getHeight() {
		return animation.getFrameHeight();
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		float screenX = position.x - updateContext.gameCamera.getX();
		float screenY = position.y - updateContext.gameCamera.getY();

		final BufferedImage currentImage = animation.getCurrentImage();

		transform.setToIdentity();
		transform.rotate(Math.atan2(direction.y, direction.x) + Math.PI / 2, screenX + currentImage.getWidth() / 2, screenY + currentImage.getHeight() / 2);
		transform.translate(screenX, screenY);

		graphics.drawImage(currentImage, transform, null);
	}

}
