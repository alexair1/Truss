package Truss;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

public class tests {
	public static void main(String[] args){

//		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
//		for (Mixer.Info mixerInfo : mixers){
//		    System.out.println(mixerInfo);
//		}
		
		Port lineIn;
		FloatControl volCtrl;
		try {
		  Mixer mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
		  System.out.println(mixer.getTargetLineInfo()[0]);
		  lineIn = (Port)mixer.getLine(Port.Info.LINE_OUT);
		  lineIn.open();
//		  volCtrl = (FloatControl) lineIn.getControl(
//
//		      FloatControl.Type.VOLUME);

		  // Assuming getControl call succeeds, 
		  // we now have our LINE_IN VOLUME control.
		} catch (Exception e) {
		  System.out.println("Failed trying to find LINE_IN"
		    + " VOLUME control: exception = " + e);
		};
		
//		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
//	    List<Line.Info> availableLines = new ArrayList<Line.Info>();
//	    for (Mixer.Info mixerInfo : mixers){
//	        System.out.println("Found Mixer: " + mixerInfo);
//
//	        Mixer m = AudioSystem.getMixer(mixerInfo);
//
//	        Line.Info[] lines = m.getTargetLineInfo();
//
//	        for (Line.Info li : lines){
//	            System.out.println("Found target line: " + li);
//	            try {
//	                m.open();
//	                availableLines.add(li);                  
//	            } catch (LineUnavailableException e){
//	                System.out.println("Line unavailable.");
//	            }
//	        }  
//	    }
//
//	    System.out.println("Available lines: " + availableLines);
		
	}
}
