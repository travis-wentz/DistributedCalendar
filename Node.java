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
import java.util.HashMap;
import java.util.Scanner;
import java.net.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/*
 * NOTE: an object cannot be added to the end of an arraylist (will result in out of bounds error)
 * 		 I think we are going to have to add it to the beginning of the list, then sort.
 */
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
			File log = new File("log.txt");
			FileWriter logFile = new FileWriter(log);
			logFile.close();
		}
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
	
	//TODO
	public void eventAdd(String name, DayOfWeek day, LocalTime start, LocalTime end, int[] part){
		
	}
	
	//TODO
	public void eventDelete(String name){
		
	}
	
	//returns the log of events (aray list)
	public ArrayList<Event> getLog() {
		return log;
	}

	public void setLog(ArrayList<Event> log) {
		this.log = log;
	}
	
	//TODO
	public void addAppointment(){
		
	}
	

	//appointments (dictionary part of the log problem)
	public ArrayList<Appointment> getCalendar() {
		return calendar;
	}

	public void setCalendar(ArrayList<Appointment> calendar) {
		this.calendar = calendar;
	}
	
	public void printCalendar(){
		DayOfWeek curDay = DayOfWeek.MONDAY;
		System.out.println("MONDAY");
		for(int i = 0; i < calendar.size(); i++){
			Appointment curAppt = calendar.get(i);
			if(curAppt.getDay() != curDay){
				curDay = calendar.get(i).getDay();
				System.out.println(curDay);
			}
			System.out.println(curAppt.getName());
			System.out.println("start - " + curAppt.getStartTime());
			System.out.println("end  -  " + curAppt.getEndTime());
		}
	}
	
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
		boolean tryAgain = true;
		String name;
		System.out.println("Welcome");
		Scanner in = new Scanner(System.in);
		do{
			boolean dayLoop = true;
			System.out.println("Choose what you would like to do:");
			System.out.println("1. View my calendar");
			System.out.println("2. Create an appointment");
			System.out.println("3. Delete an event");
			int choice = in.nextInt();
			//switch statement for what choice the user made
			switch(choice){
			case 1:
				node.printCalendar();
				tryAgain = false;
				break;
			case 2:
				DayOfWeek apptDay = DayOfWeek.MONDAY;
				System.out.println("Enter event name: ");
				name = in.nextLine();
				while(dayLoop){
					System.out.println("Enter day: ");
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
				//TODO put in loops for all these in case the entered an invalid time
				System.out.println("Enter start time hour (24 hour clock): ");
				int sHour = in.nextInt();
				System.out.println("Enter start time minute (0 or 30): ");
				int sMinute = in.nextInt();
				System.out.println("Enter end time hour (24 hour clock): ");
				int eHour = in.nextInt();
				System.out.println("Enter end time minute (0 or 30): ");
				int eMinute = in.nextInt();
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
				node.eventAdd(name, apptDay, start, end, participants);
				tryAgain = false;
				break;
			case 3:
				System.out.println("Enter event name: ");
				name = in.nextLine();
				node.eventDelete(name);
				tryAgain = false;
				break;
			default:
				System.out.println("please choose a valid menu option");
				tryAgain = true;
				break;
			}
		}while(tryAgain);
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
