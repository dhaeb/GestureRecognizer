import java.util.ArrayList;

public class Template {
	
	public String name;
	public ArrayList<Point> points = new ArrayList<Point>();
	
	//Constructor for already processed raw input
	public Template(String name, ArrayList<Point> points){
		this.name = name;
		this.points = points;
	}
	
	public Template(String name, ArrayList<Point> points, double squareSize, int N){
		this.name = name;
		this.points = points;
		
		//1. Resample points to equidistance
		this.points = GestureUtil.resample(this.points, N);
    	//2. Rotate to 0°
		this.points = GestureUtil.rotate(this.points);
    	//3. Scale to square
		this.points = GestureUtil.scale(this.points, squareSize);
    	//4. Translate to origin
		this.points = GestureUtil.translate(this.points);
	}
	
	public String getName() {
		return name;
	}

}
