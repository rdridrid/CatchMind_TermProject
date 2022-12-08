import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{
		Image img;
		public ImagePanel(Image img) {
			this.img=img;
		}
		
		public void paintComponent(Graphics g) {
			g.drawImage(img,0,0,this.getWidth(),this.getHeight(),null);
		}
	}