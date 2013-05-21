package ch.neb.spacegame.gameScreens.game.bullets;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.game.DrawableGameEntity;
import ch.neb.spacegame.gameScreens.game.Mob;
import ch.neb.spacegame.gameScreens.game.Mob.DamageType;
import ch.neb.spacegame.math.Vec2;

public class Bullet extends DrawableGameEntity {

	protected float age;
	public final float damage;

	public static final float MAX_LIFE = 2000;
	private CollisionListener collisionListener;
	protected GameEntity owner;

	public Bullet(GameScreen spaceGameScreen, GameEntity owner, CollisionListener collisionListener, BufferedImage image, Vec2 direction, Vec2 position, float speed, float damage) {
		super(spaceGameScreen, image, speed);
		this.owner = owner;
		this.collisionListener = collisionListener;

		this.direction = direction;
		this.damage = damage;
		this.position = position;
	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		age += updateContext.deltaT;

		if (age > MAX_LIFE) {
			spaceGameScreen.removeEntity(this);
		}

		final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
		position.add(offset);

	}
	

	@Override
	public void onCollide(GameEntity other, GameScreen spaceGameScreen) {
		super.onCollide(other, spaceGameScreen);

		Mob hitMob = (Mob) other;
		hitMob.doDamage(owner, damage, DamageType.GUN);

		if (collisionListener != null)
			collisionListener.onCollide(this, other);

		spaceGameScreen.removeEntity(this);

	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Mob && other != owner;
	}

	public GameEntity getOwner() {
		return owner;
	}

}
