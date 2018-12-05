public class NetAlien{
	private double posX, posY;
    private int val=-10;
    private boolean alive;

	public NetAlien(int x,int y) {
		this.posX = x; this.posY = y;
        this.alive = true;
	}

    public void setX(int x){
		this.posX=x;
	}
	
	public double getX(){
		return this.posX;
	}
	
	public void setY(double y){
		this.posY = y;		
	}

	public double getY(){
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
}