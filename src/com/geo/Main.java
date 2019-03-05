package com.geo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONException;
import org.json.JSONObject;

class Task extends TimerTask {
    public void run() {
    	Main.updateManager();
    }
}

public class Main {
	static JFrame frame = new JFrame();
	static MotionPanel panel = new MotionPanel(frame);
	static JLabel label = new JLabel("", JLabel.CENTER);
	static File stats;
	static File minecraft = null;
	
	static long timestamp = 0;
	static int time;
	static int os = os();
	public static void main(String[] args) {
		Timer timer = new Timer();
		int interval = (int) (0.05 * 1000);
		timer.scheduleAtFixedRate(new Task(), 0, interval);
		frameInit();
	}
	
	public static void frameInit() {
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		
		frame.setTitle("Minecraft Timer");
		frame.setBounds(0, 0, 260, 50);
//		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));//image not referenced properly in jar
		
		label.setText("Broken");
		label.setFont(new Font("Arial", Font.PLAIN, 52));
		label.setForeground(Color.WHITE);
		label.setBorder(BorderFactory.createEmptyBorder( -9, 0, 0, 0 ));
		
		panel.add(label, null);
		panel.setBackground(Color.BLACK);

		frame.setContentPane(panel);
		frame.setVisible(true);
	}
	
	//calls loading and updating of the timer
	public static void updateManager() {
		load();
		update();
	}
	
	public static void load() {
		if(os == 0) {
			minecraft = new File(System.getProperty("user.home")+"/AppData/Roaming/.minecraft");
		}
		if(os == 1) {
			minecraft = new File("/Users/"+System.getProperty("user.name")+"/Library/Application Support/minecraft");
		}
		if(os == 2) {
			minecraft = new File("/home/"+System.getProperty("user.name")+"/.minecraft");
		}
//		if(os == 0) {
//			world = newestFile(new File(System.getProperty("user.home")+"/AppData/Roaming/.minecraft/saves").listFiles(), 0);
//		}
//		if(os == 1) {
//			world = newestFile(new File("/Users/"+System.getProperty("user.name")+"/Library/Application Support/minecraft/saves").listFiles(), 0);
//		}
//		if(os == 2) {
//			world = newestFile(new File("/home/"+System.getProperty("user.name")+"/.minecraft/saves").listFiles(), 0);
//		}
		stats = newestFile(new File(minecraft.getPath()+"/saves/stats").listFiles(), 1);
	}
	
	public static int read(String key, File stats) {
		try {
			if(stats!=null) {
				//loads stats for player
				List<String> lines = Files.readAllLines(stats.toPath());
				if(lines.size()>0) {
					JSONObject obj = new JSONObject(lines.get(0));
					//checks whether the particular stat exists
					if(!obj.isNull(key)) {
						//returns stat value
						return (obj.getInt(key));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static File newestFile(File[] files, int dir) {
		if(files != null) {
			if(files.length!=0) {
			File newest = files[0];
			
			    for (File file : files) {
			    	switch(dir) {
			    		//returns newest directory
			    		case 0:
			    			if(file.lastModified()>newest.lastModified() && file.isDirectory()) {
				        		newest = file;
				        	}
			    		//returns newest non-directory file
			    		case 1:
			    			if(file.lastModified()>newest.lastModified() && !file.isDirectory()) {
				        		newest = file;
				        	}
			    		//returns newest file
			    		case 2:
			    			if(file.lastModified()>newest.lastModified()) {
				        		newest = file;
				        	}
			    	}
			    }
			    return newest;
			}
		}
		return null;
	}
	
	public static boolean modified(File f) {	
		if(timestamp!=f.lastModified()) {
			timestamp = f.lastModified();
			return true;
		}
		return false;
	}
	
    private static String formatTime(long timein){
    	if(timein<10) return "0"+timein;
    	return ""+timein;
    }
	
	public static String time(int time) {
        String s,sd,sd1,sd2;
        Long d1,d2;
        long d =(long) (5*time);
        d1=d/100;
        d2=d1/60;
        sd = formatTime(d%100);
        sd1 = formatTime(d1%60);
        sd2 = formatTime(d2);
        s = sd2+":"+sd1+"."+sd;
		return s;
	}
	
	public static void update() {
		time = read("stat.playOneMinute",stats);
		label.setText(time(time));
	}
	
	public static int os() {
		String systemID = System.getProperty("os.name").toLowerCase();
		if(systemID.indexOf("win")>=0) return 0;
		if(systemID.indexOf("mac")>=0) return 1;
		if(systemID.indexOf("nix")>=0||systemID.indexOf("nux")>=0||systemID.indexOf("aix")>=0) return 2;
		return -1;
	}
}
