package de.kdi.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.kdi.GestureRecognizerMain;
import de.kdi.GestureTemplateCreator;
import de.kdi.recognizer.GestureRecognizer;

public class ButtonListener implements ActionListener {
	
	private GestureRecognizerMain view;

	public ButtonListener(GestureRecognizerMain view) {
		this.view = view;
	}
	
	public void actionPerformed(ActionEvent e){
		JButton b = (JButton) e.getSource();
		if(b.equals(GestureRecognizerMain.SAVE_BUTTON)){
			if(!GestureRecognizerMain.PRV_POINTS.isEmpty()){
				String gestureNames = GestureTemplateCreator.addTemplate(GestureRecognizerMain.PRV_POINTS, GestureRecognizer.SQUARE_SIZE, GestureRecognizer.N);
				view.getTemplateLabel().setText("Available gestures: " + gestureNames);
			}
			GestureRecognizerMain.PRV_POINTS.clear();
		}
	}
}