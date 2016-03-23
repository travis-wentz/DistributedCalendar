package distlog;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	private final int nodeID;
	private int clock;
	private int[][] time_table;
	private ArrayList<Event> log;
	private HashMap<String, Appointment> calendar;
	
	public Node(int total_number_nodes){
		this.clock = 0;
		this.time_table = new int[total_number_nodes][total_number_nodes];
		this.log = new ArrayList<Event>();
		this.calendar = new HashMap<String, Appointment>();		
		this.nodeID = 1; // assign this dynamically somehow
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public int[][] getTime_table() {
		return time_table;
	}

	public void setTime_table(int[][] time_table) {
		this.time_table = time_table;
	}

	public ArrayList<Event> getLog() {
		return log;
	}

	public void setLog(ArrayList<Event> log) {
		this.log = log;
	}

	public HashMap<String, Appointment> getCalendar() {
		return calendar;
	}

	public void setCalendar(HashMap<String, Appointment> calendar) {
		this.calendar = calendar;
	}

}
