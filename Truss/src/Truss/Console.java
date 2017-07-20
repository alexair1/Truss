package Truss;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import Truss.fixtureWizard.event;

import java.awt.Color;
import java.awt.Font;

public class Console implements ActionListener{
	
	private enum Command {
		SET,
		HIGHLIGHT,
		SOLO
	}
	Command c;
	int a,b;
	
	JFrame frame = new JFrame();
	private JTextField tf;
	JButton btnPerc, btnSet, btnHighlight, btnSolo, btnThrough, btnDmx, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnClear;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JButton button_3;
	private JButton button_4;
	private JButton button_5;
	private JButton button_6;
	private JButton btnGo;
	
	/**
	 * @wbp.parser.entryPoint
	 */	
	public void actionPerformed(ActionEvent a){
		JPanel panel = new JPanel();
		panel.setLayout(null);
    
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 320, 345);
		frame.setTitle("Console");
		frame.setVisible(true);
		
		tf = new JTextField();
		tf.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tf.setBounds(10, 11, 286, 30);
		panel.add(tf);
		tf.setColumns(10);
		
		btnSet = new JButton("SET");
		btnSet.setBounds(10, 52, 89, 23);
		panel.add(btnSet);
		
		btnHighlight = new JButton("HIGHLIGHT");
		btnHighlight.setBounds(109, 52, 89, 23);
		panel.add(btnHighlight);
		
		btnSolo = new JButton("SOLO");
		btnSolo.setBounds(208, 52, 89, 23);
		panel.add(btnSolo);
		
		btnThrough = new JButton("THROUGH");
		btnThrough.setBounds(10, 86, 89, 23);
		panel.add(btnThrough);
		
		btnDmx = new JButton("DMX");
		btnDmx.setBounds(109, 86, 89, 23);
		panel.add(btnDmx);
		
		btnPerc = new JButton("%");
		btnPerc.setBounds(208, 86, 89, 23);
		panel.add(btnPerc);
		
		btn1 = new JButton("1");
		btn1.setBounds(10, 120, 89, 35);
		panel.add(btn1);
		
		btn2 = new JButton("2");
		btn2.setBounds(109, 120, 89, 35);
		panel.add(btn2);
		
		btn3 = new JButton("3");
		btn3.setBounds(208, 120, 89, 35);
		panel.add(btn3);
		
		btn4 = new JButton("4");
		btn4.setBounds(10, 166, 89, 35);
		panel.add(btn4);
		
		btn5 = new JButton("5");
		btn5.setBounds(109, 166, 89, 35);
		panel.add(btn5);
		
		btn6 = new JButton("6");
		btn6.setBounds(208, 166, 89, 35);
		panel.add(btn6);
		
		btn7 = new JButton("7");
		btn7.setBounds(10, 212, 89, 35);
		panel.add(btn7);
		
		btn8 = new JButton("8");
		btn8.setBounds(109, 212, 89, 35);
		panel.add(btn8);
		
		btn9 = new JButton("9");
		btn9.setBounds(208, 212, 89, 35);
		panel.add(btn9);
		
		btn0 = new JButton("0");
		btn0.setBounds(10, 258, 89, 35);
		panel.add(btn0);
		
		btnGo = new JButton("GO");
		btnGo.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnGo.setBounds(207, 258, 89, 35);
		panel.add(btnGo);
		
		btnClear = new JButton("CLEAR");
		btnClear.setBounds(109, 258, 89, 35);
		panel.add(btnClear);
		
