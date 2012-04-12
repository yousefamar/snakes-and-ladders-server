

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

	private SnakesAndLaddersServer server;
	private Socket clientSoc;
	
	private BufferedReader in;
	private PrintWriter out;
	
	private int id;
	public String playerName;
	public int playerPos;
	
	public ClientHandler(SnakesAndLaddersServer server, Socket clientSoc, int id) throws IOException {
		super("Client"+id+" Thread");
		this.server = server;
		this.clientSoc = clientSoc;
		this.id = id;
		this.playerName = "Player "+(id+1);

		in = new BufferedReader(new InputStreamReader(clientSoc.getInputStream()));
		out = new PrintWriter(clientSoc.getOutputStream(), true);
	}

	public void run() {
		try {
			while (handleMessage(in.readLine()));
			clientSoc.close();
		} catch (SocketException e) {
			server.console.println("Client "+id+" has disconnected.");
			server.onClientDC(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized boolean handleMessage(String msg) {
		if(msg == null) {
			server.console.println("Client "+id+" has disconnected.");
			server.onClientDC(id);
			return false;
		}
		
		String[] data = msg.split("\\s+");
		if (data[0].equals("r"))
			server.rollDice(id);
		else if (data[0].equals("n"))
			server.updatePlayerName(id, data[1]);
		else
			server.sendToAll("Player "+(id+1)+" says: "+msg);
		return true;
	}

	public synchronized void sendString(String msg) {
		out.println(msg);
	}
}