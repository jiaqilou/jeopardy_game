package game_logic;

import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Jeo_Timer {
	private Timer timer;
	private int seconds;
	
	public Jeo_Timer (int Sec, ActionListener al) {
		seconds = Sec;
		timer = new Timer (1000, al);
		timer.start();
		
		
	}
	
	public void stopTimer () {
		timer.stop();
	}
	
	public void restartTimer() {
		timer.restart();
	}
}
