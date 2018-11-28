import java.util.*;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.net.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;  
import java.awt.event.*;  

import proto.TcpPacketProtos.TcpPacket;
import proto.PlayerProtos.Player;
import proto.TcpPacketProtos.TcpPacket.CreateLobbyPacket;
import proto.TcpPacketProtos.TcpPacket.ChatPacket;


public class Menu{

    public static Scanner scanner = new Scanner(System.in);
	public static String SERVERNAME = "202.92.144.45";
	public static int PORT = 80;
    
    private JFrame preframe;
	private JFrame frame = new JFrame("Space Impact");
	private JFrame idFrame = new JFrame("Space Impact");
    private JPanel panel;
    private JTextField nameField;
    private JTextField optionField;
    private JTextField messageField;
    private JTextArea chatArea;
	
	private JLabel idLabel = new JLabel("Enter Id: ");
	private JTextField  idField = new JTextField(10);

	private Player player;
    public static void main(String s[]){

        Menu menu = new Menu();
        menu.preFrame();

    }

    public void preFrame() {

		frame.setVisible(false);
		idFrame.setVisible(false);
        preframe = new JFrame("SpaceImpact");
        panel = new JPanel();

        JLabel nameLabel = new JLabel("Enter Player Name:");
        nameField = new JTextField(10);
    
        JLabel optionLabel = new JLabel("<html>Choose: <br/> [1] Create <br/> [2] Join </html>");
        optionField = new JTextField(2);
        JButton newPlayerButton = new JButton("OK");
        newPlayerButton.addActionListener(new newPlayerButtonListener());

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(optionLabel);
        panel.add(optionField);
        panel.add(newPlayerButton);
        
        preframe.add(panel);
        preframe.setSize(300,500);
        preframe.setLocationRelativeTo(null);
		preframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        preframe.setVisible(true);

    }

    public void createLobby() {

		System.out.println("CREATED");

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());

        messageField = new JTextField(30);
        messageField.requestFocusInWindow();

        JButton sendMessage = new JButton("Send");
        sendMessage.addActionListener(new sendMessageButtonListener());


        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Serif", Font.PLAIN, 10));
        chatArea.setLineWrap(true);

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        messagePanel.add(messageField, left);
        messagePanel.add(sendMessage, right);

        chatPanel.add(BorderLayout.SOUTH, messagePanel);

        frame.add(chatPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
        frame.setSize(300, 500);
        frame.setVisible(true);

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

			System.out.println("PASOK");
            CreateLobbyPacket checkLobbyPacket = CreateLobbyPacket.parseFrom(sliced1);

            chatArea.append("Hi: " + player + "! You successfully created a lobby. Maximum players of 3. \n");
            chatArea.append("Lobby ID: " + checkLobbyPacket.getLobbyId() + "\n");
			

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


    }

	public void joinLobby(){

		idFrame.setVisible(false);

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


			System.out.println("CREATED");

			JPanel chatPanel = new JPanel();
			chatPanel.setLayout(new BorderLayout());

			JPanel messagePanel = new JPanel();
			messagePanel.setLayout(new GridBagLayout());

			messageField = new JTextField(30);
			messageField.requestFocusInWindow();

			JButton sendMessage = new JButton("Send");
			sendMessage.addActionListener(new sendMessageButtonListener());


			chatArea = new JTextArea();
			chatArea.setEditable(false);
			chatArea.setFont(new Font("Serif", Font.PLAIN, 10));
			chatArea.setLineWrap(true);

			chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

			GridBagConstraints left = new GridBagConstraints();
			left.anchor = GridBagConstraints.LINE_START;
			left.fill = GridBagConstraints.HORIZONTAL;
			left.weightx = 512.0D;
			left.weighty = 1.0D;

			GridBagConstraints right = new GridBagConstraints();
			right.insets = new Insets(0, 10, 0, 0);
			right.anchor = GridBagConstraints.LINE_END;
			right.fill = GridBagConstraints.NONE;
			right.weightx = 1.0D;
			right.weighty = 1.0D;

			messagePanel.add(messageField, left);
			messagePanel.add(sendMessage, right);

			chatPanel.add(BorderLayout.SOUTH, messagePanel);

			frame.add(chatPanel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(300, 500);
			frame.setVisible(true);

			checksConnections(sliced2, socket, player);
		}catch(Exception e){
			System.out.println("failed at connecting to lobby of your choice");
			e.printStackTrace();
		}

	}

