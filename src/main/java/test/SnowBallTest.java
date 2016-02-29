package test;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import src.NodeMap;
import src.SnowBall;
import src.SnowBallStats;
import src.StreamEdge;




public class SnowBallTest {

	//test for addNode and addEdge
	@Test
	public void testCase1() {

		SnowBallStats stats = new SnowBallStats();
		NodeMap nodeMap = new NodeMap();
		StreamEdge edge = new StreamEdge("A","B");
		addEdge(edge,nodeMap);
		SnowBall snowBall = new SnowBall(false,stats);
		snowBall.addNode("A", nodeMap);
		snowBall.addNode("B", nodeMap);
		snowBall.addEdge(edge);
		assertEquals(snowBall.getNumNodes(),2);
		assertEquals(snowBall.getNumEdges(),1);
	}
	
	//test for kCore Number
		@Test
		public void testCase2() {
			SnowBallStats stats = new SnowBallStats();
			NodeMap nodeMap = new NodeMap();
			StreamEdge edge1 = new StreamEdge("A","B");
			StreamEdge edge2 = new StreamEdge("B","C");
			StreamEdge edge3 = new StreamEdge("C","A");
			addEdge(edge1,nodeMap);
			addEdge(edge2,nodeMap);
			addEdge(edge3,nodeMap);
		
			SnowBall snowBall = new SnowBall(false,stats);
			snowBall.addNode("A", nodeMap);
			snowBall.addNode("B", nodeMap);
			snowBall.addNode("C", nodeMap);
			
			snowBall.addEdge(edge1);
			snowBall.addEdge(edge2);
			snowBall.addEdge(edge3);
			
			assertEquals(snowBall.getCoreNumber("A"),2);
			assertEquals(snowBall.getCoreNumber("B"),2);
			assertEquals(snowBall.getCoreNumber("C"),2);
			
			snowBall.removeEdge(edge3, nodeMap);
			assertNotEquals(snowBall.getCoreNumber("B"),2);
			assertEquals(snowBall.getCoreNumber("B"),1);
			
		}
		
		private void addEdge(StreamEdge edge, NodeMap nodeMap) {
			String src = edge.getSource();
			String dst = edge.getDestination();
			nodeMap.addNode(src, dst);
			nodeMap.addNode(dst, src);
		}

}
