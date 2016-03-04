package src;
import java.util.LinkedList;


public class FixedSizeSlidingWindow {
	
	public int windowSize;
	LinkedList<StreamEdge> fifo;
	FixedSizeSlidingWindow() {
		windowSize = 0;
		fifo = new LinkedList<StreamEdge>();
	}
	FixedSizeSlidingWindow(int wSize) {
		windowSize = wSize;
		fifo = new LinkedList<StreamEdge>();
	}
	
	StreamEdge add(StreamEdge newEdge) {
		fifo.addLast(newEdge);
		if(fifo.size() >=windowSize) {
			StreamEdge returnEdge = fifo.removeFirst();
			return returnEdge;
		}else {
			return null;
		}
	}
}
