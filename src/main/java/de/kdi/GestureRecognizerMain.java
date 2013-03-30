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
import de.kdi.pojo.GestureResult;
import de.kdi.pojo.Point;
import de.kdi.pojo.TemplateCollection;
import de.kdi.pojo.gui.DrawnPoints;
import de.kdi.recognizer.GestureRecognizer;

public class GestureRecognizerMain extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	
	//Holds the current gesture points
	public static List<Point> POINTS = new ArrayList<Point>();
	public static List<Point> PRV_POINTS;
	
	public static Logger LOG = LogManager.getLogger("test");
	public static JButton SAVE_BUTTON;

    private static Dimension AREA_SIZE = new Dimension(600, 600);
    
    private static JLabel RESAMPLED_FIRST_LABEL;
	private static JLabel RESAMPLED_LAST_LABEL;
    
	public static TemplateCollection TEMPLATES = new TemplateCollection();
	
	public static JFrame FRAME;
	
	private DrawnPoints pointData = new DrawnPoints();
	
	private JLabel templateLabel = new JLabel();
	
	public JLabel getTemplateLabel(){
		return templateLabel;
	}
	
    public GestureRecognizerMain() {
        setBackground(Color.white);
        setPreferredSize(AREA_SIZE);
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
            	RESAMPLED_FIRST_LABEL = new JLabel();
                RESAMPLED_LAST_LABEL = new JLabel();
				//GestureRecognizerMain.templateLabel.setText("Available gestures: "
            	//Read in all the templates
            	GestureTemplateCreator.readTemplates("ResampledFirstTemplates.txt", TEMPLATES.resampledFirstTemplates);
            	GestureTemplateCreator.readTemplates("ResampledLastTemplates.txt", TEMPLATES.resampledLastTemplates);
            	GestureTemplateCreator.getUniqueGesturesNames(TEMPLATES.resampledFirstTemplates);
            	
                GestureRecognizerMain mainView = new GestureRecognizerMain();

                FRAME = new JFrame("$1 Gesture Recognizer");
                FRAME.setResizable(false);
                
                SAVE_BUTTON = new JButton("Turn into template");
                SAVE_BUTTON.addActionListener(new ButtonListener(mainView));
                
                JPanel mainPanel = new JPanel(new MigLayout());
                mainPanel.setPreferredSize(new Dimension(AREA_SIZE.width+20, AREA_SIZE.height+60));

                JPanel controlPanel = new JPanel(new MigLayout());
                
                controlPanel.add(SAVE_BUTTON);
                
                mainPanel.add(mainView, "wrap");
				mainPanel.add(mainView.getTemplateLabel(), "wrap");
				mainPanel.add(RESAMPLED_FIRST_LABEL, "wrap");
				mainPanel.add(RESAMPLED_LAST_LABEL, "wrap");
				mainPanel.add(controlPanel);
                
                FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                FRAME.getContentPane().add(mainPanel);

                FRAME.pack();
                FRAME.setVisible(true);
            }
        });
    }

	@Override
	public void update(Observable arg0, Object arg1) {
		DrawnPoints pointData = (DrawnPoints) arg1;
		POINTS = pointData.points;
		this.pointData = pointData; 
		copyToPreviousPoints();
		recognizeGesture();
		repaint();
	}

	private void copyToPreviousPoints() {
		GestureRecognizerMain.PRV_POINTS = new ArrayList<Point>(Arrays.asList(new Point[GestureRecognizerMain.POINTS.size()]));
		Collections.copy(GestureRecognizerMain.PRV_POINTS, GestureRecognizerMain.POINTS);
		GestureRecognizerMain.LOG.debug("Prv points: " + GestureRecognizerMain.PRV_POINTS.size());
	}
	
	private void recognizeGesture() {
		GestureRecognizer recognizer = new GestureRecognizer(PRV_POINTS);
		GestureResult[] result = recognizer.recognize(TEMPLATES);
		RESAMPLED_FIRST_LABEL.setText("Resampled first " + result[0]);
		RESAMPLED_LAST_LABEL.setText("Resampled last " + result[1]);
	}
}