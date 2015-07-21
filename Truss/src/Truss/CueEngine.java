package Truss;

import java.awt.Color;

public class CueEngine {
	
	static Thread inTimer, broadcast, setData;
	
	/**
	 * Executes a given cue
	 * 
	 * @param  cue the cue to be executed
	 */
	public static void execute(final Cue cue){
		
		if(cue.data != null){

			main.nextCueLbl.setText("<html><i>Next:</i> &emsp;&nbsp;&nbsp;&nbsp; &#34;" + main.cue[(cue.id+1)].name + "&#34; &emsp (#" + (cue.id+1) + ")</html>");
			if(cue.id != 1){
				main.prevCueLbl.setText("<html><i>Previous:</i> &nbsp;&nbsp; &#34;" + main.cue[(cue.id-1)].name + "&#34; &emsp (#" + (cue.id-1) + ")</html>");
			} else {
				main.prevCueLbl.setText("<html><i>Previous:</i> &emsp; - &emsp (-)</html>");
			}
			
			main.frame.prev_channel_data = main.frame.channel_data;

				if(cue.inTime == 0){

					for(int a=0;a<512;a++){
						main.data[a] = (byte)cue.data[a];
					}
					
					// Broadcast
					if(main.artnet_node != null && !main.blackout_on) {
						main.dmx.setSequenceID(main.sequenceID % 255);
						main.dmx.setDMX(main.data, main.data.length);
						main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
						main.sequenceID++;
					}
					
					main.current_cue_lbl.setForeground(Color.GREEN);
					main.activeCue = main.current_cue;
					
				} else {
					
					inTimer = new Thread(){
						public void run(){

							long finishTime = System.currentTimeMillis() + cue.inTime;
							long currentTime = System.currentTimeMillis();
							
							while(System.currentTimeMillis() <= finishTime){
								main.inTimeLbl.setText(convertLongToString(System.currentTimeMillis() - currentTime));
							}
							
							main.current_cue_lbl.setForeground(Color.GREEN);
							main.inTimeLbl.setForeground(Color.BLUE);
							main.activeCue = main.current_cue;
						}
					};
					
					for(int a=0;a<512;a++){

						if(cue.data[a] != main.frame.channel_data[a+1]){
							final int c = a;
							
							/* 
							 * Create a thread for each channel that independently updates each time a new value is
							 * to be broadcasted. The 'broadcast' thread handles all output by broadcasting at the
							 * maximum DMX512 refresh rate of 44Hz. 
							 */
							
							new Thread(){
								public void run(){

									long interval = cue.inTime/(Math.abs(cue.data[c]-main.frame.channel_data[c+1]));

									while(main.channel_data[c+1] >= 0 && main.channel_data[c+1] <= 255 && cue.data[c] != main.channel_data[c+1]){

										if(cue.data[c] <= main.channel_data[c+1]){
											
											main.data[c]--;
											main.channel_data[c+1]--;
											
										} else {
											
											main.data[c]++;
											main.channel_data[c+1]++;

										}

										try {
											Thread.sleep((long)interval);
										} catch(Exception e){}
										System.out.println(main.data[c]);
									}
									try {
										join();
									} catch (InterruptedException e) {}
								};
							}.start();
						}
					}
					
					broadcast = new Thread(){
						public void run(){
							
							int a=0;
							while(a <= cue.inTime/22.72){
								
								// Broadcast
								if(main.artnet_node != null && !main.blackout_on) {
									main.dmx.setSequenceID(main.sequenceID % 255);
									main.dmx.setDMX(main.data, main.data.length);
									main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
									main.sequenceID++;
								}
								try {
									Thread.sleep((long)22.72);
								} catch(Exception e){}
								a++;
						
							}
							
						}
					};
					
					inTimer.start();
					broadcast.start();
					
				} // End inTime check if statement

				// If the cue has a hold time then hold and execute next cue if possible
				if(cue.holdTime > 0){

					new Thread(){
						public void run(){
							
							long finishTime = System.currentTimeMillis() + cue.inTime;
							while(System.currentTimeMillis() <= finishTime){}
							
							finishTime = System.currentTimeMillis() + cue.holdTime;
							long currentTime = System.currentTimeMillis();
							
							while(System.currentTimeMillis() <= finishTime){
								main.holdTimeLbl.setText(convertLongToString(System.currentTimeMillis() - currentTime));
							}
							
							main.holdTimeLbl.setForeground(Color.BLUE);

							if(main.cue[cue.id+1].data != null){
								
								resetCueDisplay();
								main.current_cue++;
								main.current_cue_lbl.setForeground(Color.RED);
								main.current_cue_lbl.setText(""+main.current_cue);
								main.cue_name_tf.setText(main.cue[main.current_cue].name);
								execute(main.cue[cue.id+1]);
								
							}
							
						}
						
					}.start();
					
				}
				
		}
	}
	
	/**
	 * Converts a string in format 'min : sec : milli' (eg. 00:05:60) into an equivalent numerical representation in milliseconds
	 * 
	 * @param  s the string to be converted
	 * @return the converted value
	 */
	public static long convertStringToLong(String s) {
		
		int min, sec, milli;
		
		try {
			
			min = (Integer.parseInt(s.split(":")[0])*60000);
			sec = (Integer.parseInt(s.split(":")[1])*1000);
			milli = (Integer.parseInt(s.split(":")[2])*10);
			
		} catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		
		return (min + sec + milli);
		
	}
	
	/**
	 * Converts a long to the format 'min : sec : milli' (eg. 00:05:60) from an equivalent numerical representation into milliseconds
	 * 
	 * @param  l the long to be converted
	 * @return the converted string
	 */
	public static String convertLongToString(long l) {
		
		long min, sec, milli;
		
		try {
			
			min = l/60000;
			l -= (min*60000);
			
			sec = l/1000;
			l -= (sec*1000);
			
			milli = l/10;
			
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		return String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%02d", milli);
		
	}
	
	/**
	 * Resets all components of the Cue display
	 */
	public static void resetCueDisplay(){
		
		if(main.cue[main.current_cue].data == null){
			main.no_assign_lbl.setVisible(true);
		} else {
			main.no_assign_lbl.setVisible(false);
		}
		
		main.inTimeLbl.setForeground(Color.BLACK);
		main.holdTimeLbl.setForeground(Color.BLACK);
		
		main.holdTimeLbl.setText("00:00:00");
		main.inTimeLbl.setText("00:00:00");
		
	}
	
}
