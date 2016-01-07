package Truss;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JSpinner;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Component;
import javax.swing.JCheckBox;
import java.awt.Font;

public class EffectWizard extends JFrame implements ActionListener{

	public enum EffectPattern {
		SINE, RAMP_UP
	}
	
	Thread broadcast;
	Vector<Thread> effectThread = new Vector<Thread>();
	int effectLayer = 1, effectThreadIndex = 0;
	
	JLabel[] lblIndex = new JLabel[5];
	JComboBox[] comboType = new JComboBox[5];
	JComboBox[] comboPattern = new JComboBox[5];
	JTextField[] tfPhase = new JTextField[5];
	JCheckBox[] chkActive = new JCheckBox[5];
	JButton[] btnDelete = new JButton[5];
	String[] predefinedListArray = {"Dimmer Sine", "Dimmer Ramp Up", "Pan/Tilt Sine"};
	Effect[] effect = new Effect[5];
	
	JPanel contentPane;
	JTextField textField;
	JLabel lblMin, lblMax, lblPhase;
	JSlider speedSlider, minSlider, maxSlider, phaseSlider;
	JSpinner spinnerSpeed;
	JButton btnEffectLayer;
//
//	public static void main(String[] args) {
//		EffectWizard frame = new EffectWizard();
//		frame.setVisible(true);
//	}

	public void actionPerformed(ActionEvent ev) {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}; 
		
		event e = new event();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("Effect Editor");
		setResizable(false);
		
		textField = new JTextField();
		textField.setBounds(51, 11, 184, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 14, 31, 14);
		getContentPane().add(lblName);
		
		speedSlider = new JSlider();
		speedSlider.setValue(40);
		speedSlider.setFocusable(false);
		speedSlider.setPaintTicks(true);
		speedSlider.setOrientation(SwingConstants.VERTICAL);
		speedSlider.setBounds(20, 90, 53, 200);
		getContentPane().add(speedSlider);
		
		minSlider = new JSlider();
		minSlider.setValue(0);
		minSlider.setMajorTickSpacing(100);
		minSlider.setPaintLabels(true);
		minSlider.setFocusable(false);
		minSlider.setBounds(95, 90, 140, 32);
		getContentPane().add(minSlider);
		
		maxSlider = new JSlider();
		maxSlider.setValue(100);
		maxSlider.setPaintLabels(true);
		maxSlider.setMajorTickSpacing(100);
		maxSlider.setFocusable(false);
		maxSlider.setBounds(95, 165, 140, 32);
		getContentPane().add(maxSlider);
		
		phaseSlider = new JSlider();
		phaseSlider.setFocusable(false);
		phaseSlider.setPaintLabels(true);
		phaseSlider.setMajorTickSpacing(90);
		phaseSlider.setValue(0);
		phaseSlider.setMaximum(360);
		phaseSlider.setBounds(95, 249, 140, 32);
		contentPane.add(phaseSlider);
		
		spinnerSpeed = new JSpinner(new SpinnerNumberModel(40,0,100,1));
		spinnerSpeed.setBounds(10, 290, 70, 20);
		getContentPane().add(spinnerSpeed);

		JLabel lblSpeed = new JLabel("Speed (bpm)", SwingConstants.CENTER);
		lblSpeed.setBounds(10, 72, 70, 14);
		getContentPane().add(lblSpeed);
		
		lblMin = new JLabel("<html>Min (%): <b>0</b></html>");
		lblMin.setBounds(90, 72, 88, 14);
		contentPane.add(lblMin);
		
		lblMax = new JLabel("<html>Max (%): <b>100</b></html>");
		lblMax.setBounds(95, 147, 83, 14);
		contentPane.add(lblMax);
		
		lblPhase = new JLabel("<html>Phase (Deg): <b>0</b></html>");
		lblPhase.setBounds(95, 224, 120, 14);
		contentPane.add(lblPhase);
		
		// Effect Creator
		
		JLabel lblEffectType = new JLabel("Effect Type:");
		lblEffectType.setBounds(290, 19, 75, 14);
		contentPane.add(lblEffectType);
		
		JLabel lblEffectPattern = new JLabel("Effect Pattern:");
		lblEffectPattern.setBounds(460, 19, 83, 14);
		contentPane.add(lblEffectPattern);
			
		JLabel lblLoadPredefined = new JLabel("Load Predefined:");
		lblLoadPredefined.setBounds(754, 19, 89, 14);
		contentPane.add(lblLoadPredefined);		
		
