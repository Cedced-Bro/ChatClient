package client.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class TextView extends Component{

	private int roundCorner;
	private String text;
	private Color colorLine;
	private Color colorText;
	private Color colorBackground;
	private Font font;
	private int maxTextSize;
	private int alignment;
	private int style;
	public static final int ALIGNMENT_LEFT = 23445;
	public static final int ALIGNMENT_RIGHT = 54378;
	public static final int ALIGNMENT_MIDDLE = 12778;
	public static final int SINGLE_BASELINE = -23489;
	public static final int BASE_TOPLINE = -29869;
	public static final int BOX = -8462;
	public static final int ROUNDBOX = -8562;
	public static final int RECTANGLE = -8462;
	public static final int DEFAULT = -65983;
	
	public TextView(float x, float y, float width, float height, String text, int style) {
		super(x, y, width, height);
		this.text = text;
		this.alignment = ALIGNMENT_MIDDLE;
		this.style = style;
		colorBackground = new Color(230, 230, 230, 200);
		colorLine = new Color(0, 0, 0, 150);
		colorText = new Color(0, 0, 0);
		font = new Font(null, Font.PLAIN, 12);
		maxTextSize = 16;
		roundCorner = 10;
	}
	
	public void setFont(Font font){this.font = font;}
	public void setColorText(Color colorText){this.colorText = colorText;}
	public void setColorLine(Color colorLine){this.colorLine = colorLine;}
	public void setColorBackground(Color colorBackground){this.colorBackground = colorBackground;}
	public void setText(String text){this.text = text;}
	public void setRoundCorner(int roundCorner){this.roundCorner = roundCorner;}
	public void setAlignment(int alignment){this.alignment = alignment;}
	
	@Override
	public void paintG(Graphics g) {
		int x = (int) (this.x*containerWidth + border);
		int y = (int) (this.y*containerHeight + border);
		int width = (int) (this.width*containerWidth - 2*border);
		int height = (int) (this.height*containerHeight - 2*border);
		
		int textSize = font.getSize();
		font = new Font(font.getName(), font.getStyle(), (int)(height*2/3f));
		if(font.getSize() > textSize) font = new Font(font.getName(), font.getStyle(), maxTextSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		
		String text = this.text;
		while(metrics.stringWidth(text) > (width-5) && text.length() > 0) text = text.substring(1, text.length());
		
		if(style == TextView.SINGLE_BASELINE) paintSingleBaseline(g, x, y, width, height, metrics, text);
		else if(style == TextView.BASE_TOPLINE) paintBaseTopline(g, x, y, width, height, metrics, text);
		else if(style == TextView.RECTANGLE) paintRect(g, x, y, width, height, metrics);
		else if(style == TextView.BOX) paintBox(g, x, y, width, height);
		else if(style == TextView.ROUNDBOX) paintRoundBox(g, x, y, width, height);
		
		int textY = y+height/2+metrics.getAscent()/2;
		int textX = x;
		if(alignment == TextView.ALIGNMENT_LEFT) textX = x+2;
		else if(alignment == TextView.ALIGNMENT_MIDDLE) textX = x+width/2-metrics.stringWidth(text)/2;
		else if(alignment == TextView.ALIGNMENT_RIGHT) textX = x+width-metrics.stringWidth(text);
		g.setColor(colorText);
		g.drawString(text, textX, textY);
	}
	
	private void paintSingleBaseline(Graphics g, int x, int y, int width, int height, FontMetrics metrics, String text){
		g.setColor(colorLine);
		g.drawLine(x+width/2-metrics.stringWidth(text)/2-2, y+height/2+metrics.getAscent()/2+4, x+width/2+metrics.stringWidth(text)/2+2, y+height/2+metrics.getAscent()/2+4);
	}
	private void paintBaseTopline(Graphics g, int x, int y, int width, int height, FontMetrics metrics, String text){
		g.setColor(colorLine);
		g.drawLine(x, y+height/2+metrics.getAscent()/2+4, x+width, y+height/2+metrics.getAscent()/2+4);
		g.drawLine(x, y+height/2-metrics.getAscent()/2-4, x+width, y+height/2-metrics.getAscent()/2-4);
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

}







