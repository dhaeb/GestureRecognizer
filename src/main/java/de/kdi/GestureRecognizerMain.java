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
import de.kdi.pojo.Point;
import de.kdi.pojo.gui.DrawnPoints;
import de.kdi.recognizer.GestureRecognizer;

public class GestureRecognizerMain extends JPanel implements Observer {

	//Holds the current gesture points
	public static List<Point> points = new ArrayList<Point>();
	public static List<Point> prvPoints;
	
	private DrawnPoints pointData = new DrawnPoints();
	
	public static Logger log = LogManager.getLogger("test");
	public static JButton saveButton;

	private static final long serialVersionUID = 1L;
    private static Dimension areaSize = new Dimension(600, 600);
    
    private static JLabel resampledFirstLabel;
	private static JLabel resampledLastLabel;
    static JFrame frame;
	static JLabel templateLabel;

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
        if (pointData.startPoint != null && pointData.endPoint != null) {
            BasicStroke stroke = new BasicStroke(1);
            Shape strokedShape = stroke.createStrokedShape(pointData.pointShape);
            g2.draw(strokedShape);
            g2.fill(strokedShape);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	resampledFirstLabel = new JLabel();
                resampledLastLabel = new JLabel();
				templateLabel = new JLabel();
				
            	
            	//Read in all the templates
            	GestureTemplateCreator.readTemplates("ResampledFirstTemplates.txt", true, GestureRecognizer.resampledFirstTemplates);
            	GestureTemplateCreator.readTemplates("ResampledLastTemplates.txt", false, GestureRecognizer.resampledLastTemplates);
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
		this.pointData = pointData; 
		copyToPreviousPoints();
		recognizerGesture();
		repaint();
	}

	private void copyToPreviousPoints() {
		GestureRecognizerMain.prvPoints = new ArrayList<Point>(Arrays.asList(new Point[GestureRecognizerMain.points.size()]));
		Collections.copy(GestureRecognizerMain.prvPoints, GestureRecognizerMain.points);
		GestureRecognizerMain.log.debug("Prv points: " + GestureRecognizerMain.prvPoints.size());
	}
	
	private void recognizerGesture() {
		GestureRecognizer recognizer = new GestureRecognizer(prvPoints);
		Gesture[] result = recognizer.recognize();
		resampledFirstLabel.setText("Resampled first " + result[0]);
		resampledLastLabel.setText("Resampled last " + result[1]);
	}
}