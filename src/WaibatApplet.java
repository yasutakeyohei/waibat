import javax.swing.*;
import java.awt.Container;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.*;


public class WaibatApplet extends JApplet implements Runnable, WaibatConstants{

	private CardLayout cl;
	private Container contentPane;
	private int bonusState;
	private boolean threadSuspended =false;
	
	private float[] worthRate = new float[4];
	private long gameStartTime, lastScoreTime;
	private float lastScore, scoreRate;
	
	TrackingJComponent trackJcom;
	BonusMenuJComponent bonusMenuJcom;
	FigureRotationJComponent figureRotationJcom;
	QuadrantLocationJComponent quadrantLocationJcom;
	DigitCancelingJComponent digitCancelingJcom;
	FinishJComponent finishJcom;
	
	
	public void init() {
		setFocusable(true);
		requestFocus();
	
		
		bonusState = BONUSMENU;

		contentPane = getContentPane();	
		getRootPane().setDoubleBuffered(true);
		((JComponent)getContentPane()).setDoubleBuffered(true);
		((JComponent)getContentPane()).setBackground(Color.black);

		//Tracking
		trackJcom = new TrackingJComponent(this);
		trackJcom.setPreferredSize(contentPane.getPreferredSize());
		
		//Menu
		bonusMenuJcom = new BonusMenuJComponent(this, trackJcom);
		bonusMenuJcom.setPreferredSize(contentPane.getPreferredSize());
		
		//Figure Rotation
		figureRotationJcom = new FigureRotationJComponent(this,trackJcom);
		figureRotationJcom.setPreferredSize(contentPane.getPreferredSize());

		//QuadrantLocation
		quadrantLocationJcom = new QuadrantLocationJComponent(this,trackJcom);		
		quadrantLocationJcom.setPreferredSize(contentPane.getPreferredSize());
		
		digitCancelingJcom = new DigitCancelingJComponent(this, trackJcom);
		digitCancelingJcom.setPreferredSize(contentPane.getPreferredSize());

		finishJcom = new FinishJComponent(this, trackJcom);
		finishJcom.setPreferredSize(contentPane.getPreferredSize());

		cl = new CardLayout();
		contentPane.setLayout(cl);
		contentPane.add("track",trackJcom);
		contentPane.add("bonusmenu", bonusMenuJcom);
		contentPane.add("figure", figureRotationJcom);
		contentPane.add("quadrant", quadrantLocationJcom);
		contentPane.add("digit", digitCancelingJcom);
		contentPane.add("finish", finishJcom);
				
		trackJcom.setFocusable(true);
		trackJcom.addKeyListener((KeyListener)trackJcom);
		bonusMenuJcom.setFocusable(true);
		bonusMenuJcom.addKeyListener((KeyListener)bonusMenuJcom);
		quadrantLocationJcom.setFocusable(true);
		quadrantLocationJcom.addKeyListener((KeyListener)quadrantLocationJcom);
		digitCancelingJcom.setFocusable(true);
		digitCancelingJcom.addKeyListener((KeyListener)digitCancelingJcom);		
		figureRotationJcom.setFocusable(true);
		figureRotationJcom.addKeyListener((KeyListener)figureRotationJcom);
		finishJcom.setFocusable(true);
		
		cl.show(contentPane, "track");
		trackJcom.requestFocus();
	
		worthRate[FIGURE] 	= 0.4f;
		worthRate[QUADRANT] = 0.4f;
		worthRate[DIGIT] 	= 0.4f;
		
		gameStartTime = System.currentTimeMillis();

		Thread t = new Thread(this);
		t.start();
	}

	public void startBonus(int s){
		bonusState = s;
		switch(bonusState){
		case FIGURE:	cl.show(contentPane, "figure");
						trackJcom.scoreUnSuspend();
						figureRotationJcom.startBonus();
						decreaseWorthRate(FIGURE);
						break;
		case QUADRANT:	cl.show(contentPane, "quadrant");
						trackJcom.scoreUnSuspend();
						quadrantLocationJcom.startBonus();
						decreaseWorthRate(QUADRANT);
						break;
		case DIGIT:		cl.show(contentPane, "digit");
						trackJcom.scoreUnSuspend();
						digitCancelingJcom.startBonus();
						decreaseWorthRate(DIGIT);
						break;
		default:break;
	}
	}

	public void trackToBonus(){
		switch(bonusState){
			case BONUSMENU:	//bonusMenuJcom.requestFocusInWindow();
							cl.show(contentPane, "bonusmenu");
							trackJcom.scoreSuspend();
							bonusMenuJcom.requestFocus();
							break;
			case FIGURE:	cl.show(contentPane, "figure");
							trackJcom.scoreUnSuspend();
							figureRotationJcom.bonusShow();
							break;
			case QUADRANT:	cl.show(contentPane, "quadrant");
							trackJcom.scoreUnSuspend();
							quadrantLocationJcom.bonusShow();
							break;
			case DIGIT:		cl.show(contentPane, "digit");
							trackJcom.scoreUnSuspend();
							digitCancelingJcom.bonusShow();
							break;
			case FINISH:	cl.show(contentPane, "finish");
							finishJcom.bonusShow();
							break;
			default:break;
		}
	}
	
	public void finishBonus(){
		bonusState = BONUSMENU;
		trackJcom.resetWorth();
		cl.show(contentPane, "bonusmenu");
		trackJcom.scoreSuspend();
		bonusMenuJcom.requestFocus();
	}

	public void bonusToTrack(){
		cl.show(contentPane, "track");
		trackJcom.scoreUnSuspend();
		trackJcom.requestFocus();		
	}


	public float getTotalScore(){
		float s = (trackJcom.getScore()
		+ figureRotationJcom.getScore()
		+ quadrantLocationJcom.getScore()
		+ digitCancelingJcom.getScore())/4;
		return s;
	}

	public float getFinalScore(){
		float t = (float)(System.currentTimeMillis() - gameStartTime);
		if(scoreRate < 0.000001) return (TOTALTIME-t)*0.000001f + lastScore; else return (TOTALTIME-t)*scoreRate + lastScore;
	}
	
	public void decreaseWorthRate(int stage){
		for(int i = 1; i < 4; i++){
			if(i == stage){
				worthRate[i] -= 0.05f;
				if(worthRate[i] < 0) worthRate[i] = 0;
			}else{
				worthRate[i] += 0.025f;
				if(worthRate[i] > 1) worthRate[i] = 1;				
			}
		}
	}
	
	public float getWorthRate(int stage){
		return worthRate[stage];
	}
	
	public void run(){
		try{
			while(true){
				if(!threadSuspended){
					if(lastScoreTime != 0){
						float s = getTotalScore();
						if(lastScoreTime != 0){
							scoreRate = (s - lastScore) / (float)(System.currentTimeMillis() - lastScoreTime);
						}
					}
					if(System.currentTimeMillis() - gameStartTime > TOTALTIME){
						bonusState = FINISH;
						threadSuspended = true;
						trackToBonus();
					}
					lastScoreTime = System.currentTimeMillis();
					lastScore = getTotalScore();
					Thread.sleep(400);
				}
			}
		}catch(Exception e){
			
		}
				

	}

}
