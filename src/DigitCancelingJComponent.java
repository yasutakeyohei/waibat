import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;


public class DigitCancelingJComponent extends JComponent implements KeyListener, Runnable, WaibatConstants {
	private static float NUMBOXCENTERX = 0.25f;
	private static float NUMBOXCENTERY = 0.55f;
	private static float FONTSIZE = 0.05f;
	private static float KFONTSIZE = 0.05f;
		
	private TrackingJComponent tj;
	private WaibatApplet wa;
	private int w, h;
	private Font font, kfont;
	private int stw, sth, stha, ksth, kstw, kstha;
	private int[] num  = new int[50]; //number order
	private boolean[] scored = new boolean[50];
	private int nextNum = 1;
	
	private long lastTime, timerCounter, intervalCounter;
	private boolean finished = true;
	
	private int interval = 1000;
	private int dinterval = 800;
	
	private boolean numVisible = true;
	private boolean keyEntered = false;
	
	private Thread thread;
	private boolean threadSuspended = true;
	
	private float bperformance, bworth;
	private float score;
	
	public DigitCancelingJComponent(WaibatApplet wa,TrackingJComponent tj) {
		super();
		this.tj = tj;
		this.wa = wa;
		thread = new Thread(this);
		thread.start();
	}

	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		int enteredNum = -1;
		switch(key){
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
				enteredNum = key - KeyEvent.VK_0;
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_NUMPAD8:
				if(enteredNum < 0) enteredNum = key - KeyEvent.VK_NUMPAD0;
				keyEntered = true;
				if(numVisible && nextNum > 3 && !scored[nextNum-3]){
					if(enteredNum == num[nextNum-3]){
						scored[nextNum-3] = true;
						bperformance += MAXPERFORMANCE/22;
						bworth += BWORTHRATE/25;
						if(bperformance > MAXPERFORMANCE) bperformance = MAXPERFORMANCE;
						if(bworth > MAXPERFORMANCE) bworth = MAXWORTH;
					}else{
						scored[nextNum-3] = true;
						bperformance -= MAXPERFORMANCE/22;
						bworth -= BWORTHRATE/25;											
						if(bperformance < 0) bperformance = 0;
						if(bworth < 0) bworth = 0;
					}
				}
			break;
			case KeyEvent.VK_ENTER:
				bonusToTrack();
				break;
			default:break;
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void run() {
		try{
			while(true){
				if(!threadSuspended){
					if(lastTime != 0){
						long dt = (System.currentTimeMillis() - lastTime);
						timerCounter += dt;
						if(timerCounter > TIMERMAX){
							finishBonus();
						}
						
						intervalCounter += dt;
						if(nextNum < 3){
							if(numVisible && intervalCounter > dinterval){
								numVisible = false;
								intervalCounter = 0;
							}else if(!numVisible && intervalCounter > interval){
								nextNum++;
								numVisible = true;
								intervalCounter = 0;
							}
						}else{
							if(numVisible && keyEntered && intervalCounter > interval/2){
								keyEntered = false;
								numVisible = false;
								intervalCounter = 0;
							}else if(!numVisible && intervalCounter > interval){
								//if(keyEntered) minus point
								keyEntered = false;
								nextNum++;
								numVisible = true;
								intervalCounter = 0;
							}						
						}
						bperformance -= BPERFORMANCERATE * dt;
						bworth -= BWORTHRATE * dt;
						score = bperformance * bworth * dt;
					}
					repaint();
					lastTime = System.currentTimeMillis();
				}
				Thread.sleep(300);
		        synchronized(this){
		            while(threadSuspended){wait();}  	//½Ú¯ÄÞ‚ÌˆêŽž’âŽ~
		        }
			}
		}catch(Exception e){			
		}
	}
	
	public void paint(Graphics g){
		if(w == 0){
			w = getWidth();
			h = getHeight();
			font = new Font("Serif" , Font.PLAIN , (int)(FONTSIZE*w));
			kfont = new Font("Serif" , Font.PLAIN , (int)(KFONTSIZE*w));
			g.setFont(font);
	        FontMetrics fm = g.getFontMetrics();
	        stw = fm.stringWidth("1");
	        sth = fm.getHeight();
	        stha = fm.getAscent();

	        g.setFont(kfont);
	        fm = g.getFontMetrics();
	        kstw = fm.stringWidth("1");
	        ksth = fm.getAscent()+fm.getDescent();
	        kstha = fm.getAscent();
	        
	        createResetNewStage();
	        timerCounter = 0;			
		}
		if(numVisible){
			g.setColor(Color.white);
			g.drawRect((int)((w-sth)/2), (int)(h*NUMBOXCENTERY-sth/2), (int)sth, (int)sth);
			g.setFont(kfont);
			g.drawString(""+num[nextNum-1], (int)((w - stw)/2), (int)(h*NUMBOXCENTERY - sth/2 + stha));
		}
		tj.drawPW(g);
		tj.drawPWB(g, bperformance, bworth);
		tj.drawTHM(g);
		tj.drawAI(g);
		tj.drawClock(g, timerCounter);
	}
	
	private void createResetNewStage(){
		for(int i = 0; i < 50; i++){
			num[i] = (int)(Math.random()*8) + 1;
			scored[i] = false;
		}		
		nextNum = 1;
		finished = false;
		numVisible = true;
	}
	/*
	public void drawClock(Graphics g){
		g.setColor(Color.white);
		g.drawOval((int)(CLOCKX*w), (int)(CLOCKY*h), (int)(CLOCKSIZE*w), (int)(CLOCKSIZE*w));
		g.drawLine((int)((CLOCKX+CLOCKSIZE/2)*w), (int)(CLOCKY*h), (int)((CLOCKX+CLOCKSIZE/2)*w), (int)(CLOCKY*h+CLOCKSIZE*w));
		g.drawLine((int)(CLOCKX*w), (int)(CLOCKY*h+CLOCKSIZE/2*w), (int)((CLOCKX+CLOCKSIZE)*w), (int)(CLOCKY*h+CLOCKSIZE/2*w));
		g.setColor(Color.black);
		g.fillOval((int)((CLOCKX+CLOCKSIZE*0.1)*w), (int)(CLOCKY*h+CLOCKSIZE*0.1*w), (int)(CLOCKSIZE*w*0.8), (int)(CLOCKSIZE*w*0.8));
		g.setColor(Color.white);
		g.drawLine((int)((CLOCKX+CLOCKSIZE/2)*w),(int)(CLOCKY*h+CLOCKSIZE/2*w), (int)((CLOCKX+CLOCKSIZE/2+CLOCKSIZE*0.4*Math.cos(timerCounter*2*3.14/TIMERMAX - 1.57))*w),(int)(CLOCKY*h+(CLOCKSIZE/2+CLOCKSIZE*0.4*Math.sin(timerCounter*2*3.14/TIMERMAX -1.57))*w));	
	}
	*/
	
	public synchronized void bonusToTrack(){
		threadSuspended = true;
		notify();
		wa.bonusToTrack();
	}
	
	public synchronized void bonusShow(){
		this.requestFocus();
		threadSuspended = false;
		notify();
		lastTime = System.currentTimeMillis();
	}
	
	public synchronized void startBonus(){
		timerCounter = 0;
		createResetNewStage();
		this.requestFocus();
		threadSuspended = false;
		notify();
		lastTime = System.currentTimeMillis();
		bperformance = MAXPERFORMANCE;
		bworth = MAXWORTH/2;
	}
	
	public synchronized void finishBonus(){
		threadSuspended = true;
		notify();
		wa.finishBonus();
	}
	
	public float getScore(){
		return score/(TOTALTIME*MAXPERFORMANCE*MAXWORTH);
	}

	

}
