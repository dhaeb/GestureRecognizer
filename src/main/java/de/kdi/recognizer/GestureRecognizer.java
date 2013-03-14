package de.kdi.recognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.kdi.pojo.Gesture;
import de.kdi.pojo.GestureTemplate;
import de.kdi.pojo.GestureUtil;
import de.kdi.pojo.Point;

public class GestureRecognizer {

	//Amount of points the data should be resampled to
	public static int N = 64;
	//Fitting square size
	public static double squareSize = 250.0;
    //Angel range
    private static double theta = 45.0;
    //Angel precision
    private static double dTheta = 2.0;
	//Holds the predefined templates
	public static ArrayList<GestureTemplate> resampledFirstTemplates = new ArrayList<GestureTemplate>();
	public static ArrayList<GestureTemplate> resampledLastTemplates = new ArrayList<GestureTemplate>();
	private List<Point> points;
    
	public GestureRecognizer(List<Point> points) {
		this.points = points;
	}
	
    public Gesture[] recognize(){
    	List<Point> ptsResampledFirst = processPoints(points, true);
    	List<Point> ptsResampledLast = processPoints(points, false);
    	
    	Gesture resampledFirst = recognize(ptsResampledFirst, true);
    	Gesture resampledLast = recognize(ptsResampledLast, false);
    	
    	return new Gesture[]{resampledFirst, resampledLast};
    }
    
    private List<Point> processPoints(List<Point> points, boolean resampleFirst){
		List<Point> newPoints = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
		Collections.copy(newPoints, points);
		
		if(resampleFirst){
			//Resample points to equidistance
			newPoints = Point.resample(newPoints, N);
		}
		
		//Rotate to 0 degree
		newPoints = Point.rotate(newPoints);
		
		//Scale to square
		newPoints = Point.scale(newPoints, squareSize);
		
		//Translate to origin
		newPoints = Point.translate(newPoints);
		
		if(!resampleFirst){
			//Resample last
			newPoints = Point.resample(newPoints, N);
		}
		
		return newPoints;
	}
    
    private Gesture recognize(List<Point> points, boolean resampleFirst){    
    	double best = Double.MAX_VALUE;
    	int t = -1;
    	    	
    	//Find a match
    	for(int i=0; i< resampledFirstTemplates.size(); i++){
    		double d = 0.0;
    		if(resampleFirst){
    			d = GestureUtil.distanceAtBestAngle(points, resampledFirstTemplates.get(i), -theta, theta, dTheta);
    		} else {
    			d = GestureUtil.distanceAtBestAngle(points, resampledLastTemplates.get(i), -theta, theta, dTheta);
    		}
    		
    		if(d < best){
    			best = d;
    			t = i;
    		}
    	}
    	
    	//Calculate score
    	float score = (float)(1.0 - (best/(0.5*Math.sqrt(squareSize*squareSize + squareSize*squareSize))));
    	
    	
    	if(t > -1 && score > 0.8){
    		//Return the matched gesture
    		if(resampleFirst){
    			return new Gesture(resampledFirstTemplates.get(t).name, score);
    		} else {
    			return new Gesture(resampledLastTemplates.get(t).name, score);
    		}
    	}
    	
    	return new Gesture("No match", 0.0f);
    }
	
}
