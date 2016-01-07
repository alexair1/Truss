package Truss;

import java.util.Vector;

import Truss.main.Range;

public class Profile {
	String name, mode, manu, fullname;
	Vector<ProfileChannel> function;
	int[] channel_function;
	
	public Profile(String name, String manu, String mode, String fullname, Vector<ProfileChannel> function, int[] channel_function){
		this.name = name;
		this.mode = mode;
		this.manu = manu;
		this.fullname = fullname;
		this.function = function;
		this.channel_function = channel_function;
		System.out.println(channel_function[40]);
	}
	
	public String getName(){
		return name;
	}
	public String getMode(){
		return mode;
	}
	public String getManufacturer(){
		return manu;
	}
	public String getFullName(){
		return fullname;
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
