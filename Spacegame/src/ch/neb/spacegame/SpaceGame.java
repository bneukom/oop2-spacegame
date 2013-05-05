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
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

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
	private Keys keys;
	private MouseInput mouseInput;
	private InputListener inputListener;

	private World world;

	public SpaceGame(int resolutionX, int resolutionY, boolean fullScreen) {
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
		this.fullScreen = fullScreen;
		this.keys = new Keys();
		this.mouseInput = new MouseInput();
		this.inputListener = new InputListener(keys, mouseInput);
		this.world = new World(5000, 5000);
		this.camera = new Camera(resolutionX, resolutionY, world);

		setResizable(false);
		setBounds(200, 200, resolutionX, resolutionY + DECORATOR_HEIGHT);
		setTitle("Software Renderer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint(true);

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
	}

	public void run() {
		// Get graphics configuration...
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		if (fullScreen) {
			// Change to full screen
			setUndecorated(true);
			gd.setFullScreenWindow(this);
		}

		if (gd.isDisplayChangeSupported()) {
			gd.setDisplayMode(new DisplayMode(resolutionX, resolutionY, 32, 60));
		}

		// Create BackBuffer...
		createBufferStrategy(2);
		BufferStrategy buffer = getBufferStrategy();

		// Create off-screen drawing surface
		BufferedImage bi = gc.createCompatibleImage(resolutionX, resolutionY);

		// Objects needed for rendering...
		Graphics graphics = null;
		Graphics2D g2d = null;

		// offset rendering used when in window mode
		int yOffset = fullScreen ? 0 : 25;

		long curTime = System.nanoTime();
		long lastTime = curTime;
		long deltaT = 0;

		final UpdateContext updateContext = new UpdateContext();
		updateContext.keys = keys;
		updateContext.mouseInput = mouseInput;
		updateContext.camera = camera;

		// run until user presses exit
		while (true) {
			try {
				// clear back buffer...
				g2d = bi.createGraphics();
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, resolutionX, resolutionY);
				g2d.setFont(font);

				// delta time in milli seconds
				updateContext.deltaT = (long) (deltaT / 1e6);

				// graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// update
				world.update(updateContext);

				world.checkCollisions();

				// update the camera
				updateCameraPosition();

				// render
				world.render(g2d, camera);

				mouseInput.step();

				// exit
				if (keys.exit.isDown) {
					System.exit(0);
				}

				// count Frames per second...
				lastTime = curTime;
				curTime = System.nanoTime();
				deltaT = curTime - lastTime;

				// display frames per second...
				g2d.setColor(Color.WHITE);
				g2d.setFont(font);
				g2d.drawString(String.format("fps: %s", Math.round(1e9 / (deltaT + 1))), 5, 10);

				graphics = buffer.getDrawGraphics();
				graphics.drawImage(bi, 0, yOffset, null);

				if (!buffer.contentsLost())
					buffer.show();

				Thread.yield();

			} finally {
				// release resources
				if (graphics != null)
					graphics.dispose();
				if (g2d != null)
					g2d.dispose();
			}
		}
	}

	private void updateCameraPosition() {

		camera.setPosition(world.getPlayer().position);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				final SpaceGame spacegame = new SpaceGame(800, 600, true);
				final Thread gmaeThread = new Thread(new Runnable() {

					@Override
					public void run() {
						spacegame.run();
					}
				});
				gmaeThread.start();
			}
		});

	}
}
