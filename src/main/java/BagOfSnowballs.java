import java.util.LinkedList;


public class BagOfSnowballs {
	LinkedList<SnowBall> bag = new LinkedList<SnowBall>();
	
	int getNumSnowBalls() {
		return bag.size();
	}
	
	SnowBall getSnowBall(int i) {
		return bag.get(i);
	}
	
	double getMaximalDensity() {
		double max = 0;
		for(int i =0;i<bag.size();i++) {
			double tempDensity = bag.get(i).getDensity();
			if(tempDensity > max) {
				max = tempDensity;
			}
		}
		return max;
	}
}
