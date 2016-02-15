import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;



public class BagOfSnowballs {
	LinkedList<SnowBall> bag;
	double maximalDensity = 0;
	boolean LOGGING;
	int count = 0; 

	BagOfSnowballs(boolean logging) {
		LOGGING = logging;
		bag = new LinkedList<SnowBall>();
		
	}

	void addEdge(StreamEdge edge, NodeMap nodeMap) {

		String src = edge.getSource();
		String dst = edge.getDestination();
		int srcDegree = nodeMap.getDegree(src);
		int dstDegree = nodeMap.getDegree(dst);


		if(LOGGING) {
			System.out.println("+ ("+ src+ ", "+dst+")");
			System.out.println("srcDegree: "+ srcDegree+ ", dstDegree: "+dstDegree+", maximal density: "+ maximalDensity);
		}

		if (srcDegree < maximalDensity && dstDegree < maximalDensity) {
			return;
		} else if (srcDegree >= maximalDensity && dstDegree < maximalDensity ) {
			SnowBall temp = addNode(src,nodeMap);

			synchronizeSnowBalls(temp,nodeMap);

		} else if (srcDegree < maximalDensity && (double)dstDegree >= maximalDensity ) {
			SnowBall temp = addNode(dst,nodeMap);

			synchronizeSnowBalls(temp,nodeMap);
		} else {

			SnowBall srcSnowBall = addNode(src,nodeMap);
			SnowBall dstSnowBall = addNode(dst, nodeMap);

			if(srcSnowBall.id == dstSnowBall.id) {
				srcSnowBall.addEdge(edge);
				synchronizeSnowBalls(srcSnowBall,nodeMap);
			}else {
				if(this.canMerge(srcSnowBall, dstSnowBall, nodeMap)) {
					merge(srcSnowBall,dstSnowBall,nodeMap);	

					synchronizeSnowBalls(srcSnowBall,nodeMap);
				} else {
					synchronizeSnowBalls(srcSnowBall,nodeMap);
				}	
			}
		}
		this.getMaximalDensity(nodeMap);

		if(LOGGING) {
			System.out.println("Maximal Density: "+this.getMaximalDensity(nodeMap));
			System.out.println("Number of SnowBalls: "+this.getNumOfSnowBalls());
		}
		verifyConnectivity(nodeMap);
		cleanup(nodeMap);


	}

	SnowBall addNode(String src, NodeMap nodeMap) {
		int maxIntersection = 0;
		SnowBall max = null;
		HashSet<String> neighbors = nodeMap.getNeighbors(src);
		for(SnowBall s: bag) {
			if(s.contains(src))
				return s;
			int internalDegree = s.getIntersection(neighbors);

			if(internalDegree >= s.getDensity() && internalDegree > maxIntersection) {
				max = s;
				maxIntersection = internalDegree;
			}
		}
		if(max == null) {
			SnowBall newBall = new SnowBall(LOGGING);
			newBall.addNode(src, nodeMap);
			newBall.setMaximalDensity(this.getMaximalDensity(nodeMap), nodeMap);
			bag.add(newBall);

			return newBall;
		}
		max.addNode(src, nodeMap);
		HashSet<String> nodes = new HashSet<String>();
		max.ensureFirstInVariant(nodeMap,nodes);
		for(String node:nodes)
			addNode(node,nodeMap);
		return max;
	}

	
	boolean canMerge(SnowBall s1, SnowBall s2, NodeMap nodeMap) {
		if(s1.id == s2.id) 
			return false;
		else {

			Set<String> s1Nodes = s1.getNodes();
			Set<String> s2Nodes = s2.getNodes();

			int V1 = s1Nodes.size();
			int V2 = s2Nodes.size();

			int E1 = s1.numEdges;
			int E2 = s2.numEdges;

			double rho1 = s1.getDensity();
			double rho2 = s2.getDensity();

			double max = Math.max(rho1,rho2);

			int E12 = 0;
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
			if(E12 == 0)
				return false;

			double newDensity = (E1+E2+E12)/((double)(V1+V2));

			if(newDensity >= max)
				return true;
			else
				return false;
		}
	}

	void cleanup(NodeMap nodeMap) {
		HashSet<SnowBall> snowBalls = new HashSet<SnowBall>();

		for(SnowBall s: bag) {
			if(s.getNumEdges()== 0 && s.getNumNodes() == 0)
				snowBalls.add(s);
		}

		for(SnowBall s: snowBalls)
		{
			s.print();
			bag.remove(s);
		}
	}

