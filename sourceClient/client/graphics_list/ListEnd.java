package client.graphics_list;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.graphics.Component;

public class ListEnd implements ListElement{
	public Component getByID(String id){
		return null;
	}
	public int getCursor(Point mousePos) {
		return Cursor.DEFAULT_CURSOR;
	}
	public void resizeAll(int containerWidth, int containerHeight){}
	public void paintGAll(Graphics g){}
	public void mouseMoved(Point mousePos){}
	public void mousePressed(Point mousePos){}
	public void mouseReleased(Point mousePos){}
	public void keyTyped(KeyEvent k){}
}
