package ch.neb.spacegame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;
import ch.neb.spacegame.gameScreens.game.enemies.EnemyShip;
import ch.neb.spacegame.math.Vec2;

/**
 * Draws arrow at the edge of the screen to show where {@link EnemyShip}s are.
 * 
 */
public class EnemyIndicatorEntity extends GameEntity {

	private List<EnemyShip> enemiesOutOfView = new ArrayList<>();
	private static final int ARROW_WIDTH = 3;
	private static final BasicStroke ARROW_STROKE = new BasicStroke(ARROW_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final Color ARROW_COLOR = new Color(255, 0, 0);
	private Player player;

	public EnemyIndicatorEntity(SpaceGameScreen spaceGameScreen, Player player) {
		super(spaceGameScreen);
		this.player = player;

		setLayer(Layer.GAME_OVERLAY);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		enemiesOutOfView.clear();

		for (GameEntity entity : spaceGameScreen.getGameEntities()) {
			if (entity instanceof EnemyShip) {
				final EnemyShip ship = (EnemyShip) entity;
				if (!updateContext.gameCamera.isInView(ship.bounds)) {
					enemiesOutOfView.add(ship);
				}
			}
		}

	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

		// int arrowSize = 50;
		int arrowWidth = 30;
		int arrowHeight = 10;
		int delta = arrowWidth - arrowHeight;

		graphics.setStroke(ARROW_STROKE);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (EnemyShip ship : enemiesOutOfView) {
			final int x = (int) Math.min(Math.max(0, ship.getPosition().x - updateContext.gameCamera.getX()), updateContext.gameCamera.width - arrowWidth); // - arrowHeight because the arrow will be drawn 90°
																																					// turned
			final int y = (int) Math.min(Math.max(0, ship.getPosition().y - updateContext.gameCamera.getY()), updateContext.gameCamera.height - arrowWidth);
			final String distanceString = String.valueOf((int) Vec2.distance(player.getPosition(), ship.getPosition()) / 10);
			final int stringWidth = (int) graphics.getFontMetrics().getStringBounds(distanceString, graphics).getWidth();

			graphics.setColor(ARROW_COLOR);
			// if (x == 0 && y == 0) {
			// // topleft
			// graphics.drawLine(x + arrowWidth - 5, y + ARROW_WIDTH - 1, 5, 9);
			// graphics.drawLine(x + ARROW_WIDTH - 1, y + arrowWidth - 5 + ARROW_WIDTH, 5, 9);

			if (x == 0) {
				// west arrow
				graphics.drawLine(x + arrowHeight, y, x + ARROW_WIDTH, y + arrowWidth / 2);
				graphics.drawLine(x + arrowHeight, y + arrowWidth, x + ARROW_WIDTH, y + arrowWidth / 2);
				graphics.drawString(distanceString, x + 10, y + arrowWidth / 2 + 5);
			} else if (y == updateContext.gameCamera.height - arrowWidth) {
				// south arrow
				graphics.drawLine(x, y + delta, x + arrowWidth / 2, y + arrowHeight - ARROW_WIDTH + delta);
				graphics.drawLine(x + arrowWidth, y + delta, x + arrowWidth / 2, y + arrowHeight - ARROW_WIDTH + delta);
				graphics.drawString(distanceString, x + arrowWidth / 2 - stringWidth / 2, y - 20 + delta);
			} else if (x == updateContext.gameCamera.width - arrowWidth) {
				// east arrow
				graphics.drawLine(x + delta, y, x + arrowHeight - ARROW_WIDTH + delta, y + arrowWidth / 2);
				graphics.drawLine(x + delta, y + arrowWidth, x + arrowHeight - ARROW_WIDTH + delta, y + arrowWidth / 2);
				graphics.drawString(distanceString, x - stringWidth + delta, y + arrowWidth / 2 + 5);
			} else if (y == 0) {
				// north arrow
				graphics.drawLine(x, y + arrowHeight, x + arrowWidth / 2, y + ARROW_WIDTH);
				graphics.drawLine(x + arrowWidth, y + arrowHeight, x + arrowWidth / 2, y + ARROW_WIDTH);
				graphics.drawString(distanceString, x + arrowWidth / 2 - stringWidth / 2, y + 20);
			}
			// graphics.fillRect(x, y, arrowSize, arrowSize);
		}

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setStroke(new BasicStroke());

	}

	@Override
	public boolean isInView(AbstractCamera gameCamera) {
		return true;
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}

}
