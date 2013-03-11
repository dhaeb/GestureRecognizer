package de.kdi.pojo;

import java.util.ArrayList;
import java.util.List;

public class Point {
	
	public double x;
	public double y;
	
	public Point(double x, double y){ 
		this.x = x; 
		this.y = y;
	}
	
	public Point(int x, int y){
		this.x = (double) x;
		this.y = (double) y;
	}
	
	public double getDistance(Point comparePoint){
		double dx = comparePoint.x - x;
		double dy = comparePoint.y - y;
		return Math.sqrt(dx*dx + dy*dy);
	}

	public static List<Point> translate(List<Point> points) {
		Point c = centroid(points);
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = points.get(i).x - c.x;
			double qy = points.get(i).y - c.y;
			
			newPoints.add(new Point(qx, qy));
		}
		
		return newPoints;
	}

	public static List<Point> scale(List<Point> points, double squareSize) {
		BoundingBox boundingBox = new BoundingBox(points);
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = points.get(i).x*(squareSize/boundingBox.width);
			double qy = points.get(i).y*(squareSize/boundingBox.height);
			
			newPoints.add(new Point(qx, qy));
		}
		return newPoints;
	}

	public static List<Point> rotate(List<Point> points) {
		Point c = Point.centroid(points);
		
		//For -pi <= theta <= pi
		double theta = Math.atan2(c.y-points.get(0).y, c.x-points.get(0).x);
		
		return Point.rotate(points, -theta);
	}
	
	public static List<Point> rotate(List<Point> points, double d) {
		Point c = Point.centroid(points);
		
		List<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = (points.get(i).x - c.x)*Math.cos(d) - (points.get(i).y - c.y)*Math.sin(d) + c.x;
			double qy = (points.get(i).x - c.x)*Math.sin(d) + (points.get(i).y - c.y)*Math.cos(d) + c.y;
			
			newPoints.add(new Point(qx, qy));
		}
		
		return newPoints;
	}
	
	private static Point centroid(List<Point> points) {
		double cx = 0;
		double cy = 0;
		double countOfPoints = (double)points.size();
		
		for(int i=0; i<points.size(); i++){
			cx += points.get(i).x;
			cy += points.get(i).y;
		}
		
		return new Point(cx/countOfPoints, cy/countOfPoints);
	}

	public static List<Point> resample(List<Point> points, int N) {
		
		double I = pathLength(points)/((double)(N-1));
		double D = 0.0;
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		newPoints.add(points.get(0));
		
		int i = 1;
		while(i < points.size()){
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			
			double d = p1.getDistance(p2);
			
			if((D+d) >= I){
				double qx = p1.x + ((I-D)/d) * (p2.x-p1.x);
				double qy = p1.y + ((I-D)/d) * (p2.y-p1.y);
				
				Point q = new Point(qx, qy);
				newPoints.add(q);
				//q will be the next point
				points.add(i, q);
				D = 0.0;
			} else {
				D += d;
			}
			
			i++;
		}
		
		//Due to rounding error the last point might be missing
		if(newPoints.size() == N-1){
			newPoints.add(points.get(points.size()-1));
		}
		
		return newPoints;
	}

	public static double pathLength(List<Point> points) {
		double sumOfdistance = 0;
		for(int i=1; i<points.size(); i++){
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			sumOfdistance += p1.getDistance(p2);
		}
		return sumOfdistance;
	}
}
