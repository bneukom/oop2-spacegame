package ch.neb.spacegame.gameScreens.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ch.fhnw.oop2.spacegame.Arts;
import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.Layer;
import ch.fhnw.oop2.spacegame.SpawnerGameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;
import ch.fhnw.oop2.spacegame.SpawnerGameEntity.Spawner;
import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.GameScreenManager;
import ch.neb.spacegame.gameScreens.SpaceScreen;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;
import ch.neb.spacegame.gameScreens.game.spacedebris.SpaceDebris;
import ch.neb.spacegame.math.Vec2;

public class MainMenuScreen extends SpaceScreen {

	private MenuCamera menuCamera;

	public MainMenuScreen(final int resolutionX, final int resolutionY, final SpaceGameScreen spaceGameScreen) {
		super(resolutionX, resolutionY);

		space = generateSpaceImage(800, 700, 350, 50, 5, 4);

		SpawnerGameEntity spawnerGameEntity = new SpawnerGameEntity(this);
		spawnerGameEntity.addSpawner(new DebrisSpawner(this, 400));
		addEntity(spawnerGameEntity);

		int y = spaceGameScreen != null ? 180 : 220;
		ButtonEntity newGameMenu = new ButtonEntity(this, new Runnable() {
			@Override
			public void run() {
				GameScreenManager.getInstance().setScreen(new SpaceGameScreen(resolutionX, resolutionY));
			}
		}, "New Game", 110, 550, 70, 10, y);

		if (spaceGameScreen != null) {
			ButtonEntity continueButton = new ButtonEntity(this, new Runnable() {
				@Override
				public void run() {
					GameScreenManager.getInstance().setScreen(spaceGameScreen);
				}
			}, "Continue", 110, 550, 70, 10, y += 100);
			addEntity(continueButton);
		}

		ButtonEntity options = new ButtonEntity(this, null, "Options", 72, 300, 50, 10, y += 100);
		ButtonEntity exit = new ButtonEntity(this, new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		}, "Exit", 72, 200, 50, 10, y += 100);

		addEntity(exit);
		addEntity(options);
		addEntity(newGameMenu);

		menuCamera = new MenuCamera(resolutionX, resolutionY);

	}

	@Override
	public boolean isPauseEnabled() {
		return false;
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		// main menu
		// if (updateContext.keyboard.escape.isDown) {
		// System.exit(0);
		// }
	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		graphics.drawImage(space, 0, 0, null);

		super.render(graphics, updateContext);
	}

	private static class DebrisSpawner extends Spawner {
		public DebrisSpawner(GameScreen spaceGameScreen, long cooldown) {
			super(spaceGameScreen, cooldown);
		}

		@Override
		public long getCooldown(long originalCooldown) {
			return originalCooldown;
		}

		@Override
		public void spawn(GameScreen spaceGameScreen, AbstractCamera gameCamera) {
			final Vec2 spawnPosition = new Vec2(0, 0);
			final Vec2 direction;
			final SpaceDebris debris = createDebris();

			final double random = Math.random();
			if (random < 0.25) {
				// left
				spawnPosition.translate(gameCamera.getX() - debris.getWidth(), (float) (gameCamera.getY() + Math.random() * gameCamera.height));
			} else if (random < 0.5) {
				// right
				spawnPosition.translate(gameCamera.getX() + gameCamera.width + debris.getWidth(), (float) (gameCamera.getY() + Math.random() * gameCamera.height));
			} else if (random < 0.75) {
				// top
				spawnPosition.translate((float) (gameCamera.getX() + Math.random() * gameCamera.width), gameCamera.getY() - debris.getHeight());
			} else {
				// bottom
				spawnPosition.translate((float) (gameCamera.getX() + Math.random() * gameCamera.width), gameCamera.getY() + gameCamera.height + debris.getHeight());
			}

			final int spawnOffset = 550;
			direction = Vec2.subtract(new Vec2(spaceGameScreen.getResolutionX() / 2, spaceGameScreen.getResolutionY() / 2), spawnPosition)
					.translate((float) (-spawnOffset + 2 * spawnOffset * Math.random()), (float) (-spawnOffset + 2 * spawnOffset * Math.random()))
					.normalize();

			debris.setPosition(spawnPosition);
			debris.setMovementDirection(direction);

			spaceGameScreen.addEntity(debris);

		}

		public SpaceDebris createDebris() {

			final float speed = (float) (Math.random() * 0.075f + 0.095f + 0.008);
			float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

			// random turn direction
			angularSpeed *= Math.random() > 0.5 ? -1 : 1;

			if (Math.random() < 0.25f) {
				final float maxHealth = (float) ((Math.random() * 10 + 20) * 0.45f);
				return new MenuSpaceDebris(spaceGameScreen, Arts.debris1, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			} else {
				final float maxHealth = (float) ((Math.random() * 100 + 50) * 0.65f);
				return new MenuSpaceDebris(spaceGameScreen, Arts.rock, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			}
		}
	}

	private static class MenuSpaceDebris extends SpaceDebris {

		public MenuSpaceDebris(GameScreen spaceGameScreen, BufferedImage image, Vec2 movementDirection, Vec2 position, float maxHealth, float speed, float angularSpeed) {
			super(spaceGameScreen, image, movementDirection, position, maxHealth, speed, angularSpeed);

			drawHealth = false;
		}

		@Override
		public void update(UpdateContext updateContext) {
			super.update(updateContext);

			// remove if too far away from player
			if (age > MIN_AGE_TO_DIE && isInView(updateContext.gameCamera)) {
				spaceGameScreen.removeEntity(this);
			}
		}

	}

	private static class ButtonEntity extends GameEntity {

		private Font menuFont;
		private String text;
		private int size;
		private float width;
		private Rectangle2D.Float bounds = new Rectangle2D.Float();
		private final Color hooverColor = new Color(91, 40, 91, 220);
		private Runnable action;

		public ButtonEntity(GameScreen spaceGameScreen, Runnable action, String text, int size, float width, float height, float x, float y) {
			super(spaceGameScreen);
			this.action = action;
			this.text = text;
			this.size = size;
			this.width = width;

			setLayer(Layer.HUD);

			position.setTo(x, y);
			menuFont = Arts.getFont(size);

			bounds.setRect(x, y - height, width, height);

		}

		@Override
		public void update(UpdateContext updateContext) {
			super.update(updateContext);

			final Vec2 mousePos = updateContext.mouseInput.getPosition();
			if (bounds.contains(mousePos.x, mousePos.y) && updateContext.mouseInput.wasClicked(1) && action != null) action.run();

		}

		@Override
		public float getWidth() {
			return width;
		}

		@Override
		public float getHeight() {
			return size;
		}

		@Override
		public void render(Graphics2D graphics, UpdateContext updateContext) {
			super.render(graphics, updateContext);

			// border
			graphics.setFont(menuFont);
			graphics.setColor(Color.BLACK);
			graphics.drawString(text, position.x + 3, position.y);
			graphics.drawString(text, position.x - 3, position.y);
			graphics.drawString(text, position.x, position.y + 3);
			graphics.drawString(text, position.x, position.y - 3);

			Vec2 mousePos = updateContext.mouseInput.getPosition();
			if (bounds.contains(mousePos.x, mousePos.y)) {
				graphics.setColor(hooverColor);
			} else {
				graphics.setColor(Color.WHITE);
			}

			graphics.drawString(text, position.x, position.y);
		}

	}

	@Override
	public AbstractCamera getCamera() {
		return menuCamera;
	}

}
