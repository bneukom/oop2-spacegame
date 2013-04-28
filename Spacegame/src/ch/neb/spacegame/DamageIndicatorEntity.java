package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.World;

public class DamageIndicatorEntity extends GameEntity {

	private long hitDelay = 0;

	public DamageIndicatorEntity(final World world) {
		super(world);

		setLayer(Layer.OVERLAY);

		world.addCollisionListener(new CollisionListener() {

			@Override
			public void onCollide(GameEntity a, GameEntity b) {
				if (a == world.getPlayer() || b == world.getPlayer()) {
					// exactly the time the player is immune to new hits
					hitDelay = Player.COLLIDE_COOLDOWN;
				}
			}
		});
	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		if (hitDelay > 0) {
			graphics.setColor(new Color(1, 0, 0, 0.5f * hitDelay / Player.COLLIDE_COOLDOWN));
			graphics.fillRect(0, 0, 800, 600);
		}

	}

	@Override
	public void update(UpdateContext updateContext) {
		super.update(updateContext);

		hitDelay -= updateContext.deltaT;
		hitDelay = Math.max(hitDelay, 0);
	}

	@Override
	public float getWidth() {
		return 800;
	}

	@Override
	public float getHeight() {
		return 600;
	}

	@Override
	public boolean isInView(Camera camera) {
		return true;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return false;
	}

}
