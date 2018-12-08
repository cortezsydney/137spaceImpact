
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
import java.util.ArrayList;

public class CircleWars extends JPanel implements Runnable, Constants {
	JFrame frame = new JFrame();
	int x = 10, y = 10, xspeed = 2, yspeed = 2, prevX, prevY;

	Thread t = new Thread(this);
	String name = "Joseph";
	int healthPoints, hitPoints;
	String pname;
	String server = "localhost";

	boolean connected = false;
	DatagramSocket socket = new DatagramSocket();
	String serverData;
	BufferedImage offscreen;
	int shoot = 40;
	int shotCount = 1;

	int shootPos;
	String pos;

	ArrayList<NetAlien> listOfAliens = new ArrayList<NetAlien>();
	ArrayList<Shot> listOfShots = new ArrayList<Shot>();

	String currPlayer;

	public CircleWars(String server, String name, String pos, int healthPoints, int hitPoints) throws Exception {
		this.server = server;
		this.name = name;
		this.healthPoints = 15;
		this.hitPoints = 0;
		this.pos = pos;
		frame.setTitle(APP_NAME + ":" + name);
		socket.setSoTimeout(100);

		// Some gui stuff i hate.
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 700);
		frame.setVisible(true);

		// create the buffer
		offscreen = (BufferedImage) this.createImage(800, 700);

		// Some gui stuff again...
		frame.addKeyListener(new KeyHandler());
		frame.addMouseMotionListener(new MouseMotionHandler());

		// tiime to play

		t.start();
	}

	public void send(String msg) {
		try {
			byte[] buf = new byte[1000];
			buf = msg.getBytes();
			// buf.resize(150);

			InetAddress address = InetAddress.getByName(server);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
			socket.send(packet);
		} catch (Exception e) {
		}

	}

	public void run() {
		long lastLoopTime = System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(1);
			} catch (Exception ioe) {
			}

			// Get the data from players
			byte[] buf = new byte[1000];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (Exception ioe) {
				/* lazy exception handling :) */}

			serverData = new String(buf);
			serverData = serverData.trim();

			offscreen.getGraphics().clearRect(0, 620, 800, 100);
			offscreen.getGraphics().clearRect(0, 0, 800, 15);

			// offscreen.getGraphics().clearRect(0, 620, 800, 300);
			if (!connected && serverData.startsWith("CONNECTED")) {
				connected = true;
				System.out.println("Connected.");
				send("PLAYER " + name + " " + x + " " + healthPoints + " " + hitPoints);
			} else if (!connected) {
				System.out.println("Connecting..");
				send("CONNECT " + name);
			} else if (connected) {

				if (serverData.startsWith("PLAYER")) {
					long lala = System.currentTimeMillis() - lastLoopTime;
					lastLoopTime = System.currentTimeMillis();

					String[] playersInfo = serverData.split(":");
					for (int i = 0; i < playersInfo.length; i++) {
						String[] playerInfo = playersInfo[i].split(" ");
						this.currPlayer = pname;
						String pname = playerInfo[1];
						int posX = Integer.parseInt(playerInfo[2]);

						offscreen.getGraphics().fillOval(posX, 620, 50, 50);
						offscreen.getGraphics().drawString(pname + " position:" + playerInfo[2] + " health:"
								+ playerInfo[3] + " hitPoints:" + playerInfo[4], posX, 10);

						listOfAliens.clear();
						offscreen.getGraphics().clearRect(0, 17, 800, 600);
						int j;
						for (j = 5; j + 1 < playerInfo.length; j += 2) {
							NetAlien alien = new NetAlien(Integer.parseInt(playerInfo[j]),
									Integer.parseInt(playerInfo[j + 1]));

								offscreen.getGraphics().fillOval(Integer.parseInt(playerInfo[j]),
										Integer.parseInt(playerInfo[j + 1]), 20, 20);
								listOfAliens.add(alien);
							}

						}

						// frame.repaint();
					}

					if (serverData.startsWith("SHOT")) {
						String[] shotsInfo = serverData.split(":");
						for (int i = 0; i < shotsInfo.length; i++) {
							String[] shotInfo = shotsInfo[i].split(" ");

							int posX =  Integer.parseInt(shotInfo[1]);
							int posY =  Integer.parseInt(shotInfo[2]);

							offscreen.getGraphics().fillOval(posX, posY, 10, 10);
							
							// offscreen.getGraphics().clearRect(posX, posY, 10, 10);

					}

					// frame.repaint();
				}

				frame.repaint();
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
				// for(NetAlien alien: listOfAliens){
				// msg += alien.getX() + " " + alien.getY()+ " ";
				// }
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
				// send("SHOT " + shootCount);
				fire(shotCount, x);
				shotCount += 1;
				break;
			case KeyEvent.VK_LEFT:
				x -= xspeed;
				listOfAliens.remove(1);
				send("DEAD " + name + " 0");
				break;
			case KeyEvent.VK_RIGHT:
				x += xspeed;
				break;
			}

		}
	}

	public void fire(int shotCount, int shootPos){

	NetShot shot = new NetShot(shotCount, shootPos);
	// shot.shotThread(shot.getY());
	System.out.println("shotmade "+ shot.getX() + " " + shot.getY());
	// listOfShots.add(shot);

	Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				while (shot.getY() > 10) {
					Thread.sleep(1000);
					shot.move();
					System.out.println("moving "+ shot.getX() + " " + shot.getY());
					send("SHOT " + shot.getX() + " " + shot.getY());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	});
	thread.start();

	}

	// public void fire(int shootCount, int shootPos) {
	// 	NetShot shot = new NetShot(shootCount, shootPos);

	// 	Thread thread = new Thread(new Runnable() {
	// 		@Override
	// 		public void run() {
	// 			try {
	// 				while (y > 10) {
	// 					Thread.sleep(1000);
	// 					shot.move();
	// 					System.out.println("X: " + shot.getX() + " Y: " + shot.getY());
	// 					send("SHOT " + shootCount + " " + shot.getX() + " " + shot.getY());
	// 				}
	// 			} catch (InterruptedException e) {
	// 				e.printStackTrace();
	// 			}

	// 		}
	// 	});
	// 	thread.start();

	// }

	public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			System.out.println("Usage: java -jar circlewars-client <server> <player name>");
			System.exit(1);
		}

		new CircleWars(args[0], args[1], args[2], 15, 0);
	}
}
