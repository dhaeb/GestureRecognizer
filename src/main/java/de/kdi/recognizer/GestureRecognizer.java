package de.kdi.recognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.kdi.pojo.Gesture;
import de.kdi.pojo.GestureTemplate;
import de.kdi.pojo.GestureUtil;
import de.kdi.pojo.Point;
import de.kdi.pojo.TemplateCollection;

public class GestureRecognizer {

	private static final double GESTURE_MATCHING_LIMIT = 0.8;
	//Amount of points the data should be resampled to
	public static int N = 64;
	//Fitting square size
	public static double SQUARE_SIZE = 250.0;
    //Angel range
    private static double THETA = 45.0;
    //Angel precision
    private static double D_THETA = 2.0;
    
	private List<Point> points;
    
	public GestureRecognizer(List<Point> points) {
		this.points = points;
	}
	
    public Gesture[] recognize(TemplateCollection templates){
    	List<Point> ptsResampledFirst = $1transformationsFirstResample();
    	List<Point> ptsResampledLast = $1transformationsLastResample();
    	Gesture resampledFirst = recognize(ptsResampledFirst, templates.resampledFirstTemplates);
    	Gesture resampledLast = recognize(ptsResampledLast, templates.resampledLastTemplates);
    	return new Gesture[]{resampledFirst, resampledLast};
    }
    
    private List<Point> $1transformationsFirstResample() {
    	List<Point> ptsResampledFirst = cloneArray(points);
    	ptsResampledFirst = Point.resample(ptsResampledFirst, N);
    	ptsResampledFirst = processPoints(ptsResampledFirst);
    	return ptsResampledFirst;
    }

	private List<Point> $1transformationsLastResample() {
		List<Point> ptsResampledLast = cloneArray(points);
    	ptsResampledLast = processPoints(ptsResampledLast); 
    	ptsResampledLast = Point.resample(ptsResampledLast, N);
		return ptsResampledLast;
	}
    
    private List<Point> cloneArray(List<Point> points){
		List<Point> processedPoints = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
		Collections.copy(processedPoints, points);
		return processedPoints;
	}

	private List<Point> processPoints(List<Point> points) {
		//Rotate to 0 degree
		points = Point.rotate(points);
		//Scale to square
		points = Point.scale(points, SQUARE_SIZE);
		//Translate to origin
		points = Point.translate(points);
		return points;
	}
    
    private Gesture recognize(List<Point> points, List<GestureTemplate> templateList){
    	Gesture resultingGesture = new Gesture("No match", 0.0f);
    	double bestMatch = Double.MAX_VALUE;
    	int indexOfBestMatch = -1;
    	    	
    	//Find a match
    	for(int i=0; i < templateList.size(); i++){
    		double currentMatchedValue = 0.0;
    		currentMatchedValue = GestureUtil.distanceAtBestAngle(points, templateList.get(i), -THETA, THETA, D_THETA);
    		
    		if(currentMatchedValue < bestMatch){
    			bestMatch = currentMatchedValue;
    			indexOfBestMatch = i;
    		}
    	}
    	
    	float score = calculateMatchingScore(bestMatch);
    	
    	if(indexOfBestMatch > -1 && score > GESTURE_MATCHING_LIMIT){
    		//GESTURE MATCHED!
    		resultingGesture = new Gesture(templateList.get(indexOfBestMatch).name, score);
    	}
    	
    	return resultingGesture;
    }

	private float calculateMatchingScore(double bestMatch) {
		double twoTimesSquareSizeMultSqareSize = SQUARE_SIZE * SQUARE_SIZE + SQUARE_SIZE * SQUARE_SIZE;
		double lengthOfSquareSizeLine = 0.5 * Math.sqrt( twoTimesSquareSizeMultSqareSize );
		float diffenceValue = (float) (bestMatch/lengthOfSquareSizeLine);
		return 1.0f - diffenceValue;
	}
	
}
