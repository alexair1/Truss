package Truss;

import java.io.Serializable;

public class Group implements Serializable{
	Fixture[] fixture = new Fixture[512];
	int counter = 0, id = main.group_counter;
	String name;
	Profile fixtureType;
	
	public Group(String name, Profile fixtureType) {
		this.fixtureType = fixtureType;
		this.name = name;

		main.group_data[main.group_counter-1][0] = name;
		main.group_data[main.group_counter-1][1] = fixtureType.getName();
		main.group_data[main.group_counter-1][2] = 0;
		main.group_counter++;
	}
	
	public void addMember(Fixture f){
		fixture[counter] = f;
		counter++;
		main.group_data[id-1][2] = counter;
	}
	public Fixture[] getMembers(){
		int size = 0;
		for(int a=0;a<512;a++){
			if(fixture[a] != null){
				size++;
			}
		}
		Fixture[] f = new Fixture[size];
		for(int b=0;b<size+1;b++){
			if(fixture[b] != null){
				f[b] = fixture[b];
			}
		}
		return f;
	}
	public String getName(){
		return name;
	}
	public Profile getFixtureType(){
		return fixtureType;
	}
}
