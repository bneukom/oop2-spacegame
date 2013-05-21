package ch.neb.spacegame.gameScreens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.neb.spacegame.Arts;

public abstract class SpaceScreen extends GameScreen {

	public SpaceScreen(int resolutionX, int resolutionY) {
		super(resolutionX, resolutionY);
	}

	protected BufferedImage space;


	protected BufferedImage generateSpaceImage(int width, int height, int smallStars, int bigStars, int numberOfStarsystems, int suns) {
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

		graphics.dispose();

		return background;
	}

	private static void drawImageRandom(Graphics2D graphics, BufferedImage dst, int width, int height) {
		graphics.drawImage(dst, (int) (Math.min(Math.random() * width, width - dst.getWidth())), (int) (Math.min(Math.random() * height, height - dst.getHeight())), null);
	}

}
