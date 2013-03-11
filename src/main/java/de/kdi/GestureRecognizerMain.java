package de.kdi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kdi.gui.listener.ButtonListener;
import de.kdi.gui.listener.PathListener;
import de.kdi.pojo.Gesture;
import de.kdi.pojo.GestureUtil;
import de.kdi.pojo.Point;
import de.kdi.pojo.GestureTemplate;

public class GestureRecognizerMain extends JPanel {

	//Fitting square size
	public static double squareSize = 250.0;
	//Amount of points the data should be resampled to
	public static int N = 64;
	//Holds the current gesture points
	public static ArrayList<Point> points = new ArrayList<Point>();
	public static ArrayList<Point> prvPoints;
	
	public Point start;
	public Point stop;
	public Shape shape;
	
	
	
	public static Logger log = LogManager.getLogger("test");
	public static JButton saveButton;

	private static final long serialVersionUID = 1L;
    private static Dimension areaSize = new Dimension(600, 600);
    
    private static JLabel resampledFirstLabel;
	private static JLabel resampledLastLabel;
    private static JFrame frame;
	private static JLabel templateLabel;

    
    //Angel range
    private double theta = 45.0;
    //Angel precision
    private double dTheta = 2.0;
    
    //Holds the predefined templates
    private static ArrayList<GestureTemplate> resampledFirstTemplates = new ArrayList<GestureTemplate>();
    private static ArrayList<GestureTemplate> resampledLastTemplates = new ArrayList<GestureTemplate>();

    
    public GestureRecognizerMain() {
        setBackground(Color.white);
        setPreferredSize(areaSize);
        PathListener listener = new PathListener(this);
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	resampledFirstLabel = new JLabel();
                resampledLastLabel = new JLabel();
				templateLabel = new JLabel();
				
            	
            	//Read in all the templates
            	readTemplates("ResampledFirstTemplates.txt", true, resampledFirstTemplates);
            	readTemplates("ResampledLastTemplates.txt", false, resampledLastTemplates);
            	listUniqueGestures();
            	
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
    
    public static void addTemplate(ArrayList<Point> points, double squareSize, int N){
		String templateName = "";

		JPanel panel = new JPanel(new MigLayout("", "[][grow][left, fill]", ""));
		panel.setPreferredSize(new Dimension(100, 35));

		JTextField nameText = new JTextField("");
		JLabel nameLabel = new JLabel("Gesture name");

		panel.add(nameLabel);	
		panel.add(nameText, "growx");

		final JComponent[] inputs = new JComponent[] { panel };

		Object[] options = {"Cancel", "Add Template"};
		nameText.requestFocus();
		int n = JOptionPane.showOptionDialog(frame, inputs, "Add Template", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

		//Process input
		if(n == 1){
			//Check input
			if(!nameText.getText().equals("")){
				//Create the templates in both files based on the raw input points
				addTemplateToFile(new GestureTemplate(nameText.getText(), points, squareSize, N, true), "ResampledFirstTemplates.txt");
				addTemplateToFile(new GestureTemplate(nameText.getText(), points, squareSize, N, false), "ResampledLastTemplates.txt");
				
				resampledFirstTemplates.clear();
				resampledLastTemplates.clear();
				
				readTemplates("ResampledFirstTemplates.txt", true, resampledFirstTemplates);
				readTemplates("ResampledLastTemplates.txt", false, resampledLastTemplates);
				
				listUniqueGestures();
				//Notify the user
				JOptionPane.showMessageDialog(frame, "Template "+templateName+" successfully added.", "Succes", JOptionPane.INFORMATION_MESSAGE);
			} else{
				JOptionPane.showMessageDialog(frame, "Error. Input was incorrect.", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
    
    private static void addTemplateToFile(GestureTemplate template, String fileName) {
    	log.debug("Adding template with "+template.points.size()+" points.");
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    
		    out.println();
		    out.println("#"+template.name);
		    
		    for(Point p:template.points){
		    	out.println(p.x+" "+p.y);
		    }
		    
		    out.close();
		} catch (IOException e) {
			log.warn("Could not write to file.");
		}
	}
    
    public static void readTemplates(String fileName, boolean resampledFirst, ArrayList<GestureTemplate> templates){
		try {
			Scanner sc = new Scanner(new File(fileName));

			String curTemplate = "";
			ArrayList<Point> curPoints = null;

			while(sc.hasNext()){
				String cur = sc.next();

				if(cur.charAt(0) == '#'){
					if(!curTemplate.equals("") && curPoints != null){
						templates.add(new GestureTemplate(curTemplate, curPoints, resampledFirst));
					}

					curTemplate = cur.substring(1);
					curPoints = new ArrayList<Point>();
				} else {
					if(curPoints != null){
						curPoints.add(new Point(Double.parseDouble(cur), Double.parseDouble(sc.next())));
					}
				}
			}
			
			sc.close();
			
			if(!curTemplate.equals("") && curPoints != null){
				//Add the last template
				templates.add(new GestureTemplate(curTemplate, curPoints, resampledFirst));
			}
		} catch (FileNotFoundException e) {
			log.warn("Can't find file.");
		}

		if(resampledFirst){
			log.debug(templates.size()+" resampled first templates loaded.");
		} else {
			log.debug(templates.size()+" resampled last templates loaded.");
		}
	}
    
    public static void listUniqueGestures(){
		ArrayList<String> tmpData = new ArrayList<String>();
		for(GestureTemplate t : resampledFirstTemplates){
			if(!tmpData.contains(t.name)){
				tmpData.add(t.name);
			}
		}

		String gestures = "  ";
		for(String s : tmpData){
			gestures += s + ", ";
		}
		templateLabel.setText("Available gestures: "+gestures.substring(0, gestures.length()-2));
	}
}