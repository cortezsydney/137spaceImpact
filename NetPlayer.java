import java.net.InetAddress;

public class NetPlayer {
	private InetAddress address;
	private int port;
	private String name;
	private int x,y;
	private int position, healthPoints = 15, hitPoints = 0, map = 10;

	public NetPlayer(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	public InetAddress getAddress(){
		return address;
	}

	public int getPort(){
		return port;
	}

	public String getName(){
		return name;
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
	
	public void setHealthPoints(int var){
		this.healthPoints -= var;		
	}

	public int getHealthPoints(){
		return this.healthPoints;
	}

	public void setHitPoints(int hitPoints){
		this.hitPoints += hitPoints;		
	}

	public int getHitPoints(){
		return this.hitPoints;
	}

	public void setMap(int map){
		this.map = map;		
	}

	public int getMap(){
		return this.map;
	}

	public String toString(){
		String retval="";
		retval+="PLAYER ";
		retval+=name+" ";
		retval+=position+" ";
		retval+=healthPoints+" ";
		retval+=hitPoints+" ";
		retval+=map+" ";
		return retval;
	}	
}
