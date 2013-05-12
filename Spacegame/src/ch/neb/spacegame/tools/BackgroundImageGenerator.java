package ch.neb.spacegame.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.neb.spacegame.Arts;

public class BackgroundImageGenerator {
	public static void main(String[] args) {
		generate(800, 600, 150, 75, 6, 3);

	}

	private static void generate(int width, int height, int smallStars, int bigStars, int numberOfStarsystems, int suns) {
		final BufferedImage background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = (Graphics2D) background.getGraphics();

		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, width, height);

		for (int i = 0; i < bigStars; ++i) {
			drawImageRandom(graphics, Arts.star1, width, height);
		}
		for (int i = 0; i < smallStars; ++i) {
			drawImageRandom(graphics, Arts.star2, width, height);
		}
		for (int i = 0; i < numberOfStarsystems; ++i) {
			drawImageRandom(graphics, Arts.starsystem, width, height);
		}
		for (int i = 0; i < suns; ++i) {
			drawImageRandom(graphics, Arts.sun, width, height);
			drawImageRandom(graphics, Arts.sun2, width, height);
		}

		try {
			ImageIO.write(background, "png", new File("res/sprites/space.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void drawImageRandom(Graphics2D graphics, BufferedImage dst, int width, int height) {
		graphics.drawImage(dst, (int) (Math.min(Math.random() * width, width - dst.getWidth())), (int) (Math.min(Math.random() * height, width - dst.getWidth())), null);
	}
}
