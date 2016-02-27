package ch.neb.spacegame.gameScreens;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.fhnw.oop2.spacegame.GameEntity;
import ch.fhnw.oop2.spacegame.UpdateContext;

public abstract class GameScreen {
	protected List<GameEntity> gameEntities = new ArrayList<>();

	protected Queue<GameEntity> removeGameObejctQueue = new LinkedList<>();
	protected Queue<GameEntity> addGameObjectQueue = new LinkedList<>();

	protected int resolutionX;
	protected int resolutionY;

	public GameScreen(int resolutionX, int resolutionY) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
	}

	protected synchronized void addEntities() {
		while (!addGameObjectQueue.isEmpty()) {
			final GameEntity poll = addGameObjectQueue.poll();
			gameEntities.add(poll);
		}

		Collections.sort(gameEntities);
	}

	public void render(Graphics2D graphics, UpdateContext updateContext) {
		// render all objects
		synchronized (this) {
			for (GameEntity object : gameEntities) {
				if (object.isInView(updateContext.gameCamera)) {
					object.render(graphics, updateContext);
				}
			}
		}
	}

	public boolean isPauseEnabled() {
		return true;
	}

	public List<GameEntity> getGameEntities() {
		return gameEntities;
	}

	public void update(UpdateContext updateContext) {
		// remove/add game objects
		removeEntities();
		addEntities();

		// update all game objects of this spaceGameScreen
		synchronized (this) {
			for (GameEntity object : gameEntities) {
				object.update(updateContext);
			}
		}

	}

	protected synchronized void removeEntities() {
		while (!removeGameObejctQueue.isEmpty()) {
			final GameEntity entity = removeGameObejctQueue.poll();
			entity.onDestroy();
			gameEntities.remove(entity);
		}
	}

	public synchronized void addEntity(GameEntity entity) {
		addGameObjectQueue.add(entity);
	}

	public synchronized void removeEntity(GameEntity entity) {
		removeGameObejctQueue.add(entity);
	}

	public int getResolutionX() {
		return resolutionX;
	}

	public int getResolutionY() {
		return resolutionY;
	}

	public abstract AbstractCamera getCamera();

	// public abstract Player getPlayer();
}
