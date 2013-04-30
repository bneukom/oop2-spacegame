package ch.neb.spacegame;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.math.Vec2;

public class Sprite {
	public final BufferedImage image;
	public final Vec2 position;
	public final Rectangle2D.Float bounds;

	public Sprite(BufferedImage image, Vec2 position) {
		super();
		this.image = image;
		this.position = position;
		this.bounds = new Rectangle2D.Float(position.x, position.y, image.getWidth(), image.getHeight());
	}

}
