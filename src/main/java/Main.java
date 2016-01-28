

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class Main {
	private static void ErrorMessage() {
		System.err.println("Error!");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 0) {
			ErrorMessage();
		}

		//String inFileName = "/Users/anis/Datasets/com-lj.ungraph.txt";
		String inFileName= "/Users/anis/Datasets/snap_facebook.txt";
		String sep = " ";
		BufferedReader in = null;
        try {
            InputStream rawin = new FileInputStream(inFileName);
            if (inFileName.endsWith(".gz"))
                rawin = new GZIPInputStream(rawin);
            in = new BufferedReader(new InputStreamReader(rawin));
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            e.printStackTrace();
            System.exit(1);
        }

        StreamEdgeReader reader = new StreamEdgeReader(in,sep);
		StreamEdge item = reader.nextItem();
		NodeMap nodeMap = new NodeMap();
		DegreeMap degreeMap = new DegreeMap();
		int windowSize = 10000;
		FixedSizeSlidingWindow sw = new FixedSizeSlidingWindow(windowSize);
		
		while (item != null) {
			StreamEdge oldestEdge = sw.add(item);
			
			if(oldestEdge != null) {
				EdgeHandler.handleEdgeDeletion(oldestEdge, nodeMap, degreeMap);
				reader.edgeCount--;	
			}
			
			EdgeHandler.handleEdgeAddition(item,nodeMap,degreeMap);
			
			item = reader.nextItem();
		}
		
		System.out.println(reader.edgeCount);
		System.out.println(nodeMap.getNumNodes());
		// close all files
		in.close();
	}
	
	

}
