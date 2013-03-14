package de.kdi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kdi.gui.listener.ButtonListener;
import de.kdi.gui.listener.MouseDrawingListener;
import de.kdi.pojo.Gesture;
import de.kdi.pojo.GestureUtil;
import de.kdi.pojo.Point;
import de.kdi.pojo.GestureTemplate;
import de.kdi.pojo.gui.DrawnPoints;

public class GestureRecognizerMain extends JPanel implements Observer {

	//Fitting square size
	public static double squareSize = 250.0;
	//Amount of points the data should be resampled to
	public static int N = 64;
	//Holds the current gesture points
	public static List<Point> points = new ArrayList<Point>();
	public static List<Point> prvPoints;
	
	public Point start;
	public Point stop;
	public Shape shape;
	
	
	
	public static Logger log = LogManager.getLogger("test");
	public static JButton saveButton;

	private static final long serialVersionUID = 1L;
    private static Dimension areaSize = new Dimension(600, 600);
    
    private static JLabel resampledFirstLabel;
	private static JLabel resampledLastLabel;
    static JFrame frame;
	static JLabel templateLabel;

    
    //Angel range
    private double theta = 45.0;
    //Angel precision
    private double dTheta = 2.0;
    
    //Holds the predefined templates
    static ArrayList<GestureTemplate> resampledFirstTemplates = new ArrayList<GestureTemplate>();
    static ArrayList<GestureTemplate> resampledLastTemplates = new ArrayList<GestureTemplate>();

    
    public GestureRecognizerMain() {
        setBackground(Color.white);
        setPreferredSize(areaSize);
        MouseDrawingListener listener = new MouseDrawingListener();
        listener.addObserver(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        Graphics2D g2 = (Graphics2D) gc; 
        if (start != null && stop != null) {
            BasicStroke stroke = new BasicStroke(1);
            Shape strokedShape = stroke.createStrokedShape(shape);
            g2.draw(strokedShape);
            g2.fill(strokedShape);
        }
    }
    
    /**
     * @param points
     */
    public void recognize(List<Point> points){
    	List<Point> ptsResampledFirst = processPoints(points, true);
    	List<Point> ptsResampledLast = processPoints(points, false);
    	
    	Gesture resampledFirst = recognize(ptsResampledFirst, true);
    	Gesture resampledLast = recognize(ptsResampledLast, false);
    	
    	resampledFirstLabel.setText("Resampled first "+resampledFirst);
    	resampledLastLabel.setText("Resampled last "+resampledLast);
    }
    
    /**
     * @param points
     * @param resampleFirst
     * @return
     */
    public List<Point> processPoints(List<Point> points, boolean resampleFirst){
		List<Point> newPoints = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
		Collections.copy(newPoints, points);
		
		if(resampleFirst){
			//Resample points to equidistance
			newPoints = Point.resample(newPoints, N);
		}
		
		//Rotate to 0 degree
		newPoints = Point.rotate(newPoints);
		
		//Scale to square
		newPoints = Point.scale(newPoints, squareSize);
		
		//Translate to origin
		newPoints = Point.translate(newPoints);
		
		if(!resampleFirst){
			//Resample last
			newPoints = Point.resample(newPoints, N);
		}
		
		return newPoints;
	}
    
    /**
     * @param points
     * @param resampleFirst
     * @return
     */
    public Gesture recognize(List<Point> points, boolean resampleFirst){    
    	double best = Double.MAX_VALUE;
    	int t = -1;
    	    	
    	//Find a match
    	for(int i=0; i<resampledFirstTemplates.size(); i++){
    		double d = 0.0;
    		if(resampleFirst){
    			d = GestureUtil.distanceAtBestAngle(points, resampledFirstTemplates.get(i), -theta, theta, dTheta);
    		} else {
    			d = GestureUtil.distanceAtBestAngle(points, resampledLastTemplates.get(i), -theta, theta, dTheta);
    		}
    		
    		if(d < best){
    			best = d;
    			t = i;
    		}
    	}
    	
    	//Calculate score
    	float score = (float)(1.0 - (best/(0.5*Math.sqrt(squareSize*squareSize + squareSize*squareSize))));
    	
    	
    	if(t > -1 && score > 0.8){
    		//Return the matched gesture
    		if(resampleFirst){
    			return new Gesture(resampledFirstTemplates.get(t).name, score);
    		} else {
    			return new Gesture(resampledLastTemplates.get(t).name, score);
    		}
    	}
    	
    	return new Gesture("No match", 0.0f);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	resampledFirstLabel = new JLabel();
                resampledLastLabel = new JLabel();
				templateLabel = new JLabel();
				
            	
            	//Read in all the templates
            	GestureTemplateCreator.readTemplates("ResampledFirstTemplates.txt", true, resampledFirstTemplates);
            	GestureTemplateCreator.readTemplates("ResampledLastTemplates.txt", false, resampledLastTemplates);
            	GestureTemplateCreator.listUniqueGestures();
            	
                GestureRecognizerMain shapes = new GestureRecognizerMain();

                frame = new JFrame("$1 Gesture Recognizer");
                frame.setResizable(false);
                
                saveButton = new JButton("Turn into template");
                saveButton.addActionListener(new ButtonListener());
                
                JPanel mainPanel = new JPanel(new MigLayout());
                mainPanel.setPreferredSize(new Dimension(areaSize.width+20, areaSize.height+60));

                JPanel controlPanel = new JPanel(new MigLayout());
                
                controlPanel.add(saveButton);
                
                mainPanel.add(shapes, "wrap");
				mainPanel.add(templateLabel, "wrap");
				mainPanel.add(resampledFirstLabel, "wrap");
				mainPanel.add(resampledLastLabel, "wrap");
				mainPanel.add(controlPanel);
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(mainPanel);

                frame.pack();
                frame.setVisible(true);
            }
        });
    }

	@Override
	public void update(Observable arg0, Object arg1) {
		DrawnPoints pointData = (DrawnPoints) arg1;
		points = pointData.points;
		start = pointData.startPoint;
		stop = pointData.startPoint;
		shape = pointData.pointShape;
		repaint();
		if(pointData.finishedFigure){
			copyToPreviousPoints();
			recognize(GestureRecognizerMain.prvPoints);
		}
	}

	private void copyToPreviousPoints() {
		GestureRecognizerMain.prvPoints = new ArrayList<Point>(Arrays.asList(new Point[GestureRecognizerMain.points.size()]));
		Collections.copy(GestureRecognizerMain.prvPoints, GestureRecognizerMain.points);
		GestureRecognizerMain.log.debug("Prv points: " + GestureRecognizerMain.prvPoints.size());
	}
}