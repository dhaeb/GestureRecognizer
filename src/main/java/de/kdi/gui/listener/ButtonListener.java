package de.kdi.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.kdi.GestureRecognizerMain;
import de.kdi.GestureTemplateCreator;

public class ButtonListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e){
		JButton b = (JButton) e.getSource();
		if(b.equals(GestureRecognizerMain.saveButton)){
			if(!GestureRecognizerMain.prvPoints.isEmpty()){
				GestureTemplateCreator.addTemplate(GestureRecognizerMain.prvPoints, GestureRecognizerMain.squareSize, GestureRecognizerMain.N);
			}
			GestureRecognizerMain.prvPoints.clear();
		}
	}
}