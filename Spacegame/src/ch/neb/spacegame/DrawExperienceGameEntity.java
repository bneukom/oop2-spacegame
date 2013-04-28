package ch.neb.spacegame;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.neb.spacegame.world.World;

public class DrawExperienceGameEntity extends GameEntity {

	private final Color color = new Color(91, 40, 91, 150);

	private int XP_BAR_WIDTH = 800 - 15;

	public DrawExperienceGameEntity(World world) {
		super(world);

		setLayer(Layer.OVERLAY);
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
		return false;
	}

	@Override
	public void render(Graphics2D graphics, Camera camera) {
		super.render(graphics, camera);

		graphics.setColor(color);
		graphics.fillRect(15, 570, (int) ((world.getPlayer().getExperience() / world.getPlayer().getNextLevelExperience()) * XP_BAR_WIDTH), 10);
	}

	@Override
	public boolean isInView(Camera camera) {
		return true;
	}

}
