import java.util.*;
import java.net.*;
import java.io.*;

import proto.TcpPacketProtos.TcpPacket;
import proto.PlayerProtos.Player;
import proto.TcpPacketProtos.TcpPacket.CreateLobbyPacket;
import proto.TcpPacketProtos.TcpPacket.ChatPacket;


public class Menu{
	public static Scanner scanner = new Scanner(System.in);
	public static String SERVERNAME = "202.92.144.45";
	public static int PORT = 80;
	

	public static void main(String[] args){
     	System.out.print("Enter Name:");
		String playerName = scanner.nextLine();												//instantiate a player
		Player player = Player.newBuilder().setName(playerName).build();

		System.out.println("[1] Create a Lobby");
		System.out.println("[2] Join a Lobby");
		System.out.print("Enter your choice: ");
		int mainMenuChoice = Integer.parseInt(scanner.nextLine());

		if(mainMenuChoice == 1){
			try{
				Socket socket = new Socket(SERVERNAME, PORT);								//creates a lobby
				InputStream inputSocket = socket.getInputStream();
				OutputStream outputSocket = socket.getOutputStream();
				DataOutputStream outputStream =  new DataOutputStream(outputSocket);

				CreateLobbyPacket lobbyPacket = CreateLobbyPacket.newBuilder().setMaxPlayers(3).setType(TcpPacket.PacketType.CREATE_LOBBY).build();
				outputSocket.write(lobbyPacket.toByteArray());

				byte[] recvd1 = new byte[1024];
				int readbytes1 = inputSocket.read(recvd1);
				byte[] sliced1 = Arrays.copyOf(recvd1, readbytes1);

				CreateLobbyPacket checkLobbyPacket = CreateLobbyPacket.parseFrom(sliced1);

				System.out.println("Hi: " + playerName + "! You successfully created a lobby. Maximum players of 3.");
				System.out.println("Lobby ID: " + checkLobbyPacket.getLobbyId());

				try{																		//connects to the created lobby
					TcpPacket.ConnectPacket.Builder connectionPacket = TcpPacket.ConnectPacket.newBuilder();
					connectionPacket.setType(TcpPacket.PacketType.CONNECT);
					connectionPacket.setPlayer(player);
					connectionPacket.setLobbyId(checkLobbyPacket.getLobbyId());

					OutputStream outputSocket2 = socket.getOutputStream();
					DataOutputStream outputStream2 =  new DataOutputStream(outputSocket2);
					outputStream2.write(connectionPacket.build().toByteArray(), 0, connectionPacket.build().toByteArray().length);

					InputStream inSocket2 = socket.getInputStream();
					DataInputStream inputStream2 = new DataInputStream(inSocket2);
					byte[] recvd2 = new byte[1024];
					int readbytes2 = inputStream2.read(recvd2, 0, recvd2.length);
					byte[] sliced2 = Arrays.copyOf(recvd2, readbytes2);

					checksConnections(sliced2, socket, player);

				}catch(Exception e){
					System.out.println("failed at connecting to the created lobby");
					e.printStackTrace();
				}
			}catch(Exception e){
				System.out.println("failed at creating a lobby");
				e.printStackTrace();
			}
		}else if (mainMenuChoice == 2){														//connects to the user inputted lobby
			System.out.print("Enter Lobby Id: ");
			String id = scanner.nextLine();

			try{
				Socket socket = new Socket(SERVERNAME, PORT);

				TcpPacket.ConnectPacket.Builder connectionPacket = TcpPacket.ConnectPacket.newBuilder();
				connectionPacket.setType(TcpPacket.PacketType.CONNECT);
				connectionPacket.setPlayer(player);
				connectionPacket.setLobbyId(id);
			
				OutputStream outputSocket2 = socket.getOutputStream();
				DataOutputStream outputStream2 =  new DataOutputStream(outputSocket2);
				outputStream2.write(connectionPacket.build().toByteArray(), 0, connectionPacket.build().toByteArray().length);

				InputStream inSocket2 = socket.getInputStream();
				DataInputStream inputStream2 = new DataInputStream(inSocket2);
				byte[] recvd2 = new byte[1024];
				int readbytes2 = inputStream2.read(recvd2, 0, recvd2.length);
				byte[] sliced2 = Arrays.copyOf(recvd2, readbytes2);

				checksConnections(sliced2, socket, player);
			}catch(Exception e){
				System.out.println("failed at connecting to lobby of your choice");
				e.printStackTrace();
			}
		}
     }

