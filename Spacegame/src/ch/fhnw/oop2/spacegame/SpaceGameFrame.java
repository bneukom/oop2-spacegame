package ch.fhnw.oop2.spacegame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.neb.spacegame.gameScreens.GameScreen;
import ch.neb.spacegame.gameScreens.GameScreenManager;
import ch.neb.spacegame.gameScreens.game.GameCamera;
import ch.neb.spacegame.gameScreens.game.SpaceGameScreen;
import ch.neb.spacegame.gameScreens.menu.MainMenuScreen;

public class SpaceGameFrame extends JFrame {

	private static final int DECORATOR_HEIGHT = 25;
	private int resolutionX;
	private int resolutionY;

	private boolean fullScreen;

	private Font font;

	private GameCamera gameCamera;
	private Keyboard keyboard;
	private MouseInput mouseInput;
	private InputListener inputListener;

	private GameScreenManager gameScreenManager = GameScreenManager.getInstance();

	private SpaceGameScreen spaceGameScreen;
	private MainMenuScreen mainMenuScreen;

	// the decorator when in windowed mode
	int yOffset = 0;

	public SpaceGameFrame(int resolutionX, int resolutionY, boolean fullScreen) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		this.fullScreen = fullScreen;
		this.keyboard = new Keyboard();
		this.mouseInput = new MouseInput();
		this.inputListener = new InputListener(keyboard, mouseInput);
		this.gameCamera = new GameCamera(resolutionX, resolutionY);

		this.spaceGameScreen = new SpaceGameScreen(resolutionX, resolutionY);
		this.mainMenuScreen = new MainMenuScreen(resolutionX, resolutionY, null);

		gameScreenManager.setScreen(mainMenuScreen);

		yOffset = fullScreen ? 0 : 25;
		mouseInput.setYOffset(yOffset);

		setResizable(false);
		setBounds(200, 200, resolutionX, resolutionY + DECORATOR_HEIGHT);
		setTitle("Space Shooter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint(true);

		setIconImage(Arts.star1);

		font = Arts.getFont(16);

		if (fullScreen) {
			setUndecorated(true);
		} else {
			setVisible(true);
		}

		addKeyListener(inputListener);
		addMouseListener(inputListener);
		addMouseMotionListener(inputListener);

		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Cursor cursor = toolkit.createCustomCursor(Arts.mouse, new Point(0, 0), "img");
		setCursor(cursor);

		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				Audio.stop();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				Audio.start();
			}
		});
	}

	public void run() {
		// Get graphics configuration...
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice gd = ge.getDefaultScreenDevice();
		final GraphicsConfiguration gc = gd.getDefaultConfiguration();

		if (fullScreen) {
			// Change to full screen
			setUndecorated(true);
			gd.setFullScreenWindow(this);
		}

		if (gd.isDisplayChangeSupported()) {
			gd.setDisplayMode(new DisplayMode(resolutionX, resolutionY, 32, 60));
		}

		// create backbuffer
		createBufferStrategy(2);
		BufferStrategy buffer = getBufferStrategy();

		// Offscreen rendering
		BufferedImage bi = gc.createCompatibleImage(resolutionX, resolutionY);

		// Objects needed for rendering...
		Graphics graphics = null;
		Graphics2D g2d = null;

		// offset rendering used when in window mode

		long curTime = System.nanoTime();
		long lastTime = curTime;
		long deltaT = 0;

		final UpdateContext updateContext = new UpdateContext();
		updateContext.keyboard = keyboard;
		updateContext.mouseInput = mouseInput;
		updateContext.gameCamera = gameCamera;
		updateContext.isPaused = false;

		boolean showFps = false;

		// run until user presses exit
		while (true) {
			try {
				synchronized (keyboard) {
					synchronized (mouseInput) {
						// clear back buffer...
						g2d = bi.createGraphics();
						g2d.setColor(Color.BLACK);
						g2d.fillRect(0, 0, resolutionX, resolutionY);
						g2d.setFont(font);

						// delta time in milli seconds
						updateContext.deltaT = (long) (deltaT / 1e6);

						updateContext.gameCamera = gameScreenManager.getScreen().getCamera();

						// TODO should open menu
						// exit
						if (keyboard.showFps.typed) {
							showFps = !showFps;
						}

						if (keyboard.sound.typed) {
							Audio.toggleEnabled();
						}

						if (keyboard.fullscreen.typed) {
							fullScreen = !fullScreen;

							// setPaused(updateContext, true);

							// change fullscreen mode
							dispose();
							try {
								SwingUtilities.invokeAndWait(new Runnable() {

									@Override
									public void run() {
										setUndecorated(fullScreen);
										setVisible(true);
										gd.setFullScreenWindow(!fullScreen ? null : SpaceGameFrame.this);

										if (gd.isDisplayChangeSupported()) {
											gd.setDisplayMode(new DisplayMode(resolutionX, resolutionY, 32, 60));
										}

										// create backbuffer
										createBufferStrategy(2);

										yOffset = fullScreen ? 0 : 25;
										mouseInput.setYOffset(yOffset);
									}

								});
							} catch (InvocationTargetException | InterruptedException e) {
								e.printStackTrace();
							}

						}
						// update
						gameScreenManager.update(updateContext);

						// render
						gameScreenManager.render(g2d, updateContext);

						mouseInput.update();
						keyboard.update();
					}
				}

				// count Frames per second...
				lastTime = curTime;
				curTime = System.nanoTime();
				deltaT = curTime - lastTime;

				if (showFps) {
					g2d.setColor(Color.WHITE);
					g2d.setFont(font);
					g2d.drawString(String.format("fps: %s", Math.round(1e9 / (deltaT + 1))), 5, 10);
				}

				graphics = buffer.getDrawGraphics();
				graphics.drawImage(bi, 0, yOffset, null);

				if (!buffer.contentsLost())
					buffer.show();

				Thread.yield();

			} finally {
				if (graphics != null)
					graphics.dispose();
				if (g2d != null)
					g2d.dispose();
			}
		}
	}


	public static void main(String[] args) {
		final boolean fullscreen;
		if (args.length > 0) {
			fullscreen = Boolean.parseBoolean(args[0]);
		} else {
			fullscreen = true;
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SpaceGameFrame spacegame = new SpaceGameFrame(800, 600, fullscreen);
				final Thread gameThread = new Thread(new Runnable() {

					@Override
					public void run() {
						spacegame.run();
					}
				});
				gameThread.start();
			}
		});

	}
}
