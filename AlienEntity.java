
//
// class for creating an Alien 
//
public class AlienEntity extends Entity {

	//horizontal speed
	private double moveSpeed = 75;

	private Game game;
	
	private double posX, posY;
	
	 // Create a new alien entity
	public AlienEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
		dx = -moveSpeed;
		this.posX = x; this.posY = y;
	}

	/*
	if aliens reach leftmost 
		then move to the right
	if aliens reach rightmost
		then move to the left
	else no change in direction
	*/
	public void move(long delta) {
		
		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}
		

		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		}
		this.posX = x; 
		this.posY = y; 
		System.out.println(this.posX + " " + this.posY + "\n");
		super.move(delta);
	}
	
	/*
	 moves the aliens down the y axis
	*/
	public void doLogic() {
	
		dx = -dx;
		y += 20;

		int flag = 0;
		if (y >570) {
			
				//game.clearEntities();
				game.removeEntity(this); 
				game.removeRemaining();
				flag = 1;
		}

		// if(flag == 1){
		// 	for(Object e : game.getEntities()){

		// 		game.removeEntity(e);
		// 		game.removeRemaining();
		// 		if(game.getAlienCount() == 1)  break;
		// 	}
		// }
	
	}
	
	public void collidedWith(Entity other) {

		
	}
}