package Truss;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class About extends JFrame implements ActionListener {
	public About() {
	}

	public void actionPerformed(ActionEvent e){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 400, 400);
		frame.setTitle("About Truss");
		frame.setVisible(true);
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(null);
		frame.setContentPane(panel);
		
		JLabel lblCreatingByAlex = new JLabel("Created by Alex Air \u00a9 2015", SwingConstants.CENTER);
		lblCreatingByAlex.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCreatingByAlex.setBounds(0, 316, 400, 14);
		panel.add(lblCreatingByAlex);
		
		JLabel lblNewLabel = new JLabel("v Alpha 1.1.0", SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(0, 341, 400, 14);
		panel.add(lblNewLabel);

		try {
			final BufferedImage img = ImageIO.read(new File("Truss@128px.png"));
			JLabel logo = new JLabel(new ImageIcon(img));
			logo.setBounds(136, 100, 128, 128);
			panel.add(logo);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
