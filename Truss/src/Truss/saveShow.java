package Truss;

import java.awt.Color;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

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
			
			o.writeObject(main.selectionTableData);
			
//			for(a=0;a<6;a++){
//				for(int b=0;b<6;b++){
//					o.writeObject(main.selectionTableData[a][b]);
//				}
//			}

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
			main.selectionTableData = new Object[6][6];
			main.fixtureData = new Vector<String>();
			main.dimmerData = new Vector<String>();
			main.fixture = new Fixture[513];
			
			while(a < 513){
				try {

					Object f = o.readObject();

					if(f instanceof Fixture){

						main.fixture[a] = (Fixture)f;

						main.fixtureData.add(main.fixtureNumber + "   " + ((Fixture)f).name);
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
			main.dimmer = new Dimmer[513];

			while(a < 513){
				try {
					Object d = o.readObject();

					if(d instanceof Dimmer){

						main.dimmer[a] = (Dimmer)d;
						main.dimmerData.add(main.dimmerNumber + "   " + ((Dimmer)d).name + " (Size: " + ((Dimmer)d).getFixtures().length + ")");
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
			
			main.frame.patchTable.setListData(main.dimmerData);
			
			main.selectionTableData = (Object[][])o.readObject();
			System.out.println("loaded selectionTable");
			prog.setProgress(80);
			
			try{
				main.frame.master_slider.setValue(o.read());
				System.out.println("loaded master fader");
				prog.setProgress(90);
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
		main.frame.patch_table_pane.setViewportView(main.frame.selectionTable);
	}
} 
