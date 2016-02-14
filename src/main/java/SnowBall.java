import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

public class SnowBall {
	double maximalDensity;
	double density;
	int numEdges;
	int numNodes;
	String id;
	Bahmani approximator = new Bahmani(0);
	
	HashMap<String,HashSet<String>> graph;
	KCoreDecomposition kCore;
	boolean LOGGING;
	SnowBall(boolean logging) {
		LOGGING = logging;
		id = UUID.randomUUID().toString();
		density = 0;
		graph = new HashMap<String,HashSet<String>>();
		kCore = new KCoreDecomposition(graph);
	}
	
	boolean containsNode (String src ) {
		return this.graph.containsKey(src);
	}
	void removeNode(String src) {
		HashSet<String> neighbors = graph.get(src);
		if(neighbors != null ) {
			HashSet<String> removeNodes = new HashSet<String>();
			for(String neighbor:neighbors) {
				graph.get(neighbor).remove(src);
				removeNodes.add(neighbor);
			}
			for( String neighbor:removeNodes) {
				graph.get(src).remove(neighbor);
				numEdges--;
				kCore.removeEdge(src, neighbor);
			}
		}
		graph.remove(src);
		kCore.removeNode(src);
		numNodes--;
	}
	void setMaximalDensity(double externalDensity, NodeMap nodeMap) {
		this.maximalDensity = Math.max(density,externalDensity);
	}
	
	Set<String> getNodes() {
		return graph.keySet();
	}
	double getDensity() {
		if(numNodes == 0)
			return 0;
		
		density = numEdges/(double)numNodes;
		//density = approximator.getApproximation((HashMap<String,HashSet<String>>)graph.clone(), numEdges, numNodes);
		return this.density;
	}
	
	int getIntersection (HashSet<String> nodes) {
		if(nodes == null)
			return 0;
		
		//return Sets.intersection(this.graph.keySet(),nodes).size();
		return intersection(this.graph.keySet(),nodes);
	}
	
	public int intersection (Set<String> set1, Set<String> set2) {
		Set<String> a;
		Set<String> b;
		int counter = 0;
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				counter++;
			} 
		}
		return counter;
	}
	public HashSet<String> intersectionSet (Set<String> set1, Set<String> set2) {
		Set<String> a;
		Set<String> b;
		HashSet<String> returnSet = new HashSet<String>();
		if (set1.size() <= set2.size()) {
			a = set1;
			b = set2; 
		} else {
			a = set2;
			b = set1;
		}
		for (String e : a) {
			if (b.contains(e)) {
				returnSet.add(e);
			} 
		}
		return returnSet;
	}
	
	void addNode(String src, NodeMap nodeMap) {
		HashSet<String> tempNeighbors = nodeMap.getNeighbors(src);
		if(tempNeighbors == null)
			return;
		
		HashSet<String> neighbors = intersectionSet(tempNeighbors,graph.keySet());
		
		graph.put(src, neighbors);
		kCore.addNode(src);
		for(String neighbor:neighbors) {
			graph.get(neighbor).add(src);
			kCore.addEdge(src, neighbor);
		}
		numEdges+=neighbors.size();
		numNodes++;
		return;
	}
	void ensureFirstInVariant(NodeMap nodeMap, HashSet<String> temp) {
		
		while(!verifyFirstInVariant(nodeMap, temp)) {
			//remove all the node with degrees lower than the density
			getDensity();
		}
	}
	
	boolean verifyFirstInVariant(NodeMap nodeMap, HashSet<String> temp) {
		/*apply Charikar in bulk
		 * removing all the nodes with degree 
		 * lower than the density
		 */
		boolean flag = true;
		double newDensity = getDensity();
		for(String str:graph.keySet()) {
			int globalDegree = nodeMap.getDegree(str);
			HashSet<String> neighbors = graph.get(str);
			int localDegree = neighbors.size();
			if(globalDegree < maximalDensity) {
				for(String neighbor: neighbors) {
					graph.get(neighbor).remove(str);
					kCore.removeEdge(str, neighbor);
				}
				numEdges-=localDegree;
				graph.remove(str);
				kCore.removeNode(str);
				numNodes--;
				return false;
			}else if (localDegree < newDensity) {
				for(String neighbor: neighbors) {
					graph.get(neighbor).remove(str);
					kCore.removeEdge(str, neighbor);
				}
				numEdges-=localDegree;
				graph.remove(str);
				kCore.removeNode(str);
				numNodes--;
				
				if(globalDegree > maximalDensity) {
					temp.add(str);
				}
				return false;
			}
			
		}
		return flag;
	}
	
	void ensureSecondInVariant(NodeMap nodeMap) {
		double mainCore = kCore.mainCore();
		double kmax = mainCore/2;
		HashSet<String> nodes = new HashSet<String>();
		for(String str: graph.keySet()) {
			if(kCore.getKCore(str) < kmax)
				nodes.add(str);
		}
		
		for(String str: nodes) {
			removeBulkNodes(str,nodeMap);
			graph.remove(str);
			kCore.removeNode(str);
		}
		this.getDensity();
	}
	
	void removeBulkNodes(String str, NodeMap nodeMap) {
		HashSet<String> neighbors = graph.get(str);
		if(neighbors == null)
			return;
		HashSet<String> removeNodes = new HashSet<String>();
		
		for(String neighbor:neighbors) {
			if(containsEdge(new StreamEdge(str,neighbor))) {
				removeNodes.add(neighbor);
			}
		}
		for(String nodes:removeNodes)
			removeEdge(new StreamEdge(str,nodes), nodeMap);
	}
	
	boolean contains(String src) {
		return graph.containsKey(src);
	}
	
	int getNumEdges() {
		return numEdges;
	}
	int getNumNodes() {
		return numNodes;
	}
	
	void merge(SnowBall newSnowBall, NodeMap nodeMap) {
		Set<String> nodes = newSnowBall.getNodes();
		for(String node:nodes) {
			this.addNode(node, nodeMap);
		}
		
	}
	
	void addEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src) )
			if(!graph.get(src).contains(dst)) {
				graph.get(src).add(dst);
				if(graph.containsKey(dst))
					if(!graph.get(dst).contains(src)) {
						graph.get(dst).add(src);
						numEdges++;
						kCore.addEdge(src, dst);
					}
			}
		
		
	}
	
	void print() {
		if(LOGGING) {
			System.out.println("Printing a SnowBall");
			System.out.println("Edges: " +this.numEdges + " Nodes: " + this.numNodes);
		}
		for (String name: graph.keySet()){

            String key =name;
            HashSet<String> value = graph.get(key);  
            System.out.print(key + " ["); 
            for(String str:value) {
            	System.out.print("<"+str+","+kCore.getKCore(str)+">");
            }
            System.out.println("]");
            
		} 
		
		if(LOGGING)
			System.out.println("Finished Printing the snowball");
	}
	
	boolean containsEdge(StreamEdge edge) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(graph.containsKey(src) && graph.containsKey(dst))
			if(graph.get(src).contains(dst) && graph.get(dst).contains(src))
				return true;
		
		return false;
	}
	
	void removeEdge(StreamEdge edge,NodeMap nodeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		
		graph.get(src).remove(dst);
		graph.get(dst).remove(src);
		kCore.removeEdge(src, dst);
		
		numEdges--;
	}
	boolean isEmpty() {
		return (this.getNumEdges()== 0 && this.getNumNodes() == 0);
	}
}
