package de.kdi.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.kdi.GestureRecognizerMain;

public class ButtonListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e){
		JButton b = (JButton) e.getSource();
		if(b.equals(GestureRecognizerMain.saveButton)){
			if(!GestureRecognizerMain.prvPoints.isEmpty()){
				GestureRecognizerMain.addTemplate(GestureRecognizerMain.prvPoints, GestureRecognizerMain.squareSize, GestureRecognizerMain.N);
			}
			GestureRecognizerMain.prvPoints.clear();
		}
	}
}