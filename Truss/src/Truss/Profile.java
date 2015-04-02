package Truss;

import java.util.Vector;

import Truss.main.Range;

public class Profile {
	String name;
	int id = main.profileID += 1;
	Vector<ProfileChannel> function;
	int[] channel_function;
	
	public Profile(String name, String mode, Vector<ProfileChannel> function, int[] channel_function){
		this.name = name;
		this.function = function;
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
			channel = (f.dmxChannels[0] - f.f.startChannel)+1;
			
			for(int a=0;a<function.get(channel).func.size();a++){
				
				Range rng = function.get(channel).func.get(a);
				
				if(rng.isInRange(Math.abs(f.slider.getValue()))){
					f.channel_val_str.setText(rng.func);
					break;
				}
			}
			
		} catch(Exception e){
			f.channel_val_str.setText("-");
		}
	}
}