		for(int a=0;a<5;a++){
				
			comboType[a] = new JComboBox(new Object[]{"Dimmer", "Red", "Green", "Blue", "Pan", "Tilt"});
			comboType[a].setFocusable(false);
			comboType[a].setBounds(290, 44 + (a*40), 150, 20);
			contentPane.add(comboType[a]);
			comboType[a].setVisible(false);
			comboType[a].addActionListener(e);
			
			comboPattern[a] = new JComboBox(new Object[]{"Sine", "Ramp Up"});
			comboPattern[a].setFocusable(false);
			comboPattern[a].setBounds(460, 44 + (a*40), 150, 20);
			contentPane.add(comboPattern[a]);
			comboPattern[a].setVisible(false);
			comboPattern[a].addActionListener(e);
			
			tfPhase[a] = new JTextField();
			tfPhase[a].setBounds(622, 44 + (a*40), 40, 20);
			tfPhase[a].setColumns(10);
			contentPane.add(tfPhase[a]);
			tfPhase[a].setVisible(false);
			tfPhase[a].addActionListener(e);
			
			lblIndex[a] = new JLabel(String.valueOf(a+1), SwingConstants.CENTER);
			lblIndex[a].setBounds(257, 47 + (a*40), 20, 14);
			contentPane.add(lblIndex[a]);	
			lblIndex[a].setVisible(false);
			
			chkActive[a] = new JCheckBox("");
			chkActive[a].setBounds(668, 43 + (a*40), 21, 23);
			contentPane.add(chkActive[a]);	
			chkActive[a].setVisible(false);
			
			btnDelete[a] = new JButton("X");
			btnDelete[a].setForeground(Color.RED);
			btnDelete[a].setFocusable(false);
			btnDelete[a].setFont(new Font("Tahoma", Font.BOLD, 14));
			btnDelete[a].setBounds(695, 43 + (a*40), 42, 23);
			contentPane.add(btnDelete[a]);
			btnDelete[a].setVisible(false);
			
		}
		
		comboType[0].setVisible(true);
		comboPattern[0].setVisible(true);
		tfPhase[0].setVisible(true);
		lblIndex[0].setVisible(true);
		btnDelete[0].setVisible(true);
		chkActive[0].setVisible(true);
		
		JButton btnSave = new JButton("Save");
		btnSave.setFocusable(false);
		btnSave.setBounds(785, 289, 89, 23);
		contentPane.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFocusable(false);
		btnCancel.setBounds(686, 289, 89, 23);
		contentPane.add(btnCancel);
		
		btnEffectLayer = new JButton("+ Effect Layer");
		btnEffectLayer.setFocusable(false);
		btnEffectLayer.setBounds(561, 272, 103, 40);
		contentPane.add(btnEffectLayer);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(754, 45, 120, 231);
		contentPane.add(scrollPane);
		
		JList predefinedList = new JList(predefinedListArray);
		scrollPane.setViewportView(predefinedList);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(742, 23, 2, 255);
		contentPane.add(separator_1);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(245, 11, 2, 299);
		contentPane.add(separator);
		
		JLabel lblPhase_1 = new JLabel("Phase:");
		lblPhase_1.setBounds(622, 19, 46, 14);
		contentPane.add(lblPhase_1);
		
		JLabel lblOn = new JLabel("On:");
		lblOn.setBounds(671, 19, 21, 14);
		contentPane.add(lblOn);

		speedSlider.addChangeListener(e);
		minSlider.addChangeListener(e);
		maxSlider.addChangeListener(e);
		phaseSlider.addChangeListener(e);
		spinnerSpeed.addChangeListener(e);
		btnEffectLayer.addActionListener(e);
		
