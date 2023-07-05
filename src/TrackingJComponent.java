import javax.swing.JComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;


public class TrackingJComponent extends JComponent implements KeyListener, Runnable, WaibatConstants {	
	final static float MAXHEXSPD = 0.1f;
	final static float ACC = 0.004f;
	final static float MAXWIDTH = 3.0f/5.0f;
	final static float MINWIDTH = 1.0f/3.0f;
	final static float CTOPMOST = 2.0f/7.0f;
	final static float CBTMMOST = 11.0f/21.0f;
	final static float C_R = 0.03f;
	final static float TC_R = 0.03f;
	final static float TTC_R = 0.01f;
	final static float KPFWIDTH = 0.05f; //Key(joystick) Position Frame Width
	final static float KP_R = 0.01f;
	final static float MAXKPX = 10f;
	final static float MAXKPY = 10f;
	final static float BARDIST  = 0.04f;
	final static float PWBOXWIDTH = 0.15f; //performance & worth box width
	final static float PWBOXTOP  = 0.02f;
	final static float PWBOXLEFT  = 0.02f;	
	final static float THERMWIDTH = 0.05f; // thermometer
	final static float THERMTOP  = PWBOXTOP;
	final static float THERMLEFT  = 0.98f-THERMWIDTH;
	final static float THERMHEIGHT  = 0.8f;
	final static float FONTSIZE  = 0.03f;
	final static float WORTHRATE = 0.000007f;
	final static float PERFORMANCERATE = 0.00003f;
	final static int KEY_UP = 1;
	final static int KEY_DOWN = 2;
	final static int KEY_LEFT = 4;
	final static int KEY_RIGHT = 8;
	final static int KEY_TRIGGER = 16;
	final static int MANUAL = 0;
	final static int AUTO = 1;
	final static int REPAIR = 2;
	final static int REPAIRTIME = 10000;
	final static long MINAUTOTIME = 10000;
	final static int AUTOFAILRATE = 1000; //(once per AUTOFAILRATE*40msec)
	
	private int w, h;
	private int hexWidth = 0;
	private float hexWidthFloat;
	private float hexSpd = 0.022f;
	private int hexDir = -1;
	private float cr, cx, cy;
	private float cxSpd, cySpd;
	private float tcx, tcy, ttcx, ttcy, ttcxAcc, ttcyAcc;
	private float kpx[] = new float[2];
	private float kpy[] = new float[2];
	private float kspdx, kspdy;
	private float kSpd = 0.022f;
	private float tbWidth, ttbWidth, ttbAcc, ttbSpd;
	private float performance, worth;
	private long repairStartTime;
	private int stl_autotrack, stl_manual, stl_trigger, stl_w;
	private int sth_autotrack, stha_autotrack;
	private Font font;
	private int trackMode = MANUAL;
	private boolean trackFailed = false;
	
	private int keyState[] = new int[2];// key(joystick) state; 0 for left, 1 for right

	
	private boolean accmode = false;
	private long lastTime = 0;
	private int yp[] = new int[7];
	
	private WaibatApplet wa;
	
	private long autoLastTime = 0;
	
