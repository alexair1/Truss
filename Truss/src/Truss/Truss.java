package Truss;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.SocketException;
import java.util.List;
import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

public class Truss extends JFrame implements ActionListener, ChangeListener, MouseListener, ArtNetDiscoveryListener{
	
	int[] channel_data = new int[513];
	Preset[] preset = new Preset[350];
	JSlider[] bank_fader;
	cueStack[] cueStack = new cueStack[100];
	static JToggleButton[] fixture_select_btn = new JToggleButton[512];
	static JToggleButton[] group_select_btn = new JToggleButton[512];
	static Object[][] patch_data = new Object[512][4];
	static Fader[] ctrl_fader = new Fader[512];
	static Fader[] fw_fader = new Fader[18];
	static Fixture[] fixture = new Fixture[513];
//	static Cue[] cue = new Cue[1000];
	static Profile[] profile = new Profile[100];
	static Group[] group = new Group[513];
	static int ctrl_fader_counter = 0, cueStackCounter = 0, fixture_select_btn_counter = 0, group_select_btn_counter = 0, fixtureNumber = 1, profileID = -1, fader_wing_counter = 0, group_counter = 1;
	static JLabel lbl_nothingpatched;
	static JPanel fixture_select, control, fixture_sel_panel, group_sel_panel;
	static Object selectedFixture = null;
	static File currently_loaded_show;
	static Vector groupNames = new Vector();
//	static Vector<Preset> preset = new Vector<Preset>();
	
	JButton execute_preset, assign_current_output, group_btn, store_cue_btn, page1_btn, page2_btn, page3_btn, page4_btn, remote_btn, add_cue, black_out, new_cue_stack, load_show, next_cue, prev_cue, new_fixture, edit_fixture, clear_sel, save_show;
	JSlider cue_slider, master_slider, fade_slider;
	JTextField bank_fader1_spinner, bank_fader2_spinner, textField, new_cue_stack_tf;
	JPanel cue, patch_and_control, contentPane, fw, presets;
	JTable patch_table, presets_grid;
	JComboBox cue_stack_selector;
	JScrollPane patch_table_pane;
	JTabbedPane fixture_sel_and_ctrl, screens;
	JCheckBox execute_on_select;
	JLabel cur_sel_id, cur_sel_name, cur_sel_type, lbl_nothingselected, error_disp, number_of_cues_lbl, fade_val, active_preset_lbl;
	JSpinner cue_counter, master_spinner;
	ArtNetNode artnet_node;

	Thread clear_flash, blackout_th;
	JToggleButton selectedFixture_btn;
	FileOutputStream file_stream_out;
	Properties show_settings = new Properties();
	FileNameExtensionFilter load_show_filter;
	Vector cueStackNames = new Vector();
	boolean blackout_on = false;
	private JSeparator separator_3;
	private JButton btnSetColour;

