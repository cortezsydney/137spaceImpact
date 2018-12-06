
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapState{
	private Map aliens=new HashMap();

	public MapState(){}
	
	public void update(int i, NetAlien alien){
		aliens.put(i, alien);
	}

	public String toString(){
		String retval="";
		for(Iterator ite = aliens.keySet().iterator(); ite.hasNext();){
			String iterator = (String)ite.next();
			NetAlien alien = (NetAlien)aliens.get(iterator);
			retval+=alien.toString()+":";
		}
		return retval;
	}
	
	public Map getAliens(){
		return aliens;
	}
}