	private float score;
	private boolean scoreSuspended = false;
	
	
	public TrackingJComponent(WaibatApplet wa) {
		super();
		this.wa = wa;
		Thread t = new Thread(this);
		t.start();
		worth = MAXWORTH;
		performance = MAXPERFORMANCE;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key=e.getKeyCode();
		switch(key){
			case KeyEvent.VK_W: 	keyState[0] |= KEY_UP; 		break;
			case KeyEvent.VK_S: 	keyState[0] |= KEY_DOWN;	break;
			case KeyEvent.VK_A:		keyState[0] |= KEY_LEFT;	break;
			case KeyEvent.VK_D: 	keyState[0] |= KEY_RIGHT;	break;
			case KeyEvent.VK_UP: 	keyState[1] |= KEY_UP;		break;
			case KeyEvent.VK_DOWN:	keyState[1] |= KEY_DOWN;	break;
			case KeyEvent.VK_LEFT:	keyState[1] |= KEY_LEFT;	break;
			case KeyEvent.VK_RIGHT: keyState[1] |= KEY_RIGHT;	break;
			case KeyEvent.VK_SPACE:	keyState[1] |= KEY_TRIGGER;
				if(trackMode == AUTO){
					if(trackFailed){
						trackMode = REPAIR;
						repairStartTime = System.currentTimeMillis();
						trackFailed = false;
					}else{
						trackMode = MANUAL;
					}
				}else if(trackMode == MANUAL && isPerfectTrack()){
					trackMode = AUTO;
					autoLastTime = System.currentTimeMillis();
				}
				break;
			case KeyEvent.VK_ENTER: wa.trackToBonus();
									break;
		default:break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key=e.getKeyCode();
		switch(key){
			case KeyEvent.VK_W: 	keyState[0] &= ~KEY_UP; 	break;
			case KeyEvent.VK_S: 	keyState[0] &= ~KEY_DOWN;	break;
			case KeyEvent.VK_A:		keyState[0] &= ~KEY_LEFT;	break;
			case KeyEvent.VK_D: 	keyState[0] &= ~KEY_RIGHT;	break;
			case KeyEvent.VK_UP: 	keyState[1] &= ~KEY_UP; 	break;
			case KeyEvent.VK_DOWN:	keyState[1] &= ~KEY_DOWN;	break;
			case KeyEvent.VK_LEFT:	keyState[1] &= ~KEY_LEFT;	break;
			case KeyEvent.VK_RIGHT: keyState[1] &= ~KEY_RIGHT;	break;
			case KeyEvent.VK_SPACE:	keyState[1] &= ~KEY_TRIGGER;break;
			default:break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isPerfectTrack(){
		if(Math.sqrt( (cx-tcx)*(cx-tcx)+(cy-tcy)*(cy-tcy)) <= C_R*w
				&& hexWidth < tbWidth+BARDIST*w/2 && hexWidth > tbWidth-BARDIST*w/2){
			return true;
		}else{
			return false;
		}
		
	}
	
	@Override
	public void run() {
		try{
			while(true){
				if(lastTime != 0){
					float dt = (float) (System.currentTimeMillis() - lastTime);

					//HEX movement
					hexWidthFloat += (float)hexDir * dt * hexSpd;				
					if(hexWidthFloat>w*MAXWIDTH){
						hexWidthFloat = w*MAXWIDTH;
						hexSpd = 0;
						hexDir *= -1;
					}else if(hexWidthFloat < w*MINWIDTH){
						hexWidthFloat = w*MINWIDTH;
						hexSpd = 0;
						hexDir *= -1;			
					}
					hexWidth = (int)hexWidthFloat;
					
					if((int)(Math.random() * 200.0) == 1){
						hexSpd = ACC;
						hexDir = -1*hexDir;
					}
					if((int)(Math.random() * 100.0) == 2){
						hexSpd += ACC;
						if(hexSpd > MAXHEXSPD){ hexSpd -= ACC; }
					}else if((int)(Math.random() * 100.0) == 3){
						hexSpd -= ACC;				
						if(hexSpd < 0){ hexSpd += ACC; }
					}
					
					//Circle Movement
					cx += dt * cxSpd;
					cy += dt * cySpd;
					if(cx > (w+w*MAXWIDTH)/2){
						cx = (w+w*MAXWIDTH)/2;
						cxSpd = 0;
					}else if(cx<(w-w*MAXWIDTH)/2){
						cx = (w-w*MAXWIDTH)/2;
						cxSpd = 0;
					}
					if(cy > h*(CBTMMOST-C_R)){
						cy = h*(CBTMMOST-C_R);
						cySpd = 0;
					}else if(cy< h*CTOPMOST){
						cy = h*CTOPMOST;
						cySpd = 0;
					}
					if((int)(Math.random() * 100.0) == 2){
						cxSpd += ACC;
						if(cxSpd > MAXHEXSPD){ cxSpd -= ACC; }
					}else if((int)(Math.random() * 100.0) == 3){
						cxSpd -= ACC;				
						if(cxSpd < -1*MAXHEXSPD){ cxSpd += ACC; }
					}else if((int)(Math.random() * 100.0) == 4){
						cySpd += ACC;
						if(cySpd > MAXHEXSPD){ cySpd -= ACC; }
					}else if((int)(Math.random() * 100.0) == 5){
						cySpd -= ACC;				
						if(cySpd < -1*MAXHEXSPD){ cySpd += ACC; }
					}				


					//Key(joystick) related
					for(int i=0; i < 2; i++){
						if((keyState[i] & KEY_LEFT) == KEY_LEFT){
							kpx[i] -= kSpd * dt;
							if(kpx[i] < -MAXKPX) kpx[i] = -MAXKPX;
						}else if((keyState[i] & KEY_RIGHT) == KEY_RIGHT){
							kpx[i] += kSpd * dt;
							if(kpx[i] > MAXKPX) kpx[i] = MAXKPX;
						}
	
						if((keyState[i] & KEY_UP) == KEY_UP){
							kpy[i] -= kSpd * dt;
							if(kpy[i] < -MAXKPY) kpy[i] = -MAXKPY;
						}else if((keyState[i] & KEY_DOWN) == KEY_DOWN){
							kpy[i] += kSpd * dt;
							if(kpy[i] > MAXKPY) kpy[i] = MAXKPY;
						}
						
						if(keyState[i] == 0){
							if(kpx[i] > kSpd*dt){
								kpx[i] -= kSpd*dt;
							}else  if(kpx[i] < -kSpd*dt){
								kpx[i] += kSpd*dt;
							}else{
								kpx[i] = 0;
							}
	
							if(kpy[i] > kSpd*dt){
								kpy[i] -= kSpd*dt;
							}else  if(kpy[i] < -kSpd*dt){
								kpy[i] += kSpd*dt;
							}else{
								kpy[i] = 0;
							}
						}
					}	
					
					//Target and Target Circle
					if(trackMode==AUTO){
						float fac1 = 0.0005f;
						float fac2 = 1.5f;
						if(trackFailed){							
							fac1 = 0.000001f;
							fac2 = 0.3f;
						}
						if(tcx > cx){
							tcx -= (tcx-cx)*(tcx-cx)*fac1*dt;
							ttcx = tcx-(tcx-cx)*fac2;
							if(ttcx-tcx > C_R*w) ttcx = tcx + C_R*w/2;							
						}else{
							tcx += (cx-tcx)*(cx-tcx)*fac1*dt;							
							ttcx = tcx + (cx-tcx)*fac2; 
							if(tcx-ttcx > C_R*w) ttcx = tcx - C_R*w/2;							
						}
						
						if(tcy > cy){
							tcy -= (tcy-cy)*(tcy-cy)*fac1*dt;
							ttcy = tcy-(tcy-cy)*fac2; 
							if(ttcy-tcy > C_R*w) ttcy = tcy + C_R*w/2;							
						}else{
							tcy += (cy-tcy)*(cy-tcy)*fac1*dt;					
							ttcy = tcy + (cy-tcy)*fac2; 
							if(tcy-ttcy > C_R*w) ttcy = tcy - C_R*w/2;							
						}

						if(tbWidth > hexWidth){
							tbWidth -= (hexWidth-tbWidth)*(hexWidth-tbWidth)*fac1*dt;						
							ttbWidth = tbWidth-(tbWidth-hexWidth)*fac2; 
							if(ttbWidth-tbWidth > BARDIST*w) ttbWidth = tbWidth+BARDIST*w/2;
						}else{
							tbWidth += (tbWidth-hexWidth)*(tbWidth-hexWidth)*fac1*dt;						
							ttbWidth = tbWidth+(hexWidth-tbWidth)*fac2; 
							if(tbWidth-ttbWidth > BARDIST*w) ttbWidth = tbWidth-BARDIST*w/2;							
						}
					}else{
						if(accmode){
							ttcxAcc = kpx[1]*Math.abs(kpx[1])*0.00008f* w * dt/ MAXKPX;
							kspdx += ttcxAcc * 0.00003*dt;
							tcx += kspdx*dt / MAXKPX;
							if(tcx < (TC_R*w)){
								tcx = (TC_R*w);
							}else if(tcx > (w - TC_R*w)){
								tcx = (w - TC_R*w);							
							}
							ttcx = tcx + 1.0f*TC_R*w*kpx[1]/MAXKPX;
	
							
							ttcyAcc = kpy[1]*Math.abs(kpy[1])*0.00008f * w * dt / MAXKPY;
							kspdy += ttcyAcc * 0.00003*dt;
							tcy += kspdy*dt / MAXKPY;						
							if(tcy < (TC_R*w)){
								tcy = (TC_R*w);
							}else if(tcy > (h - TC_R*w)){
								tcy = (h - TC_R*w);							
							}
							ttcy = tcy + 1.0f*TC_R*w*kpy[1]/MAXKPY;
	
	
							ttbAcc = kpy[0]*Math.abs(kpy[0])*0.00008f * w * dt / MAXKPY;
							ttbSpd += ttbAcc * 0.00003*dt;
							tbWidth += ttbSpd*dt / MAXKPY;					
							if(tbWidth < (BARDIST*2*w)){
								tbWidth = (BARDIST*2*w);
							}else if(tbWidth > (w - BARDIST*2*w)){
								tbWidth = (w - BARDIST*2*w);							
							}
							ttbWidth = tbWidth + 1.5f*BARDIST*w*kpy[0]/MAXKPY;
																			
						}else{
							ttcx += kpx[1]*0.0003* w * dt/ MAXKPX;
							if(ttcx < (TTC_R*w)){
								ttcx = (TTC_R*w);
							}else if(ttcx > (w - TTC_R*w)){
								ttcx = (w - TTC_R*w);							
							}
							
							ttcy += kpy[1]*0.0003 * w * dt / MAXKPY;
							if(ttcy < (TTC_R*w)){
								ttcy = (TTC_R*w);
							}else if(ttcy > (h - TTC_R*w)){
								ttcy = (h - TTC_R*w);							
							}
							
							if(Math.abs(ttcx - tcx) < TC_R*w){
								tcx += kSpd* (ttcx-tcx)*0.95*dt / MAXKPX;
							}else{
								tcx += kSpd* (ttcx-tcx)*1.2*dt / MAXKPX;
							}
							
							if(Math.abs(ttcy - tcy) < TC_R*w){
								tcy += kSpd* (ttcy-tcy)*0.95*dt / MAXKPY;
							}else{
								tcy += kSpd* (ttcy-tcy)*1.2*dt / MAXKPY;
							}
	
							
							ttbWidth -= kpy[0]*0.0003* w * dt/ MAXKPX;
							if(ttbWidth < (TTC_R*w)){
								ttbWidth = (TTC_R*w);
							}else if(ttbWidth > (w - TTC_R*w)){
								ttbWidth = (w - TTC_R*w);							
							}
													
							if(Math.abs(ttbWidth - tbWidth) < TC_R*w){
								tbWidth += kSpd* (ttbWidth-tbWidth)*0.95*dt / MAXKPX;
							}else{
								tbWidth += kSpd* (ttbWidth-tbWidth)*1.2*dt / MAXKPX;
							}
						}
					}
					float d = (float)Math.sqrt((tcx - cx)*(tcx - cx) +(tcy - cy)*(tcy - cy));
					worth = worth - WORTHRATE*dt;
					if(d < C_R*w){
						performance += 0.001f * dt;
						if(performance > MAXPERFORMANCE) performance = MAXPERFORMANCE;
						 worth += 0.0005f*dt;
					}else{
						if(d < 5*C_R*w ){
							performance -= PERFORMANCERATE*d*dt;
							worth -= PERFORMANCERATE*0.5*d*dt;
						}else{
							performance -= PERFORMANCERATE*5*C_R*w*dt;
							worth -= PERFORMANCERATE*5*C_R*w*0.5*dt;
						}
						if(performance < 0) performance = 0;												
					}
					if(performance < MAXPERFORMANCE/3) worth -= WORTHRATE*0.001*dt;
					if(worth < 0) worth = 0;
					if(worth > MAXWORTH) worth = MAXWORTH;
					
					if(!scoreSuspended) score += worth*performance*dt;
					
					if(repairStartTime != 0 && trackMode == REPAIR && System.currentTimeMillis()-repairStartTime > REPAIRTIME ){
						trackMode = MANUAL;
					}
				}
				//auto track failure
				//System.out.println((float)(System.currentTimeMillis() - autoLastTime));
				if(autoLastTime != 0 && System.currentTimeMillis() - autoLastTime > MINAUTOTIME){
					if((int)(Math.random()*AUTOFAILRATE) == 1){
						//trackMode = REPAIR;
						trackFailed = true;
					}
				}else if(autoLastTime == 0){
					autoLastTime = System.currentTimeMillis();					
				}
				
				if((int)(Math.random()*ACCCHANGERATE) == 1){
					accmode = !accmode;
				}
				
				lastTime = System.currentTimeMillis();
		
				repaint();
				Thread.sleep(80);
			}
		}catch(Exception e){
		}
	}
	
	public void paint(Graphics g){
		if(w == 0){
			w = getWidth();
			h = getHeight();
			hexWidthFloat = (float)w*MAXWIDTH;
			hexWidth = (int)hexWidthFloat;
			yp[0] = 3*h/7;
			yp[1] = 11*h/21;
			yp[2] = 16*h/21;
			yp[3] = 6*h/7;
			yp[4] = yp[2];yp[5] = yp[1];yp[6] = yp[0];
			cx = 2*0.9f*w/3;
			cy = 2*h/7;
			cr = (int)((float)w*C_R);
			tcx = 2*w/3;
			tcy = 2*h/7;
			ttcx = tcx; ttcy = tcy;
			tbWidth = hexWidth;
			ttbWidth = tbWidth;
			font = new Font("Serif" , Font.BOLD , (int)(FONTSIZE*w));
			g.setFont(font);
	        FontMetrics fm = g.getFontMetrics();
	        stl_autotrack = fm.stringWidth("AUTOTRACK");
	        stl_manual    = fm.stringWidth("MANUAL");
	        stl_trigger	  = fm.stringWidth("Trigger");
	        stl_w	  	  = fm.stringWidth("W");
	        sth_autotrack = fm.getHeight();
	        stha_autotrack = fm.getAscent();
		}
		int xp[] = {w/2,(w+hexWidth)/2,(w+hexWidth)/2,w/2,(w-hexWidth)/2,(w-hexWidth)/2,w/2};
		g.setColor(Color.blue);
		g.drawPolyline(xp, yp, 7);
		g.drawOval((int)(cx-cr), (int)(cy-cr), (int)(cr*2), (int)(cr*2));
		
		//Circle & Circle Target
		g.setColor(Color.yellow);
		g.drawLine((int)tcx, (int)(tcy-TC_R*w), (int)tcx, (int)(tcy+TC_R*w));
		g.drawLine((int)(tcx-TC_R*w), (int)tcy, (int)(tcx+TC_R*w), (int)tcy);
		g.drawOval((int)(ttcx-TTC_R*w), (int)(ttcy-TTC_R*w), (int)(TTC_R*w*2), (int)(TTC_R*w*2));
		g.drawLine((int)tcx, (int)tcy, (int)ttcx, (int)ttcy);
		
		//Bar & Bar Target
		g.drawLine((int)((w-tbWidth)/2 - BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*1/3), (int)((w-tbWidth)/2 - BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*2/3));
		g.drawLine((int)((w-tbWidth)/2 + BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*1/3), (int)((w-tbWidth)/2 + BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*2/3));
		g.drawLine((int)((w+tbWidth)/2 - BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*1/3), (int)((w+tbWidth)/2 - BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*2/3));
		g.drawLine((int)((w+tbWidth)/2 + BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*1/3), (int)((w+tbWidth)/2 + BARDIST*w/2), (int)(yp[1]+(yp[2]-yp[1])*2/3));
		g.drawOval((int)((w-ttbWidth)/2 - TTC_R*w), (int)((yp[2]+yp[1])/2 - TTC_R*w), (int)(TTC_R*w*2), (int)(TTC_R*w*2));
		g.drawOval((int)((w+ttbWidth)/2 - TTC_R*w), (int)((yp[2]+yp[1])/2 - TTC_R*w), (int)(TTC_R*w*2), (int)(TTC_R*w*2));
		g.drawLine((int)((w-tbWidth)/2), (int)((yp[2]+yp[1])/2),(int)((w-ttbWidth)/2), (int)((yp[2]+yp[1])/2));
		g.drawLine((int)((w+tbWidth)/2), (int)((yp[2]+yp[1])/2),(int)((w+ttbWidth)/2), (int)((yp[2]+yp[1])/2));
		
				
		//joystick - RIGHT
		g.setColor(Color.yellow);
		g.drawRect((int)(w-KPFWIDTH*w-KP_R*w*2-2),(int)(h-KPFWIDTH*w-KP_R*w*2-2),(int)(KPFWIDTH*w+KP_R*w*2), (int)(KPFWIDTH*w+KP_R*w*2));
		g.drawOval((int)(w-KPFWIDTH*w-KP_R*w  -2 + KPFWIDTH*w/2 + w*KPFWIDTH*kpx[1]/MAXKPX/2 - w*KP_R),(int)(h-KPFWIDTH*w-KP_R*w -2 + KPFWIDTH*w/2 + w*KPFWIDTH*kpy[1]/MAXKPX/2 - w*KP_R),(int)(KP_R*w*2), (int)(KP_R*w*2));
		
		//joystick - LEFT
		g.drawRect(2,(int)(h-KPFWIDTH*w-KP_R*w*2-2),(int)(KPFWIDTH*w+KP_R*w*2), (int)(KPFWIDTH*w+KP_R*w*2));
		g.drawOval((int)(2 + KPFWIDTH*w/2 + w*KPFWIDTH*kpx[0]/MAXKPX/2),(int)(h-KPFWIDTH*w-KP_R*w -2 + KPFWIDTH*w/2 + w*KPFWIDTH*kpy[0]/MAXKPX/2 - w*KP_R),(int)(KP_R*w*2), (int)(KP_R*w*2));
		
		//performance & worth
		drawPW(g);
		//drawPWB(g);
		drawTHM(g);
		
		//Auto track indicator
		drawAI(g);
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void drawPW(Graphics g){
		g.setColor(Color.blue);
		g.fillRect((int)(PWBOXLEFT*w), (int)(PWBOXTOP*h),  (int)(PWBOXWIDTH*w), (int)(PWBOXWIDTH*w));
		g.setColor(Color.black);
		g.fillRect((int)(PWBOXLEFT*w + PWBOXWIDTH*w*0.18),  (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.18), (int)(PWBOXWIDTH*w*0.68), (int)(PWBOXWIDTH*w*0.68));
		g.setColor(Color.white);
		g.drawRect((int)(PWBOXLEFT*w + PWBOXWIDTH*w*0.18),   (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.86) - (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE), (int)(PWBOXWIDTH*w*0.68*worth/MAXWORTH), (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE));
		g.drawRect((int)(PWBOXLEFT*w + PWBOXWIDTH*w*0.18)+1, (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.86) - (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE)+1, (int)(PWBOXWIDTH*w*0.68*worth/MAXWORTH)-2, (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE)-2);		
		if(performance < MAXPERFORMANCE*0.7){
			g.setColor(Color.gray);
			g.fillRect((int)(PWBOXLEFT*w + PWBOXWIDTH*w*0.18)+2, (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.86) - (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE)+2, (int)(PWBOXWIDTH*w*0.68*worth/MAXWORTH)-3, (int)(PWBOXWIDTH*w*0.68*performance /MAXPERFORMANCE)-3);					
		}
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString("P", (int)(PWBOXLEFT*w), (int)(PWBOXTOP*h + (PWBOXWIDTH*w-sth_autotrack)/2 + stha_autotrack));
		g.drawString("W", (int)(PWBOXLEFT*w + (PWBOXWIDTH*w-stl_w)/2), (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.85 + stha_autotrack));
	}

	//bonus Performance, Worth
	public void drawPWB(Graphics g, float bperformance, float bworth){
		int left= (int)((THERMLEFT - PWBOXWIDTH*1.1)*w);
		g.setColor(Color.magenta);
		g.fillRect(left, (int)(PWBOXTOP*h),  (int)(PWBOXWIDTH*w), (int)(PWBOXWIDTH*w));
		g.setColor(Color.black);
		g.fillRect(left + (int)(PWBOXWIDTH*w*0.18),  (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.18), (int)(PWBOXWIDTH*w*0.68), (int)(PWBOXWIDTH*w*0.68));
		g.setColor(Color.white);
		g.drawRect(left + (int)(PWBOXWIDTH*w*0.18),   (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.86) - (int)(PWBOXWIDTH*w*0.68*bperformance /MAXPERFORMANCE),   (int)(PWBOXWIDTH*w*0.68*bworth/MAXWORTH), (int)(PWBOXWIDTH*w*0.68*bperformance /MAXPERFORMANCE));
		g.drawRect(left + (int)(PWBOXWIDTH*w*0.18)+1, (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.86) - (int)(PWBOXWIDTH*w*0.68*bperformance /MAXPERFORMANCE)+1, (int)(PWBOXWIDTH*w*0.68*bworth/MAXWORTH)-2, (int)(PWBOXWIDTH*w*0.68*bperformance /MAXPERFORMANCE)-2);		
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString("P", left, (int)(PWBOXTOP*h + (PWBOXWIDTH*w-sth_autotrack)/2 + stha_autotrack));
		g.drawString("W", left + (int)((PWBOXWIDTH*w-stl_w)/2), (int)(PWBOXTOP*h + PWBOXWIDTH*w*0.85 + stha_autotrack));
	}

	//bonus Performance, Worth
	public void drawTHM(Graphics g){
		int left= (int)(THERMLEFT*w);
		g.setColor(Color.green);
		g.fillRect(left+1, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getTotalScore()),  (int)(THERMWIDTH*w)-1, (int)(THERMHEIGHT*h*0.96*wa.getTotalScore()));
		g.setColor(Color.white);
		g.drawRect(left, (int)(THERMTOP*h),  (int)(THERMWIDTH*w), (int)(THERMHEIGHT*h));
		g.drawLine(left, (int)(THERMTOP*h+THERMHEIGHT*h*0.02),  left+(int)(THERMWIDTH*w), (int)(THERMTOP*h+THERMHEIGHT*h*0.02));
		g.drawLine(left, (int)(THERMTOP*h+THERMHEIGHT*h*0.98),  left+(int)(THERMWIDTH*w), (int)(THERMTOP*h+THERMHEIGHT*h*0.98));		
		for(int i = 1; i < 10; i++)	g.drawLine(left+(int)(THERMWIDTH*w*0.7), (int)(THERMTOP*h+THERMHEIGHT*h*0.02+THERMHEIGHT*h*0.098*i),  left+(int)(THERMWIDTH*w), (int)(THERMTOP*h+THERMHEIGHT*h*0.02+THERMHEIGHT*h*0.098*i));
		g.drawLine(left+1, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getTotalScore()),  left+(int)(THERMWIDTH*w/2), (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getTotalScore()-THERMHEIGHT*h*0.1));
		g.drawLine(left+(int)(THERMWIDTH*w)-1, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getTotalScore()),  left+(int)(THERMWIDTH*w/2), (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getTotalScore()-THERMHEIGHT*h*0.1));

		g.drawLine(left+1, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getFinalScore()), left+(int)(THERMWIDTH*w)-1, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getFinalScore()));
		
