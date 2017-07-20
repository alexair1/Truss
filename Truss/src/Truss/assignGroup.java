package Truss;

import java.awt.event.*;
import javax.swing.*;

public class assignGroup implements ActionListener {

	JFrame frame = new JFrame();
	JTextField tf;
	JComboBox selector;
	JButton btnAddGroup, btnAssign, btnCancel;
	
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
				
			} else if(e.getSource() == btnAssign){
				
				Fixture f;
				for(int x=0;x<Loader.frame.patch_table.getSelectedRows().length;x++){
					f = main.fixture[(Integer)Loader.frame.patch_table.getValueAt(Loader.frame.patch_table.getSelectedRows()[x], 0)-1];
					main.group[selector.getSelectedIndex()+1].addMember(f);
				}
				
				frame.dispose();
				
			} else if(e.getSource() == btnCancel){
			
				frame.dispose();
				
			}
		}
	}
}