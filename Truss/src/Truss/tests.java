package Truss;

import javax.swing.JOptionPane;

public class tests {
	public static void main(String[] args){
		int value = 17   ;
		
		int y = (int)Math.ceil(value/16.0) - 1;
		int x;
		if(value % 16 == 0){
			x = value-(((value/16)*16)-15);
		} else {
			x = value-((((value/16)+1)*16)-15);
		}
		int oldValue = 001;
		System.out.println("<b>001</b>".replace("<b>"+String.format("%03d", oldValue)+"</b>", "<b>"+String.format("%03d", 2)+"</b>"));
		
		System.out.println(     value-(   ((y+1)*16)-15   )     );
		
		System.out.println(   x + ", " +y   );
	}
}