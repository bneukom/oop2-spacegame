package ch.neb.spacegame.world.guns;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.neb.spacegame.Camera;
import ch.neb.spacegame.GameEntity;
import ch.neb.spacegame.Layer;
import ch.neb.spacegame.UpdateContext;
import ch.neb.spacegame.math.Vec2;
import ch.neb.spacegame.world.Mob;
import ch.neb.spacegame.world.Mob.DamageType;
import ch.neb.spacegame.world.World;

public class LightningGun extends Gun {

	private static final int MAX_DISTANCE = 500;
	private float halfConeAngle = (float) Math.toRadians(25);
	private float damage;
	private Mob target;
	private int offspring = 1;
	private ArrayList<Mob> hitTargetsCache = new ArrayList<>();

	public LightningGun(long cooldown, World world, GameEntity owner, Mob target, float damage) {
		super(cooldown, world, owner, null);
		this.target = target;
		this.damage = damage;
	}

	@Override
	protected void doShoot(Vec2 position, Vec2 direction) {
		Mob targetToHit = null;

		if (target == null) {

			// if no target has been set, search a target in a given cone where the rocket is flying
			float shortestDistance = Float.MAX_VALUE;
			for (GameEntity entity : world.getGameEntities()) {

				if (entity instanceof Mob && entity != owner) {
					final Mob mob = (Mob) entity;
					final Vec2 mobPosition = mob.getPosition();
					final Vec2 rocketToMobVector = Vec2.subtract(mobPosition, position);
					float angle = (float) Math.acos(rocketToMobVector.dot(direction) / (rocketToMobVector.getLength() * direction.getLength()));

					if (angle < halfConeAngle) {
						float distance = Vec2.distance(mobPosition, position);
						if (distance < shortestDistance && distance < MAX_DISTANCE) {
							shortestDistance = distance;
							targetToHit = mob;
						}
					}
				}
			}
		} else {
			// try to hit the predefined target
			targetToHit = target;
		}

		if (targetToHit != null) {
			spawnLightning(position, targetToHit, offspring, damage, hitTargetsCache);
			hitTargetsCache.clear();

		}

	}

	private void spawnLightning(Vec2 from, Mob targetToHit, int offsprings, float damage, List<Mob> hitTargets) {
		world.addEntity(new Lightning(world, new Vec2(from), new Vec2(targetToHit.getPosition()).translate(targetToHit.getWidth() / 2, targetToHit.getHeight() / 2), 0,
				false));
		world.addEntity(new Lightning(world, new Vec2(from), new Vec2(targetToHit.getPosition()).translate(targetToHit.getWidth() / 2, targetToHit.getHeight() / 2), 0,
				true));
		// world.addEntity(new Lightning(world, new Vec2(from), new Vec2(targetToHit.getPosition()).translate(targetToHit.getWidth() / 2, targetToHit.getHeight() / 2), 0,
		// true));

		targetToHit.doDamage(owner, damage, DamageType.GUN);

		OFFSPRING: if (offsprings > 0) {

			for (int target = 0; target < offsprings; ++target) {
				for (GameEntity entity : world.getGameEntities()) {

					if (entity instanceof Mob && entity != owner) {
						final Mob mob = (Mob) entity;
						final Vec2 mobPosition = mob.getPosition();
						final float distance = Vec2.distance(targetToHit.getPosition(), mobPosition);

						if (distance < 200 && mob != targetToHit && !hitTargets.contains(mob)) {
							final Vec2 start = new Vec2(targetToHit.getPosition()).translate(mob.getWidth() / 2, mob.getHeight() / 2);
							hitTargets.add(mob);
							spawnLightning(start, mob, offsprings - 2, damage, hitTargets); // -2 because otherwise there'll be lots of lightnings really fast!
							break OFFSPRING;
						}
					}
				}
			}
		}
	}

	private static class Lightning extends GameEntity {
		private long time = 300;

		private List<LightningSegment> segments = new ArrayList<>();

		private Vec2 start;

		private long delay;

		private boolean weak;

