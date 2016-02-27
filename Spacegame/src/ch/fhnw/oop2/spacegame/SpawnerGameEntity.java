package ch.fhnw.oop2.spacegame;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.oop2.gameScreens.AbstractCamera;
import ch.fhnw.oop2.gameScreens.GameScreen;
import ch.fhnw.oop2.gameScreens.game.Player;
import ch.fhnw.oop2.gameScreens.game.enemies.EnemyShip;
import ch.fhnw.oop2.gameScreens.game.guns.NormalGun;
import ch.fhnw.oop2.gameScreens.game.guns.RocketLauncher;
import ch.fhnw.oop2.gameScreens.game.spacedebris.SmallSpaceDebris;
import ch.fhnw.oop2.gameScreens.game.spacedebris.SpaceDebris;
import ch.fhnw.oop2.gameScreens.game.spacedebris.SpaceRock;
import ch.fhnw.oop2.spacegame.math.Random;
import ch.fhnw.oop2.spacegame.math.Vec2;

public class SpawnerGameEntity extends GameEntity {

	private static final long DEFAULT_ENEMY_HEALTH = 300;
	private List<Spawner> spawners = new ArrayList<>();

	public SpawnerGameEntity(GameScreen spaceGameScreen) {
		super(spaceGameScreen);

	}

	public void addSpawner(Spawner spawner) {
		spawners.add(spawner);
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		for (Spawner spawner : spawners) {
			spawner.update(updateContext.deltaT, updateContext.gameCamera);
		}
	}

	@Override
	public boolean isInView(AbstractCamera gameCamera) {
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

	public static abstract class Spawner {

		protected GameScreen spaceGameScreen;
		private long cooldown;
		private long currentCooldown;

		public Spawner(GameScreen spaceGameScreen, long cooldown) {
			this.spaceGameScreen = spaceGameScreen;
			this.cooldown = cooldown;
		}

		public void update(long deltaT, AbstractCamera gameCamera) {
			currentCooldown += deltaT;
			if (currentCooldown > getCooldown(cooldown)) {
				spawn(spaceGameScreen, gameCamera);
				currentCooldown = 0;
			}

		}

		public long getCooldown(long originalCooldown) {
			return originalCooldown;
		}

		public abstract void spawn(GameScreen spaceGameScreen, AbstractCamera gameCamera);

	}

	public static abstract class GameSpawner extends Spawner {

		protected Player player;

		public GameSpawner(GameScreen spaceGameScreen, Player player, long cooldown) {
			super(spaceGameScreen, cooldown);
			this.player = player;
		}

	}

	public static class DebrisSpawner extends GameSpawner {

		public DebrisSpawner(GameScreen spaceGameScreen, Player player, long cooldown) {
			super(spaceGameScreen, player, cooldown);
		}

		@Override
		public long getCooldown(long originalCooldown) {
			return originalCooldown - (15 * player.getLevel());
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
			direction = Vec2.subtract(player.getPosition(), spawnPosition)
					.translate((float) (-spawnOffset + 2 * spawnOffset * Math.random()), (float) (-spawnOffset + 2 * spawnOffset * Math.random()))
					.normalize();

			debris.setPosition(spawnPosition);
			debris.setMovementDirection(direction);

			spaceGameScreen.addEntity(debris);

		}

		public SpaceDebris createDebris() {

			final float speed = (float) (Math.random() * 0.075f + 0.095f + 0.008 * player.getLevel());
			float angularSpeed = (float) (Math.random() * 0.001f + 0.001f);

			// random turn direction
			angularSpeed *= Math.random() > 0.5 ? -1 : 1;

			if (Math.random() < 0.25f) {
				final float maxHealth = (float) ((Math.random() * 10 + 20) * ((player.getLevel() + 1) * 0.45f));
				return new SmallSpaceDebris(spaceGameScreen, player, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			} else {
				final float maxHealth = (float) ((Math.random() * 100 + 50) * ((player.getLevel() + 1) * 0.65f));
				return new SpaceRock(spaceGameScreen, player, new Vec2(1, 0), new Vec2(0, 0), maxHealth, speed, angularSpeed);
			}
		}

	}

	public static class EnemySpawner extends GameSpawner {

		public EnemySpawner(GameScreen spaceGameScreen, Player player, long cooldown) {
			super(spaceGameScreen, player, cooldown);
		}

		private EnemyShip createEnemy(float health) {
			final EnemyShip enemyShip = new EnemyShip(spaceGameScreen, player, Random.selectRandom(Arts.ship2, Arts.ship3, Arts.ship4), (float) (0.1f * Math.random() + 0.1f),
					health);

			enemyShip.addGun(Random.selectRandom(new NormalGun(850, spaceGameScreen, Arts.bullet2, enemyShip, 3, 2), new NormalGun(800, spaceGameScreen, Arts.bullet2, enemyShip,
					1, 2)));
			enemyShip.addGun(new RocketLauncher(1750, spaceGameScreen, enemyShip, player, true, 2, 1));

			return enemyShip;
		}

		@Override
		public void spawn(GameScreen spaceGameScreen, AbstractCamera gameCamera) {
			// TODO sometimes spawn multiple enemies (so to make it a bit burstier)...
			final EnemyShip enemy = createEnemy(DEFAULT_ENEMY_HEALTH + (player.getLevel() * 60));
			final Vec2 position = new Vec2(player.getPosition()).translate((float) ((Math.random() * 500f + 300f) * Random.randomSignum()),
					(float) ((Math.random() * 500f + 300f) * Random.randomSignum()));
			enemy.setPosition(position);
			spaceGameScreen.addEntity(enemy);
		}
	}
}
