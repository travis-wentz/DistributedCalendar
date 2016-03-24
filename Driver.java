package distlog;

import java.io.*;
import java.time.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Welcome");
		LocalTime start = LocalTime.of(7, 30);
		LocalTime end = LocalTime.of(8, 30);
		int[] participants = {1,2};
		Appointment a = new Appointment("Lunch", DayOfWeek.FRIDAY, start, end, participants);
		
		Event e = new Event(a, "Create");
		
		XStream xstream = new XStream(new StaxDriver());
		String xml = xstream.toXML(e);
		System.out.println(xml);
		
		Event x = (Event) xstream.fromXML(xml);
		System.out.println(x.getAppointment().getName());
		System.out.println(x.getAppointment().getStart_time().toString());
		System.out.println(x.getAppointment().getEnd_time().toString());
	}

}
