
public class UtilityFunctions {
	UtilityFunctions() {
		
	}
	public void handleEdgeAddition(StreamEdge item, NodeMap nodeMap, DegreeMap degreeMap ) {
		String src = item.getSource();
		String dest = item.getDestination();
		
		int predegree1 = nodeMap.getDegree(src);
		int predegree2 = nodeMap.getDegree(dest);
		
		//update node map
		nodeMap.addNode(src, dest);
		nodeMap.addNode(dest, src);			

		//update degree map
		degreeMap.incrementDegree(predegree1, src);
		degreeMap.incrementDegree(predegree2, dest);
	}
	
	public void handleEdgeDeletion(StreamEdge oldestEdge, NodeMap nodeMap, DegreeMap degreeMap ) {
		String oldSrc = oldestEdge.getSource();
		String oldDest = oldestEdge.getDestination();
		
		int oldSrcDegree = nodeMap.getDegree(oldSrc);
		int oldDestDegree = nodeMap.getDegree(oldDest);
		
		//update degree map
		degreeMap.decremnetDegree(oldSrcDegree, oldSrc);
		degreeMap.decremnetDegree(oldDestDegree, oldDest);
					
		//removes from each others neighbor table
		nodeMap.removeNode(oldSrc, oldDest);
		nodeMap.removeNode(oldDest, oldSrc);
	}
}
