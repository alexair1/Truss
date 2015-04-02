package Truss;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
	
	public class Fader {
		int id, value=0, prev_val=0;
		JSpinner val_spinner;
		JSlider slider;
		JLabel channel_name, channel_val_str, channel_num;
		JSeparator sep;
		JCheckBox fine;
		int[] dmxChannels = null;
		Fixture f = null;
		JPanel p;
		
		public void create(int id, JPanel panel, Color bg_colour, int x, int y){
			this.id = id;
			
			p = new JPanel();
	//		p.setBackground(new Color(130, 130, 130));
			p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			p.setBounds(x, y, 80, 310);
				
			channel_name = new JLabel("-", SwingConstants.CENTER);
			channel_name.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			channel_name.setAlignmentX(Component.CENTER_ALIGNMENT);
			p.add(channel_name);
			
			channel_val_str = new JLabel("-", SwingConstants.CENTER);
			channel_val_str.setAlignmentX(Component.CENTER_ALIGNMENT);
			channel_val_str.setPreferredSize(new Dimension(80,20));
			p.add(channel_val_str);
			
			slider = new JSlider(0, 255, 0);
			slider.setMinorTickSpacing(15);
			slider.setPaintTicks(true);
		//	slider.setBackground(new Color(130, 130, 130));
			slider.setOrientation(SwingConstants.VERTICAL);
			slider.setAlignmentX(Component.CENTER_ALIGNMENT);
			slider.setPreferredSize(new Dimension(60,220));
			slider.setEnabled(false);
			slider.setFocusable(false);
			p.add(slider);
			
			val_spinner = new JSpinner();
			val_spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
			val_spinner.setPreferredSize(new Dimension(60,25));
			val_spinner.setEnabled(false);
			p.add(val_spinner);
			
			fine = new JCheckBox("Fine");
			fine.setAlignmentX(Component.CENTER_ALIGNMENT);
			fine.setEnabled(false);
			fine.setFocusPainted(false);
			p.add(fine);

			panel.add(p);
			
			event e = new event();
			val_spinner.addChangeListener(e);
			slider.addChangeListener(e);   

		}
		public class event implements ChangeListener {
			public void stateChanged(ChangeEvent e){
				
				if(e.getSource() == val_spinner){
					
					slider.removeChangeListener(this);
					slider.setValue((Integer)val_spinner.getValue());
					value = (Integer)val_spinner.getValue();
					slider.addChangeListener(this);
					
				} else if(e.getSource() == slider){
					
					val_spinner.removeChangeListener(this);
					val_spinner.setValue(slider.getValue());
					value = slider.getValue();
					val_spinner.addChangeListener(this);
					
				}
				updateChannelData();
			}
		}
		private void updateChannelData(){
			
			if(dmxChannels != null){
				if(dmxChannels.length == 1){
					Loader.frame.channel_data[dmxChannels[0]] = slider.getValue();
					
//					System.out.println(f);
//					System.out.println(f.getFixtureType());
//					System.out.println(f.getFixtureType().name);
//					if(f.getFixtureType().name.equals("Dimmer")){
//						main.data[dmxChannels[0]-1] = (byte)((double)Loader.frame.channel_data[dmxChannels[0]] / 255 * (Integer)Loader.frame.master_spinner.getValue());
//					} else {
//						main.data[dmxChannels[0]-1] = (byte)((double)Loader.frame.channel_data[dmxChannels[0]] / 255 * (Integer)Loader.frame.master_spinner.getValue());
//					}
					
					// Broadcast
					if(main.artnet_node != null && !main.blackout_on) {
						main.dmx.setSequenceID(main.sequenceID % 255);
						main.dmx.setDMX(main.data, main.data.length);
		           		main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
		           		main.sequenceID++;
		            }
					
				} else {

					for(int a=0;a<dmxChannels.length;a++){
						if((slider.getValue() != Loader.frame.channel_data[dmxChannels[a]])){
							int new_val;

							if(prev_val < slider.getValue()){
								new_val = Loader.frame.channel_data[dmxChannels[a]] + Math.abs(slider.getValue()-prev_val);
							} else {
								new_val = Loader.frame.channel_data[dmxChannels[a]] - Math.abs(slider.getValue()-prev_val);
							}

							if(new_val < 256 && new_val > -1){
								Loader.frame.channel_data[dmxChannels[a]] = new_val;
							} else if(new_val > 255){
								Loader.frame.channel_data[dmxChannels[a]] = 255;
							} else if(new_val < 0){
								Loader.frame.channel_data[dmxChannels[a]] = 0;
							}
						
						}
						
						for(int b=0;b<dmxChannels.length;b++){

							main.data[dmxChannels[b]-1] = (byte)(((double)Loader.frame.channel_data[dmxChannels[b]] / 255) * (Integer)Loader.frame.master_spinner.getValue());
						
						}
						
						// Broadcast
						if(main.artnet_node != null && !main.blackout_on) {
							main.dmx.setSequenceID(main.sequenceID % 255);
							main.dmx.setDMX(main.data, main.data.length);
			           		main.artnet.unicastPacket(main.dmx, main.artnet_node.getIPAddress());
			           		main.sequenceID++;
			            }
					}
				}
				prev_val = slider.getValue();

			} // End parent if statement
			
			if(f != null){
				if(f.isUsingProfile() && dmxChannels != null){
					f.getFixtureType().setStringValue(Fader.this);
				}
			} else {
				this.setStrValue("-");
			}
			
		} // End updateChannelData
		
		public void setName(String name){
			channel_name.setText(name);
		}
		public void setStrValue(String val){
			channel_val_str.setText(val);
		}
		public void setFaderVisible(boolean b){
			p.setVisible(b);
		}
		public void assignChannel(int[] dmxChannels){
			this.dmxChannels = dmxChannels;
			val_spinner.setEnabled(true);
			slider.setEnabled(true);
	//		fine.setEnabled(true);
		}
		public void unEnable(){
			val_spinner.setEnabled(false);
			slider.setEnabled(false);
			fine.setEnabled(false);
		}
		public void unassign(){
			dmxChannels = null;
		}
		public void assignFixture(Fixture f){
			this.f = f;
			if(f.isUsingProfile() && dmxChannels != null){
				f.getFixtureType().setStringValue(Fader.this);
			}
		}
		public void revalidate(){
			updateChannelData();
		}
	}
