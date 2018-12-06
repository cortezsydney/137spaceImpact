import java.net.InetAddress;


public class NetAlien{
	private int posX, posY;
    private int val=-10;
	private boolean alive;

	public NetAlien(int x, int y) {
        this.alive = true;
		this.posX = x;
		this.posY = y;
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

        if(posX < 0 || posX > 800){
            this.posY -= 30;
        } 
        else{
            this.changePosition(val);
            this.posX += val;
        }

	}

    public void changePosition(int val){
        if(val == -10){
            this.val = 10;
        }else if(this.val == 10){
            this.val = -10;
        }
    }
	
	public void doLogic() {
		posY += 20;

		if (posY >570) {
			this.alive = false;
		}
	}

    public boolean getAlive(){
        return this.alive;
    }

	public void setAlive(boolean x){
        this.alive = x;
    }

	public String toString(){
		String retval="";
		retval+="MAP ";
		retval+=this.posX+" ";
		retval+=this.posY+" ";
		return retval;
	}	
}