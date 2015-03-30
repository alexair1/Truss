package Truss;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;

public final class FixtureSelectionEngine {
	
	static ArrayList<Fixture> selectedFixtures;
	
	/*
	 * Selects a fixture.
	 */
	static void selectFixture(Fixture f){
		
		Profile prof = f.getFixtureType();
		
		selectedFixtures = new ArrayList<Fixture>();
		selectedFixtures.add(f);
		
		main.cur_sel_id.setText("ID"+f.id);
		main.cur_sel_name.setText(f.getName());
		main.cur_sel_type.setText(f.getFixtureType().name);
		
		main.btnDimmer.setEnabled(false);
		main.btnShutter.setEnabled(false);
		main.btnIris.setEnabled(false);
		main.btnFocus.setEnabled(false);
		main.btnZoom.setEnabled(false);
		main.btnColourWheel.setEnabled(false);
		main.btnRgbMixing.setEnabled(false);
		main.btnCto.setEnabled(false);
		main.btnGobo_1.setEnabled(false);
		main.btnGobo_2.setEnabled(false);
		main.btnGobo_3.setEnabled(false);
		main.btnPrism.setEnabled(false);
		main.btnFrost.setEnabled(false);
		main.btnControl.setEnabled(false);
		main.btnOther.setEnabled(false);
		
		main.tilt.setName("Tilt");
		main.pan.setName("Pan");
		
		// Check if the selected fixture/s have a the following functions, enable accordingly
		for(int a=0;a<selectedFixtures.size();a++){
			if(selectedFixtures.get(a) != null){
				if(selectedFixtures.get(a).getFixtureType().channel_function[0] != 0){
					setSingleFader(main.btnDimmer);
					main.btnDimmer.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[1] != 0){
					main.btnShutter.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[2] != 0){
					main.btnIris.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[3] != 0){
					main.btnFocus.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[4] != 0){
					main.btnZoom.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[7] != 0){
					main.btnColourWheel.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[9] != 0 || selectedFixtures.get(a).getFixtureType().channel_function[11] != 0 || selectedFixtures.get(a).getFixtureType().channel_function[13] != 0){
					setFaderBank(main.btnRgbMixing);
					main.btnRgbMixing.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[21] != 0){
					main.btnCto.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[23] != 0){
					main.btnGobo_1.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[25] != 0){
					main.btnGobo_2.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[27] != 0){
					main.btnGobo_3.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[29] != 0){
					main.btnPrism.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[31] != 0){
					main.btnFrost.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[34] != 0){
					main.btnControl.setEnabled(true);
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[39] != 0){
					main.btnOther.setEnabled(true);
				}
			}
		}
		
		Fader[] faders = {main.single, main.bank_1, main.bank_2, main.bank_3, main.bank_4, main.bank_5, main.pan, main.tilt};
		
		for(Fader fader : faders){
			if(selectedFixtures.size() == 1){
				fader.assignFixture(f);
				fader.slider.setMinimum(0);
				fader.slider.setMaximum(255);
			} else {
				fader.assignFixture(null);
				fader.slider.setMinimum(-255);
				fader.slider.setMaximum(255);
				fader.slider.setValue(0);
			}
		}
		
		// Check if the selected fixture/s have pan and/or tilt functions, enable accordingly
		for(int a=0;a<selectedFixtures.size();a++){
			if(selectedFixtures.get(a) != null){
				if(selectedFixtures.get(a).getFixtureType().channel_function[5] != 0){
					if(selectedFixtures.size() == 1){
						main.pan.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[5])-1]);
						main.pan.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[5])-1});
						main.pan.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[5])-1];
					}
				}
				if(selectedFixtures.get(a).getFixtureType().channel_function[6] != 0){
					if(selectedFixtures.size() == 1){
						main.tilt.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[6])-1]);
						main.tilt.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[6])-1});
						main.tilt.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[6])-1];
					}
				}
			}
		}
		
	}
	
	/*
	 * Selects an array of dimmers.
	 */
	static void selectDimmers(Dimmer d){
		
		selectedFixtures = new ArrayList<Fixture>();
		for(int a=0;a<d.getFixtures().length;a++){
			selectedFixtures.add(d.getFixtures()[a]);
		}
		
		main.cur_sel_id.setText("DIM");
		main.cur_sel_name.setText(d.name);
		main.cur_sel_type.setText("Size: " + d.getFixtures().length);
		
		main.btnDimmer.setEnabled(false);
		main.btnShutter.setEnabled(false);
		main.btnIris.setEnabled(false);
		main.btnFocus.setEnabled(false);
		main.btnZoom.setEnabled(false);
		main.btnColourWheel.setEnabled(false);
		main.btnRgbMixing.setEnabled(false);
		main.btnCto.setEnabled(false);
		main.btnGobo_1.setEnabled(false);
		main.btnGobo_2.setEnabled(false);
		main.btnGobo_3.setEnabled(false);
		main.btnPrism.setEnabled(false);
		main.btnFrost.setEnabled(false);
		main.btnControl.setEnabled(false);
		main.btnOther.setEnabled(false);
		
		main.tilt.setName("-");
		main.pan.setName("-");
		
		Fader[] faders = {main.single, main.bank_1, main.bank_2, main.bank_3, main.bank_4, main.bank_5, main.pan, main.tilt};
		
		for(Fader fader : faders){
			fader.slider.setMinimum(0);
			fader.slider.setMaximum(255);
		}
		setFaderBankPage(1);
		
	}
	
	/*
	 * Set the single fader to the function associated with the JButton passed
	 */
	static void setSingleFader(JButton btn){
		
		int index = 0;

		if(btn == main.btnDimmer){
			index = 0;
		} else if(btn == main.btnShutter){
			index = 1;
		} else if(btn == main.btnIris){
			index = 2;
		} else if(btn == main.btnFocus){
			index = 3;
		} else if(btn == main.btnZoom){
			index = 4;
		}
		main.single.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[index]-1)).get(0));

			if(selectedFixtures.size() == 1){
				main.single.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[index])-1]);
				main.single.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[index])-1});
				main.single.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[index])-1];
				
				if( ((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[index]-1)).size() >= 3 ){
					selectedFixtures.get(0).getFixtureType().setStringValue(main.single);
				} else {
					main.single.setStrValue("-");
				}
			} else {
				main.single.prev_val = 0;
			}	
		
	}
	
	/*
	 * Set the single fader to the function associated with the JButton passed
	 */
	static void setFaderBank(JButton btn){
		
		Fader[] bank_faders = {main.bank_1, main.bank_2, main.bank_3, main.bank_4, main.bank_5};
		
		for(Fader f : bank_faders){
			f.f = null;
			f.unassign();
			f.setName("-");
			f.slider.setValue(0);
			f.setStrValue("-");
		}

		if(btn == main.btnColourWheel){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[7])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[7])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[7])-1];
			main.bank_1.setName("Colour Wheel");
			
			setStringValueForFaders(main.bank_1, 7);
			
		} else if(btn == main.btnRgbMixing){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[9])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[9])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[9])-1];
			main.bank_1.setName("Red");
			setStringValueForFaders(main.bank_1, 9);
			
			main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[11])-1]);
			main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[11])-1});
			main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[11])-1];
			main.bank_2.setName("Green");
			setStringValueForFaders(main.bank_2, 11);
			
			main.bank_3.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[13])-1]);
			main.bank_3.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[13])-1});
			main.bank_3.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[13])-1];
			main.bank_3.setName("Blue");
			setStringValueForFaders(main.bank_3, 13);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[50] != 0){
				main.bank_4.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[50])-1]);
				main.bank_4.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[50])-1});
				main.bank_4.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[50])-1];
				main.bank_4.setName("White");
				setStringValueForFaders(main.bank_4, 50);
			}
			
		} else if(btn == main.btnCto){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[21])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[21])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[21])-1];
			main.bank_1.setName("CTO");
			setStringValueForFaders(main.bank_1, 21);
			
		} else if(btn == main.btnGobo_1){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[23])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[23])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[23])-1];
			main.bank_1.setName("Gobo 1");
			setStringValueForFaders(main.bank_1, 23);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[24] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[24])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[24])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[24])-1];
				main.bank_2.setName("Gobo 1 Rot");
				setStringValueForFaders(main.bank_2, 24);
			}
			
		} else if(btn == main.btnGobo_2){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[25])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[25])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[25])-1];
			main.bank_1.setName("Gobo 2");
			setStringValueForFaders(main.bank_1, 25);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[26] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[26])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[26])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[26])-1];
				main.bank_2.setName("Gobo 2 Rot");
				setStringValueForFaders(main.bank_2, 26);
			}
			
		} else if(btn == main.btnGobo_3){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[27])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[27])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[27])-1];
			main.bank_1.setName("Gobo 3");
			setStringValueForFaders(main.bank_1, 27);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[28] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[28])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[28])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[28])-1];
				main.bank_2.setName("Gobo 3 Rot");
				setStringValueForFaders(main.bank_2, 28);
			}
			
		} else if(btn == main.btnPrism){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[29])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[29])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[29])-1];
			main.bank_1.setName("Prism");
			setStringValueForFaders(main.bank_1, 29);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[30] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[30])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[30])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[30])-1];
				main.bank_2.setName("Prism Rot");
				setStringValueForFaders(main.bank_2, 30);
			}
			
		} else if(btn == main.btnFrost){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[31])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[31])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[31])-1];
			main.bank_1.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[31])).get(0));
			setStringValueForFaders(main.bank_1, 31);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[32] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[32])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[32])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[32])-1];
				main.bank_2.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[32])).get(0));
				setStringValueForFaders(main.bank_2, 32);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[33] != 0){
				main.bank_3.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[33])-1]);
				main.bank_3.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[33])-1});
				main.bank_3.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[33])-1];
				main.bank_3.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[33])).get(0));
				setStringValueForFaders(main.bank_3, 33);
			}
			
		} else if(btn == main.btnControl){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[34])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[34])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[34])-1];
			main.bank_1.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[34])).get(0));
			setStringValueForFaders(main.bank_1, 34);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[35] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[35])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[35])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[35])-1];
				main.bank_2.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[35])).get(0));
				setStringValueForFaders(main.bank_2, 35);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[36] != 0){
				main.bank_3.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[36])-1]);
				main.bank_3.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[36])-1});
				main.bank_3.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[36])-1];
				main.bank_3.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[36])).get(0));
				setStringValueForFaders(main.bank_3, 36);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[36] != 0){
				main.bank_4.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[37])-1]);
				main.bank_4.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[37])-1});
				main.bank_4.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[37])-1];
				main.bank_4.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[37])).get(0));
				setStringValueForFaders(main.bank_4, 37);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[38] != 0){
				main.bank_5.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[38])-1]);
				main.bank_5.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[38])-1});
				main.bank_5.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[38])-1];
				main.bank_5.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[38])).get(0));
				setStringValueForFaders(main.bank_5, 38);
			}
			
		} else if(btn == main.btnOther){
			
			main.bank_1.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[39])-1]);
			main.bank_1.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[39])-1});
			main.bank_1.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[39])-1];
			main.bank_1.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[39])).get(0));
			setStringValueForFaders(main.bank_1, 39);
			
			if(selectedFixtures.get(0).getFixtureType().channel_function[40] != 0){
				main.bank_2.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[40])-1]);
				main.bank_2.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[40])-1});
				main.bank_2.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[40])-1];
				main.bank_2.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[40])).get(0));
				setStringValueForFaders(main.bank_2, 40);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[41] != 0){
				main.bank_3.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[41])-1]);
				main.bank_3.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[41])-1});
				main.bank_3.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[41])-1];
				main.bank_3.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[41])).get(0));
				setStringValueForFaders(main.bank_3, 41);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[42] != 0){
				main.bank_4.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[42])-1]);
				main.bank_4.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[42])-1});
				main.bank_4.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[42])-1];
				main.bank_4.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[42])).get(0));
				setStringValueForFaders(main.bank_4, 42);
			}
			if(selectedFixtures.get(0).getFixtureType().channel_function[43] != 0){
				main.bank_5.slider.setValue(main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[43])-1]);
				main.bank_5.assignChannel(new int[]{(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[43])-1});
				main.bank_5.prev_val = main.channel_data[(selectedFixtures.get(0).getStartChannel()+selectedFixtures.get(0).getFixtureType().channel_function[43])-1];
				main.bank_5.setName((String)((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[43])).get(0));
				setStringValueForFaders(main.bank_5, 43);
			}
			
		}
		
	}
	
	/*
	 * Sets the string value for the given fader to the given channel function index
	 */
	private static void setStringValueForFaders(Fader f, int channel_function_index){
		if( ((Vector)selectedFixtures.get(0).getFixtureType().function.get(selectedFixtures.get(0).getFixtureType().channel_function[channel_function_index]-1)).size() >= 3 ){
			selectedFixtures.get(0).getFixtureType().setStringValue(f);
		} else {
			f.setStrValue("-");
		}
	}
	
	static void setFaderBankPage(int page){
		
		Fader[] faders = {main.bank_1, main.bank_2, main.bank_3, main.bank_4, main.bank_5, main.pan, main.tilt};
		int a = (page-1) * 7;
		
		for(Fader fader : faders){
			if(a >= selectedFixtures.size()){
				fader.f = null;
				fader.unassign();
				fader.setName("-");
				fader.slider.setValue(0);
				fader.setStrValue("-");
				fader.unEnable();
			} else {
				fader.assignFixture(selectedFixtures.get(a));
				fader.slider.setValue(main.channel_data[selectedFixtures.get(a).getStartChannel()]);
				fader.assignChannel(new int[]{selectedFixtures.get(a).getStartChannel()});
				fader.prev_val = main.channel_data[selectedFixtures.get(a).getStartChannel()];
				fader.setName(selectedFixtures.get(a).name);
				a++;
			}
			
		}
		
	}
	
}
