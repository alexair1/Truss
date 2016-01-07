package Truss;

import javax.swing.JOptionPane;

public class tests {
	public static void main(String[] args){
		
		int phase = 0;
		int angle = 5;
		if(angle == 180 || angle == 360){
			angle = 0;
		}
		System.out.println(         255*Math.sin(Math.toRadians(angle ))               );

		
	}
}