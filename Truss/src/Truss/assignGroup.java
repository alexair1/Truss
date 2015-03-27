package Truss;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class assignGroup implements ActionListener {

	JFrame frame = new JFrame();
	JTextField tf;
	JComboBox selector;
	JButton btnAddGroup, btnAssign, btnCancel;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void actionPerformed(ActionEvent a){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setBounds(100, 100, 210, 200);
		frame.setTitle("Group Assign");
		frame.setVisible(true);
		
		selector = new JComboBox(main.groupNames);
		selector.setBounds(6, 6, 200, 27);
		panel.add(selector);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 45, 200, 12);
		panel.add(separator);
		
		tf = new JTextField();
		tf.setBounds(6, 69, 198, 28);
		panel.add(tf);
		tf.setColumns(10);
		
		btnAddGroup = new JButton("Add Group");
		btnAddGroup.setBounds(89, 100, 117, 29);
		panel.add(btnAddGroup);
		
		btnAssign = new JButton("Assign");
		btnAssign.setBounds(117, 141, 87, 29);
		panel.add(btnAssign);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(36, 141, 87, 29);
		panel.add(btnCancel);
		
		event e = new event();
		btnAddGroup.addActionListener(e);
		btnAssign.addActionListener(e);
		btnCancel.addActionListener(e);

	}
	public class event implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btnAddGroup){
				
				main.groupNames.add(tf.getText());
				main.group[main.group_counter] = new Group(tf.getText(), main.fixture[Loader.frame.patch_table.getSelectedRow()+1].getFixtureType());
		//		System.out.println(main.group[main.group_counter-1].counter);
				
			} else if(e.getSource() == btnAssign){
				
	//			System.out.println(main.fixture[Loader.frame.patch_table.getSelectedRow()+1].name);
				main.group[selector.getSelectedIndex()+1].addMember(main.fixture[Loader.frame.patch_table.getSelectedRow()+1]);
				frame.dispose();
				
			} else if(e.getSource() == btnCancel){
			
				frame.dispose();
				
			}
		}
	}
}