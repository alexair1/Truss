package Truss;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

public class editFixture implements ActionListener {
	
	JButton create, cancel;
	JTextField namefield;
	JSpinner channels, startchannel;
	JCheckBox usingProfile;
	JComboBox profileSelector;
	JLabel lblStartChannel, lblChannels, errorLbl;
	JFrame frame = new JFrame();
	JList fixture_list;
	private JSeparator separator_1;
	private JScrollPane scrollPane;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void actionPerformed(ActionEvent a){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 490, 260);
		frame.setTitle("Edit Fixture");
		frame.setVisible(true);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(173, 12, 61, 16);
		panel.add(lblName);
		
		namefield = new JTextField();
		namefield.setBounds(246, 6, 134, 28);
		panel.add(namefield);
		namefield.setColumns(10);
		
		usingProfile = new JCheckBox("Profile");
		usingProfile.setSelected(true);
		usingProfile.setBounds(392, 8, 72, 23);
		panel.add(usingProfile);
		
		String[] profiles = new String[100];
		for(int b=0;b<100;b++){
			if(main.profile[b] != null){
				profiles[b] = main.profile[b].getName();
			}
		}
		
		profileSelector = new JComboBox(profiles);
		profileSelector.setBounds(167, 40, 213, 27);
		panel.add(profileSelector);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(143, 6, 12, 206);
		panel.add(separator);
		
		lblChannels = new JLabel("Channels:");
		lblChannels.setEnabled(false);
		lblChannels.setBounds(173, 109, 72, 16);
		panel.add(lblChannels);
		
		channels = new JSpinner();
		channels.setValue(1);
		channels.setEnabled(false);
		channels.setBounds(273, 103, 61, 28);
		panel.add(channels);
		
		lblStartChannel = new JLabel("Start Channel:");
		lblStartChannel.setBounds(173, 79, 88, 16);
		panel.add(lblStartChannel);
		
		startchannel = new JSpinner();
		startchannel.setValue(1);
		startchannel.setBounds(273, 73, 61, 28);
		panel.add(startchannel);
		
		create = new JButton("Save");
		create.setBounds(364, 183, 100, 29);
		panel.add(create);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(268, 183, 100, 29);
		panel.add(cancel);
		
		errorLbl = new JLabel("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tReady to Save");
		errorLbl.setBounds(173, 155, 288, 16);
		panel.add(errorLbl);
		
		separator_1 = new JSeparator();
		separator_1.setBounds(156, 137, 308, 12);
		panel.add(separator_1);

		Vector<String> fixtureNames = new Vector<String>();
		for(int b=1;b<513;b++){
			if(main.fixture[b] != null){
				fixtureNames.add(main.fixture[b].getName());
			}
		}
		
		fixture_list = new JList(fixtureNames);
		fixture_list.setSelectedIndex(0);
		fixture_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane = new JScrollPane(fixture_list);
		scrollPane.setBounds(6, 10, 125, 202);
		panel.add(scrollPane);
		
		event e = new event();
		create.addActionListener(e);
		cancel.addActionListener(e);
		usingProfile.addActionListener(e);
		fixture_list.addListSelectionListener(e);
		
		namefield.addKeyListener(e);
		channels.addChangeListener(e);
		channels.addKeyListener(e);
		startchannel.addChangeListener(e);
		startchannel.addKeyListener(e);
		
		namefield.setText(main.fixture[fixture_list.getSelectedIndex()+1].getName());
		usingProfile.setSelected(main.fixture[fixture_list.getSelectedIndex()+1].isUsingProfile());
		startchannel.setValue(main.fixture[fixture_list.getSelectedIndex()+1].getStartChannel());
		channels.setValue(main.fixture[fixture_list.getSelectedIndex()+1].getChannels());
		
		if(!usingProfile.isSelected()){
			profileSelector.setEnabled(false);
		}
		
	}
	public class event implements ActionListener, KeyListener, ChangeListener, ListSelectionListener {
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == create){
				if(usingProfile.isSelected()){
					Truss.fixture[Truss.fixtureNumber] = new Fixture(namefield.getText(), (String)profileSelector.getSelectedItem(), (Integer)startchannel.getValue(), Truss.profile[profileSelector.getSelectedIndex()].getChannels(), true); 
					Truss.fixtureNumber++;
				} else if(!usingProfile.isSelected()){
					Truss.fixture[Truss.fixtureNumber] = new Fixture(namefield.getText(), "", (Integer)startchannel.getValue(), Truss.profile[profileSelector.getSelectedIndex()].getChannels(), false); 
					Truss.fixtureNumber++;
				}
				Truss.updatePatchTable();
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
			}
		}
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == channels){
				if(((Integer)channels.getValue() < 1) || ((Integer)channels.getValue() > 512)){
					errorLbl.setForeground(Color.RED);
					errorLbl.setText("Values must be between 1 and 512");
					create.setEnabled(false);
				}
			} else if(e.getSource() == startchannel){
				if(((Integer)startchannel.getValue() < 1) || ((Integer)startchannel.getValue() > 512)){
					errorLbl.setForeground(Color.RED);
					errorLbl.setText("Values must be between 1 and 512");
					create.setEnabled(false);
				}
			}
			
			if((e.getSource() == channels) || (e.getSource() == startchannel)){
				if((((Integer)channels.getValue() > 0) && ((Integer)channels.getValue() < 513)) && (((Integer)startchannel.getValue() > 0) && ((Integer)startchannel.getValue() < 513))){
					errorLbl.setForeground(Color.BLACK);
					errorLbl.setText("																									Ready to Create");
					create.setEnabled(true);
				}
			}
		}
		public void keyPressed(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {
			if(e.getSource() == channels){
				if(((Integer)channels.getValue() < 1) || ((Integer)channels.getValue() > 512)){
					errorLbl.setForeground(Color.RED);
					errorLbl.setText("Values must be between 1 and 512");
					create.setEnabled(false);
				}
			} else if(e.getSource() == startchannel){
				if(((Integer)startchannel.getValue() < 1) || ((Integer)startchannel.getValue() > 512)){
					errorLbl.setForeground(Color.RED);
					errorLbl.setText("Values must be between 1 and 512");
					create.setEnabled(false);
				}
			}
			if((e.getSource() == channels) || (e.getSource() == startchannel)){
				if((((Integer)channels.getValue() > 0) && ((Integer)channels.getValue() < 513)) && (((Integer)startchannel.getValue() > 0) && ((Integer)startchannel.getValue() < 513))){
					errorLbl.setForeground(Color.BLACK);
					errorLbl.setText("																									Ready to Create");
					create.setEnabled(true);
				}
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			System.out.println("1");
		}
	}
}