	public void initiate() {
		Thread artnet_discovery = new Thread(){
			public void run(){
				error_disp.setText("Polling");
				ArtNet artnet = new ArtNet();
		        try {
		            artnet.start();
		            artnet.getNodeDiscovery().addListener(Truss.this);
		            artnet.startNodeDiscovery();
		        } catch (Exception e) {
		            e.printStackTrace();
		        } 
			};
		};
		artnet_discovery.start();  
		
//		Loader.loading_text.setText("Initialising");
		boolean b = true;
		int objects_amt = 0;
		try {
			ObjectInputStream counter_stream = new ObjectInputStream(new FileInputStream("test.txt"));
			while(b == true){
				try{
					counter_stream.readObject();
					objects_amt++;
				} catch(Exception e){
			//		e.printStackTrace();
					b = false;
				}
			}
			counter_stream.close();
		} catch(Exception e){
			e.printStackTrace();
		} 
		
		Vector<Object>[] vector = new Vector[100];
		b = true;
		int c = -1, d = -1;
		String profile_name="";
		try {
			ObjectInputStream obj_in = new ObjectInputStream(new FileInputStream("test.txt"));
				while(b == true){
					try{
						Object obj = obj_in.readObject();
						if(obj instanceof String){
							if(((String) obj).charAt(0) == '*'){
								if((d != -1) || (obj == "*")){
									profile[d] = new Profile(profile_name, vector[d]);
									c = -1;
								}
								d++;
								vector[d] = new Vector();
								profile_name = ((String) obj).substring(1);
							} else {
								c++;
								vector[d].add(new Vector());
								((Vector<Object>) vector[d].get(c)).add(obj);
							}
						} else {
							((Vector<Object>) vector[d].get(c)).add(obj);
						}
				//		Loader.loader.setValue(Loader.loader.getValue() + 100/objects_amt);
					} catch(Exception e){
				//		e.printStackTrace();
						b = false;
					}
				}
				obj_in.close();
				Loader.loaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		load_show_filter = new FileNameExtensionFilter("Truss Show File", "truss");
	}  

	public Truss() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 710);
		setMinimumSize(new Dimension(1280, 710));
		patch_and_control = new JPanel();
		patch_and_control.setLayout(null);
		patch_and_control.setBackground(new Color(230, 230, 230));
		
		cue = new JPanel();
		cue.setLayout(null);
		cue.setBackground(new Color(230, 230, 230));
		
		fw = new JPanel();
		fw.setBackground(new Color(230, 230, 230));
		
		presets = new JPanel();
		presets.setBackground(new Color(230, 230, 230));
		
		contentPane = new JPanel();
	//	contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setLayout(null);
	//	contentPane.setBackground(new Color(0,0,0));
		
		setContentPane(contentPane);
		setTitle("Truss, Alpha 1.0");
		setResizable(false);
		
		for(int a=0;a<512;a++){
			patch_data[a][0] = a+1;
		}
		
	/*	try {
			SynthLookAndFeel laf = new SynthLookAndFeel();
			laf.load(new FileInputStream("src/look_and_feel.xml"), this.getClass());
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			e.printStackTrace();
		}  */
		
		// Main Screen Selector
		screens = new JTabbedPane(JTabbedPane.TOP);
		screens.setBounds(6, 13, 977, 675);
		contentPane.add(screens);
		screens.addTab("Patch and Control", patch_and_control);
	//	screens.addTab("Cue", cue);
		screens.addTab("Fader Wing", fw);
		screens.addTab("Presets", presets);
		presets.setLayout(null);
		
		// Patch and Control Screen
		
		patch_table = new JTable(patch_data, new Object[] {"ID", "Fixture Name", "Fixture", "Channels"});
		patch_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		patch_table.setBounds(2, 18, 450, 8208);
		
		patch_table_pane = new JScrollPane(patch_table);
		patch_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		patch_table_pane.setBounds(7, 13, 832, 218);
		patch_and_control.add(patch_table_pane);
		
		new_fixture = new JButton("New Fixture");
		new_fixture.setBounds(851, 9, 99, 40);
		patch_and_control.add(new_fixture);
		
		edit_fixture = new JButton("Edit");
		edit_fixture.setEnabled(false);
		edit_fixture.setBounds(875, 61, 75, 29);
		patch_and_control.add(edit_fixture);
		
		group_btn = new JButton("Group");
		group_btn.setEnabled(false);
		group_btn.setBounds(875, 88, 75, 29);
		patch_and_control.add(group_btn);
		
			// Fixture Select and Control
			
			fixture_sel_and_ctrl = new JTabbedPane();
			fixture_sel_and_ctrl.setBounds(0, 243, 950, 370);
			patch_and_control.add(fixture_sel_and_ctrl);
		
			fixture_select = new JPanel();
			fixture_select.setBackground(new Color(222, 222, 222));
			fixture_sel_and_ctrl.addTab("Select", fixture_select);
			fixture_select.setLayout(null);
		
