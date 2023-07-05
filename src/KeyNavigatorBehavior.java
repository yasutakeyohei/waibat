import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import java.awt.Component;
import java.util.LinkedList;
import javax.media.j3d.*;
import com.sun.j3d.internal.J3dUtilsI18N;

/**
 * This class is a simple behavior that invokes the KeyNavigator
 * to modify the view platform transform.
 */
public class KeyNavigatorBehavior extends Behavior implements KeyListener {
    private WakeupCriterion w1 = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    private WakeupCriterion w2 = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
    private WakeupOnElapsedFrames w3 = new WakeupOnElapsedFrames(0);
    private WakeupCriterion[] warray = { w1, w2, w3 };
    private WakeupCondition w = new WakeupOr(warray);
    private KeyEvent eventKey;
    private KeyNavigator keyNavigator;
    private boolean listener = false;
    private FigureRotationJComponent figRotJcom;

    private LinkedList eventq;


    public void initialize() {
	if (listener) {
	    w1 = new WakeupOnBehaviorPost(this, KeyEvent.KEY_PRESSED);
	    w2 = new WakeupOnBehaviorPost(this, KeyEvent.KEY_RELEASED);
	    warray[0] = w1;
	    warray[1] = w2;
	    w = new WakeupOr(warray);
	    eventq = new LinkedList();
	}
	wakeupOn(w);
    }

    /**
     *  Override Behavior's stimulus method to handle the event.
     */
    public void processStimulus(Enumeration criteria) {
	WakeupOnAWTEvent ev;
	WakeupCriterion genericEvt;
	AWTEvent[] events;
	boolean sawFrame = false;
   
	while (criteria.hasMoreElements()) {
	    genericEvt = (WakeupCriterion) criteria.nextElement();
	    if (genericEvt instanceof WakeupOnAWTEvent) {
		ev = (WakeupOnAWTEvent) genericEvt;
		events = ev.getAWTEvent();
		processAWTEvent(events);
	    } else if (genericEvt instanceof WakeupOnElapsedFrames &&
		       eventKey != null) {
		sawFrame = true;
	    } else if ((genericEvt instanceof WakeupOnBehaviorPost)) {
		while(true) {
		    // access to the queue must be synchronized
		    synchronized (eventq) {
			if (eventq.isEmpty()) break;
			eventKey  = (KeyEvent)eventq.remove(0);
			if (eventKey.getID() == KeyEvent.KEY_PRESSED ||
			    eventKey.getID() == KeyEvent.KEY_RELEASED) {
			    keyNavigator.processKeyEvent(eventKey);
			}
		    }
		}
	    }
	}
	if (sawFrame)
	    keyNavigator.integrateTransformChanges();

	// Set wakeup criteria for next time
	wakeupOn(w);
    }

    /**
     *  Process a keyboard event
     */
    private void processAWTEvent(AWTEvent[] events) {
	for (int loop = 0; loop < events.length; loop++) {
	    if (events[loop] instanceof KeyEvent) {
		eventKey = (KeyEvent) events[loop];
		//  change the transformation; for example to zoom
		if (eventKey.getID() == KeyEvent.KEY_PRESSED ||
		    eventKey.getID() == KeyEvent.KEY_RELEASED) {
		    //System.out.println("Keyboard is hit! " + eventKey+eventKey.getKeyCode());
			if(eventKey.getKeyCode() == KeyEvent.VK_COMMA){
				figRotJcom.setKNB_Tg0();				
			}else if(eventKey.getKeyCode() == KeyEvent.VK_PERIOD){
				figRotJcom.setKNB_Tg1();								
			}else if(eventKey.getID() == KeyEvent.KEY_PRESSED && eventKey.getKeyCode() == KeyEvent.VK_ENTER){
				figRotJcom.bonusToTrack();
			}else if(eventKey.getID() == KeyEvent.KEY_PRESSED
					&& (
							   eventKey.getKeyCode() == KeyEvent.VK_1
							|| eventKey.getKeyCode() == KeyEvent.VK_2
							|| eventKey.getKeyCode() == KeyEvent.VK_3
							|| eventKey.getKeyCode() == KeyEvent.VK_4
							|| eventKey.getKeyCode() == KeyEvent.VK_NUMPAD1
							|| eventKey.getKeyCode() == KeyEvent.VK_NUMPAD2
							|| eventKey.getKeyCode() == KeyEvent.VK_NUMPAD3
							|| eventKey.getKeyCode() == KeyEvent.VK_NUMPAD4
					)){
				figRotJcom.answered(eventKey.getKeyCode());
			}else{
			    keyNavigator.processKeyEvent(eventKey);
			}
		}
		
	    }
	}
    }


    public void addListener(Component c) {
	if (!listener) {
	    throw new IllegalStateException(J3dUtilsI18N.getString("Behavior0"));
	}
	c.addKeyListener(this);
    }


    public KeyNavigatorBehavior(TransformGroup targetTG) {
	keyNavigator = new KeyNavigator(targetTG);
    }

    public KeyNavigatorBehavior(FigureRotationJComponent c, TransformGroup targetTG) {
	this(targetTG);
	if (c != null) {
		figRotJcom = c;
	}
	//listener = true;	
    }

    public void keyPressed(KeyEvent evt) {
// 	System.out.println("keyPressed");

	// add new event to the queue
	// must be MT safe
	synchronized (eventq) {
	    eventq.add(evt);
	    // only need to post if this is the only event in the queue
	    if (eventq.size() == 1) postId(KeyEvent.KEY_PRESSED);
	}
    }

    public void keyReleased(KeyEvent evt) {
// 	System.out.println("keyReleased");

	// add new event to the queue
	// must be MT safe
	synchronized (eventq) {
	    eventq.add(evt);
	    // only need to post if this is the only event in the queue
	    if (eventq.size() == 1) postId(KeyEvent.KEY_RELEASED);
	}
    }

    public void keyTyped(KeyEvent evt) {}

}
