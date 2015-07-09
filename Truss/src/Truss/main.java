package Truss;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.MaskFormatter;

import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.net.BindException;
import java.util.List;

import artnet4j.ArtNet;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

import javax.swing.border.EmptyBorder;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class main extends JFrame implements ActionListener, ChangeListener, MouseListener, ArtNetDiscoveryListener, KeyListener, WindowListener {
	
//	private static final long serialVersionUID = 457254905222613447L;
	static int[] channel_data = new int[513];
	int[] prev_channel_data = new int[513];
	byte[] blackout = new byte[512];
	byte[] current_output = new byte[512];
	static Object[][] fixture_data = new Object[6][7];
	static Object[][] dimmer_data = new Object[6][7];
	static Object[][] sequence_data = new Object[6][7];
	static Fixture[] fixture = new Fixture[513];
	static Dimmer[] dimmer = new  Dimmer[513];
	static Cue[] cue = new Cue[1000];
	static Profile[] profile = new Profile[100];
	static Group[] group = new Group[513];
	static byte[] data = new byte[512];
	static int channel_amt = 0, selectedFixtures_amt = 0, current_cue = 1, activeCue = 0, cueStackCounter = 0, fixture_select_btn_counter = 0, fixtureNumber = 1, dimmerNumber = 1, profileID = 0, group_counter = 1, sequenceID = 0;
	static JPanel fixture_select, control, fixture_sel_panel, group_sel_panel;
	static Fixture[] selectedFixtures = new Fixture[512];
	static File currently_loaded_show;
	static Vector<String> groupNames = new Vector<String>();
	static Object selectedFixture = null;
	
	static JButton bank_page_up, bank_page_down, slct_fix, slct_dim, slct_seq, cue_Go, cue_next, cue_prev, cue_store, cue_ok, group_btn, store_cue_btn, remote_btn, add_cue, black_out, new_cue_stack, load_show, next_cue, prev_cue, new_fixture, edit_fixture, clear_sel, save_show;
	static JButton btnFocus, btnDimmer, btnIris, btnShutter, btnZoom, btnColourWheel, btnRgbMixing, btnCto, btnGobo_1, btnGobo_2, btnGobo_3, btnPrism, btnFrost, btnControl, btnOther;
	JSlider  master_slider, fade_slider, intensity_fader;
	static JTextField cue_name_tf;
	static JTextField hold_for_tf;
	JPanel patch_and_control, contentPane, fw, presets;
	JTable fixture_table, presets_grid, group_table, dimmer_table, sequence_table;
	JScrollPane patch_table_pane, group_table_pane;
	JTabbedPane fixture_sel_and_ctrl, screens;
	JCheckBox execute_on_select, bypass_go_chk, hold_for_chk;
	static JLabel bank_page_lbl, no_assign_lbl, cur_sel_id, cur_sel_name, cur_sel_type, error_disp, fade_val, current_cue_lbl, nextCueLbl, prevCueLbl, inTimeLbl, holdTimeLbl;
	JSpinner cue_counter, master_spinner, intensity_spinner;
	static Fader single, bank_1, bank_2, bank_3, bank_4, bank_5, pan, tilt;
	static ArtNetNode artnet_node;
	static ArtNet artnet = new ArtNet();
	static ArtDmxPacket dmx = new ArtDmxPacket();
	
	JPopupMenu fixture_menu, dimmer_menu, sequence_menu;
	
	Thread clear_flash, blackout_th;
	JToggleButton selectedFixture_btn;
	FileOutputStream file_stream_out;
	Properties show_settings = new Properties();
	FileNameExtensionFilter load_show_filter;
	Vector cueStackNames = new Vector();
	static boolean blackout_on = false;
	boolean artnet_con = false;
	
	final String[] channels = {"Dimmer", "Shutter", "Iris", "Focus", "Zoom", "Pan", "Tilt", "Colour Wheel", 
			   "Colour Wheel (Fine)", "Red", "Red (Fine)", "Green", "Green (Fine)", "Blue", "Blue (Fine)",
			   "Cyan", "Cyan (Fine)", "Magenta", "Magenta (Fine)", "Yellow", "Yellow (Fine)", "CTO",
			   "CTO (Fine)"};
	
	JMenuItem saveItem, loadItem, fixtureItem, dimmerItem, aboutItem, patch_newFixture, patch_newDimmer, patch_newSequence, settingsItem;
	
//	Settings settings = new Settings();
	
	SAXParserFactory factory = SAXParserFactory.newInstance();
	private JLabel lblCurrentTheFirst;
//	DefaultHandler handler;
	
	/*
	 * Called after all components are initialized
	 */
	public void initiate() {
		
		for(int a=0;a<999;a++){
			cue[a+1] = new Cue(null, "New Cue", 0, 0, (a+1));
		}

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
		
		/*
		 * Loads all xml profiles located in the 'xml' folder in the root directory
		 */
		File f = new File("xml");
		for(int v=0;v<f.list().length;v++){
			loadProfile(f.list()[v].substring(0, f.list()[v].length()-4));
		}

		load_show_filter = new FileNameExtensionFilter("Truss Show File", "truss");
	}  

	public main() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}; 
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(50, 50, 1290, 680);
		setMinimumSize(new Dimension(1280, 710));
		patch_and_control = new JPanel();
		patch_and_control.setBounds(6, 0, 974, 660);
		patch_and_control.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorderPainted(false);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
			saveItem = new JMenuItem("Save");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			fileMenu.add(saveItem);
			
			loadItem = new JMenuItem("Load");
			loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			fileMenu.add(loadItem);
		
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		
			aboutItem = new JMenuItem("About");
			aboutMenu.add(aboutItem);
			
		JMenu settingsMenu = new JMenu("Settings");
		menuBar.add(settingsMenu);
		
			settingsItem = new JMenuItem("Settings");
			settingsItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