	 public static void chats(Socket socket, Player player){									//chats
		int chatting = 1;
		try{
			while(chatting == 1){
				String message = scanner.nextLine();
				if (message.equals("^q")){
					 TcpPacket.DisconnectPacket.Builder packet = TcpPacket.DisconnectPacket.newBuilder();
					 packet.setType(TcpPacket.PacketType.DISCONNECT);

					 OutputStream outSocket = socket.getOutputStream();
					 DataOutputStream outputStream = new DataOutputStream(outSocket);
					 outputStream.write(packet.build().toByteArray(), 0, packet.build().toByteArray().length);

					 chatting = 0;
					 System.out.println(player.getName() + "");
					 System.exit(0);
				}else if (message.equals("^p")){
					System.out.print("People in this chat: ");

					TcpPacket.PlayerListPacket.Builder packet = TcpPacket.PlayerListPacket.newBuilder();
					packet.setType(TcpPacket.PacketType.PLAYER_LIST);

					OutputStream outSocket = socket.getOutputStream();
					DataOutputStream outputStream = new DataOutputStream(outSocket);
					outputStream.write(packet.build().toByteArray(), 0, packet.build().toByteArray().length);
				}else{
					try{
						TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
						chatPacket.setType(TcpPacket.PacketType.CHAT);
						chatPacket.setPlayer(player);
						chatPacket.setMessage(message).build();
						OutputStream outputChat = socket.getOutputStream();
						DataOutputStream outputStreamChat = new DataOutputStream(outputChat);
						outputStreamChat.write(chatPacket.build().toByteArray(), 0, chatPacket.build().toByteArray().length);	
					}catch(Exception e){
						System.out.println("failed to send chat");
						e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			System.out.println("failed to send chat");
			e.printStackTrace();
		}
	 }

	public static void checksConnections(byte[] sliced2, Socket socket, Player player){
		try{
			TcpPacket parsedPacket = TcpPacket.parseFrom(sliced2);

			if(parsedPacket.getType() == TcpPacket.PacketType.CONNECT){					//if the inputted lobby id is existing
				System.out.println(player.getName() + " joined the conversation ============================" );
				System.out.println("Enter ^q to quit or ^p to view other players.");
				chatThread(socket, player);
				readThread(socket);
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LDNE){			//if the lobby does not exist
				TcpPacket.ErrLdnePacket connectionPacket = TcpPacket.ErrLdnePacket.parseFrom(sliced2);
				System.out.println("Lobby does not exist. (" + connectionPacket.getErrMessage() + ")");
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LFULL){			//max of 3 players to join the lobby
				TcpPacket.ErrLfullPacket connectionPacket = TcpPacket.ErrLfullPacket.parseFrom(sliced2);
				System.out.println("Lobby is full. (" + connectionPacket.getErrMessage() + ")");
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR){				//other errors
				TcpPacket.ErrPacket connectionPacket = TcpPacket.ErrPacket.parseFrom(sliced2);
				System.out.println("Error. (" + connectionPacket.getErrMessage() + ")");
			}
		}catch(Exception e){
			System.out.println("failed to create a connection");
			e.printStackTrace();
		}
	}

	public static void receives(Socket socket){											//gets messages from other users
		while(true){
			try{
				InputStream inputChat = socket.getInputStream();
				DataInputStream InputStreamChat = new DataInputStream(inputChat);
				byte[] recvd3 = new byte[1024];
				int readbytes3 = inputChat.read(recvd3);
				byte[] sliced3 = Arrays.copyOf(recvd3, readbytes3);
				
				TcpPacket checkChatPacket = TcpPacket.parseFrom(sliced3);

				if(checkChatPacket.getType() == TcpPacket.PacketType.CHAT){
					TcpPacket.ChatPacket nowChatPacket = TcpPacket.ChatPacket.parseFrom(sliced3);
					System.out.println("\n" + nowChatPacket.getPlayer().getName() + ": " + nowChatPacket.getMessage());
				}else if(checkChatPacket.getType() == TcpPacket.PacketType.DISCONNECT){
					TcpPacket.DisconnectPacket packet = TcpPacket.DisconnectPacket.parseFrom(sliced3);
	             	System.out.println(packet.getPlayer().getName() + " left the conversation ============================");
				}else if(checkChatPacket.getType() == TcpPacket.PacketType.CONNECT){
					TcpPacket.ConnectPacket packet = TcpPacket.ConnectPacket.parseFrom(sliced3);
	             	System.out.println(packet.getPlayer().getName() + " joined the conversation ============================");
				}else if(checkChatPacket.getType() == TcpPacket.PacketType.PLAYER_LIST){
					TcpPacket.PlayerListPacket packet = TcpPacket.PlayerListPacket.parseFrom(sliced3);
	                for(Player player: packet.getPlayerListList()) 
						System.out.print(player.getName() + " == ");
					System.out.println("");	
				}
			}catch(Exception e){
				System.out.println("failed to read coming messages");
				e.printStackTrace();
			}
		}
	}
	
	public static void chatThread(Socket socket, Player player){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){
				chats(socket, player);
			}
		  });
		thread.start();  
    }

	public static void readThread(Socket socket){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){
				receives(socket);
			}
		  });
		thread.start();
    }
}