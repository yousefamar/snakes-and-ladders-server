package core;
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

	private GameServer server;
	private Socket socket;
	
	private BufferedReader in;
	private PrintWriter out;
	
	private int id;
	public String playerName;
	public int playerPos;
	
	/**
	 * Constructs a ClientHandler object and creates the server-client IO.
	 * On running in a thread, it will listen to and handle incoming messages from a given client.
	 * @param server
	 * @param socket
	 * @param id
	 * @throws IOException
	 */
	public ClientHandler(GameServer server, Socket socket, int id) throws IOException {
		this.server = server;
		this.socket = socket;
		this.id = id;
		this.playerName = "Player"+(id+1);

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public void run() {
		try {
			/* Enter a loop that continuously listens to incoming messages. */
			while (handleMessage(in.readLine()));
		} catch (SocketException e) {
			/* Notify server of this client disconnecting. */
			server.onClientDC(id);
		} catch (Exception e) {
			/* End thread quietly if the main thread has ended (e.g. Exit from GUI). */
		}
	}
	
	/**
	 * Parses and handles incoming messages.
	 * This method is snychronised to block potential simultaneous access to the PrintWriter
	 * that is used by both the client listening thread and the main thread. 
	 * @param msg
	 * @return Returns false if and only if the client should stop listening to the server. 
	 */	
	private synchronized boolean handleMessage(String msg) {
		if(msg == null) { // End of stream has been reached.
			server.onClientDC(id);
			return false;
		}
		
		String[] data = msg.split("\\s+"); // \s+ == regex for whitespace chars 1 or more times.
		/* Check prefix and act accordingly (see report). */
		if (data[0].equals("r"))
			server.rollDice(id);
		else if (data[0].equals("n"))
			server.updatePlayerName(id, data[1]);
		else
			server.sendToAll(playerName+" (P"+(id+1)+") says: "+msg);
		return true;
	}

	public synchronized void sendString(String msg) {
		out.println(msg);
	}
	
	public void destruct() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}