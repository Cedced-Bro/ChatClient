package client.graphics_list;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.graphics.Component;

public class ComponentList {
	
	private int elements;
	
	private ListElement firstElement;
	
	public ComponentList(){
		firstElement = new ListEnd();
		elements = 0;
	}
	
	public int getNumberElements(){
		return elements;
	}
	
	public void addComponent(Component comp, String id){
		ListElement element = new ListKnot(comp, firstElement, id);
		firstElement = element;
		elements++;
	}
	
	public Component getByID(String id){
		return firstElement.getByID(id);
	}
	
	public int getCursor(Point mousePos){
		return firstElement.getCursor(mousePos);
	}
	
	public void resizeAll(int containerWidth, int containerHeight){
		firstElement.resizeAll(containerWidth, containerHeight);
	}
	public void paintGAll(Graphics g){
		firstElement.paintGAll(g);
	}
	public void mouseMoved(Point mousePos){
		firstElement.mouseMoved(mousePos);
	}
	public void mousePressed(Point mousePos){
		firstElement.mousePressed(mousePos);
	}
	public void mouseReleased(Point mousePos){
		firstElement.mouseReleased(mousePos);
	}
	public void keyTyped(KeyEvent k){
		firstElement.keyTyped(k);
	}
}










