package Truss;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.*;

public class fixtureWizard implements ActionListener {
	
	JButton create, cancel, ChooseColour;
	JTextField namefield;
	JSpinner channels, startchannel, amount;
	JCheckBox usingProfile, incrName, acg;
	JComboBox profileSelector;
	JLabel lblAmount, lblStartChannel, lblChannels, exampleLbl, errorLbl;
	JFrame frame = new JFrame();
	Color color = new Color(238, 238, 238);
	
	JColorChooser chooser = new JColorChooser();

	public void actionPerformed(ActionEvent a){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 320, 316);
		frame.setTitle("New Fixture");
		frame.setVisible(true);
		frame.setResizable(false);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(6, 12, 61, 16);
		panel.add(lblName);
		
		namefield = new JTextField();
		namefield.setBounds(55, 6, 134, 28);
		panel.add(namefield);
		namefield.setColumns(10);
		
		usingProfile = new JCheckBox("Profile");
		usingProfile.setSelected(true);
		usingProfile.setBounds(222, 8, 72, 23);
		panel.add(usingProfile);
		
		String[] profiles = new String[100];
		for(int b=0;b<100;b++){
			if(main.profile[b] != null){
				profiles[b] = main.profile[b].getName();
			}
		}
		
		profileSelector = new JComboBox(profiles);
		profileSelector.setBounds(6, 40, 183, 27);
		panel.add(profileSelector);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 79, 298, 12);
		panel.add(separator);
		
		lblChannels = new JLabel("Channels:");
		lblChannels.setEnabled(false);
		lblChannels.setBounds(6, 103, 72, 16);
		panel.add(lblChannels);
		
		channels = new JSpinner();
		channels.setValue(1);
		channels.setEnabled(false);
		channels.setBounds(71, 97, 50, 28);
		panel.add(channels);
		
		lblStartChannel = new JLabel("Start Channel:");
		lblStartChannel.setBounds(142, 103, 88, 16);
		panel.add(lblStartChannel);
		
		startchannel = new JSpinner();
		startchannel.setValue(1);
		startchannel.setBounds(233, 97, 61, 28);
		panel.add(startchannel);
		
		lblAmount = new JLabel("Amount:");
		lblAmount.setBounds(141, 131, 61, 16);
		panel.add(lblAmount);
		
		amount = new JSpinner();
		amount.setValue(1);
		amount.setBounds(233, 125, 61, 28);
		panel.add(amount);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 215, 298, 12);
		panel.add(separator_1);
		
		create = new JButton("Create");
		create.setBounds(204, 253, 100, 29);
		panel.add(create);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(99, 253, 100, 29);
		panel.add(cancel);
		
		incrName = new JCheckBox("Incr. Name");
		incrName.setEnabled(false);
		incrName.setBounds(4, 160, 100, 23);
		panel.add(incrName);
		
		exampleLbl = new JLabel("eg.");
		exampleLbl.setEnabled(false);
		exampleLbl.setBounds(142, 163, 152, 16);
		panel.add(exampleLbl);
		
		errorLbl = new JLabel("																									Ready to Create");
		errorLbl.setBounds(6, 226, 288, 16);
		panel.add(errorLbl);
		
		ChooseColour = new JButton("Choose Colour");
		ChooseColour.setBounds(199, 38, 105, 29);
		panel.add(ChooseColour);
		
		acg = new JCheckBox("Auto Create Group");
		acg.setBounds(4, 185, 152, 23);
		panel.add(acg);
		
		event e = new event();
		create.addActionListener(e);
		cancel.addActionListener(e);
		usingProfile.addActionListener(e);
		
		namefield.addKeyListener(e);
		channels.addChangeListener(e);
		channels.addKeyListener(e);
		startchannel.addChangeListener(e);
		startchannel.addKeyListener(e);
		amount.addChangeListener(e);
		amount.addKeyListener(e);
		ChooseColour.addActionListener(e);
		
	}
	public class event implements ActionListener, KeyListener, ChangeListener {
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == create){
				
				int startChannel = (Integer)startchannel.getValue();

				if((usingProfile.isSelected()) && (incrName.isSelected())){

					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText()+"-"+(a+1), main.profile[profileSelector.getSelectedIndex()], startChannel, main.profile[profileSelector.getSelectedIndex()].getChannels(), true, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += main.profile[profileSelector.getSelectedIndex()].getChannels();
					}
					
				} else if((usingProfile.isSelected()) && (!incrName.isSelected())){
						
						Profile p = main.profile[profileSelector.getSelectedIndex()];
						
					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText(), p, startChannel, p.getChannels(), true, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += p.getChannels();
					}
					
				} else if((!usingProfile.isSelected()) && (incrName.isSelected())){

					for(int a=0;a<(Integer)amount.getValue();a++){
			//			main.fixture[main.fixtureNumber] = new Fixture(namefield.getText()+"-"+(a+1), , startChannel, (Integer)channels.getValue(), false, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += (Integer)channels.getValue();
					}
					
				} else if((!usingProfile.isSelected()) && (!incrName.isSelected())){
					
					for(int a=0;a<(Integer)amount.getValue();a++){
			//			main.fixture[main.fixtureNumber] = new Fixture(namefield.getText(), "Custom", startChannel, (Integer)channels.getValue(), false, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += (Integer)channels.getValue();
					}
					
				}
				if(acg.isSelected()){
					main.group[main.group_counter] = new Group(namefield.getText(), main.profile[profileSelector.getSelectedIndex()]);
					for(int b=0;b<(Integer)amount.getValue();b++){
						main.group[main.group_counter-1].addMember(main.fixture[main.fixtureNumber-(Integer)amount.getValue()+b]);
					}
					main.groupNames.add(namefield.getText());
				}
				
				frame.dispose();
				
			} else if(e.getSource() == cancel){
				frame.dispose();
			} else if(e.getSource() == usingProfile){
				if(usingProfile.isSelected()){
					channels.setEnabled(false);
					lblChannels.setEnabled(false);
					profileSelector.setEnabled(true);
				} else {
					channels.setEnabled(true);
					lblChannels.setEnabled(true);
					profileSelector.setEnabled(false);
				}
			} else if(e.getSource() == ChooseColour){
				
				color = chooser.showDialog(frame, "Fader Colour", new Color(238,238,238));
				ChooseColour.setBackground(color);
				
			}
		}
		public void stateChanged(ChangeEvent e) {	
			checkValues(e);	 
		}
		
		private void updatePatchTable(){
			
			main.patch_data[main.fixtureNumber][1] = main.fixture[main.fixtureNumber].getName();
			main.patch_data[main.fixtureNumber][2] = main.fixture[main.fixtureNumber].getFixtureType().getName();
			main.patch_data[main.fixtureNumber][3] = main.fixture[main.fixtureNumber].getStartChannel() + "-" + (main.fixture[main.fixtureNumber].getStartChannel()+main.fixture[main.fixtureNumber].getChannels()-1);
			Loader.frame.patch_table.repaint();
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
				
			} else if(e.getSource() == channels || e.getSource() == startchannel || e.getSource() == amount){			
				checkValues(e);			
			}
		}
	}
	private void checkValues(EventObject e){
		
		if( (Integer)((JSpinner)e.getSource()).getValue() < 1 || (Integer)((JSpinner)e.getSource()).getValue() > 512 ){
			errorLbl.setForeground(Color.RED);
			errorLbl.setText("Values must be between 1 and 512");
			create.setEnabled(false);
		}
		
		if((((Integer)amount.getValue() > 0) && ((Integer)amount.getValue() < 513)) && (((Integer)channels.getValue() > 0) && ((Integer)channels.getValue() < 513)) && (((Integer)startchannel.getValue() > 0) && ((Integer)startchannel.getValue() < 513))){
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
