package Truss;

import java.util.Vector;

public class cueStack {
	int id, amtCues = 0;
	Vector cue = new Vector();
	
	public cueStack(){
		id = main.cueStackCounter;
	}
	public int getID(){
		return id;
	}
	public int getAmtCues(){
		return amtCues;
	}
	public void addCue(){
		amtCues++;
	}
	public void saveToCurrentCue(final int[] data){
		cue.add(new Cue(data));
	}
	public void executeCue(int cue_id){
		((Cue)cue.get(cue_id)).execute();
	}
}