//					settings.setVisible(!settings.isVisible());
				}
			});
			settingsMenu.add(settingsItem);
			
		setJMenuBar(menuBar);

		fw = new JPanel();

		presets = new JPanel();

		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.add(patch_and_control);

		setContentPane(contentPane);
		setTitle("Truss, Alpha 1.0");
		setResizable(false); 
		
		JPanel menu_panel = new JPanel();
		menu_panel.setBounds(0, 0, 1284, 45);
		menu_panel.setLayout(null);
		
		JPanel main_controls_panel = new JPanel();
		main_controls_panel.setBounds(980, 0, 294, 660);
		menu_panel.setLayout(null);
		contentPane.add(main_controls_panel);
		
		// Patch and Control Screen
		
		slct_seq = new JButton("Sequences");
		slct_seq.setEnabled(false);
		slct_seq.setBounds(885, 311, 89, 23);
		patch_and_control.add(slct_seq);
		
		slct_fix = new JButton("Fixtures"); 
		slct_fix.setFocusPainted(false);
		slct_fix.setBounds(786, 311, 89, 23);
		patch_and_control.add(slct_fix);
		
		slct_dim = new JButton("Dimmers");
		slct_dim.setFocusPainted(false);
		slct_dim.setBounds(687, 311, 89, 23);
		slct_dim.setForeground(Color.BLUE);
		patch_and_control.add(slct_dim);
		
		fixture_menu = new JPopupMenu();		
		patch_newFixture = new JMenuItem("New Fixture");
		fixture_menu.add(patch_newFixture);
		
		dimmer_menu = new JPopupMenu();		
		patch_newDimmer = new JMenuItem("New Dimmer");
		dimmer_menu.add(patch_newDimmer);
		
		sequence_menu = new JPopupMenu();		
		patch_newSequence = new JMenuItem("New Sequence");
		sequence_menu.add(patch_newSequence);
		
		/*
		 * Create dimmer, fixture and sequence table
		 */
			createTables();
		
		patch_table_pane = new JScrollPane(dimmer_table);
		patch_table_pane.setColumnHeaderView(null);
		patch_table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		patch_table_pane.setBounds(0, 0, 975, 300);
		patch_and_control.add(patch_table_pane);
		
		JButton open_console = new JButton();
		open_console.setBounds(80, 10, 25, 25);
		open_console.setIcon(new ImageIcon("src/img/console.png"));
		open_console.setBorder(new EmptyBorder(0,0,0,0));
		open_console.setContentAreaFilled(false);
		open_console.setFocusPainted(false);
		menu_panel.add(open_console);
		
			// Fixture Control
		
			Color bg = new Color(238, 238, 238);
		
			JPanel fixture_control = new JPanel();
			fixture_control.setBackground(bg);
			fixture_control.setLayout(null);
	//		fixture_control.setBackground(new Color(130, 130, 130));
			fixture_control.setBounds(10, 345, 964, 315);
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
			pan.create(0, fixture_control, bg, 620, 0);
			
			tilt = new Fader();
			tilt.create(0, fixture_control, bg, 710, 0);
			
			btnDimmer = new JButton("Dimmer");
			btnDimmer.setFocusable(false);
			btnDimmer.setBounds(90, 0, 75, 39);
			fixture_control.add(btnDimmer);
			
			btnFocus = new JButton("Focus");
			btnFocus.setFocusable(false);
			btnFocus.setBounds(90, 50, 75, 39);
			fixture_control.add(btnFocus);
			
			btnIris = new JButton("Iris");
			btnIris.setFocusable(false);
			btnIris.setBounds(90, 100, 75, 39);
			fixture_control.add(btnIris);
			
			btnShutter = new JButton("Shutter");
			btnShutter.setFocusable(false);
			btnShutter.setBounds(90, 150, 75, 39);
			fixture_control.add(btnShutter);
			
			btnZoom = new JButton("Zoom");
			btnZoom.setFocusable(false);
			btnZoom.setBounds(90, 200, 75, 39);
			fixture_control.add(btnZoom);
			
			btnColourWheel = new JButton("<html>Colour<br/>Wheel</html>");
			btnColourWheel.setFocusable(false);
			btnColourWheel.setBounds(800, 0, 85, 39);
			fixture_control.add(btnColourWheel);
			
			btnRgbMixing = new JButton("RGB Mixing");
			btnRgbMixing.setFocusable(false);
			btnRgbMixing.setBounds(800, 50, 85, 39);
			fixture_control.add(btnRgbMixing);
			
			btnCto = new JButton("CTO");
			btnCto.setFocusable(false);
			btnCto.setBounds(800, 100, 85, 39);
			fixture_control.add(btnCto);
			
			btnGobo_1 = new JButton("Gobo 1");
			btnGobo_1.setFocusable(false);
			btnGobo_1.setBounds(800, 150, 85, 39);
			fixture_control.add(btnGobo_1);
			
			btnGobo_2 = new JButton("Gobo 2");
			btnGobo_2.setFocusable(false);
			btnGobo_2.setBounds(800, 200, 85, 39);
			fixture_control.add(btnGobo_2);
			
			btnGobo_3 = new JButton("Gobo 3");
			btnGobo_3.setFocusable(false);
			btnGobo_3.setBounds(800, 250, 85, 39);
			fixture_control.add(btnGobo_3);
			
			btnPrism = new JButton("Prism");
			btnPrism.setFocusable(false);
			btnPrism.setBounds(895, 0, 67, 39);
			fixture_control.add(btnPrism);
			
			btnFrost = new JButton("Frost");
			btnFrost.setFocusable(false);
			btnFrost.setBounds(895, 50, 67, 39);
			fixture_control.add(btnFrost);
			
			btnControl = new JButton("Control");
			btnControl.setFocusable(false);
			btnControl.setBounds(895, 100, 67, 39);
			fixture_control.add(btnControl);
			
			btnOther = new JButton("Other");
			btnOther.setFocusable(false);
			btnOther.setBounds(895, 150, 67, 39);
			fixture_control.add(btnOther);	

		main_controls_panel.setLayout(null);
		
		clear_sel = new JButton("Clear");
		clear_sel.setFocusable(false);
		clear_sel.setBounds(64, 561, 65, 23);
		clear_sel.setEnabled(false);
		main_controls_panel.add(clear_sel);
		
		master_slider = new JSlider(0, 100, 0);
		master_slider.setFocusable(false);
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
		
		JLabel lblMaster = new JLabel("Master (%)", SwingConstants.CENTER);
		lblMaster.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblMaster.setBounds(229, 333, 65, 16);
		main_controls_panel.add(lblMaster);
		
		fade_slider = new JSlider(0, 10000, 0);
		fade_slider.setFocusable(false);
		fade_slider.setMinorTickSpacing(100);
	//	fade_slider.setBackground(new Color(130, 130, 130));
		fade_slider.setSnapToTicks(true);
		fade_slider.setOrientation(SwingConstants.VERTICAL);
		fade_slider.setBounds(139, 360, 80, 224);
		main_controls_panel.add(fade_slider);
		
		fade_val = new JLabel("0", SwingConstants.CENTER);
		fade_val.setBounds(139, 595, 80, 28);
		main_controls_panel.add(fade_val);
		
		JLabel lblFade = new JLabel("Fade (ms)", SwingConstants.CENTER);
		lblFade.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblFade.setBounds(139, 333, 80, 16);
		main_controls_panel.add(lblFade);
		
		/*
		 * Cue Panel
		 */
		
		JPanel cue_panel = new JPanel();
		cue_panel.setBounds(10, 0, 281, 322);
	//	cue_panel.setBackground(new Color(120, 120, 120));
		main_controls_panel.add(cue_panel);
		cue_panel.setLayout(null);
		
		cue_Go = new JButton("GO");
		cue_Go.setFocusable(false);
		cue_Go.setFont(new Font("Tahoma", Font.BOLD, 12));
		cue_Go.setBounds(10, 102, 89, 36);
		cue_panel.add(cue_Go);
		
		current_cue_lbl = new JLabel("1", SwingConstants.CENTER);
		current_cue_lbl.setForeground(Color.RED);
		current_cue_lbl.setFont(new Font("Tahoma", Font.PLAIN, 50));
		current_cue_lbl.setBounds(10, 10, 89, 81);
		cue_panel.add(current_cue_lbl);
		
		cue_next = new JButton("+ Cue");
		cue_next.setFocusable(false);
		cue_next.setBounds(109, 10, 63, 36);
		cue_panel.add(cue_next);
		
		cue_prev = new JButton("- Cue");
		cue_prev.setFocusable(false);
		cue_prev.setBounds(109, 52, 63, 36);
		cue_panel.add(cue_prev);
		
		bypass_go_chk = new JCheckBox("Bypass Go");
		bypass_go_chk.setFocusable(false);
		bypass_go_chk.setBounds(178, 10, 97, 23);
		cue_panel.add(bypass_go_chk);
		
		cue_store = new JButton("Store");
		cue_store.setFocusable(false);
		cue_store.setBounds(182, 52, 89, 36);
		cue_panel.add(cue_store);
		
		cue_name_tf = new JTextField("New Cue");
		cue_name_tf.setBounds(109, 110, 162, 23);
		cue_panel.add(cue_name_tf);
		cue_name_tf.setColumns(10);
		
		no_assign_lbl = new JLabel("No Assign");
		no_assign_lbl.setBounds(182, 32, 93, 14);
		cue_panel.add(no_assign_lbl);
		
		hold_for_chk = new JCheckBox("Hold for");
		hold_for_chk.setFocusPainted(false);
		hold_for_chk.setBounds(148, 147, 63, 14);
		cue_panel.add(hold_for_chk);
		
		MaskFormatter time = null;
		try {
			time = new MaskFormatter("##:##:##");
			time.setPlaceholder("00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		hold_for_tf = new JFormattedTextField(time);
		hold_for_tf.setEnabled(false);
		hold_for_tf.setBounds(211, 144, 60, 20);
		cue_panel.add(hold_for_tf);
		hold_for_tf.setColumns(10);
		
		nextCueLbl = new JLabel("<html><i>Next:</i> &emsp;&nbsp;&nbsp;&nbsp; - &emsp (-)</html>");
		nextCueLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		nextCueLbl.setBounds(10, 250, 265, 14);
		cue_panel.add(nextCueLbl);
		
		lblCurrentTheFirst = new JLabel("<html><b>Current Cue:</b>&emsp;IN &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;HOLD</html>");
		lblCurrentTheFirst.setBounds(10, 191, 265, 14);
		cue_panel.add(lblCurrentTheFirst);
		
		prevCueLbl = new JLabel("<html><i>Previous:</i> &nbsp; - &emsp; (-)</html>");
		prevCueLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		prevCueLbl.setBounds(10, 275, 261, 14);
		cue_panel.add(prevCueLbl);
		
		inTimeLbl = new JLabel("00:00:00");
		inTimeLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		inTimeLbl.setBounds(91, 211, 72, 14);
		cue_panel.add(inTimeLbl);
		
		holdTimeLbl = new JLabel("00:00:00");
		holdTimeLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		holdTimeLbl.setBounds(181, 211, 89, 14);
		cue_panel.add(holdTimeLbl);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 176, 265, 2);
		cue_panel.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 237, 265, 2);
		cue_panel.add(separator_1);
		
		// End Cue Panel

			black_out = new JButton("B.O");
			black_out.setFocusable(false);
			black_out.setBounds(64, 591, 65, 32);
			main_controls_panel.add(black_out);
			
			error_disp = new JLabel("", SwingConstants.RIGHT);
			error_disp.setBounds(10, 634, 284, 15);
			main_controls_panel.add(error_disp);
			
			error_disp.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			error_disp.setForeground(Color.RED);
			
			remote_btn = new JButton("Sync");
			remote_btn.setBounds(64, 527, 65, 23);
			main_controls_panel.add(remote_btn);
			remote_btn.setVisible(false);
			remote_btn.setEnabled(false);
			
			cue_ok = new JButton("OK");
			cue_ok.setBounds(35, 366, 46, 56);
