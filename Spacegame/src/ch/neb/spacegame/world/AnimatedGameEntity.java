package ch.neb.spacegame.world;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.Animation;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;

public class AnimatedGameEntity extends GameEntity {

	protected Animation animation;
	private AffineTransform transform = new AffineTransform();
	protected Vec2 halfWidth;

	public AnimatedGameEntity(World world, Animation animation, Vec2 position) {
		this(world, animation, position, new Vec2(1, 0));
	}

	public AnimatedGameEntity(World world, Animation animation, Vec2 position, Vec2 direction) {
		super(world, position, direction);
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
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		float screenX = position.x - camera.getX();
		float screenY = position.y - camera.getY();

		final BufferedImage currentImage = animation.getCurrentImage();

		transform.setToIdentity();
		transform.rotate(Math.atan2(direction.y, direction.x) + Math.PI / 2, screenX + currentImage.getWidth() / 2, screenY + currentImage.getHeight() / 2);
		transform.translate(screenX, screenY);

		graphics.drawImage(currentImage, transform, null);
	}

}
