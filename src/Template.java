import java.util.ArrayList;

public class Template {
	
	public String name;
	public ArrayList<Point> points = new ArrayList<Point>();
	public boolean resampleFirst;
	
	//Constructor for already processed raw input
	public Template(String name, ArrayList<Point> points, boolean resampleFirst){
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;
	}
	
	public Template(String name, ArrayList<Point> points, double squareSize, int N, boolean resampleFirst){
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;
		
		if(resampleFirst){
			//Resample points to equidistance
			this.points = GestureUtil.resample(this.points, N);
		}
		
    	//Rotate to 0�
		this.points = GestureUtil.rotate(this.points);
    	//Scale to square
		this.points = GestureUtil.scale(this.points, squareSize);
    	//Translate to origin
		this.points = GestureUtil.translate(this.points);
		
		if(!resampleFirst){
			this.points = GestureUtil.resample(this.points, N);
		}
	}
	
	public String getName() {
		return name;
	}

}