//			main_controls_panel.add(cue_ok);
			cue_ok.setFocusable(false);
			cue_ok.addActionListener(this);
			black_out.addActionListener(this);
		menu_panel.setLayout(null);
		
		bank_page_up = new JButton("Page +");
		bank_page_up.setEnabled(false);
		bank_page_up.setFocusable(false);
		bank_page_up.setBounds(895, 200, 67, 23);
		fixture_control.add(bank_page_up);
		
		bank_page_down = new JButton("Page -");
		bank_page_down.setEnabled(false);
		bank_page_down.setFocusable(false);
		bank_page_down.setBounds(895, 266, 67, 23);
		fixture_control.add(bank_page_down);
		
		bank_page_lbl = new JLabel("1", SwingConstants.CENTER);
		bank_page_lbl.setFont(new Font("Tahoma", Font.PLAIN, 28));
		bank_page_lbl.setBounds(895, 234, 67, 21);
		fixture_control.add(bank_page_lbl);

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
		clear_sel.addActionListener(this);
		master_slider.addChangeListener(this);
		master_spinner.addChangeListener(this);
		fade_slider.addChangeListener(this);
		cue_Go.addActionListener(this);
		cue_next.addActionListener(this);
		cue_prev.addActionListener(this);
		cue_store.addActionListener(this);
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
		hold_for_chk.addActionListener(this);
		
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
		
		cur_sel_id = new JLabel("ID--");
		cur_sel_id.setBounds(10, 314, 30, 14);
		patch_and_control.add(cur_sel_id);
		
		cur_sel_name = new JLabel("-");
		cur_sel_name.setBounds(50, 311, 130, 18);
		patch_and_control.add(cur_sel_name);
		cur_sel_name.setFont(new Font("Lucida Grande", Font.ITALIC, 13));
		
		cur_sel_type = new JLabel("-");
		cur_sel_type.setBounds(190, 314, 105, 14);
		patch_and_control.add(cur_sel_type);
		
		addWindowListener(this);
		
		initiate();
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

	// Returns a profile given its string name
	public static Profile getProfileByName(String name){
		for(Profile p : profile){
			
			if(p != null && p.getFullName().equals(name)){
				return p;
			}
			
		}
		return null;
	}
	
		public void actionPerformed(ActionEvent e){

			if(e.getSource() == clear_sel){
					
					FixtureSelectionEngine.clearSelection();
					
					cur_sel_id.setText("ID--");
					cur_sel_name.setText("-");
					cur_sel_type.setText("-");

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

				if(fc.showOpenDialog(main.this) == JFileChooser.APPROVE_OPTION){
					currently_loaded_show = fc.getSelectedFile();
					saveShow.load(fc.getSelectedFile());
					setTitle("Truss Alpha 1.0 - " + currently_loaded_show.getName());
				}
				
			} 

			else if(e.getSource() == black_out){
				
				if(!blackout_on){

					blackout_on = true;
					setMaster(0);
					
					// Broadcast Blackout
//					if(artnet_node != null) {
//						dmx.setSequenceID(sequenceID % 255);
//						dmx.setDMX(blackout, 512);
//		           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
//		           		sequenceID++;
//		            }
					
					blackout_th = new Thread(){
						int count = 0;
						public void run(){
							black_out.setForeground(Color.BLUE);
							while(true){
								black_out.setFont(new Font(null, Font.BOLD, 11));
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {e.printStackTrace();}
								black_out.setFont(new Font(null, Font.PLAIN, 11));
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {e.printStackTrace();}
							}
						};
					};
					blackout_th.start();
				} else {

					blackout_on = false;
					setMaster((Integer)master_spinner.getValue());
					
//					for(int a=0;a<512;a++){
//						data[a] = (byte)(((double)channel_data[a+1] / 255) * (Integer)master_spinner.getValue());
					//	data[a] = (byte)channel_data[a+1];
			//		}
					
					// Broadcast channel_data
//					if(artnet_node != null && !blackout_on) {
//						dmx.setSequenceID(sequenceID % 255);
//						dmx.setDMX(data, 512);
//		           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
//		           		sequenceID++;
//		            }
					
					blackout_th.stop();
					black_out.setForeground(Color.BLACK);
					black_out.setFont(new Font(null, Font.PLAIN, 11));
				}
				
			// Executes the current cue
			} else if(e.getSource() == cue_Go){
				
				if(cue[current_cue].data != null){
					
					CueEngine.execute(cue[current_cue]);
					
				}
			
			// Goes to the next Cue
			} else if(e.getSource() == cue_next){
				
				if(current_cue < 999){
					current_cue++;
					cue[current_cue-1].name = cue_name_tf.getText();
					current_cue_lbl.setText(""+current_cue);
					cue_name_tf.setText(main.cue[current_cue].name);
					
					if(bypass_go_chk.isSelected()){
						if(cue[current_cue].data != null){
							CueEngine.execute(cue[current_cue]);
						}
					} else {
						if(Integer.parseInt(current_cue_lbl.getText()) == activeCue){
							current_cue_lbl.setForeground(Color.GREEN);
						} else {
							current_cue_lbl.setForeground(Color.RED);
						}
					}
					
				}

				if(current_cue != 1){
					cue[current_cue-1].holdTime = CueEngine.convertStringToLong(hold_for_tf.getText());
				}
				CueEngine.resetCueDisplay();
			
			// Goes to the previous Cue
			} else if(e.getSource() == cue_prev){
				
				if(current_cue > 1){
					current_cue--;
					cue[current_cue+1].name = cue_name_tf.getText();
					current_cue_lbl.setText(""+current_cue);
					cue_name_tf.setText(main.cue[current_cue].name);
					
					if(bypass_go_chk.isSelected()){
						if(cue[current_cue].data != null){
							CueEngine.execute(cue[current_cue]);
						}
					} else {
						if(Integer.parseInt(current_cue_lbl.getText()) == activeCue){
							current_cue_lbl.setForeground(Color.GREEN);
						} else {
							current_cue_lbl.setForeground(Color.RED);
						}
					}

				}
				cue[current_cue+1].holdTime = CueEngine.convertStringToLong(hold_for_tf.getText());
				CueEngine.resetCueDisplay();
				
			} else if(e.getSource() == cue_store){
				
				int[] data = new int[512];
				
				for(int a=0;a<512;a++){
					data[a] = (int)(channel_data[a+1] * ((double)(Integer)master_spinner.getValue()/100));
				}
				cue[current_cue].data = data;
				cue[current_cue].inTime = fade_slider.getValue();
				cue[current_cue].holdTime = CueEngine.convertStringToLong(hold_for_tf.getText());
				
				no_assign_lbl.setVisible(false);
				data = null;
				
			} else if(e.getSource() == hold_for_chk){
				
				hold_for_tf.setEnabled(hold_for_chk.isSelected());
				
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
			
			if(e.getSource() == master_spinner){
				
				master_slider.setValue((Integer)master_spinner.getValue());
				setMaster((Integer)master_spinner.getValue());
				
			} else if(e.getSource() == master_slider){
				
				master_spinner.setValue(master_slider.getValue());
				setMaster((Integer)master_spinner.getValue());
				
			} else if(e.getSource() == fade_slider){
				
				fade_val.setText(""+fade_slider.getValue());
				
			} else if(e.getSource() == bypass_go_chk){
				
				if(bypass_go_chk.isSelected()){
					cue_Go.setEnabled(false);
				} else {
					cue_Go.setEnabled(true);
				}
				
			}
		}
		
		public void setMaster(int val){
			
			for(int a=0;a<512;a++){
				
				if(fixture[a+1] != null){
					if((fixture[a+1].getStartChannel()+fixture[a+1].getFixtureType().channel_function[0])-1 == (a+1)){
						data[a] = (byte)((double)channel_data[a+1] * (val/100));
					} else {
						data[a] = (byte)(double)channel_data[a+1];
					}
				} else {
					data[a] = (byte)(double)channel_data[a+1];
				}
				if(dimmer[a+1] != null){
					data[a] = (byte)((double)channel_data[a+1] * (val/100));
				} else {
					data[a] = (byte)(double)channel_data[a+1];
				}

			}
			
			// Broadcast channel_data with new master value
			if(artnet_node != null && !blackout_on) {
				dmx.setSequenceID(sequenceID % 255);
				dmx.setDMX(data, data.length);
           		artnet.unicastPacket(dmx, artnet_node.getIPAddress());
           		sequenceID++;
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
				clear_sel.setEnabled(true);
				return;
				
			} 
//			else if(e.getSource() == group_table){
//	
//					selectedFixture = group[group_table.getSelectedRow()+1];					
//
//				return;
//				
//			} 
			else if(e.getSource() == dimmer_table){
				
				dimmer_table.setColumnSelectionInterval(e.getX()/137, e.getX()/137);
				dimmer_table.setRowSelectionInterval(e.getY()/50, e.getY()/50);
				
				try {
					String[] s = (dimmer_table.getValueAt(dimmer_table.getSelectedRow(), dimmer_table.getSelectedColumn())).toString().split(" ");
					FixtureSelectionEngine.selectDimmers(dimmer[Integer.parseInt(s[s.length-1].split("<")[0])]);
					
				} catch(NullPointerException npe){
					return;
				}
				clear_sel.setEnabled(true);
				return;
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
				error_disp.setForeground(Color.BLUE);
				error_disp.setText("Node connected on: " + artnet_node.getIPAddress().toString().split("/")[1]);
			} else {
				error_disp.setForeground(Color.ORANGE);
				error_disp.setText("2+ nodes found, using first [Settings] to swap");
			}
//			settings.updateNodeList(node, Operation.NODE_DISCOVERED);
		}

		@Override
		public void discoveredNodeDisconnected(ArtNetNode node) {
			error_disp.setForeground(Color.RED);
			error_disp.setText("! Node on " + node.getIPAddress().toString().split("/")[1] + " Disconnected");
			if(artnet_node == node){
				artnet_node = null;
			}
//			settings.updateNodeList(node, Operation.NODE_DISCONECCTED);
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
		
		public void loadProfile(String name){

			channel_amt = 0;
			final String fullname = name;
			final String profile_manu = name.split("-")[0].replace("_", " ");
			final String profile_mode = name.split("@")[1].replace("_", " ");
			final String profile_name = name.split("-")[1].split("@")[0].replace("_", " "); 
			
			SAXParser parser;
			try {
				parser = factory.newSAXParser();
				parser.parse("xml/"+name+".xml", new DefaultHandler(){
					
					String channel_name = null, channel_func_name = null;
					Vector<ProfileChannel> profile_channels = new Vector<ProfileChannel>();
					int[] profile_channel_function = new int[51];
					Vector<Range> profile_channel = null;
					ProfileChannel channel_func = null;

					public void startElement(String uri, String localname, String name, Attributes attributes) throws SAXException {

						if(name == "channel"){
								
							profile_channel = new Vector<Range>();
							channel_name = attributes.getValue(0);
							channel_func_name = attributes.getValue(0);
								
							if(Arrays.asList(channels).indexOf(attributes.getValue(1)) != -1){
								profile_channel_function[Arrays.asList(channels).indexOf(attributes.getValue(1))] = channel_amt;
							}
							channel_amt++;
								
						} else if(name == "range"){
							profile_channel.addElement(new Range(Integer.parseInt(attributes.getValue(0)), Integer.parseInt(attributes.getValue(1)), attributes.getValue(2)));
						}
						
					}		
					public void endElement(String uri, String localname, String name) throws SAXException {
						
						if(name == "fixture"){
							profile[profileID] = new Profile(profile_name, profile_manu, profile_mode, fullname, profile_channels, profile_channel_function);
							profileID++;
						} else if(name == "channel"){
							
							channel_func = new ProfileChannel(channel_name, channel_func_name, profile_channel);
							profile_channels.add(channel_func);
						
						}
						
					}
				}
						
			);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		public void createTables(){
			
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
			fixture_table.setFocusable(false);
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
			dimmer_table.setFocusable(false);
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
			
			fixture_table.addMouseListener(this);
//			group_table.addMouseListener(this);
			dimmer_table.addMouseListener(this);
			sequence_table.addMouseListener(this);
		}
		
		// TABLE ROW COLOUR RENDERERS
		
//		public static class FixtureTableRenderer extends DefaultTableCellRenderer {
//			@Override
//		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//		        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
//		       	int id = (Integer)table.getValueAt(row, 0);
//		       	
//		        return this;
//		    }
//		}

		public void windowActivated(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		public void windowDeactivated(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowOpened(WindowEvent arg0) {}
		public void windowClosing(WindowEvent arg0) {
			
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