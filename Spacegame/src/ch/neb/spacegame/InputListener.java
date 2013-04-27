package ch.neb.spacegame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputListener implements MouseListener, MouseMotionListener, KeyListener {

	private Keys keys;
	private MouseInput mouseInput;

	public InputListener(Keys keys, MouseInput mouseInput) {
		this.keys = keys;
		this.mouseInput = mouseInput;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys.keyDown(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.keyUp(e.getKeyCode());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseInput.setPosition(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseInput.setPosition(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseInput.mouseDown(e.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseInput.mouseUp(e.getButton());
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
