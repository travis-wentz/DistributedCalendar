package distlog;

public class Event {
	private Appointment appointment;
<<<<<<< HEAD
	private String op;	//op can be either "insert" or "delete"
=======
	private String op;
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	private int time;
	private int nodeID;
	
	public Event(Appointment appointment, String op){
		this.appointment = appointment;
		this.op = op;
	}

<<<<<<< HEAD
=======
	// Returns the appointment object associated with an event
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public Appointment getAppointment() {
		return appointment;
	}

<<<<<<< HEAD
=======
	// Sets an appointment object to be associated with an event
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

<<<<<<< HEAD
=======
	// Gets the operation of the event, a String that is either "Create" or "Delete"
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public String getOp() {
		return op;
	}

<<<<<<< HEAD
	public void setOp(String op) {
		this.op = op;
	}

=======
	// Sets the operation of the event, a String that is either "Create" or "Delete"
	public void setOp(String op) {
		this.op = op;
	}
	
	// Gets the logical time of an event, an integer value
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public int getTime() {
		return time;
	}

<<<<<<< HEAD
=======
	// Sets the logical time of an event, an integer value
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public void setTime(int time) {
		this.time = time;
	}

<<<<<<< HEAD
=======
	// Gets the node ID of the creator of the event, an integer value
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public int getNodeID() {
		return nodeID;
	}

<<<<<<< HEAD
=======
	// Sets the node ID of the creator of the event, an integer value
>>>>>>> f0d736daeeb3ec8e714aa448575229a9fb30f862
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
}
