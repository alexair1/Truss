package Truss;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;

public class main extends JFrame implements ActionListener, ChangeListener, MouseListener, ArtNetDiscoveryListener, KeyListener {
	
	static int[] channel_data = new int[513];
	int[] prev_channel_data = new int[513];
	byte[] blackout = new byte[512];
	byte[] current_output = new byte[512];
	static Preset[][] preset = new Preset[10][35];
	JSlider[] bank_fader;
	cueStack[] cueStack = new cueStack[100];
	static JToggleButton[] fixture_select_btn = new JToggleButton[512];
	static JToggleButton[] group_select_btn = new JToggleButton[512];
	static Object[][] fixture_data = new Object[6][7];
	static Object[][] group_data = new Object[512][3];
	static Object[][] dimmer_data = new Object[6][7];
	static Object[][] sequence_data = new Object[6][7];
	static Fader[] ctrl_fader = new Fader[512];
	static Fader[] fw_fader = new Fader[18];
	static Fixture[] fixture = new Fixture[513];
	static Dimmer[] dimmer = new  Dimmer[513];
	static Cue[] cue = new Cue[1000];
	static Profile[] profile = new Profile[100];
	static Group[] group = new Group[513];
	static byte[] data = new byte[512];
	static int selectedFixtures_amt = 0, current_cue = 1, ctrl_fader_counter = 0, cueStackCounter = 0, fixture_select_btn_counter = 0, fixtureNumber = 1, dimmerNumber = 1, profileID = -1, fader_wing_counter = 0, group_counter = 1, sequenceID = 0;
	static JLabel lbl_nothingpatched;
	static JPanel fixture_select, control, fixture_sel_panel, group_sel_panel;
	static Fixture[] selectedFixtures = new Fixture[512];
	static File currently_loaded_show;
	static Vector<String> groupNames = new Vector<String>();
	static Object selectedFixture = null;
//	static Vector<Preset> preset = new Vector<Preset>();
	
	static JButton bank_page_up, bank_page_down, slct_fix, slct_dim, slct_seq, cue_Go, cue_next, cue_prev, cue_store, cue_ok, open_fw, new_dimmer, stop_cue, execute_preset, assign_current_output, group_btn, store_cue_btn, page1_btn, page2_btn, page3_btn, page4_btn, remote_btn, add_cue, black_out, new_cue_stack, load_show, next_cue, prev_cue, new_fixture, edit_fixture, clear_sel, save_show;
	JSlider cue_slider, master_slider, fade_slider, intensity_fader;
	JTextField bank_fader1_spinner, bank_fader2_spinner, textField, new_cue_stack_tf, preset_name, cue_name_tf;
	JPanel patch_and_control, contentPane, fw, presets;
	JTable fixture_table, presets_grid, group_table, dimmer_table, sequence_table;
	JComboBox cue_stack_selector;
	JScrollPane patch_table_pane, group_table_pane;
	JTabbedPane fixture_sel_and_ctrl, screens;
	JCheckBox execute_on_select, bypass_go_chk;
	static JLabel bank_page_lbl, no_assign_lbl, cur_sel_id, cur_sel_name, cur_sel_type, lbl_nothingselected, error_disp, number_of_cues_lbl, fade_val, active_preset_lbl, intensity_fader_lbl, current_cue_lbl;
	JSpinner cue_counter, master_spinner, intensity_spinner;
	static Fader single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt;
//	static JSpinner fw_page_spinner;
//	JRadioButton radio_group, radio_fixture;
	static ArtNetNode artnet_node;
	static ArtNet artnet = new ArtNet();
	static ArtDmxPacket dmx = new ArtDmxPacket();
	
	int channel_amt = 0;

	Thread clear_flash, blackout_th;
	JToggleButton selectedFixture_btn;
	FileOutputStream file_stream_out;
	Properties show_settings = new Properties();
	FileNameExtensionFilter load_show_filter;
	Vector cueStackNames = new Vector();
	static boolean blackout_on = false;
	boolean artnet_con = false, on_preset_screen = false, dimmerControl = false;
	static JButton btnFocus;
	static JButton btnDimmer;
	static JButton btnIris;
	static JButton btnShutter;
	static JButton btnZoom;
	static JButton btnColourWheel;
	static JButton btnRgbMixing;
	static JButton btnCto;
	static JButton btnGobo_1;
	static JButton btnGobo_2;
	static JButton btnGobo_3;
	static JButton btnPrism;
	static JButton btnFrost;
	static JButton btnControl;
	static JButton btnOther;
	
	JMenuItem saveItem, loadItem, fixtureItem, dimmerItem, aboutItem, patch_newFixture, patch_newDimmer, patch_newSequence;
	
	// Vars for setting patch_table cell background
	Color cell_bg = Color.black;
	int cell_row = 2, cell_col = 3;
	
	// XML Parser variables
//	String profile_name, profile_mode;
//	Vector<Vector<Object>> profile_channels = new Vector<Vector<Object>>();
//	boolean profile_built_in_dimmer;
//	int[] profile_channel_function = new int[51];
//	Vector<Object> profile_channel;
	
	SAXParserFactory factory = SAXParserFactory.newInstance();
	DefaultHandler handler;

