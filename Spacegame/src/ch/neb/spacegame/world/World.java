package ch.neb.spacegame.world;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.DamageIndicatorEntity;
import ch.neb.spacegame.DrawGameInfoEntity;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.SpaceGame;
import ch.neb.spacegame.SpawnListener;
import ch.neb.spacegame.Sprite;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.enemies.EnemyShip;
import ch.neb.spacegame.world.guns.NormalGun;
import ch.neb.spacegame.world.guns.RocketLauncher;
import ch.neb.spacegame.world.spacedebris.SmallSpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceRock;
import static ch.neb.spacegame.math.Random.selectRandom;

// TODO somehow limit enemy objects
// TODO better debris spawn!!!
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
	private long debrisSpawnTime = 0;
	private long enemySpawnTime = 0;
	private static final long DEBRIS_SPAWN_TIME = 150;
	private static final long ENEMY_SPAWN_TIME = 2000;

	public World(int width, int height) {
		super();
		this.width = width;
		this.height = height;

		createPlayer();
		createStars(2500, 2000, 100, 15);

		spawnInitialDebris(200);
		spawnInitialEnemies(5);

		addEntity(new DamageIndicatorEntity(this));
		addEntity(new DrawGameInfoEntity(this));

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

	public void spawnDebris(final Vec2 position, float theta) {
		final Vec2 direction = new Vec2((float) Math.cos(theta), (float) Math.sin(theta));

		final float speed = (float) (Math.random() * 0.06f + 0.075f);

		float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

		// random turn direction
		angularSpeed *= Math.random() > 0.5 ? -1 : 1;

		synchronized (this) {
			if (Math.random() < 0.2f) {
				final float maxHealth = (float) (Math.random() * 10 + 20);
				addEntity(new SmallSpaceDebris(this, direction, position, maxHealth, speed, angularSpeed));
			} else {
				final float maxHealth = (float) (Math.random() * 100 + 50);
				addEntity(new SpaceRock(this, direction, position, maxHealth, speed, angularSpeed));
			}
		}
	}

	public void spawnInitialDebris(int amount) {
		for (int i = 0; i < amount; ++i) {
			final Vec2 position = new Vec2((float) (Math.random() * width), (float) (Math.random() * height));
			final float theta = (float) (Math.random() * 2 * Math.PI);
			spawnDebris(position, theta);
		}

	}

	private void spawnInitialEnemies(int amount) {
		for (int i = 0; i < amount; ++i) {
			spawnEnemy();
		}
	}

	private void spawnEnemy() {
		final EnemyShip enemyShip = new EnemyShip(this, selectRandom(Arts.ship2, Arts.ship3, Arts.ship4), (float) (0.1f * Math.random() + 0.1f), 300);

		enemyShip.addGun(selectRandom(new NormalGun(350, this, Arts.bullet2, enemyShip, 3, 3), new NormalGun(100, this, Arts.bullet2, enemyShip, 1, 1)));
		enemyShip.addGun(new RocketLauncher(1500, this, enemyShip, player, true, 2, 1));
		enemyShip.setPosition((float) (Math.random() * width), (float) (Math.random() * height));
		addEntity(enemyShip);
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

		// TODO into own GameEntity?
		debrisSpawnTime += updateContext.deltaT;
		enemySpawnTime += updateContext.deltaT;

		// TODO just spawn outside of player view!!!

		// spawn random debris
		if (debrisSpawnTime > DEBRIS_SPAWN_TIME) {
			debrisSpawnTime = 0;

			final Vec2 spawnPosition = new Vec2(player.getPosition());
			double random = Math.random();
			if (random < 0.25f) {
				// left spawn
				spawnPosition.translate(-updateContext.camera.width / 2 - 100, (float) (Math.random() * updateContext.camera.height - updateContext.camera.height / 2));
				spawnDebris(spawnPosition, (float) (-Math.PI / 2 + Math.random() * Math.PI));
			} else if (random < 0.5) {
				// right spawn
				spawnPosition.translate(updateContext.camera.width / 2 + 100, (float) (Math.random() * updateContext.camera.height - updateContext.camera.height / 2));
				spawnDebris(spawnPosition, (float) (Math.PI / 2 + Math.random() * Math.PI / 2) * (float) Math.signum(Math.random() - 0.5f));
			} else {
				// top spawn
				spawnPosition.translate((float) (updateContext.camera.width * Math.random()) - updateContext.camera.width / 2, -updateContext.camera.height / 2 - 100);
				spawnDebris(spawnPosition, (float) (Math.PI / 2 + Math.random() * Math.PI / 2 * Math.signum(Math.random() - 0.5)));
			}

		}

		if (enemySpawnTime > ENEMY_SPAWN_TIME) {
			enemySpawnTime = 0;
			spawnEnemy();
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

	// TODO use an octtree
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
