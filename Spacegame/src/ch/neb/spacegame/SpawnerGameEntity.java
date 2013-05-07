package ch.neb.spacegame;

import static ch.neb.spacegame.math.Random.selectRandom;

import java.util.ArrayList;
import java.util.List;

import ch.neb.spacegame.math.Random;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.enemies.EnemyShip;
import ch.neb.spacegame.world.guns.NormalGun;
import ch.neb.spacegame.world.guns.RocketLauncher;
import ch.neb.spacegame.world.spacedebris.SmallSpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceDebris;
import ch.neb.spacegame.world.spacedebris.SpaceRock;

public class SpawnerGameEntity extends GameEntity {

	private static final long DEBRIS_SPAWN_TIME = 250;
	private static final long ENEMY_SPAWN_TIME = 4000;
	private static final long DEFAULT_ENEMY_HEALTH = 300;
	private List<Spawner> spawners = new ArrayList<>();

	public SpawnerGameEntity(World world) {
		super(world);

		spawnInitialDebris(200);
		spawnInitialEnemies(5);

		spawners.add(new DebrisSpawner(world, DEBRIS_SPAWN_TIME));
		spawners.add(new EnemySpawner(world, ENEMY_SPAWN_TIME));
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		for (Spawner spawner : spawners) {
			spawner.update(updateContext.deltaT, updateContext.camera);
		}
	}

	public void spawnInitialDebris(int amount) {
		for (int i = 0; i < amount; ++i) {
			final Vec2 position = new Vec2((float) (Math.random() * world.width), (float) (Math.random() * world.height));
			final float theta = (float) (Math.random() * 2 * Math.PI);
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

	}

	private EnemyShip createEnemy(float health) {
		final EnemyShip enemyShip = new EnemyShip(world, selectRandom(Arts.ship2, Arts.ship3, Arts.ship4), (float) (0.1f * Math.random() + 0.1f), health);

		enemyShip.addGun(selectRandom(new NormalGun(350, world, Arts.bullet2, enemyShip, 3, 3), new NormalGun(100, world, Arts.bullet2, enemyShip, 1, 1)));
		enemyShip.addGun(new RocketLauncher(1500, world, enemyShip, world.getPlayer(), true, 2, 1));

		return enemyShip;
	}

	private void spawnInitialEnemies(int amount) {
		for (int i = 0; i < amount; ++i) {
			EnemyShip enemy = createEnemy(DEFAULT_ENEMY_HEALTH);
			enemy.setPosition((float) (Math.random() * world.width), (float) (Math.random() * world.height));
			world.addEntity(enemy);
		}
	}

	@Override
	public boolean isInView(Camera camera) {
		return false;
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}

	private static abstract class Spawner {

		protected World world;
		private long cooldown;
		private long currentCooldown;

		public Spawner(World world, long cooldown) {
			this.world = world;
			this.cooldown = cooldown;
		}

		public void update(long deltaT, Camera camera) {
			currentCooldown += deltaT;
			if (currentCooldown > getCooldown(cooldown)) {
				spawn(world, camera);
				currentCooldown = 0;
			}

		}

		public long getCooldown(long originalCooldown) {
			return originalCooldown;
		}

		public abstract void spawn(World world, Camera camera);

	}

	private static class DebrisSpawner extends Spawner {

		public DebrisSpawner(World world, long cooldown) {
			super(world, cooldown);
		}

		@Override
		public long getCooldown(long originalCooldown) {
			return originalCooldown - (15 * world.getPlayer().getLevel());
		}

		@Override
		public void spawn(World world, Camera camera) {

			final Vec2 spawnPosition = new Vec2(0, 0);
			final Vec2 direction;
			final SpaceDebris debris = createDebris();

			final double random = Math.random();
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

		public SpaceDebris createDebris() {

			final float speed = (float) (Math.random() * 0.075f + 0.095f + 0.01 * world.getPlayer().getLevel());
			float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

			// random turn direction
			angularSpeed *= Math.random() > 0.5 ? -1 : 1;

			if (Math.random() < 0.25f) {
				final float maxHealth = (float) ((Math.random() * 10 + 20) * ((world.getPlayer().getLevel() + 1) * 0.75f));
				return new SmallSpaceDebris(world, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			} else {
				final float maxHealth = (float) ((Math.random() * 100 + 50) * ((world.getPlayer().getLevel() + 1) * 0.75f));
				return new SpaceRock(world, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			}
		}

	}

	private class EnemySpawner extends Spawner {

		public EnemySpawner(World world, long cooldown) {
			super(world, cooldown);
		}

		@Override
		public void spawn(World world, Camera camera) {
			final EnemyShip enemy = createEnemy(DEFAULT_ENEMY_HEALTH + (world.getPlayer().getLevel() * 80));
			final Player player = world.getPlayer();
			final Vec2 position = new Vec2(player.getPosition()).translate((float) ((Math.random() * 500f + 300f) * Random.randomSignum()),
					(float) ((Math.random() * 500f + 300f) * Random.randomSignum()));
			enemy.setPosition(position);
			world.addEntity(enemy);
		}
	}
}
