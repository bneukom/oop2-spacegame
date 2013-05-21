package ch.neb.spacegame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputListener implements MouseListener, MouseMotionListener, KeyListener {

	private Keyboard keyboard;
	private MouseInput mouseInput;

	public InputListener(Keyboard keyboard, MouseInput mouseInput) {
		this.keyboard = keyboard;
		this.mouseInput = mouseInput;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keyboard.keyTyped(KeyEvent.getExtendedKeyCodeForChar(e.getKeyChar()));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyboard.keyDown(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyboard.keyUp(e.getKeyCode());
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
		mouseInput.mouseClicked(e.getButton());
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
