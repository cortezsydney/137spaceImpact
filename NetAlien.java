import java.lang.Thread;

public class NetAlien{
	private int posX, posY;
    private int val=-10;

	public NetAlien(int x, int y) {
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
        if(this.posX > 0 && this.posX < 800){
            this.posX += 100;
        System.out.println(posX);

		} 
        // else{
		// 	this.posY += 10;
        //     this.changePosition(val);
		// 	 System.out.println("2y");
        // }

		// System.out.println(posX + " " + posY);
	}

    // public void changePosition(int val){
    //     if(val == -10){
    //         this.val = 10;
	// 		System.out.println("3y");
    //     }else if(this.val == 10){
	// 		System.out.println("4y");
    //         this.val = -10;
    //     }
    // }


	public String toString(){
		String retval="";
		retval+="MAP ";
		retval+=this.posX+" ";
		retval+=this.posY+" ";
		return retval;
	}	

	// public void alienThread(){
	// 	Thread alienT = new Thread(new Runnable (){
	// 		@override
	// 		public void run(){
	// 			try{
	// 				this.move();
	// 			}catch(Exception e){

	// 			}
	// 		}
	// 	});
	// 	alienT.start();
	// }
	
	public void alienThread(){
      	Thread thread = new Thread(new Runnable(){
			@Override
			public void run(){

				try{
					while(true){
						Thread.sleep(1000); move();
					}
					
				}catch(Exception e){

				}
				
			}
		  });
		thread.start();
    }
}