package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.neb.spacegame.world.Player;
import ch.neb.spacegame.world.World;

public class DamageIndicatorEntity extends GameEntity {

	private long hitDelay = 0;

	public DamageIndicatorEntity(final World world) {
		super(world);

		setLayer(Layer.HUD);

		world.getPlayer().addDamageListener(new DamageListener() {

			@Override
			public void damageRecieved(GameEntity attackee, float amount) {
				hitDelay = Player.COLLIDE_COOLDOWN;
			}
		});

	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		if (hitDelay > 0) {
			graphics.setColor(new Color(1, 0, 0, 0.4f * hitDelay / (Player.COLLIDE_COOLDOWN * 2)));
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
