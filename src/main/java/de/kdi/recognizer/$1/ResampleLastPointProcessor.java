package de.kdi.recognizer.$1;

import java.util.List;

import de.kdi.pojo.Point;

public class ResampleLastPointProcessor extends PointProcessorFor$1Algorithm {

	public ResampleLastPointProcessor(List<Point> points) {
		super(points);
	}

	@Override
	public List<Point> preparePointsFor$1() {
    	processPoints(); 
    	points = Point.resample(points, N);
		return points;
	}

}