		g.drawString("E", left-stl_w, (int)(THERMTOP*h+THERMHEIGHT*h*0.98-THERMHEIGHT*h*0.96*wa.getFinalScore()+stha_autotrack/2));
	}

	
	//Auto track indicator
	public void drawAI(Graphics g){
		g.setFont(font);
		int left = (int)((PWBOXLEFT+PWBOXWIDTH*1.1)*w);
		int top = (int)(PWBOXTOP*h);

		if(trackMode == AUTO && !trackFailed){
			g.setColor(Color.white);
			g.drawRect(left, top, (int)(stl_autotrack*1.1), sth_autotrack);
			g.setColor(Color.gray);
			g.drawRect(left, top+sth_autotrack+(int)(sth_autotrack*0.3), (int)(stl_autotrack*1.1), sth_autotrack);
		}else if(trackMode == AUTO && trackFailed){
			g.setColor(Color.red);			
			g.fillRect(left, top, (int)(stl_autotrack*1.1), sth_autotrack);
			g.setColor(Color.gray);
			g.drawRect(left, top+sth_autotrack+(int)(sth_autotrack*0.3), (int)(stl_autotrack*1.1), sth_autotrack);
		}else if(trackMode == REPAIR){
			g.setColor(Color.red);			
			g.fillRect(left, top, (int)(stl_autotrack*1.1), sth_autotrack);
			g.setColor(Color.white);
			g.drawRect(left, top+sth_autotrack+(int)(sth_autotrack*0.3), (int)(stl_autotrack*1.1), sth_autotrack);
		}else{
			g.setColor(Color.gray);			
			g.drawRect(left, top, (int)(stl_autotrack*1.1), sth_autotrack);
			g.setColor(Color.white);
			g.drawRect(left, top+sth_autotrack+(int)(sth_autotrack*0.3), (int)(stl_autotrack*1.1), sth_autotrack);
		}	
		

		if((keyState[1] & KEY_TRIGGER) == KEY_TRIGGER || trackMode == REPAIR || trackMode == AUTO && trackFailed){
			g.setColor(Color.red);
			g.fillRect(left+(int)(stl_autotrack*1.1+sth_autotrack*0.3)+1, top+ (int)(sth_autotrack*0.5)+1, (int)(stl_trigger*1.1)-1, sth_autotrack-1);
		}

		g.setColor(Color.blue);
		g.drawString("AUTOTRACK", left+(int)(0.05*stl_autotrack), top+stha_autotrack);
		g.setColor(Color.blue);
		g.drawString("MANUAL", left+(int)((stl_autotrack*1.1-stl_manual)/2), top+ (int)(sth_autotrack*1.3)+stha_autotrack);
		g.setColor(Color.green);
		g.drawRect(left+(int)(stl_autotrack*1.1+sth_autotrack*0.3), top+ (int)(sth_autotrack*0.5), (int)(stl_trigger*1.1), sth_autotrack);
		g.setColor(Color.white);
		g.drawString("Trigger", left+(int)(stl_autotrack*1.1+sth_autotrack*0.3+stl_trigger*0.05), top+ (int)(sth_autotrack*0.5)+stha_autotrack);								
		

	}
	
	
	public void drawClock(Graphics g, long timerCounter){
		g.setColor(Color.white);
		g.drawOval((int)(CLOCKX*w), (int)(CLOCKY*h), (int)(CLOCKSIZE*w), (int)(CLOCKSIZE*w));
		g.drawLine((int)((CLOCKX+CLOCKSIZE/2)*w), (int)(CLOCKY*h), (int)((CLOCKX+CLOCKSIZE/2)*w), (int)(CLOCKY*h+CLOCKSIZE*w));
		g.drawLine((int)(CLOCKX*w), (int)(CLOCKY*h+CLOCKSIZE/2*w), (int)((CLOCKX+CLOCKSIZE)*w), (int)(CLOCKY*h+CLOCKSIZE/2*w));
		g.setColor(Color.black);
		g.fillOval((int)((CLOCKX+CLOCKSIZE*0.1)*w), (int)(CLOCKY*h+CLOCKSIZE*0.1*w), (int)(CLOCKSIZE*w*0.8), (int)(CLOCKSIZE*w*0.8));
		g.setColor(Color.white);
		g.drawLine((int)((CLOCKX+CLOCKSIZE/2)*w),(int)(CLOCKY*h+CLOCKSIZE/2*w), (int)((CLOCKX+CLOCKSIZE/2+CLOCKSIZE*0.4*Math.cos(timerCounter*2*3.14/TIMERMAX - 1.57))*w),(int)(CLOCKY*h+(CLOCKSIZE/2+CLOCKSIZE*0.4*Math.sin(timerCounter*2*3.14/TIMERMAX -1.57))*w));	
	}
	
	public void resetWorth(){
		worth = MAXWORTH;
	}
	
	public float getScore(){
		return score/(TOTALTIME*MAXPERFORMANCE*MAXWORTH);
	}
	
	public synchronized void scoreSuspend(){
		scoreSuspended = true;
		notify();		
	}

	public synchronized void scoreUnSuspend(){
		scoreSuspended = false;
		notify();		
	}

}
