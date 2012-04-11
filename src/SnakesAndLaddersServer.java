

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class SnakesAndLaddersServer {

	public ClientHandler[] clients = new ClientHandler[5]; 
	
	private void listenForClients(int port) throws IOException {
		System.out.println("Waiting for 5 clients to connect to port "+port+".");
		ServerSocket s = new ServerSocket(port);
		for (int i = 0; i < 5; i++) {
			clients[i] = new ClientHandler(this, s.accept(), i);
			clients[i].start();
			clients[i].sendString("i "+i);
			sendToAll("n "+(i+1));
		}
	}
	
	private void playGame() throws InterruptedException {
		int[] playerPos = { 0, 0, 0, 0, 0 };
		
		int[] linkMap = new int[101];
		
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
		
		
		//Thread.sleep(1500);

		Random rand = new Random();
		System.out.println("Starting game.");
		while (true) {
			for (int id = 0; id < 5; id++) {
				if(clients[id]==null)
					continue;
				for(int dice = rand.nextInt(6)+1; dice>0; dice--) {
					sendToAll("m "+id+" "+(playerPos[id]++));
					Thread.sleep(200);
				}
				sendToAll("m "+id+" "+playerPos[id]);
				int newPos = playerPos[id]>100?101:linkMap[playerPos[id]];
				if(playerPos[id] != newPos) {
					Thread.sleep(200);
					sendToAll("m "+id+" "+(playerPos[id]=newPos));
				}
				if(playerPos[id]>100) {
					notifyWin(id);
					return;
				}
			}
		}
	}
	
	public void sendToAll(String msg) {
		for (ClientHandler client : clients)
			if(client!=null)
				client.sendString(msg);
	}
	
	public void notifyWin(int id) {
		sendToAll("w "+id);
		System.out.println("Client "+id+" has won.");
		System.out.println("Waiting for clients to disconnect.");
	}

	public static void main(String[] args) {
		System.out.println("Starting Snakes and Ladders Server.");
		SnakesAndLaddersServer server = new SnakesAndLaddersServer();
		try {
			int port = 8250;
			if(args.length>0)
				port = Integer.parseInt(args[0]);
			else
				System.out.println("To change default port ("+port+"), input a custom port as command-line argument.");
			server.listenForClients(port);
			server.playGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
