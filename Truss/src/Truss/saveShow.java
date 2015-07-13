package Truss;

import java.awt.Color;
import java.io.*;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class saveShow {
	
	static boolean isSaved = false;
	
	/*
	 * Save Show
	 */
	public static void save(File file_path){
		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file_path));
			int a;
			
			for(a=1;a<513;a++){
				o.writeObject(main.channel_data[a]);
			}

			for(a=1;a<513;a++){
				o.writeObject(main.fixture[a]);
			}
			
			for(a=1;a<513;a++){
				o.writeObject(main.dimmer[a]);
			}

			o.write(main.frame.master_slider.getValue());
			
			o.writeInt(main.frame.fade_slider.getValue());
			
			o.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		isSaved = true;
	}

	public static void load(File file_path){
		
		ProgressDialog prog = new ProgressDialog("Saving");
		
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(file_path));
			
			int a=1;

			while(a < 513){
				try {
					main.frame.channel_data[a] = (Integer)o.readObject();
					a++;
				} catch(Exception e){
					e.printStackTrace();
				}
			} 
			System.out.println("loaded channel data");
			prog.setProgress(20);

			a=1;

			main.fixtureNumber = 1;
			main.fixture_data = new Object[6][7];
			main.fixture = new Fixture[513];
			
			while(a < 513){
				try {

					Object f = o.readObject();

					if(f instanceof Fixture){

						main.fixture[a] = (Fixture)f;

						main.fixture_data[main.fixture[a].y][main.fixture[a].x] = "<html>&emsp;" + main.fixture[a].getName() + "<br>&emsp; " + a + "</html>";
						main.fixtureNumber++;
						
					} else {
						
						main.fixture[a] = null;
						
					}
					
					a++;
					
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			System.out.println("loaded fixtures");
			prog.setProgress(40);
			
			a=1;
			
			main.dimmerNumber = 1;
			main.dimmer_data = new Object[6][7];
			main.dimmer = new Dimmer[513];

			while(a < 513){
				try {
					Object d = o.readObject();

					if(d instanceof Fixture){

						main.dimmer[a] = (Dimmer)d;
						main.dimmerNumber++;
						
					} else {
						
						main.dimmer[a] = null;
						
					}
					
					a++;
					
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			System.out.println("loaded dimmers");
			prog.setProgress(60);
			
			try{
				main.frame.master_slider.setValue(o.read());
				System.out.println("loaded master fader");
				prog.setProgress(80);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				main.frame.fade_slider.setValue(o.readInt());
				System.out.println("loaded fade fader");
				prog.setProgress(100);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			o.close();
			prog.dispose();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		main.frame.createTables();
		main.frame.patch_table_pane.setViewportView(main.frame.fixture_table);
	}
} 
