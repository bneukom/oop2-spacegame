package ch.neb.test.lightningbolt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// http://drilian.com/2009/02/25/lightning-bolts/
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

		private List<LightningSegment> segments = new ArrayList<>();
		private Vec2 start = new Vec2(10, 300);

		public RenderPanel() {
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(MouseEvent e) {
					segments = generateLightning(Arrays.asList(new LightningSegment(start, new Vec2(e.getX(), e.getY()))), 4, 100);

					repaint();
				}

			});
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
			}

			return generateLightning(newSegments, generations - 1, offset / 2);

		}

		private Vec2 midPoint(Vec2 a, Vec2 b) {
			return new Vec2((a.x + b.x) / 2, (a.y + b.y) / 2);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D graphics = (Graphics2D) g;

			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, getWidth(), getHeight());

			graphics.fillRect((int) (start.x - 4), (int) (start.y - 4), (int) 8, (int) 8);

			Path2D.Float lightnig = new Path2D.Float();
			lightnig.moveTo(start.x, start.y);
			for (LightningSegment seg : segments) {
				lightnig.lineTo(seg.end.x, seg.end.y);

			}
			graphics.setStroke(new BasicStroke(40, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			graphics.setColor(new Color(255, 255, 255, 25));
			graphics.draw(lightnig);

			graphics.setStroke(new BasicStroke(17, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			graphics.setColor(new Color(255, 255, 255, 50));
			graphics.draw(lightnig);

			graphics.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			graphics.setColor(new Color(255, 255, 255, 90));
			graphics.draw(lightnig);

			graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			graphics.setColor(Color.WHITE);
			graphics.draw(lightnig);
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
