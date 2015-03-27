package Truss;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

public class wesbian implements FocusListener {
	
	JButton button;
	
	public wesbian(){
		
		/*
		 * Pretend there is a whole bunch of crap to create the JFrame here
		 */

		button = new JButton("penis");
		button.setVisible(false);
		
		JTextField tf = new JTextField();
		
		tf.addFocusListener(this);
		
	}

	public void focusGained(FocusEvent e) {
		button.setVisible(true);
	}
	public void focusLost(FocusEvent e) {
		button.setVisible(false);
	}
	
}
