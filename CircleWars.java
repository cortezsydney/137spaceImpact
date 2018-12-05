
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.ArrayList;

public class CircleWars extends JPanel implements Runnable, Constants{
	JFrame frame= new JFrame();
	int x=10,y=10,xspeed=2,yspeed=2,prevX,prevY;
	
	Thread t=new Thread(this);
	String name="Joseph";
	int healthPoints, hitPoints;
	String pname;
	String server="localhost";

	boolean connected=false;
	DatagramSocket socket = new DatagramSocket();
	String serverData;
	BufferedImage offscreen;

	int alienCount = 0;
	ArrayList listOfAliens = new ArrayList();
	
	String pos;
	public CircleWars(String server,String name, String pos, int healthPoints, int hitPoints) throws Exception{
		this.server=server;
		this.name=name;
		this.healthPoints = 15;
		this.hitPoints = 0;
		this.pos = pos;
		frame.setTitle(APP_NAME + ":" + name);
		socket.setSoTimeout(100);
		
		//Some gui stuff i hate.
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		
		//create the buffer
		offscreen=(BufferedImage)this.createImage(640, 480);
		
		//Some gui stuff again...
		frame.addKeyListener(new KeyHandler());		
		frame.addMouseMotionListener(new MouseMotionHandler());

		//tiime to play

		for(int row = 0; row < 5; row++){
			for(int col = 0; col < 12; col++){
				NetAlien alien = new NetAlien(100+(col*50), 50+(row*30));
				listOfAliens.add(alien);
				alienCount += 1;
				offscreen.getGraphics().fillOval(100+(col*50), 50+(row*30), 20, 20);
			}
		}

		

		t.start();		
	}
	
	
	public void send(String msg){
		try{
			byte[] buf = msg.getBytes();
        	InetAddress address = InetAddress.getByName(server);
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        	socket.send(packet);
        }catch(Exception e){}
		
	}
	
	public void run(){
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
						
			//Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			socket.receive(packet);
			}catch(Exception ioe){/*lazy exception handling :)*/}
			
			serverData=new String(buf);
			serverData=serverData.trim();
		

			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
				System.out.println("Connected.");
				send("PLAYER "+ name + " " + x + " " + healthPoints + " " + hitPoints );
			}else if (!connected){
				System.out.println("Connecting..");				
				send("CONNECT "+name);
			}else if (connected){
				
				if (serverData.startsWith("PLAYER")){
					System.out.println("farfarfar---------------------------");
					String[] playersInfo = serverData.split(":");
					for (int i=0;i<playersInfo.length;i++){
						String[] playerInfo = playersInfo[i].split(" ");
						String pname =playerInfo[1];
						int posX = Integer.parseInt(playerInfo[2]);
						//draws player
						offscreen.getGraphics().fillOval(Integer.parseInt(pos), 250, 100, 100);
						// offscreen.getGraphics().drawString(pname+" position:"+playerInfo[2]+
						// 										" health:"+playerInfo[3]+
						// 										" hitPoints:"+playerInfo[4], posX, 500 );	

									
					}
					
					frame.repaint();
				}			
			}			
		}
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(offscreen, 0, 0, null);
	}

	class MouseMotionHandler extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent me){
			x=me.getX();
			y=me.getY();
			if (prevX != x || prevY != y){
				send("PLAYER "+ name + " " + x + " " + healthPoints + " " + hitPoints );
			}
		}
	}

	
	class KeyHandler extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			prevX=x;prevY=y;
			switch (ke.getKeyCode()){
				case KeyEvent.VK_SPACE: break;
				case KeyEvent.VK_LEFT:x-=xspeed;break;
				case KeyEvent.VK_RIGHT:x+=xspeed;break;
			}
			if (prevX != x || prevY != y){
				send("PLAYER "+ name + " " + x + " " + healthPoints + " " + hitPoints );
			}	
		}
	}
	
	
	public static void main(String args[]) throws Exception{
		if (args.length != 3){
			System.out.println("Usage: java -jar circlewars-client <server> <player name>");
			System.exit(1);
		}


		new CircleWars(args[0],args[1], args[2], 15, 0);
	}
}
