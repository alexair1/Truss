package Truss;

import java.io.*;
import java.util.Properties;

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

			o.write(Loader.frame.master_slider.getValue());
			
			o.writeInt(Loader.frame.fade_slider.getValue());
			
			o.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		isSaved = true;
	}
//	public static void save(File file_path){
//		
//		try {
//			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file_path));
//			Properties prop = new Properties();
//			
//			int a;
//			
//			for(a=1;a<513;a++){
//				prop.setProperty("ch"+a, String.valueOf(main.channel_data[a]));
//			}
//
//			for(a=1;a<513;a++){
//				prop.setProperty("fix"+a, String.valueOf(main.fixture[a]));
//			}
//			
//			for(a=1;a<513;a++){
//				prop.setProperty("dim"+a, String.valueOf(main.dimmer[a]));
//			}
//			
//			prop.setProperty("mstr", String.valueOf(Loader.frame.master_slider.getValue()));
//			prop.setProperty("fade"+a, String.valueOf(Loader.frame.fade_slider.getValue()));
//			
//			prop.store(o, null);
//			o.close();
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		
//	}
		
	/*
	 * Load Show
	 */
//public static void load(File file_path){
//		
//		ProgressDialog prog = new ProgressDialog("Saving");
//		
//		try {
//			ObjectInputStream o = new ObjectInputStream(new FileInputStream(file_path));
//			Properties prop = new Properties();
//			
//			prop.load(o);
//			
//			int a;
//			
//			for(a=1;a<513;a++){
//				main.channel_data[a] = Integer.parseInt(prop.getProperty("ch"+a));
//			}
//			
//			for(a=1;a<513;a++){
//				main.channel_data[a] = Integer.parseInt(prop.getProperty("ch"+a));
//			}
//			System.out.println("loaded channel data");
//			prog.setProgress(20);
//
//
//			main.fixtureNumber = 1;
//			main.fixture_data = new Object[6][7];
//			main.fixture = new Fixture[513];
//			
//			for(a=1;a<513;a++){
//				main.fixture[a] = (Fixture)(Object)prop.getProperty("fix"+a);
//				main.fixtureNumber++;
//			}
//			System.out.println("loaded fixtures");
//			prog.setProgress(40);
//
//			
//			main.dimmerNumber = 1;
//			main.dimmer_data = new Object[6][7];
//			main.dimmer = new Dimmer[513];
//			
//			for(a=1;a<513;a++){
//				main.dimmer[a] = (Dimmer)(Object)prop.getProperty("dim"+a);
//				main.dimmerNumber++;
//			}
//			System.out.println("loaded dimmers");
//			prog.setProgress(60);
//			
//			Loader.frame.master_slider.setValue(Integer.parseInt(prop.getProperty("mstr")));
//			Loader.frame.fade_slider.setValue(Integer.parseInt(prop.getProperty("fade")));
//
//			o.close();
//			prog.dispose();
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	
	
	public static void load(File file_path){
		
		ProgressDialog prog = new ProgressDialog("Saving");
		
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(file_path));
			
			int a=0;

			while(a < 512){
				try {
					Loader.frame.channel_data[a+1] = (Integer)o.readObject();
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
				Loader.frame.master_slider.setValue(o.read());
				System.out.println("loaded master fader");
				prog.setProgress(80);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				Loader.frame.fade_slider.setValue(o.readInt());
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
	}
} 
