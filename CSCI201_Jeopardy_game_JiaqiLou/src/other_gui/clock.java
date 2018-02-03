package other_gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class clock extends JPanel implements ActionListener{
	ImageIcon images[];
	int totalImages = 143, currentImage = 0, animationDelay = 100;
	Timer animationTimer;
	public clock() {
		this.setBackground(Color.darkGray);
		this.setAlignmentX(CENTER_ALIGNMENT);
		this.setAlignmentY(CENTER_ALIGNMENT);
		images = new ImageIcon[totalImages];
		for (int i =0; i < images.length; i++) {
			BufferedImage img = null;
			try {
				File f = new File("images/clockAnimation/frame_"+i+"_delay-0.06s.jpg");
			    img = ImageIO.read(f);
			    f = null;
			   
			} catch (IOException e) {
			    e.printStackTrace();
			}
			Image dimg = img.getScaledInstance(40,40,Image.SCALE_FAST);
			img.flush();
			img = null;
			images[i] = new ImageIcon (dimg);
			dimg.flush();
			dimg = null;
		}
		startAnimation();	
	}
	
	public void setBackColor(Color c) {
		this.setBackground(c);
	}
	
	private void startAnimation() {
		if (animationTimer == null) {
			currentImage = 0;
			animationTimer = new Timer (animationDelay, this);
			animationTimer.start();
		} else if (!animationTimer.isRunning()) {
			animationTimer.restart();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
			images[currentImage].paintIcon(this, g,  this.getWidth()/2,this.getHeight()/2);
			currentImage = (currentImage + 1) % totalImages;
		}
	}
	
	public void stopAnimation() {
		animationTimer.stop();
	}
	
	public void restartAnimation() {
		animationTimer.restart();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}

}
