package distlog;

import java.io.*;
import java.net.*;

public class TCPListener extends Thread{

	protected ServerSocket socket = null;
	protected boolean shutdown = false;
	protected Node nodeRef = null;
	
	public TCPListener(Node node) throws IOException {
		this("TCPListener", node);
	}
	
	public TCPListener(String name, Node node) throws IOException {
		super(name);
		socket = new ServerSocket(4445);
		nodeRef = node;
	}
	
	public void run() {
		System.out.println("Listener is running...");
		while(!shutdown) {
			try {
				Socket clientSocket = socket.accept();
				System.out.println("Connection aquired");
				Connection c = new Connection(clientSocket, nodeRef);
			} catch (IOException e) {
				e.printStackTrace();
				shutdown = true;
			} 
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Public accessible method to close down this thread, call when program terminates to close resources
	// Causes exception right now but only need to call when program terminates 
	public void shutDown(){
		shutdown = true;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class Connection extends Thread {
		InputStream input;
		BufferedInputStream buffer;
		DataInputStream data;
		Socket clientSocket;
		Node nodeRef;
		
		public Connection(Socket aClientSocket, Node node) {
			nodeRef = node;
			try{
				clientSocket = aClientSocket;
				input = clientSocket.getInputStream();
				buffer = new BufferedInputStream(input);
				data = new DataInputStream(buffer);
				this.start();
			} catch(IOException e) {
				System.out.println("Connection:"+e.getMessage());
			}
		}
		
		public void run(){
			try{
				// Receive the XML String first
				int nb = data.readInt();
				byte [] digit = new byte[nb];
				for(int i = 0; i < nb; i++){
					digit[i] = data.readByte();
				}
				String xml = new String(digit);
				
				// Then receive the Time Table int[size][size] 
				int size = data.readInt();
				int[][] array = new int[size][size];
				for(int i = 0; i < size; i++){
					for(int j = 0; j < size; j++){
						array[i][j] = data.readInt();
					}
				}
				
				// Now do something with them?
				nodeRef.receiveMessage(xml, array);
				
			} catch(EOFException e) {
				System.out.println("Here...");
				e.printStackTrace();
			} catch(IOException e){
				System.out.println("Yep...");
				e.printStackTrace();
			}
		}
	}
	
}