	public void initiate() {
		
		for(int a=0;a<999;a++){
			cue[a+1] = new Cue(null, "");
		}
//		for(int b=0;b<512;b++){
//			dimmer_data[b][0] = b+1;
//		}
		new Thread(){
			public void run(){
				while(true){
					no_assign_lbl.setText("");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {e.printStackTrace();}
					no_assign_lbl.setText("No Assign");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}.start();
		
		Thread artnet_discovery = new Thread(){
			public void run(){
				error_disp.setForeground(Color.BLACK);
		        try {
		            artnet.start();
		            artnet.setBroadCastAddress("255.255.255.255");
		            artnet.getNodeDiscovery().addListener(main.this);
		            artnet.startNodeDiscovery();
		            error_disp.setText("Polling");
		            
		        } catch(BindException be){
		        	
		        	error_disp.setForeground(Color.RED);
		        	error_disp.setText("! Address already in use.");
		        	
				} catch (Exception e) {
		            e.printStackTrace();
		        } 
			};
		};
		artnet_discovery.start();  
		
//		Loader.loading_text.setText("Initialising");
		boolean b = true;
/*		int objects_amt = 0;
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
		}    */
		
		final String[] channels = {"Dimmer", "Shutter", "Iris", "Focus", "Zoom", "Pan", "Tilt", "Colour Wheel", 
								   "Colour Wheel (Fine)", "Red", "Red (Fine)", "Green", "Green (Fine)", "Blue", "Blue (Fine)",
								   "Cyan", "Cyan (Fine)", "Magenta", "Magenta (Fine)", "Yellow", "Yellow (Fine)", "CTO",
								   "CTO (Fine)"};
		
		try {
			
			handler = new DefaultHandler(){
				
				String profile_name, profile_mode;
				Vector<Vector<Object>> profile_channels = new Vector<Vector<Object>>();
				boolean profile_built_in_dimmer;
				int[] profile_channel_function = new int[51];
				Vector<Object> profile_channel;
					
				public void startElement(String uri, String localname, String name, Attributes attributes) throws SAXException {
					
					if(name == "fixture"){
						profileID++;
						profile_name = attributes.getValue(0);
						profile_mode = attributes.getValue(1);
				//		profile_channels = new Vector<Vector<Object>>();
				//		profile_channel_function = new int[51];
					} else {
						
						if(name == "channel"){
							
							profile_channel = new Vector<Object>();
							profile_channel.addElement(attributes.getValue(0));
							profile_channel.addElement(attributes.getValue(1));
				//			profile_channel.addElement(Boolean.parseBoolean(attributes.getValue(2)));
							
							channel_amt++;
							if(Arrays.asList(channels).indexOf(attributes.getValue(1)) != -1){
								profile_channel_function[Arrays.asList(channels).indexOf(attributes.getValue(1))] = channel_amt;
							}
							
						} else {
							profile_channel.addElement(new Range(Integer.parseInt(attributes.getValue(0)), Integer.parseInt(attributes.getValue(1)), attributes.getValue(2)));
						}
						
					} 
					
				}		
				public void endElement(String uri, String localname, String name) throws SAXException {
					
					if(name == "fixture"){
						profile[profileID] = new Profile(profile_name, profile_mode, profile_channels, profile_built_in_dimmer, profile_channel_function);
					} else if(name == "channel"){
						profile_channels.add(profile_channel);
				//		System.out.println(profile_channel);
					}
					
				}
			};
			loadProfile("test.xml");
		//	loadProfile("dimmer.xml");
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
//		Vector<Object>[] vector = new Vector[100];
//		b = true;
//		int c = -1, d = -1;
//		String profile_name="";
//		try {
//			boolean built_in_dimmer = false;
//	//		ObjectInputStream obj_in = new ObjectInputStream(getClass().getResourceAsStream("profiles.txt"));
//			ObjectInputStream obj_in = new ObjectInputStream(new FileInputStream("profiles.txt"));
//				while(b == true){
//					try{
//						Object obj = obj_in.readObject();
//						if(obj instanceof String){
//							if(((String) obj).charAt(0) == '*'){
//								if(d != -1 || obj == "*"){
//						//		profile[d] = new Profile(profile_name, vector[d], built_in_dimmer);
//							//		System.out.println(Boolean.parseBoolean(((String)obj).split("_")[0].substring(1)));
//							//		System.out.println(profile_name);
//									c = -1;
//								}
//								d++;
//								vector[d] = new Vector();
//								profile_name = ((String) obj).split("_")[1];
//								built_in_dimmer = Boolean.parseBoolean(((String)obj).split("_")[0].substring(1));
//							} else {
//								c++;
//								vector[d].add(new Vector());
//								((Vector<Object>) vector[d].get(c)).add(obj);
//							}
//						} else {
//							((Vector<Object>) vector[d].get(c)).add(obj);
//						}
//				//		Loader.loader.setValue(Loader.loader.getValue() + 100/objects_amt);
//					} catch(Exception e){
//				//		e.printStackTrace();
//						b = false;
//					}
//				}
//				obj_in.close();
//		//		Loader.loaded = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
		
		load_show_filter = new FileNameExtensionFilter("Truss Show File", "truss");
	}  

	public main() {
		
//		try {
//			SynthLookAndFeel laf = new SynthLookAndFeel();
//		//	laf.load(getClass().getResourceAsStream("/src/look_and_feel.xml"), getClass());
//			laf.load(new FileInputStream("src/look_and_feel.xml"), main.class);
//			UIManager.setLookAndFeel(laf);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}  
		
//		try 
//	    {
//	      UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
//	    } 
//	    catch (Exception e) 
//	    {
//	      e.printStackTrace();
//	    }

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1290, 680);
		setMinimumSize(new Dimension(1280, 710));
		patch_and_control = new JPanel();
		patch_and_control.setBounds(6, 0, 974, 660);
		patch_and_control.setLayout(null);
//		patch_and_control.setBackground(new Color(238, 238, 238));
	//	patch_and_control.setBackground(new Color(130, 130, 130));
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
			saveItem = new JMenuItem("Save");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			fileMenu.add(saveItem);
			
			loadItem = new JMenuItem("Load");
			loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			fileMenu.add(loadItem);
		
//		JMenu patchMenu = new JMenu("Patch");
//		menuBar.add(patchMenu);
//			
//			fixtureItem = new JMenuItem("New Fixture");
//			fixtureItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
//			patchMenu.add(fixtureItem);
//				
//			dimmerItem = new JMenuItem("New Dimmer");
//			dimmerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
//			patchMenu.add(dimmerItem);
			
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		
			aboutItem = new JMenuItem("About");
			aboutMenu.add(aboutItem);
			
		setJMenuBar(menuBar);
		
//		cue = new JPanel();
//		cue.setLayout(null);
//		cue.setBackground(new Color(230, 230, 230));
		
		fw = new JPanel();
//		fw.setBackground(new Color(230, 230, 230));
		
		presets = new JPanel();
	//	presets.setBackground(new Color(230, 230, 230));
		
		contentPane = new JPanel();
	//	contentPane.setBackground(new Color(130, 130, 130));
		contentPane.setLayout(null);
		contentPane.add(patch_and_control);
	//	contentPane.setBackground(new Color(0,0,0));
		
		setContentPane(contentPane);
//		contentPane.setBackground(new Color(238, 238, 238));
		setTitle("Truss, Alpha 1.0");
		setResizable(false);
		
//		for(int a=0;a<512;a++){
//			patch_data[a][0] = a+1;
//		}
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};  
		
		// Main Screen Selector
//		screens = new JTabbedPane(JTabbedPane.TOP);
//		screens.setBounds(6, 13, 977, 675);
//		contentPane.add(screens);
//		screens.addTab("Patch and Control", patch_and_control);
//	//	screens.addTab("Cue", cue);
//		screens.addTab("Fader Wing", fw);
//		screens.addTab("Presets", presets);
//		presets.setLayout(null);
		
		JPanel menu_panel = new JPanel();
//		menu_panel.setBackground(new Color(100, 100, 100));
		menu_panel.setBounds(0, 0, 1284, 45);
		menu_panel.setLayout(null);
	//	contentPane.add(menu_panel);
		
		JPanel main_controls_panel = new JPanel();
		main_controls_panel.setBounds(980, 0, 294, 660);
	//	main_controls_panel.setBackground(new Color(120, 120, 120));
		menu_panel.setLayout(null);
		contentPane.add(main_controls_panel);
		
		// Patch and Control Screen
		
		slct_seq = new JButton("Sequences");
		slct_seq.setEnabled(false);
		slct_seq.setBounds(885, 311, 89, 23);
		patch_and_control.add(slct_seq);
		
		slct_fix = new JButton("Fixtures");
		slct_fix.setBounds(786, 311, 89, 23);
		slct_fix.setForeground(Color.BLUE);
		patch_and_control.add(slct_fix);
		
		slct_dim = new JButton("Dimmers");
		slct_dim.setBounds(687, 311, 89, 23);
		patch_and_control.add(slct_dim);
		
		JPopupMenu fixture_menu = new JPopupMenu();		
		patch_newFixture = new JMenuItem("New Fixture");
		fixture_menu.add(patch_newFixture);
		
		JPopupMenu dimmer_menu = new JPopupMenu();		
		patch_newDimmer = new JMenuItem("New Dimmer");
		dimmer_menu.add(patch_newDimmer);
		
		JPopupMenu sequence_menu = new JPopupMenu();		
		patch_newSequence = new JMenuItem("New Sequence");
		sequence_menu.add(patch_newSequence);

		fixture_table = new JTable(fixture_data, new Object[] {"","","","","","",""}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			}
		};
		fixture_table.setCellSelectionEnabled(true);
		fixture_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fixture_table.setRowHeight(50);
		fixture_table.setComponentPopupMenu(fixture_menu);
		fixture_table.setTableHeader(null);
		fixture_table.setSelectionBackground(Color.LIGHT_GRAY);
		
		dimmer_table = new JTable(dimmer_data, new Object[] {"","","","","","",""}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		dimmer_table.setCellSelectionEnabled(true);
		dimmer_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dimmer_table.setRowHeight(50);
		dimmer_table.setComponentPopupMenu(dimmer_menu);
		dimmer_table.setTableHeader(null);
		dimmer_table.setSelectionBackground(Color.LIGHT_GRAY);
		
		sequence_table = new JTable(sequence_data, new Object[] {"","","","","","",""}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			}
		};
		sequence_table.setCellSelectionEnabled(true);
		sequence_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sequence_table.setRowHeight(50);
		sequence_table.setComponentPopupMenu(sequence_menu);
		sequence_table.setTableHeader(null);
		sequence_table.setSelectionBackground(Color.LIGHT_GRAY);
		
//		group_table = new JTable(group_data, new Object[] {"Name", "Fixture Type", "Size"}){
//			public boolean isCellEditable(int row, int column) {                
//                return false;               
//			};
//		};
//		group_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		group_table.setBounds(2, 18, 450, 8208);
//		group_table.getColumnModel().getColumn(2).setMaxWidth(80);
		
		patch_table_pane = new JScrollPane(fixture_table);
		patch_table_pane.setColumnHeaderView(null);
		patch_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		patch_table_pane.setBounds(0, 0, 975, 300);
		patch_and_control.add(patch_table_pane);
		
//		group_table_pane = new JScrollPane(group_table);
//		group_table_pane.setBounds(433, 13, 408, 286);
//		group_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		patch_and_control.add(group_table_pane);
		
//		new_fixture = new JButton();
//		new_fixture.setBounds(10, 10, 25, 25);
//		new_fixture.setIcon(new ImageIcon("src/img/fixture.png"));
//		new_fixture.setToolTipText("Fixture Schedule");
//		new_fixture.setBorder(new EmptyBorder(0,0,0,0));
//		new_fixture.setContentAreaFilled(false);
//		new_fixture.setFocusPainted(false);
//		menu_panel.add(new_fixture);
//		
//		new_dimmer = new JButton();
//		new_dimmer.setBounds(45, 10, 25, 25);
//		new_dimmer.setIcon(new ImageIcon("src/img/dimmer.png"));
//		new_dimmer.setBorder(new EmptyBorder(0,0,0,0));
//		new_dimmer.setContentAreaFilled(false);
//		new_dimmer.setFocusPainted(false);
//		menu_panel.add(new_dimmer);
		
		JButton open_console = new JButton();
		open_console.setBounds(80, 10, 25, 25);
		open_console.setIcon(new ImageIcon("src/img/console.png"));
		open_console.setBorder(new EmptyBorder(0,0,0,0));
		open_console.setContentAreaFilled(false);
		open_console.setFocusPainted(false);
		menu_panel.add(open_console);
		
//		edit_fixture = new JButton("Edit");
//		edit_fixture.setEnabled(false);
//		edit_fixture.setBounds(875, 102, 75, 29);
//		patch_and_control.add(edit_fixture);
		
//		group_btn = new JButton("Group");
//		group_btn.setEnabled(false);
//		group_btn.setBounds(875, 129, 75, 29);
//		patch_and_control.add(group_btn);
		
//		radio_fixture = new JRadioButton("Fixture");
//		radio_fixture.setBounds(869, 243, 81, 23);
//		radio_fixture.setSelected(true);
//		patch_and_control.add(radio_fixture);
//		
//		radio_group = new JRadioButton("Group");
//		radio_group.setBounds(869, 267, 81, 23);
//		patch_and_control.add(radio_group);
//		
//		ButtonGroup sel_radios = new ButtonGroup();
//		sel_radios.add(radio_fixture);
//		sel_radios.add(radio_group);
		
/*			// Fixture Select and Control
			
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
			fixture_select.add(group_sp);    */

			
//			control = new JPanel();
//			control.setBackground(new Color(238, 238, 238));
//			
//			int x=0;
//			for(int a=0;a<50;a++){
//				ctrl_fader[a] = new Fader();
//				ctrl_fader[a].create(ctrl_fader_counter, control, new Color(238, 238, 238));
//				ctrl_fader[a].setChannel("1/"+(a+1));
//				ctrl_fader[a].setFaderVisible(false);
//				ctrl_fader_counter++;
//			}
			
//			JScrollPane sp = new JScrollPane(control);
//			sp.setBounds(7, 300, 960, 323);
//			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//			sp.setBorder(null);
//			patch_and_control.add(sp);
			
		
			// Fixture Control
		
			Color bg = new Color(238, 238, 238);
		
			JPanel fixture_control = new JPanel();
			fixture_control.setBackground(bg);
			fixture_control.setLayout(null);
	//		fixture_control.setBackground(new Color(130, 130, 130));
			fixture_control.setBounds(10, 345, 964, 315);
			patch_and_control.add(fixture_control);
			
//			ctrl_fader[b].slider.setBackground(dimmer[b+1].c);
//			ctrl_fader[b].setChannel("1/" + (b+1));
//			ctrl_fader[b].assignFixture(dimmer[b+1]);
//			ctrl_fader[b].slider.setValue(channel_data[dimmer[b+1].getStartChannel()]);
//			ctrl_fader[b].setName(dimmer[b+1].name);
//			ctrl_fader[b].assignChannel(new int[]{dimmer[b+1].startChannel});
//			ctrl_fader[b].prev_val = 0;
			
			single = new Fader();
			single.create(0, fixture_control, bg, 0, 0);
			
			bank_1 = new Fader();
			bank_1.create(0, fixture_control, bg, 170, 0);
			
			bank_2 = new Fader();
			bank_2.create(0, fixture_control, bg, 260, 0);
			
			bank_3 = new Fader();
			bank_3.create(0, fixture_control, bg, 350, 0);
			
			bank_4 = new Fader();
			bank_4.create(0, fixture_control, bg, 440, 0);
			
			bank_5 = new Fader();
			bank_5.create(0, fixture_control, bg, 530, 0);
			
			pan = new Fader();
			pan.create(0, fixture_control, bg, 620, 0);
			pan.setName("Pan");
			
			tilt = new Fader();
			tilt.create(0, fixture_control, bg, 710, 0);
			tilt.setName("Tilt");
			
			btnDimmer = new JButton("Dimmer");
			btnDimmer.setBounds(90, 0, 75, 39);
			fixture_control.add(btnDimmer);
			
			btnFocus = new JButton("Focus");
			btnFocus.setBounds(90, 50, 75, 39);
			fixture_control.add(btnFocus);
			
			btnIris = new JButton("Iris");
			btnIris.setBounds(90, 100, 75, 39);
			fixture_control.add(btnIris);
			
			btnShutter = new JButton("Shutter");
			btnShutter.setBounds(90, 150, 75, 39);
			fixture_control.add(btnShutter);
			
			btnZoom = new JButton("Zoom");
			btnZoom.setBounds(90, 200, 75, 39);
			fixture_control.add(btnZoom);
			
			btnColourWheel = new JButton("<html>Colour<br/>Wheel</html>");
			btnColourWheel.setBounds(800, 0, 85, 39);
			fixture_control.add(btnColourWheel);
			
			btnRgbMixing = new JButton("RGB Mixing");
			btnRgbMixing.setBounds(800, 50, 85, 39);
			fixture_control.add(btnRgbMixing);
			
			btnCto = new JButton("CTO");
			btnCto.setBounds(800, 100, 85, 39);
			fixture_control.add(btnCto);
			
			btnGobo_1 = new JButton("Gobo 1");
			btnGobo_1.setBounds(800, 150, 85, 39);
			fixture_control.add(btnGobo_1);
			
			btnGobo_2 = new JButton("Gobo 2");
			btnGobo_2.setBounds(800, 200, 85, 39);
			fixture_control.add(btnGobo_2);
			
			btnGobo_3 = new JButton("Gobo 3");
			btnGobo_3.setBounds(800, 250, 85, 39);
			fixture_control.add(btnGobo_3);
			
			btnPrism = new JButton("Prism");
			btnPrism.setBounds(895, 0, 67, 39);
			fixture_control.add(btnPrism);
			
			btnFrost = new JButton("Frost");
			btnFrost.setBounds(895, 50, 67, 39);
			fixture_control.add(btnFrost);
			
			btnControl = new JButton("Control");
			btnControl.setBounds(895, 100, 67, 39);
			fixture_control.add(btnControl);
			
			btnOther = new JButton("Other");
			btnOther.setBounds(895, 150, 67, 39);
			fixture_control.add(btnOther);	
			
		// Cue Screen
			
//		next_cue = new JButton("Next");
//		next_cue.setBounds(875, 557, 75, 50);
//		cue.add(next_cue);
//			
//		prev_cue = new JButton("Prev");
//		prev_cue.setBounds(788, 557, 75, 50);
//		cue.add(prev_cue);
//			
//		cue_counter = new JSpinner();
//		cue_counter.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
//		cue_counter.setBounds(686, 560, 90, 38);
//		cue.add(cue_counter);
//			
//		cue_slider = new JSlider(0, 0, 0);
//		cue_slider.setSnapToTicks(true);
//		cue_slider.setPaintLabels(true);
//		cue_slider.setMajorTickSpacing(10);
//		cue_slider.setBounds(6, 508, 944, 37);
//		cue.add(cue_slider);
//			
//		cue_stack_selector = new JComboBox(cueStackNames);
//		cue_stack_selector.setEditable(false);
//		cue_stack_selector.setBounds(6, 18, 130, 27);
//		cue.add(cue_stack_selector);
//			
//		new_cue_stack = new JButton("+ Cue Stack");
//		new_cue_stack.setBounds(788, 85, 162, 29);
//		cue.add(new_cue_stack);
//		
//		JSeparator separator = new JSeparator();
//		separator.setOrientation(SwingConstants.VERTICAL);
//		separator.setBounds(775, 18, 12, 478);
//		cue.add(separator);
//		
//		new_cue_stack_tf = new JTextField();
//		new_cue_stack_tf.setBounds(788, 50, 162, 28);
//		cue.add(new_cue_stack_tf);
//		
//		JLabel lblNewCueName = new JLabel("Name");
//		lblNewCueName.setBounds(788, 22, 36, 16);
//		cue.add(lblNewCueName);
//		
//		JSeparator separator_1 = new JSeparator();
//		separator_1.setBounds(788, 126, 162, 12);
//		cue.add(separator_1);
//		
//		JLabel lblCurrentCueStack = new JLabel("Current Cue Stack");
//		lblCurrentCueStack.setFont(new Font("Lucida Grande", Font.BOLD, 13));
//		lblCurrentCueStack.setBounds(829, 144, 121, 16);
//		cue.add(lblCurrentCueStack);
//		
//		add_cue = new JButton("+ Cue");
//		add_cue.setBounds(875, 200, 75, 29);
//		cue.add(add_cue);
//		
//		number_of_cues_lbl = new JLabel("No. of Cues:");
//		number_of_cues_lbl.setBounds(788, 172, 162, 16);
//		cue.add(number_of_cues_lbl);
//		
//		store_cue_btn = new JButton("Store");
//		store_cue_btn.setBounds(875, 230, 75, 29);
//		cue.add(store_cue_btn);

		// Fader Wing
//		for(int z=0;z<18;z++){
//			fw_fader[z] = new Fader();
//			fw_fader[z].create(z, fw, new Color(230, 230, 230));
//			fw_fader[z].setChannel("-");
//			fw_fader[z].setName("-");
//		}
		
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
		execute_preset.setBounds(703, 594, 93, 29);
		presets.add(execute_preset);
		
		execute_on_select = new JCheckBox("Execute on Select");
		execute_on_select.setBounds(808, 595, 142, 23);
		presets.add(execute_on_select);
		
		preset_name = new JTextField();
		preset_name.setBounds(250, 593, 186, 28);
		presets.add(preset_name);
		preset_name.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(204, 599, 40, 16);
		presets.add(lblName);

		// contentPane content
//		save_show = new JButton();
//		save_show.setBounds(156, 638, 23, 23);
//		save_show.setBorder(BorderFactory.createEmptyBorder());
//		save_show.setIcon(new ImageIcon("src/img/save.png"));
//		save_show.setContentAreaFilled(false);
//		save_show.setFocusPainted(false);
//		contentPane.add(save_show);
//		
//		load_show = new JButton();
//		load_show.setBorder(BorderFactory.createEmptyBorder());
//		load_show.setIcon(new ImageIcon("src/img/load.png"));
//		load_show.setBounds(179, 638, 23, 23);
//		load_show.setContentAreaFilled(false);
//		load_show.setFocusPainted(false);
//		contentPane.add(load_show);
		main_controls_panel.setLayout(null);
		
//		JButton test_button = new JButton();
//		test_button.setIcon(new ImageIcon("src/img/add.png"));
//		test_button.setBorder(new EmptyBorder(0,0,0,0));
//		test_button.setContentAreaFilled(false);
//		test_button.setFocusPainted(false);
//		test_button.setBounds(10, 10, 25, 25);
//		contentPane.add(test_button);
		
		clear_sel = new JButton("Clear");
		clear_sel.setBounds(229, 35, 65, 23);
		clear_sel.setEnabled(false);
		main_controls_panel.add(clear_sel);
		
//		JLabel cur_sel_title = new JLabel("Current Selection");
//		cur_sel_title.setFont(new Font("Lucida Grande", Font.BOLD, 13));
//		cur_sel_title.setBounds(993, 53, 116, 16);
//		contentPane.add(cur_sel_title);
		
		cur_sel_id = new JLabel("ID--");
		cur_sel_id.setBounds(10, 10, 30, 14);
		main_controls_panel.add(cur_sel_id);
		
		cur_sel_name = new JLabel("-");
		cur_sel_name.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		cur_sel_name.setBounds(50, 7, 130, 18);
		main_controls_panel.add(cur_sel_name);
		
		cur_sel_type = new JLabel("-");
		cur_sel_type.setBounds(190, 10, 105, 14);
		main_controls_panel.add(cur_sel_type);
		
//		JSeparator separator_2 = new JSeparator();
//		separator_2.setBounds(995, 147, 279, 12);
//		contentPane.add(separator_2);
		
//		JSeparator separator_3 = new JSeparator();
//		separator_3.setBounds(995, 660, 279, 12);
//		contentPane.add(separator_3);
		
		master_slider = new JSlider(0, 255, 0);
		master_slider.addMouseListener(this);
		master_slider.setMinorTickSpacing(15);
		master_slider.setPaintTicks(true);
	//	master_slider.setBackground(new Color(238, 238, 238));
		master_slider.setOrientation(SwingConstants.VERTICAL);
		master_slider.setBounds(229, 360, 65, 224);
		main_controls_panel.add(master_slider);
		
		master_spinner = new JSpinner();
		master_spinner.setBounds(229, 595, 65, 28);
		main_controls_panel.add(master_spinner);
		
		JLabel lblMaster = new JLabel("Master", SwingConstants.CENTER);
		lblMaster.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblMaster.setBounds(229, 333, 65, 16);
		main_controls_panel.add(lblMaster);
		
		fade_slider = new JSlider(0, 10000, 0);
		fade_slider.setMinorTickSpacing(100);
	//	fade_slider.setBackground(new Color(130, 130, 130));
		fade_slider.setSnapToTicks(true);
		fade_slider.setOrientation(SwingConstants.VERTICAL);
		fade_slider.setBounds(139, 360, 80, 224);
		main_controls_panel.add(fade_slider);
		
//		intensity_fader_lbl = new JLabel("-", SwingConstants.CENTER);
//		intensity_fader_lbl.setFont(new Font("Lucida Grande", Font.BOLD, 13));
//		intensity_fader_lbl.setBounds(993, 343, 65, 16);
//		contentPane.add(intensity_fader_lbl);
//		
//		intensity_fader = new JSlider(0, 255, 0);
//		intensity_fader.setMinorTickSpacing(15);
//		intensity_fader.setBackground(new Color(238, 238, 238));
//		intensity_fader.setPaintTicks(true);
//		intensity_fader.setOrientation(SwingConstants.VERTICAL);
//		intensity_fader.setBounds(993, 370, 65, 224);
//		contentPane.add(intensity_fader);
//		
//		intensity_spinner = new JSpinner();
//		intensity_spinner.setBounds(993, 605, 65, 28);
//		contentPane.add(intensity_spinner);
//		
//		intensity_fader.setEnabled(false);
//		intensity_spinner.setEnabled(false);
//		intensity_fader_lbl.setEnabled(false);
		
		fade_val = new JLabel("0", SwingConstants.CENTER);
		fade_val.setBounds(139, 595, 80, 28);
		main_controls_panel.add(fade_val);
		
		JLabel lblFade = new JLabel("Fade (ms)", SwingConstants.CENTER);
		lblFade.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblFade.setBounds(139, 333, 80, 16);
		main_controls_panel.add(lblFade);
		
//		active_preset_lbl = new JLabel("No Preset");
//		active_preset_lbl.setBounds(1158, 276, 116, 16);
//		contentPane.add(active_preset_lbl);
		
//		stop_cue = new JButton("Stop Cue");
//		stop_cue.setEnabled(false);
//		stop_cue.setBounds(1157, 319, 117, 29);
//		contentPane.add(stop_cue);
		
//		JLabel lblFaderWingPage = new JLabel("Fader Wing Page");
//		lblFaderWingPage.setBounds(993, 276, 104, 16);
//		contentPane.add(lblFaderWingPage);
		
//		fw_page_spinner = new JSpinner();
//		fw_page_spinner.setBounds(993, 303, 80, 28);
//		fw_page_spinner.setValue(1);
//		contentPane.add(fw_page_spinner);
		
//		JSeparator separator_4 = new JSeparator();
//		separator_4.setOrientation(SwingConstants.VERTICAL);
//		separator_4.setBounds(1083, 372, 12, 269);
//		contentPane.add(separator_4);
		
		open_fw = new JButton("Dimmer Control");
		open_fw.setBounds(1025, 320, 116, 28);
		menu_panel.add(open_fw);
		
		// Cue components
		
		JPanel cue_panel = new JPanel();
		cue_panel.setBounds(10, 69, 281, 149);
	//	cue_panel.setBackground(new Color(120, 120, 120));
		main_controls_panel.add(cue_panel);
		cue_panel.setLayout(null);
		
		cue_Go = new JButton("GO");
		cue_Go.setFont(new Font("Tahoma", Font.BOLD, 12));
		cue_Go.setBounds(10, 102, 89, 36);
		cue_panel.add(cue_Go);
		
		current_cue_lbl = new JLabel("1", SwingConstants.CENTER);
		current_cue_lbl.setForeground(Color.RED);
		current_cue_lbl.setFont(new Font("Tahoma", Font.PLAIN, 50));
		current_cue_lbl.setBounds(10, 10, 89, 81);
		cue_panel.add(current_cue_lbl);
		
		cue_next = new JButton("+ Cue");
		cue_next.setBounds(109, 10, 63, 36);
		cue_panel.add(cue_next);
		
		cue_prev = new JButton("- Cue");
		cue_prev.setBounds(109, 52, 63, 36);
		cue_panel.add(cue_prev);
		
		bypass_go_chk = new JCheckBox("Bypass Go");
		bypass_go_chk.setBounds(178, 10, 97, 23);
		cue_panel.add(bypass_go_chk);
		
		cue_store = new JButton("Store");
		cue_store.setBounds(182, 52, 89, 36);
		cue_panel.add(cue_store);
		
		cue_name_tf = new JTextField();
		cue_name_tf.setBounds(109, 110, 120, 23);
		cue_panel.add(cue_name_tf);
		cue_name_tf.setColumns(10);
		
		cue_ok = new JButton("OK");
		cue_ok.setBounds(229, 109, 46, 25);
		cue_panel.add(cue_ok);
		
		no_assign_lbl = new JLabel("No Assign");
		no_assign_lbl.setBounds(182, 32, 93, 14);
		cue_panel.add(no_assign_lbl);
		
//			lbl_nothingselected = new JLabel("No fixtures selected.", SwingConstants.CENTER);
//			lbl_nothingselected.setBounds(6, 50, 917, 16);
		//	control.add(lbl_nothingselected);
			
			black_out = new JButton("B.O");
			black_out.setBounds(229, 229, 65, 32);
			main_controls_panel.add(black_out);
			
			error_disp = new JLabel("", SwingConstants.RIGHT);
			error_disp.setBounds(10, 634, 284, 15);
			main_controls_panel.add(error_disp);
			
			error_disp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			error_disp.setForeground(Color.RED);
			
			remote_btn = new JButton("Sync");
			remote_btn.setBounds(10, 229, 65, 23);
			main_controls_panel.add(remote_btn);
			remote_btn.setVisible(false);
			remote_btn.setEnabled(false);
			//	new_cue_stack.addActionListener(this);
			//	add_cue.addActionListener(this);
			//	cue_stack_selector.addActionListener(this);
				black_out.addActionListener(this);
		menu_panel.setLayout(null);
		
		bank_page_up = new JButton("Page +");
		bank_page_up.setBounds(895, 200, 67, 23);
		fixture_control.add(bank_page_up);
		
		bank_page_down = new JButton("Page -");
		bank_page_down.setBounds(895, 266, 67, 23);
		fixture_control.add(bank_page_down);
		
		bank_page_lbl = new JLabel("1", SwingConstants.CENTER);
		bank_page_lbl.setFont(new Font("Tahoma", Font.PLAIN, 28));
		bank_page_lbl.setBounds(895, 234, 67, 21);
		fixture_control.add(bank_page_lbl);
		
//		JButton btnAbout = new JButton("About");
//		btnAbout.setBounds(6, 638, 65, 23);
//		contentPane.add(btnAbout);
		
//		JButton btnSettings = new JButton("Settings");
//		btnSettings.setEnabled(false);
//		btnSettings.setBounds(81, 681, 90, 23);
//		contentPane.add(btnSettings);
		
		fixtureWizard a = new fixtureWizard();
			patch_newFixture.addActionListener(a);
			
		dimmerWizard b = new dimmerWizard();
			patch_newDimmer.addActionListener(b);
			
//		editFixture c = new editFixture();
//			edit_fixture.addActionListener(c);
			
		Console d = new Console();
			open_console.addActionListener(d);
			
//		remoteSettings d = new remoteSettings();
	//		remote_btn.addActionListener(d);
			
//		assignGroup e = new assignGroup();
//			group_btn.addActionListener(e);
			
		About f = new About();
			aboutItem.addActionListener(f);
		
		// Listeners
//		next_cue.addActionListener(this);
//		prev_cue.addActionListener(this);
//		cue_slider.addChangeListener(this);
//		cue_counter.addChangeListener(this);
		clear_sel.addActionListener(this);
//		save_show.addActionListener(this);
//		load_show.addActionListener(this);
//		fixture_sel_and_ctrl.addChangeListener(this);
//		screens.addChangeListener(this);
//		store_cue_btn.addActionListener(this);
		fixture_table.addMouseListener(this);
//		group_table.addMouseListener(this);
		dimmer_table.addMouseListener(this);
		sequence_table.addMouseListener(this);
		master_slider.addChangeListener(this);
		master_spinner.addChangeListener(this);
		fade_slider.addChangeListener(this);
		execute_preset.addActionListener(this);
//		stop_cue.addActionListener(this);
		assign_current_output.addActionListener(this);
		presets_grid.addMouseListener(this);
		execute_on_select.addChangeListener(this);
//		radio_fixture.addActionListener(this);
//		radio_group.addActionListener(this);
		preset_name.addKeyListener(this);
	//	fw_page_spinner.addChangeListener(this);
//		intensity_fader.addChangeListener(this);
//		intensity_spinner.addChangeListener(this);
		open_fw.addActionListener(this);
		cue_Go.addActionListener(this);
		cue_next.addActionListener(this);
		cue_prev.addActionListener(this);
		cue_store.addActionListener(this);
		cue_ok.addActionListener(this);
		bypass_go_chk.addChangeListener(this);
		slct_fix.addActionListener(this);
		slct_dim.addActionListener(this);
		slct_seq.addActionListener(this);
		bank_page_down.addActionListener(this);
		bank_page_up.addActionListener(this);
		
		saveItem.addActionListener(this);
		loadItem.addActionListener(this);
		
		btnDimmer.addActionListener(this);
		btnShutter.addActionListener(this);
		btnIris.addActionListener(this);
		btnFocus.addActionListener(this);
		btnZoom.addActionListener(this);
		btnColourWheel.addActionListener(this);
		btnRgbMixing.addActionListener(this);
		btnCto.addActionListener(this);
		btnGobo_1.addActionListener(this);
		btnGobo_2.addActionListener(this);
		btnGobo_3.addActionListener(this);
		btnPrism.addActionListener(this);
		btnFrost.addActionListener(this);
		btnControl.addActionListener(this);
		btnOther.addActionListener(this);
		
		btnDimmer.setEnabled(false);
		btnShutter.setEnabled(false);
		btnIris.setEnabled(false);
		btnFocus.setEnabled(false);
		btnZoom.setEnabled(false);
		btnColourWheel.setEnabled(false);
		btnRgbMixing.setEnabled(false);
		btnCto.setEnabled(false);
		btnGobo_1.setEnabled(false);
		btnGobo_2.setEnabled(false);
		btnGobo_3.setEnabled(false);
		btnPrism.setEnabled(false);
		btnFrost.setEnabled(false);
		btnControl.setEnabled(false);
		btnOther.setEnabled(false);
		
		initiate();
	}

/*	public static void updateGroupTable(){
		for(int a=0;a<512;a++){
			if(fixture[a] != null){
				for(int b=0;b<fixture[a].getChannels();b++){
					group_data[fixture[a].getId()-1][1] = fixture[a].getName();
					group_data[fixture[a].getId()-1][2] = fixture[a].getFixtureType();
					group_data[fixture[a].getId()-1][3] = fixture[a].getStartChannel() + "-" + (fixture[a].getStartChannel()+fixture[a].getChannels()-1);
				}
			}
		}
	}  */
	
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

