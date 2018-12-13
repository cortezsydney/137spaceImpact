import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.lang.String;
import java.awt.Dimension;

public class CircleWars extends JPanel implements Runnable, Constants {
	JFrame frame = new JFrame();
	int x = 10, y = 10, xspeed = 2, yspeed = 2, prevX, prevY;

	Thread t = new Thread(this);
	String name = "Joseph";
	int healthPoints, hitPoints;
	String server = "localhost";

	boolean connected = false;
	DatagramSocket socket = new DatagramSocket();
	String serverData;
	BufferedImage offscreen;
	static int shotCount = 1;

	String pname;
	String pos;
	String checkers;

	String currPlayer;

	JPanel gamePanel = new JPanel();
	public CircleWars(String server, String name, String pos, int healthPoints, int hitPoints) throws Exception {
		this.server = server;
		this.name = name;
		this.healthPoints = 15;
		this.hitPoints = 0;
		this.pos = pos;

		frame.setTitle(APP_NAME + ":" + name);
		socket.setSoTimeout(100);

		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 700);
		frame.setVisible(true);
	
		offscreen = (BufferedImage) this.createImage(800, 700);

		frame.addKeyListener(new KeyHandler());
		frame.addMouseMotionListener(new MouseMotionHandler());
		frame.setPreferredSize(new Dimension(300,700));

		t.start();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(((screenSize.width)/ 4)-75,((screenSize.height - 500) / 2));
		frame.add(gamePanel);
	}

	public void send(String msg) {
		try {
			byte[] buf = new byte[1000];
			buf = msg.getBytes();

			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
			socket.send(packet);
		} catch (Exception e) {}
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1);
			} catch (Exception ioe) {}

			// Get the data from players
			byte[] buf = new byte[1000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (Exception ioe) {}

			serverData = new String(buf);
			serverData = serverData.trim();

			offscreen.getGraphics().clearRect(0, 620, 800, 100);
			offscreen.getGraphics().clearRect(0, 0, 800, 15);

			if (!connected && serverData.startsWith("CONNECTED")) {
				connected = true;
				System.out.println("Connected.");
				send("PLAYER " + name + " " + x + " " + healthPoints + " " + hitPoints);
			} else if (serverData.startsWith("ALIEN")) {
				checkers= "";
				offscreen.getGraphics().clearRect(0, 20, 800, 600);
				String[] playersInfo = serverData.split(":");
				for (int i = 0; i < playersInfo.length; i++) {
					String[] playerInfo = playersInfo[i].split(" ");
					int posX = Integer.parseInt(playerInfo[2]);
					NetAlien player=new NetAlien(playerInfo[1], Integer.valueOf(playerInfo[2]), Integer.valueOf(playerInfo[3]),Integer.valueOf(playerInfo[4]),Integer.valueOf(playerInfo[5]));
					alienThread(player);
					checkers+=playerInfo[1] + " " + playerInfo[2] + ":";
				}
				frame.repaint();
			} else if (!connected) {
				System.out.println("Connecting..");
				send("CONNECT " + name);
			} else if (connected) {
				if (serverData.startsWith("PLAYER")) {
					String[] playersInfo = serverData.split(":");
					for (int i = 0; i < playersInfo.length; i++) {
						String[] playerInfo = playersInfo[i].split(" ");
						this.currPlayer = pname;
						String pname = playerInfo[1];
						int posX = Integer.parseInt(playerInfo[2]);

						offscreen.getGraphics().fillOval(posX, 620, 50, 50);
						if(Integer.parseInt(playerInfo[3]) == 0){
							playerInfo[3] = "is dead";
						}

						offscreen.getGraphics().drawString(pname + " position:" + playerInfo[2] + " health:" + playerInfo[3] + " hitPoints:" + playerInfo[4], posX, 10);
					}
					frame.repaint();
				}
				if (serverData.startsWith("SHOT")) {
					String[] playersInfo = serverData.split(":");
					for (int i = 0; i < playersInfo.length; i++) {
						String[] playerInfo = playersInfo[i].split(" ");
						this.currPlayer = playerInfo[3];
						NetShot player=new NetShot(shotCount, Integer.valueOf(playerInfo[1]));
						fireThread(player);
					}
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		g.drawImage(offscreen, 0, 0, null);
		Toolkit.getDefaultToolkit().sync();
	}

	class MouseMotionHandler extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent me) {
			x = me.getX();
			y = me.getY();
			if (prevX != x || prevY != y) {
				String msg = "PLAYER " + name + " " + x + " " + healthPoints + " " + hitPoints + " ";
				send(msg);
			}
		}
	}

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			prevX = x;
			prevY = y;
			switch (ke.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					NetShot shot = new NetShot(shotCount, prevX);
					send("SHOT " + shot.getX() + " " + shot.getY() + " " + name);
					shotCount += 1;
					break;
				case KeyEvent.VK_LEFT:
					x -= xspeed;
					send("DEAD " + name + " 0");
					break;
				case KeyEvent.VK_RIGHT:
					x += xspeed;
					break;
			}

		}
	}

	public void fireThread(NetShot shot){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(shot.getY() > 50){
						Thread.sleep(1000);
						shot.move();

						String[] playersInfo = checkers.split(":");
						for (int i = 0; i < playersInfo.length; i++) {
							String[] playerInfo = playersInfo[i].split(" ");
							if(shot.getX() == Integer.parseInt(playerInfo[1]) && shot.getY() == Integer.parseInt(playerInfo[2])){
								String killer = "DEAD " + playerInfo[3];
								send(killer);
								System.out.println(killer);
							}
						}
						offscreen.getGraphics().fillOval(shot.getX(), shot.getY(), 10, 10);
						frame.repaint();
					}
					frame.repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});
		thread.start();
	}

	public void alienThread(NetAlien alien){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(alien.getY() < 600){
						if(alien.getY() > 500 ){
							send("PLDEAD "+name);
						}
						
						Thread.sleep(1000);
						alien.move();
						send(alien.toString());
						offscreen.getGraphics().fillOval(alien.getX(), alien.getY(), 10, 10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});
		thread.start();
	}
}
