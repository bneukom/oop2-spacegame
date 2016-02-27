package ch.fhnw.oop2.spacegame;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.game.Player;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;

public class DamageIndicatorEntity extends GameEntity {

	private long hitDelay = 0;

	public DamageIndicatorEntity(final SpaceGameScreen spaceGameScreen) {
		super(spaceGameScreen);

		setLayer(Layer.HUD);

		spaceGameScreen.getPlayer().addDamageListener(new DamageListener() {

			@Override
			public void damageRecieved(GameEntity attackee, float amount) {
				hitDelay = Player.COLLIDE_COOLDOWN;
			}
		});

	}

	@Override
	public void render(Graphics2D graphics, UpdateContext updateContext) {
		super.render(graphics, updateContext);

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
	public boolean isInView(AbstractCamera gameCamera) {
		return true;
	}

	@Override
	public boolean shouldCollide(GameEntity other) {
		return false;
	}

}
