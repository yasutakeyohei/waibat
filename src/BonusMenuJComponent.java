import javax.swing.JComponent;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class BonusMenuJComponent extends JComponent implements KeyListener, Runnable, WaibatConstants {
	private TrackingJComponent tj;
	private WaibatApplet wa;
	private int w, h;
	private int numBoxSize, numBoxLeft, numBoxTop;
	private int stw, sth, stha;
	private Font font;
	private int[] xp0 = new int[4];
	private int[] yp0 = new int[4];
	private int[] xp1 = new int[4];
	private int[] yp1 = new int[4];
	private float score;
	private long lastTime;
	private boolean threadSuspended = false;
	
	public BonusMenuJComponent(WaibatApplet wa, TrackingJComponent tj) {
		super();
		this.tj = tj; 
		this.wa = wa;
		Thread t = new Thread(this);
		t.start();
	}

	
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		switch(key){
			case KeyEvent.VK_1:
			case KeyEvent.VK_NUMPAD1:
				 wa.startBonus(WaibatApplet.FIGURE); break;
			case KeyEvent.VK_5:
			case KeyEvent.VK_NUMPAD5:
				 wa.startBonus(WaibatApplet.QUADRANT); break;
			case KeyEvent.VK_9:
			case KeyEvent.VK_NUMPAD9:
				 wa.startBonus(WaibatApplet.DIGIT); 
				break;
			case KeyEvent.VK_ENTER:
				 wa.bonusToTrack();			
				 break;
		default:break;
		}
	}
	public void keyReleased(KeyEvent e) {	
	}
	public void keyTyped(KeyEvent e) {	
	}
	
	public void paint(Graphics g){
		if(w == 0){
			w = getWidth();
			h = getHeight();
			font = new Font("Serif" , Font.PLAIN , (int)(0.04f*w));
			g.setFont(font);
	        FontMetrics fm = g.getFontMetrics();
	        stw = fm.stringWidth("1");
	        sth = fm.getHeight();
	        stha = fm.getAscent();
			numBoxSize = (int)(sth*1.1);
			numBoxLeft = (int)(w*0.2);
			numBoxTop = (int)((TrackingJComponent.PWBOXWIDTH + 0.05f)*w);
			xp0[0] = numBoxLeft + (int)(numBoxSize*2.5)+(int)(numBoxSize*0.7*0.1);
			xp0[1] = numBoxLeft + (int)(numBoxSize*2.5)+(int)(numBoxSize*0.7*0.2);
			xp0[2] = numBoxLeft + (int)(numBoxSize*2.5)+(int)(numBoxSize*0.7*0.8);
			xp0[3] = numBoxLeft + (int)(numBoxSize*2.5)+(int)(numBoxSize*0.7*0.9);
			yp0[0] = numBoxTop + (int)(numBoxSize*0.15) + (int)(numBoxSize*0.7*0.4);
			yp0[1] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.2);
			yp0[2] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.8);
			yp0[3] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.6);
			xp1[0] = numBoxLeft + (int)(numBoxSize*3.2)+(int)(numBoxSize*0.7*0.4);
			xp1[1] = numBoxLeft + (int)(numBoxSize*3.2)+(int)(numBoxSize*0.7*0.2);
			xp1[2] = numBoxLeft + (int)(numBoxSize*3.2)+(int)(numBoxSize*0.7*0.8);
			xp1[3] = numBoxLeft + (int)(numBoxSize*3.2)+(int)(numBoxSize*0.7*0.6);
			yp1[0] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.1);
			yp1[1] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.2);
			yp1[2] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.8);
			yp1[3] = numBoxTop + (int)(numBoxSize*0.15)+ (int)(numBoxSize*0.7*0.9);
			
		}

		g.setColor(Color.white);
		g.drawRect(numBoxLeft, numBoxTop, numBoxSize, numBoxSize);
		g.drawRect(numBoxLeft, numBoxTop + (int)(numBoxSize*2.5), numBoxSize, numBoxSize);
		g.drawRect(numBoxLeft, numBoxTop + numBoxSize*5, numBoxSize, numBoxSize);
		g.setFont(font);
		g.drawString("1", numBoxLeft+(numBoxSize-stw)/2, numBoxTop+(int)(numBoxSize     - (numBoxSize-stha)/2));
		g.drawString("5", numBoxLeft+(numBoxSize-stw)/2, numBoxTop+(int)(numBoxSize*3.5 - (numBoxSize-stha)/2));
		g.drawString("9", numBoxLeft+(numBoxSize-stw)/2, numBoxTop+(int)(numBoxSize*6   - (numBoxSize-stha)/2));

		//figure rotation
		g.drawRect(numBoxLeft+(int)(numBoxSize*2.5), numBoxTop+(int)(numBoxSize*0.15), (int)(numBoxSize*0.7), (int)(numBoxSize*0.7));
		g.drawRect(numBoxLeft+(int)(numBoxSize*3.2), numBoxTop+(int)(numBoxSize*0.15), (int)(numBoxSize*0.7), (int)(numBoxSize*0.7));
		g.drawPolyline(xp0,yp0,4);
		g.drawPolyline(xp1,yp1,4);

		//Quadrant Location
		g.setColor(Color.gray);
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.5), numBoxTop+(int)(numBoxSize*2.5), (int)(numBoxSize*0.7), (int)(numBoxSize*0.5));
		g.fillRect(numBoxLeft+(int)(numBoxSize*3.2), numBoxTop+(int)(numBoxSize*2.5), (int)(numBoxSize*0.7), (int)(numBoxSize*0.5));
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.5), numBoxTop+(int)(numBoxSize*3), (int)(numBoxSize*0.7), (int)(numBoxSize*0.5));
		g.fillRect(numBoxLeft+(int)(numBoxSize*3.2), numBoxTop+(int)(numBoxSize*3), (int)(numBoxSize*0.7), (int)(numBoxSize*0.5));
		g.setColor(Color.white);
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.78), numBoxTop+(int)(numBoxSize*2.75), (int)(numBoxSize*0.14), (int)(numBoxSize*0.14));
		g.fillRect(numBoxLeft+(int)(numBoxSize*3.48), numBoxTop+(int)(numBoxSize*2.75), (int)(numBoxSize*0.14), (int)(numBoxSize*0.14));
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.78), numBoxTop+(int)(numBoxSize*3.25), (int)(numBoxSize*0.14), (int)(numBoxSize*0.14));
		g.fillRect(numBoxLeft+(int)(numBoxSize*3.48), numBoxTop+(int)(numBoxSize*3.25), (int)(numBoxSize*0.14), (int)(numBoxSize*0.14));
		
		//Digit Canceling
		g.setColor(Color.blue);
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.5), numBoxTop+(int)(numBoxSize*5), (int)(numBoxSize*0.4), (int)(numBoxSize*0.4));
		g.setColor(Color.red);
		g.fillRect(numBoxLeft+(int)(numBoxSize*2.8), numBoxTop+(int)(numBoxSize*5.1), (int)(numBoxSize*0.5), (int)(numBoxSize*0.5));
		g.setColor(Color.green);
		g.fillRect(numBoxLeft+(int)(numBoxSize*3.1), numBoxTop+(int)(numBoxSize*5.2), (int)(numBoxSize*0.6), (int)(numBoxSize*0.6));

		g.setColor(Color.gray);
		g.fillRect(numBoxLeft+(int)(numBoxSize*5), numBoxTop, numBoxSize*4, numBoxSize);
		g.fillRect(numBoxLeft+(int)(numBoxSize*5), numBoxTop+(int)(numBoxSize*2.5), numBoxSize*4, numBoxSize);
		g.fillRect(numBoxLeft+(int)(numBoxSize*5), numBoxTop+(int)(numBoxSize*5), numBoxSize*4, numBoxSize);
		g.setColor(Color.black);
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop, (int)(numBoxSize*2.5), (int)(numBoxSize*0.45));
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop+(int)(numBoxSize*2.5), (int)(numBoxSize*2.5), (int)(numBoxSize*0.45));
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop+(int)(numBoxSize*5), (int)(numBoxSize*2.5), (int)(numBoxSize*0.45));
		g.setColor(Color.white);
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop,                       (int)(numBoxSize*3*wa.getWorthRate(FIGURE)), (int)(numBoxSize*0.45));
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop+(int)(numBoxSize*2.5), (int)(numBoxSize*3*wa.getWorthRate(QUADRANT)), (int)(numBoxSize*0.45));
		g.fillRect(numBoxLeft+(int)(numBoxSize*6), numBoxTop+(int)(numBoxSize*5),   (int)(numBoxSize*3*wa.getWorthRate(DIGIT)), (int)(numBoxSize*0.45));

		tj.drawPW(g);
		tj.drawAI(g);
		tj.drawTHM(g);

	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void run() {
		try{
			while(true){
				if(lastTime != 0){
					float dt = (float) (System.currentTimeMillis() - lastTime);
					score -= 70f*dt;
					repaint();
				}
				lastTime = System.currentTimeMillis();
				Thread.sleep(300);
			}
		}catch(Exception e){			
		}
	}
	
	public float getScore(){
		return score/(TOTALTIME*MAXPERFORMANCE*MAXWORTH);
	}

	
}
