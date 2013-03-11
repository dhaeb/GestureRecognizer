package de.kdi.pojo;

import java.util.List;


public class GestureUtil {
	
	public static double phi = 0.5*(Math.sqrt(5.0) - 1.0);

	public static double distanceAtBestAngle(List<Point> points, GestureTemplate template, double f, double theta, double dTheta) { 
		double x1 = phi*f + (1-phi)*theta;
		double f1 = distanceAtBestAngle(points, template, x1);
		double x2 = (1-phi)*f + phi*theta;
		double f2 = distanceAtBestAngle(points, template, x2);
		
		while((theta-f) > dTheta){
			if(f1 < f2){
				theta = x2;
				x2 = x1;
				f2 = f1;
				x1 = phi*f + (1-phi)*theta;
				f1 = distanceAtBestAngle(points, template, x1);
			} else {
				f = x1;
				x1 = x2;
				f1 = f2;
				x2 = (1-phi)*f + phi*theta;
				f2 = distanceAtBestAngle(points, template, x2);
			}
		}
		
		return Math.min(f1, f2);
	}

	private static double distanceAtBestAngle(List<Point> points, GestureTemplate t, double theta) {
		List<Point> newPoints = Point.rotate(points, theta);
		return pathDistance(newPoints, t.points);
	}

	private static double pathDistance(List<Point> listA, List<Point> listB) {
		double totalDistance = 0.0;
		int amountOfListMembers = listA.size();
		for(int i = 0; i < amountOfListMembers; i++){
			Point currentPointFromA = listA.get(i);
			Point currentPointFromB = listB.get(i);
			totalDistance += currentPointFromA.getDistance(currentPointFromB);
		}
		return (totalDistance/(double)amountOfListMembers);
	}
}
