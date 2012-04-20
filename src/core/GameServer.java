package core;

import gui.ServerConsole;

import java.net.*;
import java.util.Random;

public class GameServer implements Runnable {

	public ServerConsole console;

	private int port;
	private int playerNum;
	private int currentPlayerID = -2;
	private ClientHandler[] clients; 
	private Random rand = new Random();
	private int[] linkMap = new int[101];
	
	/**
	 * Constructs a GameServer object that manages the clients and game logic.
	 * @param port
	 * @param playerNum
	 * @param console
	 */
	public GameServer(int port, int playerNum, ServerConsole console) {
		this.port = port;
		this.playerNum = playerNum;
		this.console = console;
		clients = new ClientHandler[playerNum];
		
		/* Initialise all square links to themselves. */
		for (int i = 0; i < linkMap.length; i++)
			linkMap[i] = i;

		/* Hardcode in square links to other squares based on snake and ladder positions.
		 * Refer to report for reasoning. */
		linkMap[1] = 38;
		linkMap[6] = 16;
		linkMap[11] = 49;
		linkMap[14] = 4;
		linkMap[21] = 60;
		linkMap[24] = 87;
		linkMap[31] = 9;
		linkMap[35] = 54;
		linkMap[44] = 26;
		linkMap[51] = 67;
		linkMap[56] = 53;
		linkMap[62] = 19;
		linkMap[64] = 42;
		linkMap[73] = 92;
		linkMap[78] = 100;
		linkMap[84] = 28;
		linkMap[91] = 71;
		linkMap[95] = 75;
		linkMap[98] = 80;
	}

	@Override
	public void run() {
		try {
			console.println("Waiting for "+playerNum+" players to connect to port "+port+".");
			ServerSocket s = new ServerSocket(port);
			/* Allow a given number of players to connect. */
			for (int id = 0; id < playerNum; id++) {
				/* Register every new connection and start a thread to handle them. */
				clients[id] = new ClientHandler(this, s.accept(), id);
				console.println("Player "+(id+1)+" has joined the game.");
				new Thread(clients[id], "P"+(id+1)+" Thread").start();
				clients[id].sendString("i "+id);
			}

			console.println("Starting game.");

			/* Sleep to give the clients time to catch up (GUI-wise). */
			Thread.sleep(1000);

			/* Let a random, non-null player begin. */
			do {
				currentPlayerID = rand.nextInt(playerNum);
			} while(clients[currentPlayerID]==null);
			
			/* Notify clients who's turn it is. */
			sendToAll("p "+currentPlayerID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to all available clients.
	 * @param msg
	 */
	public void sendToAll(String msg) {
		for (ClientHandler client : clients)
			if(client!=null)
				client.sendString(msg);
	}
	
	/**
	 * Requests a dice roll by a given client.
	 * Whether or not they're allowed and all game logic is handled within this method.
	 * @param id
	 */
	public void rollDice(int id) {
		/* Check if this client is allowed to roll (in case they hacked the client-side roll button). */
		if(id != currentPlayerID)
			return;
		
		/* Disable all client roll buttons. */
		currentPlayerID = -1;
		sendToAll("p "+currentPlayerID);
		
		/* Play turn. */
		try {
			/* Roll the dice and notify clients of result. */
			int dice = rand.nextInt(6)+1;
			sendToAll("d "+id+" "+dice);
			
			/* If move goes over 100, don't move. */
			if((clients[id].playerPos+dice) <= 100) {
				/* Move to the target square step by step. */
				for(int movesLeft = dice; movesLeft>0; movesLeft--) {
					sendToAll("m "+id+" "+(clients[id].playerPos++));
					Thread.sleep(200);
				}
				sendToAll("m "+id+" "+clients[id].playerPos);
				
				/* Update position based on potential snake or ladder link. */
				int newPos = linkMap[clients[id].playerPos];
				if(clients[id].playerPos != newPos) {
					Thread.sleep(200);
					sendToAll("m "+id+" "+(clients[id].playerPos=newPos));
				}
				
				/* Check for win. */
				if(clients[id].playerPos==100) {
					notifyWin(id);
					return;
				}
			} else
				Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/* Increment current player ID, mod to the total player count,
		 * and repeat if new current player disconnected earlier for some reason. */
		do {
			currentPlayerID = ((id+1)%playerNum);
		} while(clients[currentPlayerID]==null);
		
		sendToAll("p "+currentPlayerID);
	}
	
	/**
	 * Updates a player's name and informs all clients.
	 * @param id
	 * @param name
	 */
	public void updatePlayerName(int id, String name) {
		clients[id].playerName = name;
		String nameList = "";
		for (int j = 0; j < clients.length; j++)
			if(clients[j] != null)
				nameList += " "+clients[j].playerName;
			else if (j<playerNum)
				nameList += " ...";
		sendToAll("l"+nameList);
	}
	
	/**
	 * Sends a win flag to all clients notifying them of a win and ending the game.
	 * @param id
	 */
	public void notifyWin(int id) {
		sendToAll("w "+id);
		console.println(clients[id].playerName+" (P"+(id+1)+") has won.");
		console.println("Waiting for clients to disconnect.");
	}

	/**
	 * Cleans up if a player disconnects and informs the clients.
	 * If all players are gone after the game has started (e.g. when somebody wins),
	 * the server console is notified.
	 * @param id
	 */
	public void onClientDC(int id) {
		console.println(clients[id].playerName+" (P"+(id+1)+") has disconnected.");
		clients[id].destruct();
		clients[id] = null;
		
		/* Inform all other clients of client disconnecting. */
		String nameList = "";
		for (int j = 0; j < clients.length; j++)
			if(clients[j] != null)
				nameList += " "+clients[j].playerName;
			else if (j<playerNum)
				nameList += " ...";
		sendToAll("l"+nameList);
		
		/* Check if any other clients are still there after the game has begun and if not, notify console. */
		if (currentPlayerID==-2) //currentPlayerID is *only* -2 before the game starts.
			return;
		for (ClientHandler client : clients)
			if (client != null)
				return;
		console.println("All players have disconnected. It is safe to quit.");
	}
}