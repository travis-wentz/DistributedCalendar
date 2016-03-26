package distlog;

public class Event {
	private Appointment appointment;
	private String op;	//op can be either "insert" or "delete"
	private int time;
	private int nodeID;
	
	public Event(Appointment appointment, String op){
		this.appointment = appointment;
		this.op = op;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
}
