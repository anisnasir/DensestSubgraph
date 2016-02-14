import java.util.HashMap;
import java.util.HashSet;



/*
 * Degree map store <degree, Set<String> nodes> in a HashMap
 */
public class DegreeMap {
	HashMap<Integer,HashSet<String>>  map;
	DegreeMap() {
		map = new HashMap<Integer,HashSet<String>> ();
	}
	
	void addNode(int degree, String nodeId) {
		if (map.containsKey(degree)) {
			HashSet<String> nodes = map.get(degree);
			nodes.add(nodeId);
			map.put(degree, nodes);
		}else {
			HashSet<String> neighbors = new HashSet<String> ();
			neighbors.add(nodeId);
			map.put(degree, neighbors);
		}
	}
	
	void removeNode(int degree, String nodeId) {
		if (map.containsKey(degree)) {
			HashSet<String> nodes = map.get(degree);
			nodes.remove(nodeId);
			map.put(degree, nodes);
		}
	}
	
	HashSet<String> getNodes(int degree) {
		return this.map.get(degree);
	}
	
	void incrementDegree(int degree, String nodeId) {
		removeNode(degree,nodeId);
		addNode(degree+1,nodeId);
	}
	
	void decremnetDegree(int degree, String nodeId) {
		removeNode(degree,nodeId);
		addNode(degree-1,nodeId);
	}
	
	HashSet<String> getNodesBetween(double upperBound, double lowerBound) {
		if(Math.floor(upperBound) < lowerBound) {
			return null;
		}else {
			return map.get((int)Math.round(lowerBound));
		}
	}
	

	
	
}
