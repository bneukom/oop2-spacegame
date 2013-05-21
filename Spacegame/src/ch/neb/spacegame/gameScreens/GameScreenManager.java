package ch.neb.spacegame.gameScreens;

import java.awt.Graphics2D;

import ch.neb.spacegame.Audio;
import ch.neb.spacegame.UpdateContext;

public class GameScreenManager {
	private static GameScreenManager instance;
	private GameScreen screen;

	private GameScreenManager() {
	}

	public void render(Graphics2D graphics, UpdateContext updateContext) {
		if (screen != null) screen.render(graphics, updateContext);
	}

	public synchronized void update(UpdateContext updateContext) {
		// pause
		if (updateContext.keyboard.pause.typed) {
			GameScreenManager.getInstance().togglePaused(updateContext);
		}

		if (!updateContext.isPaused) {
			// update
			if (screen != null) screen.update(updateContext);
		} else {
			// consume less cpu
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized void setScreen(GameScreen screen) {
		this.screen = screen;
		
	}

	public GameScreen getScreen() {
		return screen;
	}

	public static GameScreenManager getInstance() {
		if (instance == null) {
			instance = new GameScreenManager();
		}
		return instance;
	}

	public void togglePaused(final UpdateContext updateContext) {
		setPaused(updateContext, !updateContext.isPaused);
	}

	public void setPaused(final UpdateContext updateContext, boolean paused) {
		if (screen == null || !screen.isPauseEnabled()) return;

		updateContext.isPaused = paused;

		if (updateContext.isPaused) {
			Audio.stop();
		}
		else {
			Audio.start();
		}
	}
}
