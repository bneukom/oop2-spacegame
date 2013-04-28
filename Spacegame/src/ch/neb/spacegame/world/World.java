package ch.neb.spacegame.world;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import ch.neb.spacegame.Arts;
import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.DamageIndicatorEntity;
import ch.neb.spacegame.DrawExperienceGameEntity;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.Sprite;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.spacedebris.SmallSpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceRock;

//TODO somehow limit enemy objects
public class World {

	public int width;
	public int height;

	private Player player;

	private List<GameEntity> gameEntities = new ArrayList<>();
	private List<CollisionListener> collisionListeners = new ArrayList<>();

	private Queue<GameEntity> removeGameObejctQueue = new LinkedList<>();
	private Queue<GameEntity> addGameObjectQueue = new LinkedList<>();

	private List<Sprite> stars = new ArrayList<>();

	private long debrisSpawnTime = 0;
	private static final long DEBRIS_SPAWN_TIME = 50;

	private final Font pixel72;

	public World(int width, int height) {
		super();
		this.width = width;
		this.height = height;

		pixel72 = Arts.getFont(72);

		createPlayer();
		createStars(2500, 2000, 100);

		spawnInitialDebris(200);

		addEntity(new DamageIndicatorEntity(this));
		addEntity(new DrawExperienceGameEntity(this));
	}

	public void spawnDebris(final Vec2 position, float theta) {
		final Vec2 direction = new Vec2((float) Math.cos(theta), (float) Math.sin(theta));

		final float speed = (float) (Math.random() * 0.05f + 0.04f);

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

	private void createPlayer() {
		player = new Player(this);
		addEntity(player);
	}

	public Player getPlayer() {
		return player;
	}

	private void createStars(int bigStars, int smallStars, int numberOfStarsystems) {
		for (int i = 0; i < bigStars; ++i) {
			stars.add(new Sprite(Arts.star1, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
		for (int i = 0; i < smallStars; ++i) {
			stars.add(new Sprite(Arts.star2, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
		}
		for (int i = 0; i < numberOfStarsystems; ++i) {
			stars.add(new Sprite(Arts.starsystem, new Vec2((float) (Math.random() * width), (float) (Math.random() * height))));
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

		// TODO just spawn outside of player view!!!
		// spawn random debris
		debrisSpawnTime += updateContext.deltaT;
		if (debrisSpawnTime > DEBRIS_SPAWN_TIME) {
			debrisSpawnTime = 0;
			if (Math.random() < 0.5f) {
				// spawn from left
				spawnDebris(new Vec2(-20f, (float) (Math.random() * height)), (float) (Math.random() * Math.PI));
			} else {
				// spawn from top
				spawnDebris(new Vec2((float) (Math.random() * width), -20f), (float) (Math.random() * Math.PI));
			}

		}
	}

	private synchronized void addEntities() {
		while (!addGameObjectQueue.isEmpty()) {
			final GameEntity poll = addGameObjectQueue.poll();
			gameEntities.add(poll);
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

		for (GameEntity object : gameEntities) {
			if (object.isInView(camera)) {
				object.render(graphics, camera);
			}
		}

		if (!player.isAlive()) {
			graphics.setFont(pixel72);
			graphics.setColor(Color.WHITE);
			graphics.drawString("Game Over!", 200, 300);
		}
	}

	public void checkCollisions() {

		for (int i = 0; i < gameEntities.size(); ++i) {
			for (int j = i; j < gameEntities.size(); ++j) {
				final GameEntity a = gameEntities.get(i);
				final GameEntity b = gameEntities.get(j);

				if (a.shouldCollide(b) && b.shouldCollide(a) && a.collidesWith(b)) {
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

	public void addEntity(GameEntity entity) {
		addGameObjectQueue.add(entity);
	}

	public void removeEntity(GameEntity entity) {
		removeGameObejctQueue.add(entity);
	}

	public void addCollisionListener(CollisionListener collisionListener) {
		this.collisionListeners.add(collisionListener);
	}
}
