
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Everything {
	
	protected double x;
	
	protected double y;
	
	protected Sprite sprite;

	protected double dx;
	
	protected double dy;
	
	private Rectangle me = new Rectangle();
	
	private Rectangle him = new Rectangle();
	
	//create entity using sprite and location
	public Everything(String ref,int x,int y) {
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
	}
	
	//movement of an entity
	public void move(long delta) {
		// update the location of the entity based on move speeds

		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}
	
	//set horizontal speed
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	//set vertical speed
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}
	
	//get horizontal speed
	public double getHorizontalMovement() {
		return dx;
	}

	//get vertical speed
	public double getVerticalMovement() {
		return dy;
	}
	
	// draw entitiy to the ui
	public void draw(Graphics g) {
		sprite.draw(g,(int) x,(int) y);
	}
	
	
	public void doLogic() {
	}
	
	//get current x location of entity
	public int getX() {
		return (int) x;
	}

	//get current y location of entity
	public int getY() {
		return (int) y;
	}

	// collision with another entity checker
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x,(int) y,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	}
	
	// pass to other entity
	// lets them use collision checker
	public abstract void collidedWith(Entity other);
}