					selectedFixture = null;
					
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
					lbl_nothingselected.setVisible(true);
					
					intensity_fader.setValue(0);
					
					intensity_fader.setEnabled(false);
					intensity_spinner.setEnabled(false);
					intensity_fader_lbl.setEnabled(false);
					clear_sel.setEnabled(false);

			} else if(e.getSource() == saveItem){
				
				if(currently_loaded_show == null){
					
					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("show.truss"));
				
					if(fc.showSaveDialog(main.this) == JFileChooser.APPROVE_OPTION){
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
				
				
			} else if(e.getSource() == loadItem){
				
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(load_show_filter);

				if(fc.showOpenDialog(main.this) == JFileChooser.APPROVE_OPTION){
					currently_loaded_show = fc.getSelectedFile();
					saveShow.load(fc.getSelectedFile());
					setTitle("Truss Alpha 1.0 - " + currently_loaded_show.getName());
			//		patch_table.revalidate();
				}
				
			} 
//				else if(e.getSource() == new_cue_stack){
//				
//				cueStackNames.add(new_cue_stack_tf.getText());
//				cueStack[cueStackCounter] = new cueStack();
//				
//			} else if(e.getSource() == cue_stack_selector){
//				
//				System.out.println("index:" + cue_stack_selector.getSelectedIndex());
//				int amtCues = cueStack[cue_stack_selector.getSelectedIndex()].getAmtCues();
//				cue_slider.setMajorTickSpacing(Math.round(amtCues/10));
//				cue_slider.setMaximum(amtCues);
//				number_of_cues_lbl.setText("No. of Cues: " + amtCues);
//				
//			} else if(e.getSource() == add_cue){
//				
//				cueStack[cue_stack_selector.getSelectedIndex()].addCue();
//				int amtCues = cueStack[cue_stack_selector.getSelectedIndex()].getAmtCues();
//				cue_slider.setMaximum(amtCues);
//				number_of_cues_lbl.setText("No. of Cues: " + amtCues);
//				
//			} 
			else if(e.getSource() == black_out){
				
				if(!blackout_on){
		//			System.out.println("Blackout on");
					blackout_on = true;
					
					// Broadcast Blackout
					if(artnet_node != null) {
						dmx.setSequenceID(sequenceID % 255);
						dmx.setDMX(blackout, 512);
		           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
		           		sequenceID++;
		            }
					
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
			//		System.out.println("Blackout off");
					blackout_on = false;
					
					for(int a=0;a<512;a++){
						data[a] = (byte)(((double)channel_data[a+1] / 255) * (Integer)master_spinner.getValue());
					//	data[a] = (byte)channel_data[a+1];
					}
					
					// Broadcast channel_data
					if(artnet_node != null && !blackout_on) {
						dmx.setSequenceID(sequenceID % 255);
						dmx.setDMX(data, 512);
		           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
		           		sequenceID++;
		            }
					
					blackout_th.stop();
					black_out.setForeground(Color.BLACK);
					black_out.setFont(new Font(null, Font.PLAIN, 11));
				}
				
			} else if(e.getSource() == store_cue_btn){
				
				cueStack[cue_stack_selector.getSelectedIndex()].saveToCurrentCue(channel_data);
				
			} else if(e.getSource() == assign_current_output){
				
				int[] data = new int[512];
				
				for(int a=0;a<512;a++){
					data[a] = (int)(((double)channel_data[a+1] / 255) * (Integer)master_spinner.getValue());
				//  System.out.println(((double)channel_data[a+1] / 255) * (int)master_spinner.getValue());
//					data[a] = (byte)channel_data[a+1];
				}
				preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()] = new Preset(presets_grid.getSelectedRow(), presets_grid.getSelectedColumn(), preset_name.getText(), data);

				data = null;
				
			} else if(e.getSource() == execute_preset){
				
				if(preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()] != null){
					preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()].execute();
				}
				
			} 