			lbl_nothingpatched = new JLabel("No fixtures patched.", SwingConstants.CENTER);
			lbl_nothingpatched.setBounds(6, 50, 917, 16);
			fixture_select.add(lbl_nothingpatched);
			
			fixture_sel_panel = new JPanel();
			fixture_sel_panel.setLayout(new GridLayout(0,5));
			fixture_sel_panel.setBackground(new Color(222, 222, 222));
			
			for(int b=0;b<512;b++){
				fixture_select_btn[b] = new JToggleButton();
				fixture_select_btn[b].addMouseListener(this);
				fixture_select_btn[b].setVisible(false);
				fixture_sel_panel.add(fixture_select_btn[b]);
			}
			
			group_sel_panel = new JPanel(new GridLayout(0,5));
			group_sel_panel.setBackground(new Color(222, 222, 222));
			
			for(int b=0;b<512;b++){
				group_select_btn[b] = new JToggleButton();
				group_select_btn[b].addMouseListener(this);
				group_select_btn[b].setVisible(false);
				group_sel_panel.add(group_select_btn[b]);
			}
			
			JScrollPane fixture_sp = new JScrollPane(fixture_sel_panel);
			fixture_sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			fixture_sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			fixture_sp.setBorder(null);
			fixture_sp.setBounds(0, 0, 929, 158);
			fixture_select.add(fixture_sp);
			
			JScrollPane group_sp = new JScrollPane(group_sel_panel);
			group_sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			group_sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			group_sp.setBorder(null);
			group_sp.setBounds(0, 160, 929, 164);
			fixture_select.add(group_sp);

			
			control = new JPanel();
			control.setBackground(new Color(222, 222, 222));
			
			int x=0;
			for(int a=0;a<50;a++){
				ctrl_fader[a] = new Fader();
				ctrl_fader[a].create(ctrl_fader_counter, control, new Color(222, 222, 222));
				ctrl_fader[a].setChannel("1/"+(a+1));
				ctrl_fader[a].setFaderVisible(false);
				ctrl_fader_counter++;
			}
			
			JScrollPane sp = new JScrollPane(control);
			sp.setBounds(0, 0, 900, 300);
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			sp.setBorder(null);
			fixture_sel_and_ctrl.addTab("Control", sp);
		
			lbl_nothingselected = new JLabel("No fixtures selected.", SwingConstants.CENTER);
			lbl_nothingselected.setBounds(6, 50, 917, 16);
			control.add(lbl_nothingselected);
			
		// Cue Screen
			
		next_cue = new JButton("Next");
		next_cue.setBounds(875, 557, 75, 50);
		cue.add(next_cue);
			
		prev_cue = new JButton("Prev");
		prev_cue.setBounds(788, 557, 75, 50);
		cue.add(prev_cue);
			
		cue_counter = new JSpinner();
		cue_counter.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		cue_counter.setBounds(686, 560, 90, 38);
		cue.add(cue_counter);
			
		cue_slider = new JSlider(0, 0, 0);
		cue_slider.setSnapToTicks(true);
		cue_slider.setPaintLabels(true);
		cue_slider.setMajorTickSpacing(10);
		cue_slider.setBounds(6, 508, 944, 37);
		cue.add(cue_slider);
			
		cue_stack_selector = new JComboBox(cueStackNames);
		cue_stack_selector.setEditable(false);
		cue_stack_selector.setBounds(6, 18, 130, 27);
		cue.add(cue_stack_selector);
			
		new_cue_stack = new JButton("+ Cue Stack");
		new_cue_stack.setBounds(788, 85, 162, 29);
		cue.add(new_cue_stack);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(775, 18, 12, 478);
		cue.add(separator);
		
		new_cue_stack_tf = new JTextField();
		new_cue_stack_tf.setBounds(788, 50, 162, 28);
		cue.add(new_cue_stack_tf);
		