		public Lightning(World world, Vec2 start, Vec2 end, long delay, boolean weak) {
			super(world);
			this.start = start;
			this.delay = delay;
			this.weak = weak;

			this.segments = generateLightning(Arrays.asList(new LightningSegment(start, end)), 4, 60);

			setLayer(Layer.OVERLAY);
		}

		@Override
		public void update(UpdateContext updateContext) {
			super.update(updateContext);

			if (delay > 0) {
				delay -= updateContext.deltaT;
				delay = Math.max(0, delay);
			}

			time -= updateContext.deltaT;
			time = Math.max(0, time);

			if (time <= 0) {
				world.removeEntity(this);
			}
		}

		@Override
		public boolean isInView(Camera camera) {
			return true;
		}

		@Override
		public void render(Graphics2D graphics, Camera camera) {
			super.render(graphics, camera);

			Path2D.Float lightnig = new Path2D.Float();
			lightnig.moveTo(start.x - camera.getX(), start.y - camera.getY());
			for (LightningSegment seg : segments) {
				lightnig.moveTo(seg.start.x - camera.getX(), seg.start.y - camera.getY());
				lightnig.lineTo(seg.end.x - camera.getX(), seg.end.y - camera.getY());

			}
			if (!weak) {
				drawLightning(graphics, lightnig, 60, 19);
				drawLightning(graphics, lightnig, 19, 50);
				drawLightning(graphics, lightnig, 11, 90);
				drawLightning(graphics, lightnig, 7, 110);
				drawLightning(graphics, lightnig, 2, 255, false);
			} else {
				drawLightning(graphics, lightnig, 2, 255, false);
			}
		}

		private void drawLightning(Graphics2D graphics, Path2D.Float lightning, int size, int alpha, boolean ignoreAlphaChange) {
			graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int changedAlpha = ignoreAlphaChange ? alpha : (int) (alpha * time / 300);
			graphics.setColor(new Color(255, 220, 150, changedAlpha));
			graphics.draw(lightning);
		}

		private void drawLightning(Graphics2D graphics, Path2D.Float lightning, int size, int alpha) {
			drawLightning(graphics, lightning, size, alpha, false);
		}

		public List<LightningSegment> generateLightning(List<LightningSegment> segments, int generations, int offset) {
			if (generations <= 0)
				return segments;

			final List<LightningSegment> newSegments = new ArrayList<>();
			for (LightningSegment lightningSegment : segments) {
				final Vec2 start = lightningSegment.start;
				final Vec2 end = lightningSegment.end;
				final Vec2 midPoint = midPoint(end, start);

				final Vec2 lineNormal = new Vec2(-(end.y - start.y), end.x - start.x).normalize();

				float rand = (float) (-offset + Math.random() * 2 * offset);
				midPoint.add(lineNormal.multiply(rand));

				newSegments.add(new LightningSegment(new Vec2(start), new Vec2(midPoint)));
				newSegments.add(new LightningSegment(new Vec2(midPoint), new Vec2(end)));

				// add a offshoot
				if (Math.random() < 0.15f) {
					final Vec2 direction = Vec2.subtract(midPoint, start);

					final float theta = (float) Math.toRadians(Math.random() * 10);
					direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
					direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

					final Vec2 splitEnd = new Vec2(direction).multiply(0.7f).add(midPoint);

					newSegments.add(new LightningSegment(midPoint, splitEnd));
				}
			}

			return generateLightning(newSegments, generations - 1, offset / 2);

		}

		private Vec2 midPoint(Vec2 a, Vec2 b) {
			return new Vec2((a.x + b.x) / 2, (a.y + b.y) / 2);
		}

		@Override
		public boolean shouldCollide(GameEntity other) {
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

	}

	private static class LightningSegment {
		public Vec2 start;
		public Vec2 end;

		public LightningSegment(Vec2 start, Vec2 end) {
			super();
			this.start = start;
			this.end = end;
		}

	}

	@Override
	public void upgrade(int level) {
		offspring += 3;
	}

}
