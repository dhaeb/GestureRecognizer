package de.kdi.recognizer;

import java.util.List;

import de.kdi.pojo.GestureResult;
import de.kdi.pojo.GestureTemplate;
import de.kdi.pojo.GestureUtil;
import de.kdi.pojo.Point;
import de.kdi.pojo.TemplateCollection;
import de.kdi.recognizer.dollar1.ResampleFirstPointProcessor;
import de.kdi.recognizer.dollar1.ResampleLastPointProcessor;

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
	
    public GestureResult[] recognize(TemplateCollection templates){
    	List<Point> ptsResampledFirst = new ResampleFirstPointProcessor(points).preparePointsFor$1();
    	List<Point> ptsResampledLast = new ResampleLastPointProcessor(points).preparePointsFor$1();
    	GestureResult resampledFirst = recognize(ptsResampledFirst, templates.resampledFirstTemplates);
    	GestureResult resampledLast = recognize(ptsResampledLast, templates.resampledLastTemplates);
    	return new GestureResult[]{resampledFirst, resampledLast};
    }
    
    private GestureResult recognize(List<Point> points, List<GestureTemplate> templateList){
    	GestureResult resultingGesture = new GestureResult("No match", 0.0f);
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
    		resultingGesture = new GestureResult(templateList.get(indexOfBestMatch).name, score);
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
