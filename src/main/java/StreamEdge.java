
public class StreamEdge{
	private String src;
	private String dest;

	StreamEdge(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}

	public String getSource() {
		return this.src;
	}

	public String getDestination() {
		return this.dest;
	}
	
	
	public String toString() {
		return this.src+ " "+this.dest;
	}
}
