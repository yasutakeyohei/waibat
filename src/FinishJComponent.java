import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JComponent;

public class FinishJComponent extends JComponent implements ActionListener, Runnable {
	private int w, h;
	private int stl_1, stl_2, sth, stha;
	private Font font;
	private JLabel label, errorLabel;
	private JPanel jp;
	private JTextField jt, ja;
	private int totalScore = -1;
	
	WaibatApplet wa;
	TrackingJComponent tj;
	JButton jb1;

	public FinishJComponent(WaibatApplet wa, TrackingJComponent tj){
		super();
		this.tj = tj;
		this.wa = wa;
		
		//jb1 = new JButton("Click to start again");

		try{
			ImageIcon icon = new ImageIcon(getClass().getResource("/wt.gif"));
			label = new JLabel(icon);
			label.setBackground(Color.white);
			errorLabel = new JLabel("Error:submit again");
			errorLabel.setForeground(Color.red);
			jt = new JTextField("–¼–³‚µ‚³‚ñ", 5);
			ja = new JTextField(12);
			JButton jb = new JButton("Submit");
			jp = new JPanel();
			jp.setBackground(Color.black);
			jp.setLayout(new BorderLayout());
			JPanel jp0 = new JPanel();
			JLabel jl0 = new JLabel("name");
			jl0.setForeground(Color.yellow);
			jp0.add(jl0);
			jp0.add(jt);
			jp0.setBackground(Color.black);
			JPanel jp1 = new JPanel();
			JLabel jl1 = new JLabel("comment");
			jl1.setForeground(Color.yellow);
			jp1.add(jl1);
			jp1.add(ja);
			jp1.setBackground(Color.black);
			JPanel jp2 = new JPanel();
			jp2.add(jb);
			jp2.add(label);
			jp2.add(errorLabel);
			jp2.setBackground(Color.black);
			label.setVisible(false);
			errorLabel.setVisible(false);
			
			jp.add(jp0, BorderLayout.NORTH);
			jp.add(jp1, BorderLayout.CENTER);
			jp.add(jp2, BorderLayout.SOUTH);
			//jp.setPreferredSize(new Dimension(100, 120));
			
			setLayout(new BorderLayout());
			//add(new JLabel(" Test finished. Your Score is:"), BorderLayout.NORTH);
			add(jp, BorderLayout.SOUTH);
			
			//add(jb, BorderLayout.SOUTH);
			//add(new JLabel(" Press Refresh Button to start again"),BorderLayout.SOUTH);
			//add(jb1);
			jb.addActionListener(this);
		}catch(Exception e){
			System.out.println(e);
		}
		

	}

	public void run() {
		// TODO Auto-generated method stub

	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(w == 0){
			w = getWidth();
			h = getHeight();
			font = new Font("Serif" , Font.BOLD , (int)(0.05f*w));
			g.setFont(font);
	        FontMetrics fm = g.getFontMetrics();
	        stl_1 	= fm.stringWidth("Test Finished.");
	        stl_2 	= fm.stringWidth("Your Score: 00");
	        sth 	= fm.getHeight();
	        stha	= fm.getAscent();
		}
		if(totalScore == -1) totalScore = (int)(wa.getTotalScore()*100);
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.yellow);
		g.setFont(font);
		g.drawString("Test Finished.", (int)(w-stl_1)/2, (int)(h*0.1));
		g.drawString("Your Score: " + totalScore, (int)(w-stl_2)/2, (int)(h*0.3));
		
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void actionPerformed(ActionEvent e){
		submitScore();
	}
	
	public synchronized void bonusShow(){
		this.requestFocus();
	}
	
	@SuppressWarnings("deprecation")
	public void submitScore(){
        String urlString = "http://localhost/postScore.php";
        label.setVisible(true);
        try {
            URL url = new URL(urlString);
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("User-Agent", "waibat");
            uc.setRequestProperty("Accept-Language", "ja");
            OutputStream os = uc.getOutputStream();        
            PrintStream ps = new PrintStream(os);
            ps.print("s="+totalScore+"&n="+jt.getText()+":"+ja.getText()+" ");
            ps.close();

            InputStream is = uc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String s;
            String[][] tableData= new String[100][3];
            int i = 0;
            while ((s = reader.readLine()) != null) {
                //System.out.println("s=" + s.split(":").length);
            	if(i < 100){
	                if(s.split(":").length > 2){
	                	tableData[i][0] = s.split(":")[0];
	                	tableData[i][1] = s.split(":")[1];
	                	tableData[i][2] = s.split(":")[2];
	                	i++;
	                }
            	}
           	}
            reader.close();
        	if(i == 0){
        		errorLabel.setVisible(true);
        	}else{
        		jp.setVisible(false);
        		remove(jp);
        		String[] columns = {"score","name","comment"};
        		DefaultTableModel tm = new DefaultTableModel();
        		tm.setColumnIdentifiers(columns);
        		for(int j = 0; j < i; j++){
        			tm.addRow(tableData[j]);
        		}
        		JTable jtable = new JTable(tm);
        		//JTable jtable = new JTable(tableData, columns);
        		jtable.setBackground(Color.black);
        		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        		jtable.setDragEnabled(false);
                jtable.setColumnSelectionAllowed(false);
                jtable.setRowSelectionAllowed(false);
        		jtable.setShowGrid(false);
        		jtable.setForeground(Color.yellow);
        		jtable.setEnabled(false);
        		
        		DefaultTableColumnModel columnModel = (DefaultTableColumnModel)jtable.getColumnModel();
        		TableColumn column = null;
       			columnModel.getColumn(0).setPreferredWidth(getWidth()/6);
       			columnModel.getColumn(1).setPreferredWidth(getWidth()*2/6);
       			columnModel.getColumn(2).setPreferredWidth(getWidth()*3/6);

        		JScrollPane jpane = new JScrollPane(jtable);
        		jpane.setBackground(Color.black);
        		jpane.setPreferredSize(new Dimension(getWidth(), getHeight()/2));
        		add(jpane, BorderLayout.SOUTH);
        	}



            /*
            System.out.println(sa[0]);
            Arrays.sort(sa, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
            		System.out.println(o1+";"+o2);
                	if(o1 != null && o2 != null){
                		String s1 = (String)o1;
                		String s2 = (String)o2;
                		int n1 = Integer.parseInt(s1.split(":")[0]);
                		int n2 = Integer.parseInt(s2.split(":")[0]);
                		System.out.println(n1+":"+n2);
                		return n1-n2;
                	}else{
                		return 0;
                	}
                }});
                */
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL format: " + urlString);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Can't connect to " + urlString);
            System.exit(-1);
        }
        label.setVisible(false);
    }
}
