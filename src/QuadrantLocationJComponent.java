import javax.swing.JComponent;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class QuadrantLocationJComponent extends JComponent implements KeyListener, Runnable, WaibatConstants{
	private static float QUADBOXTOP = 0.25f;
	private static float QUADBOXLEFT = 0.2f;
	private static float QUADBOXWIDTH = 1.0f-QUADBOXLEFT*2;
	private static float QUADBOXHEIGHT = 0.65f;
		
	private TrackingJComponent tj;
	private WaibatApplet wa;
	private int w, h;
	private Font font, kfont;
	private int stw, sth, stha, ksth, kstw, kstha;
	private int[] numx = new int[32]; //quadrant order 
	private int[] numy = new int[32]; //quadrant order
	private int[] num  = new int[32]; //number order
	private int[] numq = new int[32]; //quadrant of number
	private boolean[][] occupied = new boolean[20][10];
	
	private int[][] pattern4  = {{0,1,2,3},{3,2,1,0},{1,0,2,3},{0,2,3,1},{3,1,2,0}};
	private int[][] pattern8  = {{0,1,0,2,3,2,3,1},{0,1,1,3,3,2,2,0}, {0,2,2,3,3,1,1,0},{0,2,1,2,3,1,0,3}};
	private int pattern4n = 5;
	private int pattern8n = 4;

	private int nextNum = 1;
	private long lastTime, timerCounter;
	private boolean finished = true;
	
	private Thread thread;
	private boolean threadSuspended = true;
	
	private float bworth, bperformance;
	private float score;
	
	public QuadrantLocationJComponent(WaibatApplet wa,TrackingJComponent tj) {
		super();
		this.tj = tj;
		this.wa = wa;
		thread = new Thread(this);
		thread.start();
		bperformance = MAXPERFORMANCE;
		bworth = MAXWORTH/2;
	}
	

	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		int nk=0;
		switch(key){
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
				nk = key - KeyEvent.VK_0;
				break;
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_NUMPAD9:
				nk = key - KeyEvent.VK_NUMPAD0;
				break;
			case KeyEvent.VK_4:
			case KeyEvent.VK_NUMPAD4:
				if(finished){
					createResetNewStage();
				}
				break;
			case KeyEvent.VK_ENTER:
				bonusToTrack();
				break;
			default:nk = -1;break;
		}		
		if(nk != -1){
			if(!finished && 
					 ( nk == 9 && numq[nextNum-1] == 0
					|| nk == 6 && numq[nextNum-1] == 1
					|| nk == 5 && numq[nextNum-1] == 2
					|| nk == 8 && numq[nextNum-1] == 3)){
				nextNum++;
				if(nextNum > 32) finished = true;
				repaint();
				bperformance += MAXPERFORMANCE/32;
				bworth += MAXWORTH/32;
			}else{
				bperformance -= MAXPERFORMANCE/32;
				bworth -= MAXWORTH/32;				
			}
		}
	}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}

	public void run() {
		try{
			while(true){
				if(!threadSuspended){
					float dt = (System.currentTimeMillis() - lastTime);
					if(lastTime != 0){
						timerCounter += dt;
						if(timerCounter > TIMERMAX){
							finishBonus();						
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

	
	private void generateNumPos(){
		int nw = (int)(w*QUADBOXWIDTH/2  / stw);
		int nh = (int)(h*QUADBOXHEIGHT/2 / sth);

		int r = (int)(Math.random()*pattern4n); //pattern number
		int s = (int)(Math.random()*4); //start quadrant
		for(int i = 0; i < 4; i++){
			numq[i] = (s + pattern4[r][i]) % 4;
		}
		for(int i = 0; i < 4; i++){
			numq[i+4] = (s + pattern4[r][i]) % 4;
		}

		r = (int)(Math.random()*pattern8n); //pattern number
		s = (int)(Math.random()*4); //start quadrant
		for(int i = 0; i < 8; i++){
			numq[i+8] = (s + pattern8[r][i]) % 4;
		}
		for(int i = 0; i < 8; i++){
			numq[i+16] = (s + pattern8[r][i]) % 4;
		}

		r = (int)(Math.random()*pattern4n); //pattern number
		s = (int)(Math.random()*4); //start quadrant
		for(int i = 0; i < 4; i++){
			numq[i+24] = (s + pattern4[r][i]) % 4;
		}
		for(int i = 0; i < 4; i++){
			numq[i+28] = (s + pattern4[r][i]) % 4;
		}	


		int[] nq_x = new int[32];
		int[] nq_y = new int[32];
		for(int q = 0; q < 4; q++){
			for(int i = 0; i < 20; i++){
				for(int j = 0; j < 10; j++){
					occupied[i][j] = false;
				}
			}
			
			//4th quadrant
			for(int i = 0; i < 8; i++){
				int nx = (int)(Math.random() * nw);
				int ny = (int)(Math.random() * nh);
				
				while(occupied[nx][ny]
				            || nx >= (int)((QUADBOXWIDTH*w/4 - ksth/2)/stw-0.5)
				            && nx <= (int)((QUADBOXWIDTH*w/4 + ksth/2)/stw+0.5)
				            && ny >= (int)((QUADBOXHEIGHT*h/4 - ksth/2)/sth-0.5)
				            && ny <= (int)((QUADBOXHEIGHT*h/4 + ksth/2)/sth+0.5)){
					 nx = (int)(Math.random() * nw);
					 ny = (int)(Math.random() * nh);
				}			
				
				nq_x[i+q*8] = (q == 0 || q == 1) ? nx*stw + (int)((QUADBOXLEFT+QUADBOXWIDTH/2)*w + stw/2) : nx*stw +(int)(QUADBOXLEFT*w+ stw/2);
				nq_y[i+q*8] = (q == 1 || q == 2) ? ny*sth + (int)((QUADBOXTOP+QUADBOXHEIGHT/2)*h + sth/2) + stha :ny*sth + (int)(QUADBOXTOP*h + sth/2) + stha;
				occupied[nx][ny] = true;
				
			}

			int[] qc= new int[4]; //counter
			for(int i = 0; i < 32; i++){
				numx[i] =  nq_x[qc[numq[i]]+8*numq[i]];
				numy[i] =  nq_y[qc[numq[i]]+8*numq[i]];
				qc[numq[i]]++;
			}			
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
	        stw = fm.stringWidth("44 ");
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
		
		//Huge box
		g.setColor(Color.white);
		g.drawRect((int)(w*QUADBOXLEFT), (int)(h*QUADBOXTOP), (int)(w*QUADBOXWIDTH/2), (int)(h*QUADBOXHEIGHT/2));
		g.drawRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/2)), (int)(h*QUADBOXTOP), (int)(w*QUADBOXWIDTH/2), (int)(h*QUADBOXHEIGHT/2));
		g.drawRect((int)(w*QUADBOXLEFT), (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/2)), (int)(w*QUADBOXWIDTH/2), (int)(h*QUADBOXHEIGHT/2));
		g.drawRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/2)), (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/2)), (int)(w*QUADBOXWIDTH/2), (int)(h*QUADBOXHEIGHT/2));

		//Key Number
		g.setColor(Color.yellow);
		g.fillRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-ksth/2,   (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4)) - ksth/2, ksth, ksth);
		g.fillRect((int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-ksth/2, (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4)) - ksth/2, ksth, ksth);
		g.fillRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-ksth/2,   (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))- ksth/2, ksth, ksth);
		g.fillRect((int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-ksth/2, (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))- ksth/2, ksth, ksth);
		g.setColor(Color.white);
		g.drawRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-ksth/2,   (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4)) - ksth/2, ksth, ksth);
		g.drawRect((int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-ksth/2, (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4)) - ksth/2, ksth, ksth);
		g.drawRect((int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-ksth/2,   (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))- ksth/2, ksth, ksth);
		g.drawRect((int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-ksth/2, (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))- ksth/2, ksth, ksth);
	
		g.setColor(Color.black);
		g.drawString("8",(int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-kstw/2, (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4))+ksth-kstha);
		g.drawString("5",(int)(w*(QUADBOXLEFT+QUADBOXWIDTH/4))-kstw/2, (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))+ksth-kstha);
		g.drawString("9",(int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-kstw/2, (int)(h*(QUADBOXTOP+QUADBOXHEIGHT/4))+ksth-kstha);
		g.drawString("6",(int)(w*(QUADBOXLEFT+3*QUADBOXWIDTH/4))-kstw/2, (int)(h*(QUADBOXTOP+3*QUADBOXHEIGHT/4))+ksth-kstha);

		g.setFont(font);
		g.setColor(Color.white);
		for(int i = nextNum-1; i < 32; i++){
			g.drawString(""+(i+1), numx[i], numy[i]);
		}

		if(finished){
			g.setColor(Color.yellow);
			g.drawString("Press 4 to get a new problem.", (int)(w - g.getFontMetrics().stringWidth("Press 4 to get a new problem.        ")), (int)(h-sth));
		}
		tj.drawPW(g);
		tj.drawPWB(g, bperformance, bworth);
		tj.drawTHM(g);
		tj.drawAI(g);

		//clock
		tj.drawClock(g, timerCounter);
	}
	
	public void update(Graphics g){
		paint(g);
	}

	public void createResetNewStage(){
		generateNumPos();
		nextNum = 1;
		finished = false;
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
		g.drawLine((int)((CLOCKX+CLOCKSIZE/2)*w),(int)(CLOCKY*h+CLOCKSIZE/2*w), (int)((CLOCKX+CLOCKSIZE/2+CLOCKSIZE*0.4*Math.cos(timerCounter*6*3.14/180000 - 1.57))*w),(int)(CLOCKY*h+(CLOCKSIZE/2+CLOCKSIZE*0.4*Math.sin(timerCounter*6*3.14/180000 -1.57))*w));	
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
		paint(getGraphics());
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
		finished = true;
		threadSuspended = true;
		notify();
		wa.finishBonus();
	}
	
	public float getScore(){
		return score/(TOTALTIME*MAXPERFORMANCE*MAXWORTH);
	}

}