		setVisible(true);
		
	}
	public class event implements ActionListener, ChangeListener {
		public void actionPerformed(ActionEvent e){
		
			if(e.getSource() == btnEffectLayer){
				comboType[effectLayer].setVisible(true);
				comboPattern[effectLayer].setVisible(true);
				tfPhase[effectLayer].setVisible(true);
				lblIndex[effectLayer].setVisible(true);
				btnDelete[effectLayer].setVisible(true);
				chkActive[effectLayer].setVisible(true);
				effectLayer++;
				if(effectLayer == 5){
					btnEffectLayer.setEnabled(false);
				}
				
				// Update when new effect type is selected
			} else if(e.getSource() == comboType[0] || e.getSource() == comboType[1] || e.getSource() == comboType[2] || e.getSource() == comboType[3] || e.getSource() == comboType[4]){

				int index = comboType[Arrays.asList(comboType).indexOf(((JComboBox)e.getSource()))].getSelectedIndex();

				effect[index].setType(Arrays.asList(main.channels).indexOf(((JComboBox)e.getSource()).getSelectedItem()));
				
				offEffect(effect[index]);
				executeEffect(effect[index], null);
				
				// Update when a new effect pattern is selected
			} else if(e.getSource() == comboPattern[0] || e.getSource() == comboPattern[1] || e.getSource() == comboPattern[2] || e.getSource() == comboPattern[3] || e.getSource() == comboPattern[4]){
				
				EffectPattern ep = null;
				int index = comboPattern[Arrays.asList(comboType).indexOf(((JComboBox)e.getSource()))].getSelectedIndex();
				
				switch (index){
					case 0: ep = EffectPattern.SINE;
					case 1: ep = EffectPattern.RAMP_UP;
				}
				effect[index].setPattern(ep);
				
				offEffect(effect[index]);
				executeEffect(effect[index], null);
				
			}
			
		}
		public void stateChanged(ChangeEvent e) {
			
			if(e.getSource() == speedSlider){
				spinnerSpeed.setValue(speedSlider.getValue());
			} else if(e.getSource() == spinnerSpeed){
				speedSlider.setValue((Integer)spinnerSpeed.getValue());
			} else if(e.getSource() == minSlider){
				lblMin.setText("<html>Min (%): <b>"+minSlider.getValue()+"</b></html>");
			} else if(e.getSource() == maxSlider){
				lblMax.setText("<html>Max (%): <b>"+maxSlider.getValue()+"</b></html>");
			} else if(e.getSource() == phaseSlider){
				lblPhase.setText("<html>Phase (Deg): <b>"+phaseSlider.getValue()+"</b></html>");
			}
			
		}
	}
	
	public void executeEffect(final Effect e, final Fixture[] fixtures){
		
			main.frame.prev_channel_data = main.frame.channel_data;

			final long refreshTime = (60000/e.getBPM())/180;

					/* 
					 * Create a thread for each channel that is effected that independently updates each time a new value is
					 * to be broadcasted. The 'broadcast' thread handles all output by broadcasting at the
					 * maximum DMX512 refresh rate of 44Hz. 
					 */
					for(int a=0;a<fixtures.length;a++){
						if(fixtures[a].getFixtureType().channel_function[e.getType()] != 0){
							final int c = a;
							
							Thread t = new Thread(){
								public void run(){
									
									int channel = fixtures[c].getStartChannel() + fixtures[c].getFixtureType().channel_function[e.getType()]-1;
									
									try {
										Thread.sleep(refreshTime*e.getPhase()*(c+1));
									} catch(Exception e){}

									while(true){
										
										for(int angle=1;angle<=180;angle++){
											
											if(e.getPattern() == EffectPattern.SINE){
												
												main.data[channel] = (byte)(255 * Math.sin(Math.toRadians(angle)));
												
											} else if(e.getPattern() == EffectPattern.RAMP_UP){
												
												main.data[channel] = (byte)(1.4167 * angle);
												
											}
											try {
												Thread.sleep(refreshTime);
											} catch(Exception e){}
										}

									}
								}
							};
							t.start();
							e.addThreadUsing(t);
							effectThread.addElement(t);
						}
							
					}

					Thread t1 = new Thread(){
						public void run(){

							while(true){
								
								// Broadcast
								if(main.artnet_node != null && !main.blackout_on) {
									main.dmx.setSequenceID(main.sequenceID % 255);
									main.dmx.setDMX(main.data, main.data.length);
									main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
									main.sequenceID++;
								}
								try {
									Thread.sleep((long)22.72);
								} catch(Exception e){}
						
							}
							
						}
					};
					t1.start();
					e.addThreadUsing(t1);
					effectThread.addElement(t1);
		
	}
	
	// Disables an effect by joining all of the threads it is using
	public void offEffect(Effect e){
		
		for(Thread t : e.getThreadsUsing()){
			try {
				t.join();
			} catch (Exception ex) {}
			effectThread.remove(t);
			e.removeThreadUsing(t);
		}
		/*
		 * restore channel data
		 */
		
	}
}
