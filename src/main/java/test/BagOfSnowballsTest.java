package test;
import static org.junit.Assert.*;

import org.junit.Test;

import src.BagOfSnowballs;
import src.NodeMap;
import src.SnowBall;
import src.StreamEdge;


public class BagOfSnowballsTest {

	//test for two disconnected edges
	@Test
	public void testCase1() {
		NodeMap nodeMap = new NodeMap();
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("C","D");
		addEdge(edge1,nodeMap);
		addEdge(edge2,nodeMap);
		BagOfSnowballs bag = new BagOfSnowballs("");
		bag.addEdge(edge1,nodeMap);
		
		assertEquals(bag.getNumOfSnowBalls(),1);
		bag.addEdge(edge2,nodeMap);
		
		assertEquals(bag.getNumOfSnowBalls(),2);
	}
	private void addEdge(StreamEdge edge, NodeMap nodeMap) {
		String src = edge.getSource();
		String dst = edge.getDestination();
		nodeMap.addNode(src, dst);
		nodeMap.addNode(dst, src);
	}
}