	String playerName;
	String id;
    
    class newPlayerButtonListener implements ActionListener{

        public void actionPerformed(ActionEvent event) {
            playerName = nameField.getText();
			player = Player.newBuilder().setName(playerName).build();
		
			System.out.println(playerName);
			int choice = Integer.parseInt(optionField.getText());

            if(choice == 1){
                preframe.setVisible(false);
                createLobby();
			}
			else if(choice == 2){
				// preframe.setVisible(false);

				JPanel idPanel = new JPanel();

				idPanel.add(idLabel);
				idPanel.add(idField);

				idFrame.add(idPanel);
				idFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				idFrame.setLocationRelativeTo(null);
				idFrame.setSize(300, 500);
				idFrame.setVisible(true);

				id = idField.getText();

                joinLobby();
			}
			else{
				System.out.println(optionField.getText());
			}
            

        }

	}
	
	class sendMessageButtonListener implements ActionListener{

        public void actionPerformed(ActionEvent event) {

            if (messageField.getText().length() > 0){
                chatArea.append(player + ":  " + messageField.getText() + "\n");
                messageField.setText("");
            }
            messageField.requestFocusInWindow();
        }

	}
	
	public void checksConnections(byte[] sliced2, Socket socket, Player player){
		try{
			TcpPacket parsedPacket = TcpPacket.parseFrom(sliced2);

			if(parsedPacket.getType() == TcpPacket.PacketType.CONNECT){					//if the inputted lobby id is existing
				// System.out.println(player.getName() + " joined the conversation ============================" );
				// System.out.println("Enter ^q to quit or ^p to view other players.");

				chatArea.append(player.getName() + " joined the conversation ============================ \n");
				chatArea.append("Enter ^q to quit or ^p to view other players. \n");
				
				chatThread(socket, player);
				readThread(socket);
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LDNE){			//if the lobby does not exist
				TcpPacket.ErrLdnePacket connectionPacket = TcpPacket.ErrLdnePacket.parseFrom(sliced2);
				// System.out.println("Lobby does not exist. (" + connectionPacket.getErrMessage() + ")");

				chatArea.append("Lobby does not exist. (" + connectionPacket.getErrMessage() + ") \n");
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LFULL){			//max of 3 players to join the lobby
				TcpPacket.ErrLfullPacket connectionPacket = TcpPacket.ErrLfullPacket.parseFrom(sliced2);
				System.out.println("Lobby is full. (" + connectionPacket.getErrMessage() + ")");

				chatArea.append("Lobby is full. (" + connectionPacket.getErrMessage() + ") \n");
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR){				//other errors
				TcpPacket.ErrPacket connectionPacket = TcpPacket.ErrPacket.parseFrom(sliced2);
				System.out.println("Error. (" + connectionPacket.getErrMessage() + ")");
			}
		}catch(Exception e){
			System.out.println("failed to create a connection");
			e.printStackTrace();
		}
	}

	public void chats(Socket socket, Player player){									//chats
		int chatting = 1;
		try{
			while(chatting == 1){
				// String message = scanner.nextLine();
				String message = messageField.getText();
				if (message.equals("^q")){
					 TcpPacket.DisconnectPacket.Builder packet = TcpPacket.DisconnectPacket.newBuilder();
					 packet.setType(TcpPacket.PacketType.DISCONNECT);

					 OutputStream outSocket = socket.getOutputStream();
					 DataOutputStream outputStream = new DataOutputStream(outSocket);
					 outputStream.write(packet.build().toByteArray(), 0, packet.build().toByteArray().length);

					 chatting = 0;
					//  System.out.println(player.getName() + "");

					 chatArea.append(player.getName() + "\n");
					 System.exit(0);
				}else if (message.equals("^p")){
					// System.out.print("People in this chat: ");

					chatArea.append("People in this chat: ");

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


	public void receives(Socket socket){											//gets messages from other users
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
	
	public void chatThread(Socket socket, Player player){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){
				chats(socket, player);
			}
		  });
		thread.start();  
    }

	public void readThread(Socket socket){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){
				receives(socket);
			}
		  });
		thread.start();
    }

}