		JLabel lblNewCueName = new JLabel("Name");
		lblNewCueName.setBounds(788, 22, 36, 16);
		cue.add(lblNewCueName);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(788, 126, 162, 12);
		cue.add(separator_1);
		
		JLabel lblCurrentCueStack = new JLabel("Current Cue Stack");
		lblCurrentCueStack.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblCurrentCueStack.setBounds(829, 144, 121, 16);
		cue.add(lblCurrentCueStack);
		
		add_cue = new JButton("+ Cue");
		add_cue.setBounds(875, 200, 75, 29);
		cue.add(add_cue);
		
		number_of_cues_lbl = new JLabel("No. of Cues:");
		number_of_cues_lbl.setBounds(788, 172, 162, 16);
		cue.add(number_of_cues_lbl);
		
		store_cue_btn = new JButton("Store");
		store_cue_btn.setBounds(875, 230, 75, 29);
		cue.add(store_cue_btn);

		// Fader Wing
		for(int z=0;z<18;z++){
			fw_fader[z] = new Fader();
			fw_fader[z].create(z, fw, new Color(230, 230, 230));
			fw_fader[z].setChannel("1/"+(z+1));
			fw_fader[z].setName("-");
		}
		
		// Presets Screen
		presets_grid = new JTable(new Object[35][10], new Object[]{"","","","","","","","","",""});
		presets_grid.setRowSelectionAllowed(false);
		presets_grid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		presets_grid.setColumnSelectionAllowed(false);
		presets_grid.setCellSelectionEnabled(true);
		presets_grid.setGridColor(Color.LIGHT_GRAY);
		
		JScrollPane presets_sp = new JScrollPane(presets_grid);
		presets_sp.setBounds(6, 6, 944, 566);
		presets.add(presets_sp);
		
		assign_current_output = new JButton("Assign Current Output");
		assign_current_output.setBounds(6, 594, 186, 29);
		assign_current_output.setEnabled(false);
		presets.add(assign_current_output);
		
		execute_preset = new JButton("Execute");
		execute_preset.setBounds(204, 594, 93, 29);
		presets.add(execute_preset);
		
		execute_on_select = new JCheckBox("Execute on Select");
		execute_on_select.setBounds(295, 595, 142, 23);
		presets.add(execute_on_select);

		// contentPane content
		save_show = new JButton();
		save_show.setBounds(1196, 650, 32, 32);
		save_show.setBorder(BorderFactory.createEmptyBorder());
		save_show.setIcon(new ImageIcon("src/img/png/save.png"));
		contentPane.add(save_show);
		
		load_show = new JButton();
		load_show.setBorder(BorderFactory.createEmptyBorder());
		load_show.setIcon(new ImageIcon("src/img/png/load.png"));
		load_show.setBounds(1242, 650, 32, 32);
		contentPane.add(load_show);
		
		clear_sel = new JButton("Clear");
		clear_sel.setBounds(1184, 70, 90, 29);
		contentPane.add(clear_sel);
		
		JLabel cur_sel_title = new JLabel("Current Selection");
		cur_sel_title.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		cur_sel_title.setBounds(995, 14, 116, 16);
		contentPane.add(cur_sel_title);
		
		cur_sel_id = new JLabel("ID--");
		cur_sel_id.setBounds(995, 42, 50, 16);
		contentPane.add(cur_sel_id);
		
		cur_sel_name = new JLabel("-");
		cur_sel_name.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		cur_sel_name.setBounds(1051, 42, 90, 16);
		contentPane.add(cur_sel_name);
		
		cur_sel_type = new JLabel("-");
		cur_sel_type.setBounds(1153, 42, 121, 16);
		contentPane.add(cur_sel_type);
		
		error_disp = new JLabel("", SwingConstants.RIGHT);
		error_disp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		error_disp.setForeground(Color.RED);
		error_disp.setBounds(995, 554, 279, 16);
		contentPane.add(error_disp);
		
		black_out = new JButton("Black Out");
		black_out.setBounds(995, 639, 104, 43);
		contentPane.add(black_out);
		