		event e = new event();
		btnSet.addActionListener(e);
		btnHighlight.addActionListener(e);
		btnSolo.addActionListener(e);
		btnThrough.addActionListener(e);
		btnDmx.addActionListener(e);
		btnPerc.addActionListener(e);
		btnGo.addActionListener(e);
		btnClear.addActionListener(e);
		btn0.addActionListener(e);
		btn1.addActionListener(e);
		btn2.addActionListener(e);
		btn3.addActionListener(e);
		btn4.addActionListener(e);
		btn5.addActionListener(e);
		btn6.addActionListener(e);
		btn7.addActionListener(e);
		btn8.addActionListener(e);
		btn9.addActionListener(e); 
		tf.addKeyListener(e);
		
	}
	public class event implements ActionListener, KeyListener {
		public void actionPerformed(ActionEvent e){
			tf.setForeground(Color.BLACK);
			
			if(e.getSource() == btnSet){
				
				tf.setText(tf.getText() + "SET ");
				
			} else if(e.getSource() == btnHighlight){
				
				tf.setText(tf.getText() + "HIGHLIGHT ");
				
			} else if(e.getSource() == btnSolo){
				
				tf.setText(tf.getText() + "SOLO ");
				
			} else if(e.getSource() == btnThrough){
				
				tf.setText(tf.getText() + "-");
				
			} else if(e.getSource() == btnDmx){
				
				tf.setText(tf.getText() + " DMX ");
				
			} else if(e.getSource() == btnPerc){
				
				tf.setText(tf.getText() + " % ");
				
			} else if(e.getSource() == btnGo){
				
				executeString(tf.getText());
				
			} else if(e.getSource() == btnClear){
				
				tf.setText(null);
				
			} else if(e.getSource() == btn0){
				
				tf.setText(tf.getText() + "0");
				
			} else if(e.getSource() == btn1){
				
				tf.setText(tf.getText() + "1");
				
			} else if(e.getSource() == btn2){
				
				tf.setText(tf.getText() + "2");
				
			} else if(e.getSource() == btn3){
				
				tf.setText(tf.getText() + "3");
				
			} else if(e.getSource() == btn4){

				tf.setText(tf.getText() + "4");

			} else if(e.getSource() == btn5){
				
				tf.setText(tf.getText() + "5");

			} else if(e.getSource() == btn6){
				
				tf.setText(tf.getText() + "6");

			} else if(e.getSource() == btn7){
				
				tf.setText(tf.getText() + "7");

			} else if(e.getSource() == btn8){

				tf.setText(tf.getText() + "8");

			} else if(e.getSource() == btn9){
				
				tf.setText(tf.getText() + "9");
			}
		}

		public void keyPressed(KeyEvent k) {
			if(k.getKeyCode() == 10){
				executeString(tf.getText());
			}
		}
		public void keyReleased(KeyEvent arg0) {}
		public void keyTyped(KeyEvent arg0) {}
	}
	
	public void executeString(String string){
		tf.setText("");
		String[] part = string.split(" ");
		
		if(part[0].equals("SET")){
			c = Command.SET;
		} else if(part[0].equals("HIGHLIGHT")){
			c = Command.HIGHLIGHT;
		} else if(part[0].equals("SOLO")){
			c = Command.SOLO;
		} else if(part[0].equals("CLEAR")){
			for(a=0;a<512;a++){
				main.data[a] = 0;
			}
			main.broadcast(main.data, true);
			return;
		} else {
			tf.setForeground(Color.RED);
			return;
		}
		int startNum, endNum;
		
		if(part[1].contains("-")){
			startNum = Integer.parseInt(part[1].split("-")[0]);
			endNum = Integer.parseInt(part[1].split("-")[1]);
			
		} else {
			startNum = endNum = Integer.parseInt(part[1].split("-")[0]);
		}
		
		switch(c){
			case SET:
				
				int value = 255;
				if(part[2].equals("DMX")){
					value = Integer.parseInt(part[3]);
				} else if(part[2].equals("%")){
					value = (int) (2.55*Integer.parseInt(part[3]));
				}
				
				for(a=startNum; a<=endNum; a++){
					main.data[a-1] = value;
				}
				
				break;
			case HIGHLIGHT:
				
				for(a=startNum; a<=endNum; a++){
					main.data[a-1] = 255;
				}
				
				break;
			case SOLO:

				for(a=0;a<512;a++){
					main.data[a] = 0;
				}
				for(a=startNum; a<=endNum; a++){
					main.data[a-1] = 255;
				}
				
				break;
			
		}
		
		main.broadcast(main.data, true);
	}
}

//**Commands**
//
//SET
//	CHANNELS
//		\FIRSTCHANNEL\
//		-
//			\LASTCHANNEL\
//				DMX
//					\VALUE\
//				%
//					\VALUE\
//HIGHLIGHT
//	CHANNELS
//		\FIRSTCHANNEL\
//		-
//		\LASTCHANNEL\
//SOLO
//		CHANNELS
//		\FIRSTCHANNEL\
//		-
//		\LASTCHANNEL\
//		
//*Examples*
//
//SET 6-28 DMX 255
//
//HIGHLIGHT 3-4
//
//SOLO 98
	
