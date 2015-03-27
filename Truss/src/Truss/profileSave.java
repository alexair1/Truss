package Truss;

import java.io.*;
import Truss.main.Range;

public class profileSave {
	
	public static void main(String[] args){
		
		Object[][] macAuraBasic = {
			 	{"Strobe", new Range(0, 19, "Shutter Closed"), new Range(20, 24, "Shutter Open"), new Range(25, 64, "Fast > Slow"), new Range(65, 69, "Shutter Open"), new Range(70, 84, "Opening Pulse"), new Range(85, 89, "Shutter Open"), new Range(90, 104, "Closing Pulse"), new Range(105, 109, "Shutter Open"), new Range(110, 124, "Random Strobe"), new Range(125, 129, "Shutter Open"), new Range(130, 144, "Rand Open Plse"), new Range(145, 149, "Shutter Open"), new Range(150, 164, "Rand Close Plse"), new Range(165, 169, "Shutter Open"), new Range(170, 184, "Burst Pulse"), new Range(185, 189, "Shutter Open"), new Range(190, 204, "Rand Burst Plse"), new Range(205, 209, "Shutter Open"), new Range(210, 224, "Sine Wave"), new Range(225, 229, "Shutter Open"), new Range(230, 244, "Burst"), new Range(245, 255, "Shutter Open")},
			 	{"Dimmer", new Range(0, 255, "Intensity")},
			 	{"Zoom", new Range(0, 255, "Wide > Narrow")},
			 	{"Pan", new Range(0, 255, "Pan")},
			 	{"Pan Fine", new Range(0, 255, "Pan Fine")},
			 	{"Tilt", new Range(0, 255, "Tilt")},
			 	{"Tilt Fine", new Range(0, 255, "Tilt Fine")},
			 	{"Settings", new Range(0, 9, "No Function"), new Range(10, 14, "Reset"), new Range(15, 39, "No Function"), new Range(40, 44, "PTSP - Norm"), new Range(45, 49, "PTSP - Fast"), new Range(50, 54, "PTSP - Slow"), new Range(55, 59, "No Function"), new Range(60, 64, "Fan - Full"), new Range(65, 69, "No Function"), new Range(70, 74, "Fan - Regulated"), new Range(75, 89, "No Function"), new Range(90, 94, "Col Calib - On"), new Range(95, 99, "No Function"), new Range(100, 104, "Col Calib - Off"), new Range(105, 109, "No Function"), new Range(110, 114, "Fast Dimming"), new Range(115, 119, "No Function"), new Range(120, 124, "Smooth Dimming"), new Range(125, 249, "No Function"), new Range(250, 255, "Illuminate Disp.")},
			 	{"Colour", new Range(0, 9, "RGBW Mixing"), new Range(10, 14, "Moroccan Pink"), new Range(15, 19, "Pink"), new Range(20, 24, "Rose Pink"), new Range(25, 29, "Follies Pink"), new Range(30, 34, "Fuchsia Pink"), new Range(35, 39, "Surprise Pink"), new Range(40, 44, "Congo Blue"), new Range(45, 49, "Tokyo Blue"), new Range(50, 54, "Deep Blue"), new Range(55, 59, "Just Blue"), new Range(60, 64, "Medium Blue"), new Range(65, 69, "Double CT Blue"), new Range(70, 74, "Slate Blue"), new Range(75, 79, "Full CT Blue"), new Range(80, 84, "Half CT Blue"), new Range(85, 89, "Steel Blue"), new Range(90, 94, "Lighter Blue"), new Range(95, 99, "Light Blue"), new Range(100, 104, "Med Blue Green"), new Range(105, 109, "Dark Green"), new Range(110, 114, "Primary Green"), new Range(115, 119, "Moss Green"), new Range(120, 124, "Fern Green"), new Range(125, 129, "JAS Green"), new Range(130, 134, "Lime Green"), new Range(135, 139, "Spring Yellow"), new Range(140, 144, "Deep Amber"), new Range(145, 149, "Chrome Orange"), new Range(150, 154, "Orange"), new Range(155, 159, "Gold Amber"), new Range(160, 164, "Millennium Gold"), new Range(165, 169, "Deep Gold Amber"), new Range(170, 174, "Flame Red"), new Range(175, 179, "Open"), new Range(180, 201, "Rot Clock F>S"), new Range(202, 207, "Stop Rot"), new Range(208, 229, "Rot Ctr-Clock S>F"), new Range(230, 234, "Open"), new Range(235, 239, "Rand Colour Fast"), new Range(240, 244, "Rand Colour Med"), new Range(245, 249, "Rand Colour Slow"), new Range(250, 255, "Open")},
			 	{"Red", new Range(0, 255, "Red")},
			 	{"Green", new Range(0, 255, "Green")},
			 	{"Blue", new Range(0, 255, "Blue")},
			 	{"White", new Range(0, 255, "White")},
			 	{"CTC", new Range(0, 19, "Disabled"), new Range(20, 255, "10,000K > 2,500K")},
		     };
		
		Object[][] mac700basic = {
				{"Basic Func.", new Range(0, 19, "Shutter Closed"), new Range(20, 49, "Shutter Open"), new Range(50, 72, "Strobe, Fast > Slow"), new Range(73, 79, "Shutter Open"), new Range(80, 99, "Opening Pulse, Fast > Slow"), new Range(100, 119, "Closing Pulse, Fast > Slow"), new Range(120, 127, "Shutter Open"), new Range(128, 147, "Random Strobe, Fast"), new Range(148, 167, "Random Strobe, Medium"), new Range(168, 187, "Random Strobe, Slow"), new Range(188, 190, "Shutter Open"), new Range(191, 193, "Random Opening Pulse, Fast"), new Range(194, 196, "Random Opening Pulse, Slow"), new Range(197, 199, "Random Closing Pulse, Fast"), new Range(200, 202, "Random Closing Pulse, Slow"), new Range(203, 207, "Shutter Open"), new Range(208, 217, "Reset Fixture"), new Range(218, 227, "Shutter Open"), new Range(228, 237, "Lamp On"), new Range(238, 242, "400W Mode"), new Range(243, 247, "700W Mode"), new Range(248, 255, "Lamp Off")},
				{"Dimmer", new Range(0, 255, "Dimmer")},
				{"Not done", new Range(0, 255, "--")/*Come back to*/},
				{"Not done", new Range(0, 255, "--")/*Come back to*/},
				{"Not done", new Range(0, 255, "--")/*Come back to*/},
				{"Colour Wheel", new Range(0, 0, "Open"), new Range(1, 16, "Open > Slot 1"), new Range(17, 17, "Slot 1"), new Range(18, 33, "Slot 1 > Slot 2"), new Range(34, 34, "Slot 2"), new Range(35, 50, "Slot 2 > Slot 3"), new Range(51, 51, "Slot 3"), new Range(52, 67, "Slot 3 > Slot 4"), new Range(68, 68, "Slot 4"), new Range(39, 84, "Slot 4 > Slot 5"), new Range(85, 85, "Slot 5"), new Range(86, 101, "Slot 5 > Slot 6"), new Range(102, 102, "Slot 6"), new Range(103, 118, "Slot 6 > Slot 7"), new Range(119, 119, "Slot 7"), new Range(120, 135, "Slot 7 > Slot 8"), new Range(136, 136, "Slot 8"), new Range(137, 152, "Slot 8 > Open"), new Range(153, 153, "Open"), new Range(154, 158, "Slot 8"), new Range(159, 163, "Slot 7"), new Range(164, 168, "Slot 6"), new Range(169, 173, "Slot 5"), new Range(174, 178, "Slot 4"), new Range(179, 183, "Slot 3"), new Range(184, 188, "Slot 2"), new Range(189, 203, "Slot 1"), new Range(194, 198, "Open"), new Range(199, 219, "Rotation, Fast > Slow"), new Range(220, 240, "Rotation, Slow > Fast"), new Range(241, 245, "Random Colour, Fast"), new Range(246, 250, "Random Colour, Medium"), new Range(251, 255, "Random Colour, Slow")},
				{"Gobo (Rot)", new Range(0, 11, "Open"), new Range(12, 15, "Gobo 1 (Still)"), new Range(16, 19, "Gobo 2 (Still)"), new Range(20, 23, "Gobo 3 (Still)"), new Range(24, 27, "Gobo 4 (Still)"), new Range(28, 31, "Gobo 5 (Still)"), new Range(32, 35, "Gobo 6 (Still)"), new Range(36, 39, "Gobo 1 (Rotating)"), new Range(40, 43, "Gobo 2 (Rotating)"), new Range(44, 47, "Gobo 3 (Rotating)"), new Range(48, 51, "Gobo 4 (Rotating)"), new Range(52, 55, "Gobo 5 (Rotating)"), new Range(56, 59, "Gobo 6 (Rotating)"), new Range(60, 71, "Gobo 1 (Shake) Slow > Fast"), new Range(72, 83, "Gobo 2 (Shake) Slow > Fast"), new Range(84, 95, "Gobo 3 (Shake) Slow > Fast"), new Range(96, 107, "Gobo 4 (Shake) Slow > Fast"), new Range(108, 119, "Gobo 5 (Shake) Slow > Fast"), new Range(120, 131, "Gobo 6 (Shake) Slow > Fast"), new Range(132, 143, "Gobo 1 (Rotate + Shake) Slow > Fast"), new Range(144, 155, "Gobo 2 (Rotate + Shake) Slow > Fast"), new Range(156, 167, "Gobo 3 (Rotate + Shake) Slow > Fast"), new Range(168, 179, "Gobo 4 (Rotate + Shake) Slow > Fast"), new Range(180, 191, "Gobo 5 (Rotate + Shake) Slow > Fast"), new Range(192, 203, "Gobo 6 (Rotate + Shake) Slow > Fast"), new Range(204, 229, "Gobo Scroll with Rotation, Slow > Fast"), new Range(230, 255, "Gobo Scroll with Rotation, Fast > Slow")},
				{"Not done", new Range(0, 255, "--")/*Come back to*/},
				{"Not done", new Range(0, 255, "--")/*Come back to*/},
				{"Gobo (Static)", new Range(0, 0, "Open"), new Range(1, 10, "Open > Slot 1"), new Range(11, 11, "Slot 1"), new Range(12, 21, "Slot 1 > Slot 2"), new Range(22, 22, "Slot 2"), new Range(23, 32, "Slot 2 > Slot 3"), new Range(33, 33, "Slot 3"), new Range(34, 43, "Slot 3 > Slot 4"), new Range(44, 44, "Slot 4"), new Range(45, 54, "Slot 4 > Slot 5"), new Range(55, 55, "Slot 5"), new Range(56, 65, "Slot 5 > Slot 6"), new Range(66, 66, "Slot 6"), new Range(67, 76, "Slot 6 > Slot 7"), new Range(77, 77, "Slot 7"), new Range(78, 87, "Slot 7 > Slot 8"), new Range(88, 88, "Slot 8"), new Range(89, 98, "Slot 8 > Slot 9"), new Range(99, 99, "Slot 9"), new Range(100, 109, "Slot 9 > Open"), new Range(110, 112, "Open"), new Range(113, 121, "Slot 9 (Stepped Scroll)"), new Range(169, 173, "Slot 8 (Stepped Scroll)"), new Range(174, 178, "Slot 7 (Stepped Scroll)"), new Range(179, 183, "Slot 6 (Stepped Scroll)"), new Range(184, 188, "Slot 5 (Stepped Scroll)"), new Range(189, 203, "Slot 4 (Stepped Scroll)"), new Range(194, 198, "Slot 3 (Stepped Scroll)"), new Range(199, 219, "Slot 2 (Stepped Scroll)"), new Range(220, 240, "Slot 1 (Stepped Scroll)"), new Range(241, 245, "Random Colour, Fast"), new Range(246, 250, "Random Colour, Medium"), new Range(251, 255, "Random Colour, Slow")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},
				{"Not done", new Range(0, 255, "--")},  
			 };
		
		Object[][] conv = {
				{"Dimmer", new Range(0, 255, "Intensity")},
			};
		
		Object[][] genericLED = {
				{"Red", new Range(0, 255, "Red")},
				{"Green", new Range(0, 255, "Green")},
				{"Blue", new Range(0, 255, "Blue")},
		};

		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("profiles.txt"));
			
			o.writeObject("*false_RGB LED");
			for(int c=0;c<genericLED.length;c++){
				for(int d=0;d<genericLED[c].length;d++){
					o.writeObject(genericLED[c][d]);
				}
			}  
			o.writeObject("*true_Martin Mac Aura Basic");
			for(int a=0;a<macAuraBasic.length;a++){
				for(int b=0;b<macAuraBasic[a].length;b++){
					o.writeObject(macAuraBasic[a][b]);
				}
			}  
			o.writeObject("*true_Dimmer");
			for(int c=0;c<conv.length;c++){
				for(int d=0;d<conv[c].length;d++){
					o.writeObject(conv[c][d]);
				}
			}  
			o.writeObject("*");
			
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
