package ch.fhnw.oop2.spacegame;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ch.neb.spacegame.gameScreens.AbstractCamera;
import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.math.Vec2;

public abstract class GameEntity implements Comparable<GameEntity> {

	/**
	 * The spaceGameScreen position of the {@link GameEntity}. Default is (0, 0)
	 */
	protected Vec2 position = new Vec2(0, 0);

	/**
	 * Moving speed of the {@link GameEntity}.
	 */
	protected float speed = 0;

	/**
	 * Normalized direction vector. Default is up (1, 0)
	 */
	protected Vec2 direction = new Vec2(1, 0);

	// TODO change to Shape (not every game entity has a rectangular shape!)
	/**
	 * The bounds of this {@link GameEntity}.
	 */
	public Rectangle2D.Float bounds = new Rectangle2D.Float();

	public static float DEFAULT_SPEED = 0.3f;

	public final GameScreen spaceGameScreen;

	private Layer layer = Layer.DEFAULT;

	public GameEntity(GameScreen spaceGameScreen) {
		this(spaceGameScreen, new Vec2(0, 0), new Vec2(1, 0));
	}

	public void onDestroy() {

	}

	public GameEntity(GameScreen spaceGameScreen, Vec2 position, Vec2 direction, float speed) {
		super();
		this.spaceGameScreen = spaceGameScreen;
		this.position = position;
		this.direction = direction;
		this.speed = speed;
	}

	public GameEntity(GameScreen spaceGameScreen, Vec2 position, Vec2 direction) {
		this(spaceGameScreen, position, direction, 0);
	}

	public GameEntity(GameScreen spaceGameScreen, Vec2 position) {
		this(spaceGameScreen, position, new Vec2(1, 0), 0);
	}

	public GameEntity(GameScreen spaceGameScreen, float speed) {
		this(spaceGameScreen, new Vec2(0, 0), new Vec2(1, 0), speed);
	}

	public void checkCollisions() {

	}

	public void update(UpdateContext updateContext) {
		// update the bounds
		bounds.x = position.x;
		bounds.y = position.y;
		bounds.width = getWidth();
		bounds.height = getHeight();
	}

	public void render(Graphics2D graphics, UpdateContext updateContext) {

	}

	public void onCollide(GameEntity other, GameScreen spaceGameScreen) {

	}

	public boolean shouldCollide(GameEntity other) {
		return false;
	}

	public boolean collidesWith(GameEntity other) {
		return bounds.intersects(other.bounds);
	}

	public boolean isInView(AbstractCamera gameCamera) {
		return gameCamera.isInView(bounds);
	}

	public Rectangle2D.Float getBounds() {
		return bounds;
	}

	public Vec2 getPosition() {
		return position;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public int compareTo(GameEntity o) {
		return layer.compareTo(o.layer);
	}

	public void setDirection(Vec2 d) {
		direction.x = d.x;
		direction.y = d.y;
	}

	public void setPosition(Vec2 p) {
		position.x = p.x;
		position.y = p.y;
	}

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}

	public Vec2 getDirection() {
		return direction;
	}

	public abstract float getWidth();

	public abstract float getHeight();

}
