import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;


public class Main extends JFrame implements ActionListener{
	private Main(){
		
	}
	
	//creates the gui
	private void gui(){
		//TODO add text field that shows appointments
		JFrame f1 = new JFrame("Distributed Calendar");
		
		
		//TODO add day box
		//TODO add month box
		//TODO add year box?
		JComboBox hourBox = new JComboBox();
		JComboBox minuteBox = new JComboBox();
		for(int i = 1; i < 13; i++){
			//hourBox.addItem(i);
		}
		for(int i = 1; i < 61; i++){
			//minuteBox.addItem(i);
		}
		//TODO add jlabels for all of the above
		
		JRadioButton am = new JRadioButton("am");
		JRadioButton pm = new JRadioButton("pm");
		am.setBounds(800,230,80,30);
		pm.setBounds(800,260,80,30);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(am);
		bg1.add(pm);
		am.setSelected(true);
		
		JButton b = new JButton("go");
		b.setBounds(450,600,100, 40);  //x axis, y axis, width, height
		b.addActionListener(this);
		
		f1.add(b);
		f1.add(am);
		f1.add(pm);
		
		f1.setSize(1000,735);  //width, height
		f1.setLayout(null);
		f1.setLocationRelativeTo(null);
		f1.setVisible(true);
		          
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//send info from combo boxes and radio buttons
		//close gui(altering gui at runtime is incredably complex)
		//add or delete
		//call gui again (this will show the updated log)
	}

	public static void main(String[] args) throws IOException {
		Main main = new Main();
		//TODO figure out how to assign node IDs
//		int tempNodeIdAssigner = 0;
//		
//		//if the log doesn't exist, create it
//		//else open it and get the node ID
//		if(!new File("log.txt").exists()){
//			File log = new File("log.txt");
//			FileWriter logFile = new FileWriter(log);
//			String line = "Node ID: ";
//			tempNodeIdAssigner++;
//			line += tempNodeIdAssigner;
//			logFile.write(line);
//			logFile.close();
//		}else{
//			FileInputStream fstream;
//			fstream = new FileInputStream("log.txt");
//			BufferedReader logFile = new BufferedReader(new InputStreamReader(fstream));
//			String line = logFile.readLine();
//			Pattern pattern = Pattern.compile("\\d+");
//	        Matcher match = pattern.matcher(line); //getting node id num from file
//	        match.find();
//	        tempNodeIdAssigner = Integer.parseInt(match.group()); //recording id num
////	        System.out.println(line);
////	        System.out.println(tempNodeIdAssigner);
//	        logFile.close();
//		}
//		Node node = new Node(tempNodeIdAssigner);
		main.gui();
	}
}
