package ch.neb.spacegame.world.guns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.Float;

import ch.neb.spacegame.Camera;
import ch.neb.spacegame.CollisionListener;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.Layer;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.World;
import ch.neb.spacegame.world.Mob.DamageType;

public class LaserGun extends Gun {

	private Laser laser;

	public LaserGun(long cooldown, World world, GameEntity owner, CollisionListener collisionListener) {
		super(cooldown, world, owner, collisionListener);

		laser = new Laser(world, owner);
		world.addEntity(laser);

	}

	@Override
	public void update(long deltaT) {
		super.update(deltaT);
		laser.active = false;
	}

	@Override
	public void upgrade(int level) {
		laser.damageTick += 2.5;
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {
		laser.active = true;
		laser.setPosition(position);
		laser.setDirection(direction);
	}

	private static class Laser extends GameEntity {

		private double damageTick = 1.95;

		private boolean active = false;
		private float length = 800;
		private GameEntity owner;

		private Line2D.Float renderLaser = new Line2D.Float();
		private Line2D.Float boundsLaser = new Line2D.Float();
		private long lastDeltaT;

		public Laser(World world, GameEntity owner) {
			super(world);

			this.owner = owner;

			setLayer(Layer.HUD);
		}

		@Override
		public void update(UpdateContext updateContext) {
			super.update(updateContext);

			lastDeltaT = updateContext.deltaT;

			final Camera camera = updateContext.camera;

			final Vec2 end = Vec2.add(getPosition(), new Vec2(direction).multiply(length));

			{
				int x1 = (int) (getPosition().x - camera.getX());
				int y1 = (int) (getPosition().y - camera.getY());
				int x2 = (int) (end.x - camera.getX());
				int y2 = (int) (end.y - camera.getY());
				renderLaser.x1 = x1;
				renderLaser.y1 = y1;
				renderLaser.x2 = x2;
				renderLaser.y2 = y2;
			}

			{
				int x1 = (int) (getPosition().x);
				int y1 = (int) (getPosition().y);
				int x2 = (int) (end.x);
				int y2 = (int) (end.y);

				boundsLaser.x1 = x1;
				boundsLaser.y1 = y1;
				boundsLaser.x2 = x2;
				boundsLaser.y2 = y2;
			}
		}

		@Override
		public void render(Graphics2D graphics, Camera camera) {
			super.render(graphics, camera);

			if (active) {
				graphics.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				graphics.setColor(new Color(37, 28, 51, 210));
				graphics.draw(renderLaser);

				graphics.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				graphics.setColor(new Color(76, 62, 78, 190));
				graphics.draw(renderLaser);

				graphics.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				graphics.setColor(new Color(157, 103, 178, 130));
				graphics.draw(renderLaser);

				graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				graphics.setColor(new Color(226, 177, 243));
				graphics.draw(renderLaser);

			}

		}

		@Override
		public boolean isInView(Camera camera) {
			return true;
		}

		@Override
		public float getWidth() {
			return 0;
		}

		@Override
		public float getHeight() {
			return 0;
		}

		@Override
		public boolean shouldCollide(GameEntity other) {
			return active && other instanceof Mob && other != owner && other != this;
		}

		@Override
		public void onCollide(GameEntity other, World world) {
			super.onCollide(other, world);
			final Mob mob = (Mob) other;
			mob.doDamage(owner, (float) (lastDeltaT * damageTick), DamageType.GUN);
		}

		@Override
		public boolean collidesWith(GameEntity other) {
			final Float otherBounds = other.getBounds();
			return otherBounds.intersectsLine(boundsLaser);
		}
	}

}
