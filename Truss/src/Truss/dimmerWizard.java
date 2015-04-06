package Truss;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.*;

public class dimmerWizard implements ActionListener {
	
	JButton create, cancel, btnChooseColour;
	JTextField namefield;
	JSpinner startchannel, amount;
	JCheckBox incrName, acg;
	JLabel lblAmount, lblStartChannel, exampleLbl, errorLbl;
	JFrame frame = new JFrame();
	
	Color color = new Color(238,238,238);
	JColorChooser chooser = new JColorChooser();
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void actionPerformed(ActionEvent a){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 315, 285);
		frame.setTitle("New Dimmer");
		frame.setVisible(true);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(6, 12, 61, 16);
		panel.add(lblName);
		
		namefield = new JTextField();
		namefield.setBounds(55, 6, 239, 28);
		panel.add(namefield);
		namefield.setColumns(10);

		JSeparator separator = new JSeparator();
		separator.setBounds(6, 40, 288, 12);
		panel.add(separator);
		
		lblStartChannel = new JLabel("Start Channel:");
		lblStartChannel.setBounds(133, 64, 88, 16);
		panel.add(lblStartChannel);
		
		startchannel = new JSpinner();
		startchannel.setValue(1);
		startchannel.setBounds(233, 58, 61, 28);
		panel.add(startchannel);
		
		lblAmount = new JLabel("Amount:");
		lblAmount.setBounds(160, 92, 61, 16);
		panel.add(lblAmount);
		
		amount = new JSpinner();
		amount.setValue(1);
		amount.setBounds(233, 86, 61, 28);
		panel.add(amount);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 160, 288, 12);
		panel.add(separator_1);
		
		create = new JButton("Create");
		create.setFocusable(false);
		create.setBounds(194, 212, 100, 29);
		panel.add(create);
		
		cancel = new JButton("Cancel");
		cancel.setFocusable(false);
		cancel.setBounds(96, 212, 100, 29);
		panel.add(cancel);
		
		incrName = new JCheckBox("Incr. Name");
		incrName.setFocusable(false);
		incrName.setEnabled(false);
		incrName.setBounds(6, 64, 100, 23);
		panel.add(incrName);
		
		exampleLbl = new JLabel("eg.");
		exampleLbl.setEnabled(false);
		exampleLbl.setBounds(16, 92, 105, 16);
		panel.add(exampleLbl);
		
		errorLbl = new JLabel("Ready to Create");
		errorLbl.setBounds(6, 184, 288, 16);
		panel.add(errorLbl);
		
		acg = new JCheckBox("Auto Create Group");
		acg.setFocusable(false);
		acg.setEnabled(false);
		acg.setBounds(6, 120, 135, 28);
		panel.add(acg);
		
		btnChooseColour = new JButton("Choose Colour");
		btnChooseColour.setFocusable(false);
		btnChooseColour.setBounds(189, 119, 105, 30);
		panel.add(btnChooseColour);
		
		event e = new event();
		
		create.addActionListener(e);
		cancel.addActionListener(e);	
		namefield.addKeyListener(e);
		startchannel.addChangeListener(e);
		startchannel.addKeyListener(e);
		amount.addChangeListener(e);
		amount.addKeyListener(e);
		btnChooseColour.addActionListener(e);
		
	}
	public class event implements ActionListener, KeyListener, ChangeListener {
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == create){
				
				int startChannel = (Integer)startchannel.getValue();
				Fixture[] f = new Fixture[(Integer)amount.getValue()];
				
				if(incrName.isSelected()){

					for(int a=0;a<(Integer)amount.getValue();a++){
						f[a] = new Fixture(namefield.getText()+"-"+(a+1), "Dimmer", startChannel, 1, true, color);
				//		updatePatchTable();
						startChannel ++;
						System.out.println(f[a].getStartChannel());
					}
					
				} else {

					for(int a=0;a<(Integer)amount.getValue();a++){ 
						f[a] = new Fixture(namefield.getText(), "Dimmer", startChannel, 1, true, color); 
			//			updatePatchTable();
						startChannel ++;
					}
					
				}
				
//				if(acg.isSelected()){
//					main.group[main.group_counter] = new Group(namefield.getText(), "Dimmer");
//					for(int b=0;b<(Integer)amount.getValue();b++){
//						main.group[main.group_counter-1].addMember(main.dimmer[main.dimmerNumber-(Integer)amount.getValue()+b]);
//					}
//					main.groupNames.add(namefield.getText());
//				}
				main.dimmer[main.dimmerNumber] = new Dimmer(namefield.getText(), f);
				
				updatePatchTable();
				main.dimmerNumber++;
				
		//		main.setFaderWingPage((Integer)main.fw_page_spinner.getValue());
				frame.dispose();
				
			} else if(e.getSource() == cancel){
				frame.dispose();
			} else if(e.getSource() == btnChooseColour){
				
				color = chooser.showDialog(frame, "Fader Colour", new Color(238,238,238));
				btnChooseColour.setBackground(color);
				
			}
		}
		public void stateChanged(ChangeEvent e) {	
			checkValues(e);	 
		}

		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
			if(e.getSource() == namefield){
				
				if(namefield.getText().equals("")){
					exampleLbl.setText("eg.");
				} else {
					exampleLbl.setText("eg. " + namefield.getText() + "-2");
				}
				
			} else if(e.getSource() == startchannel || e.getSource() == amount){			
				checkValues(e);			
			}
		}
	}
	private void updatePatchTable(){
		main.dimmer_data[Loader.frame.dimmer_table.getSelectedRow()][Loader.frame.dimmer_table.getSelectedColumn()] = "<html>&emsp;" + namefield.getText() + "<br>&emsp; " + main.dimmerNumber + "</html>";
	//	main.dimmer_data[main.dimmerNumber-1][1] = main.dimmer[main.dimmerNumber].getName();
	//	main.dimmer_data[main.dimmerNumber-1][2] = main.dimmer[main.dimmerNumber].getStartChannel();
		Loader.frame.dimmer_table.repaint();
	}
	
	private void checkValues(EventObject e){
		
		if( (Integer)((JSpinner)e.getSource()).getValue() < 1 || (Integer)((JSpinner)e.getSource()).getValue() > 512 ){
			errorLbl.setForeground(Color.RED);
			errorLbl.setText("Values must be between 1 and 512");
			create.setEnabled(false);
		}
		
		if((((Integer)amount.getValue() > 0) && ((Integer)amount.getValue() < 513)) && ((((Integer)startchannel.getValue() > 0) && ((Integer)startchannel.getValue() < 513)))){
			errorLbl.setForeground(Color.BLACK);
			errorLbl.setText("						  																			Ready to Create");
			create.setEnabled(true);
		}
		
		if(e.getSource() == amount){
			if((Integer)amount.getValue() > 1){
				incrName.setEnabled(true);
			} else {
				incrName.setEnabled(false);
			}
		}
		
	}
}
