package client.graphics_list;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.graphics.Component;

public interface ListElement {
	public Component getByID(String id);
	public void resizeAll(int containerWidth, int containerHeight);
	public void paintGAll(Graphics g);
	public int getCursor(Point mousePos);
	public void mouseMoved(Point mousePos);
	public void mousePressed(Point mousePos);
	public void mouseReleased(Point mousePos);
	public void keyTyped(KeyEvent k);
}







