import java.util.ArrayList;


public class Node {
	private final int nodeID = 0;
	private static ArrayList<Event> localLog = new ArrayList<Event>();
	private String opType;
	private String time;
	
	private void newEvent(){
		Event event = new Event(opType, time, nodeID);
		localLog.add(event);
	}
}
