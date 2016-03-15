package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Densest {
	double density;
	HashMap<String,ArrayList<String>> densest;
	public double getDensity() {
		return density;
	}
	public void setDensity(double density) {
		this.density = density;
	}
	public HashMap<String,ArrayList<String>> getDensest() {
		return densest;
	}
	public void setDensest(HashMap<String,ArrayList<String>> densest_subgraph) {
		this.densest = densest_subgraph;
	}
	
	void print() {
		System.out.println("Density:" + density);
		for(String str:densest.keySet()) {
			System.out.println(str+" " + densest.get(str));
		}
	}
	
}
