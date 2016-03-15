package src;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

public class Main {
	private static void ErrorMessage() {
		System.err.println("Error!");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			ErrorMessage();
		}
		String directory = args[0];
		String fileName = args[1];
		int windowSize = Integer.parseInt(args[2]);
		Properties prop = new Properties();
		try {

			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
			input.close();
		} catch (Exception ex) {
			
		}
		boolean LOGGING = Boolean.parseBoolean(prop.get("LOGGING").toString());
		int k = Integer.parseInt(args[4]);
		String sep = "\t";
	
		
		String inFileName = directory + fileName;
		
		BufferedWriter output_insert = new BufferedWriter(new FileWriter("output_insertion_"+fileName));
		BufferedWriter output_remove = new BufferedWriter(new FileWriter("output_deletion_"+fileName));
		BufferedWriter output_density = new BufferedWriter(new FileWriter("output_density_"+fileName));
		
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
		int PRINT_INTERVAL=1000000;
		long simulationStartTime = System.currentTimeMillis();
		
		//Data Structures specific to the Algorithm
		NodeMap nodeMap = new NodeMap();
		DegreeMap degreeMap = new DegreeMap();
		UtilityFunctions utility = new UtilityFunctions();
		BagOfSnowballs bag = new BagOfSnowballs(fileName);
		
		//Initializing the window
		FixedSizeSlidingWindow sw = new FixedSizeSlidingWindow(windowSize);
		
		long flush_counter = 0;
		//Start reading the input
		System.out.println("Reading the input");
		int edgeCounter = 0;
		while (item != null) {
			if (++edgeCounter % PRINT_INTERVAL == 0) {
				System.out.println("Read " + edgeCounter/PRINT_INTERVAL
						+ "M edges.\tSimulation time: "
						+ (System.currentTimeMillis() - simulationStartTime)
						/ 1000 + " seconds");
				
			}
			
			if(++flush_counter%100000 == 0) {
				try {
					output_insert.flush();
					output_density.flush(); 
					output_remove.flush();
				}catch(Exception ex) {
					
				}
			}
			long insert_start_time = System.nanoTime();
			utility.handleEdgeAddition(item,nodeMap,degreeMap);
			bag.addEdge(item, nodeMap);
			Densest s = bag.getApproximation();
			if(LOGGING)
				bag.print();
			 
			long insert_time = (System.nanoTime()-insert_start_time);
			try {
				output_insert.write(insert_time+"\n");
				
			}catch(Exception ex) {
				
			}
			try {
				output_density.write(s.getDensity()+"\t"+ s.getDensest().size()+"\t" + bag.getNumOfSnowBalls()+"\n");
			}catch(Exception ex) {
				
			}
			StreamEdge oldestEdge = sw.add(item);
			
			
			if(oldestEdge != null) {
				long remove_start_time = System.nanoTime();
				utility.handleEdgeDeletion(oldestEdge, nodeMap, degreeMap);
				bag.removeEdge(oldestEdge,nodeMap, degreeMap);
				if(LOGGING) {
					bag.print();
					s = bag.getApproximation();
				}
				long remove_end_time  = System.nanoTime();
				double remove_time = (remove_end_time-remove_start_time);
				try {
				output_remove.write(remove_time+"\n");
				}catch(Exception ex) {
					
				}
				reader.edgeCount--;	
				
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
		try { 
			output_insert.flush();
			output_remove.flush();
			output_density.flush();
			
			output_insert.close();
			output_remove.close();
			output_density.close();
		}catch(Exception ex) {
			
		}
		System.out.println("Read " + edgeCounter
				+ " M edges \t"+nodeMap.getNumNodes()+" nodes (Last Window) \tSimulation time: "
				+ (System.currentTimeMillis() - simulationStartTime)
				/ 1000 + " seconds"
				+"\tMaximal Density: "+bag.getMaximalDensity(nodeMap));
		// close all files
		in.close();
		
		LinkedList<Densest> topK = bag.getTopK(k);
		printTopK(topK);
	}
	
	static void printTopK(LinkedList<Densest> topK) {
		System.out.println("Priting top " + topK.size() );
		for(int i =0;i <topK.size();i++) {
			Densest d = topK.get(i);
			d.print();
		}
	}
	
	

}
