import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class NetShot{
    private InetAddress address;
	//movement speed of shot
	private double moveSpeed = -300;
	private boolean used = false;
    private int x,y, shotCount;
    
    private static Ellipse2D.Double circle;
	private int port;
    
    private int width = 30, height = 30;
    private String name;


	//create new shot from , 
	public NetShot(int shotCount, int x) {
        this.name = name;
        this.x = x;
        this.y = 600;
        circle = new Ellipse2D.Double(0.5 * width, 0.9 * height,
                0.1 * width, 0.05 * height);  
    }
  
    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;

    }

    public void setX(int x){
		this.x=x;
	}
    
    public void setY(int y){
		this.y=y;		
	}

    public String toString(){
		String retval="";
		retval+="SHOT ";
        retval+=this.x+" ";
        retval+=this.y+" ";
		return retval;
    }	


    public void move(){
        this.y -= 20;
    }

}

