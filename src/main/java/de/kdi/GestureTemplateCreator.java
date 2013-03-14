package de.kdi;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.kdi.pojo.GestureTemplate;
import de.kdi.pojo.Point;

public class GestureTemplateCreator {

	public static void addTemplate(List<Point> points, double squareSize, int N){
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
		int n = JOptionPane.showOptionDialog(GestureRecognizerMain.frame, inputs, "Add Template", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
	
		//Process input
		if(n == 1){
			//Check input
			if(!nameText.getText().equals("")){
				//Create the templates in both files based on the raw input points
				GestureTemplateCreator.addTemplateToFile(new GestureTemplate(nameText.getText(), points, squareSize, N, true), "ResampledFirstTemplates.txt");
				GestureTemplateCreator.addTemplateToFile(new GestureTemplate(nameText.getText(), points, squareSize, N, false), "ResampledLastTemplates.txt");
				
				GestureRecognizerMain.resampledFirstTemplates.clear();
				GestureRecognizerMain.resampledLastTemplates.clear();
				
				GestureTemplateCreator.readTemplates("ResampledFirstTemplates.txt", true, GestureRecognizerMain.resampledFirstTemplates);
				GestureTemplateCreator.readTemplates("ResampledLastTemplates.txt", false, GestureRecognizerMain.resampledLastTemplates);
				
				GestureTemplateCreator.listUniqueGestures();
				//Notify the user
				JOptionPane.showMessageDialog(GestureRecognizerMain.frame, "Template "+templateName+" successfully added.", "Succes", JOptionPane.INFORMATION_MESSAGE);
			} else{
				JOptionPane.showMessageDialog(GestureRecognizerMain.frame, "Error. Input was incorrect.", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public static void addTemplateToFile(GestureTemplate template, String fileName) {
		GestureRecognizerMain.log.debug("Adding template with "+template.points.size()+" points.");
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    
		    out.println();
		    out.println("#"+template.name);
		    
		    for(Point p:template.points){
		    	out.println(p.x+" "+p.y);
		    }
		    
		    out.close();
		} catch (IOException e) {
			GestureRecognizerMain.log.warn("Could not write to file.");
		}
	}

	static void listUniqueGestures(){
		ArrayList<String> tmpData = new ArrayList<String>();
		for(GestureTemplate t : GestureRecognizerMain.resampledFirstTemplates){
			if(!tmpData.contains(t.name)){
				tmpData.add(t.name);
			}
		}
	
		String gestures = "  ";
		for(String s : tmpData){
			gestures += s + ", ";
		}
		GestureRecognizerMain.templateLabel.setText("Available gestures: "+gestures.substring(0, gestures.length()-2));
	}

	static void readTemplates(String fileName, boolean resampledFirst, ArrayList<GestureTemplate> templates){
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
			GestureRecognizerMain.log.warn("Can't find file.");
		}
	
		if(resampledFirst){
			GestureRecognizerMain.log.debug(templates.size()+" resampled first templates loaded.");
		} else {
			GestureRecognizerMain.log.debug(templates.size()+" resampled last templates loaded.");
		}
	}

}
