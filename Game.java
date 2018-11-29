
import java.awt.Toolkit;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Game extends Canvas {
	//buffer to use acceleration
	private BufferStrategy strategy;
	// game loop
	private boolean gameRunning = true;
	//list of all entities
	private ArrayList entities = new ArrayList();
	//list of to be removed entities
	private ArrayList removeList = new ArrayList();
	//player
	private Entity ship;
	//player movement
	private double moveSpeed = 300;
	//time for fire delay
	private long lastFire = 0;
	//interval between shots
	private long firingInterval = 500;
	//number of aliens left
	private int alienCount;
	//message for waiting any key
	private String message = "";
	//used when waiting for key press
	private boolean waitingForKeyPress = true;
	//left button press
	private boolean leftPressed = false;
	//right button press
	private boolean rightPressed = false;
	// true if firing shots
	private boolean firePressed = false;
	//for game events
	private boolean logicRequiredThisLoop = false;
	
	
	public Game() {
		// frame to create the game

		JFrame container = new JFrame("cmsc137 SPACE SHIT");
		Toolkit.getDefaultToolkit().sync();	
		// content and resolution of the game

		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,700));
		panel.setLayout(null);
		

		//create canvas for the game
		setBounds(0,0,800,700);
		panel.add(this);
		
		

		setIgnoreRepaint(true);
		
		
		//make window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		//listener to close game

		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		//add keylistener to accept key inputs
		addKeyListener(new KeyInputHandler());
		
		// request the focus so key events come to us

		requestFocus();

	
		//support acceleration of entities
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		
		//create entities
		initEntities();
	}
	
	//create new game
	public void startGame() {

		//clear all enities
		entities.clear();
		initEntities();
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
	}
	
	//initialize entities :)
	private void initEntities() {
		

		ship = new ShipEntity(this,"sprites/alien.png",370,550);
		entities.add(ship);
		
		//block of aliens
		//edit shit 

		alienCount = 0;
		for (int row=0;row<5;row++) {
			for (int x=0;x<12;x++) {
				Entity alien = new AlienEntity(this,"sprites/alien.png",100+(x*50),(50)+row*30);
				entities.add(alien);
				alienCount++;
			}
		}
	}
	
	//update logic
	//used for alien movement
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}
	
	//remove entity
	public void removeEntity(Object entity) {
		removeList.add(entity);
	}
	//message when player dies
	public void notifyDeath() {
		message = "DEADS! QUIT CMSC!";
		waitingForKeyPress = true;
	}
	
	//message when player wins
	//message when player clears all aliens
	public void notifyWin() {
		message = "YOU WIN!";
		waitingForKeyPress = true;
	}
	
	//reduce alien count
	//used to notify if alien is killed
	//if no more alien then player wins
	public void removeRemaining(){
			entities.removeAll(removeList);
			removeList.clear();
			alienCount--;
			
}
 
	public int getAlienCount(){
		return alienCount;
	}
	public ArrayList getEntities(){

		return entities;
	}

	public void notifyAlienKilled() {
		

		alienCount--;
		
		if (alienCount == 0) {
			notifyWin();
		}
		
				
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				

				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}
	
	//fire shots if player is able to (firing interval)
	//fire a shot if time between shots is > firinginterval 
	public void tryToFire() {
		

		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}
		
		//create shot entity

		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.png",ship.getX()+10,ship.getY()-30);
		entities.add(shot);
	}
	
	/*
	 The main game loop. 
	 
	  Moving the game entities
	  Drawing contents
	  Updating game events
	  Checking Input
	 
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		
		//loop until exited or game ends(win or lose)

		while (gameRunning) {
			

			long lala = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			//graphics output

			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);
			
		
			//move alien entities	
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					
					entity.move(lala);
				}
			}
			
			//draw entities

			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				
				entity.draw(g);
			}
			
			
			//check collisions
			//forced
			for (int p=0;p<entities.size();p++) {
				for (int s=p+1;s<entities.size();s++) {
					Entity one = (Entity) entities.get(p);
					Entity two = (Entity) entities.get(s);
					
					if (one.collidesWith(two)) {
						one.collidedWith(two);
						two.collidedWith(one);
					}
				}
			}
			
			// remove any entity that has been marked for clear up

			entities.removeAll(removeList);
			removeList.clear();

			//resolve logic problems

			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
					System.out.println(entities.size());
					
				}
				if(entities.size() == 1){;
					startGame();
				}
				
				
				logicRequiredThisLoop = false;
			}
			
			//message when waiting for a any key

			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("Press any shit",(800-g.getFontMetrics().stringWidth("Press any shit"))/2,300);
			}
			
			//clear graphics

			g.dispose();
			strategy.show();
			
			//movement of the players ship
			//set initial to 0
			//update when left or right key is pressed 

			ship.setHorizontalMovement(0);
			
			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);
			}
			
			//fire fire fire


			if (firePressed) {
				tryToFire();
			}
			
			
		}
	}
	
	//keyboard key listeners
	private class KeyInputHandler extends KeyAdapter {
		
		private int pressCount = 1;
		
		//key pressed notification
		public void keyPressed(KeyEvent e) {
			//left right and space inputs
			if (waitingForKeyPress) {
				return;
			}
			
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
		} 
		
		//listeners when key is released
		public void keyReleased(KeyEvent e) {
			
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
		}
		//both pressed and released at the same time
		public void keyTyped(KeyEvent e) {

		

	


			if (waitingForKeyPress) {
				if (pressCount == 1) {
					
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			// iesc to quit game 

			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
	

	// start of the main game
	public static void main(String argv[]) {
		Game g =new Game();


		g.gameLoop();
	}
}