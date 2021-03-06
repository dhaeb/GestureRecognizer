package de.kdi.pojo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestPoint {
  
	@Test
	public void testDistanceCalculation() {
		Point testable = new Point(2,2);
		assertEquals(1.0d, testable.getDistance(new Point(2, 3)), 0.1);
		assertEquals(1.41d, testable.getDistance(new Point(3, 3)), 0.1);
		assertEquals(0.0d, testable.getDistance(new Point(2, 2)), 0.1);
	}
	
	@Test
	public void testPathLength(){
		Point fixture1 = new Point(2,2);
		Point fixture2 = new Point(2,3);
		Point fixture3 = new Point(2,4);
		List<Point> fixtureList = new ArrayList<Point>();
		fixtureList.add(fixture1);
		fixtureList.add(fixture2);
		fixtureList.add(fixture3);
		assertEquals(2.0d ,Point.pathLength(fixtureList),0.1); 
	}
	
	@Test
	public void testEquals(){
		Point firstPoint = new Point(1.1d, 1.4d);
		Point secondPoint = new Point(1.1d, 1.4d);
		Point thriedPoint = new Point(1.1d, 0d);
		assertEquals(firstPoint, secondPoint);
		assertNotEquals(firstPoint,thriedPoint);
		assertNotEquals(secondPoint, thriedPoint);
		assertNotEquals(secondPoint, Integer.valueOf(2));
		assertNotEquals(secondPoint, "string");
	}
	
}