//			else if(e.getSource() == radio_fixture){
//				
//				patch_table_pane.setViewportView(patch_table);
//				
//			} else if(e.getSource() == radio_group){
//				
//				patch_table_pane.setViewportView(group_table);
//				
//			} 
			else if(e.getSource() == stop_cue){
				
				stop_cue.setEnabled(false);

				for(int a=0;a<512;a++){
//					data[a] = (byte)channe
					data[a] = (byte)channel_data[a+1];
				}
				
				// Broadcast channel_data with new master value
				if(artnet_node != null && !blackout_on) {
					dmx.setSequenceID(sequenceID % 255);
					dmx.setDMX(data, 512);
	           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
	           		sequenceID++;
	            }
				
//			else if(e.getSource() == open_fw){
//				
//				if(!dimmerControl){
//					patch_table_pane.setViewportView(dimmer_table);
//				} else {
//					patch_table_pane.setViewportView(fixture_table);
//				}
//				dimmerControl = !dimmerControl;

//				for(int b=0;b<512;b++){
//					if(dimmer[b+1] != null){
//						ctrl_fader[b].slider.setMinimum(0);
//						ctrl_fader[b].slider.setMaximum(255);
//						ctrl_fader[b].slider.setBackground(dimmer[b+1].c);
//						ctrl_fader[b].setChannel("1/" + (b+1));
//						ctrl_fader[b].setFaderVisible(true);
//						ctrl_fader[b].assignFixture(dimmer[b+1]);
//						ctrl_fader[b].slider.setValue(channel_data[dimmer[b+1].getStartChannel()]);
//						ctrl_fader[b].setName(dimmer[b+1].name);
//						ctrl_fader[b].assignChannel(new int[]{dimmer[b+1].startChannel});
//						ctrl_fader[b].prev_val = 0;
//						lbl_nothingselected.setVisible(false);
//					} else {
//						if(ctrl_fader[b] != null){
//							ctrl_fader[b].setFaderVisible(false);
//							ctrl_fader[b].unassign();
//							ctrl_fader[b].slider.setValue(0);
//						}
//					}
//				}
//				cur_sel_id.setText("-");
//				cur_sel_name.setText("Dimmers");
//				cur_sel_type.setText("-");
//				clear_sel.setEnabled(true);
				
			} else if(e.getSource() == cue_Go){
				
				if(cue[current_cue].data != null){
					cue[current_cue].execute();
					current_cue_lbl.setForeground(Color.GREEN);
				}
				
			} else if(e.getSource() == cue_next){
				
				if(current_cue < 999){
					current_cue++;
					if(bypass_go_chk.isSelected()){
						if(cue[current_cue].data != null){
							cue[current_cue].execute();
							current_cue_lbl.setForeground(Color.GREEN);
						}
					} else {
						current_cue_lbl.setForeground(Color.RED);
					}
					current_cue_lbl.setText(""+current_cue);
					cue_name_tf.setText(cue[current_cue].name);
					
					if(cue[current_cue].data == null){
						no_assign_lbl.setVisible(true);
					} else {
						no_assign_lbl.setVisible(false);
					}
				}
				
			} else if(e.getSource() == cue_prev){
				
				if(current_cue > 1){
					current_cue--;
					if(bypass_go_chk.isSelected()){
						if(cue[current_cue].data != null){
							cue[current_cue].execute();
							current_cue_lbl.setForeground(Color.GREEN);
						}
					} else {
						current_cue_lbl.setForeground(Color.RED);
					}
					current_cue_lbl.setText(""+current_cue);
					cue_name_tf.setText(cue[current_cue].name);
					
					if(cue[current_cue].data == null){
						no_assign_lbl.setVisible(true);
					} else {
						no_assign_lbl.setVisible(false);
					}
				}
				
			} else if(e.getSource() == cue_ok){
				
				cue[current_cue].name = cue_name_tf.getText();
				
			} else if(e.getSource() == cue_store){
				
				int[] data = new int[512];
				
				for(int a=0;a<512;a++){
					data[a] = (int)(((double)channel_data[a+1] / 255) * (Integer)master_spinner.getValue());
				}
				cue[current_cue].data = data;
				
				no_assign_lbl.setVisible(false);
				data = null;
			} else if(e.getSource() == btnDimmer || e.getSource() == btnShutter || e.getSource() == btnIris || e.getSource() == btnFocus || e.getSource() == btnZoom){
				
				FixtureSelectionEngine.setSingleFader((JButton)e.getSource());
				
			} else if(e.getSource() == btnColourWheel || e.getSource() == btnRgbMixing || e.getSource() == btnCto || e.getSource() == btnGobo_1 || e.getSource() == btnGobo_2 || e.getSource() == btnGobo_3 || e.getSource() == btnPrism || e.getSource() == btnFrost || e.getSource() == btnControl || e.getSource() == btnOther){
				
				FixtureSelectionEngine.setFaderBank((JButton)e.getSource());
				
			} else if(e.getSource() == slct_fix || e.getSource() == slct_dim || e.getSource() == slct_seq){
				
				slct_fix.setForeground(Color.BLACK);
				slct_dim.setForeground(Color.BLACK);
				slct_seq.setForeground(Color.BLACK);
				((JButton)e.getSource()).setForeground(Color.BLUE);
				if(e.getSource() == slct_fix){
					patch_table_pane.setViewportView(fixture_table);
				} else if(e.getSource() == slct_dim){
					patch_table_pane.setViewportView(dimmer_table);
				} else if(e.getSource() == slct_seq){
					patch_table_pane.setViewportView(sequence_table);
				}
			} else if(e.getSource() == bank_page_up){
				
				bank_page_lbl.setText(String.valueOf(Integer.parseInt(bank_page_lbl.getText())+1));
				FixtureSelectionEngine.setFaderBankPage(Integer.parseInt(bank_page_lbl.getText()));
				
			} else if(e.getSource() == bank_page_down){
				
				if(Integer.parseInt(bank_page_lbl.getText()) != 1){
					bank_page_lbl.setText(String.valueOf(Integer.parseInt(bank_page_lbl.getText())-1));
					FixtureSelectionEngine.setFaderBankPage(Integer.parseInt(bank_page_lbl.getText()));
				}
				
			}
		}
		
		/*
		 * stateChanged
		 */
		
		public void stateChanged(ChangeEvent e){
			if(e.getSource() == cue_slider){
				
				cue_counter.setValue(cue_slider.getValue());
				
			} else if(e.getSource() == cue_counter){
				
				cue_slider.setValue((Integer)cue_counter.getValue());
				
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
					if(!on_preset_screen){
						for(int a=0;a<18;a++){
							if(fw_fader[a].f != null){
								fw_fader[a].slider.setValue(channel_data[fw_fader[a].dmxChannels[0]]);
								fw_fader[a].revalidate();
							}
						}
					}
					on_preset_screen = false;
				} else if(screens.getSelectedIndex() == 0){
					if(!on_preset_screen){
						for(int a=0;a<50;a++){
							if(ctrl_fader[a].f != null){
								if(ctrl_fader[a].dmxChannels.length == 1){
									ctrl_fader[a].slider.setValue(ctrl_fader[a].dmxChannels[0]);
								}
//								ctrl_fader[a].slider.setValue(channel_data[ctrl_fader[a].dmxChannels[0]]);
								ctrl_fader[a].revalidate();
							}
						}
					}
					on_preset_screen = false;
				} else if(screens.getSelectedIndex() == 2){
					on_preset_screen = true;
				}
				
			} else if(e.getSource() == master_spinner){
				
				master_slider.setValue((Integer)master_spinner.getValue());
				setMaster((Integer)master_spinner.getValue());
				
			} else if(e.getSource() == master_slider){
				
				master_spinner.setValue(master_slider.getValue());
				setMaster((Integer)master_spinner.getValue());
				
			} else if(e.getSource() == fade_slider){
				
				fade_val.setText(""+fade_slider.getValue());
				
			} else if(e.getSource() == intensity_fader){
				
				intensity_spinner.setValue(intensity_fader.getValue());
				setIntensity((Integer)intensity_spinner.getValue());
				
			} else if(e.getSource() == intensity_spinner){
				
				intensity_fader.setValue((Integer)intensity_spinner.getValue());
				setIntensity((Integer)intensity_spinner.getValue());
				
			} else if(e.getSource() == execute_on_select){
				
				if(execute_on_select.isSelected()){
					execute_preset.setEnabled(false);
				} else {
					execute_preset.setEnabled(true);
				}
				
			} else if(e.getSource() == bypass_go_chk){
				
				if(bypass_go_chk.isSelected()){
					cue_Go.setEnabled(false);
				} else {
					cue_Go.setEnabled(true);
				}
				
			}
			
			
			// else if(e.getSource() == fw_page_spinner){
				
