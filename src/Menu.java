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
	private JPanel mainPanel, namePanel, choicePanel, mainChatPanel, idPanel;
	private JPanel cards = new JPanel(new CardLayout());
	private CardLayout cardLayout;

    private JTextField nameField, idField;
	private JTextField messageField;
	private JTextArea chatArea;
	
	
	private Player player;

	public static void main(String s[]){
        Menu menu = new Menu();
        menu.preFrame();

	}
	
	public void preFrame() {

        frame = new JFrame("SpaceImpact");
		mainPanel = new JPanel();

		//FIRST CARD
		namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Enter Player Name:");
		nameField = new JTextField(10);
		nameField.addKeyListener(new newPlayerKeyListener());
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		/////////////////////////////////////////////////////////////////////////////////////////
        

		//SECOND CARD
		choicePanel = new JPanel();
		JButton createButton = new JButton("CREATE");
		JButton joinButton = new JButton("JOIN");
		createButton.addActionListener(new createButtonListener());
		joinButton.addActionListener(new joinButtonListener());
		choicePanel.add(createButton);
		choicePanel.add(joinButton);
		////////////////////////////////////////////////////////////////////////////////////////

		//THIRD CARD
		mainChatPanel = new JPanel();
		
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setPreferredSize(new Dimension(295,480));

		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new GridBagLayout());
		messagePanel.setPreferredSize(new Dimension(295,20));
		messagePanel.setBackground(Color.black);

		JLabel headerLabel = new JLabel("Enter ^q to quit or ^p to view other players.");
		headerLabel.setPreferredSize(new Dimension(295,40));
		headerLabel.setFont(new Font("Serif", Font.PLAIN, 10));

		messageField = new JTextField(30);
		messageField.addKeyListener(new newMessageKeyListener());
		messageField.requestFocusInWindow();
		
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
		chatPanel.add(BorderLayout.NORTH, headerLabel);

		
		// mainChatPanel.add(lab el);
		mainChatPanel.add(chatPanel);
		// createLobby();
		///////////////////////////////////////////////////////////////////////////////////
		
		//FOURTH CARD

		idPanel = new JPanel();
		JLabel idLabel = new JLabel("Enter Lobby ID: ");
		idField = new JTextField(10);
		idField.addKeyListener(new newIdKeyListener());
		idPanel.add(idLabel);
		idPanel.add(idField);

		///////////////////////////////////////////////////////////////////////////////////
	
		cards.add(namePanel, "NAME");
		cards.add(choicePanel, "CHOICE");
		cards.add(mainChatPanel, "MAINCHAT");
		cards.add(idPanel, "ID");

		mainPanel.add(cards, BorderLayout.CENTER);
		
        
        frame.add(mainPanel);
        frame.setSize(300,500);
        frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


	class newPlayerKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				System.out.println(nameField.getText());
				CardLayout c = (CardLayout) cards.getLayout();
				c.show(cards, "CHOICE");
			}
		}
		public void keyTyped(KeyEvent e) {}
   		public void keyReleased(KeyEvent e) {}

	}

	class newIdKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				System.out.println(idField.getText());
				CardLayout c = (CardLayout) cards.getLayout();
				c.show(cards, "MAINCHAT");
			}
		}
		public void keyTyped(KeyEvent e) {}
   		public void keyReleased(KeyEvent e) {}

	}


	class createButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			CardLayout c = (CardLayout) cards.getLayout();
			c.show(cards, "MAINCHAT");
		}
	}

	class joinButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			CardLayout c = (CardLayout) cards.getLayout();
			c.show(cards, "ID");
		}
	}



	class newMessageKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER) {
				chatArea.append(messageField.getText() + "\n");
                messageField.setText("");
			}
			messageField.requestFocusInWindow();
		}
		public void keyTyped(KeyEvent e) {}
   		public void keyReleased(KeyEvent e) {}

	}

	
	
}