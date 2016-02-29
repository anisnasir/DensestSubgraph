package src;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class BagOfSnowballs {
	LinkedList<SnowBall> bag;
	double maximalDensity = 0;
	boolean LOGGING;
	int count = 0; 
	int k =3;
	SnowBallStats stats;
	
	
	public BagOfSnowballs(boolean logging) {
		LOGGING = logging;
		bag = new LinkedList<SnowBall>();
		stats = new SnowBallStats();
	}

	public void addEdge(StreamEdge edge, NodeMap nodeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);

		if(LOGGING) {
			System.out.println("+ ("+ src+ ", "+dst+")");
			System.out.println("srcDegree: "+ srcDegree+ ", dstDegree: "+dstDegree+", maximal density: "+ maximalDensity);
		}
		
		SnowBall temp = null;
		if (srcDegree < maximalDensity && dstDegree < maximalDensity) {
			return;
		} else if (srcDegree >= maximalDensity && dstDegree < maximalDensity ) {
			temp = addNode(src,nodeMap);
			ensureInvariant(temp,nodeMap);

		} else if (srcDegree < maximalDensity && (double)dstDegree >= maximalDensity ) {
			temp = addNode(dst,nodeMap);
			ensureInvariant(temp,nodeMap);
		} else {
			SnowBall srcSnowBall = addNode(src,nodeMap);
			ensureInvariant(srcSnowBall,nodeMap);
			SnowBall dstSnowBall = addNode(dst, nodeMap);
			ensureInvariant(dstSnowBall,nodeMap);
			
			if(srcSnowBall.equals(dstSnowBall)) {
				srcSnowBall.addEdge(edge);
				ensureInvariant(srcSnowBall,nodeMap);
			}else {
				stats.addEdge(srcSnowBall, dstSnowBall, edge);
				if(this.canMerge(srcSnowBall,dstSnowBall, nodeMap)) {
					merge(srcSnowBall,dstSnowBall,nodeMap);
					ensureInvariant(srcSnowBall,nodeMap);
				}
			}
		}
		synchronizeSnowBalls(nodeMap);
		verifyConnectivity(nodeMap);
		synchronizeSnowBalls(nodeMap);
		cleanup(nodeMap);
		
		if(LOGGING) {
			System.out.println("Maximal Density: "+this.getMaximalDensity(nodeMap));
			System.out.println("Number of SnowBalls: "+this.getNumOfSnowBalls());
		}


	}
	
	SnowBall addNode(String src, NodeMap nodeMap) {
		int maxIntersection = 0;
		SnowBall max = null;
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		for(SnowBall s: bag) {
			if(s.contains(src)) {
				return s;
			}
			int internalDegree = s.getIntersection(neighbors);

			if(internalDegree != 0 && internalDegree >= s.getDensity() && internalDegree >= maxIntersection && internalDegree >= s.getMainCore()  ) {
				max = s;
				maxIntersection = internalDegree;
			}
		}
		if(max == null) {
			SnowBall newBall = new SnowBall(LOGGING,stats);
			newBall.addNode(src, nodeMap);
			newBall.setMaximalDensity(this.getMaximalDensity(nodeMap), nodeMap);
			bag.add(newBall);
			updateStats(newBall,src,nodeMap);
			return newBall;
		}else {
			max.addNode(src, nodeMap);
			ensureInvariant(max,nodeMap);
			updateStats(max,src,nodeMap);
			return max;
		}
	}
	
	void updateStats(SnowBall s, String str, NodeMap nodeMap) {
		HashSet<String> neighbors = nodeMap.getNeighbors(str);
		for(int i =0;i<bag.size();i++) {
			SnowBall other = bag.get(i);
			if(!s.equals(other)) {
				HashSet<String> edges = s.intersectionSet(neighbors, other.getNodes());
				for(String neighbor: edges)
				{
					stats.addEdge(s, other, new StreamEdge(str,neighbor));
				}
			}
		}
	}
	
	boolean canMerge(SnowBall s1, SnowBall s2, NodeMap nodeMap) {
		if(s1.id == s2.id) 
			return false;
		else {
			Set<String> s1Nodes = s1.getNodes();
			Set<String> s2Nodes = s2.getNodes();
		
			int C1 = s1.getMainCore();
			int C2 = s2.getMainCore();
			
			if(C1 >= C2) {
				for(String s: s2Nodes) {
					//HashSet<String> neig = nodeMap.getNeighbors(s);
					//if(neig != null) {
						//int E12= s1.intersection(neig, s1Nodes);
					int E12 = stats.getEdgesFromNodetoSnowBall(s2, s, s1);
					if(E12 >= C1 && E12 !=0)
						return true;
					//}
				}
			} else {
				for(String s: s1Nodes) {
					//HashSet<String> neig = nodeMap.getNeighbors(s);
					//if(neig != null) {
						//int E12 =s2.intersection(neig, s2Nodes);
					int E12 = stats.getEdgesFromNodetoSnowBall(s1, s, s2);
					if(E12 >= C2 && E12 != 0)
						return true;
					//}
				}
			}
			
			return false;
		}
	} 
	
	/*boolean canMerge(SnowBall s1, SnowBall s2, NodeMap nodeMap) {
		if(s1.id == s2.id) 
			return false;
		else {
			Set<String> s1Nodes = s1.getNodes();
			Set<String> s2Nodes = s2.getNodes();
		
			int V1 = s1Nodes.size();
			int V2 = s2Nodes.size();

			int E1 = s1.numEdges;
			int E2 = s2.numEdges;
			
			int C1 = s1.getMainCore();
			int C2 = s2.getMainCore();
			int maxC = Math.max(C1, C2);

			double rho1 = s1.getDensity();
			double rho2 = s2.getDensity();

			double max = Math.max(rho1,rho2);

			/*int E12=0;
			if(s1Nodes.size() < s2Nodes.size()) {
				for(String s: s1Nodes) {
					HashSet<String> neig = nodeMap.getNeighbors(s);
					if(neig != null) {
						E12+=s1.intersection(neig, s2Nodes);
					}
				}
			} else {
				for(String s: s2Nodes) {
					HashSet<String> neig = nodeMap.getNeighbors(s);
					if(neig != null) {
						E12+=s2.intersection(neig, s1Nodes);
					}
				}
			}
			//return false;
			int E12 = stats.getNumEdgesBetween(s1, s2);
			//if(E121 != E12)
				//System.out.println("Error !!!! Errror !!!! Error !!!! Error!!!!! Error!!!! E121 " + E121 + " E12 " + E12 + " " + s1Nodes + " " + s2Nodes);
			//System.out.println("E12:" + E12);
			
			//return (E12>0);
			if( E12 == 0)
				return false;
			
			double newDensity = (E1+E2+E12)/((double)(V1+V2));
			
			if(C1 == C2 && E12 > 0) {
				return true;
			}
			else if(newDensity >= max && E12 >= maxC)
				return true;
			else
				return false;
		}
	} */

	void cleanup(NodeMap nodeMap) {
		HashSet<SnowBall> snowBalls = new HashSet<SnowBall>();

		for(SnowBall s: bag) {
			if(s.getNumEdges()== 0 && s.getNumNodes() == 0)
				snowBalls.add(s);
		}

		for(SnowBall s: snowBalls)
		{
			bag.remove(s);
			stats.removeSnowBall(s);
		}
	}

	double getMaximalDensity(NodeMap nodeMap) {
		double max = 0;
		for(int i =0;i<bag.size();i++) {
			double tempDensity = bag.get(i).getDensity();
			if(tempDensity > max)
				max= tempDensity;
		}
		maximalDensity = max;

		for(SnowBall s: bag) 
			s.setMaximalDensity(maximalDensity, nodeMap);
		return maximalDensity;
	}
	public int getNumOfSnowBalls() {
		return bag.size();
	}
	int getNumSnowBalls() {
		return bag.size();
	}
	SnowBall getSnowBall(int i) {
		return bag.get(i);
	}
	void merge(SnowBall S1, SnowBall S2, NodeMap nodeMap) {
		if(S1.id == S2.id)  {
			return;
		} else {
			// merge S2 to S1
			S1.merge(S2, nodeMap);
			for(String node:S2.getNodes())
				updateStats(S1,node,nodeMap);
			bag.remove(S2);
			stats.removeSnowBall(S2);
		}
	}
	void print() {
		int i =0;
		for(SnowBall s: bag) {
			System.out.println("Density: " +s.getDensity());
			System.out.println("SnowBall "+(i+1));
			s.print();
			//System.out.println(s.getDensity());
			i++;
		}
	}
	void removeEdge(StreamEdge edge, NodeMap nodeMap, DegreeMap degreeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		if(LOGGING)
			System.out.println("- ("+ src+ ", "+dst+")");
		
		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);
		
		double currentMaximalDensity = this.getMaximalDensity(nodeMap);
		
		if((srcDegree+1) < maximalDensity && (dstDegree+1) < maximalDensity)
			return;
		else if((srcDegree+1) >= maximalDensity && (dstDegree+1) < maximalDensity) {
			if(srcDegree < maximalDensity) {
				for(SnowBall s: bag) {
					if(s.containsNode(src)) {
						s.removeNode(src);
						stats.removeNode(s, src);
						if(s.isEmpty()) {
							bag.remove(s);
							stats.removeSnowBall(s);
						}
						break;
					}	
				}	
			}
			
		} else if( (srcDegree+1) < maximalDensity &&(dstDegree+1) >= maximalDensity) {
			if(dstDegree < maximalDensity) {
				for(SnowBall s: bag) {
					if(s.containsNode(dst)) {
						s.removeNode(dst);
						stats.removeNode(s, dst);
						if(s.isEmpty()) {
							bag.remove(s);
							stats.removeSnowBall(s);
						}
						break;
					}	
				}
			}
		} else {
			SnowBall temp = null;
			SnowBall srcSnowBall = null;
			SnowBall dstSnowBall = null;
			for(int i =0 ;i< bag.size();i++) {
				SnowBall s = bag.get(i);
				if(s.containsEdge(edge)){
					s.removeEdge(edge,nodeMap);
					temp =s;
					ensureInvariant(s,nodeMap);
				}
				if(s.contains(src))
					srcSnowBall = s;
				
				if(s.contains(dst))
					dstSnowBall = s;
			}
			if(srcSnowBall != null && dstSnowBall != null) 
				if(!srcSnowBall.equals(dstSnowBall))
					stats.removeEdge(srcSnowBall, src, dstSnowBall, dst);
			
			if(temp!=null) {
				if(temp.contains(src) && temp.contains(dst)) {
					HashSet<String> visited = new HashSet<String>();
					if(!isConnected(temp,src,dst,visited)) {
						SnowBall newSnowBall = new SnowBall(LOGGING,stats);
						for(String s:visited) {
							temp.removeNode(s);
							stats.removeNode(temp, s);
							if(nodeMap.getDegree(s) >= maximalDensity) {
								newSnowBall.addNode(s, nodeMap);
								this.updateStats(newSnowBall, s, nodeMap);
							}
						}
						
						
						if(!newSnowBall.isEmpty())
							bag.add(newSnowBall);
					}
				}
			}
		}
		synchronizeSnowBalls(nodeMap);
		verifyConnectivity(nodeMap);
		cleanup(nodeMap);
		
		double updatedMaximalDensity = this.getMaximalDensity(nodeMap);
		if(currentMaximalDensity == updatedMaximalDensity)
			return;
		else {
			HashSet<String> addNodes = degreeMap.getNodesBetween(currentMaximalDensity, updatedMaximalDensity);
			if(addNodes == null){
				return;
			}

			for(String str: addNodes) {
				addNode(str,nodeMap);
			}
		}
		if(LOGGING) {
			System.out.println("Maximal Density: "+this.getMaximalDensity(nodeMap));
			System.out.println("Number of SnowBalls: "+this.getNumOfSnowBalls());
		}
		verifyConnectivity(nodeMap);
		synchronizeSnowBalls(nodeMap);
		cleanup(nodeMap);
	}
	
	void ensureInvariant(SnowBall s, NodeMap nodeMap) {
		HashSet<String> nodes = new HashSet<String>();
		s.ensureFirstInVariant(nodeMap,nodes);
		for(String node:nodes) {
			stats.removeNode(s, node);
			SnowBall temp = addNode(node,nodeMap);
			updateStats(temp, node, nodeMap);
		}
	}
	void synchronizeSnowBalls(NodeMap nodeMap) {
		double density = this.getMaximalDensity(nodeMap);
		for(int i = 0;i<bag.size();i++) {
			SnowBall s = bag.get(i);
			if(s.getDensity() < density ) {
				s.setMaximalDensity(density, nodeMap);
				ensureInvariant(s,nodeMap);	
				if(s.isEmpty())
					bag.remove(s);
			}
		}	
	}
	void verifyConnectivity(NodeMap nodeMap) {
		for(int i =0 ; i < bag.size()-1;i++) {
			HashMap<SnowBall,SnowBall> mergeable = new HashMap<SnowBall, SnowBall>();
			for(int j = i+1; j<bag.size();j++) {
				if (canMerge(bag.get(i),bag.get(j),nodeMap)) {
					mergeable.put(bag.get(i), bag.get(j));
				}
			}
			for(SnowBall s: mergeable.keySet()) {
				SnowBall temp = mergeable.get(s);
				merge(s,temp,nodeMap);
				ensureInvariant(s,nodeMap);
				bag.remove(temp);		
				stats.removeSnowBall(temp);
			}
		}
	}

	boolean isConnected(SnowBall snowBall, String src, String dst, HashSet<String> visited) {
		//System.out.println(src+ " " + visited);
		visited.add(src);
		HashSet<String> neighbors = snowBall.graph.get(src);
		if(neighbors == null)
			return false;
		if(neighbors.contains(dst))
			return true;
		for(String s:neighbors) {
			if(!visited.contains(s))
				if(isConnected(snowBall,s,dst,visited))
					return true;
		}
		return false;

	}
	Densest getApproximation() {
		
		Densest returnResults = new Densest();
		double maxDensity = 0;
		for(SnowBall s: bag) {
			if(s.density > maxDensity) {
				maxDensity = s.getDensity();
				returnResults.setDensity(maxDensity);
				returnResults.setDensest(s.graph);
			}
			
		}
		return returnResults;
	}
	
	LinkedList<Densest> getTopK(int k) {
		LinkedList<Densest> returnList = new LinkedList<Densest>();
		Collections.sort(bag, Collections.reverseOrder());
		int i =0;
		while(i< k && i<bag.size()) {
			SnowBall b = bag.get(i);
			Densest d = new Densest();
			d.setDensest(b.graph);
			d.setDensity(b.getDensity());
			returnList.add(d);
			i++;
		}
		return returnList;
	}
}
