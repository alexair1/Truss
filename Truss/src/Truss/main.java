package Truss;

import java.awt.*;

import javax.print.DocFlavor.URL;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.BindException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import artnet4j.ArtNet;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Truss DMX Lighting Software.
 * 
 * @author Alex Air
 * @version Alpha 1.2.0
 */
public class main extends JFrame implements ActionListener, ChangeListener, MouseListener, ArtNetDiscoveryListener, 
											KeyListener, MouseMotionListener, ListSelectionListener, WindowListener{
	
	private static final long serialVersionUID = 1L;
	
	static Preset[] preset = new Preset[50];
	static Profile[] profile = new Profile[100];
//	static cueStack[] cueStack = new cueStack[100];
//	static Cue[] cue = new Cue[1000];
	static Vector<String> groupNames = new Vector<String>();
	
	byte[] blackout = new byte[512];
	
	static boolean[] isChannelDimmer = new boolean[512];
	static Fixture[] fixture = new Fixture[512];
	static Group[] group = new Group[513];
	static int[] data = new int[512];
	static int[] dataBeforePresetCue = new int[512];
	static int[] liveData = new int[512];
	static byte[] finaldata = new byte[512];
	static Fixture[] selectedFixtures = new Fixture[512];
	
	static Object[][] patch_data = new Object[512][4];
	static Object[][] group_data = new Object[512][3];
	
	static boolean blackout_on = false, artnet_con = false;
	static int selectedFixturesAmt = 0, 
			   current_cue = 1, 
			   cueStackCounter = 0, 
			   fixtureNumber = 0, 
			   profileID = 0, 
			   group_counter = 1, 
			   sequenceID = 0;
	
	// UI Components
	JButton preset_off, cue_Go, cue_next, cue_prev, cue_store, cue_ok, stop_cue, execute_preset, assign_current_output, group_btn, store_cue_btn, add_cue, black_out, clear, next_cue, prev_cue, new_fixture, edit_fixture;
	JSlider master_slider, fade_slider;
	static JSpinner master_spinner;
	JTextField preset_name, cue_name_tf;
	JPanel contentPane;
	JTable patch_table, presets_table, group_table;
	JScrollPane patch_table_pane, group_table_pane;
	JTabbedPane programmingTabbedPane;
	JMenuItem saveItem, loadItem, loadProfileItem, aboutItem;
	JCheckBox execute_on_select, bypass_go_chk;
	JLabel no_assign_lbl, cur_sel_id, cur_sel_name, error_disp, fade_val, active_preset_lbl, current_cue_lbl;
	Fader single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt;

	// ArtNet Variables
	static ArtNetNode artnet_node;
	static ArtNet artnet = new ArtNet();
	static ArtDmxPacket dmx = new ArtDmxPacket();

	Robot panTiltRobot;
	Thread clear_flash, blackout_th;
	File currently_loaded_show;
	
	static JButton btnFocus, btnDimmer, btnIris, btnShutter, btnZoom, btnColourWheel, btnRgbMixing, btnCto, btnGobo_1, btnGobo_2,
				   btnGobo_3, btnPrism, btnFrost, btnControl, btnOther;
	
	// XML Parser variables
	String profile_name, profile_mode;
	Vector<Vector<Object>> profile_channels = new Vector<Vector<Object>>();
	boolean profile_built_in_dimmer;
	int[] profile_channel_function = new int[51];
	Vector<Object> profile_channel;
	
	SAXParserFactory factory = SAXParserFactory.newInstance();
	DefaultHandler handler;

	public void initiate() {
		
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
		
		boolean b = true;

		/*
		 * XML Handler for Profiles
		 */
		try {
			
			handler = new DefaultHandler(){
				int channel_amt = 0;
					
				public void startElement(String uri, String localname, String name, Attributes attributes) throws SAXException {
			
					if(name.equals("fixture")){
						profile_name = attributes.getValue(0);
						profile_mode = attributes.getValue(1);
						profile_channels.clear();
						channel_amt = 0;

					} else {
						
						if(name.equals("channel")){
							
							profile_channel = new Vector<Object>();
							profile_channel.addElement(attributes.getValue(0)); //First element is channel name
							profile_channel.addElement(attributes.getValue(1)); //Second element is channel function

							if(Arrays.asList(ChFn.channels).indexOf(attributes.getValue(1)) != -1){
								profile_channel_function[Arrays.asList(ChFn.channels).indexOf(attributes.getValue(1))] = channel_amt;
							}
							channel_amt++;
							
						} else {
							profile_channel.addElement(new Range(Integer.parseInt(attributes.getValue(0)), Integer.parseInt(attributes.getValue(1)), attributes.getValue(2)));
						}
						
					} 
					
				}		
				public void endElement(String uri, String localname, String name) throws SAXException {
					
					if(name.equals("fixture")){
						profile[profileID] = new Profile(profile_name, profile_mode, (Vector<Vector<Object>>)profile_channels.clone(), 
														 profile_built_in_dimmer, Arrays.copyOf(profile_channel_function, profile_channel_function.length));
						profileID++;
					} else if(name.equals("channel")){
						profile_channels.add(profile_channel);
					}
					
				}
			};
			
			/*
			 * Load Profiles
			 */
			String path = "Truss/resources/profiles";
			java.net.URL dirURL = main.class.getClassLoader().getResource(path);

			// Load straight from directory if not complied into jar.
			if (dirURL.getProtocol().equals("file")) {

				for (File fileEntry : new File(dirURL.toURI()).listFiles()) {
					if(fileEntry.getName().endsWith(".xml")){
				    	
						Arrays.fill(profile_channel_function, -1);
				    						
						SAXParser parser;
						parser = factory.newSAXParser();
						parser.parse(new File(fileEntry.getPath()), handler);
					}
				}
				
			// Else if running standalone, load from enumeration of jar file
			} else if (dirURL.getProtocol().equals("jar")) {
				String me = main.class.getName().replace(".", "/")+".class";
				dirURL = main.class.getClassLoader().getResource(me);

				/* A JAR path */
				String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
				JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
				Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar

				while(entries.hasMoreElements()) {
						
					String name = entries.nextElement().getName();

					if (name.startsWith(path)) { //filter according to the path
						if(name.endsWith(".xml")){
								    	
							Arrays.fill(profile_channel_function, -1);
						    						
							SAXParser parser;
							parser = factory.newSAXParser();
							// Splitting at 6 removes Truss/
						 	parser.parse(main.class.getResourceAsStream(name.substring(6, name.length())), handler);
						}
					}         
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}

	}  

	public main() {
		
//		try {
//		UIManager.setLookAndFeel("javax.swing.pl");
//	} catch (Exception e1) {
//		// TODO Auto-generated catch block
//		e1.printStackTrace();
//	};
	
//	try {
//		SynthLookAndFeel laf = new SynthLookAndFeel();
//	//	laf.load(getClass().getResourceAsStream("/src/look_and_feel.xml"), getClass());
//		laf.load(new FileInputStream("src/look_and_feel.xml"), main.class);
//		UIManager.setLookAndFeel(laf);
//	} catch (Exception e) {
//		e.printStackTrace();
//	}   
		
		contentPane = new JPanel();
		contentPane.addKeyListener(this);
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(238, 238, 238));
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(0, 0, 1290, 710);
		setMinimumSize(new Dimension(1280, 710));
		
		setContentPane(contentPane);
		setTitle("Truss - Alpha 1.2");
		setResizable(false);
		
		// Menu Bar
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
			saveItem = new JMenuItem("Save");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			fileMenu.add(saveItem);
			
			loadItem = new JMenuItem("Load");
			loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			fileMenu.add(loadItem);
			
			loadProfileItem = new JMenuItem("Load Profiles");
		//	fileMenu.add(loadProfileItem);
			
			aboutItem = new JMenuItem("About");
			fileMenu.add(aboutItem);
		
		setJMenuBar(menuBar); 
		
		// Patch and Control Screen
		
		JPanel patch_and_control = new JPanel();
		patch_and_control.setBounds(6, -2, 977, 627);
		patch_and_control.setLayout(null);
		patch_and_control.setBackground(new Color(238, 238, 238));
		
		for(int a=0;a<512;a++){
			patch_data[a][0] = a+1;
		}
		
		patch_table = new JTable(patch_data, new Object[] {"ID", "Name", "Fixture", "Channels"}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		patch_table.setFocusable(false);
		patch_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		patch_table.setBounds(2, 18, 450, 8208);
		patch_table.getColumnModel().getColumn(0).setMaxWidth(30);
		patch_table.getColumnModel().getColumn(3).setMaxWidth(80);
		patch_table.getTableHeader().setReorderingAllowed(false);
		
		group_table = new JTable(group_data, new Object[] {"Name", "Fixture Type", "Size"}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		group_table.setFocusable(false);
		group_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		group_table.setBounds(2, 18, 450, 8208);
		group_table.getColumnModel().getColumn(2).setMaxWidth(80);
		group_table.getTableHeader().setReorderingAllowed(false);
		
		patch_table_pane = new JScrollPane(patch_table);
		patch_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		patch_table_pane.setBounds(7, 13, 416, 286);
		patch_and_control.add(patch_table_pane);
		
		group_table_pane = new JScrollPane(group_table);
		group_table_pane.setBounds(433, 13, 408, 286);
		group_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		patch_and_control.add(group_table_pane);
		
		new_fixture = new JButton("New Fixture");
		new_fixture.setBounds(851, 9, 99, 40);
		patch_and_control.add(new_fixture);
		
		group_btn = new JButton("Group");
		group_btn.setEnabled(false);
		group_btn.setBounds(875, 50, 75, 29);
		patch_and_control.add(group_btn);
		
		// Fixture Control
		
		Color bg = new Color(238, 238, 238);
		
		JPanel fixture_control = new JPanel();
		fixture_control.setBackground(bg);
		fixture_control.setLayout(null);
		fixture_control.setBounds(7, 310, 960, 320);
		patch_and_control.add(fixture_control);

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
		pan.create(0, fixture_control, bg, 790, 0);
		pan.setName("Pan");
			
		tilt = new Fader();
		tilt.create(0, fixture_control, bg, 880, 0);
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
		btnColourWheel.setBounds(620, 0, 85, 39);
		fixture_control.add(btnColourWheel);
			
		btnRgbMixing = new JButton("RGB Mixing");
		btnRgbMixing.setBounds(620, 50, 85, 39);
		fixture_control.add(btnRgbMixing);
			
		btnCto = new JButton("CTO");
		btnCto.setBounds(620, 100, 85, 39);
		fixture_control.add(btnCto);
			
		btnGobo_1 = new JButton("Gobo 1");
		btnGobo_1.setBounds(620, 150, 85, 39);
		fixture_control.add(btnGobo_1);
			
		btnGobo_2 = new JButton("Gobo 2");
		btnGobo_2.setBounds(620, 200, 85, 39);
		fixture_control.add(btnGobo_2);
			
		btnGobo_3 = new JButton("Gobo 3");
		btnGobo_3.setBounds(620, 250, 85, 39);
		fixture_control.add(btnGobo_3);
			
		btnPrism = new JButton("Prism");
		btnPrism.setBounds(715, 0, 67, 39);
		fixture_control.add(btnPrism);
			
		btnFrost = new JButton("Frost");
		btnFrost.setBounds(715, 50, 67, 39);
		fixture_control.add(btnFrost);
			
		btnControl = new JButton("Control");
		btnControl.setBounds(715, 100, 67, 39);
		fixture_control.add(btnControl);
			
		btnOther = new JButton("Other");
		btnOther.setBounds(715, 150, 67, 39);
		fixture_control.add(btnOther);	
			
		black_out = new JButton("Black Out");
		black_out.setBounds(851, 257, 99, 32);
		patch_and_control.add(black_out);
			
		clear = new JButton("Clear");
		clear.setBounds(851, 230, 99, 32);
		patch_and_control.add(clear);
			
		JButton open_console = new JButton("Console");
		open_console.setBounds(851, 208, 99, 23);
		patch_and_control.add(open_console);
		
		contentPane.add(patch_and_control);
		
		// contentPane (Right hand side control and display)
		
		JLabel cur_sel_title = new JLabel("Current Selection");
		cur_sel_title.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		cur_sel_title.setBounds(995, 10, 116, 16);
		contentPane.add(cur_sel_title);
		
		cur_sel_id = new JLabel("ID--");
		cur_sel_id.setBounds(995, 31, 40, 16);
		contentPane.add(cur_sel_id);
		
		cur_sel_name = new JLabel("-");
		cur_sel_name.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		cur_sel_name.setBounds(1041, 31, 233, 16);
		contentPane.add(cur_sel_name);
		
		error_disp = new JLabel("", SwingConstants.RIGHT);
		error_disp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		error_disp.setForeground(Color.RED);
		error_disp.setBounds(995, 640, 279, 16);
		contentPane.add(error_disp);	
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(995, 53, 279, 12);
		contentPane.add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(995, 629, 279, 12);
		contentPane.add(separator_3);
		
		master_slider = new JSlider(0, 255, 0);
		master_slider.addMouseListener(this);
		master_slider.setMinorTickSpacing(15);
		master_slider.setPaintTicks(true);
		master_slider.setBackground(new Color(238, 238, 238));
		master_slider.setBounds(985, 556, 289, 29);
		contentPane.add(master_slider);
		
		master_spinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		master_spinner.setBounds(1065, 529, 65, 28);
		contentPane.add(master_spinner);
		
		JLabel lblMaster = new JLabel("Master", SwingConstants.CENTER);
		lblMaster.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblMaster.setBounds(973, 535, 80, 16);
		contentPane.add(lblMaster);
		
		fade_slider = new JSlider(0, 10000, 0);
		fade_slider.setMinorTickSpacing(100);
		fade_slider.setBackground(new Color(238, 238, 238));
		fade_slider.setSnapToTicks(true);
		fade_slider.setBounds(985, 605, 289, 28);
		contentPane.add(fade_slider);
		
		fade_val = new JLabel("0", SwingConstants.CENTER);
		fade_val.setBounds(1050, 588, 80, 16);
		contentPane.add(fade_val);
		
		JLabel lblFade = new JLabel("Fade (ms)", SwingConstants.CENTER);
		lblFade.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblFade.setBounds(985, 588, 80, 16);
		contentPane.add(lblFade);
		
		// Programming Pane
		
		programmingTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		programmingTabbedPane.setBounds(985, 59, 300, 475);
		contentPane.add(programmingTabbedPane);
		
		JPanel cue_panel = new JPanel();
		cue_panel.setBackground(new Color(229, 229, 229));
	//	programmingTabbedPane.add(cue_panel);
	//	programmingTabbedPane.setTitleAt(0, "Cue");
		cue_panel.setLayout(null);
		
		JPanel preset_panel = new JPanel();
		preset_panel.setBackground(new Color(229, 229, 229));
		programmingTabbedPane.add(preset_panel);
		programmingTabbedPane.setTitleAt(0, "Preset");
		preset_panel.setLayout(null);
		
		// Presets Components
		
		presets_table = new JTable(new Object[50][1], new Object[]{"Preset"}){
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		presets_table.setRowSelectionAllowed(false);
		presets_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		presets_table.setColumnSelectionAllowed(false);
		presets_table.setCellSelectionEnabled(true);
		presets_table.setGridColor(Color.LIGHT_GRAY);
		
		JScrollPane presets_sp = new JScrollPane(presets_table);
		presets_sp.setBounds(5, 6, 269, 304);
		preset_panel.add(presets_sp);
		
		assign_current_output = new JButton("Assign Current Output");
		assign_current_output.setBounds(102, 367, 176, 29);
		assign_current_output.setEnabled(false);
		preset_panel.add(assign_current_output);
		
		execute_preset = new JButton("Execute");
		execute_preset.setBounds(185, 400, 93, 29);
		preset_panel.add(execute_preset);
		
		execute_on_select = new JCheckBox("Execute on Select");
		execute_on_select.setBounds(6, 403, 142, 23);
		preset_panel.add(execute_on_select);
		
		preset_name = new JTextField();
		preset_name.setBounds(59, 343, 215, 28);
		preset_panel.add(preset_name);
		preset_name.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(7, 349, 40, 16);
		preset_panel.add(lblName);
		
		active_preset_lbl = new JLabel("No Preset Active");
		active_preset_lbl.setBounds(7, 321, 265, 16);
		active_preset_lbl.setForeground(new Color(100,100,100));
		preset_panel.add(active_preset_lbl);
		
		preset_off = new JButton("Off");
		preset_off.setBounds(56, 367, 55, 29);
		preset_off.setEnabled(false);
		preset_panel.add(preset_off);
		
		// Cue components
		
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
		
		stop_cue = new JButton("Stop Cue");
		stop_cue.setEnabled(false);
		stop_cue.setBounds(149, 145, 117, 29);
		cue_panel.add(stop_cue);
		
		no_assign_lbl = new JLabel("No Assign");
		no_assign_lbl.setBounds(182, 32, 93, 14);
		cue_panel.add(no_assign_lbl);
		
		fixtureWizard a = new fixtureWizard();
			new_fixture.addActionListener(a);
			
		Console c = new Console();
			open_console.addActionListener(c);
			
//		remoteSettings d = new remoteSettings();
	//		remote_btn.addActionListener(d);
			
		assignGroup d = new assignGroup();
			group_btn.addActionListener(d);
			
		About e = new About();
			aboutItem.addActionListener(e);
		
		// Listeners
//		next_cue.addActionListener(this);
//		prev_cue.addActionListener(this);
//		cue_slider.addChangeListener(this);
//		cue_counter.addChangeListener(this);
		saveItem.addActionListener(this);
		loadItem.addActionListener(this);
		loadProfileItem.addActionListener(this);
//		store_cue_btn.addActionListener(this);
		patch_table.addMouseListener(this);
		group_table.addMouseListener(this);
		presets_table.getSelectionModel().addListSelectionListener(this);
		master_slider.addChangeListener(this);
		master_spinner.addChangeListener(this);
		fade_slider.addChangeListener(this);
		execute_preset.addActionListener(this);
		stop_cue.addActionListener(this);
		assign_current_output.addActionListener(this);
		presets_table.addMouseListener(this);
		execute_on_select.addChangeListener(this);
		preset_name.addKeyListener(this);
		preset_off.addActionListener(this);
		cue_Go.addActionListener(this);
		cue_next.addActionListener(this);
		cue_prev.addActionListener(this);
		cue_store.addActionListener(this);
		cue_ok.addActionListener(this);
		bypass_go_chk.addChangeListener(this);
	//	new_cue_stack.addActionListener(this);
	//	add_cue.addActionListener(this);
	//	cue_stack_selector.addActionListener(this);
		black_out.addActionListener(this);
		clear.addMouseListener(this);
		
		addWindowListener(this);
		
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
		
//		btnPanTilt = new JButton("Pan/Tilt Control");
//		btnPanTilt.setBounds(808, 0, 146, 75);
//		fixture_control.add(btnPanTilt);
		
//		tglbtnPanTilt = new JToggleButton("Pan/Tilt Control");
//		tglbtnPanTilt.setBounds(793, 87, 161, 75);
//		fixture_control.add(tglbtnPanTilt);
		
//		btnPanTilt.addActionListener(this);
//		
//		pan_tilt_frame.setBounds((int)(screenSize.getWidth()-550)/2, (int)(screenSize.getHeight()-550)/2, 550, 550);
//		pan_tilt_frame.setUndecorated(true);
//		pan_tilt_frame.setOpacity(0.5f);
//		pan_tilt_frame.setAlwaysOnTop(true);
//		pan_tilt_frame.setLayout(new GridBagLayout());
//		
//		pan_tilt_frame.addMouseMotionListener(this);
//		pan_tilt_frame.addMouseListener(this);
//		
//		JLabel pantiltlabel = new JLabel("<html><div style='text-align:center;'>Tilt +<br><br>- Pan &emsp;&emsp;&emsp;&emsp; Pan +<br><br>Tilt -</div></html>");
//		pan_tilt_frame.add(pantiltlabel);
//		
//		KeyboardFocusManager.getCurrentKeyboardFocusManager()
//		  .addKeyEventDispatcher(new KeyEventDispatcher() {
//		      @Override
//		      public boolean dispatchKeyEvent(KeyEvent e) {
//		    	  
//		    	  if(e.getID() == 401){
//
//			    		  if(e.getKeyCode() == 38) {	//Up
//				    		  data[0]+=1;
//				    	  } else if(e.getKeyCode() == 40) {	  //Down
//				    		  data[0]-=1;
//				    	  } else if(e.getKeyCode() == 37) {	 //Left
//				    		  data[0]-=15;
//				    	  } else if(e.getKeyCode() == 39) {	 //Right
//				    		  data[0]+=15;
//				    	  }
//			    		  if(data[0] < 0){
//			    			  data[0] = 0;
//			    		  } else if(data[0] > 255){
//			    			  data[0] = 255;
//			    		  }
//			    		  broadcast(data, false);
//
//		    	  }
//		    	  
//		    	  return true;
//		      }
//		});
		
		initiate();
	}
	
	/**
	 * Range object used in profile creation.
	 */
	public static class Range extends Object implements Serializable{

		private static final long serialVersionUID = 1L;
		
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
	
	/**
	 * Broadcasts given byte array
	 * @param dmxdata data to be broadcasted (size 512).
	 * @param overrideMaster set true to bypass master fader
	 */
	public static void broadcast(int[] dmxdata, boolean bypassMaster) {
		
		liveData = dmxdata;
		
		if(bypassMaster){
			for(int x=0;x<512;x++){
				finaldata[x] = (byte)dmxdata[x];
			}
		} else {
			for(int x=0;x<512;x++){
				if(isChannelDimmer[x]){
					finaldata[x] = (byte)((double)dmxdata[x]/255 * (Integer)master_spinner.getValue());
				} else {
					finaldata[x] = (byte)dmxdata[x];
				}
			}
		}

		if(artnet_node != null && !blackout_on) {
			dmx.setSequenceID(sequenceID % 255);
			dmx.setDMX(finaldata, finaldata.length);
       		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
       		sequenceID++;
        }
	}
	
		/*
		 * Action Performed
		 */
		public void actionPerformed(ActionEvent e){

			if(e.getSource() == saveItem){
				
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
				fc.setFileFilter(new FileNameExtensionFilter("Truss Show File", "truss"));

				if(fc.showOpenDialog(main.this) == JFileChooser.APPROVE_OPTION){
					currently_loaded_show = fc.getSelectedFile();
					saveShow.load(fc.getSelectedFile());
					setTitle("Truss Alpha 1.0 - " + currently_loaded_show.getName());
				}
				
			} 
//			else if(e.getSource() == loadProfileItem){
//				
//				JFileChooser fc = new JFileChooser();
//				fc.setMultiSelectionEnabled(true);
//				fc.setAcceptAllFileFilterUsed(false);
//				fc.setFileFilter(new FileNameExtensionFilter("Truss Profile", "xml"));
//
//				if(fc.showOpenDialog(main.this) == JFileChooser.APPROVE_OPTION){
//					for(File f : fc.getSelectedFiles()){
//						try {
//							Files.copy(f.toPath(), new File(getClass().getResource("resources/profiles/"+f.getName()).getPath()).toPath(), StandardCopyOption.REPLACE_EXISTING);
//						} catch (IOException e1) {
//							e1.printStackTrace();
//						}
//					}
//					JOptionPane.showMessageDialog(this, "Profiles Added Sucessfully. Please restart Truss for new profiles to be visible.");
//				}
//			}
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
					blackout_on = true;
					
					for(int x=0;x<512;x++){
						if(isChannelDimmer[x]){
							blackout[x] = (byte)0;
						} else {
							blackout[x] = (byte)data[x];
						}
					}

					// Broadcast Blackout
					if(artnet_node != null) {
						dmx.setSequenceID(sequenceID % 255);
						dmx.setDMX(blackout, blackout.length);
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
					blackout_on = false;
					
					// Broadcast channel_data
					broadcast(data, false);
					
					blackout_th.stop();
					black_out.setForeground(Color.BLACK);
					black_out.setFont(new Font(null, Font.PLAIN, 13));
				}
				
			} else if(e.getSource() == store_cue_btn){
				
			//	cueStack[cue_stack_selector.getSelectedIndex()].saveToCurrentCue(data);
				
			} 
			
			// Preset action performed
			else if(e.getSource() == assign_current_output){
				
				preset[presets_table.getSelectedRow()] = new Preset(presets_table.getSelectedRow(), preset_name.getText(), Arrays.copyOf(liveData, 512));

				preset_name.setText("");
				
			} else if(e.getSource() == execute_preset){
				
				if(preset[presets_table.getSelectedRow()] != null){
					preset[presets_table.getSelectedRow()].execute();
				}
				
			} else if(e.getSource() == preset_off){
				
				preset_off.setEnabled(false);
				active_preset_lbl.setForeground(new Color(100,100,100));
				active_preset_lbl.setText("No Preset Active");
				
				// Broadcast channel_data
				data = dataBeforePresetCue;
				broadcast(data, false);
				
			} else if(e.getSource() == stop_cue){
				
				stop_cue.setEnabled(false);
				
				// Broadcast channel_data with new master value
				broadcast(data, false);
				
			}
//			else if(e.getSource() == cue_Go){
//				
//				if(cue[current_cue].data != null){
//					cue[current_cue].execute();
//					current_cue_lbl.setForeground(Color.GREEN);
//				}
//				
//			} else if(e.getSource() == cue_next){
//				
//				if(current_cue < 999){
//					current_cue++;
//					if(bypass_go_chk.isSelected()){
//						if(cue[current_cue].data != null){
//							cue[current_cue].execute();
//							current_cue_lbl.setForeground(Color.GREEN);
//						}
//					} else {
//						current_cue_lbl.setForeground(Color.RED);
//					}
//					current_cue_lbl.setText(""+current_cue);
//					cue_name_tf.setText(cue[current_cue].name);
//					
//					if(cue[current_cue].data == null){
//						no_assign_lbl.setVisible(true);
//					} else {
//						no_assign_lbl.setVisible(false);
//					}
//				}
//				
//			} else if(e.getSource() == cue_prev){
//				
//				if(current_cue > 1){
//					current_cue--;
//					if(bypass_go_chk.isSelected()){
//						if(cue[current_cue].data != null){
//							cue[current_cue].execute();
//							current_cue_lbl.setForeground(Color.GREEN);
//						}
//					} else {
//						current_cue_lbl.setForeground(Color.RED);
//					}
//					current_cue_lbl.setText(""+current_cue);
//					cue_name_tf.setText(cue[current_cue].name);
//					
//					if(cue[current_cue].data == null){
//						no_assign_lbl.setVisible(true);
//					} else {
//						no_assign_lbl.setVisible(false);
//					}
//				}
//				
//			} else if(e.getSource() == cue_ok){
//				
//				cue[current_cue].name = cue_name_tf.getText();
//				
//			} else if(e.getSource() == cue_store){
//				
//				cue[current_cue].data = data;
//				
//				no_assign_lbl.setVisible(false);
//				data = null;
//			} 
			else if(e.getSource() == btnDimmer || e.getSource() == btnShutter || e.getSource() == btnIris || e.getSource() == btnFocus || e.getSource() == btnZoom){
				
				if(e.getSource() == btnShutter){
					setSingleFader(ChFn.SHUTTER);
				} else if(e.getSource() == btnDimmer){
					setSingleFader(ChFn.DIMMER);
				} else if(e.getSource() == btnIris){
					setSingleFader(ChFn.IRIS);
				} else if(e.getSource() == btnFocus){
					setSingleFader(ChFn.FOCUS);
				} else if(e.getSource() == btnZoom){
					setSingleFader(ChFn.ZOOM);
				}
				
			} else if(e.getSource() == btnColourWheel || e.getSource() == btnRgbMixing || e.getSource() == btnCto || e.getSource() == btnGobo_1 || e.getSource() == btnGobo_2 || e.getSource() == btnGobo_3 || e.getSource() == btnPrism || e.getSource() == btnFrost || e.getSource() == btnControl || e.getSource() == btnOther){
				setFaderBank((JButton)e.getSource());
			} 
		}
		
		/*
		 * stateChanged
		 */
		
		public void stateChanged(ChangeEvent e){
			if(e.getSource() == master_spinner){
				
				master_slider.setValue((Integer)master_spinner.getValue());
				broadcast(data, false);
				
			} else if(e.getSource() == master_slider){
				
				master_spinner.setValue(master_slider.getValue());
				broadcast(data, false);
				
			} else if(e.getSource() == fade_slider){
				
				fade_val.setText(""+fade_slider.getValue());
				
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
		}
		
		/**
		 * Set function of the single fader to a given function index, uses selectedFixtures to determine names, values etc
		 * @param functionIdx
		 */
		public void setSingleFader(int functionIdx){
			
		//	single.unassign();
			
			// Set name for fader
			single.setName(selectedFixtures[0].getChannelNameByFunctionIndex(functionIdx));
			
			// Create array of all channels to be selected
			int[] channelsToBeSelected = new int[selectedFixturesAmt];
			for(int a=0;a<selectedFixturesAmt;a++){
				channelsToBeSelected[a] = selectedFixtures[a].getUniversalChannelNumberByFunctionIndex(functionIdx);
			}
			
			single.assignChannels(channelsToBeSelected);
			
			if(selectedFixturesAmt == 1){
				single.prev_val = data[selectedFixtures[0].getUniversalChannelNumberByFunctionIndex(functionIdx)-1];
			} else {
				single.prev_val = 0;
			}	
		}
		
		/**
		 * Set function of the fader bank to a given function when button is pressed, uses selectedFixtures to determine names, values etc
		 * @param btn Button Pressed
		 */
		public void setFaderBank(JButton btn){
			
			Fader[] bank_faders = {bank_1, bank_2, bank_3, bank_4, bank_5};
			int[] functionIdxRange = {-1,-1,-1,-1,-1};
			
			if(btn == btnColourWheel){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.CW1;
				}
			} else if(btn == btnRgbMixing){
				for(int a=0;a<=2;a++){
					functionIdxRange[a] = a+ChFn.RED;
				}
			} else if(btn == btnCto){
				functionIdxRange[0] = ChFn.CTO;
			} else if(btn == btnGobo_1){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.GOBO11;
				}
			} else if(btn == btnGobo_2){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.GOBO21;
				}
			} else if(btn == btnGobo_3){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.GOBO31;
				}
			} else if(btn == btnPrism){
				functionIdxRange[0] = ChFn.PRISM;
			} else if(btn == btnFrost){
				functionIdxRange[0] = ChFn.FROST;
			} else if(btn == btnControl){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.CTRL1;
				}
			} else if(btn == btnOther){
				for(int a=0;a<=4;a++){
					functionIdxRange[a] = a+ChFn.OTHER1;
				}
			}
			
			int a = 0;
			for(Fader fader : bank_faders){
				
				if(functionIdxRange[a] != -1 && selectedFixtures[0].getFixtureType().getLocalChannelNumberByFunctionIndex(functionIdxRange[a]) != -1){
					
					// Create array of all channels to be selected
					int[] channelsToBeSelected = new int[selectedFixturesAmt];
					for(int b=0;b<selectedFixturesAmt;b++){
						channelsToBeSelected[b] = selectedFixtures[b].getUniversalChannelNumberByFunctionIndex(functionIdxRange[a]);
					}

					fader.setName(selectedFixtures[0].getChannelNameByFunctionIndex(functionIdxRange[a]));
					fader.assignFixture(selectedFixtures[0]);
					fader.assignChannels(channelsToBeSelected);
					
					if(selectedFixturesAmt == 1){
						fader.prev_val = data[selectedFixtures[0].getUniversalChannelNumberByFunctionIndex(functionIdxRange[a])-1];
					} else {
						fader.prev_val = 0;
					}
				} else {
					fader.unassign();
				}
				a++;
			}
	
		}
		
		/*
		 * Mouse Pressed Event
		 */
		public void mousePressed(MouseEvent e) {
			
			// Patch Table
			if(e.getSource() == patch_table){
				
				group_table.getSelectionModel().clearSelection();
				try {
					
					Profile p = fixture[(Integer)patch_table.getValueAt(patch_table.getSelectedRow(), 0)-1].getFixtureType();
					
					Fixture[] f = new Fixture[patch_table.getSelectedRows().length];
					for(int x=0;x<f.length;x++){
						f[x] = fixture[(Integer)patch_table.getValueAt(patch_table.getSelectedRows()[x], 0)-1];
						if(f[x].getFixtureType() != p){
							return;
						}
					}
					
					selectFixtures(f);
					
					if(f.length == 1){
						cur_sel_id.setText("ID"+f[0].id);
						cur_sel_name.setText(f[0].getName());
					} else {
						cur_sel_id.setText("ID--");
						cur_sel_name.setText(selectedFixturesAmt + " Selected");
					}
					
				} catch(NullPointerException n){
					return;
				}
			
			group_btn.setEnabled(true);
				
			// Group Table
			} else if(e.getSource() == group_table){
				
				patch_table.getSelectionModel().clearSelection();
				try {
					
					selectFixtures(group[group_table.getSelectedRow()+1].getMembers());

					cur_sel_id.setText("GRP");
					cur_sel_name.setText(group[group_table.getSelectedRow()+1].getName());
					
				} catch(NullPointerException n){
					return;
				}
			}		
		}
		
		/*
		 * List Selection Listener for Tables
		 */
		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting()){

					assign_current_output.setEnabled(true);
					
					if(preset[presets_table.getSelectedRow()] != null){
						preset_name.setText((String)presets_table.getValueAt(presets_table.getSelectedRow(), 0));
					}
					if(preset[presets_table.getSelectedRow()] != null && execute_on_select.isSelected()){
						preset[presets_table.getSelectedRow()].execute();
					}
			}
		}
		
		public void selectFixtures(Fixture[] f){

			Profile prof = f[0].getFixtureType();
			selectedFixtures = f;
			selectedFixturesAmt = f.length;
			
			clearSelection();
			
			// Assign all faders the fixtures they are controlling.
			Fader[] faders = {single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt};
						
			for(Fader fader : faders){
				fader.assignFixture(f[0]);
			}
			
			// Check if the selected fixture/s have a said functions, enable accordingly
			if(selectedFixtures[0] != null){
				
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.DIMMER) != -1){
					setSingleFader(1);
					btnDimmer.setEnabled(true);
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.SHUTTER) != -1){
					btnShutter.setEnabled(true);
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.IRIS) != -1){
					btnIris.setEnabled(true);
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.FOCUS) != -1){
					btnFocus.setEnabled(true);
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.ZOOM) != -1){
					btnZoom.setEnabled(true);
				}
				for(int x=ChFn.CW1;x<=ChFn.CW5;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnColourWheel.setEnabled(true);
					}
				}
				for(int x=ChFn.RED;x<=ChFn.BLUE;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						setFaderBank(btnRgbMixing);
						btnRgbMixing.setEnabled(true);
					}
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.CTO) != -1){
					btnCto.setEnabled(true);
				}
				for(int x=ChFn.GOBO11;x<=ChFn.GOBO15;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnGobo_1.setEnabled(true);
					}
				}
				for(int x=ChFn.GOBO21;x<=ChFn.GOBO25;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnGobo_2.setEnabled(true);
					}
				}
				for(int x=ChFn.GOBO31;x<=ChFn.GOBO35;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnGobo_3.setEnabled(true);
					}
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.PRISM) != -1){
					btnPrism.setEnabled(true);
				}
				if(prof.getLocalChannelNumberByFunctionIndex(ChFn.FROST) != -1){
					btnFrost.setEnabled(true);
				}
				for(int x=ChFn.CTRL1;x<=ChFn.CTRL5;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnControl.setEnabled(true);
					}
				}
				for(int x=ChFn.OTHER1;x<=ChFn.OTHER5;x++){
					if(prof.getLocalChannelNumberByFunctionIndex(x) != -1){
						btnOther.setEnabled(true);
					}
				}
			}
			
			// Check if the selected fixture/s have pan and/or tilt functions, enable accordingly
			int a = ChFn.PAN;
			for(Fader fader : new Fader[]{pan, tilt}){
				
				if(prof.getLocalChannelNumberByFunctionIndex(a) != -1){
					
					// Create array of all channels to be selected
					int[] channelsToBeSelected = new int[selectedFixturesAmt];
					for(int b=0;b<selectedFixturesAmt;b++){
						channelsToBeSelected[b] = selectedFixtures[b].getUniversalChannelNumberByFunctionIndex(a);
					}

					fader.setName(selectedFixtures[0].getChannelNameByFunctionIndex(a));
					fader.assignFixture(selectedFixtures[0]);
					fader.assignChannels(channelsToBeSelected);
					
					if(selectedFixturesAmt == 1){
						single.prev_val = data[selectedFixtures[0].getUniversalChannelNumberByFunctionIndex(a)-1];
					} else {
						single.prev_val = 0;
					}
				}			
				a++;
			}
		}
		
		public void clearSelection(){
			
			// Begin by disabling all buttons
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
			
			cur_sel_id.setText("ID--");
			cur_sel_name.setText("-");
			
			Fader[] faders = {single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt};
				
			for(Fader fader : faders){
				fader.unassign();
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == clear){
				if(e.getClickCount() == 1){
					clearSelection();
				} else if(e.getClickCount() == 2){
					clearSelection();
					
					Arrays.fill(data, 0);
					 
					// Broadcast Blackout
					broadcast(data, false);
				}
				group_table.getSelectionModel().clearSelection();
				patch_table.getSelectionModel().clearSelection();
			} 
		}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		@Override
		public void discoveredNewNode(ArtNetNode node) {
			if(artnet_node == null){
				artnet_node = node;
				dmx.setUniverse(artnet_node.getSubNet(), artnet_node.getDmxOuts()[0]);
				error_disp.setForeground(Color.BLUE);
				error_disp.setText("Node discovered on: " + artnet_node.getIPAddress().toString().split("/")[1]);
			}
		}

		@Override
		public void discoveredNodeDisconnected(ArtNetNode node) {
			error_disp.setForeground(Color.RED);
			error_disp.setText("! Node on " + node.getIPAddress().toString().split("/")[1] + " Disconnected");
			if(artnet_node == node){
				artnet_node = null;
			}
		}

		@Override
		public void discoveryCompleted(List<ArtNetNode> nodes) {
			if(nodes.size() == 0){
				error_disp.setForeground(Color.RED);
				error_disp.setText("! No ArtNet Nodes Discovered");
			}
		}

		@Override
		public void discoveryFailed(Throwable t) {
			error_disp.setForeground(Color.RED);
			error_disp.setText("! Discovery Failed");
		}

		public void keyTyped(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {
			
//			int xdiff = e.getX()-xOld;
//			int ydiff = -(e.getY()-yOld);
//			
//			xOld = e.getX();
//			yOld = e.getY();
//			
//			System.out.println(e.getXOnScreen()+","+ pan_tilt_frame.getX());
//			
//				panTiltRobot.mouseMove(pan_tilt_frame.getX()+275, pan_tilt_frame.getY()+275);
//			
//			if(e.getYOnScreen() <= pan_tilt_frame.getY()+25){
//				panTiltRobot.mouseMove(e.getXOnScreen(), pan_tilt_frame.getY()+26);
//			}
//			if(e.getYOnScreen() >= pan_tilt_frame.getY()+525){
//				panTiltRobot.mouseMove(e.getXOnScreen(), pan_tilt_frame.getY()+524);
//			}
//			
//			for(int a=0;a<selectedFixturesAmt;a++){
//				
//				int panChannel = selectedFixtures[a].getUniversalChannelNumberByFunctionIndex(5);
//				int tiltChannel = selectedFixtures[a].getUniversalChannelNumberByFunctionIndex(6);
//					
//				int newPan = data[panChannel] + xdiff;
//				int newTilt = data[tiltChannel] + ydiff;
//					
//				//	System.out.println(newPan + ", " + newTilt);
//					
//				if(newPan < 256 && newPan > -1){
//					data[panChannel] = newPan;
//				} 
//					
//				if(newTilt < 256 && newTilt > -1){
//					data[tiltChannel] = newTilt;
//				} 
//
//			}
//			// Broadcast
//			broadcast(data, false);
			
		}

		public void windowOpened(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			
			if(!saveShow.isSaved){
				
				int operation = JOptionPane.showOptionDialog(this, "Would you like to save before exiting?", 
		    			   "Show has not been saved", JOptionPane.YES_NO_CANCEL_OPTION, 
		    			   JOptionPane.WARNING_MESSAGE, null, 
		    			   new String[]{"Yes", "No", "Cancel"}, "Yes");

							switch(operation){
								case JOptionPane.YES_OPTION: {

											if(currently_loaded_show == null){

												JFileChooser fc = new JFileChooser();
												fc.setSelectedFile(new File("show.truss"));

												if(fc.showSaveDialog(main.this) == JFileChooser.APPROVE_OPTION){
													saveShow.save(fc.getSelectedFile());
												}

											} else {
												saveShow.save(currently_loaded_show);
											}

								}
								case JOptionPane.NO_OPTION: System.exit(0);
							}
				
			} else {
				System.exit(0);
			}
			
		}
}