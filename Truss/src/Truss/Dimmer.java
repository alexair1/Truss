package Truss;

import java.awt.Color;
import java.io.Serializable;

public class Dimmer implements Serializable {
	
	/**
	 * 
	 */
//	private static final long serialVersionUID = -9007954121995439031L;
	Fixture[] fixtures;
	String name;
	
	public Dimmer(String name, Fixture[] f){
		fixtures = f;
		this.name = name;
		saveShow.isSaved = false;
	}
	
	public Fixture[] getFixtures(){
		return fixtures;
	}
	
}