//				if((Integer)fw_page_spinner.getValue() > 0){
//					setFaderWingPage((Integer)fw_page_spinner.getValue());
//				}
				
		//	}
		}
		
//		public static void setFaderWingPage(int page){
//			for(int z=0;z<18;z++){
//				if(dimmer[((page-1)*18)+z+1] != null){
//					Fixture f = dimmer[(((Integer)fw_page_spinner.getValue()-1)*18)+z+1];
//					fw_fader[z].slider.setValue(Loader.frame.channel_data[f.startChannel]);
//					fw_fader[z].setChannel("1/" + f.startChannel);
//					fw_fader[z].assignFixture(f);
//					fw_fader[z].setName(f.name);  
//					fw_fader[z].assignChannel(new int[]{f.startChannel});
//				} else {
//					fw_fader[z].unassign();
//					fw_fader[z].setName("-");  
//					fw_fader[z].setChannel("-");
//					fw_fader[z].setStrValue("-");
//					fw_fader[z].slider.setValue(0);
//				}
//			}
//		}
		
		public void setMaster(int val){
			
			for(int a=0;a<512;a++){
				data[a] = (byte)((double)channel_data[a+1] / 255 * val);
			}
			
//			if(selectedFixture != null){
//				
//				if(selectedFixture instanceof Group){
//					for(int b=0;b<(((Group)selectedFixture).getMembers()[0]).getChannels();b++){
//						data[((((Group)selectedFixture).getMembers()[b]).getStartChannel()+b)-1] = (byte)((double)channel_data[(((Group)selectedFixture).getMembers()[b]).getStartChannel()+b] / 255 * (Integer)intensity_spinner.getValue() / 255 * val);
//					}
//				} else {
//					for(int b=0;b<((Fixture)(selectedFixture)).getChannels();b++){
//						data[(((Fixture)(selectedFixture)).getStartChannel()+b)-1] = (byte)((double)channel_data[((Fixture)(selectedFixture)).getStartChannel()+b] / 255 * (Integer)intensity_spinner.getValue() / 255 * val);
//					}
//				}
//			}
			
			// Broadcast channel_data with new master value
			if(artnet_node != null && !blackout_on) {
				dmx.setSequenceID(sequenceID % 255);
				dmx.setDMX(data, 512);
           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
           		sequenceID++;
            }
		}
		
		// Changes output of currently selected fixture to 'val'
		public void setIntensity(int val){
			
//			for(int a=0;a<512;a++){
//				data[a] = (byte)((double)channel_data[a+1] / 255 * (Integer)master_spinner.getValue() / 255 * val);
//			}
			
			if(selectedFixture != null){
				if(selectedFixture instanceof Group){
					for(int b=0;b<(((Group)selectedFixture).getMembers()[0]).getChannels();b++){
						data[((((Group)selectedFixture).getMembers()[b]).getStartChannel()+b)-1] = (byte)((double)channel_data[(((Group)selectedFixture).getMembers()[0]).getStartChannel()+b] / 255 * val / 255 * (Integer)master_spinner.getValue());
					}
				} else {
					for(int b=0;b<((Fixture)(selectedFixture)).getChannels();b++){
						data[(((Fixture)(selectedFixture)).getStartChannel()+b)-1] = (byte)((double)channel_data[((Fixture)(selectedFixture)).getStartChannel()+b] / 255 * val / 255 * (Integer)master_spinner.getValue());
					}
				}
				
				((Fixture)(selectedFixture)).intensity = val;
				
				// Broadcast channel_data with new master value
				if(artnet_node != null && !blackout_on) {
					dmx.setSequenceID(sequenceID % 255);
					dmx.setDMX(data, 512);
	           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
	           		sequenceID++;
	            }
			}
		}

		public void mousePressed(MouseEvent e) {
				
			if(e.getSource() == fixture_table){
				
				fixture_table.setColumnSelectionInterval(e.getX()/137, e.getX()/137);
				fixture_table.setRowSelectionInterval(e.getY()/50, e.getY()/50);

					try {
						
						String[] s = (fixture_table.getValueAt(fixture_table.getSelectedRow(), fixture_table.getSelectedColumn())).toString().split(" ");
						Fixture f = fixture[Integer.parseInt(s[s.length-1].split("<")[0])];
						
						FixtureSelectionEngine.selectFixture(f);
						
					} catch(NullPointerException n){
						return;
					}
				return;
				
			} else if(e.getSource() == group_table){
	
					selectedFixture = group[group_table.getSelectedRow()+1];					
			//		selectFixtures(group[group_table.getSelectedRow()+1].getMembers());

//					cur_sel_id.setText("GRP");
//					cur_sel_name.setText(group[group_table.getSelectedRow()+1].getName());
//					cur_sel_type.setText(group[group_table.getSelectedRow()+1].getFixtureType());

//					if(!getProfileByName(((Group) selectedFixture).getMembers()[0].getFixtureType()).built_in_dimmer){
//						intensity_fader.setMinimum(-255);
//						intensity_fader.setMaximum(255);
//						intensity_fader.setValue(0);
//						intensity_spinner.setValue(0);
//						intensity_fader.setEnabled(true);
//						intensity_spinner.setEnabled(true);
//						intensity_fader_lbl.setEnabled(true);
//						intensity_fader_lbl.setText(((Group)selectedFixture).getName());
//					} else {
//						intensity_fader.setValue(0);
//						intensity_fader.setEnabled(false);
//						intensity_spinner.setEnabled(false);
//						intensity_fader_lbl.setEnabled(false);
//						intensity_fader_lbl.setText("-");
//					}
				
	//			group_btn.setEnabled(false);
				return;
				
			} else if(e.getSource() == dimmer_table){
				
				dimmer_table.setColumnSelectionInterval(e.getX()/137, e.getX()/137);
				dimmer_table.setRowSelectionInterval(e.getY()/50, e.getY()/50);
		//		dimmer_table.setColumnSelectionInterval(e.getX()/137, e.getX()/137);
				
				try {
					
			//		selectedFixture = dimmer[dimmer_table.getSelectedRow()+1];
					String[] s = (dimmer_table.getValueAt(dimmer_table.getSelectedRow(), dimmer_table.getSelectedColumn())).toString().split(" ");
					FixtureSelectionEngine.selectDimmers(dimmer[Integer.parseInt(s[s.length-1].split("<")[0])]);

//					cur_sel_id.setText("ID" + (dimmer_table.getSelectedRow()+1));
//					cur_sel_name.setText(dimmer[dimmer_table.getSelectedRow()+1].getName());
//					cur_sel_type.setText(dimmer[dimmer_table.getSelectedRow()+1].getFixtureType().name);
					
			//		selectedFixtures[0] = dimmer[dimmer_table.getSelectedRow()+1];
					
				} catch(NullPointerException npe){
					return;
				}
			}
//			else if(e.getSource() == presets_grid){
//
//				if(preset_name.getText().equals("")){
//					assign_current_output.setEnabled(false);
//				} else {
//					assign_current_output.setEnabled(true);
//				}
//				
//				if(preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()] != null){
//					
//					assign_current_output.setText("Edit");
//					preset_name.setText(preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()].name);
//					assign_current_output.setEnabled(true);
//					
//					if(execute_on_select.isSelected()){
//						preset[presets_grid.getSelectedColumn()][presets_grid.getSelectedRow()].execute();
//					}
//					
//				} else {
//					assign_current_output.setText("Assign Current Output");
//				}
//				return;
//				
//			}
			
	/*		for(int a=0;a<512;a++){
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
			}  */
		}
		
