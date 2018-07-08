package client.graphics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.graphics_list.ComponentList;

public class Panel extends Component{

	private ComponentList components;
	private int roundCorner;
	private int style;
	private Color colorBackground;
	private Color colorLine;
	public static final int BOX = -8462;
	public static final int ROUNDBOX = -8562;
	public static final int DEFAULT = -65983;
	
	public Panel(float x, float y, float width, float height, int style) {
		super(x, y, width, height);
		this.style = style;
		colorBackground = new Color(230, 230, 230, 200);
		colorLine = new Color(0, 0, 0, 150);
		roundCorner = 10;
		components = new ComponentList();
	}
	
	public void setColorBackground(Color colorBackground){this.colorBackground = colorBackground;}
	public void setColorLine(Color colorLine){this.colorLine = colorLine;}
	public void setRoundCorner(int roundCorner){this.roundCorner = roundCorner;}
	
	public int getNumberElements(){
		return components.getNumberElements();
	}
	
	@Override
	public void paintG(Graphics g) {
		super.paintG(g);
		int offsetX = (int)(x*containerWidth);
		int offsetY = (int)(y*containerHeight);
		g.translate(offsetX, offsetY);

		int x = border;
		int y = border;
		int width = (int) (this.width*containerWidth - 2*border);
		int height = (int) (this.height*containerHeight - 2*border);
		
		if(style == TextView.BOX) paintBox(g, x, y, width, height);
		else if(style == TextView.ROUNDBOX) paintRoundBox(g, x, y, width, height);
		
		components.paintGAll(g);
		g.translate(-1*offsetX, -1*offsetY);
	}
	private void paintBox(Graphics g, int x, int y, int width, int height){
		g.setColor(colorBackground);
		g.fillRect(x, y, width, height);
		g.setColor(colorLine);
		g.drawRect(x, y, width, height);
	}
	private void paintRoundBox(Graphics g, int x, int y, int width, int height){
		g.setColor(colorBackground);
		g.fillRoundRect(x, y, width, height, roundCorner, roundCorner);
		g.setColor(colorLine);
		g.drawRoundRect(x, y, width, height, roundCorner, roundCorner);
	}
	
	@Override
	public void resize(int containerWidth, int containerHeight){
		super.resize(containerWidth, containerHeight);
		int widthPixels = (int)(width*this.containerWidth);
		int heightPixels = (int)(height*this.containerHeight);
		components.resizeAll(widthPixels, heightPixels);
	}

	@Override
	public void mouseMoved(Point mousePos){
		components.mouseMoved(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
	}

	@Override
	public void mousePressed(Point mousePos){
		components.mousePressed(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
	}

	@Override
	public void mouseReleased(Point mousePos){
		components.mouseReleased(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
	}
	
	@Override
	public int getCursor(Point mousePos) {
		int cursor = components.getCursor(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
		if(cursor != Cursor.DEFAULT_CURSOR) return cursor;
		else if(pointFocus(mousePos)) return componentSelectedCursor();
		else return Cursor.DEFAULT_CURSOR;
	}
	
	@Override
	public void keyTyped(KeyEvent k){
		components.keyTyped(k);
	}
	
	public void addComponent(Component comp, String id){
		components.addComponent(comp, id);
	}
}







