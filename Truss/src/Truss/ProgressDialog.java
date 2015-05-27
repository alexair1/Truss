package Truss;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

public class ProgressDialog extends JFrame {

	private JPanel contentPane;
	JProgressBar progressBar;
	JLabel lblComplete;

	public ProgressDialog(String text) {
		setUndecorated(true);
		setTitle("Progress");
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(Loader.frame.getX()+((1290/2)-200), Loader.frame.getY()+((680/2)-100), 200, 90);
		contentPane = new JPanel();
		contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		};
		
		JLabel lblTextGoesHere = new JLabel(text);
		lblTextGoesHere.setBounds(10, 11, 200, 14);
		contentPane.add(lblTextGoesHere);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 36, 180, 14);
		contentPane.add(progressBar);
		
		lblComplete = new JLabel("0% Complete");
		lblComplete.setBounds(10, 61, 146, 14);
		contentPane.add(lblComplete);
		
	}
	public void setProgress(int percent){
		progressBar.setValue(percent);
		lblComplete.setText(percent + "% Complete");
	}
}
