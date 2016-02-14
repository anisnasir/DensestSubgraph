

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
		String directory = "/Users/anis/Datasets/Densest/";
		
		String fileName = "com-lj.ungraph.txt";
		//String fileName= "com-dblp.ungraph.txt";
		//String fileName= "snap_facebook.txt";
		//String fileName = "test_case10.txt";
		
		String inFileName = directory + fileName;
		String sep = "\t";
		
		BufferedWriter output_insert = new BufferedWriter(new FileWriter("output_insertion_"+fileName));
		BufferedWriter output_remove = new BufferedWriter(new FileWriter("output_deletion_"+fileName));
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

        //initialize the input reader
        StreamEdgeReader reader = new StreamEdgeReader(in,sep);
		StreamEdge item = reader.nextItem();
		
		//Declare outprint interval variables
		int PRINT_INTERVAL=100000;
		long simulationStartTime = System.currentTimeMillis();
		boolean LOGGING = false;
		
		//Data Structures specific to the Algorithm
		NodeMap nodeMap = new NodeMap();
		DegreeMap degreeMap = new DegreeMap();
		UtilityFunctions utility = new UtilityFunctions();
		BagOfSnowballs bag = new BagOfSnowballs(LOGGING);
		
		//Initializing the window
		int windowSize = 100000;
		FixedSizeSlidingWindow sw = new FixedSizeSlidingWindow(windowSize);
		
		//Start reading the input
		System.out.println("Reading the input");
		int edgeCounter = 0;
		while (item != null) {
			if (++edgeCounter % PRINT_INTERVAL == 0) {
				System.out.println("Read " + edgeCounter/100000
						+ "M edges.\tSimulation time: "
						+ (System.currentTimeMillis() - simulationStartTime)
						/ 1000 + " seconds");
				
			}
			long insert_start_time = System.currentTimeMillis();
			utility.handleEdgeAddition(item,nodeMap,degreeMap);
			bag.addEdge(item, nodeMap);
			if(LOGGING)
				bag.print();
			
			long insert_end_time  = System.currentTimeMillis();
			double insert_time = (insert_end_time-insert_start_time)/(double)(1000);
			try {
				output_insert.write(insert_time+"\n");
				output_insert.flush();
			}catch(Exception ex) {
				
			}
			
			StreamEdge oldestEdge = sw.add(item);
			
			long remove_start_time = System.currentTimeMillis();
			if(oldestEdge != null) {
				utility.handleEdgeDeletion(oldestEdge, nodeMap, degreeMap);
				bag.removeEdge(oldestEdge,nodeMap, degreeMap);
				if(LOGGING)
					bag.print();
				reader.edgeCount--;	
			}
			long remove_end_time  = System.currentTimeMillis();
			double remove_time = (remove_end_time-remove_start_time)/(double)1000;
			try {
			output_remove.write(remove_time+"\n");
			output_remove.flush();
			}catch(Exception ex) {
				
			}
			item = reader.nextItem();
			if(item !=null)
				while(nodeMap.contains(item) ) {
					item = reader.nextItem();
					if(item == null)
						break;
				}
			
			
		}
		
		bag.print();
		output_insert.close();
		output_remove.close();
		System.out.println("Read " + edgeCounter
				+ " M edges \t"+nodeMap.getNumNodes()+" nodes (Last Window) \tSimulation time: "
				+ (System.currentTimeMillis() - simulationStartTime)
				/ 1000 + " seconds"
				+"\tMaximal Density: "+bag.getMaximalDensity(nodeMap));
		// close all files
		in.close();
	}
	
	

}
