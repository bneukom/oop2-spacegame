package ch.neb.spacegame;

import static ch.neb.spacegame.math.Random.selectRandom;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.enemies.EnemyShip;
import ch.neb.spacegame.world.guns.NormalGun;
import ch.neb.spacegame.world.guns.RocketLauncher;
import ch.neb.spacegame.world.spacedebris.SmallSpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceRock;

public class EnemySpawner extends GameEntity {

	private long debrisSpawnTime = 0;
	private long enemySpawnTime = 0;
	private static final long DEBRIS_SPAWN_TIME = 125;
	private static final long ENEMY_SPAWN_TIME = 2000;

	public EnemySpawner(World world) {
		super(world);

		spawnInitialDebris(200);
		spawnInitialEnemies(5);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		debrisSpawnTime += updateContext.deltaT;
		enemySpawnTime += updateContext.deltaT;

		// TODO THIS IS ONLY GOOD IF THE PLAYER STANDS STILL
		// spawn random debris which point towards the player
		if (debrisSpawnTime > DEBRIS_SPAWN_TIME) {
			debrisSpawnTime = 0;

			final Vec2 spawnPosition = new Vec2(0, 0);
			final Vec2 direction;
			final SpaceDebris debris = createDebris();

			final double random = Math.random();
			final Camera camera = updateContext.camera;
			if (random < 0.25) {
				// left
				spawnPosition.translate(camera.getX() - debris.getWidth(), (float) (camera.getY() + Math.random() * camera.height));
			} else if (random < 0.5) {
				// right
				spawnPosition.translate(camera.getX() + camera.width + debris.getWidth(), (float) (camera.getY() + Math.random() * camera.height));
			} else if (random < 0.75) {
				// top
				spawnPosition.translate((float) (camera.getX() + Math.random() * camera.width), camera.getY() - debris.getHeight());
			} else {
				// bottom
				spawnPosition.translate((float) (camera.getX() + Math.random() * camera.width), camera.getY() + camera.height + debris.getHeight());
			}

			final int spawnOffset = 550;
			direction = Vec2.subtract(world.getPlayer().getPosition(), spawnPosition)
					.translate((float) (-spawnOffset + 2 * spawnOffset * Math.random()), (float) (-spawnOffset + 2 * spawnOffset * Math.random()))
					.normalize();

			debris.setPosition(spawnPosition);
			debris.setMovementDirection(direction);

			world.addEntity(debris);

		}

		// TODO other more random debris spawn

		if (enemySpawnTime > ENEMY_SPAWN_TIME) {
			enemySpawnTime = 0;
			spawnEnemy();
		}
	}

	public SpaceDebris createDebris() {

		final float speed = (float) (Math.random() * 0.075f + 0.095f);
		float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

		// random turn direction
		angularSpeed *= Math.random() > 0.5 ? -1 : 1;

		if (Math.random() < 0.2f) {
			final float maxHealth = (float) (Math.random() * 10 + 20);
			return new SmallSpaceDebris(world, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
		} else {
			final float maxHealth = (float) (Math.random() * 100 + 50);
			return new SpaceRock(world, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
		}
	}

	// TODO duplicate with create debris...
	public void spawnDebris(final Vec2 position, float theta) {
		final Vec2 direction = new Vec2((float) Math.cos(theta), (float) Math.sin(theta));

		final float speed = (float) (Math.random() * 0.06f + 0.075f);

		float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

		// random turn direction
		angularSpeed *= Math.random() > 0.5 ? -1 : 1;

		if (Math.random() < 0.2f) {
			final float maxHealth = (float) (Math.random() * 10 + 20);
			world.addEntity(new SmallSpaceDebris(world, direction, position, maxHealth, speed, angularSpeed));
		} else {
			final float maxHealth = (float) (Math.random() * 100 + 50);
			world.addEntity(new SpaceRock(world, direction, position, maxHealth, speed, angularSpeed));
		}
	}

	public void spawnInitialDebris(int amount) {
		for (int i = 0; i < amount; ++i) {
			final Vec2 position = new Vec2((float) (Math.random() * world.width), (float) (Math.random() * world.height));
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
		final EnemyShip enemyShip = new EnemyShip(world, selectRandom(Arts.ship2, Arts.ship3, Arts.ship4), (float) (0.1f * Math.random() + 0.1f), 300);

		enemyShip.addGun(selectRandom(new NormalGun(350, world, Arts.bullet2, enemyShip, 3, 3), new NormalGun(100, world, Arts.bullet2, enemyShip, 1, 1)));
		enemyShip.addGun(new RocketLauncher(1500, world, enemyShip, world.getPlayer(), true, 2, 1));
		enemyShip.setPosition((float) (Math.random() * world.width), (float) (Math.random() * world.height));
		world.addEntity(enemyShip);
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
