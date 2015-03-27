package Truss;

import java.io.Serializable;

public class Preset implements Serializable{
	int row, col;
	String name;
	int[] data;
	
	public Preset(int row, int col, String name, final int[] data){
		this.row = row;
		this.col = col;
		this.data = data;
		this.name = name;
		
		Loader.frame.presets_grid.setValueAt(name, row, col);
	}

	public void execute(){
		Loader.frame.cancel_preset.setEnabled(true);
		Loader.frame.active_preset_lbl.setText((String)Loader.frame.presets_grid.getValueAt(row, col));
		
		Loader.frame.prev_channel_data = Loader.frame.channel_data;
		
	//	boolean complete = false;
	//	System.out.println(Loader.frame.fade_slider.getValue());
		if(Loader.frame.fade_slider.getValue() == 0){
//			System.out.println(hi);
			for(int a=0;a<512;a++){
				main.data[a] = (byte)data[a];
			}
			
			// Broadcast
			if(main.artnet_node != null && !main.blackout_on) {
				main.dmx.setSequenceID(main.sequenceID % 255);
				main.dmx.setDMX(main.data, main.data.length);
				main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
				main.sequenceID++;
			}
			
		} else {
			
			for(int a=0;a<512;a++){
				if(data[a] != Loader.frame.channel_data[a+1]){
					final int c = a;

					new Thread(){
						public void run(){
					//		System.out.println("data["+c+"]: " + data[c]);
					//		System.out.println("channel_data["+c+"]: " + Loader.frame.channel_data[c+1]);
							double interval = Loader.frame.fade_slider.getValue()/(Math.abs(data[c]-Loader.frame.channel_data[c+1]));
					//		System.out.println("interval"+c+": " + interval);
//							for(int b=0;b<512;b++){
//								main.data[b] = (byte)Loader.frame.channel_data[b+1];
//							}
							while(Loader.frame.channel_data[c+1] >= 0 && Loader.frame.channel_data[c+1] <= 255 && data[c] != Loader.frame.channel_data[c+1]){
//								if(c==2){
								//	System.out.println(data[c]);
								//	System.out.println(Loader.frame.channel_data[c+1]);
//								}
								if(data[c] <= Loader.frame.channel_data[c+1]){
									main.data[c]--;
									Loader.frame.channel_data[c+1]--;
							//		System.out.println("subtract");
								} else {
									main.data[c]++;
									Loader.frame.channel_data[c+1]++;
							//		System.out.println("add");
								}

								try {
									Thread.sleep((long)interval);
								}catch(Exception e){}
							}
							try {
								join();
							} catch (InterruptedException e) {}
						};
					}.start();
				}
			}
			
			int a=0;
			while(a <= Loader.frame.fade_slider.getValue()/22.72){	
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
	}
}
