import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Bahmani {
	double epsilon;
	Bahmani(double epsilon) {
		this.epsilon = epsilon;
	}
	
	HashMap<String,HashSet<String>> deepCopy(HashMap<String,HashSet<String>> graph) {
		HashMap<String,HashSet<String>> returnGraph = new HashMap<String,HashSet<String>>();
		for(String str:graph.keySet()) {
			HashSet<String> neighbors = new HashSet<String>(graph.get(str));
			returnGraph.put(str, neighbors);
		}
		return returnGraph;
		
	}
	
	Densest getApproximation(HashMap<String,HashSet<String>> graph, int numEdges, int numNodes) {
		Densest returnResult = new Densest();
		graph = deepCopy(graph);
		
		Set<String> densest_subgraph = new HashSet<String>(graph.keySet());
		double density = numEdges/(double)numNodes;
		double max_density = density;
		int new_edges = numEdges;
		while(!graph.isEmpty()) {
			new_edges = removeNode(graph,density, new_edges);
			
			density = (graph.size()==0) ? 0  : (new_edges/(double)graph.size());
			if(density > max_density) {
				max_density = density;
				densest_subgraph =new HashSet<String>(graph.keySet());;
			}
		}
		returnResult.setDensity(max_density);
		returnResult.setDensest(densest_subgraph);
		return returnResult;
	}
	
	int removeNode(HashMap<String,HashSet<String>> graph, double density,int numEdges) {
		double threshold = 2*(1+epsilon)*density;
		HashSet<String> nodesRemove = new HashSet<String>();
		for(String str: graph.keySet()) {
			if(graph.get(str).size() <= threshold) {
				nodesRemove.add(str);	
			}
		}
		for(String str: nodesRemove) {
			HashSet<String> neighbors = graph.get(str); 
			for(String neighbor: neighbors) {
				graph.get(neighbor).remove(str);
				numEdges--;
			}
			graph.remove(str);
		}
		return numEdges;
	}
}
