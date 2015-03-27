package Truss;

import java.awt.Color;

public class Dimmer {
	
	Fixture[] fixtures;
	String name;
	
	public Dimmer(String name, Fixture[] f){
		fixtures = f;
		this.name = name;
	}
	
	public Fixture[] getFixtures(){
		return fixtures;
	}
	
}
