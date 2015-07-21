package Truss;

import java.awt.Color;
import java.io.Serializable;

public class Dimmer implements Serializable {
	
//	private static final long serialVersionUID = -9007954121995439031L;
	Fixture[] fixtures;
	String name;
	int id;
	
	public Dimmer(String name, Fixture[] f, int id){
		fixtures = f;
		this.name = name;
		this.id = id;
		saveShow.isSaved = false;
	}
	
	public Fixture[] getFixtures(){
		return fixtures;
	}
	
}
