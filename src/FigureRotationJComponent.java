import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.AmbientLight;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;


import javax.swing.JComponent;

import com.sun.j3d.utils.universe.SimpleUniverse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.FileNotFoundException;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import javax.vecmath.Vector3d;




public class FigureRotationJComponent extends JComponent implements KeyListener, Runnable, WaibatConstants {
	final static float FIGBOXTOP = 0.25f;
	final static float FIGBOXWIDTH = 0.3f;
	final static float FIGBOXHEIGHT = 0.3f;
	final static float FIGBOXINTERVAL = 0.01f;
	final static float FONTSIZE = 0.03f;
	final static int IDENTICAL 	= 0;
	final static int MIRROR 	= 1;
	final static int DIFFERENT 	= 2;
	final static int WAITANSWER  = 0;
	final static int CORRECTWAIT = 1;
	final static int WRONGWAIT   = 2;
	
	
	private SimpleUniverse u = null;
	private TrackingJComponent tj;
	private WaibatApplet wa;
	private int w, h;
	private Thread thread;
	private boolean threadSuspended = true;
	
	private Canvas3D mCanvas3D0, mCanvas3D1;
	private SimpleUniverse mUniverse0, mUniverse1;
	private BoundingSphere mSchedulingBounds;
	private TransformGroup tg0, tg1;
	private KeyNavigatorBehavior knb0, knb1;
	
	private boolean leftSide = true;

	private long lastTime, timerCounter;
	
	
	private Font font;
	private int sth, stha;
	private BranchGroup model0, model1;

	private int answer = 0;
	
	private int state = WAITANSWER;
	
	private float bperformance, bworth;
	private float score;
	
	private BoundingSphere getSchedulingBounds() {
		return this.mSchedulingBounds;
	}


	public FigureRotationJComponent(WaibatApplet wa, TrackingJComponent tj) {
		super();
		this.tj = tj; 
		this.wa = wa;
		thread = new Thread(this);
		thread.start();

		mCanvas3D0 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		mUniverse0 = new SimpleUniverse(mCanvas3D0);
		mCanvas3D0.setSize((int)(w*FIGBOXWIDTH),(int)(w*FIGBOXHEIGHT));
		//mCanvas3D0.stopRenderer();
		//mCanvas3D0.setDoubleBufferEnable(true);
		
		mCanvas3D1 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		mUniverse1 = new SimpleUniverse(mCanvas3D1);
		mCanvas3D1.setSize((int)(w*FIGBOXWIDTH),(int)(w*FIGBOXHEIGHT));
		//mCanvas3D1.stopRenderer();
		//mCanvas3D1.setDoubleBufferEnable(true);
		
		try{
			initializeUniverse();
		}catch(Exception e){
		}
		setLayout(null);
		add(mCanvas3D0);
		add(mCanvas3D1);
		
		bperformance = MAXPERFORMANCE;
		bworth = MAXWORTH;
	}
	
	private void initializeUniverse() throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
		BranchGroup tRootObject = new BranchGroup();

		tg0 = new TransformGroup();
		tg0.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		tg0.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		tg0.setCapability( TransformGroup.ALLOW_CHILDREN_READ );
		tg0.setCapability( TransformGroup.ALLOW_CHILDREN_WRITE );
		tg0.setCapability( TransformGroup.ALLOW_CHILDREN_EXTEND );
		model0 = new MQOLoader().load(getClass().getResource("/box0_0.mqo")).getSceneGroup();
		model0.setCapability(BranchGroup.ALLOW_DETACH);
		tg0.addChild(model0);

		knb0 = new KeyNavigatorBehavior(this, tg0);
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius( 1000.0 );
		knb0.setSchedulingBounds( bounds );
		tg0.addChild(knb0);

		tRootObject.addChild(tg0);
		tRootObject.addChild(createAmbientLight());
		tRootObject.addChild(createDirectionalLight());
		//tRootObject.addChild(tLoad.getSceneGroup());

		mUniverse0.addBranchGraph(tRootObject);
		mUniverse0.getViewingPlatform().setNominalViewingTransform();
		Transform3D tTransform3D = new Transform3D();
		mUniverse0.getViewingPlatform().getViewPlatformTransform().getTransform(tTransform3D);
		tTransform3D.setTranslation(new Vector3d(0d, 0d, 500d));
		mUniverse0.getViewingPlatform().getViewPlatformTransform().setTransform(tTransform3D);		
		mUniverse0.getViewer().getView().setBackClipDistance(1000.0);

		
		
		tRootObject = new BranchGroup();

		tg1 = new TransformGroup();
		tg1.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		tg1.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		tg1.setCapability( TransformGroup.ALLOW_CHILDREN_READ );
		tg1.setCapability( TransformGroup.ALLOW_CHILDREN_WRITE );
		tg1.setCapability( TransformGroup.ALLOW_CHILDREN_EXTEND );
		model1 = new MQOLoader().load(getClass().getResource("/box0_0.mqo")).getSceneGroup();
		model1.setCapability(BranchGroup.ALLOW_DETACH);
		tg1.addChild(model1);
		knb1 = new KeyNavigatorBehavior(this, tg1);
		knb1.setSchedulingBounds( bounds );	
		knb1.setEnable(false);
		tg1.addChild(knb1);

		
		tRootObject.addChild(tg1);
		tRootObject.addChild(createAmbientLight());
		tRootObject.addChild(createDirectionalLight());
		//tRootObject.addChild(tLoad.getSceneGroup());

