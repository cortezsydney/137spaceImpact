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

class ShotThread extends Thread {
    private Thread t;
    private String threadName;
    
    ShotThread () {
       System.out.println("Creating ");
    }
    
    public void run() {
       System.out.println("Running " +  threadName );
       try {
          for(int i = 4; i > 0; i--) {
             System.out.println("Thread: " + threadName + ", " + i);
             // Let the thread sleep for a while.
             Thread.sleep(50);
          }
       }catch (InterruptedException e) {
          System.out.println("Thread " +  threadName + " interrupted.");
       }
       System.out.println("Thread " +  threadName + " exiting.");
    }
    
    public void start () {
       System.out.println("Starting " +  threadName );
       if (t == null) {
          t = new Thread (this, threadName);
          t.start ();
       }
    }
 }