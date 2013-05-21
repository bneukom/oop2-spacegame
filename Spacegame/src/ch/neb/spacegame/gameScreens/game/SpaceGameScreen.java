package ch.neb.spacegame.gameScreens.game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.DamageIndicatorEntity;
import ch.neb.spacegame.EnemyIndicatorEntity;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.GameInfoEntity;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.SpawnerGameEntity;
import ch.neb.spacegame.SpawnerGameEntity.DebrisSpawner;
import ch.neb.spacegame.SpawnerGameEntity.EnemySpawner;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.GameScreenManager;
import ch.neb.spacegame.gameScreens.SpaceScreen;
import ch.neb.spacegame.gameScreens.menu.MainMenuScreen;

// TODO somehow limit enemy objects
public class SpaceGameScreen extends SpaceScreen {

	private Player player;

	private List<CollisionListener> collisionListeners = new ArrayList<>();
	private List<SpawnListener> spawnListeners = new ArrayList<>();

	private BufferedImage space;
	private GameCamera camera;

	public SpaceGameScreen(int resolutionX, int resolutionY) {
		super(resolutionX, resolutionY);
		player = new Player(this);
		addEntity(player);

		addEntity(new DamageIndicatorEntity(this));
		addEntity(new GameInfoEntity(this, player));
		addEntity(new EnemyIndicatorEntity(this, player));

		SpawnerGameEntity entity = new SpawnerGameEntity(this);
		entity.addSpawner(new DebrisSpawner(this, player, 250));
		entity.addSpawner(new EnemySpawner(this, player, 4000));
		addEntity(entity);

		space = generateSpaceImage(2000, 2000, 1450, 800, 25, 12);
		camera = new GameCamera(resolutionX, resolutionY);
	}

	@Override
	protected synchronized void addEntities() {
		// fire listeners
		for (GameEntity gameEntity : addGameObjectQueue) {
			for (SpawnListener spawnListener : spawnListeners) {
				spawnListener.spawned(gameEntity);
			}
		}

		// add entities
		super.addEntities();
	}

	public Player getPlayer() {
		return player;
	}

	public void update(UpdateContext updateContext) {
		// main menu
		if (updateContext.keyboard.escape.isDown) {
			GameScreenManager.getInstance().setScreen(new MainMenuScreen(resolutionX, resolutionY));
		}

		super.update(updateContext);

		checkCollisions();
	}

	public void render(Graphics2D graphics, UpdateContext updateContext) {

		// update the camera position
		updateContext.gameCamera.setPosition(getPlayer().getPosition());

		// render background
		float playerX = player.getPosition().x;
		float playerY = player.getPosition().y;
		int x = playerX >= 0 ? (int) (playerX % space.getWidth()) : (int) ((space.getWidth() + playerX % space.getWidth()));
		int y = playerY >= 0 ? (int) (playerY % space.getHeight()) : (int) ((space.getHeight() + playerY % space.getHeight()));

		graphics.drawImage(space, -x, -y, null);
		graphics.drawImage(space, space.getWidth() - x, -y, null);
		graphics.drawImage(space, -x, space.getHeight() - y, null);
		graphics.drawImage(space, space.getWidth() - x, space.getHeight() - y, null);

		// graphics.drawImage(space, 0, 0, null);

		// render game entities
		super.render(graphics, updateContext);
	}

	// TODO use a quadtree for better performance
	private synchronized void checkCollisions() {
		for (int i = 0; i < gameEntities.size(); ++i) {
			for (int j = 0; j < gameEntities.size(); ++j) {

				final GameEntity a = gameEntities.get(i);
				final GameEntity b = gameEntities.get(j);

				if (a != b && (a.shouldCollide(b) || b.shouldCollide(a)) && a.collidesWith(b)) {
					a.onCollide(b, this);
					b.onCollide(a, this);

					// fire collision events
					for (CollisionListener collisionListener : collisionListeners) {
						collisionListener.onCollide(a, b);
					}
				}
			}
		}
	}

	public synchronized void addCollisionListener(CollisionListener collisionListener) {
		this.collisionListeners.add(collisionListener);
	}

	public synchronized void addSpawnListener(SpawnListener spawnListener) {
		spawnListeners.add(spawnListener);
	}

	@Override
	public AbstractCamera getCamera() {
		return camera;
	}
}
