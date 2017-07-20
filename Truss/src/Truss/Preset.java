package Truss;

import java.awt.Color;
import java.io.Serializable;

public class Preset implements Serializable{

	private static final long serialVersionUID = 95897742426040005L;
	int row;
	String name;
	private final int[] presetData;
	
	public Preset(int row, String name, int[] data){
		this.row = row;
		presetData = data;
		this.name = name;
		
		Loader.frame.presets_table.setValueAt(name, row, 0);
	}
	
	public int[] getPresetData(){
		return presetData;
	}

	public void execute(){
		Loader.frame.preset_off.setEnabled(true);
		Loader.frame.active_preset_lbl.setForeground(Color.BLUE);
		Loader.frame.active_preset_lbl.setText(name);
		
		main.dataBeforePresetCue = main.data;
		
		if(Loader.frame.fade_slider.getValue() == 0){

			main.broadcast(presetData, false);
			main.data = presetData;
			
		} else {
			
			for(int a=0;a<512;a++){
				if(presetData[a] != main.data[a]){
					final int c = a;

					new Thread(){
						public void run(){

							double interval = Loader.frame.fade_slider.getValue()/(Math.abs(presetData[c]-main.data[c]));

							while(main.data[c] >= 0 && main.data[c] <= 255 && presetData[c] != main.data[c]){

								if(presetData[c] <= main.data[c]){
									main.data[c]--;
								} else {
									main.data[c]++;
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
				main.broadcast(main.data, false);

				try {
					Thread.sleep((long)22.72);
				} catch(Exception e){}
				a++;
			}
		}
	}
}
