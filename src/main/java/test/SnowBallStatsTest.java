package test;
import static org.junit.Assert.*;

import org.junit.Test;

import src.SnowBallStats;
import src.SnowBall;
import src.StreamEdge;

public class SnowBallStatsTest {

	//test case for addition
	@Test
	public void testCase1() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("B","C");
		stats.addEdge(s1, s2, edge1);
		stats.addEdge(s1, s2, edge2);
		assertNotEquals(stats.getEdgesBetween(s1, s2),1);
		assertEquals(stats.getEdgesBetween(s1, s2),2);
	}
	
	//test for removeSnowBall
	@Test
	public void testCase2() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		stats.addEdge(s1, s2, edge1);
		stats.removeSnowBall(s2);
		assertEquals(stats.getEdgesBetween(s1, s2),0);
	}
	
	//test for multiple additions
	@Test
	public void testCase3() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		SnowBall s3 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("C","D");
		StreamEdge edge3 = new StreamEdge("A","D");
		stats.addEdge(s1, s2, edge1);
		stats.addEdge(s2, s3, edge2);
		stats.addEdge(s1, s3, edge3);
		assertEquals(stats.getEdgesBetween(s1, s2),1);
		assertEquals(stats.getEdgesBetween(s2, s3),1);
		assertEquals(stats.getEdgesBetween(s1, s3),1);
	}
	//test for mmultiple additions
	@Test
	public void testCase4() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		SnowBall s3 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("C","D");
		StreamEdge edge3 = new StreamEdge("A","D");
		StreamEdge edge4 = new StreamEdge("A","C");
		stats.addEdge(s1, s2, edge1);
		stats.addEdge(s2, s3, edge2);
		stats.addEdge(s1, s3, edge3);
		stats.addEdge(s1, s2, edge4);
		assertEquals(stats.getEdgesBetween(s1, s2),2);
		assertEquals(stats.getEdgesBetween(s2, s3),1);
		assertEquals(stats.getEdgesBetween(s1, s3),1);
	}
	//test for removeSnowBall
	@Test
	public void testCase5() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		SnowBall s3 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("C","D");
		StreamEdge edge3 = new StreamEdge("A","D");
		StreamEdge edge4 = new StreamEdge("A","C");
		stats.addEdge(s1, s2, edge1);
		stats.addEdge(s2, s3, edge2);
		stats.addEdge(s1, s3, edge3);
		stats.addEdge(s1, s2, edge4);
		assertEquals(stats.getEdgesBetween(s1, s2),2);
		assertEquals(stats.getEdgesBetween(s2, s3),1);
		assertEquals(stats.getEdgesBetween(s1, s3),1);
		stats.removeSnowBall(s1);
		assertEquals(stats.getSize(),2);
		
	}
	
	//test for remove Node
	@Test
	public void testCase6() {
		SnowBallStats stats = new SnowBallStats();
		SnowBall s1 = new SnowBall(true,stats);
		SnowBall s2 = new SnowBall(true,stats);
		SnowBall s3 = new SnowBall(true,stats);
		StreamEdge edge1 = new StreamEdge("A","B");
		StreamEdge edge2 = new StreamEdge("C","D");
		StreamEdge edge3 = new StreamEdge("A","D");
		StreamEdge edge4 = new StreamEdge("A","C");
		stats.addEdge(s1, s2, edge1);
		stats.addEdge(s2, s3, edge2);
		stats.addEdge(s1, s3, edge3);
		stats.addEdge(s1, s2, edge4);
		assertEquals(stats.getEdgesBetween(s1, s2),2);
		assertEquals(stats.getEdgesBetween(s2, s3),1);
		assertEquals(stats.getEdgesBetween(s1, s3),1);
		stats.removeNode(s1,"A");
		assertEquals(stats.getEdgesBetween(s1, s2),0);
		
	}
	
	

}
