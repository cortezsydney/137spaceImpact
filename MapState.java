
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

public class MapState{
	private Map aliens=new HashMap();

	public MapState(){}
	
	public void update(String name, NetAlien player){
		aliens.put(name, player);
	}

	public String toString(){
		String retval="";
		for(Iterator ite = aliens.keySet().iterator(); ite.hasNext();){
			String name = (String)ite.next();
			NetAlien player = (NetAlien)aliens.get(name);
			retval+=player.toString()+":";
		}
		return retval;
	}
	
	public Map getliens(){
		return aliens;
	}
}
