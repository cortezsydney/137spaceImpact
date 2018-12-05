//
//player entity
//
public class BossEntity extends Entity {
	private int healthPoints;
	private int hitPoints;
	private int position;
	private Game game;
// create new player entity
	public BossEntity(Game game,String ref,int x,int y) {
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
	public int getPosition(){
		return this.position;

	}
	public int getHealthPoints(){
		return this.healthPoints;

	}
	

	public void setPosition(int position){
		this.position = position;

	}

	public void setHealthPoints(int healthPoints){
		this.hitPoints += healthPoints;

	}
	
	
	
	//if player collides with aliens then deads
	public void collidedWith(Entity other) {
		if (other instanceof ShotEntity) {
			//game.notifyDeath();
			this.healthPoints -=1;
			System.out.println("healthPoints = "+healthPoints);
			game.removeEntity(this);
			game.removeEntity(other);
			game.notifyAlienKilled();
		}
	}
}