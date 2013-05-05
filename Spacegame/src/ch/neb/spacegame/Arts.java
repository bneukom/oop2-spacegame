package ch.neb.spacegame;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class Arts {
	public static BufferedImage ship1 = createImage("sprites/ship1.png");
	public static BufferedImage ship2 = createImage("sprites/ship2.png");
	public static BufferedImage ship3 = createImage("sprites/ship3.png");
	public static BufferedImage ship4 = createImage("sprites/ship4.png");
	public static BufferedImage star1 = createImage("sprites/star1.png");
	public static BufferedImage star2 = createImage("sprites/star2.png");
	public static BufferedImage sun = createImage("sprites/sun.png");
	public static BufferedImage sun2 = createImage("sprites/sun2.png");
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
	public static BufferedImage exhaust = createImage("sprites/exhaust.png");


	public static BufferedImage createImage(String path) {
		try {
			return ImageIO.read(new File(Arts.class.getClassLoader().getResource(path).toURI()));
		} catch (IOException | URISyntaxException e) {
			throw new IllegalArgumentException("Invalid image path.");
		}

	}

	public static Font getFont(float size) {
		try {
			File resource = new File(Arts.class.getClassLoader().getResource("font/visitor1.ttf").toURI());
			return Font.createFont(Font.TRUETYPE_FONT, resource).deriveFont(size);
		} catch (FontFormatException | IOException | URISyntaxException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}
