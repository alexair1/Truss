package Truss;

import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Sync implements ActionListener{

	public void actionPerformed(ActionEvent e) {
		new Thread(){
			public void run(){
				try {
					
					ServerSocket serverskt = new ServerSocket(10000);
					serverskt.setSoTimeout(10000);
					
					Socket skt = serverskt.accept();
					
					BufferedReader inputstream = new BufferedReader(new InputStreamReader(skt.getInputStream())); 
					DataOutputStream outputstream = new DataOutputStream(skt.getOutputStream());

					String str = "";
					for(int a=0;a<10;a++){
						str = str + "bump" + a + ":";
					}
					outputstream.writeBytes(str + "\n");
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};
		}.start();
	}
	
}
