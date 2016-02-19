package src;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class SnowBallStats {
	HashMap<SnowBall,LinkedList<SnowBallStatsEntry>> map = new HashMap<SnowBall,LinkedList<SnowBallStatsEntry>>();
	public void addEdge(SnowBall s1, SnowBall s2, StreamEdge edge) {
		addEdgeHelper(s1, s2, edge);
		addEdgeHelper(s2, s1, edge);
	}
	
	void addEdgeHelper(SnowBall s1, SnowBall s2, StreamEdge edge) {
		if(map.containsKey(s1)) {
			LinkedList<SnowBallStatsEntry> list = map.get(s1);
			for(SnowBallStatsEntry entry: list) {
				if(entry.a.equals(s2)) {
					entry.addEdge(edge);
					return;
				}
			}
 			SnowBallStatsEntry entry = new SnowBallStatsEntry();
			entry.setKey(s2);
			entry.addEdge(edge);
			list.add(entry);
		}else {
			LinkedList<SnowBallStatsEntry> list = new LinkedList<SnowBallStatsEntry>();
			SnowBallStatsEntry entry = new SnowBallStatsEntry();
			entry.setKey(s2);
			entry.addEdge(edge);
			list.add(entry);
			map.put(s1, list);
		}
	}
	
	public int getEdgesBetween(SnowBall s1, SnowBall s2) {
		if(map.containsKey(s1)) {
			LinkedList<SnowBallStatsEntry> list = map.get(s1);
			for(SnowBallStatsEntry entry:list) {
				if(entry.a.equals(s2))
					return entry.getNumEdges();
			}
		}
		return 0;
	}
	
	public void removeSnowBall(SnowBall snowBall) {
		if(map.containsKey(snowBall)) {
			LinkedList<SnowBallStatsEntry> list = map.get(snowBall);
			if(list.size() == 0) {
				map.remove(snowBall);
				return;
			}
			HashSet<SnowBall> removed = new HashSet<SnowBall>();
			for(SnowBallStatsEntry s: list) {
				remove(s.a, snowBall);
				if(s.numEdges == 0)
					removed.add(s.a);
			}
			for(SnowBall s: removed)
				removeSnowBall(s);
			map.remove(snowBall);
			
		}else {
			return;
		}
	}
	
	void remove(SnowBall from, SnowBall snowBall) {
		if(map.containsKey(from)){
			LinkedList<SnowBallStatsEntry> list = map.get(from);
			if(list.size() == 0)
				return;
			SnowBallStatsEntry temp = null;
			for(SnowBallStatsEntry entry:list ) {
				if(entry.a.id == snowBall.id) {
					temp = entry;
					break;
				}
			}
			list.remove(temp);
			return;
			
		}
		return;
	}
	
	public void removeNode(SnowBall snowBall, String node) {
		HashMap<SnowBall,String> redundant = new HashMap<SnowBall,String> ();
		if(map.containsKey(snowBall)) {
			LinkedList<SnowBallStatsEntry> list = map.get(snowBall);
			if(list.size()!=0) {
				for(SnowBallStatsEntry entry: list) {
					if(entry.containsNode(node)) {
						redundant.put(entry.a, node);
						entry.removeNode(node);
					}
				}
				removeNodeHelper(redundant, snowBall);
			}
		}
	}
	
	void removeNodeHelper(HashMap<SnowBall,String> redundant, SnowBall snowBall) {
		for(SnowBall s: redundant.keySet()) {
			String entry = redundant.get(s);
			LinkedList<SnowBallStatsEntry> list = map.get(s);
			for(SnowBallStatsEntry newEntry : list) { 
				if(newEntry.a.equals(snowBall))
					newEntry.removeNode(entry);
			}
		}
	}
	
	public int getSize() {
		return this.map.size();
	}
}
