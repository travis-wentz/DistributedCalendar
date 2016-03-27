package distlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public Node(int totalNumberNodes) throws IOException{
		this.clock = 0;
		this.timeTable = new int[totalNumberNodes][totalNumberNodes];
		this.log = new ArrayList<Event>();
		this.calendar = new ArrayList<Appointment>();		
		this.nodeID = 1; // assign this dynamically somehow
		if(!new File("log.txt").exists()){
			//create the log file
			File logF = new File("log.txt");
			FileWriter logFile = new FileWriter(logF);
			logFile.close();
		}
		if(!new File("dictionary.txt").exists()){
			//create the log file
			File calendarF = new File("dictionary.txt");
			FileWriter dictionaryFile = new FileWriter(calendarF);
			dictionaryFile.close();
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
		System.out.println("*********CONFLICT RESOLUTION ENTERED**********");
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
					int bStart = calendar.get(i).getStartTime().getHour();
					bStart += calendar.get(i).getStartTime().getMinute();
					int bEnd = calendar.get(i).getEndTime().getHour();
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
				in.close();
				if(choice.equals(appt.getName()) || choice.equals(conflictingAppt)){
					ask = false;
				}
			}
			if(choice.equalsIgnoreCase(appt.getName())){
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
		int index = 0;
		Appointment deletedAppt = null;
		while(!found){
			if(calendar.get(index).getName().equals(name)){
				deletedAppt = calendar.get(index);
				calendar.remove(index);
				found = true;
			}else{
				index++;
			}
		}
		
		//create delete event and add to log
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
	
	
	// Uses UDP connection to send an XML log to the node specified by nodeID
	// Can change this to TCP if our logs get too big and UDP starts acting up...
	private void sendLog(int nodeID, String log) {
		InetAddress address;
		try {
			address = InetAddress.getByName(otherIPs.get(nodeID));
			byte[] buf = new byte[65000];
			buf = log.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		Node node = new Node(4);
		boolean mainMenu = true;
		String name;
		System.out.println("Welcome");
		Scanner in = new Scanner(System.in);
		while(mainMenu){
			boolean dayLoop = true;
			System.out.println();
			System.out.println("Choose what you would like to do:");
			System.out.println("1. View my calendar");
			System.out.println("2. Create an appointment");
			System.out.println("3. Delete an event");
			System.out.println("4. Exit");
			int choice = in.nextInt();
			//switch statement for what choice the user made
			switch(choice){
			case 1:
				node.printCalendar();
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
					if(eHour < 0 || eHour > 24){
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
				node.eventInsert(name, apptDay, start, end, participants);
				break;
			case 3:
				System.out.println("Enter event name: ");
				name = in.nextLine();
				name += in.nextLine();
				node.eventDelete(name);
				break;
			case 4:
				System.out.println("goodbye");
				mainMenu = false;
				break;
			default:
				System.out.println("please choose a valid menu option");
				tryAgain = true;
				break;
			}
		}
		in.close();
		//if the log file doesn't exist, create it
		//else open it and read it
//		if(!new File("log.txt").exists()){
//			//create the log file
//			File log = new File("log.txt");
//			FileWriter logFile = new FileWriter(log);
//
//			//create a new event
//			ArrayList<Event> list = new ArrayList<Event>();
//			LocalTime start = LocalTime.of(7, 30);
//			LocalTime end = LocalTime.of(14, 30);
//			int[] participants = {1,2};
//			Appointment a = new Appointment("Lunch", DayOfWeek.FRIDAY, start, end, participants);
//			Event e = new Event(a, "Create");
//			list.add(e);
//			
//			start = LocalTime.of(9, 30);
//			end = LocalTime.of(7, 30);
//			a = new Appointment("Dinner", DayOfWeek.FRIDAY, start, end, participants);
//			e = new Event(a, "Create");
//			list.add(e);
//			
//			//save new event to a string & save it to log file
//			XStream xstream = new XStream(new StaxDriver());
//			String xml = xstream.toXML(list.get(0));
//			System.out.println(xml);
//			xml = xstream.toXML(list.get(1));
//			System.out.println(xml);
//			
//			xml = xstream.toXML(list);
//			logFile.write(xml);
//			
//			logFile.close(); //close the log file
//		}else{
//			//open and read the file
//			FileInputStream fstream = new FileInputStream("log.txt");
//			BufferedReader logFile = new BufferedReader(new InputStreamReader(fstream));
//			String xml = logFile.readLine();
//			//System.out.println(xml);
//			
//			XStream xstream = new XStream(new StaxDriver());
//			ArrayList<Event> list = new ArrayList<Event>();
//			list = (ArrayList<Event>) xstream.fromXML(xml);
//			//Event x = (Event) xstream.fromXML(xml);
//			
//			Event x = list.get(0);
//			System.out.println(x.getAppointment().getName());
//			System.out.println(x.getAppointment().getDay().toString());
//			System.out.println(x.getAppointment().getStartTime().toString());
//			System.out.println(x.getAppointment().getEndTime().toString());
//			
//			System.out.println();
//			
//			x = list.get(1);
//			System.out.println(x.getAppointment().getName());
//			System.out.println(x.getAppointment().getStartTime().toString());
//			System.out.println(x.getAppointment().getEndTime().toString());
//			
//			logFile.close(); //close the file
//		}
	}

}
