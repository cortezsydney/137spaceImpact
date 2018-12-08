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


	private JFrame frame = new JFrame("Space Impact");
	private JPanel mainPanel, namePanel, choicePanel, mainChatPanel, idPanel, headerPanel, invalidIdPanel, fullLobbyPanel, messagePanel;
	private JLabel welcomeLabel, idLabel;
	private JPanel cards = new JPanel(new CardLayout());
	private CardLayout cardLayout;

    private JTextField nameField, idField, invalidIdField, fullLobbyField;
	private JTextField messageField;
	private JTextArea chatArea;
	
	private Player player;
	private String playerName, message;

	public static void main(String s[]){
        Menu menu = new Menu();
        menu.preFrame();

	}
	
	public void preFrame() {

        frame = new JFrame("SpaceImpact");
		mainPanel = new JPanel();

		//NAME CARD
		namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Enter Player Name:");
		nameField = new JTextField(10);
		nameField.addKeyListener(new newPlayerKeyListener());
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		/////////////////////////////////////////////////////////////////////////////////////////
        

		//CHOICE CARD
		choicePanel = new JPanel();
		JButton createButton = new JButton("CREATE");
		JButton joinButton = new JButton("JOIN");
		createButton.addActionListener(new createButtonListener());
		joinButton.addActionListener(new joinButtonListener());
		choicePanel.add(createButton);
		choicePanel.add(joinButton);
		////////////////////////////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////////////////////////

		
		//CHAT CARD
		mainChatPanel = new JPanel();
		
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(295,480));

		messagePanel = new JPanel();
		messagePanel.setLayout(new GridBagLayout());
		messagePanel.setPreferredSize(new Dimension(295,30));
		messagePanel.setBackground(Color.black);

		messageField = new JTextField(30);

		headerPanel = new JPanel();
		welcomeLabel = new JLabel();
		welcomeLabel.setPreferredSize(new Dimension(295,40));
		idLabel = new JLabel();
		idLabel.setPreferredSize(new Dimension(295,40));
		JLabel headerLabel = new JLabel("Enter ^q to quit or ^p to view other players.");
		headerLabel.setPreferredSize(new Dimension(295,10));

		headerLabel.setFont(new Font("Serif", Font.PLAIN, 10));
		headerPanel.add(welcomeLabel);
		headerPanel.add(headerLabel);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setFont(new Font("Serif", Font.PLAIN, 11));
		chatArea.setLineWrap(true);
		chatArea.setBackground(Color.pink);

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


		chatPanel.add(BorderLayout.SOUTH, messagePanel);
		chatPanel.add(BorderLayout.NORTH, headerPanel);
		
	
		mainChatPanel.add(chatPanel);


		//////////////////////////////////////////////////////////////////////////////


		//JOIN CARD

		idPanel = new JPanel();
		JLabel idLabel = new JLabel("Enter Lobby ID: ");
		idField = new JTextField(10);
		idField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					String id = idField.getText();
	
					joinLobby(id);
	
				}
			}
			public void keyTyped(KeyEvent e) {}
			   public void keyReleased(KeyEvent e) {}
		});
		idPanel.add(idLabel);
		idPanel.add(idField);

		///////////////////////////////////////////////////////////////////////////////

		//WRONG ID CARD

		invalidIdPanel = new JPanel();
		JLabel invalidIdLabel = new JLabel("Invalid Lobby ID. Please enter a valid Lobby ID: ");
		invalidIdField = new JTextField(10);
		invalidIdField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					String id = invalidIdField.getText();
	
					joinLobby(id);
	
				}
			}
			public void keyTyped(KeyEvent e) {}
			   public void keyReleased(KeyEvent e) {}
		});
		invalidIdPanel.add(invalidIdLabel);
		invalidIdPanel.add(invalidIdField);

		//////////////////////////////////////////////////////////////////////////

		//FULL LOBBY CARD

		fullLobbyPanel = new JPanel();
		JLabel fullLobbyLabel = new JLabel("Lobby is full. Please enter another Lobby ID: ");
		fullLobbyField = new JTextField(10);
		fullLobbyField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					String id = fullLobbyField.getText();
	
					joinLobby(id);
	
				}
			}
			public void keyTyped(KeyEvent e) {}
			   public void keyReleased(KeyEvent e) {}
		});
		fullLobbyPanel.add(fullLobbyLabel);
		fullLobbyPanel.add(fullLobbyField);

		//////////////////////////////////////////////////////////////////////////
	
		cards.add(namePanel, "NAME");
		cards.add(choicePanel, "CHOICE");
		cards.add(mainChatPanel, "MAINCHAT");
		cards.add(idPanel, "ID");
		cards.add(invalidIdPanel, "INVALID");
		cards.add(fullLobbyPanel, "FULL");


		mainPanel.add(cards, BorderLayout.CENTER);
		
        
        frame.add(mainPanel);
        frame.setSize(300,500);
        frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

	// NEW PLAYER //////////////////////////////////////////////////////////////////
	class newPlayerKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				playerName = nameField.getText();
				player = Player.newBuilder().setName(playerName).build();

				CardLayout c = (CardLayout) cards.getLayout();
				c.show(cards, "CHOICE");

			}
		}
		public void keyTyped(KeyEvent e) {}
   		public void keyReleased(KeyEvent e) {}

	}

	// ID /////////////////////////////////////////////////////////////////////////
	class newIdKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				String id = idField.getText();

				joinLobby(id);

			}
		}
		public void keyTyped(KeyEvent e) {}
   		public void keyReleased(KeyEvent e) {}

	}

	// CREATE LOBBY ////////////////////////////////////////////////////////////////
	class createButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {

			createLobby();
		}
	}


	// JOIN LOBBY ////////////////////////////////////////////////////////////////
	class joinButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			CardLayout c = (CardLayout) cards.getLayout();
			c.show(cards, "ID");


		}
	}

	
	public void createLobby(){

		try{
					
			CardLayout c = (CardLayout) cards.getLayout();
			c.show(cards, "MAINCHAT");

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

			

			welcomeLabel.setText("Hi " + playerName + "! You successfully created a lobby. Maximum players of 3.");
			idLabel.setText("Lobby ID: " + checkLobbyPacket.getLobbyId());
			headerPanel.add(idLabel);
			headerPanel.add(welcomeLabel);

			// System.out.println("Hi: " + playerName + "! You successfully created a lobby. Maximum players of 3.");
			System.out.println("Lobby ID: " + checkLobbyPacket.getLobbyId());

			try{												//connects to the created lobby
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

	public void joinLobby(String id){

		try{
			CardLayout c = (CardLayout) cards.getLayout();
			c.show(cards, "MAINCHAT");

			welcomeLabel.setText("Hi " + playerName + "! You successfully created a lobby. Maximum players of 3.");
			idLabel.setText("Lobby ID: " + id);
			headerPanel.add(idLabel);
			headerPanel.add(welcomeLabel);

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

	public void checksConnections(byte[] sliced2, Socket socket, Player player){
		try{
			TcpPacket parsedPacket = TcpPacket.parseFrom(sliced2);
			
			if(parsedPacket.getType() == TcpPacket.PacketType.CONNECT){					//if the inputted lobby id is existing
				chatArea.append("--- " + player.getName() + " joined me the conversation ---\n");
	
				
				chatThread(socket, player);
				readThread(socket);
			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LDNE){			//if the lobby does not exist
				TcpPacket.ErrLdnePacket connectionPacket = TcpPacket.ErrLdnePacket.parseFrom(sliced2);
				System.out.println("Lobby does not exist. (" + connectionPacket.getErrMessage() + ")");

				CardLayout c = (CardLayout) cards.getLayout();
				c.show(cards, "INVALID");

			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR_LFULL){			//max of 3 players to join the lobby
				TcpPacket.ErrLfullPacket connectionPacket = TcpPacket.ErrLfullPacket.parseFrom(sliced2);
				System.out.println("Lobby is full. (" + connectionPacket.getErrMessage() + ")");

				CardLayout c = (CardLayout) cards.getLayout();
				c.show(cards, "FULL");


			}else if(parsedPacket.getType() == TcpPacket.PacketType.ERR){				//other errors
				TcpPacket.ErrPacket connectionPacket = TcpPacket.ErrPacket.parseFrom(sliced2);
				System.out.println("Error. (" + connectionPacket.getErrMessage() + ")");
			}
		}catch(Exception e){
			System.out.println("failed to create a connection");
			e.printStackTrace();
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

	public void chats(Socket socket, Player player){									//chats
		int chatting = 1;
			
			messageField.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_ENTER) {
						String message = messageField.getText();
						int chatting=1;
						try{
								if (message.equals("^q")){
									TcpPacket.DisconnectPacket.Builder packet = TcpPacket.DisconnectPacket.newBuilder();
									packet.setType(TcpPacket.PacketType.DISCONNECT);
			
									OutputStream outSocket = socket.getOutputStream();
									DataOutputStream outputStream = new DataOutputStream(outSocket);
									outputStream.write(packet.build().toByteArray(), 0, packet.build().toByteArray().length);
			
					
									chatting = 0;
									
				
										System.exit(0);
								}else if (message.equals("^p")){
									
				
									TcpPacket.PlayerListPacket.Builder packet = TcpPacket.PlayerListPacket.newBuilder();
									packet.setType(TcpPacket.PacketType.PLAYER_LIST);
				
									OutputStream outSocket = socket.getOutputStream();
									DataOutputStream outputStream = new DataOutputStream(outSocket);
									outputStream.write(packet.build().toByteArray(), 0, packet.build().toByteArray().length);
				
									
				
								}else if(message != null){
				
									try{
										TcpPacket.ChatPacket.Builder chatPacket = TcpPacket.ChatPacket.newBuilder();
										chatPacket.setType(TcpPacket.PacketType.CHAT);
										chatPacket.setPlayer(player);
										chatPacket.setMessage(message).build();
										OutputStream outputChat = socket.getOutputStream();
										DataOutputStream outputStreamChat = new DataOutputStream(outputChat);
										outputStreamChat.write(chatPacket.build().toByteArray(), 0, chatPacket.build().toByteArray().length);	
									
									}catch(Exception err){
										System.out.println("failed to send chat");
										err.printStackTrace();
									}
								}
							
						
					
						}catch(Exception err){
							System.out.println("failed to send chat");
							err.printStackTrace();
						}
						messageField.requestFocusInWindow();
					}	
				}
				public void keyTyped(KeyEvent e) {}
				public void keyReleased(KeyEvent e) {}
			});
			messageField.requestFocusInWindow();
			

			
		
		
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

					chatArea.append("\n" + nowChatPacket.getPlayer().getName() + ": " + nowChatPacket.getMessage());

				}else if(checkChatPacket.getType() == TcpPacket.PacketType.DISCONNECT){
					TcpPacket.DisconnectPacket packet = TcpPacket.DisconnectPacket.parseFrom(sliced3);
				
					 chatArea.append("\n\n --- " + packet.getPlayer().getName() + " left the conversation --- \n ");

				}else if(checkChatPacket.getType() == TcpPacket.PacketType.CONNECT){
					TcpPacket.ConnectPacket packet = TcpPacket.ConnectPacket.parseFrom(sliced3);
					
					 
					 chatArea.append("\n\n --- " + packet.getPlayer().getName() + " joined the conversation --- \n");
				}else if(checkChatPacket.getType() == TcpPacket.PacketType.PLAYER_LIST){
					TcpPacket.PlayerListPacket packet = TcpPacket.PlayerListPacket.parseFrom(sliced3);
					chatArea.append("\n\nPeople in this chat: ");
	                for(Player player: packet.getPlayerListList()) 
						chatArea.append(player.getName()+ " == ");
					chatArea.append("\n");
				}
			}catch(Exception e){
				System.out.println("failed to read coming messages");
				e.printStackTrace();
			}

			messageField.setText("");
		}
	}






}
