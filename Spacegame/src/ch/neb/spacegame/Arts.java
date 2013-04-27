package ch.neb.spacegame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Arts {
	public static BufferedImage playerShip = createImage("sprites/playership.png");
	public static BufferedImage star1 = createImage("sprites/star1.png");
	public static BufferedImage star2 = createImage("sprites/star2.png");
	public static BufferedImage starsystem = createImage("sprites/starsystem.png");
	public static BufferedImage mouse = createImage("sprites/mouse.png");
	public static BufferedImage bullet1 = createImage("sprites/bullet1.png");
	public static BufferedImage bullet2 = createImage("sprites/bullet2.png");
	public static BufferedImage rock = createImage("sprites/rock.png");
	public static BufferedImage explosion = createImage("sprites/explosion.png");
	public static BufferedImage destroyedrock = createImage("sprites/destroyedrock.png");
	public static BufferedImage debris1 = createImage("sprites/debris1.png");
	public static BufferedImage rocket = createImage("sprites/rocket.png");
	public static BufferedImage smallexplosion = createImage("sprites/smallexplosion.png");

	public static BufferedImage createImage(String path) {
		try {
			return ImageIO.read(new File(Arts.class.getClassLoader().getResource(path).toURI()));
		} catch (IOException | URISyntaxException e) {
			throw new IllegalArgumentException("Invalid image path.");
		}

	}
}
