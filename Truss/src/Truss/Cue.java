package Truss;

public class Cue {
	int[] data;
	int id;
	String name;
	long inTime, holdTime;
	
	public Cue(final int[] data, String name, long inTime, long holdTime, int id){
		this.data = data;
		this.name = name;
		this.inTime = inTime;
		this.holdTime = holdTime;
		this.id = id;
	}
	
	public int getChannelValue(int channel){
		return data[channel];
	}

}
