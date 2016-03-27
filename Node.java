package distlog;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.net.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class Node {
	private final int nodeID;
	private int clock;
	private int[][] timeTable;
	private ArrayList<Event> log;
	private ArrayList<Appointment> calendar;
	private HashMap<Integer, String> otherIPs;
	private XStream xstream;
	
	public Node(int totalNumberNodes) throws IOException{
		this.clock = 0;
		this.timeTable = new int[totalNumberNodes][totalNumberNodes];
		this.log = new ArrayList<Event>();
		this.calendar = new ArrayList<Appointment>();		
		this.nodeID = 0; // assign this dynamically somehow
		this.otherIPs = new HashMap<Integer, String>();
		this.xstream = new XStream(new StaxDriver());
		
		if(!new File("log.txt").exists()){
			//create the log file
			File logF = new File("log.txt");
			FileWriter logFile = new FileWriter(logF);
			logFile.close();
		} else {
			// read existing log
		}
		
		if(!new File("dictionary.txt").exists()){
			//create the log file
			File calendarF = new File("dictionary.txt");
			FileWriter dictionaryFile = new FileWriter(calendarF);
			dictionaryFile.close();
		} else {
			// read existing calendar
		}
		//TODO make it read existing log and dictionary to simulate what happens upon failure
	}

	//logical time (int counter)
	public int getClock() {
		return clock;
	}

//	public void setClock(int clock) {
//		this.clock = clock;
//	}

	//if Main is in Node, we won't need time_table getter or setter
	//so you know what info other nodes have and what you don't need to send
	public int[][] getTime_table() {
		return timeTable;
	}

	public void setTime_table(int[][] time_table) {
		this.timeTable = time_table;
	}
	
	
	public void eventInsert(String name, DayOfWeek day, LocalTime start, LocalTime end, int[] part) throws IOException{
		//increment clock
		clock++;
		
		//create appointment and event
		Appointment appointment = new Appointment(name, day, start, end, part);
		Event event = new Event(appointment, "insert");
		//check for conflicts
//		boolean addEvent = conflictResolution(appointment);
//		if(addEvent){
		if(conflictResolution(appointment)){
			this.log.add(event); //add event to events log
			this.calendar.add(appointment); //add appointment to calendar
			Collections.sort(calendar); //sort calendar
			
			//TODO update time table?
			
			//save log file to hard drive
			FileWriter logFile = new FileWriter("log.txt");
			XStream xstream = new XStream(new StaxDriver());
			String xml = xstream.toXML(log);
			xml = xstream.toXML(log);
			logFile.write(xml);
			logFile.close(); //close the log file
			
			//save calendar file to hard drive
			FileWriter dictionaryFile = new FileWriter("dictionary.txt");
			XStream xstream2 = new XStream(new StaxDriver());
			String xml2 = xstream.toXML(calendar);
			xml2 = xstream2.toXML(calendar);
			dictionaryFile.write(xml2);
			dictionaryFile.close(); //close the log file
		}
	}
	
	//returns true if appointment is to be made (no conflict or original appt is to be deleted and new created)
	public boolean conflictResolution(Appointment appt) throws IOException{
		String conflictingAppt = null;
		boolean conflictFound = false;
		DayOfWeek aDay = appt.getDay();
		int aStart = appt.getStartTime().getHour() * 100; // b/c logical operators can't be used on LocalTime
		aStart += appt.getStartTime().getMinute();
		int aEnd = appt.getEndTime().getHour() * 100;
		aEnd += appt.getEndTime().getMinute();
		
		for(int i = 0; i < calendar.size(); i++){
			//if events are on the same day
			if(aDay == calendar.get(i).getDay()){
				//if events have same member
				boolean sameMember = false;
				int[] aParticipants = appt.getParticipants();
				int[] bParticipants = calendar.get(i).getParticipants();
				for(int j = 0; j < aParticipants.length; j++){
					for(int k = 0; k < bParticipants.length; k++){
						if(aParticipants[j] == bParticipants[k]){
							sameMember = true;
							break;
						}
					}
				}
				if(sameMember){
					//if events are at conflicting times
					int bStart = calendar.get(i).getStartTime().getHour() * 100;
					bStart += calendar.get(i).getStartTime().getMinute();
					int bEnd = calendar.get(i).getEndTime().getHour() * 100;
					bEnd += calendar.get(i).getEndTime().getMinute();
					if((aStart >= bStart && aStart < bEnd)  || (aEnd > bStart && aEnd <= bEnd)){
						conflictingAppt = calendar.get(i).getName();
						conflictFound = true;
						break;
					}
				}
			}
		}
		if(conflictFound){
			boolean ask = true;
			String choice = null;
			System.out.println("Your new appointment " + appt.getName() 
					+ " conflicts with an existing appointment " + conflictingAppt);
			while(ask){
				System.out.println("Please type the name of the appointment you would like to delete: ");
				Scanner in = new Scanner(System.in);
				choice = in.nextLine();
				if(choice.equals(appt.getName()) || choice.equals(conflictingAppt)){
					ask = false;
				}
				in.close();
			}
			if(choice.equals(appt.getName())){
				//all we have to do is return b/c event hasn't actually been added anywhere
				return false;
			}else{
				//delete old event and create new one
				eventDelete(choice);
				return true;
			}
		}else{
			return true;
		}
	}
	
	public void eventDelete(String name) throws IOException{
		//increment clock
		clock++;
		
		//find and delete appt (appts are assumed to have unique names)
		boolean found = false;
		Appointment deletedAppt = null;
		for(int i = 0; i < calendar.size(); i++) {
			if(calendar.get(i).getName().equals(name)){
				deletedAppt = calendar.get(i);
				calendar.remove(i);
				found = true;
			}
		}
		
		//create delete event and add to log if event was found
		if(found){
			Event deleteEvent = new Event(deletedAppt, "delete");
			log.add(deleteEvent);
			System.out.println("The appointment " + name + " was deleted.");
			
			//TODO update time table?
			
			//save log file to hard drive
			FileWriter logFile = new FileWriter("log.txt");
			XStream xstream = new XStream(new StaxDriver());
			String xml = xstream.toXML(log);
			xml = xstream.toXML(log);
			logFile.write(xml);
			logFile.close(); //close the log file
			
			//save calendar file to hard drive
			FileWriter dictionaryFile = new FileWriter("dictionary.txt");
			XStream xstream2 = new XStream(new StaxDriver());
			String xml2 = xstream.toXML(calendar);
			xml2 = xstream2.toXML(calendar);
			dictionaryFile.write(xml2);
			dictionaryFile.close(); //close the log file
		} else { // If it wasn't found alert user
			System.out.println("No appointment found by that name");
		}
	}
	
	//TODO do we need this method?
	public ArrayList<Event> getLog() {
		return log;
	}

	public void setLog(ArrayList<Event> log) {
		this.log = log;
	}

	//TODO do we need this?
	public ArrayList<Appointment> getCalendar() {
		return calendar;
	}

	public void setCalendar(ArrayList<Appointment> calendar) {
		this.calendar = calendar;
	}
	
	public void printCalendar(){
		DayOfWeek curDay = DayOfWeek.MONDAY;
		for(int i = 0; i < calendar.size(); i++){
			Appointment curAppt = calendar.get(i);
			if(i == 0 || curAppt.getDay() != curDay){
				System.out.println();
				curDay = calendar.get(i).getDay();
				System.out.println(curDay);
			}
			System.out.println(curAppt.getName().toString());
			System.out.println("start - " + curAppt.getStartTime());
			System.out.println("end  -  " + curAppt.getEndTime());
			System.out.print("participants - ");
			for(int j = 0; j < curAppt.getParticipants().length; j++){
				if(j != 0){
					System.out.print(", " + curAppt.getParticipants()[j]);
				}else{
					System.out.print(curAppt.getParticipants()[j]);
				}
			}
			System.out.println();
		}
	}
	
	// Checks the local time table to see if one of the nodes already has
	// the record of a particular event, used to create partial logs
	private boolean hasRec(Event event, int nodeID){
		return timeTable[nodeID][event.getNodeID()] >= event.getTime();
	}
	
	// Method that runs when a new partial log is received from another node
	// This method is called from the TCPListener thread when a new message is received
	public void receiveMessage(String xml, int[][] table){
		ArrayList<Event> partialLog = (ArrayList<Event>) xstream.fromXML(xml);
		ArrayList<Event> newLog = new ArrayList<Event>();
		for(int i = 0; i < partialLog.size(); i++){
			if(!hasRec(partialLog.get(i), nodeID)){
				newLog.add(partialLog.get(i));
			}
		}
		
		//TODO:  update calendar, update TimeTable, update Log
	}
	
	// Uses TCP connection to send an XML log to the node specified by nodeID
	// Also sends the local copy of the TimeTable[][]
	private void sendLog(int nodeID, String log) {
		InetAddress address;
		Socket socket = null;
		try {
			address = InetAddress.getByName(otherIPs.get(nodeID));
			socket = new Socket(address, 4445);
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream output = new DataOutputStream(bos);
			
			// send XML log first
			output.writeInt(log.getBytes().length);
			output.writeBytes(log);
			
			// send TimeTable next
			output.writeInt(timeTable.length);
			for(int i = 0; i < timeTable.length; i++){
				for(int j = 0; j < timeTable.length; j++){
					output.writeInt(timeTable[i][j]);
				}
			}
			output.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void menu() throws IOException{
		String name;
		Scanner in = new Scanner(System.in);
		boolean dayLoop = true;
		System.out.println();
		System.out.println("Choose what you would like to do:");
		System.out.println("1. View my calendar");
		System.out.println("2. Create an appointment");
		System.out.println("3. Delete an event");
		System.out.println("4. Exit");
		int choice = in.nextInt();
		switch(choice){
		case 1:
			printCalendar();
			menu();
			break;
		case 2:
			DayOfWeek apptDay = DayOfWeek.MONDAY;
			System.out.println("Enter event name: ");
			name = in.nextLine();
			name += in.nextLine();
			System.out.println("Enter day: ");
			while(dayLoop){
				String apptDayString = in.nextLine().toUpperCase();
				switch(apptDayString){
				case "MONDAY":
					apptDay = DayOfWeek.MONDAY;
					dayLoop = false;
					break;
				case "TUESDAY":
					apptDay = DayOfWeek.TUESDAY;
					dayLoop = false;
					break;
				case "WEDNESDAY":
					apptDay = DayOfWeek.WEDNESDAY;
					dayLoop = false;
					break;
				case "THURSDAY":
					apptDay = DayOfWeek.THURSDAY;
					dayLoop = false;
					break;
				case "FRIDAY":
					apptDay = DayOfWeek.FRIDAY;
					dayLoop = false;
					break;
				case "SATURDAY":
					apptDay = DayOfWeek.SATURDAY;
					dayLoop = false;
					break;
				case "SUNDAY":
					apptDay = DayOfWeek.SUNDAY;
					dayLoop = false;
					break;
				default:
					System.out.println("Enter the day of week again. Spell it right this time: ");
					dayLoop = true; //just in case
					break;
				}
			}
			boolean tryAgain = false;
			int sHour, sMinute, eHour, eMinute;
			sHour = sMinute = eHour = eMinute = 0;
			do{
				System.out.println("Enter start time hour (24 hour clock): ");
				sHour = in.nextInt();
				if(sHour < 0 || sHour > 24){
					tryAgain = true;
				}else{
					tryAgain = false;
				}
			}while(tryAgain);
			do{
				System.out.println("Enter start time minute (0 or 30): ");
				sMinute = in.nextInt();
				if(sMinute != 0 && sMinute != 30){
					tryAgain = true;
				}else{
					tryAgain = false;
				}
			}while(tryAgain);
			do{
				System.out.println("Enter end time hour (24 hour clock): ");
				eHour = in.nextInt();
				if(eHour < 0 || eHour > 24 || eHour < sHour){
					tryAgain = true;
				}else{
					tryAgain = false;
				}
			}while(tryAgain);
			do{
				System.out.println("Enter end time minute (0 or 30): ");
				eMinute = in.nextInt();
				if(eMinute != 0 && eMinute != 30){
					tryAgain = true;
				}else{
					tryAgain = false;
				}
			}while(tryAgain);
			System.out.println("Number of participants: ");
			int numPart = in.nextInt();
			int[] participants = new int[numPart];
			for(int i = 0; i < numPart; i++){
				if(i == 0){
					System.out.println("Enter first participant: ");
				}else{
					System.out.println("Enter next participant: ");
				}
				participants[i] = in.nextInt();
			}
			LocalTime start = LocalTime.of(sHour, sMinute);
			LocalTime end = LocalTime.of(eHour, eMinute);
			eventInsert(name, apptDay, start, end, participants);
			menu();
			break;
		case 3:
			System.out.println("Enter event name: ");
			String blank = in.nextLine(); // Consume newline character
			name = in.nextLine();
			eventDelete(name);
			menu();
			break;
		case 4:
			System.out.println("goodbye");
			break;
		default:
			System.out.println("please choose a valid menu option");
			menu();
			break;
		}
		in.close();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome");
		Node node = new Node(4);
//		TCPListener tcplistener = new TCPListener(node);
//		tcplistener.start();
		node.otherIPs.put(1, "54.152.162.118");
		node.menu();
//		tcplistener.shutDown();
	}

}
