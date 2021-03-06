package distlog;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class Node {
	private int nodeID;
	private int clock;
	private int totalNodes;
	private int[][] timeTable;
	private ArrayList<Event> log;
	private ArrayList<Appointment> calendar;
	private HashMap<Integer, String> otherIPs;
	private XStream xstream;
	
	@SuppressWarnings("unchecked")
	public Node(int totalNumberNodes) throws IOException{
		this.totalNodes = totalNumberNodes;
		this.clock = 0;
		this.timeTable = new int[totalNumberNodes][totalNumberNodes];
		this.log = new ArrayList<Event>();
		this.calendar = new ArrayList<Appointment>();
		this.otherIPs = new HashMap<Integer, String>();
		this.xstream = new XStream(new StaxDriver());
		
		if(!new File("log.txt").exists()){
			//create the log file
			File logF = new File("log.txt");
			FileWriter logFile = new FileWriter(logF);
			logFile.close();
		} else {
			byte[] encoded = Files.readAllBytes(Paths.get("log.txt"));
			String xml = new String(encoded, Charset.defaultCharset());
			log = (ArrayList<Event>) xstream.fromXML(xml);
		}
		
		if(!new File("dictionary.txt").exists()){
			//create the log file
			File calendarF = new File("dictionary.txt");
			FileWriter dictionaryFile = new FileWriter(calendarF);
			dictionaryFile.close();
		} else {
			byte[] encoded = Files.readAllBytes(Paths.get("dictionary.txt"));
			String xml = new String(encoded, Charset.defaultCharset());
			calendar = (ArrayList<Appointment>) xstream.fromXML(xml);
		}
	}
	
	// Set the node ID, I removed FINAL from nodeID so we can assign this to each node when we start them
	public void setID(int id){
		this.nodeID = id;
	}

	//logical time (int counter)
	public int getClock() {
		return clock;
	}

	//if Main is in Node, we won't need time_table getter or setter
	//so you know what info other nodes have and what you don't need to send
	public int[][] getTime_table() {
		return timeTable;
	}

	public void setTime_table(int[][] time_table) {
		this.timeTable = time_table;
	}
	
	public void saveToDisk() throws IOException{
		//save log file to hard drive
		FileWriter logFile = new FileWriter("log.txt");
		String xml = xstream.toXML(log);
		logFile.write(xml);
		logFile.close(); //close the log file
		
		//save calendar file to hard drive
		FileWriter dictionaryFile = new FileWriter("dictionary.txt");
		String xml2 = xstream.toXML(calendar);
		dictionaryFile.write(xml2);
		dictionaryFile.close(); //close the log file
	}
	
	
	public void eventInsert(String name, DayOfWeek day, LocalTime start, LocalTime end, int[] part) throws IOException{
		
		//create appointment and event
		Appointment appointment = new Appointment(name, day, start, end, part);
		Event event = new Event(appointment, "insert", clock+1, nodeID);
		
		//check for conflicts
		if(conflictResolution(appointment)){
			//increment clock
			clock++;
			this.log.add(event); //add event to events log
			this.calendar.add(appointment); //add appointment to calendar
			Collections.sort(calendar); //sort calendar
			
			// update timetable
			timeTable[nodeID][nodeID] = clock;
			
			// save
			saveToDisk();
			
			// If other nodes need to be notified, create partial log and send
			if(part.length > 1){
				ArrayList<Event> partialLog = new ArrayList<Event>();
				// Create partial log for each recipient i
				for(int i = 0; i < part.length; i++){
					if(nodeID != part[i]){
						// Iterate through local log
						for(int j = 0; j < log.size(); j++){
							if(!hasRec(log.get(j), part[i])){
								partialLog.add(log.get(j));
							}
						}
						// Convert to XML and send
						String sendLog = xstream.toXML(partialLog);
						sendLog(part[i], sendLog);
						// Clear Partial Log for next recipient
						partialLog.clear();
					}
				}
			}
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
			System.out.println();
			System.out.println("WARNING:");
			System.out.println("Your new appointment " + appt.getName() 
					+ " conflicts with an existing appointment " + conflictingAppt);
			System.out.println("Please try again or delete existing appointment.");
			return false;
		}else{
			return true;
		}
	}
	
	public void eventDelete(String name) throws IOException{
				
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
			//increment clock
			clock++;
			Event deleteEvent = new Event(deletedAppt, "delete", clock, nodeID);
			log.add(deleteEvent);
			System.out.println("The appointment " + name + " was deleted.");
			
			timeTable[nodeID][nodeID] = clock;
			
			saveToDisk();
			
			//if other nodes were involved, build partial log and send
			// If other nodes need to be notified, create partial log and send
			if(deletedAppt.getParticipants().length > 1){
				ArrayList<Event> partialLog = new ArrayList<Event>();
				// Create partial log for each recipient i
				for(int i = 0; i < deletedAppt.getParticipants().length; i++){
					if(nodeID != i){
						// Iterate through local log
						for(int j = 0; j < log.size(); j++){
							if(!hasRec(log.get(j), i)){
								partialLog.add(log.get(j));
							}
						}
						// Convert to XML and send
						String sendLog = xstream.toXML(partialLog);
						sendLog(i, sendLog);
						// Clear Partial Log for next recipient
						partialLog.clear();
					}
				}
			}
		} else { // If it wasn't found alert user
			System.out.println("No appointment found by that name");
		}
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
	// the record of a particular event, used to create partial logs before sending
	private boolean hasRec(Event event, int nodeID){
		boolean has = timeTable[nodeID][event.getNodeID()] >= event.getTime();
		return has;
	}
	
	// Method that runs when a new partial log is received from another node
	// This method is called from the TCPListener thread when a new message is received
	public void receiveMessage(int senderID, String xml, int[][] table) throws IOException{
		
		// Assemble Log of events we have no record of
		ArrayList<Event> receivedLog = (ArrayList<Event>) xstream.fromXML(xml);
		ArrayList<Event> partialLog = new ArrayList<Event>();
		for(int i = 0; i < receivedLog.size(); i++){
			if(!hasRec(receivedLog.get(i), nodeID)){
				partialLog.add(receivedLog.get(i));
			}
		}
		
		// Update calendar(dictionary) by iterating through received log and adding/deleting
		for(int i = 0; i < partialLog.size(); i++) {
			Event event = (Event)partialLog.get(i);
			if(event.getOp().equals("insert")){
				// Check for conflicts
				if(conflictResolution(event.getAppointment())){
					calendar.add(event.getAppointment());
					Collections.sort(calendar);
				} else {
					System.out.println("Incoming conflict, need resolution protocol");
					// TODO:  standardize conflict resolution? create a delete event and send back?
					/*ArrayList<Event> deleteEvent = new ArrayList<Event>();
					clock++;
					Event deletion = new Event(event.getAppointment(), "delete", clock, nodeID);
					log.add(deletion);
					String conflictResolution = xstream.toXML(log);
					sendLog(senderID, conflictResolution);*/
				}
			} else {
				// delete events
				//find and delete appt (appts are assumed to have unique names)
				boolean found = false;
				for(int j = 0; j < calendar.size(); j++) {
					if(calendar.get(j).getName().equals(event.getAppointment().getName())){
						calendar.remove(j);
						found = true;
					}
				}
			}
		}
		
		// update TimeTable
		/*System.out.println("Updating Time tables");
		System.out.println("Local Table:");
		for(int i = 0; i < totalNodes; i++){
			for(int j = 0; j < totalNodes; j++){
				System.out.print(timeTable[i][j]);
			}
			System.out.println("");
		}
		System.out.println("Printing received timetable:" + senderID);
		for(int i = 0; i < totalNodes; i++){
			for(int j = 0; j < totalNodes; j++){
				System.out.print(table[i][j]);
			}
			System.out.println("");
		}*/
		
		for(int i = 0; i < totalNodes; i++){
			timeTable[nodeID][i] = Math.max(timeTable[nodeID][i], table[senderID][i]);
		}
		for(int i = 0; i < totalNodes; i++){
			for(int j = 0; j < totalNodes; j++){
				timeTable[i][j] = Math.max(timeTable[i][j], table[i][j]);
			}
		}
		
		// Rebuild local log, discard events that all nodes know of
		ArrayList<Event> newLog = new ArrayList<Event>();
		// Go through local log
		for(int i = 0; i < log.size(); i++){
			Event event = (Event)log.get(i);
			for(int j = 0; j < totalNodes; j++){
				if(!hasRec(event, j)){
					newLog.add(event);
				}
			}
		}
		// Go through partial log
		for(int i = 0; i < partialLog.size(); i++){
			Event event = (Event)partialLog.get(i);
			for(int j = 0; j < totalNodes; j++){
				if(!hasRec(event, j)){
					newLog.add(event);
				}
			}
		}
		log = newLog;
		
		// Save!
		saveToDisk();
		
	}
	
	// Uses TCP connection to send an XML log to the node specified by nodeID
	// Also sends the local copy of the TimeTable[][]
	private void sendLog(int destination, String log) {
		InetAddress address;
		Socket socket = null;
		try {
			address = InetAddress.getByName(otherIPs.get(destination));
			socket = new Socket(address, 4445);
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream output = new DataOutputStream(bos);
			
			// send ID
			output.writeInt(nodeID);
			
			// send XML log
			output.writeInt(log.getBytes().length);
			output.writeBytes(log);
			
			// send TimeTable
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
		boolean menu = true;
		do{
			String name;
			Scanner in = new Scanner(System.in); //closing will result in scanner error. DON'T DO IT, MAN!
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
					System.out.println("Enter start time minute (00 or 30): ");
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
					System.out.println("Enter end time minute (00 or 30): ");
					eMinute = in.nextInt();
					if(eMinute != 0 && eMinute != 30){
						tryAgain = true;
					}else{
						tryAgain = false;
					}
				}while(tryAgain);
				//TODO make sure end time is at least a half hour after start time
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
				break;
			case 3:
				System.out.println("Enter event name: ");
				name = in.nextLine();
				name += in.nextLine();
				eventDelete(name);
				break;
			case 4:
				System.out.println("goodbye");
				menu = false;
				break;
			case 7:
				System.out.println("Deleting local log and calendar");
				/*try{
					Files.delete("log.txt");
					Files.delete("dictionary.txt");
				} catch (NoSuchFileException x) {
				    System.err.format("%s: no such" + " file or directory%n", path);
				} catch (DirectoryNotEmptyException x) {
				    System.err.format("%s not empty%n", path);
				} catch (IOException x) {
				    // File permission problems are caught here.
				    System.err.println(x);
				}*/
			default:
				System.out.println("please choose a valid menu option");
				break;
			}
		}while(menu);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome");
		System.out.println("Which node is this? (0-3)");
		Scanner quickscan = new Scanner(System.in);
		int id = quickscan.nextInt();
		Node node = new Node(4);
		node.setID(id);
		TCPListener tcplistener = new TCPListener(node);
		tcplistener.start();
		node.otherIPs.put(0, "54.172.20.219");
		node.otherIPs.put(1, "54.175.243.93");
		node.otherIPs.put(2, "52.38.32.251");
		node.otherIPs.put(3, "52.27.249.225");
		node.menu();
		tcplistener.shutDown();
		quickscan.close();
	}

}
