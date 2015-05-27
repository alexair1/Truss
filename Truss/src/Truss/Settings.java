package Truss;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.JFormattedTextField;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Settings extends JFrame {

	JPanel contentPane;
	private JFormattedTextField broadcastadr_tf1, broadcastadr_tf2, broadcastadr_tf3, broadcastadr_tf4;

	public static void main(String[] args) {

					Settings frame = new Settings();
					frame.setVisible(true);
	}

	public Settings() {
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
		tabbedPane.setBounds(0, 0, 594, 371);
		contentPane.add(tabbedPane);
		
		JPanel General = new JPanel();
		tabbedPane.addTab("General", null, General, null);
		
		JPanel Control = new JPanel();
		tabbedPane.addTab("Control", null, Control, null);
		
		JPanel Network = new JPanel();
		tabbedPane.addTab("Network", null, Network, null);
		Network.setLayout(null);
		
		JCheckBox chckbxUseFirstArtnet = new JCheckBox("Use first ArtNet node discovered");
		chckbxUseFirstArtnet.setSelected(true);
		chckbxUseFirstArtnet.setBounds(6, 7, 183, 23);
		Network.add(chckbxUseFirstArtnet);
		
		JLabel lblBroadcastAddress = new JLabel("Broadcast address:");
		lblBroadcastAddress.setBounds(222, 11, 94, 14);
		Network.add(lblBroadcastAddress);
		
		 MaskFormatter address_formatter = null;
		    try {
		    	address_formatter.setValidCharacters("0123456789");
		    	address_formatter.setPlaceholder("255");
		    } catch (Exception e) {}
		
		broadcastadr_tf1 = new JFormattedTextField(address_formatter);
		broadcastadr_tf1.setBounds(326, 8, 30, 20);
		Network.add(broadcastadr_tf1);
		broadcastadr_tf1.setColumns(10);
		
		broadcastadr_tf2 = new JFormattedTextField(address_formatter);
		broadcastadr_tf2.setColumns(10);
		broadcastadr_tf2.setBounds(366, 8, 30, 20);
		Network.add(broadcastadr_tf2);
		
		broadcastadr_tf3 = new JFormattedTextField(address_formatter);
		broadcastadr_tf3.setColumns(10);
		broadcastadr_tf3.setBounds(406, 8, 30, 20);
		Network.add(broadcastadr_tf3);
		
		broadcastadr_tf4 = new JFormattedTextField(address_formatter);
		broadcastadr_tf4.setColumns(10);
		broadcastadr_tf4.setBounds(446, 8, 30, 20);
		Network.add(broadcastadr_tf4);
		
		JLabel label = new JLabel(".            .            .");
		label.setBounds(360, 15, 150, 14);
		Network.add(label);
		
		JLabel lblDefaultNodeAddress = new JLabel("Default node address:");
		lblDefaultNodeAddress.setBounds(11, 37, 107, 14);
		Network.add(lblDefaultNodeAddress);
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 62, 573, 100);
		Network.add(panel);
		
			
		
		
	}
}
