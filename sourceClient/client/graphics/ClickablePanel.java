package client.graphics;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import client.graphics_list.ComponentList;

public class ClickablePanel extends Component{

	private ComponentList components;
	private int roundCorner;
	private int style;
	private Color colorBackground;
	private Color colorLine;
	private int status;
	private String id;
	public static final int BOX = -8462;
	public static final int ROUNDBOX = -8562;
	private static final int PRESSED = 1;
	private static final int FOCUS = 2;
	private static final int RELEASED = 0;
	private String actionCommand;
	
	public ClickablePanel(float x, float y, float width, float height, int style, String id) {
		super(x, y, width, height);
		this.style = style;
		this.id = id;
		colorBackground = new Color(230, 230, 230, 200);
		colorLine = new Color(0, 0, 0, 150);
		roundCorner = 10;
		actionCommand = "clickpnl";
		components = new ComponentList();
	}
	
	public void setColorBackground(Color colorBackground){this.colorBackground = colorBackground;}
	public void setColorLine(Color colorLine){this.colorLine = colorLine;}
	public void setRoundCorner(int roundCorner){this.roundCorner = roundCorner;}
	public void setActionCommand(String actionCommand){this.actionCommand = actionCommand;}
	
	public int getNumberElements(){
		return components.getNumberElements();
	}
	
	@Override
	public void paintG(Graphics g) {
		super.paintG(g);
		int offsetX = (int)(x*containerWidth);
		int offsetY = (int)(y*containerHeight);
		
		if(status == PRESSED){
			offsetX -= 1;
			offsetY -= 1;
		}
		
		g.translate(offsetX, offsetY);

		int x = border;
		int y = border;
		int width = (int) (this.width*containerWidth - 2*border);
		int height = (int) (this.height*containerHeight - 2*border);
		
		if(status == RELEASED);
		else if(status == FOCUS){
			width = width+1;
			height = height+1;
		}else if(status == PRESSED){
			width = width+1;
			height = height+1;
		}
		
		if(style == TextView.BOX) paintBox(g, x, y, width, height);
		else if(style == TextView.ROUNDBOX) paintRoundBox(g, x, y, width, height);
		
		components.paintGAll(g);
		
		if(status == PRESSED){
			g.setColor(new Color(0, 0, 0, 20));
			g.fillRoundRect(x, y, width, height, roundCorner, roundCorner);
		}
		
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
		if(pointFocus(mousePos)){
			if(status == RELEASED) status = FOCUS;
		}else status = RELEASED;
	}

	@Override
	public void mousePressed(Point mousePos){
		components.mousePressed(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
		if(pointFocus(mousePos)) status = PRESSED;
	}

	@Override
	public void mouseReleased(Point mousePos){
		components.mouseReleased(new Point(mousePos.x-(int)(x*containerWidth), mousePos.y-(int)(y*containerHeight)));
		if(status == PRESSED){
			status = FOCUS;
			onClick();
		}
		if(!pointFocus(mousePos)) status = RELEASED;
	}
	
	@Override
	public int componentSelectedCursor() {
		return Cursor.HAND_CURSOR;
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
	
	public void onClick(){
		if(listener == null) return;
		ActionEvent event = new ActionEvent(this, Event.ACTION_EVENT, actionCommand);
		listener.actionPerformed(event);
	}
	
}







