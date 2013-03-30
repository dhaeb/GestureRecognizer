package de.kdi.pojo;

public class GestureResult{
	
	public String name;
	public float score;
	
	public GestureResult(String name, float score){
		this.name = name;
		this.score = score;
	}
	
	@Override
	public String toString(){
		return "Recognized: "+ name + "; Score: " + score;
	}
}