package distlog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.time.*;


public class Main extends JFrame implements ActionListener{
	JFrame f1;
	JTextField apptName;
	JRadioButton create;
	JRadioButton delete;
	JComboBox<Integer> hourBox;
	JComboBox<Integer> minuteBox;
	JComboBox<DayOfWeek> dayBox;
	JComboBox<Integer> durationHour;
	JComboBox<Integer> durationMinute;
//	JComboBox<Integer> monthBox;
	JRadioButton am;
	JRadioButton pm;
	
	private Main(){
		
	}
	
	//creates the gui. if int passed in == 1, no appt was entered
	// if int passed in == 2, appt created interferes with pre-existing one
	// if int passed in == 3, appt they're trying to delete didn't exist
	private void gui(int problem) throws FileNotFoundException{
		//TODO add text field that shows appointments
		f1 = new JFrame("Distributed Calendar");
		File file = new File("log.txt");
		Scanner in = new Scanner(file);
		String calendar = "";
		
		JTextArea txtArea = new JTextArea();
		txtArea.setBounds(25, 25, 950, 500);
		txtArea.setBorder(BorderFactory.createLineBorder(Color.black));
		while(in.hasNextLine()){
		    calendar += in.nextLine();
		    calendar += "\n";
		}
		txtArea.setText(calendar);
		in.close();
		
		if(problem == 1){
			JLabel probLabel = new JLabel("*** PLEASE ENTER AN APPOINTMENT NAME ***");
			probLabel.setBounds(350, 545, 300, 20);
			f1.add(probLabel);
		}else if(problem == 2){
			JLabel probLabel = new JLabel("*** Try Again: an appointment already exists at that time ***");
			probLabel.setBounds(350, 545, 300, 20);
			f1.add(probLabel);
		}else if(problem == 3){
			JLabel probLabel = new JLabel("*** Try Again: no appointment exists at that time ***");
			probLabel.setBounds(350, 545, 300, 20);
			f1.add(probLabel);
		}
		
		String text = "create or delete an appointment       ";
		text += "appointment name:                    ";
		text += "hour:           minute:                     ";
		text += "                    day:            month:";
		JLabel label = new JLabel(text);
		label.setBounds(25, 575, 970, 20);
		
		apptName = new JTextField(20);  //columns, rows
		apptName.setBounds(250, 605, 175, 20);  //x axis, y axis, width, height
		
		create = new JRadioButton("create");
		delete = new JRadioButton("delete");
		create.setBounds(50,600,80,30);
		delete.setBounds(125,600,80,30);
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(create);
		bg2.add(delete);
		create.setSelected(true);
		
		hourBox = new JComboBox<>();
		minuteBox = new JComboBox<>();
		for(int i = 1; i <= 12; i++){
			hourBox.addItem(new Integer(i));
		}
		for(int i = 1; i <= 60; i++){
			minuteBox.addItem(new Integer(i));
		}
		hourBox.setBounds(450, 600, 75, 30);
		minuteBox.setBounds(525, 600, 75, 30);
		
		am = new JRadioButton("am");
		pm = new JRadioButton("pm");
		am.setBounds(600,600,80,30);
		pm.setBounds(650,600,80,30);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(am);
		bg1.add(pm);
		am.setSelected(true);
		
		dayBox = new JComboBox<>(DayOfWeek.values()); //move this box to top of gui (displays the cur. day)
		dayBox.setBounds(740, 600, 75, 30);
		
//		monthBox = new JComboBox<>();
//		for(int i = 1; i <= 12; i++){
//			monthBox.addItem(new Integer(i));
//		}
//		monthBox.setBounds(815, 600, 75, 30);
		
		durationHour = new JComboBox<>();
		for(int i = 0; i <= 8; i++){
			durationHour.addItem(new Integer(i));
		}
		
		durationMinute = new JComboBox<>();
		durationMinute.addItem(0);
		durationMinute.addItem(30);
		
		JButton b = new JButton("Submit");
		b.setBounds(450,650,100, 40);  //x axis, y axis, width, height
		b.addActionListener(this);

		f1.add(txtArea);
		f1.add(label);
		f1.add(apptName);
		f1.add(create);
		f1.add(delete);
		f1.add(hourBox);
		f1.add(minuteBox);
		f1.add(b);
		f1.add(am);
		f1.add(pm);
		f1.add(dayBox);
//		f1.add(monthBox);
		
		f1.setSize(1000,735);  //width, height
		f1.setLayout(null);
		f1.setLocationRelativeTo(null);
		f1.setVisible(true);
		          
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//frame closes and another will be opened
		f1.dispose();
		String appt = apptName.getText();
		if(appt.equals("")){
			try {
				gui(1);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}else if(create.isSelected()){
			//TODO check if event already exists
//			if(event already exists){
//				gui(2);
//				return;
//			}else{
//				gui(0);
//				return;
//			}
		}else{
			//TODO check event they're trying to delete exists
//			if(!event exists){
//				gui(3);
//				return;
//			}else{
//				gui(0);
//				return;
//			}
		}
	}

//	public static void main(String[] args) throws IOException {
//		Main main = new Main(); //action listener needs an instance
//		//TODO figure out how to assign node IDs
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
//		main.gui(0);
//	}
}
