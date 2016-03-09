/**
 * 
 * @author TravisWentz & Joe DeBryucker
 *
 */
public class Event {
	private String op;
	private String time;
	private int nodeID;
	
	public Event(String op, String time, int nodeID){
		this.op = op;
		this.time = time;
		this.nodeID = nodeID;
	}
	
	public String getOp(){
		return op;
	}
	
	public String getTime(){
		return time;
	}
	
	public int getNodeID(){
		return nodeID;
	}
}
