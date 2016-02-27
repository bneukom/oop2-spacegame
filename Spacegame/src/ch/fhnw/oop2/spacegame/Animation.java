package ch.fhnw.oop2.spacegame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {
	private List<Frame> frames = new ArrayList<>();
	private Frame currentFrame;
	private int currentFrameIndex = 0;
	private int loops;
	private int frameWidth;
	private int frameHeight;
	private long maxLoops;

	public Animation(final BufferedImage framesImage, int frameWidth, int frameHeight, long frameDuration, long maxLoops) {
		this(framesImage, frameWidth, frameHeight, 0, frameDuration, maxLoops);
	}

	public Animation(final BufferedImage framesImage, int frameWidth, int frameHeight, int frameOffset, long frameDuration, long maxLoops) {
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.maxLoops = maxLoops;
		for (int y = 0; y < framesImage.getHeight() / frameHeight; ++y) {
			for (int x = 0; x < framesImage.getWidth() / frameWidth; ++x) {
				BufferedImage frameImage = framesImage.getSubimage(x * frameWidth + x * frameOffset, y * frameHeight + y * frameOffset, frameWidth, frameHeight);
				frames.add(new Frame(frameImage, frameDuration));
			}
		}

		if (frames.size() == 0) {
			throw new IllegalArgumentException("Empty animations are not valid");
		}

		currentFrame = frames.get(0);
	}

	public BufferedImage getCurrentImage() {
		return currentFrame.image;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void update(long deltaT) {
		currentFrame.currentTime += deltaT;
		if (currentFrame.currentTime > currentFrame.maxTime) {
			currentFrame.currentTime = 0;
			currentFrameIndex++;

			// loop
			if (currentFrameIndex >= frames.size()) {
				currentFrameIndex = 0;
				loops++;

				if (loops >= maxLoops) {
					return;
				}
			}

			currentFrame = frames.get(currentFrameIndex);
		}
	}

	public int getLoops() {
		return loops;
	}

	public static class Frame {
		final BufferedImage image;
		final long maxTime;
		long currentTime;

		public Frame(BufferedImage image, long maxTime) {
			super();
			this.image = image;
			this.maxTime = maxTime;
		}

	}
}
