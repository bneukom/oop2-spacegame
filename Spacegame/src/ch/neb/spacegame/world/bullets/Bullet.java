package ch.neb.spacegame.world.bullets;

import java.awt.image.BufferedImage;

import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.DrawableGameEntity;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.World;

// TODO create Weapon!
public class Bullet extends DrawableGameEntity {

	protected float age;
	public final float damage;

	public static final float MAX_LIFE = 2000;
	private CollisionListener collisionListener;

	public Bullet(World world, CollisionListener collisionListener, BufferedImage image, Vec2 direction, Vec2 position, float speed, float damage) {
		super(world, image, speed);
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
			world.removeEntity(this);
		}

		final Vec2 offset = Vec2.multiply(direction, speed * updateContext.deltaT);
		position.add(offset);

	}

	@Override
	public void onCollide(GameEntity other, World world) {
		super.onCollide(other, world);

		Mob hitMob = (Mob) other;
		hitMob.doDamage(damage);

		if (collisionListener != null)
			collisionListener.onCollide(this, other);

		world.removeEntity(this);

	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return other instanceof Mob;
	}

}