		remote_btn = new JButton("Sync");
		remote_btn.setBounds(1184, 582, 90, 29);
		contentPane.add(remote_btn);	
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(995, 104, 279, 12);
		contentPane.add(separator_2);
		
		separator_3 = new JSeparator();
		separator_3.setBounds(995, 530, 279, 12);
		contentPane.add(separator_3);
		
		master_slider = new JSlider(0, 255, 0);
		master_slider.setMinorTickSpacing(15);
		master_slider.setPaintTicks(true);
		master_slider.setOrientation(SwingConstants.VERTICAL);
		master_slider.setBounds(1209, 264, 65, 224);
		contentPane.add(master_slider);
		
		master_spinner = new JSpinner();
		master_spinner.setBounds(1209, 490, 65, 28);
		contentPane.add(master_spinner);
		
		JLabel lblMaster = new JLabel("Master", SwingConstants.CENTER);
		lblMaster.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblMaster.setBounds(1209, 251, 65, 16);
		contentPane.add(lblMaster);
		
		fade_slider = new JSlider(0, 20000, 0);
		fade_slider.setMinorTickSpacing(100);
		fade_slider.setSnapToTicks(true);
		fade_slider.setOrientation(SwingConstants.VERTICAL);
		fade_slider.setBounds(1117, 264, 80, 224);
		contentPane.add(fade_slider);
		
		fade_val = new JLabel("0", SwingConstants.CENTER);
		fade_val.setBounds(1117, 490, 80, 28);
		contentPane.add(fade_val);
		
		JLabel lblFade = new JLabel("Fade (ms)", SwingConstants.CENTER);
		lblFade.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblFade.setBounds(1117, 251, 80, 16);
		contentPane.add(lblFade);
		
		JLabel lblActivePreset = new JLabel("Active Preset:");
		lblActivePreset.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		lblActivePreset.setBounds(995, 251, 90, 16);
		contentPane.add(lblActivePreset);
		
		active_preset_lbl = new JLabel("-");
		active_preset_lbl.setBounds(995, 275, 90, 16);
		contentPane.add(active_preset_lbl);
		
		fixtureWizard a = new fixtureWizard();
			new_fixture.addActionListener(a);
			
		editFixture b = new editFixture();
			edit_fixture.addActionListener(b);
			
		Sync c = new Sync();
			remote_btn.addActionListener(this);
			
		assignGroup d = new assignGroup();
			group_btn.addActionListener(d);
		
		// Listeners
		next_cue.addActionListener(this);
		prev_cue.addActionListener(this);
		cue_slider.addChangeListener(this);
		cue_counter.addChangeListener(this);
		clear_sel.addActionListener(this);
		save_show.addActionListener(this);
		load_show.addActionListener(this);
		new_cue_stack.addActionListener(this);
		add_cue.addActionListener(this);
		cue_stack_selector.addActionListener(this);
		black_out.addActionListener(this);
		fixture_sel_and_ctrl.addChangeListener(this);
		screens.addChangeListener(this);
		store_cue_btn.addActionListener(this);
		patch_table.addMouseListener(this);
		master_slider.addChangeListener(this);
		master_spinner.addChangeListener(this);
		fade_slider.addChangeListener(this);
		execute_preset.addActionListener(this);
		assign_current_output.addActionListener(this);
		presets_grid.addMouseListener(this);
		execute_on_select.addChangeListener(this);
		
