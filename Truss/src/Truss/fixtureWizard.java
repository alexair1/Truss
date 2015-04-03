package Truss;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.*;

public class fixtureWizard implements ActionListener {
	
	JButton create, cancel, ChooseColour;
	JTextField namefield, manu_tf, model_tf;
	JSpinner channels, startchannel, amount;
	JCheckBox usingProfile, incrName, acg;
	JComboBox profileSelector;
	JLabel lblAmount, lblStartChannel, lblChannels, exampleLbl, errorLbl;
	JFrame frame = new JFrame();
	JTable profile_table;
	Color color = new Color(238, 238, 238);
	
	String[][] profiles = new String[100][3];
	String[][] profiles_original;
	
	JColorChooser chooser = new JColorChooser();
	
	/**
	 * @wbp.parser.entryPoint
	 */

	
	public void actionPerformed(ActionEvent a){
		
		for(int b=0;b<100;b++){
			for(int c=0;c<3;c++){
				if(main.profile[b] != null){
					if(c == 0){
						profiles[b][c] = main.profile[b].getManufacturer();
					} else if(c == 1){
						profiles[b][c] = main.profile[b].getName();
					} else if(c == 2){
						profiles[b][c] = main.profile[b].getMode();
					}	
				}
			}
		}
		profiles_original = profiles;
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 450, 355);
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
		usingProfile.setBounds(230, 8, 94, 23);
		panel.add(usingProfile);
		
		lblChannels = new JLabel("Channels:");
		lblChannels.setEnabled(false);
		lblChannels.setBounds(6, 219, 72, 16);
		panel.add(lblChannels);
		
		channels = new JSpinner();
		channels.setValue(1);
		channels.setEnabled(false);
		channels.setBounds(71, 213, 50, 28);
		panel.add(channels);
		
		lblStartChannel = new JLabel("Start Channel:");
		lblStartChannel.setBounds(142, 219, 88, 16);
		panel.add(lblStartChannel);
		
		startchannel = new JSpinner();
		startchannel.setValue(1);
		startchannel.setBounds(233, 213, 61, 28);
		panel.add(startchannel);
		
		lblAmount = new JLabel("Amount:");
		lblAmount.setBounds(141, 247, 61, 16);
		panel.add(lblAmount);
		
		amount = new JSpinner();
		amount.setValue(1);
		amount.setBounds(233, 241, 61, 28);
		panel.add(amount);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(6, 280, 426, 12);
		panel.add(separator_1);
		
		create = new JButton("Create");
		create.setBounds(334, 291, 100, 29);
		panel.add(create);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(224, 291, 100, 29);
		panel.add(cancel);
		
		incrName = new JCheckBox("Incr. Name");
		incrName.setEnabled(false);
		incrName.setBounds(315, 233, 79, 23);
		panel.add(incrName);
		
		exampleLbl = new JLabel("eg.");
		exampleLbl.setEnabled(false);
		exampleLbl.setBounds(318, 253, 152, 16);
		panel.add(exampleLbl);
		
		errorLbl = new JLabel("Ready to Create");
		errorLbl.setBounds(6, 297, 208, 16);
		panel.add(errorLbl);
		
		ChooseColour = new JButton("Choose Colour");
		ChooseColour.setEnabled(false);
		ChooseColour.setBounds(329, 6, 105, 29);
		panel.add(ChooseColour);
		
		acg = new JCheckBox("Auto Create Group");
		acg.setEnabled(false);
		acg.setBounds(315, 214, 117, 23);
		panel.add(acg);
		
