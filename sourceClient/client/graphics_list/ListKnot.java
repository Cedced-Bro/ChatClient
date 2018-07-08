package client.graphics_list;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.graphics.Component;

public class ListKnot implements ListElement{

	private ListElement nextElement;
	private Component component;
	private String id;
	
	public ListKnot(Component component, ListElement nextElement, String id){
		this.component = component;
		this.nextElement = nextElement;
		this.id = id;
	}
	
	public Component getByID(String id){
		if(id.equals(this.id)) return component;
		else return nextElement.getByID(id);
	}
	
	public int getCursor(Point mousePos){
		int cursor = nextElement.getCursor(mousePos);
		int thisCursor = component.getCursor(mousePos);
		if(cursor == Cursor.DEFAULT_CURSOR) return thisCursor;
		else return cursor;
	}
	
	public void resizeAll(int containerWidth, int containerHeight){
		component.resize(containerWidth, containerHeight);
		nextElement.resizeAll(containerWidth, containerHeight);
	}
	public void paintGAll(Graphics g){
		component.paintG(g);
		nextElement.paintGAll(g);
	}
	public void mouseMoved(Point mousePos){
		component.mouseMoved(mousePos);
		nextElement.mouseMoved(mousePos);
	}
	public void mousePressed(Point mousePos){
		component.mousePressed(mousePos);
		nextElement.mousePressed(mousePos);
	}
	public void mouseReleased(Point mousePos){
		component.mouseReleased(mousePos);
		nextElement.mouseReleased(mousePos);
	}
	public void keyTyped(KeyEvent k){
		component.keyTyped(k);
		nextElement.keyTyped(k);
	}
}