		mUniverse1.addBranchGraph(tRootObject);
		mUniverse1.getViewingPlatform().setNominalViewingTransform();
		tTransform3D = new Transform3D();
		mUniverse1.getViewingPlatform().getViewPlatformTransform().getTransform(tTransform3D);
		tTransform3D.setTranslation(new Vector3d(0d, 0d, 500d));
		mUniverse1.getViewingPlatform().getViewPlatformTransform().setTransform(tTransform3D);		
		mUniverse1.getViewer().getView().setBackClipDistance(1000.0);
		
	}

	/*
	private ViewPlatformBehavior createViewPlatformBehavior() {
		OrbitBehavior tOrbitBehavior = new OrbitBehavior(this.getCanvas(), OrbitBehavior.REVERSE_ALL);
		tOrbitBehavior.setSchedulingBounds(this.getSchedulingBounds());
		tOrbitBehavior.setZoomFactor(100d);
		tOrbitBehavior.setTransFactors(100d, 100d);
		return tOrbitBehavior;
	}
	*/

	private AmbientLight createAmbientLight() {
		AmbientLight tLight = new AmbientLight();
		tLight.setInfluencingBounds(this.getSchedulingBounds());
		return tLight;
	}

	private DirectionalLight createDirectionalLight() {
		DirectionalLight tLight = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, 0.0f));
		tLight.setInfluencingBounds(this.getSchedulingBounds());
		return tLight;
	}

	/*
	private BranchGroup createSceneGraph() throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {

	}
	*/

	
	public void keyPressed(KeyEvent e) {
/*		System.out.println("tete");
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			wa.bonusToTrack();
		}else{
			mCanvas3D0.dispatchEvent(e);
			mCanvas3D1.dispatchEvent(e);
		}
		*/
	}

	public void keyReleased(KeyEvent arg0) {	}

	public void keyTyped(KeyEvent arg0) {	}

	public void run() {
		try{
			while(true){
				if(!threadSuspended){
					float dt = (System.currentTimeMillis() - lastTime);
					if(lastTime != 0){
						timerCounter += dt;
						if(timerCounter > TIMERMAX){
							//finished = true;
							finishBonus();
						}
						if(timerCounter > FIGPWWAITTIME){
							bworth -= BWORTHRATE * dt;
						}
						bperformance -= BPERFORMANCERATE * dt;
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
	
	public void destroy(){
		u.cleanup();
	}
	
	public void paint(Graphics g){
		if(w == 0){
			w = getWidth();
			h = getHeight();
			mCanvas3D0.setBounds((int)(w*(1-2*FIGBOXWIDTH-FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP),(int)(w*FIGBOXWIDTH),(int)(w*FIGBOXHEIGHT)); 
			mCanvas3D1.setBounds((int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP),(int)(w*FIGBOXWIDTH),(int)(w*FIGBOXHEIGHT)); 
			mCanvas3D0.setFocusable(true);
			mCanvas3D0.requestFocus();
			font = new Font("Serif" , Font.PLAIN , (int)(FONTSIZE*w));
			g.setFont(font);
	        FontMetrics fm = g.getFontMetrics();
	        sth = fm.getHeight();
	        stha = fm.getAscent();

		}
		if(leftSide) g.setColor(Color.white); else g.setColor(Color.gray);
		g.drawRect((int)(w*(1-2*FIGBOXWIDTH-FIGBOXINTERVAL)/2)-1, (int)(h*FIGBOXTOP)-1,(int)(w*FIGBOXWIDTH)+2,(int)(w*FIGBOXHEIGHT)+2); 
		if(leftSide) g.setColor(Color.gray); else g.setColor(Color.white);		
		g.drawRect((int)(w*(1+FIGBOXINTERVAL)/2)-1, (int)(h*FIGBOXTOP)-1,(int)(w*FIGBOXWIDTH)+2,(int)(w*FIGBOXHEIGHT)+2); 
		g.setColor(Color.yellow);
		g.setFont(font);
		if(state == CORRECTWAIT){			
			g.drawString("Correct!", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth);
			g.drawString("Press 4 to get a new problem.", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth*2);
		}else if(state == WRONGWAIT){
			g.drawString("Oops! Incorrect!", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth);			
			g.drawString("You can use the rest of time to", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth*2);
			g.drawString("know why your answer was wrong.", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth*3);			
		}else{
			g.drawString("1 -- Identical", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth);
			g.drawString("2 -- Mirror Symmetric", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth*2);
			g.drawString("3 -- Different", (int)(w*(1+FIGBOXINTERVAL)/2), (int)(h*FIGBOXTOP+w*FIGBOXHEIGHT)+sth*3);			
		}

		
		tj.drawPW(g);
		tj.drawPWB(g, bperformance, bworth);
		tj.drawTHM(g);
		tj.drawAI(g);
		tj.drawClock(g, timerCounter);

		//mCanvas3D0.repaint();
		//mCanvas3D1.repaint();
		//mCanvas3D0.swap();
		//mCanvas3D1.swap();
		
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void setKNB_Tg0(){
		knb1.setEnable(false);
		knb0.setEnable(true);
		mCanvas3D0.setFocusable(true);
		mCanvas3D0.requestFocus();
		leftSide = true;
		repaint();
	}

	public void setKNB_Tg1(){
		knb0.setEnable(false);
		knb1.setEnable(true);
		mCanvas3D1.setFocusable(true);
		mCanvas3D1.requestFocus();
		leftSide = false;
		repaint();
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
		if(leftSide){
			setKNB_Tg0();		
		}else{
			setKNB_Tg1();
		}
		threadSuspended = false;
		notify();
		lastTime = System.currentTimeMillis();
	}
	
	public synchronized void startBonus(){
		createResetNewStage();
		this.requestFocus();
		timerCounter = 0;
		if(leftSide){
			setKNB_Tg0();		
		}else{
			setKNB_Tg1();
		}
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
	
	
	public void createResetNewStage(){
		tg0.removeChild(model0);
		tg1.removeChild(model1);

		answer = (int)(Math.random()*3);
		int mn = (int)(Math.random()*3);
		int mn_n = (int)(Math.random()*3);
		int m = (int)(Math.random()*2);
		String fileName0 = "", fileName1 = "";
		
		try{
		switch(answer){
			case IDENTICAL:
				fileName0 = "/box"+mn+"_"+mn_n+".mqo";
				fileName1 = "/box"+mn+"_"+mn_n+".mqo";
				break;
			case MIRROR:
				fileName0 = "/box"+mn+"_" +  m    + ".mqo";
				fileName1 = "/box"+mn+"_" + (1-m) + ".mqo";
				break;				
			case DIFFERENT:
				fileName0 = "/box"+mn+"_" +  m*2    + ".mqo";
				fileName1 = "/box"+mn+"_" + (1-m)*2 + ".mqo";
				break;				
			default:
			break;
		}
		
		//System.out.println(answer);
		model0 = new MQOLoader().load(getClass().getResource(fileName0)).getSceneGroup();
		model1 = new MQOLoader().load(getClass().getResource(fileName1)).getSceneGroup();
		model0.setCapability(BranchGroup.ALLOW_DETACH);
		model1.setCapability(BranchGroup.ALLOW_DETACH);
		tg0.addChild(model0);
		tg1.addChild(model1);
		Transform3D t3d = new Transform3D();
		t3d.rotX(Math.PI*Math.random()*2);
		t3d.rotY(Math.PI*Math.random()*2);
		t3d.rotZ(Math.PI*Math.random()*2);
		tg0.setTransform(t3d);
		t3d.rotX(Math.PI*Math.random()*2);
		t3d.rotY(Math.PI*Math.random()*2);
		t3d.rotZ(Math.PI*Math.random()*2);
		tg1.setTransform(t3d);

		}catch(Exception e){		
			System.out.println(e);
		}
	}
	
	public void answered(int key){
		if(state == WAITANSWER){
			switch(key){
				case KeyEvent.VK_1:
				case KeyEvent.VK_NUMPAD1:
					if(answer == IDENTICAL){
						state = CORRECTWAIT; 
						bperformance = MAXPERFORMANCE;
						bworth += MAXWORTH/4;
						if(bworth > MAXWORTH) bworth = MAXWORTH;
					}else{
						state = WRONGWAIT;
						bworth -= MAXWORTH/4;
						if(bworth < 0) bworth = 0;
					}
					break;
					
				case KeyEvent.VK_2:
				case KeyEvent.VK_NUMPAD2:
					if(answer == MIRROR){
						state = CORRECTWAIT;
						bperformance = MAXPERFORMANCE;
						bworth += MAXWORTH/4;
						if(bworth > MAXWORTH) bworth = MAXWORTH;
					}else{
						state = WRONGWAIT;
						bworth -= MAXWORTH/4;
						if(bworth < 0) bworth = 0;
					}
					break;
				case KeyEvent.VK_3:
				case KeyEvent.VK_NUMPAD3:
					if(answer == DIFFERENT){
						state = CORRECTWAIT;
						bperformance = MAXPERFORMANCE;
						bworth += MAXWORTH/4;
						if(bworth > MAXWORTH) bworth = MAXWORTH;
					}else{
						state = WRONGWAIT;
						bworth -= MAXWORTH/4;
						if(bworth < 0) bworth = 0;
					}
					break;
					default:break;
			}
		}else if(state==CORRECTWAIT){
			switch(key){
				case KeyEvent.VK_4:
				case KeyEvent.VK_NUMPAD4:
					state = WAITANSWER;
					createResetNewStage();
					break;
				default: break;
			}
		}
	}
	public float getScore(){
		return score/(TOTALTIME*MAXPERFORMANCE*MAXWORTH);
	}
}
