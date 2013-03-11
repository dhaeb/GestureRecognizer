package de.kdi.pojo;

public class Gesture{
	
	private String name;
	private float score;
	
	public Gesture(String name, float score){
		this.name = name;
		this.score = score;
	}
	
	@Override
	public String toString(){
		return "Recognized: "+ name + "; Score: " + score;
	}
}