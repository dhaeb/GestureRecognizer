package de.kdi.gui.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Observable;

import de.kdi.pojo.Point;
import de.kdi.pojo.gui.DrawnPoints;

public class MouseDrawingListener extends Observable implements MouseListener, MouseMotionListener {

	private DrawnPoints pointsMetaData = new DrawnPoints();
	
	@Override
	public void mousePressed(MouseEvent event) {
		Point startPoint = new Point(event.getPoint().x, event.getPoint().y);
		pointsMetaData.startPoint = startPoint;
		pointsMetaData.points = new ArrayList<Point>(100);
		pointsMetaData.points.add(startPoint); 
		Path2D path = new Path2D.Double();
		pointsMetaData.pointShape = path;
		setChanged();
		notifyObservers(pointsMetaData);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		try {
			pointsMetaData.pointShape.closePath();
		} catch (Exception e) {
			// ignore...
		}
		setChanged();
		notifyObservers(pointsMetaData);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent event) {
		Point endPoint = new Point(event.getPoint().x, event.getPoint().y);
		pointsMetaData.endPoint = endPoint;
		pointsMetaData.points.add(endPoint);
		
		pointsMetaData.pointShape.moveTo(pointsMetaData.startPoint.x, pointsMetaData.startPoint.y);
		pointsMetaData.pointShape.lineTo(pointsMetaData.endPoint.x, pointsMetaData.endPoint.y);
		
		pointsMetaData.startPoint = pointsMetaData.endPoint;
		setChanged();
		notifyObservers(pointsMetaData);
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
}
