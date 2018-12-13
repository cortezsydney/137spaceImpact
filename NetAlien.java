import java.lang.Thread;

public class NetAlien{
	private int posX, posY, type, healthPoints,originalX,alienCount;
	private String name;
    private int val=100;


	public NetAlien( String name, int x, int y, int type, int healthPoints) {
		this.posX = x;
		this.originalX = x;
		this.posY = y;
		this.name = name;
		this.type = type;
		this.healthPoints = healthPoints;
	}

    public void setX(int x){
		this.posX=x;
	}
	
	public int getX(){
		return this.posX;
	}
	
	public void setY(int y){
		this.posY = y;		
	}

	public int getY(){
		return this.posY;
	}

	public void move() {
		if(this.posY < 600){
			if(this.posX>700 || this.posX<100 ){
				this.posY+=50;
				this.posX = 150;
			}else {
				this.posX+=val;
			}
		}
	}

	public String toString(){
		String retval="";
		retval+="ALIEN ";
		retval+=this.name+" ";
		retval+=this.posX+" ";
		retval+=this.posY+" ";
		retval+=this.type+" ";
		retval+=this.healthPoints+" ";
		return retval;
	}	

	public void alienThread(){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					while(true){
						Thread.sleep(1000); move();
					}
					
				}catch(Exception e){}
			}
		  });
		thread.start();
    }
}