package Truss;

import java.io.*;

public class saveShow {
	
	static boolean isSaved = false;
	
	// Save Show
	public static void save(File file_path){

		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file_path));
			int a;
			
			o.writeObject(main.data);

			o.writeObject(main.isChannelDimmer);

			for(a=0;a<512;a++){
				o.writeObject(main.fixture[a]);
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
			
			for(a=0;a<50;a++){
				o.writeObject(main.preset[a]);
			}
			
			o.write(Loader.frame.master_slider.getValue());
			
			o.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		isSaved = true;
	}
	
	// Load Show
	public static void load(File file_path){
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(file_path));
			
			int a=0;

			main.data = (int[])o.readObject();
			main.isChannelDimmer = (boolean[])o.readObject();

			a=0;
			
			main.fixtureNumber = 0;

			while(a < 512){
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

			a=0;
			
			while(a < 512){
				try {
					main.patch_data[a] = (Object[])o.readObject();
					a++;
				} catch(Exception e){
					e.printStackTrace();
				}
			} 
			
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
			
			for(a=0;a<50;a++){
				try {
					Preset p = (Preset)o.readObject();
					if(p instanceof Preset){
						main.preset[a] = new Preset(p.row, p.name, p.getPresetData());
					} else {
						main.preset[a] = null;
					}
						
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			try{
				Loader.frame.master_slider.setValue(o.read());
			} catch(Exception e){
				e.printStackTrace();
			}
			
			o.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
} 
