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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public DayOfWeek getDay() {
		return day;
	}


	public void setDay(DayOfWeek day) {
		this.day = day;
	}


	public LocalTime getStart_time() {
		return start_time;
	}


	public void setStart_time(LocalTime start_time) {
		this.start_time = start_time;
	}


	public LocalTime getEnd_time() {
		return end_time;
	}


	public void setEnd_time(LocalTime end_time) {
		this.end_time = end_time;
	}


	public int[] getParticipants() {
		return participants;
	}


	public void setParticipants(int[] participants) {
		this.participants = participants;
	}
}
