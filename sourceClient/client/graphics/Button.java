package client.graphics;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Button extends Component{
	
	private int roundCorner;
	private String text;
	private Color colorBackground;
	private Color colorLine;
	private Color colorText;
	private Font font;
	private int maxTextSize;
	private int status;
	private static final int PRESSED = 1;
	private static final int FOCUS = 2;
	private static final int RELEASED = 0;
	private String actionCommand;
	
	
	public Button(float x, float y, float width, float height, String text) {
		super(x, y, width, height);
		this.text = text;
		roundCorner = 10;
		colorBackground = new Color(255, 20, 50, 200);
		colorLine = new Color(0, 0, 0, 150);
		colorText = new Color(0, 0, 0);
		font = new Font(null, Font.PLAIN, 12);
		maxTextSize = 16;
		actionCommand = "btn";
	}
	
	public void setFont(Font font){this.font = font;}
	public void setColorText(Color colorText){this.colorText = colorText;}
	public void setColorLine(Color colorLine){this.colorLine = colorLine;}
	public void setColorBackground(Color colorBackground){this.colorBackground = colorBackground;}
	public void setText(String text){this.text = text;}
	public void setRoundCorner(int roundCorner){this.roundCorner = roundCorner;}
	public void setActionCommand(String actionCommand){this.actionCommand = actionCommand;}
	
	@Override
	public void paintG(Graphics g) {
		int x = (int) (this.x*containerWidth + border);
		int y = (int) (this.y*containerHeight + border);
		int width = (int) (this.width*containerWidth - 2*border);
		int height = (int) (this.height*containerHeight - 2*border);
		if(status == RELEASED);
		else if(status == FOCUS){
			width = width+1;
			height = height+1;
		}else if(status == PRESSED){
			width = width+1;
			height = height+1;
			x = x-1;
			y = y-1;
		}
		g.setColor(colorBackground);
		g.fillRoundRect(x, y, width, height, roundCorner, roundCorner);
		g.setColor(colorLine);
		g.drawRoundRect(x, y, width, height, roundCorner, roundCorner);
		
		if(status == PRESSED){
			g.setColor(new Color(0, 0, 0, 20));
			g.fillRoundRect(x, y, width, height, roundCorner, roundCorner);
		}
		
		int textSize = font.getSize();
		font = new Font(font.getName(), font.getStyle(), (int)(height*2/3f));
		if(font.getSize() > textSize) font = new Font(font.getName(), font.getStyle(), maxTextSize);
		g.setFont(font);
		g.setColor(colorText);
		FontMetrics metrics = g.getFontMetrics();
		
		String text = this.text;
		while(metrics.stringWidth(text) > (width-5) && text.length() > 0) text = text.substring(1, text.length());
		
		int textX = (int) (x + width/2 - 0.5*metrics.stringWidth(text));
		int textY = (int) (y + height/2 + 0.5*metrics.getAscent());
		g.drawString(text, textX, textY);
	}
	
	@Override
	public void mouseMoved(Point mousePos) {
		if(pointFocus(mousePos)){
			if(status == RELEASED) status = FOCUS;
		}else status = RELEASED;
	}
	
	@Override
	public void mousePressed(Point mousePos) {
		if(pointFocus(mousePos)) status = PRESSED;
	}
	
	@Override
	public void mouseReleased(Point mousePos) {
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
	
	public void onClick(){
		if(listener == null) return;
		ActionEvent event = new ActionEvent(this, Event.ACTION_EVENT, actionCommand);
		listener.actionPerformed(event);
	}
}
