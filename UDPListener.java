package distlog;

import java.io.*;
import java.net.*;

public class UDPListener extends Thread{

	protected DatagramSocket socket = null;
	protected boolean shutdown = false;
	
	public UDPListener() throws IOException {
		this("UDPListener");
	}
	
	public UDPListener(String name) throws IOException {
		super(name);
		socket = new DatagramSocket(4445);
	}
	
	public void run() {
		System.out.println("Listener is running...");
		while(!shutdown) {
			try {
				byte[] buf = new byte[65000];
				
				//receive message (byte representation of XML string)
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				//process message
				buf = packet.getData();
				String xml = new String(buf);
				
				// TODO: Determine what to do with incoming message, whether we convert it to XML here
				// or pass it to the main thread for processing
			} catch (IOException e) {
				e.printStackTrace();
				shutdown = true;
			} 
		}
		socket.close();
	}
	
	// Public accessible method to close down this thread, call when program terminates to close resources
	// Causes exception right now but only need to call when program terminates 
	public void shutDown(){
		shutdown = true;
		socket.close();
	}
	
}
