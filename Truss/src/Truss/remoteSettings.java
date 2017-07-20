//package Truss;
//
//import java.awt.Color;
//import java.awt.event.*;
//
//import javax.crypto.Cipher;
//import javax.crypto.CipherOutputStream;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import javax.swing.*;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//import java.sql.*;
//
//public class remoteSettings implements ActionListener{
//	
//	JFrame frame = new JFrame();
//	JTextField name_tf;
//	JButton open;
//	JLabel info_disp, info_disp2;
//	Connection link;
//	ResultSet rs;
//	
//	/**
//	 * @wbp.parser.entryPoint
//	 */
//	public void actionPerformed(ActionEvent e) {
//		JPanel panel = new JPanel();
//		panel.setLayout(null);
//		frame.setContentPane(panel);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setBounds(100, 100, 300, 115);
//		frame.setTitle("Remote Settings");
//		frame.setVisible(true);
//		
//		open = new JButton("Open");
//		open.setBounds(194, 35, 100, 29);
//	//	open.setEnabled(false);
//		panel.add(open);
//		
//		JLabel lblUsername = new JLabel("Name\n");
//		lblUsername.setBounds(5, 12, 40, 16);
//		panel.add(lblUsername);
//		
//		name_tf = new JTextField();
//		name_tf.setBounds(57, 6, 237, 28);
//		panel.add(name_tf);
//		name_tf.setColumns(10);
//		
//		info_disp = new JLabel();
//		info_disp.setBounds(5, 65, 289, 16);
//		panel.add(info_disp);
//		
//		info_disp2 = new JLabel();
//		info_disp2.setBounds(5, 112, 289, 16);
//		panel.add(info_disp2);
//		
//		event a = new event();
//		open.addActionListener(a);
//	}
//	
//	public class event implements ActionListener{
//		public void actionPerformed(ActionEvent e){
//			if(e.getSource() == open){
//				
//				try {
//					info_disp.setText("Your Key: "+retrieveKey(name_tf.getText()));
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				
//			}
//		}
//	}
//	
//	public int retrieveKey(String name) throws Exception{
//		Statement statement = null;
//		
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		link = DriverManager.getConnection("jdbc:mysql://localhost?user=root");
//		
//			statement = link.createStatement();
//			statement.executeUpdate("INSERT INTO Truss.Online (name, data) values('"+name+"','"+main.preset+"')");
//
//		rs = statement.executeQuery("SELECT * FROM Truss.Online ORDER BY id DESC LIMIT 1");
//
//		if(rs.next()){
//			return rs.getInt(1);
//		} else {
//			return 0;
//		}
//	}
//	
//	public void mysqlLogin(String name, byte[] pass) {
//		
//		info_disp.setForeground(Color.BLUE);
//		info_disp.setText("Logged in.");
//		open.setEnabled(true);
//		
//		Statement statement = null;
//		byte[] key = "4D92199549E0F2E4".getBytes();
//		try {
//			
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			link = DriverManager.getConnection("jdbc:mysql://localhost?user=root");
//			
//			try {
//				statement = link.createStatement();
//			} catch (SQLException e1) {}
//			
//			rs = statement.executeQuery("SELECT * FROM Truss.Users WHERE user='"+name+"'");
//	
//	/*		while(rs.next()){
//	//			SecretKey iv = key.generateKey();
//				Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
//				c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(rs.getBytes(4)));
//		//		byte[] password = c.doFinal();
//				
//				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
//			    CipherOutputStream cipherOutputStream = new CipherOutputStream(byteOutputStream, c);
//			    cipherOutputStream.write(rs.getBytes(4));
//			    cipherOutputStream.write(pass);
//			    cipherOutputStream.flush();
//			    cipherOutputStream.close();
//			    byte[] password = byteOutputStream.toByteArray();
//
//			    for(int a=0;a<password.length;a++){
//			    	System.out.print(password[a]);
//			    }
//			    System.out.println("::");
//			    for(int a=0;a<rs.getBytes(3).length;a++){
//			    	System.out.print(rs.getBytes(3)[a]);
//			    }
//				
//		//		System.out.println(rs.getBytes(3));
//				//  && name.equals(rs.getString(2))
//				if(password.equals(rs.getBytes(3))){
//					System.out.println("WORKED!!");
//					
//					info_disp.setForeground(Color.BLUE);
//					info_disp.setText("Logged in.");
//					open.setEnabled(true);
//					 //
//					 // Set user vars
//					 //
//	//				sync(link, rs.getInt(1));
//				} else {
//					System.out.println("WRONG PASSWORD");
//					info_disp.setForeground(Color.RED);
//					info_disp.setText("User or pass was incorrect.");
//				}
//			}  */
//		} catch(Exception e){
//			e.printStackTrace();
//		}   
////		try {
////			sync(link, rs.getInt(1));
////		} catch (SQLException e) {
////			e.printStackTrace();
////		}
//	}
////	public void sync(Connection link, int id){
////		try {
////				info_disp2.setText("Setting up connection...");
////			
////			link.createStatement().execute("UPDATE Truss.Users SET ip='"+InetAddress.getLocalHost().getHostAddress()+"' WHERE id="+id+"");			
////			ServerSocket serverskt = new ServerSocket(10000);
////			
////				info_disp2.setText("Waiting for client...");
////			
////			sync2(serverskt);
////			
////		} catch (Exception e) {
////			info_disp2.setForeground(Color.RED);
////			info_disp2.setText("Failed to open connection.");
////		}	
////	}
////	public void sync2(ServerSocket serverskt){
////		try {
////	//		serverskt.setSoTimeout(10000);
////			
////			Socket socket = serverskt.accept();
////			BufferedReader inputstream = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
////			DataOutputStream outputstream = new DataOutputStream(socket.getOutputStream());
////
////			String str = "";
////			for(int a=0;a<10;a++){
////				str = str + "bump" + a + ":";
////			}
////			outputstream.writeBytes(str + "\n");
////			
////		} catch(SocketTimeoutException ste){
////			sync2(serverskt);
////		} catch (Exception e) {
////			info_disp2.setForeground(Color.RED);
////			info_disp2.setText("Failed to open connection.");
////			e.printStackTrace();
////			return;
////		}	
////		info_disp2.setText("Connected to brower.");
////	}
//}
