package client.graphics;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import client.graphics_list.ComponentList;

public abstract class Pane extends JPanel{
	
	protected ComponentList components;
	protected int width;
	protected int height;
	
	protected Pane() {
		components = new ComponentList();
		
		setFocusable(true);
		
		setupListeners();
	}
	
	public void setupListeners(){
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e){
				Pane.this.resize();
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				Pane.this.mousePressed();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				Pane.this.mouseReleased();
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				Pane.this.mouseMoved();
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent k) {
				super.keyTyped(k);
				Pane.this.KeyTyped(k);
			}
		});
	}
	
	public ComponentList getPaneComponents(){return components;}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		components.paintGAll(g);
	}
	
	public void resize(){
		int width = getWidth();
		int height = getHeight();
		this.width = width;
		this.height = height;
		components.resizeAll(width, height);
		this.repaint();
	}
	
	public void mouseMoved(){
		Point mousePos = getMousePosition();
		if(mousePos == null) return;
		components.mouseMoved(mousePos);
		this.setCursor(new Cursor(components.getCursor(mousePos)));
		this.repaint();
	}
	
	public void mousePressed(){
		Point mousePos = getMousePosition();
		if(mousePos == null) return;
		components.mousePressed(mousePos);
		this.repaint();
	}
	
	public void mouseReleased(){
		Point mousePos = getMousePosition();
		if(mousePos == null) return;
		components.mouseReleased(mousePos);
		this.repaint();
	}
	
	public void KeyTyped(KeyEvent k){
		components.keyTyped(k);
		this.repaint();
	}
}









