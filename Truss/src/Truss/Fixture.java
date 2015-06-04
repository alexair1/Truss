package Truss;

import java.awt.Color;
import java.io.Serializable;

public class Fixture extends Object implements Serializable{

//	private static final long serialVersionUID = -1290584544546311538L;
	String name, fixtureType;
	int startChannel, channels, id, intensity = 0, x, y;
	boolean usingProfile;
	Color c = null;
	
	public Fixture(String name, String fixtureType, int startChannel, int channels, boolean usingProfile, Color colour, int x, int y) {
		this.name = name;
		this.fixtureType = fixtureType;
		this.startChannel = startChannel;
		this.channels = channels;
		this.usingProfile = usingProfile;
		this.x = x;
		this.y = y;
		id = main.fixtureNumber;
		c = colour;
		saveShow.isSaved = false;
		
//		if(main.getProfileByName(fixtureType).built_in_dimmer){
//			intensity = 255;
//		}

	}
		public String getName() {
			return name;
		}
		public void setName(String name){
			this.name = name;
		}
		public int getStartChannel(){
			return startChannel;
		}
		public void setStartChannel(int startChannel){
			this.startChannel = startChannel;
		}
		public int getChannels(){
			return channels;
		}
		public void setChannels(int channels){
			this.channels = channels;
		}
		public Profile getFixtureType(){
			return main.getProfileByName(fixtureType);
		}
		public boolean isUsingProfile(){
			return usingProfile;
		}
		public int getId(){
			return id;
		}

}