		initiate();
	}
	
	public static void updatePatchTable(){
		for(int a=0;a<512;a++){
			if(fixture[a] != null){
				for(int b=0;b<fixture[a].getChannels();b++){
					patch_data[fixture[a].getId()-1][1] = fixture[a].getName();
					patch_data[fixture[a].getId()-1][2] = fixture[a].getFixtureType();
					patch_data[fixture[a].getId()-1][3] = fixture[a].getStartChannel() + "-" + (fixture[a].getStartChannel()+fixture[a].getChannels()-1);
				}
			}
		}
	}
	// Range (used in profile creation)
	public static class Range extends Object implements Serializable{
		int low, high;
		String func;
		
		public Range(int low, int high, String function){
			this.low = low;
			this.high = high;
			func = function;
		}
		
		public boolean isInRange(int value){
			if((value <= high) && (value >= low)){
				return true;
			}
			return false;
		}
	}
	
	// Unselect a fixture
	public void unselectFixture(JToggleButton tb){
		tb.setSelected(false);
	}
	
	// Returns a profile given its string name
	public static Profile getProfileByName(String name){
		for(Profile p : profile){
			
			if(p != null && p.getName().equals(name)){
				return p;
			}
			
		}
		return null;
	}
	
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == next_cue){
				
				cue_slider.setValue(cue_slider.getValue()+1);
				cue_counter.setValue(cue_slider.getValue());
				
			} else if(e.getSource() == prev_cue){
				
				cue_slider.setValue(cue_slider.getValue()-1);
				cue_counter.setValue(cue_slider.getValue());
				
			} else if(e.getSource() == clear_sel){
				
				if(selectedFixture_btn != null){
					unselectFixture(selectedFixture_btn);
					selectedFixture = null;
					selectedFixture_btn = null;
					
					for(int d=0;d<512;d++){
						if(ctrl_fader[d] != null){
							ctrl_fader[d].setFaderVisible(false);
							ctrl_fader[d].unassign();
							ctrl_fader[d].slider.setValue(0);
						}
					}
					cur_sel_id.setText("ID--");
					cur_sel_name.setText("-");
					cur_sel_type.setText("-");
				}  
				
			} else if(e.getSource() == save_show){
				
				if(currently_loaded_show == null){
					
					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("show.truss"));
				
					if(fc.showSaveDialog(Truss.this) == JFileChooser.APPROVE_OPTION){
						currently_loaded_show = fc.getSelectedFile();
						saveShow.save(fc.getSelectedFile());
						setTitle("Truss Alpha 1.0 - " + currently_loaded_show.getName());
					}
					
				} else {
					saveShow.save(currently_loaded_show);
				}
				Thread saved = new Thread(){
					public void run(){
						String prev_str = error_disp.getText();
						Color prev_colour = error_disp.getForeground();
						
						error_disp.setForeground(Color.BLUE);
						error_disp.setText("Saved");
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {e.printStackTrace();}
						
						error_disp.setForeground(prev_colour);
						error_disp.setText(prev_str);
					}
				};
				saved.start();
				saved.yield();
				
				
			} else if(e.getSource() == load_show){
				
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(load_show_filter);

				if(fc.showOpenDialog(Truss.this) == JFileChooser.APPROVE_OPTION){
					currently_loaded_show = fc.getSelectedFile();
					saveShow.load(fc.getSelectedFile());
					setTitle("Truss Alpha 1.0 - " + currently_loaded_show.getName());
			//		patch_table.revalidate();
				}
				
			} else if(e.getSource() == new_cue_stack){
				
				cueStackNames.add(new_cue_stack_tf.getText());
				cueStack[cueStackCounter] = new cueStack();
				
			} else if(e.getSource() == cue_stack_selector){
				
				System.out.println("index:" + cue_stack_selector.getSelectedIndex());
				int amtCues = cueStack[cue_stack_selector.getSelectedIndex()].getAmtCues();
				cue_slider.setMajorTickSpacing(Math.round(amtCues/10));
				cue_slider.setMaximum(amtCues);
				number_of_cues_lbl.setText("No. of Cues: " + amtCues);
				
			} else if(e.getSource() == add_cue){
				
				cueStack[cue_stack_selector.getSelectedIndex()].addCue();
				int amtCues = cueStack[cue_stack_selector.getSelectedIndex()].getAmtCues();
				cue_slider.setMaximum(amtCues);
				number_of_cues_lbl.setText("No. of Cues: " + amtCues);
				
			} else if(e.getSource() == black_out){
				
				if(!blackout_on){
					System.out.println("Blackout on");
					blackout_on = true;
					/*
					 * Broadcast Blackout
					 */
					blackout_th = new Thread(){
						int count = 0;
						public void run(){
							while(true){
									if(count == 0){
										black_out.setForeground(Color.BLUE);
										black_out.setFont(new Font(null, Font.BOLD, 13));
										count++;
									} else {
										black_out.setForeground(Color.BLACK);
										count--;
									}
									try {
										Thread.sleep(500);
									} catch(Exception e){e.printStackTrace();}	
							}
						};
					};
					blackout_th.start();
				} else {
					System.out.println("Blackout off");
					blackout_on = false;
					/*
					 * Broadcast channel_data
					 */
					blackout_th.stop();
					black_out.setForeground(Color.BLACK);
					black_out.setFont(new Font(null, Font.PLAIN, 13));
				}
				
			} else if(e.getSource() == store_cue_btn){
				
				cueStack[cue_stack_selector.getSelectedIndex()].saveToCurrentCue(channel_data);
				
			} else if(e.getSource() == assign_current_output){
				
				int[] data = new int[512];
				
				for(int a=0;a<512;a++){
					data[a] = (channel_data[a+1] / 255) * master_slider.getValue();
				}
				preset[presets_grid.getSelectedColumn()*presets_grid.getSelectedRow()] = new Preset(presets_grid.getSelectedRow(), presets_grid.getSelectedColumn(), data);

				presets_grid.setValueAt("Preset", presets_grid.getSelectedRow(), presets_grid.getSelectedColumn());
				
			} else if(e.getSource() == execute_preset){
				
				if(preset[presets_grid.getSelectedColumn()*presets_grid.getSelectedRow()] != null){
					preset[presets_grid.getSelectedColumn()*presets_grid.getSelectedRow()].execute();
				}
				
			}
		}
		public void stateChanged(ChangeEvent e){
			if(e.getSource() == cue_slider){
				
				cue_counter.setValue(cue_slider.getValue());
				
			} else if(e.getSource() == cue_counter){
				
				cue_slider.setValue((int)cue_counter.getValue());
				
			} else if(e.getSource() == fixture_sel_and_ctrl){
				
				if(fixture_sel_and_ctrl.getSelectedIndex() == 1){
					for(int a=0;a<50;a++){
						if(ctrl_fader[a].f != null){
							ctrl_fader[a].revalidate();
						}
					}
				}
				
			} else if(e.getSource() == screens){
				
				if(screens.getSelectedIndex() == 1){
					for(int a=0;a<18;a++){
						if(ctrl_fader[a].f != null){
							fw_fader[a].revalidate();
						}
					}
				} else if(screens.getSelectedIndex() == 0){
					for(int a=0;a<50;a++){
						if(ctrl_fader[a].f != null){
							ctrl_fader[a].revalidate();
						}
					}
				}
				
			} else if(e.getSource() == master_spinner){
				
				master_slider.setValue((int)master_spinner.getValue());
				setMaster((int)master_spinner.getValue());
				
			} else if(e.getSource() == master_slider){
				
				master_spinner.setValue(master_slider.getValue());
				setMaster((int)master_spinner.getValue());
				
			} else if(e.getSource() == fade_slider){
				
				fade_val.setText(""+fade_slider.getValue());
				
			} else if(e.getSource() == execute_on_select){
				
				if(execute_on_select.isSelected()){
					execute_preset.setEnabled(false);
				} else {
					execute_preset.setEnabled(true);
				}
				
			}
		}
		
		public void setMaster(int val){
			int[] data = new int[512];
			
			for(int a=0;a<512;a++){
				data[a] = (channel_data[a+1] / 255) * val;
			}
			/*
			 * Broadcast here
			 */
		}

		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == patch_table){

				group_btn.setEnabled(true);
				return;
				
			} else if(e.getSource() == presets_grid){

				assign_current_output.setEnabled(true);
				if(execute_on_select.isSelected()){
					
					if(preset[presets_grid.getSelectedColumn()*presets_grid.getSelectedRow()] != null){
						preset[presets_grid.getSelectedColumn()*presets_grid.getSelectedRow()].execute();
					}
					
				}
				return;
				
			}
			
			for(int a=0;a<512;a++){
					if(e.getSource() == fixture_select_btn[a]){

						selectedFixture = fixture[a+1];
						if(selectedFixture_btn != null){
							unselectFixture(selectedFixture_btn);
						}  
						
						selectFixtures(new Fixture[]{fixture[a+1]}, true);

						cur_sel_id.setText("ID"+fixture[a+1].id);
						cur_sel_name.setText(fixture[a+1].getName());
						cur_sel_type.setText(fixture[a+1].getFixtureType());
						selectedFixture_btn = fixture_select_btn[a];  
						return;
					}
			}
			for(int a=0;a<512;a++){
				if(e.getSource() == group_select_btn[a]) {
					
					selectedFixture = group[a+1];
					if(selectedFixture_btn != null){
						unselectFixture(selectedFixture_btn);
					}  

					selectFixtures(group[a+1].getMembers(), false);

					cur_sel_id.setText("GRP");
					cur_sel_name.setText(group[a+1].getName());
					cur_sel_type.setText(group[a+1].getFixtureType());
					selectedFixture_btn = group_select_btn[a]; 
					return;
					
				}
			}
		}
		
		public void selectFixtures(Fixture[] f, boolean usingOverallChannels){
			
			for(int d=0;d<512;d++){
				if(ctrl_fader[d] != null){
					ctrl_fader[d].setFaderVisible(false);
					ctrl_fader[d].unassign();
					ctrl_fader[d].slider.setValue(0);
				}
			}
			
			Profile prof = getProfileByName(f[0].getFixtureType());

			for(int b=0;b<f[0].getChannels();b++){  
				ctrl_fader[b].setFaderVisible(true);
				ctrl_fader[b].assignFixture(f[0]);
				if(f.length > 1){
					ctrl_fader[b].slider.setValue(0);
				} else {
					ctrl_fader[b].slider.setValue(channel_data[f[0].getStartChannel()+b]);
				}
				if(usingOverallChannels){
					ctrl_fader[b].setChannel(b+1 + "/" + (f[0].getStartChannel()+b));
				} else {
					ctrl_fader[b].setChannel(b+1 + "/-");
				}
				
				if(f[0].isUsingProfile()){
					ctrl_fader[b].setName((String)((Vector)prof.function.get(b)).get(0)); 
					prof.setStringValue(ctrl_fader[b]);
				} else {
					ctrl_fader[b].setName("Channel " + (b+1));
				}
				
				
				int[] channels = new int[f.length];
				for(int a=0;a<channels.length;a++){
					if(f[a] != null){
						channels[a] = f[a].getStartChannel()+b;
					}
				}
				
				ctrl_fader[b].assignChannel(channels);
				lbl_nothingselected.setVisible(false);
			}
			
		}
		
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		@Override
		public void discoveredNewNode(ArtNetNode node) {
			if(artnet_node == null){
				artnet_node = node;
				System.out.println("node discovered");
			}
		}

		@Override
		public void discoveredNodeDisconnected(ArtNetNode node) {
			System.out.println("node disconnected: " + node);
			if(artnet_node == node){
				artnet_node = null;
			}
		}

		@Override
		public void discoveryCompleted(List<ArtNetNode> nodes) {
		//	System.out.println(nodes.size() + " nodes found");
			if(nodes.size() == 0){
				error_disp.setText("! No ArtNet Nodes Discovered");
			}
		}

		@Override
		public void discoveryFailed(Throwable t) {
			System.out.println("failed");
		}
}