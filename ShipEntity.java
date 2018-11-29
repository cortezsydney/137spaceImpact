//
//player entity
//
public class ShipEntity extends Entity {
	
	private Game game;
// create new player entity
	public ShipEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
	}
	
	
	/*
	if player reach leftmost
	if player reach rightmost
		cant move
	else can move
	*/
	public void move(long delta) {
		

		if ((dx < 0) && (x < 10)) {
			return;
		}
		
		if ((dx > 0) && (x > 750)) {
			return;
		}
		
		super.move(delta);
	}
	
	//if player collides with aliens then deads
	public void collidedWith(Entity other) {
		if (other instanceof AlienEntity) {
			//game.notifyDeath();
			
			game.removeEntity(this);
			game.removeRemaining();
			game.startGame();
		}
	}
}