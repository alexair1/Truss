package Truss;

import javax.swing.JOptionPane;

public class tests {
	public static void main(String[] args){
		int a = 7;
		int index = (a+1) % 7;
		if(index == 0 && a != 0){
			index = 7;
		}
		index--;
		System.out.println(index);
		
	}
}