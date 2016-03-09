import java.util.ArrayList;

/**
 * 
 * @author TravisWentz & Joe DeBryucker
 *
 */
public class Node {
	private int nodeID;
	private static ArrayList<Event> localLog = new ArrayList<Event>();
	private String opType; //can be: local, send, receive
	private String time;
	
	public Node(int nodeID){
		this.nodeID = nodeID;
	}
	
	private void newEvent(){
		Event event = new Event(opType, time, nodeID);
		localLog.add(event);
	}
}
