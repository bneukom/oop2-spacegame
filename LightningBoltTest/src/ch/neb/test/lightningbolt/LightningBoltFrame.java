package ch.neb.test.lightningbolt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// http://drilian.com/2009/02/25/lightning-bolts/
// TODO http://stackoverflow.com/questions/12541081/adding-filter-to-image-in-swing !!!
public class LightningBoltFrame extends JFrame {

	public LightningBoltFrame() {
		setTitle("Lightning Test");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RenderPanel panel = new RenderPanel();
		setContentPane(panel);

		setVisible(true);
		

	}

	private class RenderPanel extends JPanel {

		private Vec2 start = new Vec2(10, 300);

		private List<Lightning> lightnings = new ArrayList<>();

		final float[] kernel = { 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
				1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
				1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f };

		final BufferedImageOp blurFilter = new ConvolveOp(new Kernel(3, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
		final BufferedImage image;
		private Graphics2D imageGraphics;

		public RenderPanel() {

			image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
			imageGraphics = (Graphics2D) image.getGraphics();

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					super.mouseReleased(e);
					synchronized (RenderPanel.this) {
						lightnings.add(new Lightning(new Vec2(start), new Vec2(e.getX(), e.getY()), 0, false));
						lightnings.add(new Lightning(new Vec2(start),
								new Vec2(e.getX(), e.getY()).translate((float) (-10 + Math.random() * 20), (float) (-30 + Math.random() * 60)), 0, true));
						lightnings.add(new Lightning(new Vec2(start),
								new Vec2(e.getX(), e.getY()).translate((float) (-10 + Math.random() * 20), (float) (-30 + Math.random() * 60)), 0, true));
						// lightnings.add(new Lightning(new Vec2(start), new Vec2(e.getX(), e.getY()), 75));
					}
				}
			});

			Thread renderer = new Thread(new Runnable() {

				private long lastTime;


				@Override
				public void run() {

					while (true) {
						synchronized (RenderPanel.this) {
							long deltaT = System.currentTimeMillis() - lastTime;
							lastTime = System.currentTimeMillis();
							Iterator<Lightning> iterator = lightnings.iterator();
							while (iterator.hasNext()) {
								Lightning next = iterator.next();
								if (next.time <= 0) {
									iterator.remove();
								} else {
									next.update(deltaT);
								}
							}
						}

						repaint();
					}
				}
			});

			renderer.start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D graphics = (Graphics2D) g;

			// render to image
			imageGraphics.setColor(Color.BLACK);
			imageGraphics.fillRect(0, 0, getWidth(), getHeight());

			synchronized (RenderPanel.this) {
				for (Lightning lightning : lightnings) {
					lightning.render(imageGraphics);
				}
			}

			// apply gaussian filter
			long start = System.currentTimeMillis();
			graphics.drawImage(image, blurFilter, 0, 0);
			System.out.println(System.currentTimeMillis() - start);
			// graphics.drawImage(image, 0, 0, null);
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new LightningBoltFrame();
			}
		});

	}

	private static class Lightning {
		private long time = 300;

		private List<LightningSegment> segments = new ArrayList<>();

		private Vec2 start;

		private long delay;

		private boolean weak;

		public Lightning(Vec2 start, Vec2 end, long delay, boolean weak) {
			this.start = start;
			this.delay = delay;
			this.weak = weak;

			this.segments = generateLightning(Arrays.asList(new LightningSegment(start, end)), 6, 150);
		}

		public void update(long deltaT) {

			if (delay > 0) {
				delay -= deltaT;
				delay = Math.max(0, delay);
			}

			time -= deltaT;
			time = Math.max(0, time);

		}

		public void render(Graphics2D graphics) {
			if (delay > 0)
				return;

			Path2D.Float lightnig = new Path2D.Float();
			lightnig.moveTo(start.x, start.y);
			for (LightningSegment seg : segments) {
				lightnig.moveTo(seg.start.x, seg.start.y);
				lightnig.lineTo(seg.end.x, seg.end.y);

			}
			if (!weak) {
				drawLightning(graphics, lightnig, 60, 19);
				drawLightning(graphics, lightnig, 19, 50);
				drawLightning(graphics, lightnig, 11, 110);
				drawLightning(graphics, lightnig, 2, 255, false);
			} else {
				drawLightning(graphics, lightnig, 2, 255, false);
			}

		}

		private void drawLightning(Graphics2D graphics, Path2D.Float lightning, int size, int alpha, boolean ignoreAlphaChange) {
			graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int changedAlpha = ignoreAlphaChange ? alpha : (int) (alpha * time / 300);
			graphics.setColor(new Color(255, 220, 150, changedAlpha));
			graphics.draw(lightning);
		}

		private void drawLightning(Graphics2D graphics, Path2D.Float lightning, int size, int alpha) {
			drawLightning(graphics, lightning, size, alpha, false);
		}

		public List<LightningSegment> generateLightning(List<LightningSegment> segments, int generations, int offset) {
			if (generations <= 0)
				return segments;

			final List<LightningSegment> newSegments = new ArrayList<>();
			for (LightningSegment lightningSegment : segments) {
				final Vec2 start = lightningSegment.start;
				final Vec2 end = lightningSegment.end;
				final Vec2 midPoint = midPoint(end, start);

				final Vec2 lineNormal = new Vec2(-(end.y - start.y), end.x - start.x).normalize();

				float rand = (float) (-offset + Math.random() * 2 * offset);
				midPoint.add(lineNormal.multiply(rand));

				newSegments.add(new LightningSegment(new Vec2(start), new Vec2(midPoint)));
				newSegments.add(new LightningSegment(new Vec2(midPoint), new Vec2(end)));

				// add a offshoot
				if (Math.random() < 0.35f) {
					final Vec2 direction = Vec2.subtract(midPoint, start);

					final float theta = (float) Math.toRadians(Math.random() * 10);
					direction.x = (float) (direction.x * Math.cos(theta) - direction.y * Math.sin(theta));
					direction.y = (float) (direction.x * Math.sin(theta) + direction.y * Math.cos(theta));

					final Vec2 splitEnd = new Vec2(direction).multiply(0.9f).add(midPoint);

					newSegments.add(new LightningSegment(midPoint, splitEnd));
				}
			}

			return generateLightning(newSegments, generations - 1, offset / 2);

		}

		private Vec2 midPoint(Vec2 a, Vec2 b) {
			return new Vec2((a.x + b.x) / 2, (a.y + b.y) / 2);
		}

	}

	private static class LightningSegment {
		public Vec2 start;
		public Vec2 end;

		public LightningSegment(Vec2 start, Vec2 end) {
			super();
			this.start = start;
			this.end = end;
		}

	}
}
