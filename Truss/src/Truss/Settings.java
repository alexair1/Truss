package Truss;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import artnet4j.ArtNetNode;

import javax.swing.JRadioButton;
import javax.swing.JButton;

public class Settings extends JFrame implements ActionListener {

	public enum Operation {
		NODE_DISCOVERED, NODE_DISCONECCTED
	}
	
	JFormattedTextField broadcastadr_tf1, broadcastadr_tf2, broadcastadr_tf3, broadcastadr_tf4, defaultadr_tf1, defaultadr_tf2, defaultadr_tf3, defaultadr_tf4;
	JTable table;
	JButton btnApply;
	JRadioButton cmyBtn, rgbBtn;
//	ArrayList<ArtNetNode> nodes = new ArrayList<ArtNetNode>();
	String[][] nodes = new String[25][3];

	public static void main(String[] args) {

		Settings frame = new Settings();
		frame.setVisible(true);
	}

	public Settings() {
		
		JPanel contentPane;
		
		nodes[0][0] = "Test Data";
		nodes[0][1] = "10.10.10.10";
		nodes[0][2] = "2";
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("Settings");
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		};  
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(-1, -1, 598, 325);
		contentPane.add(tabbedPane);
		
		JPanel General = new JPanel();
		tabbedPane.addTab("General", null, General, null);
		General.setLayout(null);
		
		JPanel Control = new JPanel();
		tabbedPane.addTab("Control", null, Control, null);
		Control.setLayout(null);
		
		JLabel lblColourMixing = new JLabel("Colour Mixing:");
		lblColourMixing.setBounds(10, 11, 68, 14);
		Control.add(lblColourMixing);
		
		rgbBtn = new JRadioButton("RGB");
		rgbBtn.setBounds(84, 7, 45, 23);
		Control.add(rgbBtn);
		
		cmyBtn = new JRadioButton("CMY");
		cmyBtn.setBounds(84, 25, 55, 23);
		Control.add(cmyBtn);
		
		ButtonGroup colour_sel = new ButtonGroup();
		colour_sel.add(rgbBtn);
		colour_sel.add(cmyBtn);
		
		JPanel Network = new JPanel();
		tabbedPane.addTab("Network", null, Network, null);
		Network.setLayout(null);
		
		JCheckBox chckbxUseFirstArtnet = new JCheckBox("Use first ArtNet node discovered");
		chckbxUseFirstArtnet.setSelected(true);
		chckbxUseFirstArtnet.setBounds(6, 7, 183, 23);
		Network.add(chckbxUseFirstArtnet);
		
		JLabel lblBroadcastAddress = new JLabel("Broadcast address:");
		lblBroadcastAddress.setBounds(258, 33, 94, 14);
		Network.add(lblBroadcastAddress);
		
		 MaskFormatter address_formatter = null;
		    try {
		    	address_formatter.setValidCharacters("0123456789");
		    	address_formatter.setPlaceholder("255");
		    } catch (Exception e) {}
		
		broadcastadr_tf1 = new JFormattedTextField(address_formatter);
		broadcastadr_tf1.setBounds(361, 33, 30, 20);
		broadcastadr_tf1.setText("255");
		Network.add(broadcastadr_tf1);
		broadcastadr_tf1.setColumns(10);
		
		broadcastadr_tf2 = new JFormattedTextField(address_formatter);
		broadcastadr_tf2.setColumns(10);
		broadcastadr_tf2.setText("255");
		broadcastadr_tf2.setBounds(401, 33, 30, 20);
		Network.add(broadcastadr_tf2);
		
		broadcastadr_tf3 = new JFormattedTextField(address_formatter);
		broadcastadr_tf3.setColumns(10);
		broadcastadr_tf3.setText("255");
		broadcastadr_tf3.setBounds(441, 33, 30, 20);
		Network.add(broadcastadr_tf3);
		
		broadcastadr_tf4 = new JFormattedTextField(address_formatter);
		broadcastadr_tf4.setColumns(10);
		broadcastadr_tf4.setText("255");
		broadcastadr_tf4.setBounds(481, 33, 30, 20);
		Network.add(broadcastadr_tf4);
		
		JLabel label = new JLabel(".            .            .");
		label.setBounds(395, 40, 150, 14);
		Network.add(label);
		
		JLabel lblDefaultNodeAddress = new JLabel("Default node address:");
		lblDefaultNodeAddress.setEnabled(false);
		lblDefaultNodeAddress.setBounds(244, 9, 107, 14);
		Network.add(lblDefaultNodeAddress);
		
		table = new JTable(nodes, new String[]{"Name", "IP Address", "# of Ports"});
		table.setBounds(99, 108, 1, 1);
		Network.add(table);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(6, 62, 580, 229);
		Network.add(scrollPane);		
		
		defaultadr_tf1 = new JFormattedTextField(address_formatter);
		defaultadr_tf1.setEnabled(false);
		defaultadr_tf1.setText("10");
		defaultadr_tf1.setColumns(10);
		defaultadr_tf1.setBounds(361, 7, 30, 20);
		Network.add(defaultadr_tf1);
		
		defaultadr_tf2 = new JFormattedTextField(address_formatter);
		defaultadr_tf2.setEnabled(false);
		defaultadr_tf2.setText("1");
		defaultadr_tf2.setColumns(10);
		defaultadr_tf2.setBounds(401, 7, 30, 20);
		Network.add(defaultadr_tf2);
		
		defaultadr_tf3 = new JFormattedTextField(address_formatter);
		defaultadr_tf3.setEnabled(false);
		defaultadr_tf3.setText("1");
		defaultadr_tf3.setColumns(10);
		defaultadr_tf3.setBounds(441, 7, 30, 20);
		Network.add(defaultadr_tf3);
		
		defaultadr_tf4 = new JFormattedTextField(address_formatter);
		defaultadr_tf4.setEnabled(false);
		defaultadr_tf4.setText("99");
		defaultadr_tf4.setColumns(10);
		defaultadr_tf4.setBounds(481, 7, 30, 20);
		Network.add(defaultadr_tf4);
		
		JLabel label_1 = new JLabel(".            .            .");
		label_1.setBounds(395, 15, 150, 14);
		Network.add(label_1);
		
		JCheckBox chckbxSendDmx = new JCheckBox("Send DMX");
		chckbxSendDmx.setSelected(true);
		chckbxSendDmx.setBounds(6, 29, 97, 23);
		Network.add(chckbxSendDmx);
		
		btnApply = new JButton("Apply");
		btnApply.setBounds(495, 337, 89, 23);
		contentPane.add(btnApply);
		
	}
	
	/*
	 * Updates the list of artnet nodes in the settings menu.
	 * Takes enum to determine if node has been discovered or disconnected
	 */
	public void updateNodeList(ArtNetNode node, Operation op){
		
		switch(op) {
		
			case NODE_DISCOVERED: {
				
	//			nodes.add(node);
				
			}
		
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnApply){
			
			saveSettings();
			
		}
		
	}
	
	private void saveSettings(){
		
		try {
			
			FileOutputStream o = new FileOutputStream("");
			Properties p = new Properties();
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
