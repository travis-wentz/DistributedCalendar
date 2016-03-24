package distlog;

import java.time.*;

public class Appointment {
	private String name;
	private DayOfWeek day;
	private LocalTime start_time;
	private LocalTime end_time;
	private int[] participants;

	
	public Appointment(String name, DayOfWeek day, LocalTime start, LocalTime end, int[] part){
		this.name = name;
		this.day = day;
		this.start_time = start;
		this.end_time = end;
		this.participants = part;
	}

	// Returns the unique name of an appointment
	public String getName() {
		return name;
	}

	// Sets the unique name of an appointment
	public void setName(String name) {
		this.name = name;
	}

	// Returns the date of the appointment (only a Day of the week in this implementation)
	public DayOfWeek getDay() {
		return day;
	}

	// Sets the day of a week for an appointment, type is java.time.DayOfWeek
	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	// Gets the java.time.LocalTime start time of an appointment
	public LocalTime getStart_time() {
		return start_time;
	}

	// Sets the java.time.LocalTime start time of an appointment
	public void setStart_time(LocalTime start_time) {
		this.start_time = start_time;
	}

	// Gets the java.time.LocalTime end time of an appointment
	public LocalTime getEnd_time() {
		return end_time;
	}

	// Sets the java.time.LocalTime end time of an appointment
	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}

	// Gets the integer array of the appointment participants, contains node IDs
	public int[] getParticipants() {
		return participants;
	}

	// Sets the integer array of the appointment participants
	public void setParticipants(int[] participants) {
		this.participants = participants;
	}
}
