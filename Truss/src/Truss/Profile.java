package Truss;

import java.util.Vector;

import Truss.main.Range;

public class Profile {
	String name;
	int id = main.profileID += 1;
	Vector function;
	boolean built_in_dimmer;
	int[] channel_function;
	
	public Profile(String name, String mode, Vector function, boolean built_in_dimmer, int[] channel_function){
		this.name = name;
		this.function = function;
		this.built_in_dimmer = built_in_dimmer;
		this.channel_function = channel_function;
	}
	
	public String getName(){
		return name;
	}
	public int getChannels(){
		return function.size();
	}
	public void setStringValue(Fader f){
		int channel=0;
		try{
	//		channel = Integer.parseInt(f.channel_num.getText().split("/")[0]);
			channel = f.dmxChannels[0] - f.f.startChannel;
		} catch(Exception e){}

		for(int a=0;a<((Vector)function.get(channel)).size()-2;a++){

			Range rng = (Range)(Object)((Vector) function.get(channel)).get(a+2);
			
	//		Range rng = (Range)(Object)((Vector) function.get(channel-1)).get(a+1);
			
			if(rng.isInRange(Math.abs(f.slider.getValue()))){
				f.channel_val_str.setText(rng.func);
				return;
			}
		}
		return;
	}
}