	double getMaximalDensity(NodeMap nodeMap) {
		double max = 0;
		for(int i =0;i<bag.size();i++) {
			double tempDensity = bag.get(i).getDensity();
			if(tempDensity > max) {
				max = tempDensity;
			}
		}
		maximalDensity = max;

		for(SnowBall s: bag) 
			s.setMaximalDensity(maximalDensity, nodeMap);
		return max;
	}
	int getNumOfSnowBalls() {
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
			bag.remove(S2);
			HashSet<String> nodes = new HashSet<String>();
			S1.ensureFirstInVariant(nodeMap,nodes);
			for(String node:nodes)
				addNode(node,nodeMap);
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
			SnowBall temp = null;
			if(srcDegree < maximalDensity) {
				for(SnowBall s: bag) {
					if(s.containsNode(src)) {
						s.removeNode(src);
						temp = s;
						break;
					}	
				}
				if(temp!=null) {
					HashSet<String> nodes = new HashSet<String>();
					temp.ensureFirstInVariant(nodeMap,nodes);
					for(String node:nodes)
						addNode(node,nodeMap);
				}
			}
			
		} else if((srcDegree+1) < maximalDensity && (dstDegree+1) >= maximalDensity) {
			if(dstDegree <maximalDensity) {
				SnowBall temp = null;
				for(SnowBall s: bag) {
					if(s.containsNode(dst)) {
						s.removeNode(dst);
						temp = s;
						break;
					}	
				}
				if(temp!=null) {
					HashSet<String> nodes = new HashSet<String>();
					temp.ensureFirstInVariant(nodeMap,nodes);
					for(String node:nodes)
						addNode(node,nodeMap);
				}
			}
		} else {
			SnowBall temp = null;
			
			for(int i =0 ;i< bag.size();i++) {
				SnowBall s = bag.get(i);
				if(s.containsEdge(edge)){
					s.removeEdge(edge,nodeMap);
					temp =s;
					break;	
				}	
				HashSet<String> nodes = new HashSet<String>();
				s.ensureFirstInVariant(nodeMap,nodes);
				for(String node:nodes)
					addNode(node,nodeMap);
			}
			
			if(temp!=null) {
				HashSet<String> visited = new HashSet<String>();
				if(!isConnected(temp,src,dst,visited)) {
					SnowBall newSnowBall = new SnowBall(LOGGING);
					for(String s:visited) {
						temp.removeNode(s);
						if(nodeMap.getDegree(s) >= maximalDensity)
							newSnowBall.addNode(s, nodeMap);
					}
					HashSet<String> nodes = new HashSet<String>();
					newSnowBall.ensureFirstInVariant(nodeMap,nodes);
					for(String node:nodes)
						addNode(node,nodeMap);
					
					if(!newSnowBall.isEmpty())
						bag.add(newSnowBall);
				}
				if(dstDegree < maximalDensity)
					temp.removeNode(dst); 
				
				HashSet<String> nodes = new HashSet<String>();
				temp.ensureFirstInVariant(nodeMap,nodes);
				for(String node:nodes)
					addNode(node,nodeMap);
				if(temp.isEmpty())
					bag.remove(temp);
			
			}
		}
		
		double updatedMaximalDensity = this.getMaximalDensity(nodeMap);
		if(currentMaximalDensity == updatedMaximalDensity)
			return;
		else {
			HashSet<String> addNodes = degreeMap.getNodesBetween(currentMaximalDensity, updatedMaximalDensity);
			if(addNodes == null){
				getMaximalDensity(nodeMap);
				return;
			}

			for(String str: addNodes) {
				addNode(str,nodeMap);
			}
			getMaximalDensity(nodeMap);
		}
		if(LOGGING) {
			System.out.println("Maximal Density: "+this.getMaximalDensity(nodeMap));
			System.out.println("Number of SnowBalls: "+this.getNumOfSnowBalls());
		}
		verifyConnectivity(nodeMap);
		cleanup(nodeMap);
	}
	void synchronizeSnowBalls(SnowBall temp, NodeMap nodeMap) {
		double density = temp.getDensity();
		//nodes that are removed from SnowBall but can be added 
		
		for(int i = 0;i<bag.size();i++) {
			SnowBall s = bag.get(i);
			s.setMaximalDensity(density, nodeMap);	
			HashSet<String> nodes = new HashSet<String>();
			s.ensureFirstInVariant(nodeMap,nodes);
			for(String node: nodes) {
				addNode(node,nodeMap);
			}
			s.ensureSecondInVariant(nodeMap);
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
				merge(s,mergeable.get(s),nodeMap);
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
}
