package distlog;

public class Event {
	private Appointment appointment;
	private String op;
	private int time;
	private int nodeID;
	
	public Event(Appointment appointment, String op){
		this.appointment = appointment;
		this.op = op;
	}

	// Returns the appointment object associated with an event
	public Appointment getAppointment() {
		return appointment;
	}

	// Sets an appointment object to be associated with an event
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	// Gets the operation of the event, a String that is either "Create" or "Delete"
	public String getOp() {
		return op;
	}

	// Sets the operation of the event, a String that is either "Create" or "Delete"
	public void setOp(String op) {
		this.op = op;
	}
	
	// Gets the logical time of an event, an integer value
	public int getTime() {
		return time;
	}

	// Sets the logical time of an event, an integer value
	public void setTime(int time) {
		this.time = time;
	}

	// Gets the node ID of the creator of the event, an integer value
	public int getNodeID() {
		return nodeID;
	}

	// Sets the node ID of the creator of the event, an integer value
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
}
