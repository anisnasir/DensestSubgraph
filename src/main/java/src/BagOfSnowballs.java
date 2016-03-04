package src;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class BagOfSnowballs {
	ArrayList<SnowBall> bag;
	double maximalDensity = 0;
	boolean LOGGING;
	int count = 0; 
	int k =3;
	SnowBallStats stats;


	public BagOfSnowballs(boolean logging) {
		LOGGING = logging;
		bag = new ArrayList<SnowBall>();
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

		double initialDensity= this.getMaximalDensity(nodeMap);
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
					mergeBulk(srcSnowBall,dstSnowBall,nodeMap);
					//ensureInvariant(srcSnowBall,nodeMap);
				}
			}
		}
		if(this.getMaximalDensity(nodeMap) != initialDensity) {
			synchronizeSnowBalls(nodeMap);
			double maxDensity = getMaximalDensity(nodeMap);
			verifyConnectivity(nodeMap);
			double newDensity = getMaximalDensity(nodeMap);
			if(newDensity != maxDensity) {
				synchronizeSnowBalls(nodeMap);
			}
		}
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
		for(int i =0;i<bag.size();i++) {
			SnowBall s = bag.get(i);
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
		if(neighbors == null)
			return;
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
			int maxC = Math.max(C1, C2);

			int E12 = 0;
			if(C1 >= C2) {
				ArrayList<String> removeNodes = new ArrayList<String>();
				for(String s: s2Nodes) {
					//HashSet<String> neig = nodeMap.getNeighbors(s);
					//if(neig != null) {
					//int E12= s1.intersection(neig, s1Nodes);
					int value = stats.getEdgesFromNodetoSnowBall(s2, s, s1);
					if(value >= C1 && value !=0) {
						removeNodes.add(s);
					}
					else {
						E12 +=value;
					}
					//}
				}
				for(String s:removeNodes) {
					s2.removeNode(s);
					s1.addNode(s, nodeMap);
					stats.removeNode(s2, s);
					updateStats(s1,s,nodeMap);
					C1 = s1.getMainCore();
				}
				if(s2.isEmpty())
					bag.remove(s2);
			} else {
				ArrayList<String> removeNodes = new ArrayList<String>();
				for(String s: s1Nodes) {
					//HashSet<String> neig = nodeMap.getNeighbors(s);
					//if(neig != null) {
					//int E12 =s2.intersection(neig, s2Nodes);
					int value = stats.getEdgesFromNodetoSnowBall(s1, s, s2);
					if(value >= C2 && value != 0) {
						removeNodes.add(s);
					}else {
						E12 += value;
					}
					//}
				}
				for(String s:removeNodes) {
					s1.removeNode(s);
					s2.addNode(s, nodeMap);
					stats.removeNode(s1, s);
					updateStats(s2,s,nodeMap);
					C2 = s2.getMainCore();
				}
				if(s1.isEmpty())
					bag.remove(s1);
			}
			if( E12 == 0)
				return false;

			if((E12 > 0 && C1 == C2)) {
				mergeBulk(s1,s2,nodeMap);
				return false;
			}else if(E12 >= maxC)
				return true;
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
		ArrayList<SnowBall> snowBalls = new ArrayList<SnowBall>();

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
	void mergeBulk(SnowBall S1, SnowBall S2, NodeMap nodeMap) {
		if(S1.id == S2.id)  {
			return;
		} else {
			// merge S2 to S1
			S1.graph.putAll(S2.graph);
			S1.kCore.kCore.putAll(S2.kCore.kCore);
			S1.numEdges+=S2.numEdges;
			S1.numNodes+=S2.numNodes;

			HashMap<String,HashSet<String>> graph = stats.getBulkEdges(S1, S2);
			if(graph != null) {
				for(String str:graph.keySet()) {
					HashSet<String> neighbors = graph.get(str);
					for(String neighbor:neighbors) {
						S1.addEdge(new StreamEdge(str,neighbor));
					}
				}
			}

			bag.remove(S2);
			stats.removeSnowBall(S2);
			S1.getDensity();
			ensureInvariant(S1,nodeMap);
		}
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

		int prevSrcDegree = srcDegree+1;
		int prevDstDegree = dstDegree+1;

		if(prevSrcDegree < maximalDensity && prevDstDegree < maximalDensity)
			return;
		
		double currentMaximalDensity = this.getMaximalDensity(nodeMap);
		boolean flag = false;

		if(prevSrcDegree >= maximalDensity ) {
			if(srcDegree < maximalDensity) {
				for(SnowBall s: bag) {
					if(s.containsNode(src)) {
						s.removeNode(src);
						stats.removeNode(s, src);
						ensureInvariant(s,nodeMap);
						if(s.isEmpty()) {
							bag.remove(s);
							stats.removeSnowBall(s);
						}
						flag = true;
						break;
					}	
				}	
			}

		}
		if( prevDstDegree >= maximalDensity) {
			if(dstDegree < maximalDensity) {
				for(SnowBall s: bag) {
					if(s.containsNode(dst)) {
						s.removeNode(dst);
						stats.removeNode(s, dst);
						ensureInvariant(s,nodeMap);
						if(s.isEmpty()) {
							bag.remove(s);
							stats.removeSnowBall(s);
						}
						flag = true;
						break;
					}	
				}
			}
		} 
		if(!flag) {
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
					ArrayList<String> visited = new ArrayList<String>();
					//checking for disconnected snowBalls
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
						ensureInvariant(newSnowBall,nodeMap);
						ensureInvariant(temp,nodeMap);
						if(!newSnowBall.isEmpty())
							bag.add(newSnowBall);
					}
				}
			}
		}

		if(currentMaximalDensity != this.getMaximalDensity(nodeMap)) {
			synchronizeSnowBalls(nodeMap);
			double maxDensity = getMaximalDensity(nodeMap);
			verifyConnectivity(nodeMap);
			double newDensity = getMaximalDensity(nodeMap);
			if(newDensity != maxDensity)
				synchronizeSnowBalls(nodeMap);
		}
		cleanup(nodeMap);

		double updatedMaximalDensity = this.getMaximalDensity(nodeMap);
		if(currentMaximalDensity != updatedMaximalDensity) {
			ArrayList<String> addNodes = degreeMap.getNodesBetween(currentMaximalDensity, updatedMaximalDensity);
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

		if(this.getMaximalDensity(nodeMap) != updatedMaximalDensity) {
			synchronizeSnowBalls(nodeMap);
			double maxDensity = getMaximalDensity(nodeMap);
			verifyConnectivity(nodeMap);
			double newDensity = getMaximalDensity(nodeMap);
			if(newDensity != maxDensity)
				synchronizeSnowBalls(nodeMap);
		}
		cleanup(nodeMap);
	}

	void ensureInvariant(SnowBall s, NodeMap nodeMap) {
		ArrayList<String> nodes = new ArrayList<String>();
		s.ensureFirstInVariant(nodeMap,nodes);
		for(int i =0;i<nodes.size();i++) {
			String node = nodes.get(i);
			stats.removeNode(s, node);
			SnowBall temp = addNode(node,nodeMap);
			updateStats(temp, node, nodeMap);
		}
	}
	void synchronizeSnowBalls(NodeMap nodeMap) {
		double density = this.getMaximalDensity(nodeMap);
		ArrayList<SnowBall> removable = new ArrayList<SnowBall> ();
		for(int i = 0;i<bag.size();i++) {
			SnowBall s = bag.get(i);
			if(s.getDensity() < density ) {
				s.setMaximalDensity(density, nodeMap);
				ensureInvariant(s,nodeMap);	
				if(s.isEmpty())
					removable.add(s);
			}
		}
		for(int i = 0 ;i<removable.size();i++) {
			SnowBall s = removable.get(i);
			bag.remove(s);
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
				mergeBulk(s,temp,nodeMap);
				//ensureInvariant(s,nodeMap);
				//bag.remove(temp);		
				//stats.removeSnowBall(temp);
			}
		}
	}

	boolean isConnected(SnowBall snowBall, String src, String dst, ArrayList<String> visited) {
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
