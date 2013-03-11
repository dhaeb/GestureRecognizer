package de.kdi.gui.listener;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.kdi.GestureRecognizerMain;
import de.kdi.pojo.Point;


public class PathListener extends MouseAdapter {
    /**
	 * 
	 */
	private final GestureRecognizerMain gestureRecognizerMain;

	/**
	 * @param gestureRecognizerMain
	 */
	public PathListener(GestureRecognizerMain gestureRecognizerMain) {
		this.gestureRecognizerMain = gestureRecognizerMain;
	}

	public void mousePressed(MouseEvent event) {
        this.gestureRecognizerMain.start = new Point(event.getPoint().x, event.getPoint().y);
        //Add start point
        GestureRecognizerMain.points.add(this.gestureRecognizerMain.start);
       
        Path2D path = new Path2D.Double();
        this.gestureRecognizerMain.shape = path;
    }

    public void mouseDragged(MouseEvent event) {
        this.gestureRecognizerMain.stop = new Point(event.getPoint().x, event.getPoint().y);
        //Add end point
        GestureRecognizerMain.points.add(this.gestureRecognizerMain.stop);
        
        Path2D path = (Path2D) this.gestureRecognizerMain.shape;
        path.moveTo(this.gestureRecognizerMain.start.x, this.gestureRecognizerMain.start.y);
        path.lineTo(this.gestureRecognizerMain.stop.x, this.gestureRecognizerMain.stop.y);
        this.gestureRecognizerMain.shape = path;
        this.gestureRecognizerMain.start = this.gestureRecognizerMain.stop;

        this.gestureRecognizerMain.repaint();
    }

    public void mouseReleased(MouseEvent event) {
        Path2D path = (Path2D) this.gestureRecognizerMain.shape;
        try {
            path.closePath();
        } catch(Exception ingore) {
        }
        this.gestureRecognizerMain.shape = path;
        this.gestureRecognizerMain.repaint();
        
        if(!GestureRecognizerMain.points.isEmpty()){
        	GestureRecognizerMain.prvPoints = new ArrayList<Point>(Arrays.asList(new Point[GestureRecognizerMain.points.size()]));
        	Collections.copy(GestureRecognizerMain.prvPoints, GestureRecognizerMain.points);
        	
        	GestureRecognizerMain.log.debug("Prv points: "+GestureRecognizerMain.prvPoints.size());
        	this.gestureRecognizerMain.recognize(GestureRecognizerMain.prvPoints);
        }
        
      //Clear from previous run
      GestureRecognizerMain.points.clear();
    }
}