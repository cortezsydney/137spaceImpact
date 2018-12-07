import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class Shot{
	//movement speed of shot
	private double moveSpeed = -300;
	private boolean used = false;
    private int x,y;
    BufferedImage offscreen;
    


	//create new shot from player
	public Shot(int x) {
        this.x = x;
        this.y = 600;

    }
  

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;

    }


    public void move(){
        if (this.y>10){
            this.y -= 20;
            System.out.println("Y: " + this.y);
            
        }
    }


    public void shotThread(int y){
       Thread thread = new Thread(new Runnable(){
          @Override
          public void run() { 
            try {
               while(y>10){
                  Thread.sleep(1000);
                  move();
               }
               // Thread.sleep(100);
               // move();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           
          }
       });
       thread.start();
    }

}