		profile_table = new JTable(profiles, new Object[] {"Manufacturer","Model","Mode"}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			}
		};
		profile_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		profile_table.setFocusable(false);
		
		JScrollPane scrollPane = new JScrollPane(profile_table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(6, 39, 428, 169);
		panel.add(scrollPane);
		
		manu_tf = new JTextField();
		manu_tf.setVisible(false);
		manu_tf.setBounds(77, 40, 134, 20);
		panel.add(manu_tf);
		manu_tf.setColumns(10);
		
		model_tf = new JTextField();
		model_tf.setVisible(false);
		model_tf.setBounds(252, 40, 180, 20);
		panel.add(model_tf);
		model_tf.setColumns(10);
		
		JLabel lblModel = new JLabel("Model");
		lblModel.setVisible(false);
		lblModel.setBounds(217, 43, 28, 14);
		panel.add(lblModel);
		
		JLabel lblManufacturer = new JLabel("Manufacturer");
		lblManufacturer.setVisible(false);
		lblManufacturer.setBounds(6, 43, 65, 14);
		panel.add(lblManufacturer);
		
		event e = new event();
		create.addActionListener(e);
		cancel.addActionListener(e);
		usingProfile.addActionListener(e);
		model_tf.addKeyListener(e);
		manu_tf.addKeyListener(e);
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
				
				String fullname = "";
				try {
					fullname = profile_table.getValueAt(profile_table.getSelectedRow(), 0).toString().replace(" ", "_")+"-"+profile_table.getValueAt(profile_table.getSelectedRow(), 1).toString().replace(" ", "_")+"@"+profile_table.getValueAt(profile_table.getSelectedRow(), 2).toString().replace(" ", "_");
				} catch(Exception ex){
					errorLbl.setForeground(Color.RED);
					errorLbl.setText("Please select a profile");
					return;
				}

				if((usingProfile.isSelected()) && (incrName.isSelected())){

					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText()+"-"+(a+1), fullname, startChannel, main.getProfileByName(fullname).getChannels(), true, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += main.getProfileByName(fullname).getChannels();
					}
					
				} else if((usingProfile.isSelected()) && (!incrName.isSelected())){

					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText(), fullname, startChannel, main.getProfileByName(fullname).getChannels(), true, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += main.getProfileByName(fullname).getChannels();
					}
					
				} else if((!usingProfile.isSelected()) && (incrName.isSelected())){

					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText()+"-"+(a+1), "Custom", startChannel, (Integer)channels.getValue(), false, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += (Integer)channels.getValue();
					}
					
				} else if((!usingProfile.isSelected()) && (!incrName.isSelected())){
					
					for(int a=0;a<(Integer)amount.getValue();a++){
						main.fixture[main.fixtureNumber] = new Fixture(namefield.getText(), "Custom", startChannel, (Integer)channels.getValue(), false, color); 
						updatePatchTable();
						main.fixtureNumber++;
						startChannel += (Integer)channels.getValue();
					}
					
				}
//				if(acg.isSelected()){
//					main.group[main.group_counter] = new Group(namefield.getText(), "Dimmer");
//					for(int b=0;b<(Integer)amount.getValue();b++){
//						main.group[main.group_counter-1].addMember(main.dimmer[main.dimmerNumber-(Integer)amount.getValue()+b]);
//					}
//					main.groupNames.add(namefield.getText());
//				}
				
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
	 		main.fixture_data[Loader.frame.fixture_table.getSelectedRow()][Loader.frame.fixture_table.getSelectedColumn()] = "<html>&emsp;" + main.fixture[main.fixtureNumber].getName() + "<br>&emsp; " + main.fixtureNumber + "</html>";
			Loader.frame.fixture_table.repaint();
		}
		
		/*
		 * Key Events
		 */
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
			} else if(e.getSource() == manu_tf){
				
			//	ArrayList<String[]> list = new ArrayList<String[]>();
				
//				int c = 0;
//				for(int a=0;a<100;a++){
//					if(main.profile[a] != null){
//						if(profiles[a][0].toLowerCase().startsWith(manu_tf.getText().toLowerCase())){
//							profiles[a][0] = "";
//							profiles[a][1] = "";
//							profiles[a][2] = "";
//							profiles[c] = profiles_original[a];
//							c++;
//						} else {
//							profiles[a][0] = "";
//							profiles[a][1] = "";
//							profiles[a][2] = "";
//						}
//					}
//				}
//				profile_table.repaint();
				
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
