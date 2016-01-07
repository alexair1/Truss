package Truss;

public class Group {

	String name;
	int id;
	Fixture[] fixtures;
	
	public Group(String name, int id, Fixture[] fixtures){
		this.name = name;
		this.id = id;
		this.fixtures = fixtures;
	}
	
	public Fixture[] getFixtures(){
		return fixtures;
	}
}
