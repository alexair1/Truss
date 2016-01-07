package Truss;

import java.util.Vector;

import Truss.EffectWizard.EffectPattern;

public class Effect {

	private Vector<Thread> threads = new Vector<Thread>();
	private int bpm, min, max, phase, channelFunc;
	private EffectPattern ep;
	
	public void setBPM(int bpm){
		this.bpm = bpm;
	}
	public void setMin(int min){
		this.min = min;
	}
	public void setMax(int max){
		this.max = max;
	}
	public void setPhase(int phase){
		this.phase = phase;
	}
	public void setPattern(EffectPattern ep){
		this.ep = ep;
	}
	public void setType(int channelFunc){
		this.channelFunc = channelFunc;
	}
	
	public int getBPM(){
		return bpm;
	}
	public int getMin(){
		return min;
	}
	public int getMax(){
		return max;
	}
	public int getPhase(){
		return phase;
	}
	public EffectPattern getPattern(){
		return ep;
	}
	public int getType(){
		return channelFunc;
	}
	
	public void addThreadUsing(Thread t){
		threads.addElement(t);
	}
	
	public void removeThreadUsing(Thread t){
		threads.remove(t);
	}
	
	public Vector<Thread> getThreadsUsing(){
		return threads;
	}
}
