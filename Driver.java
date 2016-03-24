package distlog;

import java.io.*;
import java.time.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class Driver {

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome");
		//if the log file doesn't exist, create it
		//else open it and read it
		if(!new File("log.txt").exists()){
			//create the log file
			File log = new File("log.txt");
			FileWriter logFile = new FileWriter(log);

			//create a new event
			LocalTime start = LocalTime.of(7, 30);
			LocalTime end = LocalTime.of(8, 30);
			int[] participants = {1,2};
			Appointment a = new Appointment("Lunch", DayOfWeek.FRIDAY, start, end, participants);
			
			Event e = new Event(a, "Create");
			
			//save new event to a string & save it to log file
			XStream xstream = new XStream(new StaxDriver());
			String xml = xstream.toXML(e);
			System.out.println(xml);
			logFile.write(xml);
			
			logFile.close(); //close the log file
		}else{
			//open and read the file
			FileInputStream fstream = new FileInputStream("log.txt");
			BufferedReader logFile = new BufferedReader(new InputStreamReader(fstream));
			String xml = logFile.readLine();
			System.out.println(xml);
			
			XStream xstream = new XStream(new StaxDriver());
			Event x = (Event) xstream.fromXML(xml);
			System.out.println(x.getAppointment().getName());
			System.out.println(x.getAppointment().getStart_time().toString());
			System.out.println(x.getAppointment().getEnd_time().toString());
			
			logFile.close(); //close the file
		}
	}

}
