package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.GameScreenManager;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;
import ch.neb.spacegame.gameScreens.menu.MainMenuScreen;

/**
 * Draws user information (like the xp, level etc.)
 * 
 */
public class GameInfoEntity extends GameEntity {

	private final Color color = new Color(91, 40, 91, 150);

	private int XP_BAR_WIDTH = 800 - 100;

	private Font pointsFont;
	private Font infoFont;
	private Font xpFont;
	private Font deathFont;
	private Font pausedFont;

	private long deathTime = MAX_DEATH_TIME;
	private static long MAX_DEATH_TIME = 4000;

	private static long MAX_INITIAL_SHOW_TIME = 12000;
	private long initialInfoTime = MAX_INITIAL_SHOW_TIME;

	private Player player;

	public GameInfoEntity(SpaceGameScreen spaceGameScreen, Player player) {
		super(spaceGameScreen);
		this.player = player;

		setLayer(Layer.HUD);

		pointsFont = Arts.getFont(20);
		infoFont = Arts.getFont(25);
		xpFont = Arts.getFont(18);
		deathFont = Arts.getFont(75);
		pausedFont = Arts.getFont(110);
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return false;
	}

	@Override
	public void update(UpdateContext updateContext) {
		if (initialInfoTime > 0) {
			initialInfoTime -= updateContext.deltaT;
		}

		if (!player.isAlive()) {
			deathTime -= updateContext.deltaT;
		}

		if (deathTime < 0) {
			GameScreenManager screenManager = GameScreenManager.getInstance();

			screenManager.setScreen(new MainMenuScreen(screenManager.getScreen().getResolutionX(), screenManager.getScreen().getResolutionY(), null));
		}
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		if (!updateContext.isPaused) {
			if (initialInfoTime > 0) {
				graphics.setFont(infoFont);
				
				// draw info
				graphics.setColor(new Color(1f, 1f, 1f, (float) initialInfoTime / MAX_INITIAL_SHOW_TIME));
				drawInfo(graphics, 20, 250);
			}
		} else {
			graphics.setFont(pausedFont);
			graphics.setColor(Color.BLACK);
			graphics.drawString("Paused", 203, 300);
			graphics.drawString("Paused", 197, 300);
			graphics.drawString("Paused", 200, 303);
			graphics.drawString("Paused", 200, 297);
			graphics.setColor(Color.WHITE);
			graphics.drawString("Paused", 200, 300);
		}

		graphics.setColor(Color.WHITE);
		graphics.setFont(pointsFont);

		final String pointsString = "Level: " + (int) player.getLevel() + " Points: " + (int) player.getPoints();
		graphics.setColor(color);
		graphics.fillRect(800 - 250, 0, 250, 30);
		graphics.setColor(Color.WHITE);
		graphics.drawString(pointsString, 800 - 250 + 5, 20);

		// draw experience
		graphics.setColor(new Color(0, 0, 0, 180));
		graphics.fillRect(49, 569, XP_BAR_WIDTH + 1, 12);
		graphics.setColor(color);
		float xpPercentage = player.getTotalExperience() / player.getNextLevelExperience();
		graphics.fillRect(50, 570, (int) (xpPercentage * XP_BAR_WIDTH), 10);
		graphics.setColor(new Color(255, 255, 255));
		graphics.setFont(xpFont);
		graphics.drawString((int) (xpPercentage * 100) + "%", 400, 580);

		if (!player.isAlive()) {
			graphics.setFont(deathFont);
			final int alpha = (int) (50 + 200 * deathTime / MAX_DEATH_TIME);
			graphics.setColor(new Color(255, 255, 255, alpha));
			graphics.drawString("Game Over!", 200, 300);

		}
	}

	private void drawInfo(Graphics2D graphics, int x, int y) {
		graphics.drawString("Use W A S D for movement.", x, y);
		graphics.drawString("Press Shift for a speed boost an Space for a shield.", x, y += 25);
		graphics.drawString("Use the right mouse button for a laser.", x, y += 25);
		graphics.drawString("Press Escape to exit the game.", x, y += 25);
		graphics.drawString("Press P for pause..", x, y += 25);
		graphics.drawString("Gain Points by shooting stuff.", x, y += 25);
	}

	@Override
	public boolean isInView(AbstractCamera gameCamera) {
		return true;
	}

}
