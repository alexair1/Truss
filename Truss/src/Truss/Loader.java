package Truss;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.Font;

public class Loader extends JFrame {

	private JPanel contentPane;
	static JProgressBar loader;
	static JLabel loading_text;
	static boolean loaded = false;
	static main frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Loader frame = new Loader();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Loader() {
		startup();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 130);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		loader = new JProgressBar();
		loader.setBounds(6, 82, 438, 20);
		loader.setMaximum(100);
		contentPane.add(loader);
		
		JLabel lblTrussIsStarting = new JLabel("Truss is starting up.");
		lblTrussIsStarting.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblTrussIsStarting.setBounds(6, 6, 135, 16);
		contentPane.add(lblTrussIsStarting);
		
		loading_text = new JLabel();
		loading_text.setBounds(6, 54, 200, 16);
		contentPane.add(loading_text);
	}
	private void startup(){
		frame = new main();
	//	frame.setVisible(false);
	//	while(loaded = false){
	//		System.out.println("2");
	//	}
		
		frame.setVisible(true);
		this.setVisible(false);
	}
}
