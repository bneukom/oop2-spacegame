package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.neb.spacegame.world.World;

public class DrawGameInfoEntity extends GameEntity {

	private final Color color = new Color(91, 40, 91, 150);

	private int XP_BAR_WIDTH = 800 - 15;

	private Font pointsFont;
	private Font infoFont;
	private Font deathFont;

	private static long MAX_INITIAL_SHOW_TIME = 11000;
	private long initialInfoTime = MAX_INITIAL_SHOW_TIME;

	public DrawGameInfoEntity(World world) {
		super(world);

		setLayer(Layer.OVERLAY);

		pointsFont = Arts.getFont(20);
		infoFont = Arts.getFont(25);
		deathFont = Arts.getFont(75);
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
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		if (initialInfoTime > 0) {
			graphics.setFont(infoFont);
			graphics.setColor(new Color(1f, 1f, 1f, (float) initialInfoTime / MAX_INITIAL_SHOW_TIME));

			graphics.drawString("Use W A S D for movement.", 50, 250);
			graphics.drawString("Press Shift for a speed boost an Space for a shield.", 50, 275);
			graphics.drawString("Use the right mouse button for a laser.", 50, 300);
			graphics.drawString("Press Escape to exit the game.", 50, 325);
			graphics.drawString("Gain Points by shooting stuff.", 50, 350);
		}

		graphics.setColor(Color.WHITE);
		graphics.setFont(pointsFont);

		final String pointsString = "Level: " + (int) world.getPlayer().getLevel() + " Points: " + (int) world.getPlayer().getPoints();
		graphics.drawString(pointsString, 800 - graphics.getFontMetrics().stringWidth(pointsString) - 5, 20);

		graphics.setColor(color);
		graphics.fillRect(15, 570, (int) ((world.getPlayer().getTotalExperience() / world.getPlayer().getNextLevelExperience()) * XP_BAR_WIDTH), 10);

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
