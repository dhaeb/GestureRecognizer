import java.util.ArrayList;


public class BoundingBox {

	public Point min;
	public Point max;
	public double width;
	public double height;
	
	public BoundingBox(ArrayList<Point> points){
		min = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
		max = new Point(Double.MIN_VALUE, Double.MIN_VALUE);
		compute(points);
	}
	
	public void compute(ArrayList<Point> points){
		for (Point point : points) {
			if(point.x < min.x) min.x = point.x;
			if(point.y < min.y) min.y = point.y;
			if(point.x > max.x) max.x = point.x;
			if(point.y > max.y) max.y = point.y; 
		}
		
		width = max.x-min.x;
		height = max.y-min.y;
	}
}
