
public class ShotEntity extends Entity {
	//movement speed of shot
	private double moveSpeed = -300;

	private Game game;

	private boolean used = false;
	
	//create new shot from player

	public ShotEntity(Game game,String sprite,int x,int y) {
		super(sprite,x,y);
		
		this.game = game;
		
		dy = moveSpeed;
	}

	//movement of the shot

	public void move(long delta) {
		// proceed with normal move

		super.move(delta);
		
		// if we shot off the screen, remove ourselfs

		if (y < -100) {
			game.removeEntity(this);
		}
	}
	

	/*
	if shot collides with alien
		remove shot 
		remove alien
		update aliens	
	*/

	public void collidedWith(Entity other) {
		

		if (used) {
			return;
		}
		
		// if we've hit an alien, kill it!

		if (other instanceof AlienEntity) {
			
			//remove alien

			game.removeEntity(this);
			game.removeEntity(other);
			
			// update game that an alien is gone

			game.notifyAlienKilled();
			used = true;
		}
	}
}