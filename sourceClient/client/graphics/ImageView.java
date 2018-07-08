package client.graphics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class ImageView extends Component{
	
	private Image img;
	
	public ImageView(float x, float y, float width, float height, Image img) {
		super(x, y, width, height);
		this.img = img;
	}
	
	public ImageView(float x, float y, float width, float height, String imgUrl) {
		super(x, y, width, height);
		Image img = new ImageIcon(getClass().getResource(imgUrl)).getImage();
		this.img = img;
	}
	
	@Override
	public void paintG(Graphics g){
		Rectangle pos = getPosition();
		int x = pos.x;
		int y = pos.y;
		int width = pos.width;
		int height = pos.height;
		g.drawImage(img, x, y, width, height, null);
	}
	
}
