package de.kdi.pojo;

import java.util.ArrayList;
import java.util.List;

public class GestureTemplate {
	
	public String name;
	public List<Point> points = new ArrayList<Point>();
	public boolean resampleFirst;
	
	//Constructor for already processed raw input
	public GestureTemplate(String name, ArrayList<Point> points, boolean resampleFirst){
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;
	}
	
	public GestureTemplate(String name, List<Point> points, double squareSize, int N, boolean resampleFirst){
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;
		
		initPoints(squareSize, N, resampleFirst);
	}

	private void initPoints(double squareSize, int N, boolean resampleFirst) {
		if(resampleFirst){
			//Resample points to equidistance
			this.points = Point.resample(this.points, N);
		}
		
    	//Rotate to 0
		this.points = Point.rotate(this.points);
    	//Scale to square
		this.points = Point.scale(this.points, squareSize);
    	//Translate to origin
		this.points = Point.translate(this.points);
		
		if(!resampleFirst){
			this.points = Point.resample(this.points, N);
		}
	}
	
	public String getName() {
		return name;
	}

}
