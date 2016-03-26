package distlog;

import java.time.*;

public class Appointment implements Comparable<Appointment>{
	private String name;
	private DayOfWeek day;
	private LocalTime startTime;
	private LocalTime endTime;
	private int[] participants;

	
	public Appointment(String name, DayOfWeek day, LocalTime start, LocalTime end, int[] part){
		this.name = name;
		this.day = day;
		this.startTime = start;
		this.endTime = end;
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


	public LocalTime getStartTime() {
		return startTime;
	}


	public void setStartTime(LocalTime start_time) {
		this.startTime = start_time;
	}


	public LocalTime getEndTime() {
		return endTime;
	}


	public void setEndTime(LocalTime end_time) {
		this.endTime = end_time;
	}


	public int[] getParticipants() {
		return participants;
	}


	public void setParticipants(int[] participants) {
		this.participants = participants;
	}


	//this stupid method took 8 hours to finally figure out and will sort first by day of week, then by starting time
	@Override
	public int compareTo(Appointment b) {
		if(day.compareTo(b.day) == 0){
			int aTime = (int) startTime.getHour();
			aTime *= 100;
			aTime += (int) startTime.getMinute();
			
			int bTime = (int) b.startTime.getHour();
			bTime *= 100;
			bTime += (int) b.startTime.getMinute();
			
			if(aTime > bTime){
				return 1;
			}else if(bTime > aTime){
				return -1;
			}else{
				return 0;
			}
		}else{
			return day.compareTo(b.day);
		}
	}
}
