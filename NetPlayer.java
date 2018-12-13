import java.net.InetAddress;
import java.util.Random;
import java.util.ArrayList;


public class NetPlayer {
	private InetAddress address;
	private int port;
	private String name;
	private int x,y;
	private int position, healthPoints = 15, hitPoints = 0, mapId;
	private ArrayList<NetAlien> listOfAliens = new ArrayList<NetAlien>();
	
	public NetPlayer(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	public InetAddress getAddress(){
		return this.address;
	}

	public int getPort(){
		return this.port;
	}

	public int getMapId(){
		return this.mapId;
	}

	public String getName(){
		return this.name;
	}
	
	public void setX(int x){
		this.x=x;
		this.position = x;
	}
	
	public int getX(){
		return x;
	}
	
	public void setY(int y){
		this.y=400;		
	}

	public int getY(){
		return y;
	}

	public int getHealthPoints(){
		return this.healthPoints;
	}

	public void setHealthPoints(int var){
		this.healthPoints -= var;		
	}


	public int getHitPoints(){
		return this.hitPoints;
	}

	public void setHitPoints(int hitPoints){
		this.hitPoints += hitPoints;		
	}

	public void setAlien(ArrayList<NetAlien> listOfAliens){
		this.listOfAliens = listOfAliens;
	}

	public ArrayList<NetAlien> getListOfAliens(){
		return this.listOfAliens;
	}

	public void deleteAlien(int i){
		this.listOfAliens.remove(i);
	}

	public String toString(){
		String retval="";
		retval+="PLAYER ";
		retval+=name+" ";
		retval+=position+" ";
		retval+=healthPoints+" ";
		retval+=hitPoints+" ";

		for(NetAlien alien: this.listOfAliens){
			retval += alien.getX() + " " + alien.getY() + " ";
		} 
		return retval;
	}	
}
