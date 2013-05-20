package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Audio;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.DamageIndicatorEntity;
import ch.neb.spacegame.GameInfoEntity;
import ch.neb.spacegame.EnemyIndicatorEntity;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.Keyboard;
import ch.neb.spacegame.SpaceGame;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.SpawnerGameEntity;
import ch.neb.spacegame.Sprite;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;

// TODO somehow limit enemy objects
public class World {

	private Player player;

	private List<GameEntity> gameEntities = new ArrayList<>();
	private List<CollisionListener> collisionListeners = new ArrayList<>();
	private List<SpawnListener> spawnListeners = new ArrayList<>();

	private Queue<GameEntity> removeGameObejctQueue = new LinkedList<>();
	private Queue<GameEntity> addGameObjectQueue = new LinkedList<>();

	private BufferedImage space;

	public World() {
		super();

		createPlayer();

		addEntity(new DamageIndicatorEntity(this));
		addEntity(new GameInfoEntity(this));
		addEntity(new SpawnerGameEntity(this));
		addEntity(new EnemyIndicatorEntity(this));

		space = generate(800, 600, 150, 75, 6, 3);

		// Audio.loopSound("audio/megamantheme.wav");
	}

	private void createPlayer() {
		player = new Player(this);
		addEntity(player);
	}

	public Player getPlayer() {
		return player;
	}

	public List<GameEntity> getGameEntities() {
		return gameEntities;
	}

	public void update(UpdateContext updateContext) {
		// remove/add game objects
		removeEntities();
		addEntities();

		// update all game objects of this world
		synchronized (this) {
			for (GameEntity object : gameEntities) {
				object.update(updateContext);
			}
		}
	}

	private synchronized void addEntities() {
		while (!addGameObjectQueue.isEmpty()) {
			final GameEntity poll = addGameObjectQueue.poll();
			gameEntities.add(poll);
			for (SpawnListener spawnListener : spawnListeners) {
				spawnListener.spawned(poll);
			}
		}

		Collections.sort(gameEntities);
	}

	private synchronized void removeEntities() {
		while (!removeGameObejctQueue.isEmpty()) {
			final GameEntity entity = removeGameObejctQueue.poll();
			entity.onDestroy();
			gameEntities.remove(entity);
		}
	}

	private BufferedImage generate(int width, int height, int smallStars, int bigStars, int numberOfStarsystems, int suns) {
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
		graphics.drawImage(dst, (int) (Math.min(Math.random() * width, width - dst.getWidth())), (int) (Math.min(Math.random() * height, width - dst.getWidth())), null);
	}

	public void render(Graphics2D graphics, UpdateContext updateContext) {
		// render background
		graphics.drawImage(space, 0, 0, null);

		// render game entities
		synchronized (this) {
			for (GameEntity object : gameEntities) {
				if (object.isInView(updateContext.camera)) {
					object.render(graphics, updateContext);
				}
			}
		}
	}

	// TODO use a quadtree for better performance
	public synchronized void checkCollisions() {
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

	public synchronized void addEntity(GameEntity entity) {
		addGameObjectQueue.add(entity);
	}

	public synchronized void removeEntity(GameEntity entity) {
		removeGameObejctQueue.add(entity);
	}

	public void addCollisionListener(CollisionListener collisionListener) {
		this.collisionListeners.add(collisionListener);
	}

	public void addSpawnListener(SpawnListener spawnListener) {
		spawnListeners.add(spawnListener);
	}
}