//		public void selectFixtures(Fixture[] f){
//
//			Profile prof = f[0].getFixtureType();
//			selectedFixtures_amt = f.length;
//			
//			btnDimmer.setEnabled(false);
//			btnShutter.setEnabled(false);
//			btnIris.setEnabled(false);
//			btnFocus.setEnabled(false);
//			btnZoom.setEnabled(false);
//			btnColourWheel.setEnabled(false);
//			btnRgbMixing.setEnabled(false);
//			btnCto.setEnabled(false);
//			btnGobo_1.setEnabled(false);
//			btnGobo_2.setEnabled(false);
//			btnGobo_3.setEnabled(false);
//			btnPrism.setEnabled(false);
//			btnFrost.setEnabled(false);
//			btnControl.setEnabled(false);
//			btnOther.setEnabled(false);
//			
//		//	System.out.println(selectedFixtures[0].getFixtureType().channel_function[1]);
//			
//			// Check if the selected fixture/s have a the following functions, enable accordingly
//			for(int a=0;a<512;a++){
//				if(selectedFixtures[a] != null){
//					if(selectedFixtures[a].getFixtureType().channel_function[0] != 0){
//						setSingleFader(btnDimmer);
//						btnDimmer.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[1] != 0){
//						btnShutter.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[2] != 0){
//						btnIris.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[3] != 0){
//						btnFocus.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[4] != 0){
//						btnZoom.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[7] != 0){
//						btnColourWheel.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[9] != 0 || selectedFixtures[a].getFixtureType().channel_function[11] != 0 || selectedFixtures[a].getFixtureType().channel_function[13] != 0){
//						setFaderBank(btnRgbMixing);
//						btnRgbMixing.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[21] != 0){
//						btnCto.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[23] != 0){
//						btnGobo_1.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[25] != 0){
//						btnGobo_2.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[27] != 0){
//						btnGobo_3.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[29] != 0){
//						btnPrism.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[31] != 0){
//						btnFrost.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[34] != 0){
//						btnControl.setEnabled(true);
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[39] != 0){
//						btnOther.setEnabled(true);
//					}
//				}
//			}
//			
//			Fader[] faders = {single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt};
//			
//			for(Fader fader : faders){
//				if(selectedFixtures_amt == 1){
//					fader.assignFixture(f[0]);
//					fader.slider.setMinimum(0);
//					fader.slider.setMaximum(255);
//				} else {
//					fader.assignFixture(null);
//					fader.slider.setMinimum(-255);
//					fader.slider.setMaximum(255);
//					fader.slider.setValue(0);
//				}
//			}
//			
//			// Check if the selected fixture/s have pan and/or tilt functions, enable accordingly
//			for(int a=0;a<512;a++){
//				if(selectedFixtures[a] != null){
//					if(selectedFixtures[a].getFixtureType().channel_function[5] != 0){
//						if(selectedFixtures_amt == 1){
//							pan.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[5])-1]);
//							pan.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[5])-1});
//							pan.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[5])-1];
//						}
//					}
//					if(selectedFixtures[a].getFixtureType().channel_function[6] != 0){
//						if(selectedFixtures_amt == 1){
//							pan.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[6])-1]);
//							pan.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[6])-1});
//							pan.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[6])-1];
//						}
//					}
//				}
//			}
			
			
//			if(selectedFixtures_amt == 1){
//				single.assignFixture(selectedFixtures[0]);
//				bank_1.assignFixture(selectedFixtures[0]);
//				bank_2.assignFixture(selectedFixtures[0]);
//				bank_3.assignFixture(selectedFixtures[0]);
//				bank_4.assignFixture(selectedFixtures[0]);
//				bank_5.assignFixture(selectedFixtures[0]);
//				pan.assignFixture(selectedFixtures[0]);
//				tilt.assignFixture(selectedFixtures[0]);
//			} else {
//				single.assignFixture(null);
//				bank_1.assignFixture(null);
//				bank_2.assignFixture(null);
//				bank_3.assignFixture(null);
//				bank_4.assignFixture(null);
//				bank_5.assignFixture(null);
//				pan.assignFixture(null);
//				tilt.assignFixture(null);
//			}

//				if(f.length > 1){
//					ctrl_fader[b].slider.setMinimum(-255);
//					ctrl_fader[b].slider.setMaximum(255);
//					ctrl_fader[b].slider.setValue(0);
//				} else {
//					ctrl_fader[b].slider.setMinimum(0);
//					ctrl_fader[b].slider.setMaximum(255);
//					ctrl_fader[b].slider.setValue(channel_data[f[0].getStartChannel()+b]);
//				}
//				ctrl_fader[b].setFaderVisible(true);
//				if(usingOverallChannels){
//					ctrl_fader[b].setChannel(b+1 + "/" + (f[0].getStartChannel()+b));
//				} else {
//					ctrl_fader[b].setChannel(b+1 + "/-");
//				}
//				
//				int[] channels = new int[f.length];
//				for(int a=0;a<channels.length;a++){
//					if(f[a] != null){
//						channels[a] = f[a].getStartChannel()+b;
//					}
//			}	
//		}

		// Set the function of the "Single" Control Fader, based off the button clicked -which is passed to this method
//		public void setSingleFader(JButton btn){
//			int index = 0;
//
//			if(btn == btnDimmer){
//				index = 0;
//			} else if(btn == btnShutter){
//				index = 1;
//			} else if(btn == btnIris){
//				index = 2;
//			} else if(btn == btnFocus){
//				index = 3;
//			} else if(btn == btnZoom){
//				index = 4;
//			}
//			single.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[index]-1)).get(0));
//
//				if(selectedFixtures_amt == 1){
//					single.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[index])-1]);
//					single.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[index])-1});
//					single.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[index])-1];
//					
//					if( ((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[index]-1)).size() >= 3 ){
//						selectedFixtures[0].getFixtureType().setStringValue(single);
//					} else {
//						single.setStrValue("-");
//					}
//				} else {
//					single.prev_val = 0;
//				}	
//		}
		
		// Set the function of "Bank" Fader 1-5, based off the button clicked -which is passed to this method
//		public void setFaderBank(JButton btn){
//			
//			Fader[] bank_faders = {bank_1, bank_2, bank_3, bank_4, bank_5};
//			
//			for(Fader f : bank_faders){
//				f.f = null;
//				f.unassign();
//				f.setName("-");
//				f.slider.setValue(0);
//				f.setStrValue("-");
//			}
//
//			if(btn == btnColourWheel){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[7])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[7])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[7])-1];
//				bank_1.setName("Colour Wheel");
//				
//				setStringValueForFaders(bank_1, 7);
//				
//			} else if(btn == btnRgbMixing){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[9])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[9])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[9])-1];
//				bank_1.setName("Red");
//				setStringValueForFaders(bank_1, 9);
//				
//				bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[11])-1]);
//				bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[11])-1});
//				bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[11])-1];
//				bank_2.setName("Green");
//				setStringValueForFaders(bank_2, 11);
//				
//				bank_3.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[13])-1]);
//				bank_3.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[13])-1});
//				bank_3.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[13])-1];
//				bank_3.setName("Blue");
//				setStringValueForFaders(bank_3, 13);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[50] != 0){
//					bank_4.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[50])-1]);
//					bank_4.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[50])-1});
//					bank_4.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[50])-1];
//					bank_4.setName("White");
//					setStringValueForFaders(bank_4, 50);
//				}
//				
//			} else if(btn == btnCto){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[21])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[21])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[21])-1];
//				bank_1.setName("CTO");
//				setStringValueForFaders(bank_1, 21);
//				
//			} else if(btn == btnGobo_1){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[23])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[23])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[23])-1];
//				bank_1.setName("Gobo 1");
//				setStringValueForFaders(bank_1, 23);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[24] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[24])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[24])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[24])-1];
//					bank_2.setName("Gobo 1 Rot");
//					setStringValueForFaders(bank_2, 24);
//				}
//				
//			} else if(btn == btnGobo_2){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[25])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[25])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[25])-1];
//				bank_1.setName("Gobo 2");
//				setStringValueForFaders(bank_1, 25);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[26] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[26])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[26])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[26])-1];
//					bank_2.setName("Gobo 2 Rot");
//					setStringValueForFaders(bank_2, 26);
//				}
//				
//			} else if(btn == btnGobo_3){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[27])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[27])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[27])-1];
//				bank_1.setName("Gobo 3");
//				setStringValueForFaders(bank_1, 27);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[28] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[28])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[28])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[28])-1];
//					bank_2.setName("Gobo 3 Rot");
//					setStringValueForFaders(bank_2, 28);
//				}
//				
//			} else if(btn == btnPrism){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[29])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[29])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[29])-1];
//				bank_1.setName("Prism");
//				setStringValueForFaders(bank_1, 29);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[30] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[30])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[30])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[30])-1];
//					bank_2.setName("Prism Rot");
//					setStringValueForFaders(bank_2, 30);
//				}
//				
//			} else if(btn == btnFrost){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[31])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[31])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[31])-1];
//				bank_1.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[31])).get(0));
//				setStringValueForFaders(bank_1, 31);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[32] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[32])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[32])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[32])-1];
//					bank_2.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[32])).get(0));
//					setStringValueForFaders(bank_2, 32);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[33] != 0){
//					bank_3.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[33])-1]);
//					bank_3.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[33])-1});
//					bank_3.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[33])-1];
//					bank_3.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[33])).get(0));
//					setStringValueForFaders(bank_3, 33);
//				}
//				
//			} else if(btn == btnControl){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[34])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[34])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[34])-1];
//				bank_1.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[34])).get(0));
//				setStringValueForFaders(bank_1, 34);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[35] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[35])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[35])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[35])-1];
//					bank_2.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[35])).get(0));
//					setStringValueForFaders(bank_2, 35);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[36] != 0){
//					bank_3.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[36])-1]);
//					bank_3.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[36])-1});
//					bank_3.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[36])-1];
//					bank_3.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[36])).get(0));
//					setStringValueForFaders(bank_3, 36);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[36] != 0){
//					bank_4.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[37])-1]);
//					bank_4.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[37])-1});
//					bank_4.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[37])-1];
//					bank_4.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[37])).get(0));
//					setStringValueForFaders(bank_4, 37);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[38] != 0){
//					bank_5.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[38])-1]);
//					bank_5.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[38])-1});
//					bank_5.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[38])-1];
//					bank_5.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[38])).get(0));
//					setStringValueForFaders(bank_5, 38);
//				}
//				
//			} else if(btn == btnOther){
//				
//				bank_1.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[39])-1]);
//				bank_1.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[39])-1});
//				bank_1.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[39])-1];
//				bank_1.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[39])).get(0));
//				setStringValueForFaders(bank_1, 39);
//				
//				if(selectedFixtures[0].getFixtureType().channel_function[40] != 0){
//					bank_2.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[40])-1]);
//					bank_2.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[40])-1});
//					bank_2.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[40])-1];
//					bank_2.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[40])).get(0));
//					setStringValueForFaders(bank_2, 40);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[41] != 0){
//					bank_3.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[41])-1]);
//					bank_3.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[41])-1});
//					bank_3.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[41])-1];
//					bank_3.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[41])).get(0));
//					setStringValueForFaders(bank_3, 41);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[42] != 0){
//					bank_4.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[42])-1]);
//					bank_4.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[42])-1});
//					bank_4.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[42])-1];
//					bank_4.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[42])).get(0));
//					setStringValueForFaders(bank_4, 42);
//				}
//				if(selectedFixtures[0].getFixtureType().channel_function[43] != 0){
//					bank_5.slider.setValue(channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[43])-1]);
//					bank_5.assignChannel(new int[]{(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[43])-1});
//					bank_5.prev_val = channel_data[(selectedFixtures[0].getStartChannel()+selectedFixtures[0].getFixtureType().channel_function[43])-1];
//					bank_5.setName((String)((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[43])).get(0));
//					setStringValueForFaders(bank_5, 43);
//				}
//				
//			}
//
//		}
		public void setStringValueForFaders(Fader f, int channel_function_index){
			if( ((Vector)selectedFixtures[0].getFixtureType().function.get(selectedFixtures[0].getFixtureType().channel_function[channel_function_index]-1)).size() >= 3 ){
				selectedFixtures[0].getFixtureType().setStringValue(f);
			} else {
				f.setStrValue("-");
			}
		}
		
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		@Override
		public void discoveredNewNode(ArtNetNode node) {
			if(artnet_node == null){
				artnet_node = node;
				dmx.setUniverse(artnet_node.getSubNet(), artnet_node.getDmxOuts()[0]);
//				System.out.println("node discovered");
				error_disp.setForeground(Color.WHITE);
				error_disp.setText("Node discovered on: " + artnet_node.getIPAddress().toString().split("/")[1]);
			}
		}

		@Override
		public void discoveredNodeDisconnected(ArtNetNode node) {
			error_disp.setForeground(Color.RED);
			error_disp.setText("! Node on " + node.getIPAddress().toString().split("/")[1] + " Disconnected");
//			System.out.println("node disconnected: " + node);
			if(artnet_node == node){
				artnet_node = null;
			}
		}

		@Override
		public void discoveryCompleted(List<ArtNetNode> nodes) {
		//	System.out.println(nodes.size() + " nodes found");
			if(nodes.size() == 0){
//				System.out.println("nodes discovered: " + nodes.size());
				error_disp.setForeground(Color.RED);
				error_disp.setText("! No ArtNet Nodes Discovered");
			}
		}

		@Override
		public void discoveryFailed(Throwable t) {
			error_disp.setForeground(Color.RED);
			error_disp.setText("! Discovery Failed");
//			System.out.println("failed");
		}

		public void keyTyped(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {

//			if(preset_name.getText().equals("") || presets_grid.getSelectedRow() == -1){
//				assign_current_output.setEnabled(false);
//			} else {
//				assign_current_output.setEnabled(true);
//			}
			
		}
		
		public void loadProfile(String name){
//			for(int c=0;c<51;c++){
//				profile_channel_function[c] = 0;
//			}
			channel_amt = 0;
			
			SAXParser parser;
			try {
				parser = factory.newSAXParser();
				parser.parse("file:\\Desktop/"+name, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		// TABLE ROW COLOUR RENDERERS
		
		public static class FixtureTableRenderer extends DefaultTableCellRenderer {
			@Override
		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
		       	int id = (Integer)table.getValueAt(row, 0);

//		       		try {
//		       			if(fixture[id].c != null){
//			       			table.setBackground(fixture[id].c);
//			       		} else {
//			                setOpaque(false);
//			            }
//		       		} catch(Exception e){
//		       			
//		       		}
		       	
		        return this;
		    }
		}
}