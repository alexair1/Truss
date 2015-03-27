package Truss;

import java.io.*;

public class saveShow {
	
	// Save Show
	public static void save(File file_path){
		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file_path));
			int a;
			
			for(a=1;a<513;a++){
				o.writeObject(Loader.frame.channel_data[a]);
			}

			for(a=1;a<513;a++){
				o.writeObject(main.fixture[a]);
			}
			
			for(a=1;a<513;a++){
				o.writeObject(main.dimmer[a]);
			}
			
			for(a=0;a<512;a++){
				o.writeObject(main.patch_data[a]);
			} 
			
			for(a=1;a<513;a++){
				o.writeObject(main.group[a]);
			}
			
			o.write(main.groupNames.size());
			
			for(a=0;a<main.groupNames.size();a++){
				o.writeUTF(main.groupNames.get(a));
			}
			
			for(a=0;a<10;a++){
				for(int b=0;b<35;b++){
					o.writeObject(main.preset[a][b]);
				}
			}
			
			o.write(Loader.frame.master_slider.getValue());
			
			o.writeInt(Loader.frame.fade_slider.getValue());
			
			o.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// Load Show
	public static void load(File file_path){
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(file_path));
			
			int a=0;

			while(a < 512){
				try {
					Loader.frame.channel_data[a+1] = (Integer)o.readObject();
					a++;
				} catch(Exception e){
	//				e.printStackTrace();
				}
			} 
			System.out.println("loaded channel data");

			a=1;
			
	//		main.fixture_select_btn_counter = 0;
			main.fixtureNumber = 1;
	//		main.fader_wing_counter = 0;
			while(a < 513){
				try {
					Object f = o.readObject();

					if(f instanceof Fixture){
		//				System.out.println(a + " : " + f);
						main.fixture[a] = (Fixture)f;
					//	main.fixture[a].createControls();
						main.fixtureNumber++;
					} else {
						main.fixture[a] = null;
					}
					a++;
				} catch(Exception e){
			//		e.printStackTrace();
				}
			}
			System.out.println("loaded fixtures");
			
			a=1;
			
	//		main.fixture_select_btn_counter = 0;
			main.dimmerNumber = 1;
	//		main.fader_wing_counter = 0;
			while(a < 513){
				try {
					Object d = o.readObject();

					if(d instanceof Fixture){
		//				System.out.println(a + " : " + f);
						main.dimmer[a] = (Fixture)d;
					//	main.fixture[a].createControls();
						main.dimmerNumber++;
					} else {
						main.dimmer[a] = null;
					}
					a++;
				} catch(Exception e){
					e.printStackTrace();
				}
			}
//			main.setFaderWingPage((Integer)main.fw_page_spinner.getValue());
			System.out.println("loaded dimmers");

			a=0;
			
			while(a < 512){
				try {
					main.patch_data[a] = (Object[])o.readObject();
					a++;
				} catch(Exception e){
					e.printStackTrace();
				}
			} 
			System.out.println("loaded patch data");
			
			a=1;
			main.group_counter = 1;
			
			while(a < 513){
				try {
					
					Object g = o.readObject();

					if(g instanceof Group){
						main.group[a] = (Group)g;			
						main.group_data[main.group_counter-1][0] = main.group[a].name;
						main.group_data[main.group_counter-1][1] = main.group[a].fixtureType;
						main.group_data[main.group_counter-1][2] = main.group[a].getMembers().length;
						main.group_counter++;
					} else {
						main.group[a] = null;
					}
					a++;
				} catch(Exception e){
					if(a == 1){
						e.printStackTrace();
					}
				}
			} 
			System.out.println("loaded groups");
			
			boolean b = true;
			a=0;
			int groupNamesSize = 0;
			
			try {
				groupNamesSize = o.read();
			} catch(Exception e){
				e.printStackTrace();
			}
			
			while(a < groupNamesSize){
				try {
					if(main.groupNames.size() == 0){
						main.groupNames.add(o.readUTF());
					} else if(a < main.groupNames.size()){
						main.groupNames.set(a, o.readUTF());
					} else {
						main.groupNames.add(o.readUTF());
					}
					
					a++;
				} catch(Exception e){
					b = false;
					e.printStackTrace();
				}
			}
			System.out.println("loaded group names");
			
			a=0;
			
			for(a=0;a<10;a++){
				for(int z=0;z<35;z++){
					try {
						Preset p = (Preset)o.readObject();
						if(p instanceof Preset){
							main.preset[a][z] = new Preset(p.row, p.col, p.name, p.data);
						} else {
							main.preset[a][z] = null;
						}
						
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			System.out.println("loaded presets");
			
			try{
				Loader.frame.master_slider.setValue(o.read());
				System.out.println("loaded master fader");
			} catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				Loader.frame.fade_slider.setValue(o.readInt());
				System.out.println("loaded fade fader");
			} catch(Exception e){
				e.printStackTrace();
			}
			
			o.close();
		} catch(Exception e){
	//		e.printStackTrace();
		}
	}
} 
