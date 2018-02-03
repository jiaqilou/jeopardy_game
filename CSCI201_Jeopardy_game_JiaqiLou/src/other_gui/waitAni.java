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

public class waitAni extends JPanel implements ActionListener{
	
	ImageIcon images[];
	int totalImages = 8, currentImage = 0, animationDelay = 100;
	Timer animationTimer;
	public waitAni() {
		this.setBackground(Color.darkGray);
		this.setAlignmentX(CENTER_ALIGNMENT);
		this.setAlignmentY(CENTER_ALIGNMENT);
		images = new ImageIcon[totalImages];
		for (int i =0; i < images.length; i++) {
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("images/waitingAnimation/frame_"+i+
						"_delay-0.1s.jpg"));
			} catch (IOException e) {
			    e.printStackTrace();
			}
			Image dimg = img.getScaledInstance(100,100,Image.SCALE_FAST);
			images[i] = new ImageIcon (dimg);
		}
		startAnimation();
	}
	
	public void stopAnimation() {
		animationTimer.stop();
	}
	
	public void restartAnimation() {
		animationTimer.restart();
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
			images[currentImage].paintIcon(this, g,  this.getWidth()/2,this.getHeight()/2-30);
			currentImage = (currentImage + 1) % totalImages;
		}
	}
	
	public void startAnimation() {
		if (animationTimer == null) {
			currentImage = 0;
			animationTimer = new Timer (animationDelay, this);
			animationTimer.start();
		} else if (!animationTimer.isRunning()) {
			animationTimer.restart();
		}	
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}

}
