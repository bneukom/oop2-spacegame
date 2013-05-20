package ch.neb.spacegame;

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
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.neb.spacegame.world.World;

public class SpaceGame extends JFrame {

	private static final int DECORATOR_HEIGHT = 25;
	private int resolutionX;
	private int resolutionY;

	private boolean fullScreen;

	private Font font;

	private Camera camera;
	private Keyboard keyboard;
	private MouseInput mouseInput;
	private InputListener inputListener;

	private World world;

	int yOffset = 0;

	public SpaceGame(int resolutionX, int resolutionY, boolean fullScreen) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		this.fullScreen = fullScreen;
		this.keyboard = new Keyboard();
		this.mouseInput = new MouseInput();
		this.inputListener = new InputListener(keyboard, mouseInput);
		this.world = new World();
		this.camera = new Camera(resolutionX, resolutionY, world);

		yOffset = fullScreen ? 0 : 25;

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
		updateContext.camera = camera;
		updateContext.isPaused = false;

		boolean showFps = false;

		// run until user presses exit
		while (true) {
			try {
				synchronized (keyboard) {
					// clear back buffer...
					g2d = bi.createGraphics();
					g2d.setColor(Color.BLACK);
					g2d.fillRect(0, 0, resolutionX, resolutionY);
					g2d.setFont(font);

					// delta time in milli seconds
					updateContext.deltaT = (long) (deltaT / 1e6);

					// exit
					if (keyboard.exit.isDown) {
						System.exit(0);
					}

					if (keyboard.showFps.typed) {
						showFps = !showFps;
					}

					if (keyboard.sound.typed) {
						Audio.toggleEnabled();
					}

					if (keyboard.fullscreen.typed) {
						fullScreen = !fullScreen;
						
						setPaused(updateContext, true);
						
						// change fullscreen mode
						dispose();
						try {
							SwingUtilities.invokeAndWait(new Runnable() {

								@Override
								public void run() {
									setUndecorated(fullScreen);
									setVisible(true);
									gd.setFullScreenWindow(!fullScreen ? null : SpaceGame.this);

									if (gd.isDisplayChangeSupported()) {
										gd.setDisplayMode(new DisplayMode(resolutionX, resolutionY, 32, 60));
									}

									// create backbuffer
									createBufferStrategy(2);

									yOffset = fullScreen ? 0 : 25;
								}

							});
						} catch (InvocationTargetException | InterruptedException e) {
							e.printStackTrace();
						}

					}

					// pause
					if (keyboard.pause.typed) {
						togglePaused(updateContext);
					}

					if (!updateContext.isPaused) {
						// update
						world.update(updateContext);
					} else {
						// consume less cpu
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// check if any collisions happened
					world.checkCollisions();

					// update the camera
					updateCameraPosition();

					// render
					world.render(g2d, updateContext);

					mouseInput.update();
					keyboard.update();
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
	
	private void togglePaused(final UpdateContext updateContext) {
		setPaused(updateContext, !updateContext.isPaused);
	}

	private void setPaused(final UpdateContext updateContext, boolean paused) {
		updateContext.isPaused = paused;

		if (updateContext.isPaused) {
			Audio.stop();
		}
		else {
			Audio.start();
		}
	}

	private void updateCameraPosition() {

		camera.setPosition(world.getPlayer().position);
	}

	public static void main(String[] args) {
		final boolean fullscreen;
		if (args.length > 0) {
			fullscreen = Boolean.parseBoolean(args[0]);
		} else {
			fullscreen = false;
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SpaceGame spacegame = new SpaceGame(800, 600, fullscreen);
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
