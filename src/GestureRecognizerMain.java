import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class GestureRecognizerMain extends JPanel {

	private static final long serialVersionUID = 1L;
	private Point start;
    private Point stop;
    private Shape shape;
    private static Dimension areaSize = new Dimension(600, 600);
    
    private static JButton saveButton;
    private static JLabel resultLabel;
    
    //Angel range
    private double theta = 45.0;
    //Angel precision
    private double dTheta = 2.0;
    //Fitting square size
    static double squareSize = 250.0;
    //Amount of points the data should be resampled to
    static int N = 64;
    
    
    //Holds the current gesture points
    private static ArrayList<Point> points = new ArrayList<Point>();
    private static ArrayList<Point> prvPoints;
    //Holds the predefined templates
    private static ArrayList<Template> templates = new ArrayList<Template>();

    public GestureRecognizerMain() {
        setBackground(Color.white);
        setPreferredSize(areaSize);
        PathListener listener = new PathListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

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

    private class PathListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {
            start = new Point(event.getPoint().x, event.getPoint().y);
            //Add start point
            points.add(start);
           
            Path2D path = new Path2D.Double();
            shape = path;
        }

        public void mouseDragged(MouseEvent event) {
            stop = new Point(event.getPoint().x, event.getPoint().y);
            //Add end point
            points.add(stop);
            
            Path2D path = (Path2D) shape;
            path.moveTo(start.x, start.y);
            path.lineTo(stop.x, stop.y);
            shape = path;
            start = stop;

            repaint();
        }

        public void mouseReleased(MouseEvent event) {
            Path2D path = (Path2D) shape;
            try {
                path.closePath();
            } catch(Exception ingore) {
            }
            shape = path;
            repaint();
            
            if(!points.isEmpty()){
            	prvPoints = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
            	Collections.copy(prvPoints, points);
            	//ArrayList<Point> tmp = new ArrayList<Point>(Arrays.asList(new Point[points.size()]));
            	//Collections.copy(tmp, points);
            	
            	//Print out the match
            	//System.out.println(recognize(prvPoints));
            	resultLabel.setText(recognize(prvPoints).toString());
            }
            
          //Clear from previous run
          points.clear();
        }
    }
    
    public Gesture recognize(ArrayList<Point> points){    
    	//1. Resample points to equidistance
    	points = GestureUtil.resample(points, N);
    	//2. Rotate to 0 degree
    	points = GestureUtil.rotate(points);
    	//3. Scale to square
    	points = GestureUtil.scale(points, squareSize);
    	
    	
    	
    	//4. Translate to origin
    	points = GestureUtil.translate(points);
    	
    	double best = Double.MAX_VALUE;
    	int t = -1;
    	    	
    	//Find a match
    	for(int i=0; i<templates.size(); i++){
    		//System.out.println("Comparing to template: "+templates.get(i).name);
    		double d = GestureUtil.distanceAtBestAngle(points, templates.get(i), -theta, theta, dTheta);
    		
    		if(d < best){
    			best = d;
    			t = i;
    		}
    	}
    	
    	//Calculate score
    	float score = (float)(1.0 - (best/(0.5*Math.sqrt(squareSize*squareSize + squareSize*squareSize))));
    	
    	
    	if(t > -1 && score > 0.8){
    		//Return the matched gesture
        	return new Gesture(templates.get(t).getName(), score);
    	}
    	
    	return new Gesture("No match", 0.0f);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	//Read in all the templates
            	readTemplates();
            	
                GestureRecognizerMain shapes = new GestureRecognizerMain();

                JFrame frame = new JFrame("$1 Gesture Recognizer");
                frame.setResizable(false);
                
                
                resultLabel = new JLabel();
                
                saveButton = new JButton("Turn into template");
                saveButton.addActionListener(new ButtonListener());
                
                JPanel mainPanel = new JPanel(new MigLayout());
                mainPanel.setPreferredSize(new Dimension(areaSize.width+20, areaSize.height+60));

                ArrayList<String> tmpData = new ArrayList<String>();
                for(Template t : templates){
                	if(!tmpData.contains(t.name)){
                		tmpData.add(t.name);
                	}
                }
                String[] data = new String[tmpData.size()];
                tmpData.toArray(data);
                
                JPanel controlPanel = new JPanel(new MigLayout());
                JList templateList = new JList(data);
                
                String gestures = "";
                for(String s : tmpData){
                	gestures += s + ", ";
                }
                JLabel templateLabel = new JLabel("Available gestures: "+gestures.substring(0, gestures.length()-2));
                
                controlPanel.add(templateLabel, "cell 0 0");
                controlPanel.add(templateList, "cell 0 1");
                controlPanel.add(saveButton, "cell 1 0");
                
                
                mainPanel.add(shapes, "wrap");
                mainPanel.add(templateLabel, "wrap");
                mainPanel.add(resultLabel);
                
                //mainPanel.add(controlPanel);
                
                
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(mainPanel);

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
    
    public static void readTemplates(){
    	try {
			Scanner sc = new Scanner(new File("templates.txt"));
			
			String curTemplate = "";
			ArrayList<Point> curPoints = null;
			
			while(sc.hasNext()){
				String cur = sc.next();
							
				if(cur.charAt(0) == '#'){
					if(!curTemplate.equals("") && curPoints != null){
						//System.out.println("Current template: "+curTemplate);
						//System.out.println("#points: "+curPoints.size());
						
						templates.add(new Template(curTemplate, curPoints));
					}
					
					curTemplate = cur.substring(1);
					curPoints = new ArrayList<Point>();
				} else {
					if(curPoints != null){
						curPoints.add(new Point(Double.parseDouble(cur), Double.parseDouble(sc.next())));
					}
				}
			}
			//Add the last template
			templates.add(new Template(curTemplate, curPoints));
			
			//System.out.println("Last template: "+curTemplate);
			//System.out.println("#points: "+curPoints.size());
			//System.out.println("Last Point: "+curPoints.get(curPoints.size()-1).x + ", "+curPoints.get(curPoints.size()-1).y);
			
			
		} catch (FileNotFoundException e) {
			System.out.println("Can't find file.");
		}
    	
    	System.out.println(templates.size()+" templates loaded.");
    }
    
    protected static class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			JButton b = (JButton) e.getSource();
				
			//Scan the selected row and add it to the order tlabe
			if(b.equals(saveButton)){
	            Template tmp = new Template("TEST", prvPoints, squareSize, N);
	            
	            for(int i=0; i<tmp.points.size(); i++){
	            	//System.out.println(i+". Point: ("+prvPoints.get(i).x+", "+prvPoints.get(i).y+")");
	            	System.out.println(tmp.points.get(i).x + " "+tmp.points.get(i).y);
	            }
	            
	            prvPoints.clear();
			}
		}
    }
}