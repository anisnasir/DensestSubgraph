package src;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;


public class SnowBallStatsEntry implements Serializable, Comparable<SnowBallStatsEntry>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1637934369139391975L;
	SnowBall a;
	HashMap<String,HashSet<String>> graph = new HashMap<String,HashSet<String>>();
	int numEdges = 0;
	
	void setKey(SnowBall b) {
		a=b;
	}
	void addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src))
			if(graph.get(src).contains(dst))
				return;
		addEdge(edge.getSource(),edge.getDestination());
		addEdge(edge.getDestination(),edge.getSource());
		numEdges++;
	}
	void removeEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src))
			if(graph.get(src).contains(dst)) {
				removeNode(edge.getSource(),edge.getDestination());
				removeNode(edge.getDestination(),edge.getSource());
				numEdges--;
			}
	}
	int addEdge(String src, String dest) { 
		if (graph.containsKey(src)) {
			HashSet<String> neighbors = graph.get(src);
			neighbors.add(dest);
			graph.put(src, neighbors);
			return neighbors.size();
		}else {
			HashSet<String> neighbors = new HashSet<String> ();
			neighbors.add(dest);
			graph.put(src, neighbors);
			return neighbors.size();
		}
	}
	int removeNode(String src, String dest) { 
		if(graph.containsKey(src))
		{
			HashSet<String> neighbors = graph.get(src);
			neighbors.remove(dest);
			if(!neighbors.isEmpty()) {
				graph.put(src, neighbors);
				return neighbors.size();
			}
			else {
				graph.remove(src);
				return 0;
			}
		}else 
			return 0;
		
	}
	void removeNode(String str) {
		HashSet<String> neighbors = new HashSet<String>(graph.get(str));
		if(neighbors != null)
			for(String neighbor:neighbors) {
				graph.get(neighbor).remove(str);
				numEdges--;
			}
		graph.remove(str);
	}
	int getNumEdges() {
		return this.numEdges;
	}
	@Override
	public int compareTo(SnowBallStatsEntry o) {
		return (a.equals(o.a))? 0:1;
	}
	boolean containsNode(String str) {
		return graph.containsKey(str);
	}
}