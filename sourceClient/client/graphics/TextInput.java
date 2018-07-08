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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TextInput extends Component{

	private int roundCorner;
	private String text;
	private Color colorLine;
	private Color colorText;
	private Color colorBackground;
	private Font font;
	private int maxTextSize;
	private int status;
	private int style;
	private static final int SELECTED = 1;
	private static final int DESELECTED = 0;
	public static final int SINGLE_BASELINE = -23489;
	public static final int BOX = -8462;
	public static final int ROUNDBOX = -8562;
	public static final int RECTANGLE = -8462;
	private String actionCommand;
	
	public TextInput(float x, float y, float width, float height, String text, int style) {
		super(x, y, width, height);
		this.text = text;
		this.style = style;
		roundCorner = 10;
		maxTextSize = 16;
		colorBackground = new Color(230, 230, 230, 200);
		colorLine = new Color(0, 0, 0, 150);
		colorText = new Color(0, 0, 0);
		font = new Font(null, Font.PLAIN, 12);
		actionCommand = "txtin";
	}
	
	public void setFont(Font font){this.font = font;}
	public void setColorText(Color colorText){this.colorText = colorText;}
	public void setColorLine(Color colorLine){this.colorLine = colorLine;}
	public void setColorBackground(Color colorBackground){this.colorBackground = colorBackground;}
	public void setText(String text){this.text = text;}
	public void setStyle(int style){this.style = style;}
	public void setRoundCorner(int roundCorner){this.roundCorner = roundCorner;}
	public void setActionCommand(String actionCommand){this.actionCommand = actionCommand;}
	
	public String getText() {return text;}
	
	@Override
	public void paintG(Graphics g) {
		Rectangle pos = getPosition();
		int x = pos.x+border;
		int y = pos.y+border;
		int width = pos.width-2*border;
		int height = pos.height-2*border;
		
		int textSize = font.getSize();
		font = new Font(font.getName(), font.getStyle(), (int)(height*2/3f));
		if(font.getSize() > textSize) font = new Font(font.getName(), font.getStyle(), maxTextSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		int textX = x+2;
		int textY = y+height/2 + metrics.getAscent()/2;
		
		String text = this.text;
		while(metrics.stringWidth(text) > (width-5) && text.length() > 0) text = text.substring(1, text.length());
		
		if(style == TextInput.SINGLE_BASELINE) paintSingleBaseline(g, x, y, width, height, metrics);
		else if(style == TextInput.RECTANGLE) paintRect(g, x, y, width, height, metrics);
		else if(style == TextInput.BOX) paintBox(g, x, y, width, height);
		else if(style == TextInput.ROUNDBOX) paintRoundBox(g, x, y, width, height);
		
		g.setColor(colorText);
		g.drawString(text, textX, textY);
		
		if(status == SELECTED){
			g.setColor(colorLine);
			int textEndX = textX + metrics.stringWidth(text) + 1;
			g.drawLine(textEndX, textY-metrics.getAscent(), textEndX, textY);
		}
	}
	
	private void paintSingleBaseline(Graphics g, int x, int y, int width, int height, FontMetrics metrics){
		g.setColor(colorLine);
		g.drawLine(x, y+height/2+metrics.getAscent()/2+4, x+width, y+height/2+metrics.getAscent()/2+4);
	}
	private void paintRect(Graphics g, int x, int y, int width, int height, FontMetrics metrics){
		g.setColor(colorBackground);
		g.fillRect(x, y+height/2-metrics.getAscent()/2-4, width, metrics.getAscent()+8);
		g.setColor(colorLine);
		g.drawRect(x, y+height/2-metrics.getAscent()/2-4, width, metrics.getAscent()+8);
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
	public int componentSelectedCursor() {
		return Cursor.TEXT_CURSOR;
	}

	public void keyTyped(KeyEvent k){
		char c = k.getKeyChar();
		if(status == DESELECTED) return;
		if((c > 31 && c < 127) || (c > 128 && c < 155)) text = text+k.getKeyChar();
		else if(c == 8 && text.length() > 0) text = text.substring(0, text.length()-1);
		else if(c == 10) onEnter();
	}
	
	public void onEnter(){
		if(listener == null) return;
		ActionEvent event = new ActionEvent(this, Event.ACTION_EVENT, actionCommand);
		listener.actionPerformed(event);
	}
	
	@Override
	public void mousePressed(Point mousePos) {
		if(pointFocus(mousePos)) status = SELECTED;
		else status = DESELECTED;
	}
	
}





