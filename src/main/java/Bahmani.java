import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Bahmani {
	double epsilon;
	Bahmani(double epsilon) {
		
	}
	
	double getApproximation(HashMap<String,HashSet<String>> graph, int numEdges, int numNodes) {
		double density = numEdges/(double)numNodes;
		double max_density = density;
		Set<String> densest_subgraph = graph.keySet();
		while(!graph.isEmpty()) {
			density= removeNode(graph,density);
			if(density > max_density) {
				density = max_density;
				densest_subgraph = graph.keySet();
			}
		}
		return max_density;
	}
	
	double removeNode(HashMap<String,HashSet<String>> graph, double density) {
		HashSet<String> nodesRemove = new HashSet<String>();
		int numNodes = graph.size();
		int numEdges = 0;
		for(String str: graph.keySet()) {
			numEdges += graph.get(str).size();
			if(graph.get(str).size() < 2*(1+epsilon)*density)
				nodesRemove.add(str);	
		}
		numEdges/=2;
		
		for(String str: nodesRemove) {
			HashSet<String> neighbors = graph.get(str); 
			for(String neighbor: neighbors) {
				graph.get(str).remove(neighbor);
				graph.get(neighbor).remove(str);
				numEdges--;
			}
			graph.remove(str);
			numNodes--;
		}
		return numEdges/(double)numNodes;
	}
}
