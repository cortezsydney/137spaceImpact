
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public class ShotState{
	private Map shots=new HashMap();
	public ShotState(){}
	
	public void update(int shotCount, NetShot shot){
		shots.put(shotCount, shot);
	}

	public String toString(){
		String retval="MOVE ";
		for(Iterator ite = shots.keySet().iterator(); ite.hasNext();){
			int shotCount = (int)ite.next();
			NetShot shot = (NetShot)shots.get(shotCount);
			retval+=shot.toString()+":";
		}
		return retval;
	}

	
	public Map getShots(){
		return shots;
	}
}
