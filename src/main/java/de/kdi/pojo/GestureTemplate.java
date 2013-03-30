package de.kdi.pojo;

import java.util.ArrayList;
import java.util.List;

import de.kdi.recognizer.dollar1.ResampleFirstPointProcessor;
import de.kdi.recognizer.dollar1.ResampleLastPointProcessor;

public class GestureTemplate {
	
	public String name;
	public List<Point> points = new ArrayList<Point>();
	
	//Constructor for already processed raw input
	public GestureTemplate(String name, List<Point> points){
		this.name = name;
		this.points = points;
	}
	
	public GestureTemplate(String name, List<Point> points, boolean resampleFirst){
		this.name = name;
		this.points = points;
		initPoints(resampleFirst);
	}

	private void initPoints(boolean resampleFirst) {
		if(resampleFirst){
			points = new ResampleFirstPointProcessor(points).preparePointsFor$1();
		} else {
			points = new ResampleLastPointProcessor(points).preparePointsFor$1();
		}
	}
	
	public String getName() {
		return name;
	}

}
