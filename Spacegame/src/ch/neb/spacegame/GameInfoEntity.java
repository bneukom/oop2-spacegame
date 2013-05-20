package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.neb.spacegame.world.World;

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

	private static long MAX_INITIAL_SHOW_TIME = 12000;
	private long initialInfoTime = MAX_INITIAL_SHOW_TIME;

	public GameInfoEntity(World world) {
		super(world);

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
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		if (!updateContext.isPaused) {
			if (initialInfoTime > 0) {
				graphics.setFont(infoFont);
				graphics.setColor(new Color(1f, 1f, 1f, (float) initialInfoTime / MAX_INITIAL_SHOW_TIME));

				graphics.drawString("Use W A S D for movement.", 20, 250);
				graphics.drawString("Press Shift for a speed boost an Space for a shield.", 20, 275);
				graphics.drawString("Use the right mouse button for a laser.", 20, 300);
				graphics.drawString("Press Escape to exit the game.", 20, 325);
				graphics.drawString("Press P for pause..", 20, 350);
				graphics.drawString("Gain Points by shooting stuff.", 20, 376);
			}
		} else {
			graphics.setFont(pausedFont);
			graphics.setColor(Color.WHITE);
			graphics.drawString("Paused", 200, 300);
		}

		graphics.setColor(Color.WHITE);
		graphics.setFont(pointsFont);

		final String pointsString = "Level: " + (int) world.getPlayer().getLevel() + " Points: " + (int) world.getPlayer().getPoints();
		graphics.setColor(color);
		graphics.fillRect(800 - 250, 0, 250, 30);
		graphics.setColor(Color.WHITE);
		graphics.drawString(pointsString, 800 - 250 + 5, 20);

		// draw experience
		graphics.setColor(new Color(0, 0, 0, 180));
		graphics.fillRect(49, 569, XP_BAR_WIDTH + 1, 12);
		graphics.setColor(color);
		float xpPercentage = world.getPlayer().getTotalExperience() / world.getPlayer().getNextLevelExperience();
		graphics.fillRect(50, 570, (int) (xpPercentage * XP_BAR_WIDTH), 10);
		graphics.setColor(new Color(255,255,255));
		graphics.setFont(xpFont);
		graphics.drawString((int)(xpPercentage * 100) + "%", 400, 580);

		if (!world.getPlayer().isAlive()) {
			graphics.setFont(deathFont);
			graphics.setColor(Color.WHITE);
			graphics.drawString("Game Over!", 200, 300);
		}
	}

	@Override
	public boolean isInView(Camera camera) {
		return true;
	}

}
