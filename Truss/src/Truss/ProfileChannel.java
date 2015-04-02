package Truss;

import java.util.Vector;
import Truss.main.Range;

public class ProfileChannel {
	
	String name, func_name;
	Vector<Range> func;
	
	public ProfileChannel(String name, String func_name, Vector<Range> func){
		this.name = name;
		this.func_name = func_name;
		this.func = func;
	}
	
}
