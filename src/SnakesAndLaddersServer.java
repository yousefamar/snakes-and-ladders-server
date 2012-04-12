import java.io.IOException;
import java.net.*;
import java.util.Random;

public class SnakesAndLaddersServer {

	public ServerConsole console;

	private int playerNum;
	private ClientHandler[] clients; 
	private Random rand = new Random();
	private int currentPlayerID = 0;
	private int[] linkMap = new int[101];
	
	public SnakesAndLaddersServer(ServerConsole console, int playerNum) {
		this.console = console;
		this.playerNum = playerNum;
		clients = new ClientHandler[playerNum];
		
		for (int i = 0; i < linkMap.length; i++)
			linkMap[i] = i;

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

	public void listenForClientsAndStartGame(int port) throws IOException, InterruptedException {
		console.println("Waiting for "+playerNum+" clients to connect to port "+port+".");
		ServerSocket s = new ServerSocket(port);
		for (int id = 0; id < playerNum; id++) {
			clients[id] = new ClientHandler(this, s.accept(), id);
			console.println("Client "+id+" has joined the game.");
			clients[id].start();
			clients[id].sendString("i "+id);
		}
		
		console.println("Starting game.");
		/* Sleep to give the clients time to initialise. */
		Thread.sleep(1000);
		/* Let a random player start. */
		sendToAll("p "+(currentPlayerID = rand.nextInt(playerNum)));
	}
	
	public void sendToAll(String msg) {
		for (ClientHandler client : clients)
			if(client!=null)
				client.sendString(msg);
	}
	
	public void rollDice(int id) {
		if(id != currentPlayerID)
			return;
		
		currentPlayerID = -1;
		sendToAll("p "+currentPlayerID);
		
		/* Play */
		try {
			int dice = rand.nextInt(6)+1;
			sendToAll("d "+id+" "+dice);
			
			/* If move goes over 100, don't move. */
			if((clients[id].playerPos+dice) <= 100) {
				for(int movesLeft = dice; movesLeft>0; movesLeft--) {
					sendToAll("m "+id+" "+(clients[id].playerPos++));
					Thread.sleep(200);
				}
				sendToAll("m "+id+" "+clients[id].playerPos);
				int newPos = linkMap[clients[id].playerPos];
				if(clients[id].playerPos != newPos) {
					Thread.sleep(200);
					sendToAll("m "+id+" "+(clients[id].playerPos=newPos));
				}
				if(clients[id].playerPos==100) {
					notifyWin(id);
					return;
				}
			} else
				Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		currentPlayerID = ((id+1)%playerNum);
		sendToAll("p "+currentPlayerID);
	}
	
	public void updatePlayerName(int id, String name) {
		clients[id].playerName = name;
		String nameList = "";
		for (int j = 0; j < clients.length; j++)
			if(clients[j] != null)
				nameList += " "+clients[j].playerName;
			else
				nameList += " ....................";
		sendToAll("l"+nameList);
	}
	
	public void notifyWin(int id) {
		sendToAll("w "+id);
		console.println("Client "+id+" has won.");
		console.println("Waiting for clients to disconnect.");
	}

	public void onClientDC(int id) {
		clients[id] = null;
		/* Check if any other clients are still there and if not, notify console. */
		for (ClientHandler client : clients)
			if (client != null)
				return;
		console.println("All clients have disconnected. It is safe to quit.");
	}
}
