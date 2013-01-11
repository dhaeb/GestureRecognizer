import java.util.ArrayList;

public class GestureUtil {
	
	public static double phi = 0.5*(Math.sqrt(5.0) - 1.0);

	public static ArrayList<Point> resample(ArrayList<Point> points, int N) {
		double I = pathLength(points)/((double)(N-1));
		double D = 0.0;
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		newPoints.add(points.get(0));
		
		int i = 1;
		while(i < points.size()){
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			
			double d = distance(p1, p2);
			
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

	public static double distance(Point p1, Point p2){
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	private static double pathLength(ArrayList<Point> points) {
		double d = 0;
		for(int i=1; i<points.size(); i++){
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			
			d += distance(p1, p2);
		}
		
		return d;
	}

	public static ArrayList<Point> rotate(ArrayList<Point> points) {
		Point c = centroid(points);
		
		//For -pi <= theta <= pi
		double theta = Math.atan2(c.y-points.get(0).y, c.x-points.get(0).x);
		
		return rotate(points, -theta);
	}

	private static ArrayList<Point> rotate(ArrayList<Point> points, double d) {
		Point c = centroid(points);
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = (points.get(i).x - c.x)*Math.cos(d) - (points.get(i).y - c.y)*Math.sin(d) + c.x;
			double qy = (points.get(i).x - c.x)*Math.sin(d) + (points.get(i).y - c.y)*Math.cos(d) + c.y;
			
			newPoints.add(new Point(qx, qy));
		}
		
		return newPoints;
	}

	private static Point centroid(ArrayList<Point> points) {
		double cx = 0;
		double cy = 0;
		double n = (double)points.size();
		
		for(int i=0; i<points.size(); i++){
			cx += points.get(i).x;
			cy += points.get(i).y;
		}
		
		return new Point(cx/n, cy/n);
	}

	public static ArrayList<Point> scale(ArrayList<Point> points, double squareSize) {
		BoundingBox b = new BoundingBox(points);
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = points.get(i).x*(squareSize/b.width);
			double qy = points.get(i).y*(squareSize/b.height);
			
			newPoints.add(new Point(qx, qy));
		}
		return newPoints;
	}

	public static ArrayList<Point> translate(ArrayList<Point> points) {
		Point c = centroid(points);
		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for(int i=0; i<points.size(); i++){
			double qx = points.get(i).x - c.x;
			double qy = points.get(i).y - c.y;
			
			newPoints.add(new Point(qx, qy));
		}
		
		return newPoints;
	}

	public static double distanceAtBestAngle(ArrayList<Point> points, Template t, double f, double theta, double dTheta) { 
		double x1 = phi*f + (1-phi)*theta;
		double f1 = distanceAtBestAngle(points, t, x1);
		double x2 = (1-phi)*f + phi*theta;
		double f2 = distanceAtBestAngle(points, t, x2);
		
		while((theta-f) > dTheta){
			if(f1 < f2){
				theta = x2;
				x2 = x1;
				f2 = f1;
				x1 = phi*f + (1-phi)*theta;
				f1 = distanceAtBestAngle(points, t, x1);
			} else {
				f = x1;
				x1 = x2;
				f1 = f2;
				x2 = (1-phi)*f + phi*theta;
				f2 = distanceAtBestAngle(points, t, x2);
			}
		}
		
		return Math.min(f1, f2);
	}

	private static double distanceAtBestAngle(ArrayList<Point> points, Template t, double theta) {
		ArrayList<Point> newPoints = rotate(points, theta);
		return pathDistance(newPoints, t.points);
	}

	private static double pathDistance(ArrayList<Point> A, ArrayList<Point> B) {
		double d = 0.0;
		
		for(int i=0; i<A.size(); i++){
			d = d + distance(A.get(i), B.get(i));
		}
		
		return (d/(double)A.size());
	}
}
