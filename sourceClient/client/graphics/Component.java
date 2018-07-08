package client.graphics;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public abstract class Component {

	protected ActionListener listener;
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	protected int containerWidth;
	protected int containerHeight;
	protected int border;
	
	protected Component(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		border = 2;
	}
	
	public void setBorder(int border){this.border = border;}
	
	public Rectangle getPosition(){
		return new Rectangle((int) (x*containerWidth), (int) (y*containerHeight), (int) (width*containerWidth), (int) (height*containerHeight));
	}
	
	public void resize(int containerWidth, int containerHeight){
		this.containerWidth = containerWidth;
		this.containerHeight = containerHeight;
	}
	
	public void paintG(Graphics g){}
	
	public int componentSelectedCursor(){
		return Cursor.DEFAULT_CURSOR;
	}
	
	public int getCursor(Point mousePos){
		if(pointFocus(mousePos)) return componentSelectedCursor();
		else return Cursor.DEFAULT_CURSOR;
	}
	
	public void addActionListener(ActionListener e) {
		listener = e;
	}
	
	public void mouseMoved(Point mousePos){}
	public void mousePressed(Point mousePos){}
	public void mouseReleased(Point mousePos){}
	public void keyTyped(KeyEvent k){}
	
	public boolean pointFocus(Point p){
		Rectangle pos = getPosition();
		if(p.x > pos.x && p.x < (pos.x+pos.width) && p.y > pos.y && p.y < (pos.y+pos.height)) return true;
		return false;
	}
}





