package ch.neb.spacegame.world;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.DamageIndicatorEntity;
import ch.neb.spacegame.DrawGameInfoEntity;
import ch.neb.spacegame.SpawnerGameEntity;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.Sprite;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;

// TODO somehow limit enemy objects
// TODO limitles space?
public class World {

	public int width;
	public int height;

	private Player player;

	private List<GameEntity> gameEntities = new ArrayList<>();
	private List<CollisionListener> collisionListeners = new ArrayList<>();
	private List<SpawnListener> spawnListeners = new ArrayList<>();

	private Queue<GameEntity> removeGameObejctQueue = new LinkedList<>();
	private Queue<GameEntity> addGameObjectQueue = new LinkedList<>();

	private List<Sprite> stars = new ArrayList<>();

	// TODO into own game entity

	public World(int width, int height) {
		super();
		this.width = width;
		this.height = height;

		createPlayer();
		createStars(2500, 2000, 100, 15);

		addEntity(new DamageIndicatorEntity(this));
		addEntity(new DrawGameInfoEntity(this));
		addEntity(new SpawnerGameEntity(this));

		// play background music
		// try {
		// File file = new File(SpaceGame.class.getClassLoader().getResource("audio/background.wav").toURI());
		// AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		// Clip clip = AudioSystem.getClip();
		// clip.open(audioInputStream);
		// clip.loop(Clip.LOOP_CONTINUOUSLY);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

	}

	private void createPlayer() {
		player = new Player(this);
		addEntity(player);
	}

	public Player getPlayer() {
		return player;
	}

	private void createStars(int bigStars, int smallStars, int numberOfStarsystems, int suns) {
		for (int i = 0; i < bigStars; ++i) {
			stars.add(new Sprite(Arts.star1, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
		for (int i = 0; i < smallStars; ++i) {
			stars.add(new Sprite(Arts.star2, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
		for (int i = 0; i < numberOfStarsystems; ++i) {
			stars.add(new Sprite(Arts.starsystem, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
		for (int i = 0; i < suns; ++i) {
			stars.add(new Sprite(Arts.sun, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
			stars.add(new Sprite(Arts.sun2, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
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

	public static <T> List<T> create(Class<T> c) {
		return new ArrayList<T>();
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

	public void render(Graphics2D graphics, Camera camera) {
		for (Sprite star : stars) {
			if (camera.isInView(star.bounds))
				graphics.drawImage(star.image, (int) (star.position.x - camera.getX()), (int) (star.position.y - camera.getY()), null);
		}

		synchronized (this) {
			for (GameEntity object : gameEntities) {
				if (object.isInView(camera)) {
					object.render(graphics, camera);
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

	public boolean shouldCollide(GameEntity other) {
		return false;
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
