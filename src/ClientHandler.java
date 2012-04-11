

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

	private SnakesAndLaddersServer server;
	private Socket clientSoc;
	private int id;

	private BufferedReader in;
	private PrintWriter out;
	
	public ClientHandler(SnakesAndLaddersServer server, Socket clientSoc, int id) throws IOException {
		super("Client"+id+" Thread");
		this.server = server;
		this.clientSoc = clientSoc;
		this.id = id;

		in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
		out = new PrintWriter(clientSoc.getOutputStream(), true);
	}

	public void run() {
		try {
			while (handleMessage(in.readLine()));
			clientSoc.close();
		} catch (SocketException e) {
			System.out.println("Client "+id+" has disconnected.");
			server.clients[id] = null;
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private synchronized boolean handleMessage(String msg) {
		if(msg == null) {
			System.out.println("Client "+id+" has disconnected.");
			return false;
		}
		//TODO: Why should the clients have to send anything at all? Dice rolls are server-side.
		server.sendToAll("Player "+(id+1)+" says: "+msg);
		return true;
	}

	public synchronized void sendString(String msg) {
		out.println(msg);
	}
}