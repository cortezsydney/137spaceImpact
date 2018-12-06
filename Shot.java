import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.util.concurrent.TimeUnit;

public class Shot {
	//movement speed of shot
	private double moveSpeed = -300;
	private boolean used = false;
    private int x,y;
    BufferedImage offscreen;
    
    
	//create new shot from player

	public Shot(int x) {
        this.x = x;
        this.y = 600;

        // this.offscreen = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
        // Graphics g = offscreen.getGraphics();
        // g.drawString("shot", this.x,this.y);
    
    }
    
    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;

    }

    public void move(){
        while (this.y>10){
            this.y -= 20;
            System.out.println("Y: " + this.y);
        }
    }

}