

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameServer implements Runnable, Constants{
	String playerData;
	int playerCount=0, alienCount = 0;
	DatagramSocket serverSocket = null;
    GameState game;
	MapState map;
	int gameStage = WAITING_FOR_PLAYERS;
	int numPlayers;
	
	Thread t = new Thread(this);
	
	public GameServer(int numPlayers){
		this.numPlayers = numPlayers;
		try {
            serverSocket = new DatagramSocket(PORT);
			serverSocket.setSoTimeout(100);
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+ PORT);
            System.exit(-1);
		}catch(Exception e){}

		game = new GameState();
		map = new MapState();
		System.out.println("Game created...");
		
		t.start();
	}
	
	// Helper method for broadcasting data to all players
	public void broadcast(String msg){
		for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();){
			String name = (String)ite.next();
			NetPlayer player = (NetPlayer)game.getPlayers().get(name);			
			send(player,msg);	
		}
	}

	public void broadcastMap(String msg){
		for(Iterator ite = map.getAliens().keySet().iterator(); ite.hasNext();){
			int iterator = (Integer)ite.next();
			NetAlien alien = (NetAlien)map.getAliens().get(iterator);			
			sendMap(alien, msg);	
		}
	}

	public void send(NetPlayer player, String msg){
		DatagramPacket packet;	
		byte buf[] = msg.getBytes();		
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort());
		
		try{ 
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	public void sendMap(NetAlien alien, String msg){
		DatagramPacket packet;	
		byte buf[] = msg.getBytes();		
		packet = new DatagramPacket(buf, buf.length, alien.getAddress(),alien.getPort());
		
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			serverSocket.receive(packet);
			}catch(Exception ioe){}
			
			playerData=new String(buf);

			playerData = playerData.trim();
			
			switch(gameStage){
				  case WAITING_FOR_PLAYERS:
						if (playerData.startsWith("CONNECT")){
							String tokens[] = playerData.split(" ");
							NetPlayer player=new NetPlayer(tokens[1],packet.getAddress(),packet.getPort());
							System.out.println("Player connected: " + tokens[1]);
							game.update(tokens[1].trim(), player);
							broadcast("CONNECTED "+ tokens[1]);
							playerCount++;
							if (playerCount == numPlayers){
								gameStage = GAME_START;
							}
						}
					  break;	
				  case GAME_START:
					  System.out.println("Game State: START");
					  broadcast("START");
					  gameStage = IN_PROGRESS;

						for(int row = 0; row < 5; row++){
							for(int col = 0; col < 12; col++){
								NetAlien alien = new NetAlien(alienCount, packet.getAddress(),packet.getPort());
								map.update(alienCount, alien);
								alien.setX(100+(col*50));
								alien.setY(50+(row*30));
								alienCount += 1;
								
								System.out.println(alien.toString().getClass().getName());
								broadcastMap(alien.toString());
							}
						}
					  break;
				  case IN_PROGRESS:
				  	  System.out.println(playerData);
					  if (playerData.startsWith("PLAYER")){
						  String[] playerInfo = playerData.split(" ");					  
						  String pname =playerInfo[1];
						  int posX = Integer.parseInt(playerInfo[2].trim());
						  //Get the player from the game state
						
						  NetPlayer player=(NetPlayer)game.getPlayers().get(pname);					  
						
						  player.setX(posX);
						
						  game.update(pname, player);
						  broadcast(game.toString());
					  }else if(playerData.startsWith("MAP")){
						  System.out.println(playerData);
						  String[] alienInfo = playerData.split(" ");			
						  int pname = Integer.parseInt(alienInfo[1].trim());
						  int posX = Integer.parseInt(alienInfo[2].trim());
						  int posY = Integer.parseInt(alienInfo[3].trim());
						  //Get the player from the game state
						  NetAlien alien=(NetAlien)map.getAliens().get(pname);					  
						  
						  map.update(pname, alien);
						  broadcastMap(map.toString());
					  }
					  break;
			}				  
		}
	}	
	
	

	public static void main(String args[]){
		if (args.length != 1){
			System.out.println("Usage: java -jar circlewars-server <number of players>");
			System.exit(1);
		}
		
		new GameServer(Integer.parseInt(args[0]));
	}
}

