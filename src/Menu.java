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
		String playerName = scanner.nextLine();										//instantiate a player
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

				System.out.println("Hi: " + playerName + "! You successfully created a lobby.");
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

					TcpPacket parsedPacket = TcpPacket.parseFrom(sliced2);

					checksConnections(sliced2, socket, player);
					// if(parsedPacket.getType() == TcpPacket.PacketType.CONNECT){				
					// 	TcpPacket.ConnectPacket connectionPacket = TcpPacket.ConnectPacket.parseFrom(sliced2);
					// 	System.out.println("Enter ^q to quit or ^p to view other players.");
					// 	chatThread(socket, player);											//starts to chat
					// 	readThread(socket);
					// }
				}catch(Exception e){
					System.out.println("failed at connecting to the created lobby");
				}
			}catch(Exception e){
				System.out.println("failed at creating a lobby");
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
			}
		}
     }

	 public static void chats(Socket socket, Player player){
		int chatting = 1;

		while(chatting == 1){
			
			String message = scanner.nextLine();
			if (message.equals("^q")){
				chatting = 0;
				System.exit(0);
			}else if (message.equals("^p")){
				System.out.println("People in this chat:");
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

				}
			}
		}
	 }

	public static void checksConnections(byte[] sliced2, Socket socket, Player player){
		try{
			TcpPacket parsedPacket = TcpPacket.parseFrom(sliced2);
			if(parsedPacket.getType() == TcpPacket.PacketType.CONNECT){					//if the inputted lobby id is existing
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
		}
		
	}

	 public static void sends(Socket socket){
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
				}
			}catch(Exception e){

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
				sends(socket);
			}
		  });
		thread.start();
    }
	
}